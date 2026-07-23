CREATE TABLE park.vehicle_occurrence (
    id_vehicle BIGINT NOT NULL,
    id_occurrence BIGINT NOT NULL,
    id_parkuser_vehicle BIGINT NOT NULL,

    PRIMARY KEY(id_vehicle, id_occurrence),
    FOREIGN KEY (id_vehicle) REFERENCES park.vehicle(id),
    FOREIGN KEY (id_occurrence) REFERENCES park.occurrence(id),
    FOREIGN KEY (id_user_vehicle) REFERENCES park.parkuser_vehicle(id)
);