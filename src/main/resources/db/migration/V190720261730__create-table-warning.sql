CREATE TABLE park.warning (
    id BIGINT UNIQUE NOT NULL,
    warning_type VARCHAR(255) NOT NULL,
    description VARCHAR(450),
    CONSTRAINT fk_warning FOREIGN KEY (id) REFERENCES park.occurrence(id) ON DELETE CASCADE
);