package com.or.portfolio_tracker.transaction.validation;

import com.or.portfolio_tracker.transaction.TransactionCreateDTO;
import com.or.portfolio_tracker.transaction.TransactionKind;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class TransactionPayloadValidator implements ConstraintValidator<ValidTransactionPayload, TransactionCreateDTO> {

    @Override
    public boolean isValid(TransactionCreateDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.kind() == null) return true; // @NotNull on kind handles null

        // Helper predicates
        Predicate<BigDecimal> gtZero = v -> v != null && v.signum() > 0;
        Predicate<BigDecimal> isZeroOrNull = v -> v == null || v.signum() == 0;

        // We'll add field-specific messages
        ctx.disableDefaultConstraintViolation();

        switch (dto.kind()) {
            case BUY, SELL -> {
                if (!gtZero.test(dto.quantity())) {
                    add(ctx, "quantity", "BUY/SELL require quantity > 0");
                    return false;
                }
                if (!gtZero.test(dto.price())) {
                    add(ctx, "price", "BUY/SELL require price > 0");
                    return false;
                }
                return true; // fee >= 0 is enforced at field level
            }

            case DEPOSIT, WITHDRAWAL -> {
                if (!gtZero.test(dto.cashAmount())) {
                    add(ctx, "cashAmount", dto.kind() + " requires cashAmount > 0");
                    return false;
                }
                if (!isZeroOrNull.test(dto.quantity())) {
                    add(ctx, "quantity", dto.kind() + " should not have a positive quantity");
                    return false;
                }
                if (dto.price() != null && dto.price().signum() != 0) {
                    add(ctx, "price", dto.kind() + " should not have a price");
                    return false;
                }
                return true;
            }

            case DIVIDEND, FEE, TRANSFER_IN, TRANSFER_OUT -> {
                // Keep flexible; field-level constraints already apply.
                return true;
            }
        }
        return true;
    }

    private void add(ConstraintValidatorContext ctx, String field, String message) {
        ctx.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}