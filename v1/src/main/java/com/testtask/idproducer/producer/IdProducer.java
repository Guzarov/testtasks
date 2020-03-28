package com.testtask.idproducer.producer;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.UUID;

@Component
public class IdProducer {

    public BigInteger generateId() {
        var uuid = UUID.randomUUID();
        return new BigInteger(
                Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits()),
                16
        );
    }
}
