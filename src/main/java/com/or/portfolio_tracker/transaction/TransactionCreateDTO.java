package com.or.portfolio_tracker.transaction;

import jakarta.validation.constraints.*;
import com.or.portfolio_tracker.transaction.validation.ValidTransactionPayload;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Creation payload with Bean Validation.
 * Field rules + a class-level cross-field rule (@ValidTransactionPayload).
 */
@ValidTransactionPayload
public record TransactionCreateDTO(
        // Nullable: cash-only events may omit an asset
        Long assetId,

        @NotNull(message = "kind is required")
        TransactionKind kind,

        // For BUY/SELL we check >0 in the class-level validator; here just allow null
        @Digits(integer = 20, fraction = 8, message = "quantity precision is up to 20,8")
        @DecimalMin(value = "0.00000000", inclusive = true, message = "quantity must be >= 0")
        BigDecimal quantity,

        // For BUY/SELL we check >0; here just basic format
        @Digits(integer = 20, fraction = 6, message = "price precision is up to 20,6")
        @DecimalMin(value = "0.000000", inclusive = true, message = "price must be >= 0")
        BigDecimal price,

        // For DEPOSIT/WITHDRAWAL we check >0; here basic non-negative
        @Digits(integer = 20, fraction = 2, message = "cashAmount precision is up to 20,2")
        @DecimalMin(value = "0.00", inclusive = true, message = "cashAmount must be >= 0")
        BigDecimal cashAmount,

        @Digits(integer = 20, fraction = 2, message = "fee precision is up to 20,2")
        @DecimalMin(value = "0.00", inclusive = true, message = "fee must be >= 0")
        BigDecimal fee,

        @NotNull(message = "tradeTime is required")
        OffsetDateTime tradeTime,

        @Size(max = 2000, message = "note max length is 2000")
        String note
) {}