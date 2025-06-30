CREATE TRIGGER IF NOT EXISTS trg_restapla
AFTER INSERT ON reserva
BEGIN
  UPDATE actividad
  SET plazasDisponibles = plazasDisponibles - 1
  WHERE idActividad = NEW.idActividad;
END

CREATE TRIGGER IF NOT EXISTS trg_sumapla
AFTER DELETE ON reserva
BEGIN
  UPDATE actividad
  SET plazasDisponibles = plazasDisponibles + 1
  WHERE idActividad = OLD.idActividad;
END;



