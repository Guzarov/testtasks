package com.testtask.idgenerator.generator.memcached;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.InetSocketAddress;

@Configuration
@Profile({"diapasons", "default"})
public class MemcachedDiapasonConfig {


    @Value("${memcached.host}")
    protected String host;

    @Value("${memcached.port}")
    protected int port;

    @Value("${generator.diapasons.size}")
    protected long diapasonSize;

    @Bean
    public DiapasonSizeStrategy strategy() {
        return new DiapasonSizeStrategy(diapasonSize);
    }

    @Bean
    public MemcachedClient memcachedClient() throws IOException {
        return new MemcachedClient(new InetSocketAddress(host, port));
    }

}
