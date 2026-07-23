CREATE TABLE park.guard_occurrence (
    id_guard BIGINT NOT NULL,
    id_occurrence BIGINT NOT NULL,

    PRIMARY KEY(id_guard, id_occurrence),
    FOREIGN KEY (id_guard) REFERENCES park.guard(id),
    FOREIGN KEY (id_occurrence) REFERENCES park.occurrence(id)
);