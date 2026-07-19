CREATE TABLE park.illegal_parking (
    id BIGINT UNIQUE NOT NULL,
    parking_space_type VARCHAR(255) NOT NULL,
    description VARCHAR(450) NOT NULL,
    CONSTRAINT fk_illegal_parking FOREIGN KEY (id) REFERENCES park.occurrence(id) ON DELETE CASCADE
);