CREATE TABLE notification.notification (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid UUID DEFAULT uuidv7() UNIQUE NOT NULL,
    id_notificated_user BIGINT NOT NULL,
    message VARCHAR(350) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_notificated_user) REFERENCES park.parkuser(id)
);