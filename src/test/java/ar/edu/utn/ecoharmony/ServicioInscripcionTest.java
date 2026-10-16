package ar.edu.utn.ecoharmony;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioInscripcionTest {
    private static final String ACTIVIDAD_SAFARI = "safari";
    private static final LocalDateTime TURNO_MIERCOLES_14 = LocalDateTime.of(2026, 10, 14, 14, 0);
    private static final String CONTACTO_ANA = "ana@ejemplo.com";
    private static final Participante ANA = new Participante("Ana Gómez", "30111222", 30);
    private static final Participante JUAN = new Participante("Juan Pérez", "28444555", 34);
    private static final Participante PEDRO = new Participante("Pedro Sosa", "31222333", 12);
    private static final Participante LUCIA = new Participante("Lucía Ríos", "45333444", 9);
    private static final Participante MARTA = new Participante("Marta Díaz", "27555666", 61);

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
        var solicitud = solicitudValida(ANA, JUAN);

        var resultado = servicio.inscribir(solicitud);

        assertNotNull(resultado.idInscripcion());
        assertEquals(solicitud, repositorio.obtenerInscripcion(resultado.idInscripcion()).orElseThrow());
    }

    @Test
    void debeEnviarUnCorreoAlContactoCuandoLaInscripcionQuedaRegistrada() {
        servicio.inscribir(solicitudValida(ANA));

        assertEquals(1, emisorCorreo.mensajes().size(), "debe enviarse un correo por inscripción");
        assertEquals(CONTACTO_ANA, emisorCorreo.mensajes().get(0).destinatario());
    }

    @Test
    void debeResumirActividadTurnoYParticipantesCuandoSeEnviaLaConfirmacion() {
        servicio.inscribir(solicitudValida(ANA, JUAN));

        var mensaje = emisorCorreo.mensajes().get(0);

        assertEquals(ACTIVIDAD_SAFARI, mensaje.actividad());
        assertEquals(TURNO_MIERCOLES_14, mensaje.inicioTurno());
        assertEquals(List.of(ANA, JUAN), mensaje.participantes());
    }

    @Test
    void debeEnviarUnSoloCorreoCuandoLaInscripcionEsGrupal() {
        servicio.inscribir(solicitudValida(ANA, JUAN, PEDRO, LUCIA, MARTA));

        assertEquals(1, emisorCorreo.mensajes().size(), "un correo por inscripción, no uno por participante");
        assertEquals(
                List.of(ANA, JUAN, PEDRO, LUCIA, MARTA),
                emisorCorreo.mensajes().get(0).participantes());
    }

    @Test
    void debeAceptarInscripcionCuandoNombreEsValido() {
        // Preparación: un nombre con tildes y más de dos palabras.
        Participante participante = new Participante("María José Pérez Gómez", "33222333", 25);
        SolicitudInscripcion solicitud = solicitudValida(participante);

        // Ejecución: se solicita la inscripción con ese nombre.
        ResultadoInscripcion resultado = servicio.inscribir(solicitud);

        // Resultado esperado: se acepta y conserva el nombre completo.
        assertNotNull(resultado.idInscripcion());
        assertEquals(solicitud, repositorio.obtenerInscripcion(resultado.idInscripcion()).orElseThrow());
    }

    @Test
    void debeRechazarInscripcionCuandoNombreEstaVacioOEnBlanco() {
        // Preparación: un participante con nombre vacío y los demás datos válidos.
        Participante participante = new Participante("", "33222333", 25);
        SolicitudInscripcion solicitud = new SolicitudInscripcion(
                ACTIVIDAD_SAFARI,
                TURNO_MIERCOLES_14,
                List.of(participante),
                CONTACTO_ANA);

        // Ejecución y resultado esperado: se rechaza la inscripción.
        assertThrows(IllegalArgumentException.class, () -> servicio.inscribir(solicitud));
    }

    private SolicitudInscripcion solicitudValida(Participante... participantes) {
        return new SolicitudInscripcion(
                ACTIVIDAD_SAFARI,
                TURNO_MIERCOLES_14,
                List.of(participantes),
                CONTACTO_ANA);
    }
}
