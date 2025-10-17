package com.or.portfolio_tracker.portfolio;

import java.math.BigDecimal;

/**
 * High-level portfolio summary returned by /portfolio/summary.
 * All rounding is performed in the service layer.
 */
public class PortfolioSummaryDTO {
    private final long positions;               // distinct symbols count
    private final BigDecimal totalQuantity;     // Σ quantity (2 fraction digits)
    private final BigDecimal totalCost;         // Σ cost (2 fraction digits)
    private final BigDecimal totalCurrentValue; // Σ current value (2 fraction digits)
    private final BigDecimal totalUnrealizedPnl;// Σ (current - cost) (2 fraction digits)
    private final BigDecimal totalUnrealizedPct;// Σ pnl / cost * 100 (2 fraction digits)

    public PortfolioSummaryDTO(long positions,
                               BigDecimal totalQuantity,
                               BigDecimal totalCost,
                               BigDecimal totalCurrentValue,
                               BigDecimal totalUnrealizedPnl,
                               BigDecimal totalUnrealizedPct) {
        this.positions = positions;
        this.totalQuantity = totalQuantity;
        this.totalCost = totalCost;
        this.totalCurrentValue = totalCurrentValue;
        this.totalUnrealizedPnl = totalUnrealizedPnl;
        this.totalUnrealizedPct = totalUnrealizedPct;
    }

    public long getPositions() { return positions; }
    public BigDecimal getTotalQuantity() { return totalQuantity; }
    public BigDecimal getTotalCost() { return totalCost; }
    public BigDecimal getTotalCurrentValue() { return totalCurrentValue; }
    public BigDecimal getTotalUnrealizedPnl() { return totalUnrealizedPnl; }
    public BigDecimal getTotalUnrealizedPct() { return totalUnrealizedPct; }
}