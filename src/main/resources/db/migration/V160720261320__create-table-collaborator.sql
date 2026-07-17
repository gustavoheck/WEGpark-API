CREATE TABLE park.collaborator (
    id BIGINT PRIMARY KEY,
    badgeNumber VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    CONSTRAINT fk_collaborator FOREIGN KEY (id) REFERENCES parkuser(id) ON DELETE CASCADE
);