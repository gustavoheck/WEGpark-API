CREATE TABLE park.collaborator (
    company VARCHAR(255),
    cpf VARCHAR(255) UNIQUE NOT NULL
) INHERITS (park.parkuser);