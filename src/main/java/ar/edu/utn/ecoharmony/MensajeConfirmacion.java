package ar.edu.utn.ecoharmony;

import java.time.LocalDateTime;
import java.util.List;

public record MensajeConfirmacion(
        String destinatario,
        String actividad,
        LocalDateTime inicioTurno,
        List<Participante> participantes) {
}
