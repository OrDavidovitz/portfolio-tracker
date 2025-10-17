package com.or.portfolio_tracker.asset;

import com.or.portfolio_tracker.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Asset business logic.
 */
@Service
public class AssetService {
    private final AssetRepository repo;

    public AssetService(AssetRepository repo) {
        this.repo = repo;
    }

    /** Create from DTO, enforcing symbol uniqueness. */
    @Transactional
    public Asset add(AssetCreateDTO dto) {
        String symbol = dto.getSymbol().trim(); // normalize input

        if (repo.existsBySymbol(symbol)) {
            throw new IllegalStateException(
                    "Asset with symbol '" + symbol + "' already exists"
            );
        }
        Asset a = new Asset(symbol, dto.getAssetType());
        return repo.save(a);
    }

    public Asset getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset id " + id + " not found"));
    }

    public Asset getBySymbol(String symbol) {
        return repo.findBySymbol(symbol.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Asset symbol '" + symbol + "' not found"));
    }

    /** Full replacement (PUT) from DTO, with uniqueness check if symbol changes. */
    @Transactional
    public Asset update(Long id, AssetUpdateDTO dto) {
        Asset existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset id " + id + " not found"));

        String newSymbol = dto.getSymbol().trim();

        // If symbol is changing to a value that exists on another record -> 409
        if (repo.existsBySymbol(newSymbol) &&
                !newSymbol.equals(existing.getSymbol())) {
            throw new IllegalStateException(
                    "Asset with symbol '" + newSymbol + "' already exists"
            );
        }

        existing.setSymbol(newSymbol);
        existing.setAssetType(dto.getAssetType());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Asset id " + id + " not found");
        }
        repo.deleteById(id);
    }

    public Page<Asset> list(Pageable pageable) {
        return repo.findAll(pageable);
    }
}