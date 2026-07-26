CREATE TABLE park.vehicle_user_occurrence (
    id_parkuser_vehicle BIGINT NOT NULL,
    id_occurrence BIGINT NOT NULL,

    PRIMARY KEY(id_parkuser_vehicle, id_occurrence),
    FOREIGN KEY (id_occurrence) REFERENCES park.occurrence(id),
    FOREIGN KEY (id_parkuser_vehicle) REFERENCES park.parkuser_vehicle(id)
);