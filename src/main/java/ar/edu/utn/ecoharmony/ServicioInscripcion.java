package ar.edu.utn.ecoharmony;

import java.util.Objects;

/** Coordina la primera versión del caso de uso de inscripción. */
public class ServicioInscripcion {
    private static final String SOLO_LETRAS_Y_ESPACIOS = "[\\p{L} ]+";

    private final RepositorioParque repositorio;
    private final EmisorCorreo emisorCorreo;

    public ServicioInscripcion(RepositorioParque repositorio, EmisorCorreo emisorCorreo) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
        this.emisorCorreo = Objects.requireNonNull(emisorCorreo, "El emisor de correo es obligatorio");
    }

    public ResultadoInscripcion inscribir(SolicitudInscripcion solicitud) {
        validarNombres(solicitud);

        String idInscripcion = repositorio.guardarInscripcion(solicitud);
        emisorCorreo.enviar(confirmacionDe(solicitud));
        return new ResultadoInscripcion(idInscripcion);
    }

    private void validarNombres(SolicitudInscripcion solicitud) {
        for (Participante participante : solicitud.participantes()) {
            String nombre = participante.nombre();
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }
            if (!nombre.matches(SOLO_LETRAS_Y_ESPACIOS)) {
                throw new IllegalArgumentException("El nombre solo puede contener letras y espacios");
            }
        }
    }

    private MensajeConfirmacion confirmacionDe(SolicitudInscripcion solicitud) {
        return new MensajeConfirmacion(
                "ana@ejemplo.com",
                solicitud.actividad(),
                solicitud.inicioTurno(),
                solicitud.participantes());
    }
}
