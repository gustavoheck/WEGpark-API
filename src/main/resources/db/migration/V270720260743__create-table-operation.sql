CREATE TABLE rh.operation (
    id BIGINT PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    operation VARCHAR(255) NOT NULL,
    uuid_operated_user UUID NOT NULL,
    FOREIGN KEY (uuid_operated_user) REFERENCES auth.users(uuid)
);