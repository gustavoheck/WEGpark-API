ALTER TABLE notification.notification
    ADD COLUMN entity_notification_type VARCHAR(50);

UPDATE notification.notification AS notification
SET entity_notification_type = 'VEHICLE_ASSOCIATION'
WHERE EXISTS (
    SELECT 1
    FROM notification.vehicle_association_notification AS association
    WHERE association.id = notification.id
);

UPDATE notification.notification
SET notification_type = 'FIVE_OCCURRENCE'
WHERE notification_type = 'Notification'
  AND message LIKE 'Identificamos 5 ou mais registros de ocorr%';

UPDATE notification.notification
SET notification_type = 'OCCURRENCE'
WHERE notification_type = 'Notification';

ALTER TABLE notification.notification
    ADD CONSTRAINT ck_notification_entity_type
        CHECK (
            entity_notification_type IS NULL
            OR entity_notification_type = 'VEHICLE_ASSOCIATION'
        );
