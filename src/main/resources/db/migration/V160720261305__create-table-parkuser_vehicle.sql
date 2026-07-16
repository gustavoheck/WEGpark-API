CREATE TABLE park.parkuser_vehicle (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vehicle_owner BOOLEAN NOT NULL,
    ativo BOOLEAN NOT NULL,
    id_parkuser INT NOT NULL,
    id_vehicle INT NOT NULL,

    FOREIGN KEY (id_parkuser) REFERENCES park.parkuser(id),
    FOREIGN KEY (id_vehicle) REFERENCES park.vehicle(id)
);