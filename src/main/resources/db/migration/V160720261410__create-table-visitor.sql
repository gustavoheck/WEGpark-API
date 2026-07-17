CREATE TABLE park.collaborator (
    id BIGINT PRIMARY KEY,
    company VARCHAR(255),
    cpf VARCHAR(255) UNIQUE NOT NULL,
    CONSTRAINT fk_visitor FOREIGN KEY (id) REFERENCES parkuser(id) ON DELETE CASCADE
);