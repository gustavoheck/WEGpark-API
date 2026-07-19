CREATE TABLE park.traffic_accident (
    id BIGINT UNIQUE NOT NULL,
    occurrence_date DATE NOT NULL,
    victim_name VARCHAR(255) NOT NULL,
    responsible_boss_name VARCHAR(255),
    responsible_factory VARCHAR(255),
    responsible_section VARCHAR(255),
    traffic_occurrence_type VARCHAR(50) NOT NULL,
    guard_testimony VARCHAR(450) NOT NULL,
    victim_testimony VARCHAR(450) NOT NULL,
    CONSTRAINT fk_traffic_accident FOREIGN KEY (id) REFERENCES park.occurrence(id) ON DELETE CASCADE
);