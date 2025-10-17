package com.or.portfolio_tracker.asset;

/**
 * Enum representing the types of assets supported by the system.
 *
 * Used to classify each Asset into one of the allowed categories.
 */
public enum AssetType {
    EQUITY,     // Single stocks
    ETF,        // Exchange-Traded Funds
    BOND,       // Bonds
    CRYPTO,     // Cryptocurrencies
    CASH,       // Cash holdings
    INDEX       // Direct market indices (e.g. S&P 500, NASDAQ)
}