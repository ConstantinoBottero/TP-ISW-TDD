# Medios y movimiento — EcoHarmony Park

Este documento registra los recursos visibles del prototipo de la US **Inscribirme a actividad**. Son referencias visuales para validar con el PO; no representan instalaciones, especies ni servicios confirmados de EcoHarmony Park.

## Recursos operativos

| Uso | Archivo en `dist` | Origen y atribución |
| --- | --- | --- |
| Portada | `assets/videos/portada.mp4` y `assets/photos/portada.jpg` | Copia local optimizada del clip de referencia de BIOPARC Valencia. El original se conserva en `referencias/bioparc-fondo.mp4`. Fuente: [BIOPARC Valencia](https://bioparcvalencia.es/en/). |
| Safari | `assets/videos/safari.mp4` y `assets/photos/safari.jpg` | Clip aportado para el TP: `Videos/safari/227950_medium.mp4`. |
| Palestra | `assets/videos/palestra.mp4` y `assets/photos/palestra.jpg` | Clip aportado para el TP: `Videos/palestra/6013503_Climb_Outdoor_1280x720.mp4`. |
| Jardinería | `assets/videos/jardineria.mp4` y `assets/photos/jardineria.jpg` | Clip aportado para el TP: `Videos/Jardineria/6014775_Flower_Garden_1280x720.mp4`. |
| Tirolesa | `assets/videos/tirolesa-590f8c2d8334.mp4` y `assets/photos/tirolesa-video.jpg` | Clip generado con Gemini y aportado por el usuario: `Videos/Tirolesa/gemini_generated_video_5f65a8ff.mp4`. Duración aproximada: 10 segundos; resolución 1280 × 720. |

Los cinco posters de video se generan desde el segundo 1 de sus respectivos clips para que coincidan con el inicio de la reproducción y evitar un salto visual. La copia operativa de portada se reencodificó en H.264 sin audio para respetar el límite de tamaño del alojamiento; el original continúa intacto en `referencias/bioparc-fondo.mp4`. Los originales aportados continúan en `../../Videos/` sin modificaciones. La fotografía anterior de Tirolesa, de [Ditrich Oleg en Pexels](https://www.pexels.com/photo/man-on-a-zipline-10674304/) bajo la [licencia de Pexels](https://www.pexels.com/license/), se conserva en `assets/photos/tirolesa.jpg` como recurso anterior; la interfaz utiliza ahora el fotograma del nuevo clip.

## Comportamiento

- Portada y actividad elegida usan video local, silenciado, en bucle y dentro de la página. El resto de las tarjetas conserva una fotografía.
- Durante todos los pasos de inscripción, una capa fija de hojas translúcidas se balancea en los márgenes de la pantalla. Permanece al desplazarse y no se recorta dentro de las tarjetas. En celular reduce su presencia; se oculta en portada y correo. Respeta pausa manual, foco en campos, modales, pestaña oculta y movimiento reducido.
- Sombras desenfocadas de follaje acompañan los márgenes en ciclos de 29 y 32 segundos, al 6 % de opacidad (4 % en celular), dejando libre el centro. Comparten la pausa del ambiente y quedan estáticas con movimiento reducido.
- Solo puede reproducirse un video: la portada o el video de la actividad seleccionada. Al cambiar de etapa, actividad, pestaña, modal o foco en un campo se pausa.
- El poster permanece visible durante la carga, ante un error o si el navegador no permite la reproducción automática. El recorrido nunca depende del video.
- Con `prefers-reduced-motion`, los videos se sustituyen por fotografías y se eliminan las transiciones decorativas.
- Las transiciones de interfaz duran entre 160 y 280 ms y no retrasan acciones de teclado ni navegación.

## Límites de la muestra

No hay ilustraciones generadas, Canvas, motor 3D, video de fondo remoto ni edición de marcas. Las fotos y los videos se cargan desde los recursos locales del prototipo. El PO debe validar la estética, la relación entre cada medio y la experiencia, y los textos de referencia antes de usar cualquier recurso en una publicación real.

## Optimización de Tirolesa — 14/10/2026

La copia operativa se optimizó en H.264, conservando 1280 × 720, 24 fps, duración y encuadre. Se eliminó el audio y se aplicó CRF 19, preset slow, techo de 4 Mbps, buffer de 4 Mb, fotograma clave cada segundo y faststart. El peso pasó de 13.214.126 a 5.075.683 bytes (61,6 % menos). La comparación SSIM con el clip previo dio 0,975; se revisó visualmente un fotograma del segundo 3 y se verificó la decodificación completa.

El nombre del archivo incluye un hash de contenido para que la interfaz solicite la nueva versión y evite reutilizar el archivo anterior del caché. El poster y las dimensiones de los componentes se conservan. Los originales de `../../Videos/` permanecen intactos. La latencia del CDN se mide por separado; esta compresión no garantiza un tiempo de inicio fijo para todas las conexiones.

## Optimización de Palestra — 14/10/2026

La copia operativa se reencodificó en H.264 con CRF 20 y preset slow, conservando 1280 × 720, 50 fps, 22,20 segundos, encuadre y señalización de color BT.709. Se retiraron audio y timecode, se incorporaron fotogramas clave cada segundo y se mantuvieron los metadatos de inicio rápido al comienzo del MP4. El peso pasó de 14.333.826 a 9.514.916 bytes (33,6 % menos). La comparación SSIM dio 0,9887 y se revisó visualmente un fotograma del segundo 5. La recompresión introduce diferencias pequeñas; SSIM no equivale a un porcentaje de calidad percibida.

El recurso conserva su ruta `palestra.mp4` para mantener compatibilidad con páginas ya abiertas. La configuración añade `?v=0b7c52cdf7b2` al video y `?v=media-0b7c52cdf7b2` a `app.js` para evitar el caché de versiones anteriores sin duplicar archivos. Se mantienen poster y tamaños de la interfaz, y el original de `../../Videos/palestra/` no se modifica. La decodificación completa terminó sin errores.
