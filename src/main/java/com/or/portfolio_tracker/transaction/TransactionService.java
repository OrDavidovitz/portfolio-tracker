package com.or.portfolio_tracker.transaction;

import com.or.portfolio_tracker.asset.Asset;
import com.or.portfolio_tracker.asset.AssetRepository;
import com.or.portfolio_tracker.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository repo;
    private final AssetRepository assetRepo;

    public TransactionService(TransactionRepository repo, AssetRepository assetRepo) {
        this.repo = repo;
        this.assetRepo = assetRepo;
    }

    @Transactional
    public Transaction create(TransactionCreateDTO dto) {
        // 1) Optional asset lookup
        Asset asset = null;
        if (dto.assetId() != null) {
            asset = assetRepo.findById(dto.assetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset id " + dto.assetId() + " not found"));
        }

        // 2) Normalize + validate by kind
        final TransactionKind kind = dto.kind();
        if (kind == null) {
            throw new IllegalArgumentException("kind is required");
        }

        final Transaction t = new Transaction();
        t.setAsset(asset);
        t.setKind(kind);
        t.setFee(nonNull(dto.fee()));
        t.setTradeTime(dto.tradeTime() != null ? dto.tradeTime() : java.time.OffsetDateTime.now());
        t.setNote(dto.note());

        switch (kind) {
            case BUY, SELL -> {
                // Trades must include positive quantity and a price
                if (dto.quantity() == null || dto.quantity().signum() <= 0) {
                    throw new IllegalArgumentException("BUY/SELL must have quantity > 0");
                }
                if (dto.price() == null) {
                    throw new IllegalArgumentException("BUY/SELL must have a price");
                }
                t.setQuantity(dto.quantity());
                t.setPrice(dto.price());
                // cashAmount is optional for trades (e.g., rebates); keep as-is
                t.setCashAmount(dto.cashAmount());
            }
            default -> {
                // Cash-only events: force quantity=0 and price=NULL
                t.setQuantity(BigDecimal.ZERO);
                t.setPrice(null);

                // Require a non-negative cashAmount
                BigDecimal cash = dto.cashAmount();
                if (cash == null || cash.signum() < 0) {
                    throw new IllegalArgumentException(kind + " requires non-negative cashAmount");
                }
                t.setCashAmount(cash);
            }
        }

        return repo.save(t);
    }

    private static BigDecimal nonNull(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}