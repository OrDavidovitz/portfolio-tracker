package com.or.portfolio_tracker.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Central cache configuration.
 * Defines a Caffeine-based CacheManager with a 5-minute TTL for the "prices" cache.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Create a cache named "prices"
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("prices");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES) // keep entries for 5 minutes
                        .maximumSize(100)                      // avoid unbounded growth
        );
        return cacheManager;
    }
}