CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_reference VARCHAR(255) NOT NULL UNIQUE,
    item_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    shipping_address TEXT NOT NULL,
    status ENUM('NEW', 'DISPATCHED', 'CANCELLED') DEFAULT 'NEW',
    placement_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_status ON orders (status);