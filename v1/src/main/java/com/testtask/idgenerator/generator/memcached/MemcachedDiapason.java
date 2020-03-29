package com.testtask.idgenerator.generator.memcached;

import java.io.Serializable;
import java.math.BigInteger;

public class MemcachedDiapason implements Serializable {

    private final BigInteger from;
    private final BigInteger to;

    public MemcachedDiapason(BigInteger from, long delta) {
        this.from = from;
        to = from.add(BigInteger.valueOf(delta));
    }

    public BigInteger getTo() {
        return to;
    }

    public BigInteger getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "MemcachedDiapason{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
