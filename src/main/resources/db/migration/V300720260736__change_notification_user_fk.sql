ALTER TABLE notification.notification
    DROP CONSTRAINT notification_id_notificated_user_fkey,
    ADD CONSTRAINT fk_notification_notificated_auth_user
        FOREIGN KEY (id_notificated_user)
        REFERENCES auth.users(id);
