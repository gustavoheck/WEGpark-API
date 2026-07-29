ALTER TABLE rh.operation
ADD CONSTRAINT fk_operated_user
FOREIGN KEY (id_operated_user) REFERENCES auth.users(id);

ALTER TABLE rh.operation
ADD CONSTRAINT fk_rh
FOREIGN KEY (id_rh) REFERENCES rh.rh(id);