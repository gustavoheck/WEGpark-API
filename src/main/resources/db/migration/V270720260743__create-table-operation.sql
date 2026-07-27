CREATE TABLE rh.operation (
    id BIGINT PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    operation VARCHAR(255) NOT NULL,
    date_hour TIMESTAMP NOT NULL,
    id_operated_user BIGINT NOT NULL,
    id_rh BIGINT NOT NULL
);