CREATE TABLE rh.historic (
    id_rh BIGINT NOT NULL,
    id_operation BIGINT NOT NULL,
    date_hour TIMESTAMP NOT NULL,

    PRIMARY KEY(id_rh, id_operation),
    FOREIGN KEY (id_rh) REFERENCES rh.rh(id),
    FOREIGN KEY (id_operation) REFERENCES rh.operation(id)
);