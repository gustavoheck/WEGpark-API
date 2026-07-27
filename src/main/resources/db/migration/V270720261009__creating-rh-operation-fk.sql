
ALTER TABLE auth.users
ADD CONSTRAINT fk_operated_user
FOREIGN KEY (id_operated_user) REFERENCES auth.users(id),

ALTER TABLE rh.operation
ADD CONSTRAINT fk_rh
FOREIGN KEY (id_rh) REFERENCES rh.rh(id)

ALTER TABLE rh.rh
ADD CONSTRAINT fk_operation
FOREIGN KEY (id_operation) REFERENCES rh.operation(id)