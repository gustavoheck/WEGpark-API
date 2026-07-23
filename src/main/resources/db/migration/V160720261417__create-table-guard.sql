CREATE TABLE park.guard (
    id BIGINT PRIMARY KEY,
    boss VARCHAR(255) NOT NULL,
    CONSTRAINT fk_guard FOREIGN KEY (id) REFERENCES park.collaborator(id) ON DELETE CASCADE
);