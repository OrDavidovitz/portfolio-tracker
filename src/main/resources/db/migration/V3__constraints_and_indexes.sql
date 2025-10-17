-- V3: Constraints & Indexes hardening

-- 1) Enforce unique symbol at the database level (creates a unique btree index under the hood)
ALTER TABLE assets
    ADD CONSTRAINT uk_assets_symbol UNIQUE (symbol);

-- 2) Add a proper foreign key from holdings -> assets
--    RESTRICT deletes of an asset that still has holdings (prevents accidental cascade wipes).
ALTER TABLE holdings
    ADD CONSTRAINT fk_holdings_asset
        FOREIGN KEY (asset_id)
        REFERENCES assets(id)
        ON DELETE RESTRICT;

-- 3) Basic safety checks (business logic is still enforced in service layer, but DB should reject obvious bad data)
--    Quantities must be non-negative (allows CASH top-ups/withdrawals modeled as positive/zero).
ALTER TABLE holdings
    ADD CONSTRAINT chk_holdings_quantity_non_negative
        CHECK (quantity >= 0);

--    Purchase price must be non-negative (CASH=1 is enforced at service-level; DB just blocks negatives).
ALTER TABLE holdings
    ADD CONSTRAINT chk_holdings_price_non_negative
        CHECK (purchase_price >= 0);

-- 4) Performance indexes for common queries/aggregations

--    Filter/sort/group by asset type quickly (portfolio screens, filtering).
CREATE INDEX idx_assets_asset_type
    ON assets(asset_type);

--    Speed up joins & group-bys on holdings by asset.
CREATE INDEX idx_holdings_asset_id
    ON holdings(asset_id);

--    Common reporting pattern: “per asset over time”
--    This composite index helps queries like:
--      WHERE asset_id = ? ORDER BY purchase_date DESC
CREATE INDEX idx_holdings_asset_id_purchase_date_desc
    ON holdings(asset_id, purchase_date DESC);