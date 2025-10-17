-- V1: initial schema

create table if not exists assets (
  id          bigserial primary key,
  symbol      varchar(15) not null unique,
  asset_type  varchar(20) not null
    check (asset_type in ('EQUITY','ETF','BOND','CRYPTO','CASH','INDEX')),
  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now()
);

create table if not exists holdings (
  id              bigserial primary key,
  asset_id        bigint not null references assets(id),
  quantity        numeric(28,8) not null,
  purchase_price  numeric(26,6) not null,
  purchase_date   date not null,
  created_at      timestamptz not null default now(),
  updated_at      timestamptz not null default now()
);