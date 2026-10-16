# Registro de ciclos TDD

EcoHarmony Park | TP6 | Inscribirme a actividad

Evidencia de cada tanda y fase según [GUIA_DE_TRABAJO_TDD.md](GUIA_DE_TRABAJO_TDD.md). Se anota la salida real de cada ejecución; no se reconstruyen resultados.

Comando de ejecución: `./mvnw test` (Linux/macOS) o `mvnw.cmd test` (Windows).

## Tanda A — Confirmación por correo

**Seam:** S1 `ServicioInscripcion.inscribir(solicitud)`
**IDs del plan:** E04, E01, G11, E06

Se trabaja en iteraciones de un test cada una. El verde de cada iteración usa el código más chico que la haga pasar, incluidos valores fijos; el test siguiente rompe ese valor fijo y obliga a generalizar. Es triangulación: así cada iteración deja un refactor real en lugar de llegar a la solución final en el primer verde.

Secuencia prevista y valor fijo que elimina cada iteración:

| Iteración | Caso | Valor fijo que elimina |
|---|---|---|
| A1 | Se envía un correo al contacto (E04) | Ninguno; introduce destinatario fijo. |
| A2 | El resumen nombra actividad, turno y participantes (E01) | El cuerpo fijo del mensaje. |
| A3 | Un grupo de cinco recibe un solo correo (G11) | Previsiblemente guarda, no rojo. |
| A4 | Dos inscripciones conservan sus propios datos (E06) | El destinatario fijo. |

Contacto: se agregó el campo `contacto` a `SolicitudInscripcion`, siguiendo «contacto resuelto» del plan de clases y el campo «Email de contacto» del prototipo, que es único por inscripción y no por participante.

### Iteración A1 — RED

| Test | Resultado |
|---|---|
| `debeEnviarUnCorreoAlContactoCuandoLaInscripcionQuedaRegistrada` | FALLA — `debe enviarse un correo por inscripción ==> expected: <1> but was: <0>` |

```
[ERROR] Tests run: 5, Failures: 1, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

Conteos: 5 ejecutados, 1 fallo de aserción, 0 errores. Los cuatro tests previos siguen en verde.

Guardas: ninguna.

Cambios:

- `MensajeConfirmacion` (nuevo): por ahora solo `destinatario`. Los demás campos aparecerán cuando un test los exija.
- `EmisorCorreo` (nuevo): límite de entrega, con `enviar(MensajeConfirmacion)`.
- `EmisorCorreoSimulado` (nuevo): bandeja recuperable mediante `mensajes()`. Es la implementación vigente del límite, no un doble de prueba.
- `SolicitudInscripcion`: nuevo componente `contacto`, obligatorio.
- `ServicioInscripcion`: recibe el emisor por constructor y todavía no lo usa, para que el rojo sea de aserción y no de compilación.
- `ServicioInscripcionTest`: el test previo se adapta a la nueva firma del record; se agrega el test de la iteración.

Commit RED: `80f9934`.

### Iteración A1 — GREEN

```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in GeneradorQRTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in ServicioInscripcionTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Conteos: 5 ejecutados, 0 fallos, 0 errores.

Cambio: una línea en `ServicioInscripcion.inscribir`, que tras guardar llama a `emisorCorreo.enviar` con un `MensajeConfirmacion` cuyo destinatario es la constante literal `"ana@ejemplo.com"`.

El destinatario fijo es deliberado. Es el valor más chico que hace pasar el test y no se generaliza todavía, porque ningún caso lo exige: con un solo contacto en la suite, leerlo de la solicitud sería anticipar la iteración A4. Esa iteración introduce un segundo contacto y obliga a eliminarlo.

Commit GREEN: _pendiente de confirmación del usuario._

## Tanda B1 — Generación del QR

**Seam:** S3 `GeneradorQR.generar(contenido)`
**IDs del plan:** E05
**Casos elegidos por el usuario:** que el QR se genere bien y se pueda decodificar; que sirva como adjunto del correo; que rechace contenido vacío.

Decisión de diseño registrada: ante contenido vacío se lanza `IllegalArgumentException`. No surge de una respuesta del PO; la propuso el asistente y la aceptó el equipo. Si el PO define otro resultado, se revisa.

Contexto: Q03 quedó respondido en cuanto a cantidad (un QR por inscripción, compartido por el grupo familiar, enviado a un solo correo). Falta definir **qué debe identificar** el QR; esta tanda no lo necesita, porque S3 solo comprueba que el contenido recibido se recupere al decodificar.

