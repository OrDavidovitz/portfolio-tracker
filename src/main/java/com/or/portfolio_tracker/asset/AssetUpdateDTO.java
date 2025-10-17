package com.or.portfolio_tracker.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Input model for full replacement (PUT) of an Asset.
 * Same fields as create; client must provide both.
 */
public class AssetUpdateDTO {

    @NotBlank(message = "Symbol cannot be blank")
    @Pattern(regexp = "^[A-Z0-9.-]{1,15}$",
            message = "Symbol must be UPPERCASE letters, digits, dots or dashes (1-15)")
    private String symbol;

    @NotNull(message = "Asset type cannot be null")
    private AssetType assetType;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public AssetType getAssetType() { return assetType; }
    public void setAssetType(AssetType assetType) { this.assetType = assetType; }
}