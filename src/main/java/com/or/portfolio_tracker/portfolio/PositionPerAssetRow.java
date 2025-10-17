package com.or.portfolio_tracker.portfolio;

import com.or.portfolio_tracker.asset.AssetType;
import java.math.BigDecimal;

/**
 * Projection class used for aggregation queries at the repository layer.
 *
 * Represents a "row" of grouped results per asset symbol, typically returned
 * by {@link com.or.portfolio_tracker.holding.HoldingRepository#sumPerAssetRaw()}.
 *
 * Fields:
 * - symbol        → Asset ticker symbol (e.g., "AAPL", "BTC")
 * - totalQuantity → Sum of all quantities held for this symbol
 * - totalCost     → Sum of invested cost for this symbol
 *
 * This is *not* exposed directly to the API layer — instead it is mapped
 * into {@link AssetPositionSummaryDTO}, where additional calculations
 * (like average cost) are performed and formatting/rounding rules are applied.
 */

/** Projection row used by repository to aggregate per symbol (and asset type). */
/** Projection for grouped per-asset aggregation. */
public class PositionPerAssetRow {
    private final String symbol;
    private final AssetType assetType;
    private final BigDecimal totalQuantity;
    private final BigDecimal totalCost;

    public PositionPerAssetRow(String symbol, AssetType assetType,
                               BigDecimal totalQuantity, BigDecimal totalCost) {
        this.symbol = symbol;
        this.assetType = assetType;
        this.totalQuantity = totalQuantity;
        this.totalCost = totalCost;
    }

    public String getSymbol() { return symbol; }
    public AssetType getAssetType() { return assetType; }
    public BigDecimal getTotalQuantity() { return totalQuantity; }
    public BigDecimal getTotalCost() { return totalCost; }
}