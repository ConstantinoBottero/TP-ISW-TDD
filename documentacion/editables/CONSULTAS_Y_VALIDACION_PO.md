# Consultas y validación con el PO

EcoHarmony Park | Inscribirme a actividad | Versión 2.2 - 16/10/2026

Registro para completar en la reunión. No se enviaron consultas ni se registraron aprobaciones nuevas. Las respuestas ya disponibles se separan de las preguntas abiertas. El prototipo todavía espera las indicaciones concretas de rediseño del usuario.

## 1. Preguntas abiertas de dominio

| ID | Consulta para el PO | Referencias anteriores |
|---|---|---|
| Q03 | ¿Corresponde un QR por inscripción grupal o por persona? ¿Qué debe identificar y qué se espera al presentarlo? El control de acceso no se incorpora sin ampliar el alcance. | PDF P06; catálogo P017 |
| Q04 | ¿Qué ocurre si una persona intenta inscribirse otra vez en la misma actividad, fecha y turno? ¿Rechazo o recuperación de su inscripción anterior? | PDF P07; catálogo P009 |
| Q05 | ¿Qué ocurre si el mismo DNI se carga dos veces dentro de una inscripción? | PDF P08; catálogo P010 |
| Q06 | ¿Qué formatos de DNI se admiten, cómo se compara su identidad y qué tratamiento corresponde a bebés sin DNI? No se solicita pasaporte. | PDF P12; catálogo P008 |
| Q07 | ¿La edad declarada debe corresponder al día de inscripción o al día de la actividad? ¿Basta la declaración sin fecha de nacimiento? | PDF P12; catálogo P007 |
| Q08 | ¿Una aceptación del responsable alcanza para todo el grupo, incluidos menores? ¿Se requiere algún dato adicional del responsable? | PDF P10; catálogo P014 |
| Q09 | Si una persona no cumple edad, tiene datos incompletos o un conflicto de horario, ¿se rechaza toda la solicitud para que se corrija? Propuesta: evitar inscripción parcial silenciosa. El rechazo total por falta de cupos ya está confirmado. | PDF P11; catálogo P012/P013; H04 |
| Q10 | Si la inscripción quedó registrada y falla la generación del QR, ¿qué estado debe mostrar y cómo se recupera la confirmación? | PDF P15; catálogo P019 |
| Q11 | Si falla la entrega de la confirmación después del registro, ¿qué debe ver el visitante y cómo puede recuperar o solicitar el mensaje? La estrategia interna de reintento la define el equipo. | PDF P14; catálogo P018/P020 |
| Q12 | ¿Es aceptable avisar cupos insuficientes al elegir cantidad y comprobarlos nuevamente al confirmar? | Respuestas del grupo, pregunta 12 sin contestar |

Cada respuesta se registra con ID, texto acordado, fecha, persona que respondió y documentos afectados.

## 2. Decisiones técnicas del equipo

| ID | Decisión por acordar | Restricción que debe respetar |
|---|---|---|
| D01 | Zona horaria del parque y representación de fechas/instantes | El prototipo usa `America/Argentina/Buenos_Aires` para demostrar la ventana de inscripción. La decisión técnica definitiva para la futura implementación sigue pendiente. |
| D02 | Almacenamiento y conservación al reiniciar | Cupos, registros y conflictos consistentes. Aclarar al PO el comportamiento observable de reinicio si forma parte de la entrega. No elegir aún BD. |
| D03 | Contrato de reintento, idempotencia y recuperación de mensajes | Evitar consumo duplicado por reintento técnico; respetar resultado visible acordado en Q11. |
| D04 | Mecanismo de atomicidad y demostración de concurrencia | No superar capacidad ni confirmar dos actividades simultáneas para una persona. Los dobles no demuestran garantías del adaptador real. |

