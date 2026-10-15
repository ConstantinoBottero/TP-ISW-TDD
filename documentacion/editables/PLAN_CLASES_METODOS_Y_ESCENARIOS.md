# Plan de clases, métodos y escenarios

EcoHarmony Park | TP6 | Inscribirme a actividad

Versión 2.0 - 14/10/2026

Documento de planificación actualizado. No contiene código implementado ni pruebas ejecutadas. Los nombres de clases y métodos son orientativos; las firmas, el framework y el almacenamiento se decidirán al iniciar el desarrollo. Java y TypeScript son los lenguajes acordados.

## 1. Alcance y fuentes

Historia: como visitante quiero inscribirme a una actividad para reservar mi lugar en ella.

Se planifican selección de actividad y turno, cantidad y datos de participantes, validación de reglas, registro con consumo de cupos, términos específicos y un correo de confirmación con QR. La interfaz será móvil o web responsive con enfoque mobile first.

Quedan fuera compra y validación de entradas, pagos, administración de actividades, autenticación real y lectura de QR para controlar el acceso, salvo ampliación expresa del alcance.

Fuentes: [S1] ISW_TP_ENUN_6.pdf, pp.1 y 3; [S2] ISW_TP_ENUN_2.pdf, p.2; [S3] FAQ PO TDD EcoHarmony Park -US_inscribirme_a_actividad.pdf, pp.1-4; [S4] TP #6 ISW - G2.pdf, pp.1-3; [S5] convención de estilos actualizada del equipo. Las FAQ y respuestas se conservan sin alterar. La propuesta anterior es un antecedente, no una respuesta del PO.

Origen: R = requisito confirmado; T = decisión o comprobación técnica propuesta; P = depende de una consulta abierta. Los límites exactos de 24 h y 15 min siguen pendientes; no se adoptan silenciosamente como inclusivos.

## 2. Reglas consolidadas

| Actividad | Capacidad por turno | Edad mínima | Vestimenta |
|---|---:|---|---|
| Safari | 8 | Todas las edades | No requerida |
| Jardinería | 12 | Todas las edades | No requerida |
| Palestra | 12 | 12 inclusive | XS, S, M, L, XL, XXL |
| Tirolesa | 10 | 8 inclusive | XS, S, M, L, XL, XXL |

- Turnos de una hora: inicios a las 09:00, 10:00, 11:00, 12:00, 13:00, 14:00, 15:00, 16:00 y 17:00. No hay inicio a las 18:00. [S3:12]
- Parque cerrado lunes, 25/12 y 01/01. [S3:1]
- Inscripción dentro de las 24 h previas al turno; cierre 15 min antes. Anticipación máxima, no mínima. [S3:23,30]
- Cantidad positiva y datos de cada persona: nombre, DNI, edad y talle si corresponde. Cantidad y lista deben coincidir. [S1]
- Si no alcanza el cupo, rechazar todo el grupo. No existe límite adicional de diez participantes. [S4:5; S3:31]
- Una persona no puede reservar otra actividad del mismo horario. Comparar también la fecha. [S3:29]
- Aceptar términos específicos de la actividad; un cambio de actividad invalida la aceptación previa. [S1; coherencia técnica]
- Un correo por inscripción, con resumen y QR. La simulación debe mostrar un correo representativo, con adjunto, en otra ventana o aplicación. [S3:5,28; S4:2,7,13]
- Obtener el email de un campo o de una sesión simulada son alternativas permitidas. Un correo por inscripción no define la cantidad de QR. [S4:2,13]

La elección de DNI como clave de persona, su normalización y excepciones documentales están pendientes. No imponer ocho dígitos, edad máxima, altura, UUID obligatorio ni un máximo de diez.

## 3. Modelo de datos propuesto

| Elemento | Datos y responsabilidad |
|---|---|
| Actividad | Identificador, nombre, capacidad, edad mínima opcional, requiereTalle y talles permitidos. Configuración por actividad, sin exigir una subclase por cada una. |
| Participante | Nombre, DNI como texto, edad declarada y talle opcional. No incorporar fecha de nacimiento o tutor sin definición del PO. |
| Turno | Identificador, actividad, fecha e inicio, duración, capacidad y estado de habilitación. Cupos disponibles se obtienen del estado registrado. |
| TerminosActividad | Actividad y contenido, indicando si es borrador o está aprobado. |
| SolicitudInscripcion | Actividad, turno, cantidad, participantes, aceptación vinculada a la actividad y contacto resuelto. |
| Inscripcion | Identidad propia, actividad/turno confirmados, participantes, aceptación, contacto y fecha/hora de registro. |
| MensajeConfirmacion | Destinatario, asunto, resumen y adjunto(s) QR, derivados del registro confirmado. |
| ResultadoInscripcion | Confirmación o rechazo con error identificable. No comunicar éxito completo si falta una parte de la confirmación. |