### Fase RED

| Test | Resultado |
|---|---|
| `debeDecodificarAlContenidoOriginalElCodigoGenerado` | FALLA — `expected: <INSCRIPCION-1> but was: <null>` |
| `debeGenerarImagenPngValidaParaAdjuntarAlCorreo` | FALLA — `los bytes generados no son una imagen PNG legible ==> expected: not <null>` |
| `debeRechazarContenidoVacioCuandoNoHayNadaQueCodificar` | FALLA — `Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.` |

```
[ERROR] Tests run: 4, Failures: 3, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

Conteos: 4 ejecutados, 3 fallos, 0 errores, 0 omitidos. Los tres fallos son de aserción (`Failures`), no de compilación ni de excepción (`Errors: 0`), como exige la guía.

Guardas: ninguna. Los tres tests nuevos fallan.

Sin regresión: `ServicioInscripcionTest` sigue en verde (1 ejecutado, 0 fallos).

Cambios:

- `pom.xml`: dependencias ZXing `core` y `javase` 3.5.3, con property `zxing.version`.
- `src/main/java/ar/edu/utn/ecoharmony/GeneradorQR.java` (nuevo): firma `byte[] generar(String)` devolviendo `new byte[0]`. Código mínimo para compilar; no implementa la regla.
- `src/test/java/ar/edu/utn/ecoharmony/GeneradorQRTest.java` (nuevo): los tres tests y dos auxiliares privados de decodificación que devuelven `null` ante contenido ilegible, para que el rojo sea de aserción.

Commit RED: `de32a89`.

### Fase GREEN

```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in GeneradorQRTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- in ServicioInscripcionTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Conteos: 4 ejecutados, 0 fallos, 0 errores. La suite previa sigue en verde.

Cambios: solo `src/main/java/ar/edu/utn/ecoharmony/GeneradorQR.java`. `generar(String)` codifica el contenido con `QRCodeWriter` y lo escribe como PNG con `MatrixToImageWriter`; antes rechaza el contenido vacío con `IllegalArgumentException`. Los fallos de codificación o escritura se propagan como `IllegalStateException`.

Decisiones de implementación, por si el equipo quiere revisarlas:

- El rechazo cubre `null` y cadenas en blanco, no solo `""` como pedía el test. Es robustez del borde, no una regla de negocio anticipada.
- Lado de 300 px: ZXing exige un tamaño y ningún requisito lo fija. Constante con nombre para cambiarlo en un solo lugar.
- `IllegalStateException` ante fallo de ZXing es provisional. El resultado visible cuando falla el QR depende de Q10, todavía abierto.

Commit GREEN: `b70aa12`.

### Fase REFACTOR

Sin cambios. Se revisaron `GeneradorQR` y `GeneradorQRTest` y no se encontró una mejora que justifique modificar código en verde. Siguiendo la guía, no se fabrica un refactor ni un commit vacío.

Candidatos evaluados y descartados:

| Candidato | Motivo del descarte |
|---|---|
| Extraer un método de validación del contenido | Tres líneas; la indirección no reduce complejidad. |
| `QRCodeWriter` como campo de instancia | Es sin estado y su creación es barata; no mejora legibilidad ni rendimiento observable. |
| Sustituir `@BeforeEach` por un campo `final` en el test | Ahorra cuatro líneas, pero `ServicioInscripcionTest` usa `@BeforeEach`; la inconsistencia entre los dos tests cuesta más que el ahorro. |
| Acotar el `catch (Exception)` del auxiliar de decodificación | El auxiliar ya devuelve `null` ante cualquier fallo; acotarlo no cambia comportamiento ni claridad. |
| Deduplicar la llamada a `generar` entre dos tests | Mover el paso *Act* a un auxiliar empeora la estructura AAA de cada test. |

Pendiente menor, no bloqueante: las líneas 51 y 65 de `GeneradorQRTest` conservan espacios sobrantes de una edición previa. Es limpieza cosmética; se resuelve junto al próximo cambio del archivo.

La suite quedó en verde antes y después de la revisión: 4 ejecutados, 0 fallos, 0 errores.

### Cierre de la tanda

Resultado: `GeneradorQR` genera un QR real en PNG, decodificable al contenido original y adjuntable al correo. Queda disponible para la tanda de confirmación y correo, que lo usará sin dobles.

Lo que esta tanda no resuelve: qué contenido identifica al QR (mitad pendiente de Q03) y qué ve el visitante si la generación falla (Q10).
