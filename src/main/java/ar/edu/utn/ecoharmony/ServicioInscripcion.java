package ar.edu.utn.ecoharmony;

import java.util.Objects;

/** Coordina la primera versión del caso de uso de inscripción. */
public class ServicioInscripcion {
    private final RepositorioParque repositorio;

    public ServicioInscripcion(RepositorioParque repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
    }

    public ResultadoInscripcion inscribir(SolicitudInscripcion solicitud) {
        String idInscripcion = repositorio.guardarInscripcion(solicitud);
        return new ResultadoInscripcion(idInscripcion);
    }
}
