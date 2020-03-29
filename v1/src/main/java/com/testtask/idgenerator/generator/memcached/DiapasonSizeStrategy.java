package com.testtask.idgenerator.generator.memcached;

public class DiapasonSizeStrategy {

    private final long size;

    public DiapasonSizeStrategy(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "DiapasonSizeStrategy{" +
                "size=" + size +
                '}';
    }
}
