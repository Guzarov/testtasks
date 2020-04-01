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
import java.util.concurrent.atomic.AtomicLong;

@Component
@Profile({"diapasons", "default"})
public class MemcachedGenerator implements Generator {

    private static final String KEY = "identifiers_diapason";
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);

    private final MemcachedClient client;
    private final DiapasonSizeStrategy sizeStrategy;

    private volatile MemcachedDiapason diapason;
    private AtomicLong delta = new AtomicLong(0);

    public MemcachedGenerator(MemcachedClient client, DiapasonSizeStrategy sizeStrategy)
            throws ExecutionException, InterruptedException {
        this.client = client;
        this.sizeStrategy = sizeStrategy;
        logger.debug("Using memcached diapason based generator with strategy " + sizeStrategy);
        reserveNextDiapason(sizeStrategy.getSize());
    }


    private synchronized void reserveNextDiapason(long diapasonSize) throws ExecutionException, InterruptedException {
        do {
            var exists = getOrCreate();
            logger.debug("About to reserve next diapason (current is " + diapason +
                    ", exists is " + exists.getValue() + ")");
            diapason = new MemcachedDiapason(((MemcachedDiapason) exists.getValue()).getTo(), diapasonSize);
            CASResponse casResponse = client.cas(KEY, exists.getCas(), diapason);
            switch (casResponse) {
                case OK:
                    delta.set(0);
                    return;
                case EXISTS:
                    logger.debug("Someone already reserved this, try next");
                    break;
                default:
                    throw new IllegalStateException("Cannot CAS: " + casResponse);
            }
        } while (true);
    }

    private CASValue<Object> getOrCreate() throws ExecutionException, InterruptedException {
        var exists = client.gets(KEY);
        if (exists == null) {
            // server stores the data, only if it does not already exist
            client.add(KEY, 0, new MemcachedDiapason(BigInteger.ZERO, 0L)).get();
            exists = client.gets(KEY);
        }
        return exists;
    }

    @Override
    public BigInteger generate() {
        try {
            var current = diapason.getFrom().add(BigInteger.valueOf(delta.incrementAndGet()));
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
