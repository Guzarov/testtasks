package com.testtask.idgenerator.generator.uuid;

import com.testtask.idgenerator.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.UUID;

@Component
@Profile("uuid")
public class UUIDGenerator implements Generator {

    private static final Logger logger = LoggerFactory.getLogger(Generator.class);

    public UUIDGenerator() {
        logger.debug("Using UUID based generator");
    }

    public BigInteger generate() {
        var uuid = UUID.randomUUID();
        return new BigInteger(
                uuid.toString().replaceAll("-", ""),
                16
        );
    }
}


