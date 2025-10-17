package com.or.portfolio_tracker.portfolio;

import java.math.BigDecimal;

/**
 * Aggregated position for a single symbol.
 * Values are already rounded upstream in the service where noted.
 */
public class AssetPositionSummaryDTO {

    private final String symbol;

    // Quantity across all lots for this symbol (kept with up to 8 fraction digits).
    private final BigDecimal totalQuantity;

    // Sum(quantity * purchasePrice) for this symbol (rounded to 2 fraction digits).
    private final BigDecimal totalCost;

    // Average purchase cost = totalCost / totalQuantity (rounded to 2 fraction digits).
    private final BigDecimal averageCost;

    // Optional live price data (null when price is unavailable).
    private final BigDecimal currentPrice;   // 2 fraction digits
    private final BigDecimal currentValue;   // quantity * currentPrice (2 fraction digits)
    private final BigDecimal unrealizedPnl;  // currentValue - totalCost (2 fraction digits)
    private final BigDecimal unrealizedPct;  // unrealizedPnl / totalCost * 100 (2 fraction digits)

    public AssetPositionSummaryDTO(
            String symbol,
            BigDecimal totalQuantity,
            BigDecimal totalCost,
            BigDecimal averageCost,
            BigDecimal currentPrice,
            BigDecimal currentValue,
            BigDecimal unrealizedPnl,
            BigDecimal unrealizedPct
    ) {
        this.symbol = symbol;
        this.totalQuantity = totalQuantity;
        this.totalCost = totalCost;
        this.averageCost = averageCost;
        this.currentPrice = currentPrice;
        this.currentValue = currentValue;
        this.unrealizedPnl = unrealizedPnl;
        this.unrealizedPct = unrealizedPct;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getTotalQuantity() { return totalQuantity; }
    public BigDecimal getTotalCost() { return totalCost; }
    public BigDecimal getAverageCost() { return averageCost; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public BigDecimal getUnrealizedPnl() { return unrealizedPnl; }
    public BigDecimal getUnrealizedPct() { return unrealizedPct; }
}