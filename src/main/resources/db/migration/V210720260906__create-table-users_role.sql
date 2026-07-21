CREATE TABLE auth.users_role (
    id_users BIGINT NOT NULL,
    id_role UUID NOT NULL

    PRIMARY KEY (id_users, id_role),
    FOREIGN KEY (id_users) REFERENCES auth.users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_role) REFERENCES auth.role(id) ON DELETE CASCADE
);