Las estructuras simples se verifican a través del comportamiento. No planificar pruebas dedicadas a getters, setters o constructores triviales.

## 4. Responsabilidades y operaciones

| Componente | Operaciones orientativas | Responsabilidad |
|---|---|---|
| Actividad | validarEdad; validarTalle | Restricciones de esa actividad. |
| Participante | validarDatosObligatorios | Presencia y coherencia básica; el formato externo se valida en el límite de entrada. |
| Turno | cuposDisponibles; validarCantidad | Estado de cupos sin modificarlo durante la validación. |
| PoliticaTurnos | validarDisponibilidad(turno, ahora) | Calendario, horario, habilitación y ventana de inscripción. |
| ValidadorInscripcion | validar(solicitud, actividad, turno, ahora) | Componer reglas sin duplicar validaciones ni producir efectos. |
| ServicioConsultaActividades | listarActividades; consultarTurnos; obtenerTerminos | Catálogo, agenda y términos específicos. Separar agenda operativa de disponibilidad actual. |
| ServicioInscripcion | inscribir(solicitud) | Coordinar consulta, validación, conflictos, registro y confirmación. Entrada pública del caso de uso. |
| RepositorioParque | confirmar; obtenerInscripcion; obtenerTurno; consultarTurnos; obtenerActividad; listarActividades; obtenerTerminos | Acceso al estado y registro indivisible. No obliga a una BD concreta. |
| Consulta de inscripciones | existeInscripcionEnHorario(dni, inicio), o equivalente | Detectar registros previos de la misma persona en el mismo día y horario. Puede integrarse al repositorio. Identidad documental pendiente. |
| Reloj | ahora | Tiempo controlable; no probar un getter trivial de forma aislada. |
| GeneradorConfirmacion | generarMensaje(inscripcion) | Un mensaje por inscripción; cantidad y contenido de QR pendientes. |
| GeneradorQR | generar(contenido) | QR real decodificable en la futura implementación. Biblioteca por elegir. |
| EmisorCorreo | enviar(mensaje) | Límite de entrega de la confirmación. |
| EmisorCorreoSimulado | enviar; listarMensajes; obtenerMensaje | Bandeja recuperable y vista de correo separada, sin SMTP real. |

Los métodos auxiliares de validación pueden extraerse durante refactor. No convertir cada auxiliar en API pública. La distribución de estas responsabilidades puede simplificarse cuando se desarrollen los primeros comportamientos.

### Secuencia del caso de uso

1. Resolver actividad, turno y hora; validar datos y reglas conocidas.
2. Consultar conflictos personales utilizando inscripciones previas, no solo la lista actual.
3. Si hay rechazo previo al registro, terminar sin inscripción, descuento de cupos ni confirmación nueva.
4. Confirmar registro y consumo de lugares de forma indivisible, comprobando cupos actuales y ausencia de conflictos en ese mismo límite.
5. Construir un mensaje desde la inscripción confirmada y entregarlo al emisor simulado.

Comprobar disponibilidad antes y guardar después mediante operaciones independientes no evita condiciones de carrera. Cupos y conflictos deben preservarse al confirmar. Si falla QR/correo después del registro, el resultado y la recuperación dependen de Q10/Q11; no se presume rollback de un envío externo.

## 5. Interfaces de usuario planificadas

| Superficie | Comportamiento a presentar |
|---|---|
| Selección de actividad | Cuatro actividades, edad mínima y necesidad de vestimenta. |
| Fecha y turno | Fecha, horario y cupos; días cerrados no seleccionables. Diferenciar agotado, cerrado, fuera de ventana y no habilitado. |
| Participantes | Cantidad y datos de cada persona; talle solo cuando corresponde; errores junto al campo. |
| Términos | Modal con contenido diferente por actividad y aceptación desde el modal. Textos pendientes de aprobación. |
| Revisión | Actividad, fecha, hora, personas y contacto; permitir corregir datos. |
| Resultado | Éxito únicamente tras confirmación del servicio en la aplicación futura; rechazo comprensible y recuperable. |
| Correo simulado | Otra ventana/aplicación con destinatario, resumen y adjunto QR. |

Los nombres ControladorInscripcion, FormularioParticipantes, ModalTerminos, VistaResultadoInscripcion y VisorCorreoSimulado describen roles; no obligan a usar clases ni un framework específico.

