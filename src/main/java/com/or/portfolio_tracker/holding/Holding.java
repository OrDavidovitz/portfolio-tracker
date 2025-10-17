package com.or.portfolio_tracker.holding;

import com.or.portfolio_tracker.asset.Asset;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Entity representing a Holding: a user's position in a specific Asset.
 * Each Holding is linked to exactly one Asset.
 */
@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many holdings can reference the same asset.
     * The relation is mandatory (no holding without asset).
     * FetchType.LAZY = asset data will be loaded only when accessed.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    /**
     * Quantity of the asset owned.
     * Stored with high precision (28 total digits, 8 after decimal).
     */
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be > 0")
    @Digits(integer = 20, fraction = 8)
    @Column(nullable = false, precision = 28, scale = 8)
    private BigDecimal quantity;

    /**
     * Purchase price per unit at the time of acquisition.
     * Precision: up to 20 digits before decimal, 6 after.
     */
    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", message = "Purchase price must be >= 0")
    @Digits(integer = 20, fraction = 6)
    @Column(name = "purchase_price", nullable = false, precision = 26, scale = 6)
    private BigDecimal purchasePrice;

    /**
     * Date when the holding was purchased.
     */
    @NotNull(message = "Purchase date is required")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /**
     * Auto-managed timestamps.
     */
    @Column(name = "created_at", updatable = false, nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssXXX",
            timezone = "Asia/Jerusalem")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssXXX",
            timezone = "Asia/Jerusalem")
    private Instant updatedAt;

    public Holding() {}

    public Holding(Asset asset, BigDecimal quantity, BigDecimal purchasePrice, LocalDate purchaseDate) {
        this.asset = asset;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}