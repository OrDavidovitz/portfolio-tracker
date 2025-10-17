package com.or.portfolio_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Enable Spring's annotation-driven cache support (@Cacheable, @CacheEvict, etc.)
public class PortfolioTrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(PortfolioTrackerApplication.class, args);
	}
}