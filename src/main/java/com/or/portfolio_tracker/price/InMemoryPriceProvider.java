// src/main/java/com/or/portfolio_tracker/price/InMemoryPriceProvider.java
package com.or.portfolio_tracker.price;

import com.or.portfolio_tracker.asset.AssetType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory map for DEV profile. */
@Profile("dev")
@Component
public class InMemoryPriceProvider implements PriceProvider {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public void put(String symbol, BigDecimal price) {
        prices.put(symbol.toUpperCase(), price);
    }

    @Override
    public Optional<BigDecimal> getCurrentPrice(String symbol, AssetType type) {
        return Optional.ofNullable(prices.get(symbol.toUpperCase()));
    }
}