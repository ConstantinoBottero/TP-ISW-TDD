package ar.edu.utn.ecoharmony;

import java.util.Optional;

/** Límite de almacenamiento de las inscripciones. */
public interface RepositorioParque {
    String guardarInscripcion(SolicitudInscripcion solicitud);

    Optional<SolicitudInscripcion> obtenerInscripcion(String id);
}
