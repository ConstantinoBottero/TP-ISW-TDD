package ar.edu.utn.ecoharmony;

import java.util.Objects;

/** Coordina la primera versión del caso de uso de inscripción. */
public class ServicioInscripcion {
    private final RepositorioParque repositorio;
    private final EmisorCorreo emisorCorreo;

    public ServicioInscripcion(RepositorioParque repositorio, EmisorCorreo emisorCorreo) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
        this.emisorCorreo = Objects.requireNonNull(emisorCorreo, "El emisor de correo es obligatorio");
    }

    public ResultadoInscripcion inscribir(SolicitudInscripcion solicitud) {
        validarNombresObligatorios(solicitud);

        String idInscripcion = repositorio.guardarInscripcion(solicitud);
        emisorCorreo.enviar(confirmacionDe(solicitud));
        return new ResultadoInscripcion(idInscripcion);
    }

    private void validarNombresObligatorios(SolicitudInscripcion solicitud) {
        for (Participante participante : solicitud.participantes()) {
            String nombre = participante.nombre();
            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
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
