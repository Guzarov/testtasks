package com.testtask.idgenerator.generator;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.UUID;

@Component
public class Generator {

    public BigInteger generate() {
        var uuid = UUID.randomUUID();
        return new BigInteger(
                Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits()),
                16
        );
    }
}
