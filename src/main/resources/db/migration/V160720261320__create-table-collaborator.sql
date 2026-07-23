CREATE TABLE park.collaborator (
    id BIGINT PRIMARY KEY,
    badge_number VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    CONSTRAINT fk_collaborator FOREIGN KEY (id) REFERENCES park.parkuser(id) ON DELETE CASCADE
);