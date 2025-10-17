package com.or.portfolio_tracker.portfolio;

import com.or.portfolio_tracker.asset.AssetType;
import com.or.portfolio_tracker.holding.HoldingRepository;
import com.or.portfolio_tracker.price.PriceProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Portfolio-level calculations.
 * Pulls raw aggregates from HoldingRepository and enriches them with pricing via PriceProvider.
 */
@Service
public class PortfolioService {

    private static final int QTY_SCALE = 8;   // internal precision for quantities
    private static final int MONEY_SCALE = 2; // cents
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    private final HoldingRepository holdingRepo;
    private final PriceProvider priceProvider;

    public PortfolioService(HoldingRepository holdingRepo, PriceProvider priceProvider) {
        this.holdingRepo = holdingRepo;
        this.priceProvider = priceProvider;
    }

    /**
     * Grouped breakdown per symbol:
     * - totalQuantity (8dp), totalCost (2dp), averageCost (2dp)
     * - live fields when available: currentPrice/currentValue/unrealizedPnl/unrealizedPct (all 2dp)
     * - special handling for CASH (no market price; value = quantity, PnL fields are null)
     */
    public List<AssetPositionSummaryDTO> perAsset() {
        return holdingRepo.sumPerAssetRaw().stream()
                .map(row -> {
                    // Normalize and round inputs defensively
                    BigDecimal qty = (row.getTotalQuantity() == null)
                            ? BigDecimal.ZERO
                            : row.getTotalQuantity().setScale(QTY_SCALE, RM);

                    BigDecimal cost = (row.getTotalCost() == null)
                            ? BigDecimal.ZERO
                            : row.getTotalCost().setScale(MONEY_SCALE, RM);

                    BigDecimal avg = (qty.signum() == 0)
                            ? BigDecimal.ZERO
                            : cost.divide(qty, MONEY_SCALE, RM);

                    BigDecimal currentPrice;
                    BigDecimal currentValue = null;
                    BigDecimal pnl = null;
                    BigDecimal pnlPct = null;

                    // --- Special case: CASH ---
                    if (row.getAssetType() == AssetType.CASH) {
                        // For CASH, we present price as 1.00 and value = qty; no PnL semantics.
                        currentPrice = BigDecimal.ONE.setScale(MONEY_SCALE, RM);
                        currentValue = qty.setScale(MONEY_SCALE, RM);
                        // pnl and pnlPct remain null
                    } else {
                        // Normal flow: resolve current price via provider using symbol + assetType
                        currentPrice = priceProvider
                                .getCurrentPrice(row.getSymbol(), row.getAssetType())
                                .map(p -> p.setScale(MONEY_SCALE, RM))
                                .orElse(null);

                        if (currentPrice != null) {
                            currentValue = currentPrice.multiply(qty).setScale(MONEY_SCALE, RM);
                            pnl = currentValue.subtract(cost).setScale(MONEY_SCALE, RM);
                            if (cost.signum() != 0) {
                                // compute with extra precision, then round to 2dp
                                pnlPct = pnl.divide(cost, 4, RM)
                                        .multiply(new BigDecimal("100"))
                                        .setScale(MONEY_SCALE, RM);
                            } else {
                                pnlPct = BigDecimal.ZERO.setScale(MONEY_SCALE, RM);
                            }
                        }
                    }

                    return new AssetPositionSummaryDTO(
                            row.getSymbol(), qty, cost, avg,
                            currentPrice, currentValue, pnl, pnlPct
                    );
                })
                .toList();
    }

    /**
     * Whole-portfolio rollup, built on top of perAsset():
     * positions = distinct symbols count.
     */
    public PortfolioSummaryDTO summary() {
        List<AssetPositionSummaryDTO> perAsset = perAsset();

        long positions = perAsset.size();

        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (AssetPositionSummaryDTO row : perAsset) {
            totalQty = totalQty.add(nullToZero(row.getTotalQuantity()));
            totalCost = totalCost.add(nullToZero(row.getTotalCost()));
            totalCurrentValue = totalCurrentValue.add(nullToZero(row.getCurrentValue()));
        }

        totalQty          = totalQty.setScale(MONEY_SCALE, RM);   // display 2dp for summary
        totalCost         = totalCost.setScale(MONEY_SCALE, RM);
        totalCurrentValue = totalCurrentValue.setScale(MONEY_SCALE, RM);

        BigDecimal totalPnl = totalCurrentValue.subtract(totalCost).setScale(MONEY_SCALE, RM);
        BigDecimal totalPct = (totalCost.signum() == 0)
                ? BigDecimal.ZERO.setScale(MONEY_SCALE, RM)
                : totalPnl.divide(totalCost, 4, RM).multiply(new BigDecimal("100")).setScale(MONEY_SCALE, RM);

        return new PortfolioSummaryDTO(
                positions, totalQty, totalCost, totalCurrentValue, totalPnl, totalPct
        );
    }

    private static BigDecimal nullToZero(BigDecimal v) {
        return (v == null) ? BigDecimal.ZERO : v;
    }
}