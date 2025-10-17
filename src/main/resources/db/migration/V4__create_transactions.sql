-- -----------------------------------------------------------
-- V4 — Create transactions table (portfolio event history)
-- -----------------------------------------------------------
-- Stores raw events: BUY/SELL orders, deposits, withdrawals,
-- dividends, fees, transfers, etc. Complements asset/holding.

-- 1) Idempotent create of enum type for transaction kind
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'transaction_kind') THEN
    CREATE TYPE transaction_kind AS ENUM (
      'BUY', 'SELL',
      'DEPOSIT', 'WITHDRAWAL',
      'DIVIDEND', 'FEE',
      'TRANSFER_IN', 'TRANSFER_OUT'
    );
  END IF;
END $$ LANGUAGE plpgsql;

-- 2) Transactions table
CREATE TABLE IF NOT EXISTS transactions (
  id           BIGSERIAL PRIMARY KEY,

  -- FK to traded asset. For pure cash events you may reference the CASH asset
  -- or leave NULL. We keep history by not cascading deletes.
  asset_id     BIGINT NULL REFERENCES assets(id) ON DELETE SET NULL,

  kind         transaction_kind NOT NULL,

  -- For BUY/SELL: quantity > 0, price required.
  -- For cash-only events (DEPOSIT/WITHDRAWAL/DIVIDEND cash/FEE/TRANSFERS),
  -- quantity may be 0 and price NULL.
  quantity     NUMERIC(20,8) NOT NULL DEFAULT 0,
  price        NUMERIC(20,6),

  -- Explicit cash amount when it doesn’t come from quantity*price,
  -- e.g., $1,500 deposit or $12.34 cash dividend.
  cash_amount  NUMERIC(20,2),

  -- Optional per-transaction fee.
  fee          NUMERIC(20,2) NOT NULL DEFAULT 0,

  -- When the event happened (not insertion time).
  trade_time   TIMESTAMPTZ NOT NULL,

  note         TEXT,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  -- Guard rails
  CONSTRAINT tx_qty_price_for_trades CHECK (
    (kind IN ('BUY','SELL') AND quantity > 0 AND price IS NOT NULL)
    OR (kind NOT IN ('BUY','SELL'))
  ),
  CONSTRAINT tx_non_negative_money CHECK (
    (cash_amount IS NULL OR cash_amount >= 0)
    AND fee >= 0
    AND quantity >= 0
  )
);

-- 3) Helpful indexes (query by asset or kind over time)
CREATE INDEX IF NOT EXISTS idx_tx_asset_time ON transactions (asset_id, trade_time DESC);
CREATE INDEX IF NOT EXISTS idx_tx_kind_time  ON transactions (kind, trade_time DESC);

-- 4) Optional dev seed (only inserts if symbols exist)

-- AAPL buy 2 @ 190
INSERT INTO transactions (asset_id, kind, quantity, price, fee, trade_time, note)
SELECT a.id, 'BUY', 2, 190.00, 0, NOW() - INTERVAL '7 days', 'Seed: AAPL buy'
FROM assets a
WHERE a.symbol = 'AAPL'
  AND NOT EXISTS (
    SELECT 1 FROM transactions t
    WHERE t.kind = 'BUY' AND t.asset_id = a.id AND t.quantity = 2 AND t.price = 190.00
  )
LIMIT 1;

-- AAPL buy 3 @ 200
INSERT INTO transactions (asset_id, kind, quantity, price, fee, trade_time, note)
SELECT a.id, 'BUY', 3, 200.00, 0, NOW() - INTERVAL '5 days', 'Seed: AAPL buy #2'
FROM assets a
WHERE a.symbol = 'AAPL'
  AND NOT EXISTS (
    SELECT 1 FROM transactions t
    WHERE t.kind = 'BUY' AND t.asset_id = a.id AND t.quantity = 3 AND t.price = 200.00
  )
LIMIT 1;

-- BTC buy 0.01 @ 60000
INSERT INTO transactions (asset_id, kind, quantity, price, fee, trade_time, note)
SELECT a.id, 'BUY', 0.01, 60000.00, 0, NOW() - INTERVAL '6 days', 'Seed: BTC buy'
FROM assets a
WHERE a.symbol = 'BTC'
  AND NOT EXISTS (
    SELECT 1 FROM transactions t
    WHERE t.kind = 'BUY' AND t.asset_id = a.id AND t.quantity = 0.01 AND t.price = 60000.00
  )
LIMIT 1;

-- Cash deposit $1500 — will use CASH asset if present; otherwise skip
INSERT INTO transactions (asset_id, kind, cash_amount, trade_time, note)
SELECT a.id, 'DEPOSIT', 1500.00, NOW() - INTERVAL '8 days', 'Seed: cash deposit'
FROM assets a
WHERE a.symbol = 'CASH'
  AND NOT EXISTS (
    SELECT 1 FROM transactions t
    WHERE t.kind = 'DEPOSIT' AND t.cash_amount = 1500.00
  )
LIMIT 1;