Siguen abiertas la selección de framework frontend, herramientas de pruebas de interfaz y organización del equipo. Java para backend y JavaScript para frontend son decisiones técnicas del equipo; Maven y JUnit se usan para el backend. Ninguna de estas decisiones técnicas se atribuye al PO.

## 3. Respuestas confirmadas: no volver a consultarlas

| Tema | Respuesta disponible | Fuente |
|---|---|---|
| Calendario | Cerrado lunes, 25/12 y 01/01 | FAQ específica 1; grupo 14 |
| Turnos | Una hora, inicios 09:00-17:00; sin inicio 18:00 | FAQ 12; grupo 4 |
| Apertura de inscripción | Desde 24 horas antes del inicio, inclusive; no martes para viernes | FAQ 23; grupo 10; confirmación del PO registrada el 14/10/2026 |
| Cierre de inscripción | Hasta 15 minutos antes del inicio, inclusive | FAQ 30; confirmación del PO registrada el 14/10/2026 |
| Simultaneidad | Una persona no puede reservar otras actividades del mismo horario | FAQ 29; grupo 18 |
| Cantidad por transacción | Cualquier cantidad que quepa en ese turno | FAQ 31; grupo 11 |
| Falta de cupos | Rechazar toda la solicitud | Grupo 5 |
| Correo | Un correo por inscripción, resumen y QR | Grupo 7/13; FAQ 28 |
| Simulación de correo | Vista representativa con adjunto; otra ventana/aplicación | FAQ 5; grupo 2 |
| Fuente del email | Campo ingresado o sesión simulada | Grupo 2 |
| Entrada al parque | Su validación queda fuera de esta US | Grupo 9 |
| Documento | Solo DNI, no pasaporte | FAQ 32 |
| Vestimenta | Palestra/Tirolesa, talles XS a XXL | FAQ 9/14; grupo 8 |
| Edad | Palestra 12+, Tirolesa 8+, Safari/Jardinería todas | FAQ 15; grupo 8 |
| Términos | Contenido distinto por actividad, a diseñar y validar | FAQ 16; grupo 6 |
| Interfaz | Mobile o web responsive mobile first; Lato y paleta definida | FAQ 3/24; grupo 1/3 |
| Logo | Diseñar y presentar para validación | FAQ 17 |

Fuentes: FAQ PO TDD EcoHarmony Park -US_inscribirme_a_actividad.pdf, pp.1-4; TP #6 ISW - G2.pdf, pp.1-3. Los números de FAQ/grupo corresponden a las preguntas de esas fuentes, no a páginas.

## 4. Agenda de validación del futuro prototipo

| ID | Aspecto a presentar | Estado actual |
|---|---|---|
| V01 | Estética, jerarquía, distribución móvil y escritorio, Lato y paleta | Pendiente de rediseño y presentación |
| V02 | Recorrido actividad, fecha/turno, personas, términos, revisión y resultado | La selección visual muestra Hoy y Mañana dentro de la ventana confirmada; pendiente de validación del resto del recorrido. |
| V03 | Textos específicos de términos de las cuatro actividades | Borradores del prototipo anterior; sin aprobación registrada |
| V04 | Logo del parque | Propuesta anterior; sin aprobación registrada |
| V05 | Correo separado, resumen, adjunto y representación del QR | Pendiente de propuesta final; cantidad QR según Q03 |
| V06 | Estados visuales: sin cupos, fecha cerrada, fuera de ventana, datos faltantes y conflicto | Pendiente; demostraciones de muestra, sin lógica de negocio |

La aprobación del diseño de pantallas, del texto de términos y de reglas de dominio se registra por separado. No tomar una aprobación estética como respuesta implícita de Q03-Q12.

## 5. Acta para completar después de la reunión

Por cada Q/V revisado, registrar: identificador; fecha; persona que valida; respuesta o cambios pedidos; estado (aceptado, requiere cambios o pendiente); evidencia o referencia; documentos afectados. No completar campos con respuestas supuestas.
