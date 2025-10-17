
package com.or.portfolio_tracker.price;

import com.or.portfolio_tracker.asset.AssetType;
import java.math.BigDecimal;
import java.util.Optional;

/** Abstraction over any price source (stocks/ETFs/crypto). */
public interface PriceProvider {
    Optional<BigDecimal> getCurrentPrice(String symbol, AssetType type);
}