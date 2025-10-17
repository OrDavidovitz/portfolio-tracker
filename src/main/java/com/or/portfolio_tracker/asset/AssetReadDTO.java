package com.or.portfolio_tracker.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
/**
 * Read-only DTO returned by AssetController endpoints.
 *
 * Provides a safe API view of the Asset entity:
 *  - Exposes id, symbol, type, and timestamps
 *  - Uses ISO-8601 string formatting (with timezone Asia/Jerusalem)
 *
 * We keep DTOs separate from entities to avoid exposing JPA internals
 * or accidental lazy-loading in API responses.
 */
public record AssetReadDTO(
        Long id,
        String symbol,
        AssetType assetType,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ssXXX",
                timezone = "Asia/Jerusalem")
        Instant createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ssXXX",
                timezone = "Asia/Jerusalem")
        Instant updatedAt
) {
    public static AssetReadDTO fromEntity(Asset a) {
        return new AssetReadDTO(
                a.getId(),
                a.getSymbol(),
                a.getAssetType(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}