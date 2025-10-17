package com.or.portfolio_tracker.holding;

import com.or.portfolio_tracker.asset.AssetType;

import java.math.BigDecimal;

public class HoldingReadDTO {
    private final Long id;
    private final NestedAsset asset;        // denormalized asset info
    private final BigDecimal quantity;
    private final BigDecimal purchasePrice;
    private final java.time.LocalDate purchaseDate;

    public HoldingReadDTO(Long id,
                          NestedAsset asset,
                          BigDecimal quantity,
                          BigDecimal purchasePrice,
                          java.time.LocalDate purchaseDate) {
        this.id = id;
        this.asset = asset;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    // ---- getters for Jackson ----
    public Long getId() { return id; }
    public NestedAsset getAsset() { return asset; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public java.time.LocalDate getPurchaseDate() { return purchaseDate; }

    // Factory: build DTO from entity (safe to call INSIDE a transaction)
    public static HoldingReadDTO fromEntity(Holding h) {
        NestedAsset nested = new NestedAsset(
                h.getAsset().getSymbol(),
                h.getAsset().getAssetType()
        );
        return new HoldingReadDTO(
                h.getId(),
                nested,
                h.getQuantity(),
                h.getPurchasePrice(),
                h.getPurchaseDate()
        );
    }

    // Minimal nested asset DTO
    public static class NestedAsset {
        private final String symbol;
        private final AssetType assetType;

        public NestedAsset(String symbol, AssetType assetType) {
            this.symbol = symbol;
            this.assetType = assetType;
        }

        // >>> אלה קריטיים! <<<
        public String getSymbol() { return symbol; }
        public AssetType getAssetType() { return assetType; }
    }
}