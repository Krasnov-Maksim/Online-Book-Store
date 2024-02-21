INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'john@test.com', '$2a$10$l7wlcrpU7ncwbVK/qMUafe7gSFrzpXz3xj4Y3tOJo7BgQZT4rxMaq',
        'John', 'Doe', 'John Shipping Address', FALSE);

INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (2, 'bob@test.com', 'bob1234', 'Bob', 'Davidson', 'Bob Shipping Address', FALSE);

INSERT INTO roles (id, name)
VALUES (1, 'ROLE_USER');

INSERT INTO roles (id, name)
VALUES (2, 'ROLE_ADMIN');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 2);

INSERT INTO users_roles (user_id, role_id)
VALUES (2, 1);

INSERT INTO shopping_carts (id, user_id, is_deleted)
VALUES (1, 1, FALSE);

INSERT INTO shopping_carts (id, user_id, is_deleted)
VALUES (2, 2, FALSE);

INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'Category 1', 'Category 1 description', FALSE);

INSERT INTO categories (id, name, description, is_deleted)
VALUES (2, 'Category 2', 'Category 2 description', FALSE);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES ( 1, 'Book 1', 'Author 1', '978-3-16-148410-0', 100.00, 'Description for Book 1'
       , 'image1.jpg', FALSE);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES ( 2, 'Book 2', 'Author 2', '978-1-4028-9462-6', 200.00, 'Description for Book 2'
       , 'image2.jpg', FALSE);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES ( 3, 'Book 3', 'Author 3', '978-1-56619-909-4', 300.00, 'Description for Book 3'
       , 'image3.jpg', FALSE);

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1),
       (2, 1),
       (3, 1);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 5);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES (1, 1, 'PROCESSING', 500, '2024-01-18 12:45:17', 'John Shipping Address', FALSE);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES (2, 2, 'PENDING', 900, '2024-01-18 12:45:17', 'Bob Shipping Address', FALSE);

INSERT INTO order_items (id, order_id, book_id, quantity, price, is_deleted)
VALUES (1, 1, 1, 1, 100.00, false),
       (2, 1, 2, 2, 200.00, false),
       (3, 2, 3, 3, 300.00, false);
