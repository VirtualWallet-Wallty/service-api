DROP SCHEMA IF EXISTS virtual_wallet;
CREATE SCHEMA virtual_wallet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE virtual_wallet;

CREATE TABLE roles
(
    role_id BIGINT      NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(10) NOT NULL UNIQUE,

    CHECK ( name IN ('USER', 'ADMIN') )
);

CREATE TABLE users
(
    user_id       BIGINT       NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,

    email         VARCHAR(255) NOT NULL UNIQUE,
    phone_number  VARCHAR(10)  NULL UNIQUE,
    photo_url     VARCHAR(512) NULL,

    role_id       BIGINT       NOT NULL,

    is_blocked    BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE currencies
(
    currency_code VARCHAR(3)  NOT NULL UNIQUE PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    symbol        VARCHAR(5)  NOT NULL,
    decimals      INT         NOT NULL DEFAULT 2,
    is_active     BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE wallets
(
    wallet_id     BIGINT         NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT         NOT NULL,

    name          VARCHAR(16)    NOT NULL,
    balance       DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    currency_code VARCHAR(3)     NOT NULL,

    is_default    BOOLEAN        NOT NULL DEFAULT FALSE,
    version       BIGINT         NOT NULL DEFAULT 0,

    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    is_deleted    BOOLEAN        NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_wallet_currencies FOREIGN KEY (currency_code) REFERENCES currencies (currency_code)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CHECK ( balance >= 0 )
);

CREATE TABLE cards
(
    card_id          BIGINT       NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT,
    user_id          BIGINT       NOT NULL,

    card_holder      VARCHAR(100) NOT NULL,
    card_suffix      VARCHAR(4)   NOT NULL,

    expiration_month INT          NOT NULL,
    expiration_year  INT          NOT NULL,

    status           VARCHAR(20)  NOT NULL,

    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    is_deleted       BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CHECK ( status IN ('ACTIVE', 'USER_DEACTIVATED', 'ADMIN_DEACTIVATED') )
);

CREATE TABLE exchange_rates
(
    exchange_rate_id   BIGINT AUTO_INCREMENT PRIMARY KEY,

    from_currency_code VARCHAR(3)     NOT NULL,
    to_currency_code   VARCHAR(3)     NOT NULL,

    rate               DECIMAL(19, 8) NOT NULL,
    last_updated       TIMESTAMP      NOT NULL,

    CONSTRAINT fk_er_from_currency FOREIGN KEY (from_currency_code) REFERENCES currencies (currency_code)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_er_to_currency FOREIGN KEY (to_currency_code) REFERENCES currencies (currency_code)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT uq_exchange UNIQUE (from_currency_code, to_currency_code),

    CHECK ( rate > 0 )
);

CREATE TABLE transactions
(
    transaction_id          BIGINT         NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT,
    label                   VARCHAR(50)    NULL,

    type                    VARCHAR(20)    NOT NULL,
    status                  VARCHAR(20)    NOT NULL,

    sender_amount           DECIMAL(19, 2) NOT NULL,
    sender_currency_code    VARCHAR(3)     NOT NULL,

    recipient_amount        DECIMAL(19, 2) NOT NULL,
    recipient_currency_code VARCHAR(3)     NOT NULL,

    exchange_rate           DECIMAL(19, 8) NOT NULL,

    sender_id               BIGINT         NULL,
    sender_wallet_id        BIGINT         NULL,

    recipient_id            BIGINT         NULL,
    recipient_wallet_id     BIGINT         NULL,

    external_reference      VARCHAR(255)   NULL,

    created_at              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tx_sender_currency FOREIGN KEY (sender_currency_code) REFERENCES currencies (currency_code)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_tx_recipient_currency FOREIGN KEY (recipient_currency_code) REFERENCES currencies (currency_code)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_tx_sender_user FOREIGN KEY (sender_id) REFERENCES users (user_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_tx_recipient_user FOREIGN KEY (recipient_id) REFERENCES users (user_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_tx_sender_wallet FOREIGN KEY (sender_wallet_id) REFERENCES wallets (wallet_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT fk_tx_recipient_wallet FOREIGN KEY (recipient_wallet_id) REFERENCES wallets (wallet_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    CHECK ( type IN ('TRANSFER', 'TOP_UP', 'PAYMENT') ),
    CHECK ( status IN ('PENDING', 'CONFIRMED', 'FAILED', 'REJECTED') ),
    CHECK ( sender_amount > 0 ),
    CHECK ( recipient_amount > 0 ),
    CHECK ( exchange_rate > 0 )
);

CREATE INDEX idx_exchange_from ON exchange_rates (from_currency_code);
CREATE INDEX idx_exchange_to ON exchange_rates (to_currency_code);

CREATE INDEX idx_tx_created_at ON transactions (created_at);

CREATE INDEX idx_tx_sender_wallet ON transactions (sender_wallet_id);
CREATE INDEX idx_tx_recipient_wallet ON transactions (recipient_wallet_id);

CREATE INDEX idx_tx_sender_user ON transactions (sender_id);
CREATE INDEX idx_tx_recipient_user ON transactions (recipient_id);

CREATE INDEX idx_tx_type ON transactions (type);
CREATE INDEX idx_tx_status ON transactions (status);