El prototipo para el PO solo representará este recorrido y estados de muestra. No implementará reglas, almacenamiento, generación real de QR ni envío de correo. Su rediseño se hará con las indicaciones concretas del usuario.

## 6. Datos base y criterios de planificación

Reloj fijo de ejemplo: 14/10/2026 10:00, offset -03:00. Turnos principales de Safari, Palestra, Tirolesa y Jardinería: 14/10/2026 14:00, duración 60 min, habilitados, cero ocupados y capacidades 8/12/10/12. Son miércoles y están dentro de la ventana. El offset es un dato del fixture; la zona de operación es D01.

Para otro día, mover también el reloj de esa ejecución. Para calendario cerrado, usar una hora situada dentro de la ventana para aislar la causa. Ejemplos: domingo 18/10 10:00 para lunes 19/10 09:00; 24/12 12:00 para 25/12 11:00; 31/12/2026 12:00 para 01/01/2027 11:00.

RECHAZO_SIN_EFECTOS: error identificable, mismas inscripciones/participantes/cupos y ningún correo de éxito nuevo antes de confirmar. EXITO_REGISTRADO: identidad propia, datos recuperables, consumo exacto de n lugares y un correo por inscripción. El número de QR depende de Q03.

En Java, tipos de entrada incorrectos corresponden a deserialización/validación de datos externos. No inventar llamadas que el compilador impide. Las variantes se parametrizan y cada ejecución modifica una sola causa.

## 7. Escenarios conservados y actualizados

Los identificadores A-F se conservan para mantener trazabilidad. G/H agregan reglas faltantes. Son escenarios documentados; no tests escritos o ejecutados. R/T/P se refieren al origen, no al resultado de ejecución.

### A. Actividades y turnos

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| A01 | R | Consultar catálogo | Las cuatro actividades seleccionables. |
| A02 | R | Consultar configuración | Cupos, edad y vestimenta según tabla. |
| A03 | R | Consultar agenda operativa de un día abierto | Nueve inicios 09:00-17:00, duración 60 min; distinguir cuáles admiten inscripción ahora. |
| A04 | R | Intentar inicio a las 18:00 | Rechazo por horario no disponible. |
| A05 | R | Turno no habilitado | Rechazo sin efectos. |
| A06 | R | Lunes, 25/12 o 01/01, por separado | Rechazo por parque cerrado, reloj dentro de ventana. |
| A07 | T | Turno inexistente | Error de turno; sin registro. |
| A08 | T | Actividad inexistente | Error de actividad; sin registro. |
| A09 | T | Actividad y turno incompatibles | Rechazo por incoherencia. |
| A10 | T | Fecha/actividad sin agenda publicada | Lista vacía o estado sin disponibilidad; no inventar turnos. |
| A11 | R | Consultar términos por actividad | Cada una obtiene su texto; identificar borrador/aprobado. |

### B. Participantes, edades y vestimenta

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| B01 | R | Nombre ausente/vacío | Error de nombre obligatorio. |
| B02 | T | Nombre solo espacios | Error de nombre obligatorio. |
| B03 | R | DNI ausente/vacío | Error obligatorio; excepciones pendientes Q06. |
| B04 | R | Edad ausente | Error de edad obligatoria. |
| B05 | T | Edad -1 | Error de edad inválida. |
| B06 | T | Edad externa no numérica | Error controlado en el límite de entrada. |
| B07 | R | Palestra, 11 años | Rechazo por edad. |
| B08 | R | Palestra, 12 años | Cumple edad. |
| B09 | R | Tirolesa, 7 años | Rechazo por edad. |
| B10 | R | Tirolesa, 8 años | Cumple edad. |
| B11 | R | Safari/Jardinería con 0, 8, 12 y 70 años | Sin restricción por edad; parametrizar. |
| B12 | R | Palestra/Tirolesa sin talle | Error de talle obligatorio. |
| B13 | R | Cada talle XS-XXL en ambas actividades | Cumple vestimenta. |
| B14 | R | Talle XXXL u otro fuera del conjunto | Error de talle no permitido. |
| B15 | R | Safari/Jardinería sin talle | Ausencia válida. |
| B16 | R/P | Solo tercera persona inválida | Detectar su error; tratamiento del grupo según Q09. |

