package com.or.portfolio_tracker.transaction;

import com.or.portfolio_tracker.asset.AssetType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

public record TransactionReadDTO(
        Long id,
        Long assetId,
        String assetSymbol,
        AssetType assetType,
        TransactionKind kind,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal cashAmount,
        BigDecimal fee,
        OffsetDateTime tradeTime,
        String note,
        OffsetDateTime createdAt
) {
    public static TransactionReadDTO fromEntity(Transaction t) {
        var a = t.getAsset();
        return new TransactionReadDTO(
                t.getId(),
                (a != null ? a.getId() : null),
                (a != null ? a.getSymbol() : null),
                (a != null ? a.getAssetType() : null),
                t.getKind(),
                safeScale(t.getQuantity(), 8),
                safeScale(t.getPrice(), 6),
                safeScale(t.getCashAmount(), 2),
                safeScale(t.getFee(), 2),
                t.getTradeTime(),
                t.getNote(),
                t.getCreatedAt()
        );
    }

    private static BigDecimal safeScale(BigDecimal v, int scale) {
        return v == null ? null : v.setScale(scale, RoundingMode.UNNECESSARY);
    }
}