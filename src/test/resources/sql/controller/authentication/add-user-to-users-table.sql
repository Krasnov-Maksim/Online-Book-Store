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
