-- V10__motivo_id_auto_increment.sql
-- Hacer AUTO_INCREMENT la PK de motivo sin cambiar el tipo (TINYINT),
-- soltando y recreando la FK que la referencia.

START TRANSACTION;

-- 1) Soltar la FK que referencia motivo(id) desde motivo_servicio
--    Usa el nombre exacto que te mostró el error: fk_ms_motivo
ALTER TABLE motivo_servicio
  DROP FOREIGN KEY fk_ms_motivo;

-- 2) Marcar la PK como AUTO_INCREMENT (mantengo TINYINT para no tocar tipos en la relación)
ALTER TABLE motivo
  MODIFY COLUMN id TINYINT NOT NULL AUTO_INCREMENT;

-- 3) Recrear la FK
ALTER TABLE motivo_servicio
  ADD CONSTRAINT fk_ms_motivo
  FOREIGN KEY (motivo_id) REFERENCES motivo(id)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT;

COMMIT;
