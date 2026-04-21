CREATE DATABASE IF NOT EXISTS marketplace;
USE marketplace;

INSERT INTO roles (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'ADMIN'
);

INSERT INTO roles (name)
SELECT 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'USER'
);

INSERT INTO users (username, email, password, first_name, last_name, role_id)
SELECT
    'admin',
    'admin@example.com',
    '$2a$10$8vsqzc2YMM6VC4rDleq7puc9fB/bwExSnv9X6ND8FtOuNw4Il6jme',
    'Admin',
    'Principal',
    (SELECT id FROM roles WHERE name = 'ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);