DROP TABLE IF EXISTS order_item, orders, product;

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
    line_total   DECIMAL(10,2) NULL,


    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id),

    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id) REFERENCES product(id)
);
