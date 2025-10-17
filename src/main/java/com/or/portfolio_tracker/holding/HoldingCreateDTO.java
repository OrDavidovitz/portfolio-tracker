package com.or.portfolio_tracker.holding;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Input model for creating a new Holding.
 * Keeps the API payload minimal and stable.
 */
public class HoldingCreateDTO {

    @NotNull(message = "assetId is required")
    private Long assetId;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.00000001", message = "quantity must be > 0")
    @Digits(integer = 20, fraction = 8)
    private BigDecimal quantity;

    @NotNull(message = "purchasePrice is required")
    @DecimalMin(value = "0.0", message = "purchasePrice must be >= 0")
    @Digits(integer = 20, fraction = 6)
    private BigDecimal purchasePrice;

    @NotNull(message = "purchaseDate is required")
    private LocalDate purchaseDate;

    // getters/setters
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
}