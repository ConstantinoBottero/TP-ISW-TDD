/** Estado del parque en memoria, mientras no se elija almacenamiento (D02). */
export function crearRepositorioParque() {
  const inscripciones = new Map();

  return {
    guardarInscripcion(inscripcion) {
      const id = String(inscripciones.size + 1);
      inscripciones.set(id, inscripcion);
      return id;
    },

    obtenerInscripcion(id) {
      return inscripciones.get(id);
    },
  };
}
