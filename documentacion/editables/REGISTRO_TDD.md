# Registro de ciclos TDD

EcoHarmony Park | TP6 | Inscribirme a actividad

Evidencia de cada tanda y fase según [GUIA_DE_TRABAJO_TDD.md](GUIA_DE_TRABAJO_TDD.md). Se anota la salida real de cada ejecución; no se reconstruyen resultados.

Comando de ejecución: `./mvnw test` (Linux/macOS) o `mvnw.cmd test` (Windows).

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

Commit RED: _pendiente de confirmación del usuario._

### Fase GREEN

_Pendiente._

### Fase REFACTOR

_Pendiente._
