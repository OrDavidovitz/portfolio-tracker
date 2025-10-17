/* V5 — Convert transactions.kind from enum to VARCHAR safely */

/* 0) Drop the CHECK constraint that was compiled against the enum type */
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.table_constraints
    WHERE table_name = 'transactions'
      AND constraint_type = 'CHECK'
      AND constraint_name = 'tx_qty_price_for_trades'
  ) THEN
    ALTER TABLE transactions DROP CONSTRAINT tx_qty_price_for_trades;
  END IF;
END $$;

/* 1) Change column type to VARCHAR using explicit cast from enum -> text */
ALTER TABLE transactions
  ALTER COLUMN kind TYPE VARCHAR(32)
  USING kind::text;

/* 2) Keep NOT NULL */
ALTER TABLE transactions
  ALTER COLUMN kind SET NOT NULL;

/* 3) Re-create the guard-rails CHECK constraint using plain strings */
ALTER TABLE transactions
  ADD CONSTRAINT tx_qty_price_for_trades CHECK (
    (kind IN ('BUY','SELL') AND quantity > 0 AND price IS NOT NULL)
    OR (kind NOT IN ('BUY','SELL'))
  );

/* 4) Drop the enum type now that nothing depends on it */
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'transaction_kind') THEN
    DROP TYPE transaction_kind;
  END IF;
END $$;