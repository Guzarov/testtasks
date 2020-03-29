package com.testtask.idgenerator;

import com.testtask.idgenerator.generator.Generator;
import com.testtask.idgenerator.generator.memcached.DiapasonSizeStrategy;
import com.testtask.idgenerator.generator.memcached.MemcachedGenerator;
import net.spy.memcached.MemcachedClient;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemcachedGeneratorTest {

    @ClassRule
    public static GenericContainer MEMCACHED = new GenericContainer<>("memcached:alpine")
            .withExposedPorts(11211);

    @Test
    public void testSmth() throws IOException, ExecutionException, InterruptedException {
        var sizeStrategy = new DiapasonSizeStrategy(10);
        var client = new MemcachedClient(new InetSocketAddress(MEMCACHED.getContainerIpAddress(),
                MEMCACHED.getFirstMappedPort()));

        var node1 = new MemcachedGenerator(client, sizeStrategy);
        ensureDiapasonGenerated(1, 12, node1);

        var node2 = new MemcachedGenerator(client, sizeStrategy);
        ensureDiapasonGenerated(21, 42, node2);

        ensureDiapasonGenerated(13, 20, node1);
        ensureDiapasonGenerated(51, 60, node1);

        ensureDiapasonGenerated(43, 50, node2);
        ensureDiapasonGenerated(71, 80, node2);

        ensureDiapasonGenerated(61, 69, node1);
    }

    private void ensureDiapasonGenerated(int from, int to, Generator generator) {
        for (int i = from; i <= to; i++) {
            assertEquals(i, generator.generate().longValue());
        }
    }

}
