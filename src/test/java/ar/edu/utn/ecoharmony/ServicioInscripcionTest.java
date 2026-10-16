package ar.edu.utn.ecoharmony;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioInscripcionTest {
    private static final LocalDateTime TURNO_MIERCOLES_14 = LocalDateTime.of(2026, 10, 14, 14, 0);
    private static final String CONTACTO_ANA = "ana@ejemplo.com";

    private RepositorioParqueEnMemoria repositorio;
    private EmisorCorreoSimulado emisorCorreo;
    private ServicioInscripcion servicio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioParqueEnMemoria();
        emisorCorreo = new EmisorCorreoSimulado();
        servicio = new ServicioInscripcion(repositorio, emisorCorreo);
    }

    @Test
    void debeRegistrarInscripcionConParticipantesCuandoSolicitudEsValida() {
        var participantes = List.of(
                new Participante("Ana Gómez", "30111222", 30),
                new Participante("Juan Pérez", "28444555", 34));
        var solicitud = new SolicitudInscripcion(
                "safari",
                TURNO_MIERCOLES_14,
                participantes,
                CONTACTO_ANA);

        var resultado = servicio.inscribir(solicitud);

        assertNotNull(resultado.idInscripcion());
        assertEquals(solicitud, repositorio.obtenerInscripcion(resultado.idInscripcion()).orElseThrow());
    }

    @Test
    void debeEnviarUnCorreoAlContactoCuandoLaInscripcionQuedaRegistrada() {
        var solicitud = new SolicitudInscripcion(
                "safari",
                TURNO_MIERCOLES_14,
                List.of(new Participante("Ana Gómez", "30111222", 30)),
                CONTACTO_ANA);

        servicio.inscribir(solicitud);

        assertEquals(1, emisorCorreo.mensajes().size(), "debe enviarse un correo por inscripción");
        assertEquals(CONTACTO_ANA, emisorCorreo.mensajes().get(0).destinatario());
    }
}
