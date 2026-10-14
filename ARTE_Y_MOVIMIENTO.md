# Medios y movimiento — EcoHarmony Park

Este documento registra los recursos visibles del prototipo de la US **Inscribirme a actividad**. Son referencias visuales para validar con el PO; no representan instalaciones, especies ni servicios confirmados de EcoHarmony Park.

## Recursos operativos

| Uso | Archivo en `dist` | Origen y atribución |
| --- | --- | --- |
| Portada | `assets/videos/portada.mp4` y `assets/photos/portada.jpg` | Copia local optimizada del clip de referencia de BIOPARC Valencia. El original se conserva en `referencias/bioparc-fondo.mp4`. Fuente: [BIOPARC Valencia](https://bioparcvalencia.es/en/). |
| Safari | `assets/videos/safari.mp4` y `assets/photos/safari.jpg` | Clip aportado para el TP: `Videos/safari/227950_medium.mp4`. |
| Palestra | `assets/videos/palestra.mp4` y `assets/photos/palestra.jpg` | Clip aportado para el TP: `Videos/palestra/6013503_Climb_Outdoor_1280x720.mp4`. |
| Jardinería | `assets/videos/jardineria.mp4` y `assets/photos/jardineria.jpg` | Clip aportado para el TP: `Videos/Jardineria/6014775_Flower_Garden_1280x720.mp4`. |
| Tirolesa | `assets/videos/tirolesa.mp4` y `assets/photos/tirolesa-video.jpg` | Clip generado con Gemini y aportado por el usuario: `Videos/Tirolesa/gemini_generated_video_5f65a8ff.mp4`. Duración aproximada: 10 segundos; resolución 1280 × 720. |

Los cinco posters de video se generan desde el segundo 1 de sus respectivos clips para que coincidan con el inicio de la reproducción y evitar un salto visual. La copia operativa de portada se reencodificó en H.264 sin audio para respetar el límite de tamaño del alojamiento; el original continúa intacto en `referencias/bioparc-fondo.mp4`. Los originales aportados continúan en `../Videos/` sin modificaciones. La fotografía anterior de Tirolesa, de [Ditrich Oleg en Pexels](https://www.pexels.com/photo/man-on-a-zipline-10674304/) bajo la [licencia de Pexels](https://www.pexels.com/license/), se conserva en `assets/photos/tirolesa.jpg` como recurso anterior; la interfaz utiliza ahora el fotograma del nuevo clip.

## Comportamiento

- Portada y actividad elegida usan video local, silenciado, en bucle y dentro de la página. El resto de las tarjetas conserva una fotografía.
- Durante todos los pasos de inscripción, una capa fija de hojas translúcidas se balancea en los márgenes de la pantalla. Permanece al desplazarse y no se recorta dentro de las tarjetas. En celular reduce su presencia; se oculta en portada y correo. Respeta pausa manual, foco en campos, modales, pestaña oculta y movimiento reducido.
- Sombras desenfocadas de follaje acompañan los márgenes en ciclos de 29 y 32 segundos, al 6 % de opacidad (4 % en celular), dejando libre el centro. Comparten la pausa del ambiente y quedan estáticas con movimiento reducido.
- Solo puede reproducirse un video: la portada o el video de la actividad seleccionada. Al cambiar de etapa, actividad, pestaña, modal o foco en un campo se pausa.
- El poster permanece visible durante la carga, ante un error o si el navegador no permite la reproducción automática. El recorrido nunca depende del video.
- El control del encabezado permite pausar/reanudar. Con `prefers-reduced-motion`, los videos se sustituyen por fotografías y se eliminan las transiciones decorativas.
- Las transiciones de interfaz duran entre 160 y 280 ms y no retrasan acciones de teclado ni navegación.

## Límites de la muestra

No hay ilustraciones generadas, Canvas, motor 3D, video de fondo remoto ni edición de marcas. Las fotos y los videos se cargan desde los recursos locales del prototipo. El PO debe validar la estética, la relación entre cada medio y la experiencia, y los textos de referencia antes de usar cualquier recurso en una publicación real.
