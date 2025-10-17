package com.or.portfolio_tracker.transaction;

/**
 * Discrete kinds of portfolio events stored in the transactions table.
 * Keep in sync with the DB enum (Flyway V4 migration).
 */
public enum TransactionKind {
    BUY, SELL,
    DEPOSIT, WITHDRAWAL,
    DIVIDEND, FEE,
    TRANSFER_IN, TRANSFER_OUT
}