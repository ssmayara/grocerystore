DROP TABLE IF EXISTS order_item, orders, product_discount, product, discount;

CREATE TABLE IF NOT EXISTS product (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,

    name            VARCHAR(100) NOT NULL,
    category        VARCHAR(100) NOT NULL,
    unit_type       VARCHAR(100) NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at       DATETIME NULL DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NULL
);

CREATE TABLE IF NOT EXISTS order_item (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,

    quantity     INT NOT NULL DEFAULT 1,
    weight_grams INT NULL,
    unit_price   DECIMAL(10,2) NOT NULL,
    discount_applied DECIMAL(10,2) NULL,
    line_total   DECIMAL(10,2) NULL,


    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id),

    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE IF NOT EXISTS discount (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,

    product_category   VARCHAR(100) NOT NULL,
    rule_type          VARCHAR(100) NOT NULL,

    buy_qty         INT NULL,
    take_qty        INT NULL,
    min_day_age     INT NULL,
    max_day_age     INT NULL,

    min_weight_grams INT NULL,
    max_weight_grams INT NULL,
    discount_percent DECIMAL(5,2) NULL,

    pack_size       INT NULL,
    beer_country    VARCHAR(100) NULL,
    discount_amount DECIMAL(10,2) NULL,

    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at       DATETIME NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_discount (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    discount_id     BIGINT NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_discount_product
        FOREIGN KEY (product_id) REFERENCES product(id),

    CONSTRAINT fk_product_discount_discount
        FOREIGN KEY (discount_id) REFERENCES discount(id)
);

INSERT INTO product (name, category, unit_type, unit_price, created_at)
VALUES
    ('French Bread Fresh', 'BREAD', 'DAY', 1.00, NOW()),
    ('French Bread 3 days', 'BREAD', 'DAY', 1.00, (NOW() - INTERVAL 3 DAY)),
    ('French Bread 6 days', 'BREAD', 'DAY', 1.00, (NOW() - INTERVAL 6 DAY)),
    ('French Bread 7 days', 'BREAD', 'DAY', 1.00, (NOW() - INTERVAL 7 DAY)),
    ('Tomato', 'VEGETABLE', 'GRAM', 1.00, NOW()),
    ('Potato', 'VEGETABLE', 'GRAM', 1.20, NOW()),
    ('Beer Bottle Dutch', 'BEER', 'UNIT', 0.50, NOW()),
    ('Beer Bottle Belgium', 'BEER', 'UNIT', 0.50, NOW()),
    ('Beer Bottle Germany', 'BEER', 'UNIT', 0.50, NOW());


INSERT INTO discount (product_category, rule_type, buy_qty, take_qty, min_day_age, max_day_age, active)
VALUES ('BREAD', 'BUY_TAKE', 1, 2, 3, 5, TRUE);

INSERT INTO discount (product_category, rule_type, buy_qty, take_qty, min_day_age, max_day_age, active)
VALUES ('BREAD', 'BUY_TAKE', 1, 3, 6, 6, TRUE);

INSERT INTO discount (product_category, rule_type, min_weight_grams, max_weight_grams, discount_percent, active)
 VALUES ('VEGETABLE', 'PERCENTAGE', 0, 100, 5.00, TRUE);

 INSERT INTO discount (product_category, rule_type, min_weight_grams, max_weight_grams, discount_percent, active)
 VALUES ('VEGETABLE', 'PERCENTAGE', 101, 500, 7.00, TRUE);

 INSERT INTO discount (product_category, rule_type, min_weight_grams, max_weight_grams, discount_percent, active)
 VALUES ('VEGETABLE', 'PERCENTAGE', 501, 999999, 10.00, TRUE);

 INSERT INTO discount (product_category, rule_type, pack_size, beer_country, discount_amount, active)
 VALUES ('BEER', 'FIXED_AMOUNT', 6, 'BELGIUM', 3.00, TRUE);

 INSERT INTO discount (product_category, rule_type, pack_size, beer_country, discount_amount, active)
 VALUES ('BEER', 'FIXED_AMOUNT', 6, 'NETHERLANDS', 2.00, TRUE);

 INSERT INTO discount (product_category, rule_type, pack_size, beer_country, discount_amount, active)
 VALUES ('BEER', 'FIXED_AMOUNT', 6, 'GERMANY', 4.00, TRUE);

 INSERT INTO product_discount (product_id, discount_id) VALUES
     (1, 1),
     (1, 2),
     (2, 1),
     (2, 2),
     (3, 1),
     (3, 2),
     (4, 1),
     (4, 2),
     (5, 3),
     (5, 4),
     (5, 5),
     (7,7),
     (8,6),
     (9,8);
