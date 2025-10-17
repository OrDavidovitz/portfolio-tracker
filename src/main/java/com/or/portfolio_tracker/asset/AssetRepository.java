package com.or.portfolio_tracker.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for Asset entities.
 *
 * Extends Spring Data JPA's JpaRepository, which already provides
 * basic CRUD methods (save, findAll, findById, delete, etc).
 *
 * Here we add custom query methods for Asset-specific lookups.
 */
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * Find an Asset by its unique symbol.
     *
     * @param symbol stock/crypto/etc symbol (e.g. "AAPL")
     * @return Optional containing the Asset if found
     */
    Optional<Asset> findBySymbol(String symbol);

    /**
     * Check if an Asset already exists with the given symbol.
     *
     * @param symbol symbol to check
     * @return true if an Asset exists, false otherwise
     */
    boolean existsBySymbol(String symbol);
}