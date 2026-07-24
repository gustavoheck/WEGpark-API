CREATE TABLE notification.vehicle_association_notification (
    id BIGINT UNIQUE NOT NULL,
    id_vehicle BIGINT NOT NULL,
    id_user_to_associate BIGINT NOT NULL,
    FOREIGN KEY (id_vehicle) REFERENCES park.vehicle(id),
    FOREIGN KEY (id_user_to_associate) REFERENCES park.parkuser(id),
    CONSTRAINT fk_vehicle_association_notification FOREIGN KEY (id) REFERENCES notification.notificiation(id) ON DELETE CASCADE
);