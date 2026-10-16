package ar.edu.utn.ecoharmony;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Almacenamiento en memoria para la primera iteración del TP. */
public class RepositorioParqueEnMemoria implements RepositorioParque {
    private final Map<String, SolicitudInscripcion> inscripciones = new HashMap<>();
    private final AtomicLong siguienteId = new AtomicLong(1);

    @Override
    public String guardarInscripcion(SolicitudInscripcion solicitud) {
        String id = Long.toString(siguienteId.getAndIncrement());
        inscripciones.put(id, solicitud);
        return id;
    }

    @Override
    public Optional<SolicitudInscripcion> obtenerInscripcion(String id) {
        return Optional.ofNullable(inscripciones.get(id));
    }
}
