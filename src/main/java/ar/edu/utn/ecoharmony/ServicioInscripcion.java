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
        String idInscripcion = repositorio.guardarInscripcion(solicitud);
        return new ResultadoInscripcion(idInscripcion);
    }
}
