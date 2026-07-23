CREATE TABLE park.visitor (
    id BIGINT PRIMARY KEY,
    company VARCHAR(255),
    cpf VARCHAR(255) UNIQUE NOT NULL,
    CONSTRAINT fk_visitor FOREIGN KEY (id) REFERENCES park.parkuser(id) ON DELETE CASCADE
);