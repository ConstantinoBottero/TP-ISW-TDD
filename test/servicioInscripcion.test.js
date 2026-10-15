import { beforeEach, describe, expect, it } from "vitest";
import { crearRepositorioParque } from "../src/repositorioParque.js";
import { crearServicioInscripcion } from "../src/servicioInscripcion.js";

// Miércoles 14:00: día abierto y horario de turno.
const TURNO_MIERCOLES_14 = "2026-10-14T14:00";

describe("Inscribirme a actividad", () => {
  let repositorio;
  let servicio;

  beforeEach(() => {
    repositorio = crearRepositorioParque();
    servicio = crearServicioInscripcion({ repositorio });
  });

  it("debe registrar la inscripción con sus participantes cuando la solicitud es válida", () => {
    const participantes = [
      { nombre: "Ana Gómez", dni: "30111222", edad: 30 },
      { nombre: "Juan Pérez", dni: "28444555", edad: 34 },
    ];

    const resultado = servicio.inscribir({
      actividad: "safari",
      inicioTurno: TURNO_MIERCOLES_14,
      participantes,
    });

    expect(repositorio.obtenerInscripcion(resultado.idInscripcion)).toMatchObject({
      participantes,
    });
  });
});
