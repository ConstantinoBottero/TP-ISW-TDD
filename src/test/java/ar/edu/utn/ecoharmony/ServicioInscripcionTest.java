package ar.edu.utn.ecoharmony;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioInscripcionTest {
    private RepositorioParqueEnMemoria repositorio;
    private ServicioInscripcion servicio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioParqueEnMemoria();
        servicio = new ServicioInscripcion(repositorio);
    }

    @Test
    void debeRegistrarInscripcionConParticipantesCuandoSolicitudEsValida() {
        var participantes = List.of(
                new Participante("Ana Gómez", "30111222", 30),
                new Participante("Juan Pérez", "28444555", 34));
        var solicitud = new SolicitudInscripcion(
                "safari",
                LocalDateTime.of(2026, 10, 14, 14, 0),
                participantes);

        var resultado = servicio.inscribir(solicitud);

        assertNotNull(resultado.idInscripcion());
        assertEquals(solicitud, repositorio.obtenerInscripcion(resultado.idInscripcion()).orElseThrow());
    }
}
