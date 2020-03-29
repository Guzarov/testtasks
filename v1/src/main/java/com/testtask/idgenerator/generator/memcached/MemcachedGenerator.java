package com.testtask.idgenerator.generator.memcached;

import com.testtask.idgenerator.generator.Generator;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@Component
@Profile({"diapasons", "default"})
public class MemcachedGenerator implements Generator {

    private static final String KEY = "identifiers_diapason";
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);

    private final MemcachedClient client;
    private final DiapasonSizeStrategy sizeStrategy;

    private MemcachedDiapason diapason;
    private long delta;

    public MemcachedGenerator(MemcachedClient client, DiapasonSizeStrategy sizeStrategy)
            throws ExecutionException, InterruptedException {
        this.client = client;
        this.sizeStrategy = sizeStrategy;
        logger.debug("Using memcached diapason based generator with strategy " + sizeStrategy);
        reserveNextDiapason(sizeStrategy.getSize());
    }


    private void reserveNextDiapason(long diapasonSize) throws ExecutionException, InterruptedException {
        var reservationCompleted = false;
        do {
            var exists = getOrCreate();
            logger.debug("About to reserve next diapason (current is " + diapason +
                    ", exists is " + exists.getValue() + ")");
            diapason = new MemcachedDiapason(((MemcachedDiapason) exists.getValue()).getTo(), diapasonSize);
            CASResponse casResponse = client.cas(KEY, exists.getCas(), diapason);
            switch (casResponse) {
                case OK:
                    reservationCompleted = true;
                    break;
                case EXISTS:
                    logger.debug("Someone already reserved this, try next");
                    reservationCompleted = false;
                    break;
                default:
                    throw new IllegalStateException("Cannot CAS: " + casResponse);
            }
        } while (!reservationCompleted);
        delta = 0;
    }

    private CASValue<Object> getOrCreate() throws ExecutionException, InterruptedException {
        var exists = client.gets(KEY);
        if (exists == null) {
            if (!client.set(KEY, 0, new MemcachedDiapason(BigInteger.ZERO, 0L)).get()) {
                throw new RuntimeException("Cannot create value for key " + KEY);
            }
        }
        return client.gets(KEY);
    }

    @Override
    public BigInteger generate() {
        synchronized (this) {
            try {
                delta++;
                var current = diapason.getFrom().add(BigInteger.valueOf(delta));
                var comparisonResult = current.compareTo(diapason.getTo());
                if (comparisonResult == 0) {
                    reserveNextDiapason(sizeStrategy.getSize());
                } else if (comparisonResult > 0) {
                    throw new RuntimeException("Slipped diapason detected: " +
                            current + ", delta = " + delta + ". Please restart service.");
                }
                return current;
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new RuntimeException("Cannot generate. Please restart service", e);
            }
        }
    }


}
