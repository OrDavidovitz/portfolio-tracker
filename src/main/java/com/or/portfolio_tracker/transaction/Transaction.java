package com.or.portfolio_tracker.transaction;

import com.or.portfolio_tracker.asset.Asset;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * JPA entity for a portfolio event (BUY/SELL/DEPOSIT/...).
 * Matches db/migration/V4__create_transactions.sql.
 *
 * Notes:
 * - asset is nullable (cash-only events).
 * - quantity >= 0; for BUY/SELL, service-level validation should enforce > 0 and price != null.
 * - fee, cashAmount are non-negative monetary amounts.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable: deposits/withdrawals/dividends may not reference a specific Asset.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false)
    private TransactionKind kind;

    // Quantity is 0 for cash-only events; precision/scale match migration (NUMERIC(20,8)).
    @NotNull
    @DecimalMin("0")
    @Column(name = "quantity", nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity = BigDecimal.ZERO;

    // For BUY/SELL only. Precision/scale match migration (NUMERIC(20,6)).
    @Column(name = "price", precision = 20, scale = 6)
    private BigDecimal price;

    // Explicit cash amount for deposits/dividends/withdrawals/fees (NUMERIC(20,2)).
    @DecimalMin("0")
    @Column(name = "cash_amount", precision = 20, scale = 2)
    private BigDecimal cashAmount;

    // Optional fee per transaction (NUMERIC(20,2), default 0).
    @NotNull
    @DecimalMin("0")
    @Column(name = "fee", nullable = false, precision = 20, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    @NotNull
    @Column(name = "trade_time", nullable = false)
    private OffsetDateTime tradeTime;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Transaction() {
        // JPA
    }

    public Transaction(
            Asset asset,
            TransactionKind kind,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal cashAmount,
            BigDecimal fee,
            OffsetDateTime tradeTime,
            String note
    ) {
        this.asset = asset;
        this.kind = kind;
        this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
        this.price = price;
        this.cashAmount = cashAmount;
        this.fee = fee != null ? fee : BigDecimal.ZERO;
        this.tradeTime = tradeTime;
        this.note = note;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    // ----- Getters & Setters -----

    public Long getId() { return id; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public TransactionKind getKind() { return kind; }
    public void setKind(TransactionKind kind) { this.kind = kind; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCashAmount() { return cashAmount; }
    public void setCashAmount(BigDecimal cashAmount) { this.cashAmount = cashAmount; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public OffsetDateTime getTradeTime() { return tradeTime; }
    public void setTradeTime(OffsetDateTime tradeTime) { this.tradeTime = tradeTime; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    // ----- Equality by id (standard JPA pattern) -----

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}