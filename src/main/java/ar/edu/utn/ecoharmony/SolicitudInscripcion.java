package ar.edu.utn.ecoharmony;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Datos que identifican la actividad, el turno y las personas solicitantes. */
public record SolicitudInscripcion(
        String actividad,
        LocalDateTime inicioTurno,
        List<Participante> participantes,
        String contacto) {

    public SolicitudInscripcion {
        Objects.requireNonNull(actividad, "La actividad es obligatoria");
        Objects.requireNonNull(inicioTurno, "El inicio del turno es obligatorio");
        participantes = List.copyOf(Objects.requireNonNull(participantes, "Los participantes son obligatorios"));
        Objects.requireNonNull(contacto, "El contacto es obligatorio");
    }
}
