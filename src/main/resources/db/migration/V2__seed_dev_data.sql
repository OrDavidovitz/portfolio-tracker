-- V2: optional dev seed

insert into assets(symbol, asset_type)
values ('AAPL','EQUITY'),
       ('NVDA','EQUITY'),
       ('BTC','CRYPTO'),
       ('CASH','CASH')
on conflict (symbol) do nothing;


insert into holdings(asset_id, quantity, purchase_price, purchase_date)
values
  ((select id from assets where symbol='AAPL'), 2.0, 190.00, date '2025-10-10'),
  ((select id from assets where symbol='AAPL'), 2.0, 200.00, date '2025-10-12'),
  ((select id from assets where symbol='BTC'),  0.01000000, 60000.000000, date '2025-10-11'),
  ((select id from assets where symbol='BTC'),  0.00500000, 62000.000000, date '2025-10-13'),
  ((select id from assets where symbol='CASH'), 1500.00000000, 1.000000,  date '2025-10-14');