### C. Solicitud y términos

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| C01 | R | Sin actividad | Error de selección. |
| C02 | R | Sin turno | Error de selección. |
| C03 | R | Sin cantidad | Error de cantidad obligatoria. |
| C04 | T | Cantidad 0, -1 o 1,5 | Error: entero positivo. |
| C05 | T | Cantidad declarada distinta de lista | Error de consistencia. |
| C06 | R | Sin aceptación | Rechazo por términos. |
| C07 | T | Aceptación de otra actividad | Rechazo; no reutilizar aceptación. |
| C08 | R | Solicitud completa, 4 h antes del turno | Sin errores para reglas confirmadas; contratos pendientes explícitos. |
| C09 | T | Validar sin confirmar | Sin mutaciones, QR ni correo. |

### D. Registro y cupos

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| D01 | R | Inscripción individual válida | Registro con datos y hora del reloj; iniciar confirmación. |
| D02 | R | Safari vacío: inscribir 3 | Registrar 3; quedan 5. |
| D03 | R | Quedan 2: solicitar 2 | Registrar ambos; quedan 0. |
| D04 | R | Quedan 2: solicitar 3 | Rechazo total; siguen 2 libres. |
| D05 | R | Quedan 0: solicitar 1 | Rechazo sin efectos. |
| D06 | R | Solicitud sin términos | Sin registro, cupos ni correo. |
| D07 | T | Registrar 2 y luego 3 en Safari | Quedan 3; no doble descuento. |
| D08 | T | Registrar en turno A y consultar B | B conserva sus lugares. |
| D09 | T | Fallo de almacenamiento durante registro | Sin escritura parcial ni consumo; comprobar adaptador real. |
| D10 | T | Consulta obsoleta: otra reserva consume cupo | Rechazar usando disponibilidad actual. |
| D11 | T | Dos inscripciones válidas | Identidades distintas. |
| D12 | T | Recuperar inscripción por identidad | Datos completos sin mezcla con otra. |

### E. Confirmación, QR y correo

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| E01 | R | Construir resumen del registro | Actividad, día, hora y participantes correctos; un mensaje. |
| E02 | R | QR de prueba devuelto por generador | Imagen incorporada al mensaje y adjunto. |
| E03 | P | Cantidad/payload QR de la inscripción | Resultado pendiente Q03; no fijar aún QR grupal/individual. |
| E04 | R | Confirmar solicitud válida | Un correo al contacto resuelto con resumen y QR. |
| E05 | T | QR real y bandeja simulada | Dividir en dos escenarios: decodificación y recuperación íntegra. |
| E06 | T | Mensajes de dos inscripciones | Cada uno conserva sus propios datos. |
| E07 | T | Registro falla antes de confirmar | Ningún correo de éxito de esa solicitud. |

### F. Interfaz y aceptación

| ID | Origen | Escenario | Resultado esperado en la aplicación futura |
|---|---|---|---|
| F01 | R | Recorrido mobile first | Controles utilizables, sin desbordes que bloqueen acciones. |
| F02 | R | Cambiar actividad | Reglas/campos correspondientes; talle según actividad. |
| F03 | T | Aceptar Safari y cambiar a Palestra | Aceptación anterior invalidada. |
| F04 | R | Cargar tres participantes | Datos de las tres personas presentes. |
| F05 | R | Abrir términos | Contenido específico y aceptación desde modal. |
| F06 | T | Enviar sin términos evitando UI | Backend rechaza; validación visual no reemplaza dominio. |
| F07 | R | Recorrido válido completo | Registro, cupos y correo separado con QR. |
| F08 | R | Turno agotado | Rechazo visible, sin falsa confirmación. |
| F09 | R | Safari/Jardinería sin talle | Inscripción válida con lo demás correcto. |
| F10 | R | Día cerrado o turno no habilitado | Casos separados; rechazo sin registro. |
| F11 | R | No aceptar términos | Impedir inscripción. |
| F12 | R | Omitir talle requerido | Error visible y rechazo. |
| F13 | R | Abrir correo de inscripción | Otra ventana/aplicación con resumen y adjunto QR. |

### G. Tiempo, calendario y confirmación: agregados

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| G01 | R | 24 h + 1 s antes | Rechazar: inscripción no abierta. |
| G02 | R | 23 h 59 min 59 s antes | Cumple ventana. |
| G03 | R | 15 min + 1 s antes | Cumple ventana. |
| G04 | R | 15 min - 1 s antes | Rechazar: inscripción cerrada. |
| G05 | R | Turno pasado o iniciado | Rechazar. |
| G06 | R | Lunes con reloj dentro de ventana | Parque cerrado. |
| G07 | R | 25/12 con reloj dentro de ventana | Parque cerrado. |
| G08 | R | 01/01 con reloj dentro de ventana | Parque cerrado. |
| G09 | T | Mismo instante con offsets diferentes | Misma decisión temporal bajo contrato por instantes; D01. |
| G10 | R | Grupo válido de 11 y 12 en Palestra/Jardinería | Sin máximo global de diez. |
| G11 | R | Inscripción grupal | Un correo por inscripción, no uno por persona. |
| G12 | R | Correo simulado recuperado | Resumen y archivo adjunto QR disponibles. |
| G13 | T | Consultar agenda y disponibilidad actual | Diferenciar horario operativo de ventana vigente. |
| G14 | T | Día distinto para aislar cupos | Ajustar reloj a cada ejecución; no aceptar por error un turno fuera de ventana. |

