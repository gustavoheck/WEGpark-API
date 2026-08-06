ALTER TABLE park.parkuser_vehicle
    DROP CONSTRAINT parkuser_vehicle_id_parkuser_fkey,
    DROP CONSTRAINT parkuser_vehicle_id_vehicle_fkey;

ALTER TABLE park.parkuser_vehicle
    ALTER COLUMN id_parkuser TYPE BIGINT USING id_parkuser::BIGINT,
    ALTER COLUMN id_vehicle TYPE BIGINT USING id_vehicle::BIGINT;

ALTER TABLE park.parkuser_vehicle
    ADD CONSTRAINT fk_parkuser_vehicle_parkuser
        FOREIGN KEY (id_parkuser) REFERENCES park.parkuser(id),
    ADD CONSTRAINT fk_parkuser_vehicle_vehicle
        FOREIGN KEY (id_vehicle) REFERENCES park.vehicle(id);

ALTER TABLE auth.role
    ADD CONSTRAINT uk_auth_role_role UNIQUE (role);

ALTER TABLE auth.users
    ADD CONSTRAINT uk_auth_users_email_role UNIQUE (email, id_role);

ALTER TABLE park.collaborator
    ADD CONSTRAINT uk_park_collaborator_badge_number UNIQUE (badge_number);

ALTER TABLE park.parkuser_vehicle
    ADD CONSTRAINT uk_parkuser_vehicle_user_vehicle UNIQUE (id_parkuser, id_vehicle);

ALTER TABLE rh.rh
    ADD CONSTRAINT uk_rh_rh_badge_number UNIQUE (badge_number);

ALTER TABLE park.traffic_accident
    ALTER COLUMN occurrence_date TYPE TIMESTAMP USING occurrence_date::TIMESTAMP;

CREATE INDEX idx_notification_user_time
    ON notification.notification (id_notificated_user, notification_time DESC);

CREATE INDEX idx_parkuser_vehicle_uuid_parkuser
    ON park.parkuser_vehicle (uuid_parkuser);

CREATE INDEX idx_parkuser_vehicle_id_vehicle
    ON park.parkuser_vehicle (id_vehicle);

CREATE INDEX idx_guard_occurrence_id_occurrence
    ON park.guard_occurrence (id_occurrence);

CREATE INDEX idx_vehicle_user_occurrence_id_occurrence
    ON park.vehicle_user_occurrence (id_occurrence);

CREATE INDEX idx_vehicle_association_notification_id_vehicle
    ON notification.vehicle_association_notification (id_vehicle);

CREATE INDEX idx_vehicle_association_notification_id_user
    ON notification.vehicle_association_notification (id_user_to_associate);
