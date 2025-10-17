package com.or.portfolio_tracker.transaction.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TransactionPayloadValidator.class)
public @interface ValidTransactionPayload {
    String message() default "Invalid transaction payload for the given kind";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}