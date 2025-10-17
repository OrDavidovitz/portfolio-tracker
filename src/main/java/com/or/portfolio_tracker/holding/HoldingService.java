package com.or.portfolio_tracker.holding;

import com.or.portfolio_tracker.asset.Asset;
import com.or.portfolio_tracker.asset.AssetRepository;
import com.or.portfolio_tracker.asset.AssetType;
import com.or.portfolio_tracker.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Holding domain logic:
 * - Validates asset existence (and CASH rules)
 * - Delegates persistence to the repository
 */
@Service
public class HoldingService {

    private final HoldingRepository holdingRepo;
    private final AssetRepository assetRepo;

    public HoldingService(HoldingRepository holdingRepo, AssetRepository assetRepo) {
        this.holdingRepo = holdingRepo;
        this.assetRepo = assetRepo;
    }

    @Transactional
    public Holding create(HoldingCreateDTO dto) {
        Asset asset = assetRepo.findById(dto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset id " + dto.getAssetId() + " not found"));

        // Business rule for CASH (optional, as you already added)
        if (asset.getAssetType() == AssetType.CASH) {
            if (dto.getPurchasePrice() == null || dto.getPurchasePrice().compareTo(BigDecimal.ONE) != 0) {
                throw new IllegalStateException("For CASH holdings, purchasePrice must be 1");
            }
            if (dto.getQuantity().signum() < 0) {
                throw new IllegalStateException("Quantity for CASH cannot be negative");
            }
        }

        Holding h = new Holding(asset, dto.getQuantity(), dto.getPurchasePrice(), dto.getPurchaseDate());
        return holdingRepo.save(h);
    }

    @Transactional(readOnly = true)
    public Page<HoldingReadDTO> list(Pageable pageable) {
        return holdingRepo.findAll(pageable)
                .map(HoldingReadDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Holding getById(Long id) {
        return holdingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holding id " + id + " not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (!holdingRepo.existsById(id)) {
            throw new ResourceNotFoundException("Holding id " + id + " not found");
        }
        holdingRepo.deleteById(id);
    }
}