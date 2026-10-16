# Plan de TDD

EcoHarmony Park | TP6 | Inscribirme a actividad

Versión 0.2 - 16/10/2026 (primera iteración implementada)

Plan de los ciclos Red-Green-Refactor para implementar la US. No contiene código ni tests ejecutados. Se apoya en el [plan de clases y escenarios](PLAN_CLASES_METODOS_Y_ESCENARIOS.md) (IDs A-H), la [convención de estilos](CONVENCION_ESTILOS.md) y las [consultas al PO](CONSULTAS_Y_VALIDACION_PO.md) (Q01-Q12, D01-D04).

## 1. Seams acordados

Un seam es la interfaz pública donde se observa el comportamiento sin entrar en la implementación. Los tests se escriben solo en estos tres.

| Seam | Qué atrapa | Qué no atrapa | Escenarios |
|---|---|---|---|
| S1 `ServicioInscripcion.inscribir(solicitud)` | Las seis pruebas de usuario del enunciado; cupos, edades, talles, términos, datos de participantes, calendario, ventana de 24 h / 15 min, simultaneidad y el correo que genera cada inscripción. | Atomicidad y concurrencia del almacenamiento real (D04); que el QR se pueda decodificar; la interfaz. | A04-A07, A09, B01-B05, B07-B15, C, D01-D08, D10-D12, E01, E02, E04, E06, E07, G01-G12, G14, H01-H03 |
| S2 `ServicioConsultaActividades` | Catálogo de las cuatro actividades con su configuración, agenda de nueve turnos por día abierto, cupos disponibles, días sin agenda y términos por actividad. | La inscripción; cómo la UI muestra cada estado. | A01-A03, A10, A11, G13 |
| S3 `GeneradorQR.generar(contenido)` | Que el QR producido se decodifica y devuelve el contenido original. | Qué identifica el QR y cuántos van por inscripción (Q03). | E05 (decodificación) |

**Clases que no se testean directamente.** `Actividad` (validarEdad, validarTalle), `Participante`, `Turno`, `PoliticaTurnos` y `ValidadorInscripcion` se ejercitan a través de S1. Así un refactor que las cree, las mueva o las fusione no rompe ningún test, y no se convierte cada auxiliar en API pública (plan §4).

**Fuera de estos seams por ahora:**

- D09, H05 y la parte concurrente de D10: necesitan el adaptador real de almacenamiento (D02, D04).
- A08, B06 y la cantidad 1,5 de C04: son datos externos con tipo o valor imposible para el dominio, y corresponden al límite de deserialización, que no existe hasta elegir la capa HTTP.
- F01-F13: la interfaz JavaScript, con framework sin elegir.
- E03, B16 y H04: dependen de Q03 y Q09 (ver §4).

## 2. Dobles y datos de prueba

Dobles solo en los límites del sistema. No se mockea nada que el grupo controle: reglas, entidades y repositorio son los reales.

| Límite | Doble | Motivo |
|---|---|---|
| Tiempo | `java.time.Clock` fijo (`Clock.fixed`), con la zona `America/Argentina/Buenos_Aires` como constante con nombre (D01). | La ventana y el calendario dependen de «ahora». `Clock` de la JDK cumple el rol de `Reloj` del plan sin crear una interfaz propia. |
| Almacenamiento | Repositorio en memoria. | D02 sigue abierto: es la implementación vigente, no un mock. Los tests no leen su estado interno; los cupos se observan inscribiendo de nuevo o consultando S2. |
| Correo | `EmisorCorreoSimulado` con bandeja recuperable. | Límite externo, y la FAQ pide una vista simulada. «Se envió» se comprueba en la bandeja. |

**Datos base** (plan §6): reloj miércoles 14/10/2026 10:00 (-03:00); turnos de las 14:00 de Safari, Jardinería, Palestra y Tirolesa, habilitados y sin ocupar (capacidades 8, 12, 12 y 10). DNIs, nombres y correos ficticios.

**Una causa por test.** El código de test tiene un armador de solicitud válida y cada test cambia una sola cosa; así una prueba negativa solo puede fallar por la causa que nombra. Las variantes (edades, talles, días cerrados) van con `@ParameterizedTest`, una causa por ejecución. Para probar otro día se mueve también el reloj, de modo que el turno caiga dentro de la ventana y el rechazo sea por el motivo buscado.

## 3. Dinámica de trabajo

La dinámica para trabajar con asistentes está detallada en [GUIA_DE_TRABAJO_TDD.md](GUIA_DE_TRABAJO_TDD.md). Para este equipo, esa guía sustituye la regla anterior de un solo test por ciclo.

1. **Selección.** El usuario elige los casos. Se trabaja en tandas pequeñas, normalmente de hasta cinco casos relacionados, en un seam acordado.
2. **RED.** Se escriben solo los tests de la tanda. Se permite código mínimo para compilar, pero no implementar la regla. El rojo debe ser un fallo de aserción por el comportamiento esperado; los casos que ya pasan se registran como guardas.
3. **Commit RED.** El usuario revisa y hace el commit. El asistente no prepara commits ni modifica el historial.
4. **GREEN.** Después de la indicación del usuario, se implementa lo mínimo para que pase la tanda y la suite previa.
5. **Commit GREEN.** El usuario hace el commit después de revisar el resultado.
6. **REFACTOR.** Tras la indicación del usuario, se mejora el diseño sin alterar comportamiento ni añadir casos. La suite debe pasar antes y después. Si no hay una mejora real, no se fabrica una.
7. **Commit REFACTOR.** El usuario hace el commit y confirma el cierre de la tanda antes de indicar los siguientes casos.
8. **Registro.** Se anotan los casos, seam, IDs, comando y resultado real de cada fase, las guardas y los hashes de commit confirmados por el usuario. No se reporta una fase como realizada si falta su evidencia.

**Herramientas.** Java 17, Maven Wrapper 3.9.16 y JUnit Jupiter 5.14.2. Ejecutar mvnw.cmd test en Windows o ./mvnw test en macOS/Linux. ZXing se incorporará al llegar a S3, cuando corresponda implementar el QR real.

## 4. Consultas que frenan tests

Estos tests se escriben recién con la respuesta del PO. Hasta entonces no se fija un comportamiento supuesto.

| Consulta | Tests que espera |
|---|---|
| Q03 | Cantidad y contenido del QR (E03). |
| Q04 | Reinscribir a la misma persona en la misma actividad y turno. |
| Q05 | Mismo DNI dos veces dentro de una inscripción. |
| Q06 | Formato y comparación de DNI; bebés sin DNI. |
| Q07 | Edad al día de inscripción o al de la actividad. |
| Q09 | Rechazo total o parcial cuando una sola persona falla (B16, H04). |
| Q10 / Q11 | Resultado visible si fallan el QR o el correo después del registro. |

## Pendiente

- **Paso 1 completado:** se migró la primera prueba de servicio/repositorio a Java + JUnit. Comprueba que una solicitud válida queda recuperable con todos sus participantes. La implementación actual solo guarda y devuelve un identificador; aún no aplica reglas de negocio.
- Paso 2: ciclos de S1 para las seis pruebas del enunciado.
- Paso 3: ciclos de S1 para el resto de las reglas.
- Paso 4: ciclos de S2.
- Paso 5: ciclos de S3 y del correo en S1.
- Paso 6: puntos de refactor y trazabilidad.
