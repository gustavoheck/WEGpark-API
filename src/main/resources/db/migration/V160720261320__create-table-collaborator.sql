CREATE TABLE park.collaborator (
    badgeNumber VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
) INHERITS (park.parkuser);