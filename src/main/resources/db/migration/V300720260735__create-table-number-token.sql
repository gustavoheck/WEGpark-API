CREATE TABLE auth.number_token (
    token UUID DEFAULT uuidv7() PRIMARY KEY,
    digits VARCHAR (55) NOT NULL,
    tries INTEGER NOT NULL,
    used BOOL NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    id_target_user BIGINT NOT NULL,
    FOREIGN KEY (id_target_user) REFERENCES auth.users(id)
);