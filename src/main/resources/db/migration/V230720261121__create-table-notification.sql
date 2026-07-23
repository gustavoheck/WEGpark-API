CREATE TABLE notification.notificiation (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid UUID DEFAULT uuidv7() UNIQUE NOT NULL,
    id_notificated_user BIGINT NOT NULL,
    message VARCHAR(350),
    FOREIGN KEY (id_notificated_user) REFERENCES park.parkuser(id)
);