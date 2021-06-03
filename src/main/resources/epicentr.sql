CREATE TABLE IF NOT EXISTS cities
(
    id   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS shops
(
    id      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    city_id INT UNSIGNED NOT NULL,
    address VARCHAR(255),
    FOREIGN KEY (city_id) REFERENCES cities (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS products
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id INT UNSIGNED,
    name        VARCHAR(255),
    brand       VARCHAR(255),
    price       DOUBLE,

    FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS product_shop
(
    product_id INT UNSIGNED,
    shop_id    INT UNSIGNED,
    count      INT UNSIGNED,

    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (shop_id) REFERENCES shops (id)
);

CREATE INDEX product_shop_index ON product_shop (product_id, shop_id);
