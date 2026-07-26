ALTER TABLE park.occurrence
ADD CONSTRAINT fk_guard
FOREIGN KEY (id_guard) REFERENCES park.guard(id);