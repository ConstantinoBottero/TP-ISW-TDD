# EcoHarmony Park — portada cinematográfica y actividades reales

Prototipo visual para validar con el PO la US **Inscribirme a actividad**. Es una interfaz estática en HTML, CSS y JavaScript: no hay backend, reglas de negocio, reservas, almacenamiento, envío de correos ni pruebas automatizadas.

## Abrir el prototipo

Abrir [dist/index.html](dist/index.html) en un navegador moderno o ejecutar un servidor local:

```powershell
node serve-local.js
```

El servidor solo escucha en `127.0.0.1:8766` y permite revisar los MP4 locales. No requiere instalación ni compilación.

La versión para compartir con el PO está alojada en [EcoHarmony Park · Inscripciones](https://ecoharmony-park-inscripciones.jonife002.chatgpt.site).

La carpeta `dist` contiene el prototipo editable:

- `index.html`: portada y estructura del recorrido.
- `styles.css`: Lato, paleta del PO, composición responsive y estado visual.
- `app.js`: navegación, datos de ejemplo en memoria, modal, resumen y correo de muestra.
- `terms.js`: términos formales de las cuatro actividades, borrador v2.1 para el PO; su copia de revisión está en `../documentacion/editables/TERMINOS_Y_CONDICIONES_ACTIVIDADES_PO.md`.
- `media.js`: reproducción exclusiva, posters, pausa y respaldo fotográfico.
- `assets/photos` y `assets/videos`: solo los medios usados por la interfaz.

Los clips originales aportados se preservan en `../../Videos/`. La atribución y la decisión de medios se detallan en [ARTE_Y_MOVIMIENTO.md](ARTE_Y_MOVIMIENTO.md). Los documentos de negocio y del TP no se modificaron.

## Recorrido de muestra

1. La portada a pantalla completa presenta un video de referencia de BIOPARC Valencia y permite entrar de inmediato con **Inscribirme a actividad**.
2. La selección muestra Safari, Tirolesa, Palestra y Jardinería. La actividad elegida reproduce su video; las demás muestran un fotograma de respaldo. Tirolesa utiliza el clip aportado `../../Videos/Tirolesa/gemini_generated_video_5f65a8ff.mp4`.
3. Fecha y horario muestra los turnos de Hoy y Mañana que se encuentran entre 24 horas y 15 minutos antes de su inicio, usando la hora de Argentina. Participantes y términos usan datos ilustrativos en memoria; la cabecera mantiene la foto de la experiencia y en celular la acción principal queda fija abajo.
4. El resumen permite revisar los datos y elegir **Confirmar inscripción**. La confirmación posterior presenta el pase, sello y QR, y ofrece **Ver confirmación por correo** como acción secundaria.

Para la presentación al PO, todos los textos visibles y accesibles describen la experiencia definitiva, sin etiquetas de prueba. La confirmación es un cambio visual en memoria: no se incorporaron registro, QR válido ni envío de correo. El carácter de prototipo y los créditos de los recursos permanecen documentados aquí y en los documentos del equipo.

**Volver al inicio** conserva lo escrito y volver a ingresar abre la selección. Recargar reinicia el prototipo.

## Identidad y accesibilidad

La interfaz conserva Lato local y los cinco colores definidos por el PO: `#386641`, `#6a994e`, `#a7c957`, `#f2e8cf` y `#bc4749`. Los medios mantienen sus colores naturales.

Hay foco visible, enlace para saltar al contenido, controles operables con teclado, modal nativo con Escape y retorno de foco. La reproducción se suspende al ocultar la pestaña, abrir un modal o enfocar un campo; con movimiento reducido se muestran fotos estáticas.

## Alcance de la validación del PO

La portada, fotografías, escenas en video, textos, recorrido, términos y vistas de muestra deben ser validados por el PO. Los medios son referencias visuales y no prueban que EcoHarmony posea esas instalaciones o especies. La lógica de negocio del backend Java y las pruebas TDD quedan fuera de este prototipo. El frontend JavaScript del prototipo será la base para continuar la interfaz.

## Carpeta de edición y publicación

Esta carpeta es la copia principal para editar la interfaz. `../.sites-sync/` conserva el repositorio técnico para publicar en Sites; no es una segunda propuesta de diseño. Los originales de video están en `../../Videos/`, junto a la carpeta `Tp6`. Mantener la estructura interna de `dist/` permite mover el proyecto completo sin romper sus recursos.
