CREATE TABLE auth.auth_token (
    token UUID DEFAULT uuidv7() PRIMARY KEY,
    token_type VARCHAR (55) NOT NULL,
    used BOOL NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    id_target_user BIGINT NOT NULL,
    FOREIGN KEY (id_target_user) REFERENCES auth.users(id)
);