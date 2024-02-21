INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES (1, 1, 'COMPLETED', 5, '2024-01-18 12:45:17', 'Shipping address 1', FALSE);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES (2, 1, 'DELIVERED', 10, '2024-01-18 12:45:17', 'Shipping address 1', FALSE);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES (3, 2, 'PROCESSING', 15, '2023-01-18 12:45:17', 'Shipping address 2', FALSE);
