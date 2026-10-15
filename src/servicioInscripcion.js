export function crearServicioInscripcion({ repositorio }) {
  return {
    inscribir(solicitud) {
      const idInscripcion = repositorio.guardarInscripcion(solicitud);
      return { idInscripcion };
    },
  };
}
