package com.or.portfolio_tracker.transaction;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionPayloadValidatorTest {

    static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
    }

    private TransactionCreateDTO dto(
            Long assetId,
            TransactionKind kind,
            BigDecimal qty,
            BigDecimal price,
            BigDecimal cash,
            BigDecimal fee
    ) {
        return new TransactionCreateDTO(
                assetId,
                kind,
                qty,
                price,
                cash,
                fee,
                OffsetDateTime.parse("2025-10-17T10:00:00Z"),
                "test"
        );
    }

    @Test
    void buy_ok() {
        var d = dto(1L, TransactionKind.BUY,
                new BigDecimal("2.0"),
                new BigDecimal("190"),
                null,
                BigDecimal.ZERO);
        assertThat(validator.validate(d)).isEmpty();
    }

    @Test
    void buy_missingQuantity_shouldFail() {
        var d = dto(1L, TransactionKind.BUY,
                null,
                new BigDecimal("190"),
                null,
                BigDecimal.ZERO);
        assertThat(validator.validate(d)).isNotEmpty();
    }

    @Test
    void sell_missingPrice_shouldFail() {
        var d = dto(1L, TransactionKind.SELL,
                new BigDecimal("1"),
                null,
                null,
                BigDecimal.ZERO);
        assertThat(validator.validate(d)).isNotEmpty();
    }

    @Test
    void deposit_ok() {
        var d = dto(4L, TransactionKind.DEPOSIT,
                BigDecimal.ZERO, // or null
                null,
                new BigDecimal("500"),
                BigDecimal.ZERO);
        assertThat(validator.validate(d)).isEmpty();
    }

    @Test
    void deposit_missingCash_shouldFail() {
        var d = dto(4L, TransactionKind.DEPOSIT,
                BigDecimal.ZERO,
                null,
                null,
                BigDecimal.ZERO);
        assertThat(validator.validate(d)).isNotEmpty();
    }

    @Test
    void withdrawal_withQuantity_shouldFail() {
        var d = dto(4L, TransactionKind.WITHDRAWAL,
                new BigDecimal("1"),
                null,
                new BigDecimal("200"),
                BigDecimal.ZERO);
        assertThat(validator.validate(d)).isNotEmpty();
    }
}