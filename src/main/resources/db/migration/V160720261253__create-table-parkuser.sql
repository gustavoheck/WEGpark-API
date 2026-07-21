CREATE TABLE park.parkuser (
    id BIGINT PRIMARY KEY,
    uuid UUID DEFAULT uuidv7() UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    user_type VARCHAR(50) NOT NULL
);