USE virtual_wallet;

INSERT INTO roles (name)
VALUES ('USER'),
       ('ADMIN');

INSERT INTO currencies (currency_code, name, symbol, decimals, is_active)
VALUES ('USD', 'US Dollar', '$', 2, 1),
       ('EUR', 'Euro', '€', 2, 1),
       ('BGN', 'Bulgarian Lev', 'лв', 2, 0),
       ('GBP', 'British Pound', '£', 2, 1);

INSERT INTO users (username, password_hash, first_name, last_name, email, role_id)
VALUES ('admin', '$2a$12$OXI1ydbzap4eYyUs5zgoyOL27Wt3Hb8XWfD908GFNStr/6GD0A4t2', 'Admin', 'Adminov', 'admin@mail.com', 2),
       ('krushkov', '$2a$12$XPsdsEF8cOWtGC.w/DX9Kuq1DZtpcq7jXnnLPYnQBMyU.4i1Q.UaW', 'Todor', 'Krushkov', 'krushkov@example.com', 1),
       ('gosho', '$2a$12$nfZFgBGhvLmhr9GXs9VEkOXsueNQGnTGvvuY4vZ3iZwt7jZuFmZkC', 'Georgi', 'Georgiev', 'georgi@example.com', 1);

INSERT INTO wallets (user_id, name, balance, currency_code, is_default)
VALUES (2, 'For USA', 500.00, 'USD', FALSE),
       (2, 'Primary', 203.60, 'EUR', TRUE),
       (3, 'Main', 1000.23, 'EUR', TRUE),
       (3, 'Savings UK', 234.25, 'GBP', FALSE);

INSERT INTO cards (user_id, card_holder, card_suffix, expiration_month, expiration_year, status)
VALUES (2, 'TODOR KRUSHKOV', '5931', 12, 2028, 'ACTIVE'),
       (2, 'TODOR KRUSHKOV', '3821', 10, 2029, 'USER_DEACTIVATED'),
       (2, 'TODOR KRUSHKOV', '2831', 3, 2027, 'ADMIN_DEACTIVATED'),
       (3, 'GEORGI GEORGIEV', '5678', 11, 2027, 'ACTIVE');

INSERT INTO exchange_rates (from_currency_code, to_currency_code, rate, last_updated)
VALUES ('EUR', 'USD', 1.10, NOW()),
       ('USD', 'EUR', 0.91, NOW()),
       ('EUR', 'BGN', 1.95583, NOW()),
       ('BGN', 'EUR', 0.51, NOW());

INSERT INTO transactions (label, type, status, sender_amount, sender_currency_code, recipient_amount, recipient_currency_code, exchange_rate, sender_id, sender_wallet_id, recipient_id, recipient_wallet_id)
VALUES ('Top Up ⋅⋅5931', 'TOP_UP', 'CONFIRMED', 200.00, 'EUR', 200.00, 'EUR', 1.0, NULL, NULL, 2, 2),
       ('Top Up ⋅⋅2831', 'TOP_UP', 'CONFIRMED', 200.00, 'EUR', 200.00, 'EUR', 1.0, NULL, NULL, 2, 2),
       ('Top Up ⋅⋅2831', 'TOP_UP', 'CONFIRMED', 200.00, 'EUR', 200.00, 'EUR', 1.0, NULL, NULL, 2, 2),
       ('Transfer krushkov → gosho', 'TRANSFER', 'CONFIRMED', 100.00, 'USD', 91.00, 'EUR', 0.91000000, 3, 3, 2, 1),
       ('Transfer gosho → krushkov', 'TRANSFER', 'CONFIRMED', 91.00, 'EUR', 91.00, 'EUR', 1.00000000, 3, 3, 2, 2),
       ('Primary → MERCHANT', 'PAYMENT', 'CONFIRMED', 100.00, 'EUR', 100.00, 'EUR', 1.00000000, 2, 2, NULL, NULL),
       ('Primary → For USA', 'TRANSFER', 'CONFIRMED', 42.64, 'EUR', 50.00, 'USD', 1.17000000, 2, 2, 2, 1);
