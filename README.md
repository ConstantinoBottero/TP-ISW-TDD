# TP6 - EcoHarmony Park

US: **Inscribirme a actividad**. Documentación actualizada el 16/10/2026.

La carpeta contiene la documentación revisada, el prototipo visual y la primera iteración del backend TDD para **Inscribirme a actividad**. El backend y sus pruebas están en Java; el prototipo web sigue siendo estático en HTML, CSS y JavaScript.

## Organización

- **documentacion/fuentes/**: enunciados TP2/TP6, FAQ específica, FAQ general y respuestas del grupo. Son cinco fuentes distintas, conservadas sin cambios.
- **documentacion/editables/**: cinco documentos vigentes en Markdown, incluidos los términos y condiciones; son la fuente para futuras modificaciones.
- **documentacion/pdf/**: las versiones PDF de los cuatro documentos principales, listos para compartir.
- **prototipo/**: carpeta principal para editar la interfaz, los medios y su documentación.
- **src/main/java/**: implementación Java del backend en progreso.
- **src/test/java/**: pruebas JUnit del backend.
- **pom.xml**: compilación Java 17 y dependencias de test.
- **.sites-sync/**: copia técnica del prototipo con el repositorio utilizado por Sites. Se conserva para sincronizar y publicar; editar primero en `prototipo/`.
- **../Videos/**: videos originales aportados, ubicados junto a `Tp6`, fuera de los recursos operativos de la página.
- **herramientas/**: generador de PDF, ajustado a esta estructura.

Se retiraron copias de compatibilidad, respaldos antiguos, archivos comprimidos, el informe de revisión ya incorporado a los documentos y el script de migración del catálogo. Hay una sola copia de cada documento por formato; PDF y Markdown cumplen funciones distintas.

## Documentación vigente

Nuevo borrador para revisión del PO: [Términos y condiciones por actividad](documentacion/editables/TERMINOS_Y_CONDICIONES_ACTIVIDADES_PO.md). Incluye los mismos textos que el prototipo y distingue condiciones confirmadas de propuestas pendientes de validación.

| Documento | PDF | Editable |
|---|---|---|
| Plan de clases, métodos y escenarios | [Abrir](documentacion/pdf/TP6_Plan_clases_metodos_y_escenarios.pdf) | [Modificar](documentacion/editables/PLAN_CLASES_METODOS_Y_ESCENARIOS.md) |
| Catálogo de escenarios | [Abrir](documentacion/pdf/TP6_Catalogo_pruebas.pdf) | [Modificar](documentacion/editables/CATALOGO_PRUEBAS.md) |
| Convención de estilos y calidad | [Abrir](documentacion/pdf/TP6_Convencion_estilos.pdf) | [Modificar](documentacion/editables/CONVENCION_ESTILOS.md) |
| Consultas y validación con el PO | [Abrir](documentacion/pdf/TP6_Consultas_y_validacion_PO.pdf) | [Modificar](documentacion/editables/CONSULTAS_Y_VALIDACION_PO.md) |

El plan contiene diseño orientativo y escenarios de interfaz. El catálogo desarrolla escenarios de backend. Q01-Q12 identifica consultas abiertas y D01-D04, decisiones técnicas. La primera iteración backend persiste en memoria una solicitud válida y tiene una prueba JUnit; el resto de las reglas y escenarios siguen pendientes.

## Backend y pruebas

El backend se implementa en Java 17 y las pruebas usan JUnit Jupiter. Se necesita un JDK 17 instalado. Desde la raíz, ejecutar `mvnw.cmd test` en Windows o `./mvnw test` en macOS/Linux; Maven Wrapper descarga la versión configurada automáticamente. Por ahora el repositorio es en memoria y el servicio solo registra la solicitud; no deben considerarse implementadas las reglas de edad, cupos, horarios, términos, correo o QR.

La configuración Maven está en [pom.xml](pom.xml). El plan de TDD y el registro de ciclos están en [PLAN_TDD.md](documentacion/editables/PLAN_TDD.md). El prototipo de `prototipo/dist/` conserva JavaScript porque es una maqueta de frontend independiente; no es la implementación Java del backend ni parte de los tests.

## Prototipo «Portada cinematográfica y actividades reales»

Abrir [prototipo/dist/index.html](prototipo/dist/index.html) en un navegador. No requiere instalar dependencias.

Archivos de trabajo:

- [index.html](prototipo/dist/index.html): estructura de la página.
- [styles.css](prototipo/dist/styles.css): presentación y adaptación a distintos tamaños.
- [app.js](prototipo/dist/app.js): interacciones visuales de muestra.
- [media.js](prototipo/dist/media.js): reproducción exclusiva, pausa y respaldo fotográfico para los videos locales.
- [assets](prototipo/dist/assets/): fotografías, videos seleccionados, fuentes Lato y licencia.
- [README del prototipo](prototipo/README.md): recorrido y alcance.
- [Medios y movimiento](prototipo/ARTE_Y_MOVIMIENTO.md): procedencia, créditos y comportamiento de los recursos audiovisuales.

La carpeta dist contiene los archivos editables del frontend en HTML, CSS y JavaScript, sin framework ni dependencias de instalación. El equipo adopta Java para el backend y JavaScript para el frontend; el prototipo será la base de la interfaz futura.

El recorrido incorpora una portada con video de referencia, tarjetas fotográficas, videos locales para Safari, Tirolesa, Palestra y Jardinería, cabecera compacta y pase/correo de muestra. Tirolesa y Palestra utilizan copias optimizadas de los clips aportados; los originales se conservan por separado. Los medios no afirman instalaciones ni especies confirmadas del parque. No tiene aprobación registrada del PO ni implementa las reglas del plan. La presentación permite validar estética, recorrido, escenas y textos; las consultas de dominio siguen abiertas.

La carpeta `.sites-sync/` contiene el repositorio técnico de Sites y su configuración `.openai/hosting.json`. No debe borrarse al limpiar archivos duplicados. Las carpetas `prototipo/dist/` y `.sites-sync/dist/` cumplen funciones distintas: edición y publicación. Modificar archivos locales no actualiza automáticamente el sitio publicado.

## Mantener los documentos

Editar primero el Markdown de documentacion/editables. El generador `herramientas/generar_pdfs.py` requiere Python, ReportLab y pypdf, y utiliza las fuentes Lato de prototipo/dist/assets.

Para generar todos los PDF: `python herramientas/generar_pdfs.py`.

Para regenerar solo uno: `python herramientas/generar_pdfs.py --solo plan` (alternativas: catalogo, estilos o po).

El generador escribe directamente en documentacion/pdf. No reconstruye contenido desde versiones antiguas ni crea copias adicionales.

## Ubicación de trabajo y mantenimiento

La carpeta actual es `C:/Users/jonat/Desktop/UTN/CUARTO/ISW/Nueva carpeta/Tp6`. Se movió el proyecto completo desde su ubicación anterior; no se detectaron recursos faltantes en `prototipo/dist/`.

Para continuar, editar la interfaz en `prototipo/dist/` y mantener sus nombres y rutas internas. Abrir una terminal dentro de `prototipo/` y ejecutar `node serve-local.js` para revisar en `http://127.0.0.1:8766/`. Si una herramienta todavía apunta a la antigua carpeta `ISW/Tp6`, volver a seleccionar la carpeta actual antes de usarla.

Los originales están en [Videos](../Videos/). Los recursos que realmente usa la página están en [assets](prototipo/dist/assets/). Los documentos de negocio permanecen en `documentacion/`; no moverlos dentro del prototipo.