### H. Agenda personal: agregados

| ID | Origen | Escenario | Resultado esperado |
|---|---|---|---|
| H01 | R | Misma persona, otra actividad, mismo día/hora | Rechazar segunda inscripción. |
| H02 | R | Misma persona, otro horario no simultáneo | Permitir si cumple lo demás. |
| H03 | R | Misma hora de otro día | No rechazar por simultaneidad; ajustar reloj. |
| H04 | R/P | Una persona del grupo tiene conflicto | Detectar conflicto; tratamiento grupal Q09. |
| H05 | T | Dos solicitudes concurrentes, misma persona/hora | Como máximo una confirmada; comprobar límite de registro real. |

## 8. Consultas y decisiones pendientes

El documento CONSULTAS_Y_VALIDACION_PO.md mantiene Q01-Q12 y D01-D04. No duplicar preguntas resueltas.

| Referencia anterior | Estado actualizado |
|---|---|
| P01 | Q01: inclusión de 24 h exactas. |
| P02 / P03 | Resueltos; G01/G02 corrigen los resultados. |
| P04 | Calendario, anticipación máxima y cupos por turno confirmados; representación técnica abierta. |
| P05 | Un correo y ambas fuentes de contacto permitidas; elección UI pendiente del equipo. |
| P06 | Q03: cantidad, contenido y uso del QR. |
| P07 / P08 | Q04/Q05: duplicados de reserva y dentro del grupo. |
| P09 | Resuelto; H01. |
| P10 / P11 / P12 | Q08/Q09/Q06-Q07. |
| P13 | D02: almacenamiento y conservación al reiniciar. |
| P14 / P15 | Q11/Q10: fallos posteriores al registro. |
| P16 | D04: demostración de concurrencia; preservar cupos/conflictos no queda opcional. |
| P17, nuevo | Q02: inclusión de 15 min exactos. |

## 9. Trazabilidad y desarrollo futuro

| Prueba de usuario del TP6 | Escenarios del plan | Catálogo detallado |
|---|---|---|
| Inscripción válida con confirmación | F07; D01; E01/E02/E04 | I001/I002/I031/I033/I046 |
| Sin cupos | F08; D04/D05 | I004/I005 |
| Sin talle no requerido | F09; B15 | I006 |
| Parque cerrado o actividad no disponible | F10; A05/A06 | I011/I012/I041 |
| Sin aceptar términos | F11; C06/D06 | I008 |
| Sin talle requerido | F12; B12 | I007 |

Secuencia orientativa: inscripción mínima válida; cupos; datos/edades/talles; términos; tiempo/calendario; agenda personal; registro real; QR/correo; UI e integración del recorrido. Cada comportamiento recorre Red, Green y Refactor conforme a [S1]. No escribir todas las clases o toda la suite primero.

Los límites de observación planificados son servicio público, repositorio real e interfaz del visitante. Los casos de deserialización, QR y correo se verifican en sus límites respectivos. No fijar porcentajes de cobertura como reemplazo de requisitos ni afirmar atomicidad mediante mocks.

El PDF anterior contenía 68 escenarios principales. Se conservan sus IDs y se agregan 14 G y 5 H: 87 filas principales, varias parametrizadas y algunas condicionadas a Q03/Q09. Este número no equivale a cantidad de tests ejecutables o ejecutados; E05 se divide al implementar. No se impone un reparto de cinco subgrupos ni un número de integrantes.

## 10. Control de versión

Versión 2.0: corrige ventana temporal, incorpora cierre y calendario, resuelve simultaneidad y distribución de correo, agrega consulta de registros previos, mejora trazabilidad y distingue decisiones técnicas de preguntas del PO. No incorpora reglas documentales ni de menores no confirmadas. Fuentes originales en documentacion/fuentes; documentación vigente en documentacion/editables y documentacion/pdf. Se retiraron copias redundantes y respaldos obsoletos al organizar la carpeta de trabajo.
