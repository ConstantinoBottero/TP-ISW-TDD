# Catálogo de escenarios unitarios y de integración

EcoHarmony Park | TP6 | Inscribirme a actividad | Versión 2.1 - 16/10/2026

**Planificación documental. Este catálogo describe escenarios aún pendientes; la primera prueba JUnit del backend está implementada por separado en `src/test/java/`.**

Esta versión actualiza el catálogo anterior y conserva sus identificadores U001-U095 e I001-I039. Contiene pruebas de presencia, tipo, consistencia y reglas del dominio, junto con integración de registro, cupos, QR y correo. No contiene pruebas del frontend, navegador, componentes, estilos, botones ni modales. Las seis pruebas de usuario del TP6 se conservan como escenarios del servicio de aplicación, sin interfaz gráfica.

Es una especificación de pruebas a implementar; no informa pruebas ejecutadas ni garantiza cobertura de reglas que el PO todavía no definió. Cada caso incluye método, preparación, entradas, acción, resultado y aserciones. Las variantes enumeradas se ejecutan por separado como tests parametrizados.

**Origen:** R = requisito del enunciado o aclaración registrada; T = contrato o comprobación técnica propuesta al grupo; P = decisión de dominio pendiente del PO. Una decisión de representación técnica no necesita convertirse en una consulta de negocio. Un caso P se mantiene pendiente, sin elegir silenciosamente su resultado.

**Índice**

1. [Alcance y contrato](#1-alcance-y-contrato-de-datos)
2. [Clases y métodos](#2-clases-metodos-y-colaboraciones-a-testear)
3. [Fixtures y aserciones](#3-datos-base-y-aserciones-comunes)
4. [Escenarios unitarios](#4-tests-unitarios)
5. [Escenarios de integración](#5-tests-de-integracion)
6. [Consultas y decisiones](#6-consultas-y-decisiones)
7. [Trazabilidad y ejecución](#7-trazabilidad-y-ejecucion-con-tdd)


<a id="1-alcance-y-contrato-de-datos"></a>

## 1 Alcance y contrato de datos

### Reglas confirmadas

| Actividad | Capacidad informada | Edad mínima | Talle requerido |
|---|---:|---|---|
| Safari | 8 | Todas las edades | No |
| Jardinería | 12 | Todas las edades | No |
| Palestra | 12 | 12 inclusive | Sí, de XS a XXL |
| Tirolesa | 10 | 8 inclusive | Sí, de XS a XXL |

Actividades de una hora, de 09:00 a 18:00, sin turno que empiece a las 18:00. Se requieren actividad y turno disponibles, cantidad y datos de cada participante, aceptación de términos específicos y confirmación con QR. Ante cupos insuficientes se rechaza toda la solicitud. No se exige validar una entrada al parque. La inscripción abre desde 24 h antes del inicio, inclusive, y cierra 15 min antes, inclusive. No se admite una anticipación mayor a 24 h. El parque cierra lunes, 25/12 y 01/01. Una persona no puede reservar otra actividad del mismo día y horario. No hay máximo adicional por transacción mientras el grupo quepa. Se envía un correo por inscripción; cantidad de QR pendiente Q03.

### Contrato técnico propuesto

Los ejemplos usan una entrada estructurada con tipos estrictos en el backend Java. Los tests de tipo incorrecto se escriben sobre la deserialización o validación de entrada, donde llega el dato sin tipar; no se fuerza una llamada imposible a un método tipado. El frontend JavaScript y los datos de API requieren validación en ejecución. `ValidadorEntrada` es un nombre orientativo para ese límite, no una obligación de agregar una clase nueva.

| Dato | Tipo canónico | Presencia y restricciones |
|---|---|---|
| Solicitud | Objeto estructurado | Obligatoria; no lista ni escalar. |
| actividadId | Cadena | Obligatoria, no vacía; identificador opaco del catálogo. |
| turnoId | Cadena | Obligatoria, no vacía; referencia a un turno existente. |
| cantidadDeclarada | Entero semántico | Obligatorio y mayor que 0; debe coincidir con la lista. |
| participantes | Lista de objetos | Obligatoria, no vacía; validar cada elemento. |
| participantes[i].nombre | Cadena | Obligatoria, no vacía ni solo espacios. |
| participantes[i].dni | Cadena | Obligatoria según el criterio actual; conservar el valor. Formato y excepciones esperan al PO. |
| participantes[i].edad | Entero semántico | Obligatorio, finito, mayor o igual que 0; cumplir mínimo de actividad. |
| participantes[i].talle | Cadena u omisión/null | Obligatorio y enumerado en Palestra/Tirolesa. Opcional en Safari/Jardinería. |
| terminos | Objeto | Aceptación asociada a la actividad. Representación técnica propuesta. |
| terminos.actividadId | Cadena | Obligatoria y coincidente con actividadId. |
| terminos.aceptados | Booleano | Debe ser `true`; `false` es un tipo válido pero un rechazo de negocio. |
| contacto | Objeto de contacto | Campo de email o sesión simulada son alternativas permitidas. El fixture usa campo de contacto; elección visual del equipo. |
| contacto.email | Cadena | Si se adopta email de responsable: no vacía y sintácticamente válida. |
| fecha de consulta | Fecha real | Ejemplo de formato externo ISO `AAAA-MM-DD`, convertida a tipo fecha. |
| turno.inicio / registro | Fecha y hora con zona u offset | Objetos internos consistentes; zona real de operación por acordar. |
| capacidad / ocupados | Enteros | No negativos; ocupados no puede superar capacidad. |
| habilitado / requiereTalle | Booleanos | Sin coerciones desde texto o números. |

**Decisiones de contrato, no reglas nuevas del parque:** conservar DNI como texto; no convertir `"00123456"` a un número. No convertir implícitamente `"12"` a edad 12 ni `"false"` a un booleano verdadero. Si desean admitir esa normalización, agréguenla en un adaptador y cambien sus tests de manera explícita. No impongan 8 dígitos de DNI, una edad máxima, un máximo global de 10 participantes o un formato UUID que las fuentes no exigen.

**Entero semántico:** 12 y una representación numérica equivalente a 12.0 cumplen la condición de cantidad/edad entera; 12.5 no. La representación lexical del JSON no define por sí sola la regla. NaN e infinitos son inválidos cuando el lenguaje permite recibirlos; no son valores JSON válidos.

**Presencia:** distinguir propiedad omitida, `null`, cadena vacía, espacios, valor 0 y `false`. Una edad 0 puede ser válida; una cantidad 0 no; una aceptación `false` debe rechazarse por la regla correspondiente. No usar una comprobación genérica de valor falsy para todos los campos.

**Talle opcional:** bajo este contrato, omisión y `null` son válidos en actividades que no requieren talle. Si llega un valor no nulo, debe ser cadena. La validación del enum XS a XXL se aplica cuando la actividad requiere vestimenta. Si el grupo prefiere descartar por completo ese campo opcional, debe documentar ese contrato y ajustar sus tests técnicos.

**Errores:** los nombres `DATO_OBLIGATORIO`, `TIPO_INVALIDO`, `VALOR_INVALIDO`, `ACTIVIDAD_INEXISTENTE`, `TURNO_INEXISTENTE`, `SIN_CUPOS`, `EDAD_INSUFICIENTE`, `TALLE_REQUERIDO` y `TERMINOS_NO_ACEPTADOS` son ejemplos técnicos. Las aserciones identifican categoría y campo, no una frase literal ni un orden obligatorio de errores. No se exige recolectar todos los errores si el grupo elige fallar en el primero.


<a id="2-clases-metodos-y-colaboraciones-a-testear"></a>

## 2 Clases métodos y colaboraciones a testear

| Componente | Método o función | Qué se verifica |
|---|---|---|
| ValidadorEntrada o deserializador | validarSolicitud(raw) / convertirEntrada(raw) | Presencia, tipos, forma de listas/objetos y conversión explícita. |
| Participante | validarDatosObligatorios() | Nombre, DNI, edad y errores identificables por participante. |
| Actividad | validarEdad(edad), validarTalle(talle) | Reglas de edad y vestimenta de la actividad. |
| Turno | cuposDisponibles(), validarCantidad(cantidad) | Disponibilidad y límites, sin mutaciones de la validación. |
| PoliticaTurnos | validarDisponibilidad(turno, ahora) | Turno habilitado, calendario real, horario, apertura de 24 h y cierre de 15 min; ambas fronteras son inclusivas. |
| ValidadorInscripcion | validar(solicitud, actividad, turno, ahora) | Consistencia completa antes del registro. |
| ServicioConsultaActividades | listarActividades(), consultarTurnos(actividadId, fecha), obtenerTerminos(actividadId) | Selección basada en datos reales y sin efectos secundarios. |
| ServicioInscripcion | inscribir(solicitud) | Coordinación del caso de uso y resultado observable. |
| RepositorioParque | confirmar(solicitud), obtenerInscripcion(id), existeInscripcionEnHorario(dni, inicio), obtenerTurno(id), consultarTurnos(...), obtenerActividad(id), listarActividades(), obtenerTerminos(id) | Registro y consumo indivisibles; revalidar cupos y conflictos personales. Firma y clave documental orientativas, sujetas a Q06. |
| Reloj | ahora() | Tiempo controlado en las decisiones y fecha de registro. |
| GeneradorConfirmacion | generarMensaje(inscripcion) | Un mensaje por inscripción con datos registrados, destinatario y adjunto QR; cantidad QR Q03. |
| GeneradorQR | generar(contenido) | Contenido correcto y QR real decodificable. |
| EmisorCorreo | enviar(mensaje) | Contrato de destinatario, cuerpo y QR. |
| EmisorCorreoSimulado | enviar(mensaje), listarMensajes(), obtenerMensaje(id) | Recuperación de mensajes sin transporte real. |

`SolicitudInscripcion`, `Inscripcion`, `TerminosActividad`, `MensajeConfirmacion` y `ResultadoInscripcion` son estructuras de datos. Se verifican mediante deserialización, validación, registro y recuperación; no se agregan tests solo para comprobar getters o constructores triviales.


<a id="3-datos-base-y-aserciones-comunes"></a>

## 3 Datos base y aserciones comunes

### Solicitud válida de referencia

```json
{
  "actividadId": "palestra",
  "turnoId": "P1",
  "cantidadDeclarada": 2,
  "participantes": [
    {
      "nombre": "Persona de ejemplo uno",
      "dni": "00123456",
      "edad": 24,
      "talle": "M"
    },
    {
      "nombre": "Persona de ejemplo dos",
      "dni": "00987654",
      "edad": 12,
      "talle": "XS"
    }
  ],
  "terminos": {
    "actividadId": "palestra",
    "aceptados": true
  },
  "contacto": {
    "email": "demo@example.com"
  }
}
```

Los identificadores son ejemplos opacos. El DNI de ejemplo ilustra la conservación del texto, no una validación de longitud ni un formato aprobado. `contacto.email` es una alternativa permitida; el fixture la utiliza sin exigir login. La aceptación única del grupo sigue como propuesta pendiente Q08.

### Estado inicial

- Reloj fijo: `2026-10-14T10:00:00-03:00`. El offset es solo de fixture, no una regla del parque.
- Turnos `S1` Safari, `P1` Palestra, `T1` Tirolesa, `J1` Jardinería: inicio `2026-10-14T14:00:00-03:00`, duración 60 minutos, habilitados, parque abierto según calendario del fixture y ocupación 0.
- Capacidades configuradas: S1=8, P1=12, T1=10, J1=12. Capacidad correspondiente a cada turno de actividad y fecha, según FAQ 13/31 y respuestas del grupo 4/11.
- Turno S2: Safari 14/10/2026 15:00; S3: Safari 15/10/2026 14:00. Ambos tienen 8 lugares libres. Al inscribir en S3 usar reloj 15/10/2026 10:00; no reutilizar el reloj del día anterior si queda fuera de ventana.
- Repositorio y bandeja de correo nuevos por test; sin datos compartidos entre ejecuciones.
- `solicitudValida(actividad, turno, n)` crea exactamente n participantes válidos, DNIs distintos, edades admisibles y talles cuando se necesitan. Nunca cambia solo la cantidad dejando una lista incompatible.
- Ocupación preexistente: sembrar inscripciones consistentes con la cantidad ocupada; no falsear un contador separado del registro.
- Política confirmada: un mensaje por inscripción a un contacto resuelto. El fixture usa un destinatario controlado y un QR con contenido de prueba; ese QR no resuelve Q03.

### Aserciones comunes

**RECHAZO_SIN_EFECTOS:** resultado rechazado, error identificable; mismas inscripciones y participantes persistidos; mismos cupos; sin nueva confirmación en la bandeja ni envío. En unitarios se observa el doble; en integración se consulta el adaptador real. Se aplica a rechazos anteriores al registro, no automáticamente a fallos del correo después de confirmar.

**EXITO_REGISTRADO:** una nueva inscripción con identidad propia; mismo turno/actividad y todos los participantes correctos; diferencia de ocupación igual a n; datos de confirmación derivados del registro. Un correo por inscripción; cantidad/payload QR bajo contrato de prueba mientras Q03 siga abierto.

**ERROR_DE_DATOS:** categoría de error y ruta de campo correctas (`participantes[1].edad`, por ejemplo); sin una excepción técnica no controlada como resultado de una entrada externa malformada. El mecanismo puede ser excepción de validación o ResultadoInscripcion, según el lenguaje.

**CONFIGURACION_INVALIDA:** fallo controlado al construir/mapear estado interno imposible. No convertir ese estado en una inscripción válida ni reportarlo como dato ingresado por el visitante.

**UNA_VARIANTE_UN_FALLO:** usar copia nueva de la solicitud base y alterar solo el dato probado. No mezclar un DNI faltante con falta de cupos si se quiere demostrar cuál regla rechaza.

### Niveles de prueba

- Unitario: componente real y colaboradores reemplazados por fakes/stubs/spies cuando corresponde. Puede haber un test de servicio con repositorio falso; ese test no demuestra atomicidad del almacenamiento.
- Integración: servicio, validadores, entidades y repositorio real conectados. Si el producto usa base de datos, usar una base temporal del mismo motor elegido para probar transacciones; SQLite no demuestra automáticamente comportamiento de otro motor. QR real en sus integraciones. El emisor puede ser el adaptador simulado permitido por el PO.
- No se necesita SMTP real, Mercado Pago, login completo, compra de entradas ni navegador para estos tests. Las pruebas de destinatario autenticado se incorporan si se acuerda esa variante de contacto.


<a id="4-tests-unitarios"></a>

## 4 Escenarios unitarios planificados


### 4.1 Forma general de la entrada


#### U001 — Solicitud ausente o de tipo incorrecto

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud(raw).
- **Preparación:** El límite recibe datos sin tipar.
- **Datos:** Omisión de argumento donde sea posible; null; []; "texto"; 1; true.
- **Acción:** Validar cada variante por separado.
- **Resultado esperado:** ERROR_DE_DATOS en la raíz de la solicitud.
- **Aserciones:** No se ejecuta registro, QR ni correo. Diferenciar ausencia y tipo incorrecto.


#### U002 — JSON malformado en una entrada serializada

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional a la existencia de un adaptador JSON.
- **Método o colaboración:** AdaptadorSolicitud.convertirEntrada(raw).
- **Preparación:** Solo si la entrada del caso de uso se recibe como JSON.
- **Datos:** Cuerpo incompleto, sintaxis inválida o literales NaN/Infinity.
- **Acción:** Deserializar el cuerpo.
- **Resultado esperado:** Error de deserialización controlado; no se construye una solicitud válida.
- **Aserciones:** El servicio no registra nada. Omitir este test si no existe una entrada serializada.


#### U003 — Solicitud canónica bien tipada

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud(raw).
- **Preparación:** Usar solicitud válida de referencia.
- **Datos:** Objeto completo, cantidad 2 y dos participantes.
- **Acción:** Validar o convertir.
- **Resultado esperado:** Datos válidos para el contrato técnico.
- **Aserciones:** No se altera DNI, edad, cantidad, aceptación ni participantes. La validación estructural no garantiza todavía cupos.


### 4.2 Identificadores nombre y DNI


#### U004 — Presencia de actividadId

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarSeleccion.
- **Preparación:** Los demás campos son válidos.
- **Datos:** Eliminar actividadId; luego probar null, "" y "   ".
- **Acción:** Validar cada variante.
- **Resultado esperado:** ERROR_DE_DATOS de dato obligatorio en actividadId.
- **Aserciones:** Una ruta de campo correcta; no tomar espacios como un dato presente.


#### U005 — Tipo de actividadId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarSeleccion.
- **Preparación:** Contrato de cadenas estrictas.
- **Datos:** actividadId=0, 42, 12.5, true, false, [], {}; variantes independientes.
- **Acción:** Validar la entrada.
- **Resultado esperado:** TIPO_INVALIDO en actividadId.
- **Aserciones:** No convertir números, booleanos, listas u objetos a texto para aceptar la solicitud.


#### U006 — Valor de texto preservado en actividadId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarSeleccion.
- **Preparación:** Valor correcto para el contrato estructural.
- **Datos:** actividadId="palestra".
- **Acción:** Validar o convertir la entrada.
- **Resultado esperado:** El valor conserva su identidad como cadena.
- **Aserciones:** Nombre Unicode y apóstrofe son válidos estructuralmente; DNI conserva ceros iniciales. Existencia de IDs se verifica después, sin asumir formato UUID.


#### U007 — Presencia de turnoId

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarSeleccion.
- **Preparación:** Los demás campos son válidos.
- **Datos:** Eliminar turnoId; luego probar null, "" y "   ".
- **Acción:** Validar cada variante.
- **Resultado esperado:** ERROR_DE_DATOS de dato obligatorio en turnoId.
- **Aserciones:** Una ruta de campo correcta; no tomar espacios como un dato presente.


#### U008 — Tipo de turnoId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarSeleccion.
- **Preparación:** Contrato de cadenas estrictas.
- **Datos:** turnoId=0, 42, 12.5, true, false, [], {}; variantes independientes.
- **Acción:** Validar la entrada.
- **Resultado esperado:** TIPO_INVALIDO en turnoId.
- **Aserciones:** No convertir números, booleanos, listas u objetos a texto para aceptar la solicitud.


#### U009 — Valor de texto preservado en turnoId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarSeleccion.
- **Preparación:** Valor correcto para el contrato estructural.
- **Datos:** turnoId="P1".
- **Acción:** Validar o convertir la entrada.
- **Resultado esperado:** El valor conserva su identidad como cadena.
- **Aserciones:** Nombre Unicode y apóstrofe son válidos estructuralmente; DNI conserva ceros iniciales. Existencia de IDs se verifica después, sin asumir formato UUID.


#### U010 — Presencia de participantes[0].nombre

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Los demás campos son válidos.
- **Datos:** Eliminar participantes[0].nombre; luego probar null, "" y "   ".
- **Acción:** Validar cada variante.
- **Resultado esperado:** ERROR_DE_DATOS de dato obligatorio en participantes[0].nombre.
- **Aserciones:** Una ruta de campo correcta; no tomar espacios como un dato presente.


#### U011 — Tipo de participantes[0].nombre

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Contrato de cadenas estrictas.
- **Datos:** participantes[0].nombre=0, 42, 12.5, true, false, [], {}; variantes independientes.
- **Acción:** Validar la entrada.
- **Resultado esperado:** TIPO_INVALIDO en participantes[0].nombre.
- **Aserciones:** No convertir números, booleanos, listas u objetos a texto para aceptar la solicitud.


#### U012 — Valor de texto preservado en participantes[0].nombre

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Valor correcto para el contrato estructural.
- **Datos:** participantes[0].nombre="María José O’Connor".
- **Acción:** Validar o convertir la entrada.
- **Resultado esperado:** El valor conserva su identidad como cadena.
- **Aserciones:** Nombre Unicode y apóstrofe son válidos estructuralmente; DNI conserva ceros iniciales. Existencia de IDs se verifica después, sin asumir formato UUID.


#### U013 — Presencia de participantes[0].dni

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Los demás campos son válidos.
- **Datos:** Eliminar participantes[0].dni; luego probar null, "" y "   ".
- **Acción:** Validar cada variante.
- **Resultado esperado:** ERROR_DE_DATOS de dato obligatorio en participantes[0].dni.
- **Aserciones:** Una ruta de campo correcta; no tomar espacios como un dato presente.


#### U014 — Tipo de participantes[0].dni

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Contrato de cadenas estrictas.
- **Datos:** participantes[0].dni=0, 42, 12.5, true, false, [], {}; variantes independientes.
- **Acción:** Validar la entrada.
- **Resultado esperado:** TIPO_INVALIDO en participantes[0].dni.
- **Aserciones:** No convertir números, booleanos, listas u objetos a texto para aceptar la solicitud.


#### U015 — Valor de texto preservado en participantes[0].dni

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Valor correcto para el contrato estructural.
- **Datos:** participantes[0].dni="00123456".
- **Acción:** Validar o convertir la entrada.
- **Resultado esperado:** El valor conserva su identidad como cadena.
- **Aserciones:** Nombre Unicode y apóstrofe son válidos estructuralmente; DNI conserva ceros iniciales. Existencia de IDs se verifica después, sin asumir formato UUID.


#### U016 — Presencia de terminos.actividadId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarAceptacionTerminos.
- **Preparación:** Los demás campos son válidos.
- **Datos:** Eliminar terminos.actividadId; luego probar null, "" y "   ".
- **Acción:** Validar cada variante.
- **Resultado esperado:** ERROR_DE_DATOS de dato obligatorio en terminos.actividadId.
- **Aserciones:** Una ruta de campo correcta; no tomar espacios como un dato presente.


#### U017 — Tipo de terminos.actividadId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarAceptacionTerminos.
- **Preparación:** Contrato de cadenas estrictas.
- **Datos:** terminos.actividadId=0, 42, 12.5, true, false, [], {}; variantes independientes.
- **Acción:** Validar la entrada.
- **Resultado esperado:** TIPO_INVALIDO en terminos.actividadId.
- **Aserciones:** No convertir números, booleanos, listas u objetos a texto para aceptar la solicitud.


#### U018 — Valor de texto preservado en terminos.actividadId

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / validarAceptacionTerminos.
- **Preparación:** Valor correcto para el contrato estructural.
- **Datos:** terminos.actividadId="palestra".
- **Acción:** Validar o convertir la entrada.
- **Resultado esperado:** El valor conserva su identidad como cadena.
- **Aserciones:** Nombre Unicode y apóstrofe son válidos estructuralmente; DNI conserva ceros iniciales. Existencia de IDs se verifica después, sin asumir formato UUID.


### 4.3 Cantidad y colección de participantes


#### U019 — Cantidad obligatoria

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Lista válida de dos personas.
- **Datos:** Omitir cantidadDeclarada; luego null.
- **Acción:** Validar.
- **Resultado esperado:** DATO_OBLIGATORIO en cantidadDeclarada.
- **Aserciones:** No inferir una cantidad silenciosamente si el contrato exige declararla.


#### U020 — Cantidad de tipo incorrecto

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Los demás datos son válidos.
- **Datos:** cantidadDeclarada="2", true, false, [], {}, NaN, +Infinity, -Infinity cuando el lenguaje los admite.
- **Acción:** Validar cada variante.
- **Resultado esperado:** Tipo inválido o número no finito.
- **Aserciones:** true nunca equivale a una persona; no hay coerción desde cadenas.


#### U021 — Cantidad no positiva o fraccionaria

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Participantes y selección válidos para aislar esta regla.
- **Datos:** cantidadDeclarada=0; -1; 1.5.
- **Acción:** Validar.
- **Resultado esperado:** VALOR_INVALIDO en cantidadDeclarada.
- **Aserciones:** No continuar al registro con cantidad cero, negativa o fraccionaria.


#### U022 — Cantidad entera válida

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** La lista contiene la cantidad indicada.
- **Datos:** cantidadDeclarada=1 y 2; representación numérica equivalente a 2.0.
- **Acción:** Validar cada solicitud.
- **Resultado esperado:** La cantidad es un entero semántico válido.
- **Aserciones:** La cadena "2" se prueba como rechazo en el caso anterior; no confundir tipo con representación lexical.


#### U023 — Participantes obligatorios

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Solicitud con actividad, turno y cantidad.
- **Datos:** Omitir participantes; luego null.
- **Acción:** Validar.
- **Resultado esperado:** DATO_OBLIGATORIO en participantes.
- **Aserciones:** No construir una inscripción sin colección.


#### U024 — Tipo de la colección de participantes

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Contrato de lista de objetos.
- **Datos:** participantes={}, "persona", 1, true.
- **Acción:** Validar.
- **Resultado esperado:** TIPO_INVALIDO en participantes.
- **Aserciones:** Un objeto individual no se convierte implícitamente en lista.


#### U025 — Lista vacía

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Cantidad declarada 1 para la variante.
- **Datos:** participantes=[].
- **Acción:** Validar.
- **Resultado esperado:** Lista vacía o inconsistencia de cantidad; nunca inscripción válida.
- **Aserciones:** No registrar una inscripción con cero participantes.


#### U026 — Tipo de cada elemento de participantes

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Cantidad 2 y un primer participante válido.
- **Datos:** Segundo elemento null; "persona"; 12; false; [].
- **Acción:** Validar cada variante.
- **Resultado esperado:** TIPO_INVALIDO en participantes[1].
- **Aserciones:** No evaluar atributos sobre un elemento escalar ni omitirlo.


#### U027 — Cantidad mayor que los datos cargados

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Dos participantes válidos.
- **Datos:** cantidadDeclarada=3 y longitud=2.
- **Acción:** Validar.
- **Resultado esperado:** Error de cantidad y colección inconsistentes.
- **Aserciones:** No crear un tercer participante vacío.


#### U028 — Cantidad menor que los datos cargados

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Tres participantes válidos.
- **Datos:** cantidadDeclarada=2 y longitud=3.
- **Acción:** Validar.
- **Resultado esperado:** Error de cantidad y colección inconsistentes.
- **Aserciones:** No descartar silenciosamente al tercer participante.


#### U029 — Validación de todos los participantes

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Tres personas; modificar una sola por ejecución.
- **Datos:** Dato inválido en índice 0, 1 y 2; repetir con nombre, edad y talle obligatorio.
- **Acción:** Validar.
- **Resultado esperado:** Se identifica el participante y campo inválidos en cada posición.
- **Aserciones:** No validar solo el primero. La política de inscripción parcial del grupo espera al PO.


### 4.4 Edad y talle como datos


#### U030 — Edad obligatoria

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Nombre y DNI presentes.
- **Datos:** Omitir edad; luego null.
- **Acción:** Validar.
- **Resultado esperado:** DATO_OBLIGATORIO en participantes[0].edad.
- **Aserciones:** Edad 0 se conserva como valor distinto de ausencia.


#### U031 — Tipo de edad

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / Participante.validarDatosObligatorios.
- **Preparación:** Los demás datos son válidos.
- **Datos:** edad="12", "doce", true, false, [], {}.
- **Acción:** Validar cada variante.
- **Resultado esperado:** TIPO_INVALIDO.
- **Aserciones:** No convertir texto ni aceptar booleanos como enteros.


#### U032 — Edad negativa fraccionaria o no finita

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Participante.validarDatosObligatorios.
- **Preparación:** Contrato de años enteros no negativos.
- **Datos:** edad=-1; 12.5; NaN; +Infinity; -Infinity cuando el lenguaje lo permite.
- **Acción:** Validar.
- **Resultado esperado:** VALOR_INVALIDO o número no finito.
- **Aserciones:** El valor no llega como edad válida a la regla de la actividad.


#### U033 — Edad cero y entero semántico

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Participante.validarDatosObligatorios.
- **Preparación:** Validar estructura, sin regla de actividad.
- **Datos:** edad=0; 12; 12.0 como número equivalente.
- **Acción:** Validar.
- **Resultado esperado:** Datos de edad estructuralmente válidos.
- **Aserciones:** 0 no produce edad ausente; después se evalúa edad mínima por actividad.


#### U034 — Tipo de talle informado

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** La actividad requiere talle; otros datos correctos.
- **Datos:** talle=42; true; []; {}.
- **Acción:** Validar.
- **Resultado esperado:** TIPO_INVALIDO en talle.
- **Aserciones:** No transformar un objeto o número en una talla.


#### U035 — Talle ausente cuando se necesita

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarTalle.
- **Preparación:** Palestra o Tirolesa; participante de edad suficiente.
- **Datos:** Omitir talle; null; ""; "   ".
- **Acción:** Validar cada combinación actividad y ausencia.
- **Resultado esperado:** TALLE_REQUERIDO.
- **Aserciones:** Cada variante rechaza la condición de vestimenta; no se exige un talle por defecto.


#### U036 — Talle ausente cuando es opcional

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarTalle.
- **Preparación:** Safari y Jardinería.
- **Datos:** Talle omitido y null.
- **Acción:** Validar cada combinación.
- **Resultado esperado:** La condición de talle se cumple.
- **Aserciones:** No hay TALLE_REQUERIDO.


#### U037 — Talle opcional informado con un tipo incorrecto

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Safari o Jardinería bajo el contrato de campos opcionales definido.
- **Datos:** talle=1, false, [], {}.
- **Acción:** Validar.
- **Resultado esperado:** TIPO_INVALIDO si el campo no nulo se conserva en el contrato.
- **Aserciones:** Caso técnico ajustable si el grupo decide descartar por completo el campo no aplicable.


### 4.5 Aceptación de términos y contacto


#### U038 — Objeto de términos obligatorio

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / ValidadorInscripcion.
- **Preparación:** Solicitud sin otra falla.
- **Datos:** Omitir terminos; luego null.
- **Acción:** Validar.
- **Resultado esperado:** No existe una aceptación válida: rechazo.
- **Aserciones:** No asumir aceptación por defecto.


#### U039 — Tipo de objeto de términos

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Contrato de aceptación estructurada.
- **Datos:** terminos=true; "acepto"; []; 1.
- **Acción:** Validar.
- **Resultado esperado:** TIPO_INVALIDO en terminos.
- **Aserciones:** Un booleano aislado no cumple este contrato de vínculo con actividad.


#### U040 — Bandera de aceptación obligatoria

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / ValidadorInscripcion.
- **Preparación:** Objeto de términos asociado a Palestra.
- **Datos:** Omitir terminos.aceptados; luego null.
- **Acción:** Validar.
- **Resultado esperado:** Aceptación ausente: rechazo.
- **Aserciones:** No se toma la existencia de terminos como prueba de aceptación.


#### U041 — Tipo de la bandera de aceptación

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Campo obligatorio booleano.
- **Datos:** aceptados="true", "false", 1, 0, [], {}.
- **Acción:** Validar cada variante.
- **Resultado esperado:** TIPO_INVALIDO.
- **Aserciones:** "false" no se convierte a true por ser texto no vacío.


#### U042 — Términos explícitamente no aceptados

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Aceptación asociada a la actividad correcta.
- **Datos:** aceptados=false.
- **Acción:** Validar.
- **Resultado esperado:** TERMINOS_NO_ACEPTADOS, no un error de tipo.
- **Aserciones:** El booleano es válido estructuralmente, pero incumple la regla de negocio.


#### U043 — Términos de otra actividad

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Actividad Palestra y turno P1.
- **Datos:** terminos.actividadId="safari", aceptados=true.
- **Acción:** Validar.
- **Resultado esperado:** Rechazo por términos no correspondientes.
- **Aserciones:** Aceptar Safari no autoriza Palestra.


#### U044 — Contacto ausente sin otra fuente acordada

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional a adoptar contacto.email como entrada; no fija la obligación de login.
- **Método o colaboración:** ValidadorEntrada / resolución de destinatario.
- **Preparación:** Fixture de email del responsable sin usuario autenticado.
- **Datos:** Contacto omitido, null o email omitido/null/vacío/espacios.
- **Acción:** Resolver el destinatario.
- **Resultado esperado:** Bajo este contrato no puede construirse un mensaje con destinatario válido.
- **Aserciones:** ERROR_DE_DATOS en la ruta faltante. Si se elige mail de sesión, reemplazar por los tests de ese contrato.


#### U045 — Tipo del contacto y del email

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional a adoptar contacto.email como entrada; no fija la obligación de login.
- **Método o colaboración:** ValidadorEntrada.validarSolicitud.
- **Preparación:** Fixture de contacto.email.
- **Datos:** contacto=[]/true/"texto"; email=42/false/[]/{}.
- **Acción:** Validar cada variante.
- **Resultado esperado:** TIPO_INVALIDO en el objeto o en email.
- **Aserciones:** No convertir datos arbitrarios a direcciones.


#### U046 — Sintaxis de email inválida

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional a adoptar contacto.email como entrada; no fija la obligación de login.
- **Método o colaboración:** Validador de contacto.
- **Preparación:** Usar la biblioteca o política técnica de email acordada.
- **Datos:** "persona"; "@example.com"; "persona@"; "per sona@example.com".
- **Acción:** Validar.
- **Resultado esperado:** Email sintácticamente inválido.
- **Aserciones:** No intentar DNS, SMTP ni existencia de la cuenta para un test unitario.


#### U047 — Email sintácticamente válido

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional a adoptar contacto.email como entrada; no fija la obligación de login.
- **Método o colaboración:** Validador de contacto.
- **Preparación:** Fixture de contacto.email.
- **Datos:** "demo@example.com" y "demo+grupo@example.com".
- **Acción:** Validar.
- **Resultado esperado:** Email válido para el contrato.
- **Aserciones:** Se conserva el destinatario; no afirmar que la cuenta existe o recibió un correo.


### 4.6 Catálogo fechas y estado interno


#### U048 — Catálogo y configuración por actividad

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioConsultaActividades.listarActividades / configuración.
- **Preparación:** Fake del catálogo con las cuatro actividades.
- **Datos:** Safari 8 sin talle; Jardinería 12 sin talle; Palestra 12 mínimo 12 y talle; Tirolesa 10 mínimo 8 y talle.
- **Acción:** Consultar o construir la configuración.
- **Resultado esperado:** Los cuatro registros conservan reglas y capacidades correctas.
- **Aserciones:** No confundir capacidad con cupos libres ni exigir altura.


#### U049 — Actividad inexistente

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / validarSeleccion.
- **Preparación:** Repositorio falso informa ausencia de actividad.
- **Datos:** actividadId="desconocida".
- **Acción:** Intentar inscribir.
- **Resultado esperado:** ACTIVIDAD_INEXISTENTE.
- **Aserciones:** RECHAZO_SIN_EFECTOS.


#### U050 — Turno inexistente

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / validarSeleccion.
- **Preparación:** Actividad existente y repositorio falso sin ese turno.
- **Datos:** turnoId="inexistente".
- **Acción:** Intentar inscribir.
- **Resultado esperado:** TURNO_INEXISTENTE.
- **Aserciones:** RECHAZO_SIN_EFECTOS.


#### U051 — Turno asociado a otra actividad

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Actividad Palestra y turno Safari S1.
- **Datos:** actividadId="palestra", turnoId="S1".
- **Acción:** Validar.
- **Resultado esperado:** Error de asociación actividad y turno.
- **Aserciones:** Nunca evaluar S1 con reglas de Palestra.


#### U052 — Fecha de consulta obligatoria y tipada

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional a una entrada externa de consulta con fecha ISO.
- **Método o colaboración:** AdaptadorConsulta / consultarTurnos.
- **Preparación:** Contrato de consulta por fecha ISO.
- **Datos:** Fecha omitida/null; 12/false/[]/{}; "16/10/2026".
- **Acción:** Convertir los argumentos.
- **Resultado esperado:** Error de ausencia, tipo o formato respectivamente.
- **Aserciones:** No adivinar formatos locales ambiguos.


#### U053 — Fecha real y año bisiesto

- **Nivel y origen:** Unitario · T.
- **Estado:** Condicional al adaptador de fechas elegido.
- **Método o colaboración:** AdaptadorConsulta / conversión de fecha.
- **Preparación:** Mismo contrato ISO.
- **Datos:** Inválidas: "2026-02-29", "2026-04-31", "2026-13-01". Válidas: "2028-02-29", "2026-10-16".
- **Acción:** Convertir, sin evaluar todavía el horizonte de reservas.
- **Resultado esperado:** Se rechazan fechas inexistentes; las fechas reales se convierten.
- **Aserciones:** Que la fecha exista no significa que esté abierta o dentro del horizonte permitido.


#### U054 — Tipo y validez de inicio interno del turno

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Mapeo o construcción de Turno.
- **Preparación:** El adaptador recibe un registro interno de turno.
- **Datos:** Inicio omitido/null; tipo equivocado; fecha-hora imposible; fecha-hora válida con offset.
- **Acción:** Convertir o construir Turno.
- **Resultado esperado:** CONFIGURACION_INVALIDA en valores imposibles; valor válido conserva instante y offset.
- **Aserciones:** No interpretar hora sin zona según la zona local del equipo de pruebas.


#### U055 — Tipos de capacidad ocupación y flags

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Mapeo de Turno y Actividad.
- **Preparación:** Construir estado interno para las reglas.
- **Datos:** capacidad/ocupados="8", false, 1.5, NaN; habilitado/requiereTalle="false", 1, null.
- **Acción:** Validar o construir cada variante.
- **Resultado esperado:** CONFIGURACION_INVALIDA.
- **Aserciones:** Booleanos no se usan como números; flags solo aceptan booleanos.


#### U056 — Ocupación interna imposible

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Turno / mapa de repositorio.
- **Preparación:** Capacidad 8.
- **Datos:** ocupados=-1; 9; capacidad=-1.
- **Acción:** Construir o mapear estado.
- **Resultado esperado:** CONFIGURACION_INVALIDA.
- **Aserciones:** Nunca informar disponibilidad negativa ni una capacidad inventada.


#### U057 — Consulta sin turnos

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioConsultaActividades.consultarTurnos.
- **Preparación:** Fake devuelve lista vacía para fecha/actividad.
- **Datos:** Actividad existente y fecha real sin agenda.
- **Acción:** Consultar.
- **Resultado esperado:** Lista vacía.
- **Aserciones:** No inventar turnos ni registrar una inscripción.


#### U058 — Términos específicos recuperados

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioConsultaActividades.obtenerTerminos.
- **Preparación:** Fake contiene textos de prueba distintos para cada actividad.
- **Datos:** Consultar Safari, Palestra, Tirolesa y Jardinería.
- **Acción:** Recuperar el contenido.
- **Resultado esperado:** Cada respuesta corresponde a la actividad solicitada.
- **Aserciones:** No reutilizar el texto de Safari para otra actividad. La aprobación del texto no es un test automático.


### 4.7 Reglas de edad vestimenta y disponibilidad


#### U059 — Límites de edad de Palestra

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarEdad.
- **Preparación:** Actividad Palestra.
- **Datos:** Edades 11, 12 y 13.
- **Acción:** Validar cada edad.
- **Resultado esperado:** 11 rechaza; 12 y 13 cumplen edad mínima.
- **Aserciones:** El límite es inclusivo en 12.


#### U060 — Límites de edad de Tirolesa

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarEdad.
- **Preparación:** Actividad Tirolesa.
- **Datos:** Edades 7, 8 y 9.
- **Acción:** Validar.
- **Resultado esperado:** 7 rechaza; 8 y 9 cumplen edad mínima.
- **Aserciones:** El límite es inclusivo en 8.


#### U061 — Todas las edades en Safari y Jardinería

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarEdad.
- **Preparación:** Edad estructuralmente válida.
- **Datos:** 0, 1, 7, 8, 11, 12, 70 y 100; cruzar con ambas actividades.
- **Acción:** Validar.
- **Resultado esperado:** Ningún rechazo por mínimo ni por máximo de edad.
- **Aserciones:** No inventar una edad máxima. Formato e identificación del bebé son otra cuestión.


#### U062 — Conjunto de talles admitidos

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarTalle.
- **Preparación:** Palestra y Tirolesa.
- **Datos:** XS, S, M, L, XL, XXL.
- **Acción:** Validar cada combinación actividad y talle.
- **Resultado esperado:** Todas cumplen la condición de vestimenta.
- **Aserciones:** Cubrir explícitamente XS y XXL como extremos.


#### U063 — Talle fuera del conjunto

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarTalle.
- **Preparación:** Palestra y Tirolesa, talle presente y cadena.
- **Datos:** XXS, XXXL, "talle libre".
- **Acción:** Validar.
- **Resultado esperado:** Talle no permitido.
- **Aserciones:** No aceptarlo como default ni truncarlo.


#### U064 — Talle no obligatorio no bloquea la regla

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Actividad.validarTalle.
- **Preparación:** Safari o Jardinería.
- **Datos:** Talle omitido; null; cadena XS.
- **Acción:** Validar.
- **Resultado esperado:** La condición de talle se cumple.
- **Aserciones:** No trasladar reglas de vestimenta de Palestra.


#### U065 — Agenda horaria de un día habilitado

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Agenda configurada / PoliticaTurnos.
- **Preparación:** Parque abierto, actividad habilitada, fecha lejos del límite.
- **Datos:** Inicios 09:00, 10:00, 11:00, 12:00, 13:00, 14:00, 15:00, 16:00, 17:00.
- **Acción:** Consultar o validar cada turno configurado.
- **Resultado esperado:** Agenda operativa de 09:00 a 17:00, cada turno de una hora. Si la consulta devuelve solo turnos inscribibles, filtrar además por ventana y cupos; no exigir nueve opciones en todo momento.
- **Aserciones:** No contar 18:00 como un inicio válido.


#### U066 — Inicio fuera de agenda o no publicado

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** PoliticaTurnos / repositorio de turnos.
- **Preparación:** Cada inicio inválido tiene un reloj fijo propio dentro de la ventana para aislar agenda: para 08:00, usar 07:00 del mismo día abierto; para otros horarios, ajustar de manera equivalente.
- **Datos:** 08:00, 18:00, 19:00; 09:30 no está publicado.
- **Acción:** Validar turno o buscar agenda.
- **Resultado esperado:** No disponible.
- **Aserciones:** 09:30 rechaza por no pertenecer a la agenda, sin inventar una regla global contra medias horas fuera del fixture.


#### U067 — Turno o parque no disponibles

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** PoliticaTurnos.validarDisponibilidad.
- **Preparación:** Horario válido y cupos presentes.
- **Datos:** Turno habilitado=false; lunes; 25/12; 01/01; actividad no habilitada. Variantes separadas y reloj dentro de la ventana temporal.
- **Acción:** Validar cada causa por separado.
- **Resultado esperado:** Rechazo por disponibilidad.
- **Aserciones:** No confundir falta de cupos con cierre.


#### U068 — Disponibilidad según ocupación

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Turno.cuposDisponibles.
- **Preparación:** Capacidad 8.
- **Datos:** ocupados=0, 3 y 8.
- **Acción:** Consultar cupos disponibles.
- **Resultado esperado:** 8, 5 y 0 respectivamente.
- **Aserciones:** No mutar el turno ni consumir lugares al consultar.


#### U069 — Cantidad frente a los lugares libres

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Turno.validarCantidad.
- **Preparación:** Turno con 2 lugares libres.
- **Datos:** Solicitar 1, 2 y 3 personas válidas.
- **Acción:** Validar cantidad.
- **Resultado esperado:** 1 y 2 cumplen; 3 rechaza por cupos.
- **Aserciones:** La igualdad con cupos disponibles se acepta.


#### U070 — Sin lugares disponibles

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Turno.validarCantidad.
- **Preparación:** Capacidad 8, ocupados 8.
- **Datos:** Solicitar 1.
- **Acción:** Validar.
- **Resultado esperado:** SIN_CUPOS.
- **Aserciones:** No validar como éxito ni consumir lugares.


#### U071 — Sin límite global de diez participantes

- **Nivel y origen:** Unitario · R (FAQ 31).
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Palestra con 12 lugares libres y adultos válidos.
- **Datos:** Solicitar 11 y 12, con listas coherentes.
- **Acción:** Validar reglas conocidas.
- **Resultado esperado:** No aparece un rechazo por máximo global de 10.
- **Aserciones:** Límite 10 corresponde a compra de entradas, no a esta historia; regla confirmada FAQ 31. La FAQ 31 ya descarta un máximo adicional por transacción.


#### U072 — Validación sin efectos secundarios

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorInscripcion.validar.
- **Preparación:** Solicitud válida y otra sin términos.
- **Datos:** Ejecutar validar sobre ambas.
- **Acción:** Comparar snapshot antes y después.
- **Resultado esperado:** Sin cambios en solicitud, turno, participantes ni registro.
- **Aserciones:** No generar QR ni enviar mensajes desde la validación.


### 4.8 Coordinación del servicio y resultados


#### U073 — Inscripción válida con colaboradores controlados

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Dominio real; repositorio fake y reloj fijo; dobles solo en límites de QR/correo. El fake respeta el contrato de registro, pero no prueba atomicidad real.
- **Datos:** Solicitud válida para P1 con dos personas.
- **Acción:** Inscribir.
- **Resultado esperado:** Resultado con inscripción registrada.
- **Aserciones:** Datos enviados al registro coinciden con la solicitud; fecha de registro es la del reloj fijo; confirmación usa la identidad retornada por el repositorio.


#### U074 — Solicitud inválida no alcanza el registro

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Un participante único; no aceptación de términos.
- **Datos:** Solicitud con aceptados=false.
- **Acción:** Inscribir.
- **Resultado esperado:** TERMINOS_NO_ACEPTADOS.
- **Aserciones:** Repositorio de escritura, generador QR y emisor no registran llamadas; fake conserva estado.


#### U075 — Participante único inválido no se inscribe

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Palestra y un solo participante; las demás reglas cumplen.
- **Datos:** Variantes edad 11, talle ausente, nombre ausente.
- **Acción:** Inscribir cada variante.
- **Resultado esperado:** Rechazo por la regla correspondiente.
- **Aserciones:** RECHAZO_SIN_EFECTOS. No fija aún el tratamiento de grupos mixtos.


#### U076 — Registro rechazado por cupos actuales

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Consulta inicial permite la solicitud; fake de confirmar devuelve SIN_CUPOS.
- **Datos:** Solicitud inicialmente válida.
- **Acción:** Inscribir.
- **Resultado esperado:** El servicio propaga rechazo por cupos, no un éxito de la consulta inicial.
- **Aserciones:** No generar confirmación ni enviar correo.


#### U077 — Fallo de registro antes del commit

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Repositorio fake falla antes de confirmar.
- **Datos:** Solicitud válida.
- **Acción:** Inscribir.
- **Resultado esperado:** Error controlado de registro.
- **Aserciones:** No QR ni correo; no interpretar fallo del repositorio como inscripción confirmada. Este unitario no prueba rollback real.


#### U078 — Correo construido después del registro

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Fake confirmar devuelve inscripción con id definitivo y snapshot conocido.
- **Datos:** Modificar solo campos devueltos de identificación del registro.
- **Acción:** Inscribir y observar mensaje.
- **Resultado esperado:** La confirmación usa el id/datos efectivamente confirmados.
- **Aserciones:** No usa un id provisional ni la entrada sin registrar.


#### U079 — Resultado rechazado consistente

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ResultadoInscripcion a través del servicio.
- **Preparación:** Solicitud sin términos.
- **Datos:** Inscribir.
- **Acción:** Inspeccionar la respuesta.
- **Resultado esperado:** Rechazo con error identificable y sin inscripción exitosa.
- **Aserciones:** No devolver simultáneamente éxito y error ni un id de reserva inexistente.


#### U080 — Resultado exitoso consistente

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ResultadoInscripcion a través del servicio.
- **Preparación:** Todas las colaboraciones del fixture completan correctamente.
- **Datos:** Inscribir una solicitud válida.
- **Acción:** Inspeccionar.
- **Resultado esperado:** Éxito con inscripción y datos tipados correctos.
- **Aserciones:** No hay errores de rechazo; no afirmar entrega SMTP real cuando se usa simulación.


#### U081 — Sin dependencia de una entrada al parque

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Ningún colaborador de compra o validación de entradas está configurado.
- **Datos:** Solicitud válida y contacto del fixture.
- **Acción:** Inscribir.
- **Resultado esperado:** Cumple el caso de uso sin entrada del parque.
- **Aserciones:** No consultar un servicio de entradas ni exigir idEntrada.


#### U082 — Estado aislado entre solicitudes

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion / GeneradorConfirmacion.
- **Preparación:** Dos solicitudes distintas procesadas con colaboradores controlados.
- **Datos:** Una de Safari y otra de Palestra, distintos participantes y turnos.
- **Acción:** Procesar ambas.
- **Resultado esperado:** Cada resultado y mensaje corresponden a su solicitud.
- **Aserciones:** No reutilizar participantes, términos o fecha de una solicitud anterior.


### 4.9 Confirmación QR y contrato de correo


#### U083 — Resumen contiene información registrada

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** GeneradorConfirmacion.generarMensaje.
- **Preparación:** Inscripción registrada conocida y generador QR stub.
- **Datos:** Actividad Palestra, 14/10/2026, 14:00, dos participantes.
- **Acción:** Generar un mensaje con política confirmada de distribución.
- **Resultado esperado:** Resumen con actividad, día, hora y las dos personas.
- **Aserciones:** Verificar valores relevantes; no una frase o HTML literal completo.


#### U084 — Cantidad y lista del resumen son coherentes

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** GeneradorConfirmacion.generarMensaje.
- **Preparación:** Registro de tres participantes.
- **Datos:** Generar resumen.
- **Acción:** Inspeccionar cantidad y personas.
- **Resultado esperado:** La cantidad es 3 y aparecen los tres, cada uno una vez.
- **Aserciones:** No depender de la cantidad declarada si difiere del registro; generar desde la inscripción confirmada.


#### U085 — Tipo e integridad del mensaje generado

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** GeneradorConfirmacion.generarMensaje.
- **Preparación:** Política de un mensaje y destinatario del fixture.
- **Datos:** Inscripción conocida.
- **Acción:** Generar un mensaje desde la inscripción confirmada.
- **Resultado esperado:** Un mensaje con destinatario/cuerpo/asunto de tipo cadena y adjunto QR en el tipo acordado.
- **Aserciones:** Campos necesarios no vacíos; QR no null. La representación PNG/bytes debe estar acordada en el adaptador.


#### U086 — Incorporación del QR al mensaje

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** GeneradorConfirmacion.generarMensaje.
- **Preparación:** GeneradorQR stub devuelve una imagen conocida.
- **Datos:** Inscripción registrada válida.
- **Acción:** Generar.
- **Resultado esperado:** El mensaje incorpora la imagen del QR recibida.
- **Aserciones:** No se pierde el adjunto ni se reemplaza por una etiqueta de texto.


#### U087 — Destinatario del mensaje

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo alternativa permitida contacto.email; no fija obligación de login.
- **Método o colaboración:** GeneradorConfirmacion / EmisorCorreo.
- **Preparación:** Destinatario fijo demo@example.com definido por el fixture.
- **Datos:** Inscripción con contacto del fixture.
- **Acción:** Generar y entregar al emisor.
- **Resultado esperado:** El destinatario coincide con el contacto resuelto.
- **Aserciones:** No enviar al participante equivocado. Comprobar un mensaje por inscripción, según respuesta 13 del grupo; no uno por participante.


#### U088 — QR con entrada ausente o de tipo incorrecto

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Contrato GeneradorQR.generar.
- **Preparación:** Contrato técnico de contenido de texto no vacío.
- **Datos:** Omisión/null/""; 12/false/[]/{}.
- **Acción:** Validar entrada del adaptador.
- **Resultado esperado:** Error controlado de contenido QR obligatorio o tipo inválido.
- **Aserciones:** No producir una imagen aparentemente válida para contenido ausente. Si se usa otro tipo de payload, ajustar el contrato.


#### U089 — Preservación del contenido entregado al QR

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** GeneradorConfirmacion con GeneradorQR spy.
- **Preparación:** Generador recibe un payload técnico de fixture conocido.
- **Datos:** Payload de ejemplo "eco-fixture-inscripcion-1".
- **Acción:** Generar.
- **Resultado esperado:** El colaborador recibe exactamente el contenido preparado.
- **Aserciones:** El ejemplo no define qué debe contener el QR de producción; eso espera al PO.


#### U090 — Mensaje sin campos necesarios

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Validador de mensaje / EmisorCorreoSimulado.enviar.
- **Preparación:** Mensaje de fixture válido; alterar un dato por vez.
- **Datos:** Destinatario/cuerpo/asunto omitido o vacío; QR omitido/null.
- **Acción:** Validar el contrato de salida antes de almacenarlo/enviarlo.
- **Resultado esperado:** Fallo de contrato controlado.
- **Aserciones:** No conservar ni enviar un mensaje incompleto. No exige una clase extra si lo garantiza el tipo construido.


#### U091 — Tipo de campos del mensaje

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Contrato de MensajeConfirmacion.
- **Preparación:** Mensaje válido salvo un campo.
- **Datos:** Destinatario/cuerpo/asunto como número, booleano, lista u objeto; QR en tipo no admitido por el adaptador.
- **Acción:** Construir o validar.
- **Resultado esperado:** TIPO_INVALIDO de salida o error de contrato.
- **Aserciones:** No aceptar un QR con el tipo correcto pero ausente; integridad de imagen se prueba en integración.


#### U092 — Fallo explícito del emisor

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** EmisorCorreo.enviar como adaptador aislado.
- **Preparación:** Emisor controlado configurado para fallar.
- **Datos:** Mensaje válido.
- **Acción:** Enviar.
- **Resultado esperado:** El adaptador comunica el fallo; no entrega un éxito falso.
- **Aserciones:** El destino de la reserva tras ese fallo se prueba después de respuesta del PO, no se presume rollback.


#### U093 — Consulta vacía de bandeja simulada

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** EmisorCorreoSimulado.listarMensajes.
- **Preparación:** Adaptador nuevo sin mensajes.
- **Datos:** Listar.
- **Acción:** Consultar la bandeja.
- **Resultado esperado:** Lista vacía.
- **Aserciones:** No aparecen confirmaciones de otras pruebas.


### 4.10 Contratos adicionales de agenda y datos externos


#### U094 — Duración de actividad y tipo del dato

- **Nivel y origen:** Unitario · R/T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Construcción o mapeo de Turno.
- **Preparación:** Una actividad dura una hora según el caso.
- **Datos:** duracionMinutos omitida/null; "60"/true/[]; 30/90; valor 60.
- **Acción:** Construir o mapear cada variante.
- **Resultado esperado:** Datos ausentes o mal tipados: CONFIGURACION_INVALIDA. Duración 30/90 no representa los turnos de este caso. Duración 60 es válida.
- **Aserciones:** No aceptar una actividad de otra duración ni permitir que la entrada del visitante cambie la duración.


#### U095 — Datos de servidor no son campos de la solicitud

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ValidadorEntrada / deserializador.
- **Preparación:** Propuesta técnica: rechazar campos que pretenden definir reglas o resultados del servidor.
- **Datos:** Añadir a la solicitud cuposDisponibles=99, edadMinima=0, requiereTalle=false o confirmada=true; variantes.
- **Acción:** Validar entrada antes de crear DTO.
- **Resultado esperado:** CAMPO_NO_ADMITIDO bajo este contrato técnico.
- **Aserciones:** No construir actividad/turno desde esos valores. Si el grupo elige ignorar extras en lugar de rechazarlos, documentar y probar que las reglas del servidor siguen intactas.



### 4.11 Ventana, calendario y agenda personal

#### U096 - Inscripción todavía no abierta

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / PoliticaTurnos.
- **Preparación:** Turno en miércoles abierto, cupos y participantes válidos; reloj fijo según variante.
- **Datos:** 24 h y 1 segundo antes.
- **Acción:** Evaluar disponibilidad temporal.
- **Resultado esperado:** Rechazo por inscripción no abierta.
- **Aserciones:** RECHAZO_SIN_EFECTOS.

#### U097 - Inscripción dentro del máximo de anticipación

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / PoliticaTurnos.
- **Preparación:** Turno en miércoles abierto, cupos y participantes válidos; reloj fijo según variante.
- **Datos:** 23 h 59 min 59 s antes.
- **Acción:** Evaluar disponibilidad temporal.
- **Resultado esperado:** Cumple condición temporal.
- **Aserciones:** No interpretar 24 h como mínimo; la frontera exacta de 24 h es válida.

#### U098 - Inscripción antes del cierre

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / PoliticaTurnos.
- **Preparación:** Turno en miércoles abierto, cupos y participantes válidos; reloj fijo según variante.
- **Datos:** 15 min y 1 segundo antes.
- **Acción:** Evaluar disponibilidad temporal.
- **Resultado esperado:** Cumple condición temporal.
- **Aserciones:** La frontera exacta de 15 min es válida.

#### U099 - Inscripción cerrada

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / PoliticaTurnos.
- **Preparación:** Turno en miércoles abierto, cupos y participantes válidos; reloj fijo según variante.
- **Datos:** 14 min 59 s antes.
- **Acción:** Evaluar disponibilidad temporal.
- **Resultado esperado:** Rechazo por inscripción cerrada.
- **Aserciones:** RECHAZO_SIN_EFECTOS.

#### U100 - Turno iniciado o pasado

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir / PoliticaTurnos.
- **Preparación:** Turno en miércoles abierto, cupos y participantes válidos; reloj fijo según variante.
- **Datos:** Inicio exactamente ahora y un minuto pasado, por separado.
- **Acción:** Evaluar disponibilidad temporal.
- **Resultado esperado:** Rechazo por inscripción cerrada.
- **Aserciones:** RECHAZO_SIN_EFECTOS.

#### U101 - Calendario real cerrado

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** PoliticaTurnos / ServicioInscripcion.
- **Preparación:** Reloj dentro de ventana; horario operativo y cupos válidos.
- **Datos:** Lunes 19/10/2026 09:00 con reloj domingo 18/10 10:00; 25/12/2026 11:00 con reloj 24/12 12:00; 01/01/2027 11:00 con reloj 31/12/2026 12:00.
- **Acción:** Evaluar cada fecha por separado.
- **Resultado esperado:** Rechazo por parque cerrado.
- **Aserciones:** No atribuir el rechazo a otro dato; RECHAZO_SIN_EFECTOS.

#### U102 - Persona con actividad simultánea

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir y consulta de registros previos.
- **Preparación:** Repositorio fake con persona ya inscripta en Safari el 14/10 14:00; clave de identidad documental bajo contrato explícito.
- **Datos:** Misma persona solicita Palestra el 14/10 14:00, edad/talle válidos.
- **Acción:** Inscribir.
- **Resultado esperado:** Rechazo por conflicto horario.
- **Aserciones:** Error identifica persona; no registro ni consumo nuevo. El fake no demuestra garantías concurrentes.

#### U103 - Persona en otro horario

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Registro previo a las 14:00, reloj 14/10 10:00, turno de las 15:00 habilitado.
- **Datos:** Misma persona en otra actividad a las 15:00.
- **Acción:** Inscribir.
- **Resultado esperado:** Sin rechazo por simultaneidad; éxito con lo demás válido.
- **Aserciones:** No prohibir todas las actividades del día.

#### U104 - Misma hora en otro día

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion.inscribir.
- **Preparación:** Registro del 14/10 14:00; reloj de nueva ejecución 15/10 10:00.
- **Datos:** Misma persona solicita 15/10 14:00.
- **Acción:** Inscribir.
- **Resultado esperado:** Sin conflicto horario; éxito si cumple lo demás.
- **Aserciones:** Comparar fecha e instante, no solo texto 14:00.

#### U105 - Un correo para la inscripción grupal

- **Nivel y origen:** Unitario · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion / GeneradorConfirmacion.generarMensaje.
- **Preparación:** Inscripción válida de tres personas y bandeja controlada; contenido QR técnico de prueba.
- **Datos:** Contacto demo@example.com.
- **Acción:** Confirmar y consultar mensaje.
- **Resultado esperado:** Un mensaje para esa inscripción con las tres personas.
- **Aserciones:** No un mensaje por participante; no deducir cantidad de QR de esa distribución.

#### U106 - Agenda operativa y disponibilidad actual

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioConsultaActividades.consultarTurnos.
- **Preparación:** Día abierto con agenda 09:00-17:00 y reloj controlado.
- **Datos:** Turnos operativos pasados, dentro de ventana y posteriores a 24 h según fixture.
- **Acción:** Consultar disponibilidad.
- **Resultado esperado:** Turnos inscribibles diferenciados de operativos; motivo o filtrado según contrato UI que se adopte.
- **Aserciones:** No mostrar todo turno operativo como reservable; este caso no obliga a mostrar nueve botones.

#### U107 - Instantes equivalentes con diferentes offsets

- **Nivel y origen:** Unitario · T.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** PoliticaTurnos.
- **Preparación:** Contrato técnico de comparación por instantes; zona operativa D01 todavía por acordar.
- **Datos:** Turno 2026-10-14T14:00-03:00 y 2026-10-14T17:00Z; ahora 2026-10-14T13:00Z.
- **Acción:** Comparar ambas representaciones.
- **Resultado esperado:** Misma decisión temporal: dentro de ventana.
- **Aserciones:** No comparar cadenas ni horas locales sin offset; no convertir D01 en una regla ya aprobada.


<a id="5-tests-de-integracion"></a>

## 5 Escenarios de integración planificados

Se prueban colaboraciones reales sin frontend. La siguiente preparación abreviada se aplica salvo indicación contraria: **BASE_REAL** = ServicioInscripcion + validadores + entidades reales + repositorio temporal del producto + reloj fijo + QR real + emisor simulado real; agenda y términos de fixture. Los casos de transacción y concurrencia usan el motor/adaptador real elegido.


### 5.1 Casos completos de negocio


#### I001 — Inscripción válida en cada actividad

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → repositorio → QR → emisor.
- **Preparación:** BASE_REAL; ocupación inicial 0 en cada turno.
- **Datos:** Una persona en Safari/Jardinería sin talle; en Palestra edad 12 y XS; en Tirolesa edad 8 y XXL.
- **Acción:** Inscribir cada actividad en un fixture independiente.
- **Resultado esperado:** EXITO_REGISTRADO; lugar disponible disminuye en 1.
- **Aserciones:** Recuperar inscripción, comprobar todos sus datos, recuperar mensaje y decodificar QR de fixture. Sin navegador.


#### I002 — Inscripción grupal válida

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y adaptadores reales.
- **Preparación:** BASE_REAL; Safari S1 con 8 libres.
- **Datos:** Solicitud coherente para 3 personas válidas sin talle.
- **Acción:** Inscribir y volver a consultar.
- **Resultado esperado:** Una nueva inscripción de 3 personas; quedan 5 lugares.
- **Aserciones:** Las tres personas están persistidas; el resumen contiene a las tres; envío según política del fixture.


#### I003 — Reserva exacta de los lugares restantes

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → confirmar.
- **Preparación:** BASE_REAL; S1 con 6 personas previamente inscriptas y 2 libres.
- **Datos:** Solicitud válida para 2.
- **Acción:** Inscribir.
- **Resultado esperado:** Se acepta; 8 ocupados y 0 libres.
- **Aserciones:** No hay sobreocupación; registro de dos participantes completo.


#### I004 — Grupo mayor que los lugares restantes

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → confirmar.
- **Preparación:** BASE_REAL; S1 con 2 libres y snapshot de registros.
- **Datos:** Solicitud coherente para 3 participantes válidos.
- **Acción:** Inscribir.
- **Resultado esperado:** SIN_CUPOS y rechazo de todo el grupo.
- **Aserciones:** RECHAZO_SIN_EFECTOS; no guardar solamente dos participantes.


#### I005 — Turno agotado

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → repositorio.
- **Preparación:** BASE_REAL; S1 con 8 ocupados.
- **Datos:** Solicitud para 1 persona.
- **Acción:** Inscribir.
- **Resultado esperado:** SIN_CUPOS.
- **Aserciones:** RECHAZO_SIN_EFECTOS.


#### I006 — Actividad sin requisito de talle

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y validación real.
- **Preparación:** BASE_REAL; Safari y Jardinería en fixtures independientes.
- **Datos:** Talle omitido y null, con el resto válido.
- **Acción:** Inscribir cada variante.
- **Resultado esperado:** Inscripción válida.
- **Aserciones:** Registro y mensaje correctos; no se inventa una talla.


#### I007 — Actividad con requisito de talle

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y validación real.
- **Preparación:** BASE_REAL; un solo participante válido por edad.
- **Datos:** Palestra y Tirolesa: talle omitido/null/vacío.
- **Acción:** Inscribir cada variante.
- **Resultado esperado:** TALLE_REQUERIDO.
- **Aserciones:** RECHAZO_SIN_EFECTOS.


#### I008 — Términos sin aceptar

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y validación real.
- **Preparación:** BASE_REAL; turno y participante válidos.
- **Datos:** aceptados=false, objeto de aceptación ausente y aceptación de otra actividad; variantes.
- **Acción:** Inscribir.
- **Resultado esperado:** Rechazo por aceptación.
- **Aserciones:** RECHAZO_SIN_EFECTOS; el servicio no depende de que una pantalla impida el envío.


#### I009 — Edad inmediatamente inferior al mínimo

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y Actividad.
- **Preparación:** BASE_REAL; un participante y talle válido.
- **Datos:** Palestra 11; Tirolesa 7.
- **Acción:** Inscribir cada variante.
- **Resultado esperado:** EDAD_INSUFICIENTE.
- **Aserciones:** RECHAZO_SIN_EFECTOS.


#### I010 — Edad exactamente igual al mínimo

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y Actividad.
- **Preparación:** BASE_REAL; talle válido y disponibilidad.
- **Datos:** Palestra 12; Tirolesa 8.
- **Acción:** Inscribir.
- **Resultado esperado:** EXITO_REGISTRADO.
- **Aserciones:** No excluir el límite por una comparación estricta incorrecta.


#### I011 — Actividad no disponible o parque cerrado

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → PoliticaTurnos.
- **Preparación:** BASE_REAL; cupos presentes y horario válido.
- **Datos:** Actividad/turno no habilitado; lunes; 25/12; 01/01. Una causa por variante, con los demás datos válidos y reloj dentro de ventana.
- **Acción:** Inscribir cada causa por separado.
- **Resultado esperado:** Rechazo por disponibilidad.
- **Aserciones:** RECHAZO_SIN_EFECTOS. Calendario confirmado: lunes, 25/12 y 01/01. Reloj dentro de ventana para aislar el cierre.


#### I012 — Fuera de los horarios publicados

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y agenda real.
- **Preparación:** BASE_REAL con turno inválido por agenda, pero todavía futuro y dentro de la ventana respecto de un reloj propio por variante. No usar ahora=10:00 con inicio=08:00, que probaría además un turno pasado.
- **Datos:** Intentar turno de 18:00 o identificador de turno que no está publicado.
- **Acción:** Inscribir.
- **Resultado esperado:** Rechazo por horario no disponible o turno inexistente.
- **Aserciones:** No registrar una actividad que termine fuera de la agenda por aceptar inicio 18:00.


#### I013 — Referencias inexistentes e incoherentes

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y repositorio real.
- **Preparación:** BASE_REAL con actividades y turnos conocidos.
- **Datos:** Actividad inexistente; turno inexistente; Palestra con S1.
- **Acción:** Inscribir cada variante.
- **Resultado esperado:** Error de referencia correspondiente.
- **Aserciones:** RECHAZO_SIN_EFECTOS; no usar datos enviados para fabricar una actividad nueva.


### 5.2 Datos registro y aislamiento


#### I014 — Datos ausentes no alcanzan la escritura

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Adaptador de entrada → servicio → repositorio.
- **Preparación:** BASE_REAL y adaptador real de entrada.
- **Datos:** Eliminar de una solicitud de participante único: actividadId, turnoId, cantidad, lista, nombre, DNI, edad; casos separados.
- **Acción:** Enviar por el límite de aplicación.
- **Resultado esperado:** Error de datos o rechazo correspondiente.
- **Aserciones:** ERROR_DE_DATOS y RECHAZO_SIN_EFECTOS; no registrar entidades incompletas.


#### I015 — Tipos inválidos no alcanzan la escritura

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Adaptador de entrada → servicio.
- **Preparación:** BASE_REAL. Reutilizar variantes de tipos unitarias en el límite real.
- **Datos:** Edad como "12"/false; cantidad como "2"/true; lista como objeto; nombre/DNI como número; aceptación como "false"; talle como objeto.
- **Acción:** Enviar cada variante por separado.
- **Resultado esperado:** TIPO_INVALIDO antes del registro.
- **Aserciones:** RECHAZO_SIN_EFECTOS; no un error técnico inesperado ni coerción que acepte el caso.


#### I016 — Cantidad y lista inconsistentes

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → repositorio real.
- **Preparación:** BASE_REAL con cupos suficientes.
- **Datos:** 3 declarados y 2 personas; 2 declarados y 3 personas.
- **Acción:** Inscribir cada variante.
- **Resultado esperado:** Rechazo de consistencia.
- **Aserciones:** No guardar lista truncada o completada artificialmente; cupos sin cambios.


#### I017 — Conservación de datos al recuperar

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Repositorio real y generación de resumen.
- **Preparación:** BASE_REAL. Nombre Unicode y DNI de texto en fixture.
- **Datos:** Nombre "María José O’Connor"; DNI "00123456"; edad 24; talle M.
- **Acción:** Inscribir, recuperar registro y generar/rescatar confirmación.
- **Resultado esperado:** Nombre y DNI preservados; tipos canónicos en registro; talle correcto.
- **Aserciones:** No perder ceros, acentos, apóstrofe, edad ni asociación al turno.


#### I018 — Inscripciones sucesivas

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y repositorio real.
- **Preparación:** BASE_REAL; S1 inicialmente con 8 libres.
- **Datos:** Inscribir 2; luego 3.
- **Acción:** Consultar después de cada operación.
- **Resultado esperado:** Libres 6 y luego 3.
- **Aserciones:** Dos inscripciones distintas y cinco personas nuevas; no doble descuento.


#### I019 — Aislamiento entre turnos y fechas

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Repositorio real y consulta real.
- **Preparación:** BASE_REAL con S1/S2 el 14/10 y S3 el 15/10. Usar reloj 14/10 10:00 al inscribir S1/S2 y 15/10 10:00 para S3.
- **Datos:** Inscribir 2 en S1.
- **Acción:** Consultar S1, S2 y S3.
- **Resultado esperado:** S1 queda en 6; S2 y S3 en 8 bajo la unidad de cupos del fixture.
- **Aserciones:** No consumir lugares de otro horario ni otro día; ajustar el reloj al día de cada inscripción para respetar la ventana.


#### I020 — Identidad única de registros y mensajes

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Repositorio real → GeneradorConfirmacion → emisor.
- **Preparación:** BASE_REAL con turnos suficientes.
- **Datos:** Dos solicitudes válidas con participantes distintos.
- **Acción:** Confirmar y recuperar ambas por identificador.
- **Resultado esperado:** Identificadores distintos y mensajes asociados al registro correcto.
- **Aserciones:** No imponer formato UUID; no recuperar datos de la otra inscripción.


#### I021 — Consulta no consume lugares

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioConsultaActividades y repositorio real.
- **Preparación:** BASE_REAL; snapshot de ocupación y registros.
- **Datos:** Listar actividades, consultar turnos y obtener términos repetidamente.
- **Acción:** Comparar estado antes y después.
- **Resultado esperado:** Solo lecturas; mismo estado.
- **Aserciones:** No reservar lugares por consultar ni por leer la disponibilidad.


#### I022 — Capacidad conocida sin máximo global de diez

- **Nivel y origen:** Integración · R (FAQ 31).
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion → repositorio real.
- **Preparación:** BASE_REAL; P1 con 12 libres.
- **Datos:** Grupo coherente de 11 adultos con talles válidos.
- **Acción:** Inscribir.
- **Resultado esperado:** No rechazo por máximo global de 10; queda 1 lugar bajo fixture.
- **Aserciones:** Comprobar todos los participantes registrados; la FAQ 31 confirma que no existe un máximo adicional por solicitud.


#### I023 — Caso de uso sin entrada al parque

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y sus adaptadores reales.
- **Preparación:** BASE_REAL sin servicio o tabla de compra de entradas requerida por el flujo.
- **Datos:** Solicitud válida para S1.
- **Acción:** Inscribir.
- **Resultado esperado:** EXITO_REGISTRADO.
- **Aserciones:** Ningún requisito idEntrada, pago, pase VIP o Mercado Pago.


### 5.3 Atomicidad y actualización de disponibilidad


#### I024 — Fallo antes de confirmar el registro

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Repositorio real → servicio.
- **Preparación:** BASE_REAL; inyección de fallo en el adaptador antes de completar la escritura.
- **Datos:** Solicitud válida y snapshot de estado.
- **Acción:** Ejecutar con fallo.
- **Resultado esperado:** Error de registro controlado.
- **Aserciones:** Sin filas/objetos nuevos, mismos lugares y sin mensajes. No basta un fake que solo lanza una excepción.


#### I025 — Fallo durante registro de participantes

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Transacción del repositorio real.
- **Preparación:** Fixture con escritura de inscripción y varios participantes en una unidad de trabajo.
- **Datos:** Fallar después de una escritura intermedia, por ejemplo el segundo participante.
- **Acción:** Intentar confirmar el grupo.
- **Resultado esperado:** Rollback completo.
- **Aserciones:** No inscripción huérfana, no primer participante guardado y no consumo parcial. Si el esquema usa una sola escritura, inducir el fallo en la operación equivalente.


#### I026 — Fallo entre registro y actualización de cupos

- **Nivel y origen:** Integración · T.
- **Estado:** Adaptar al modelo de almacenamiento elegido.
- **Método o colaboración:** Unidad indivisible del repositorio real.
- **Preparación:** Solo si ocupación se almacena en un contador separado.
- **Datos:** Fallo tras escribir inscripción pero antes de actualizar contador, y variante inversa.
- **Acción:** Confirmar con fallo.
- **Resultado esperado:** Rollback de ambas escrituras.
- **Aserciones:** No divergencia entre inscripciones y ocupación. Si cupos se derivan de inscripciones, reemplazar por fallo antes del commit de esas inscripciones.


#### I027 — Disponibilidad obsoleta al confirmar

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Repositorio real y servicio.
- **Preparación:** S1 con 2 libres; obtener una consulta inicial; otra inscripción real consume 1.
- **Datos:** Primera solicitud necesita 2.
- **Acción:** Confirmar usando el estado actual del repositorio.
- **Resultado esperado:** SIN_CUPOS; se conserva solo la inscripción intermedia.
- **Aserciones:** No confiar en el cupo mostrado anteriormente ni superar capacidad.


#### I028 — Dos solicitudes simultáneas por el último lugar

- **Nivel y origen:** Integración · T.
- **Estado:** Recomendado si el adaptador soporta solicitudes concurrentes; alcance de demostración por confirmar.
- **Método o colaboración:** Servicio y repositorio real con concurrencia soportada.
- **Preparación:** S1 con 1 libre. Dos conexiones/contextos reales; barrera controlada antes de confirmar.
- **Datos:** Dos solicitudes distintas de 1 persona, válidas.
- **Acción:** Lanzar ambas sin sleeps arbitrarios y esperar finalización.
- **Resultado esperado:** Como máximo una se confirma; en ejecución sin otros fallos, una acepta y otra rechaza por cupos.
- **Aserciones:** Ocupación final 8, una inscripción nueva y mensajes solo para la aceptada. Este test técnico no define la política de DNI duplicado.


#### I029 — Rechazo no inutiliza el siguiente registro

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Servicio y repositorio real.
- **Preparación:** Una operación rechazada por cupos o una transacción revertida; luego un turno con capacidad.
- **Datos:** Primera solicitud inválida; segunda válida e independiente.
- **Acción:** Procesar en secuencia.
- **Resultado esperado:** La primera no altera estado; la segunda puede confirmar normalmente.
- **Aserciones:** No quedar con transacción abierta, bloqueo residual o datos de la solicitud anterior.


### 5.4 QR real y correo simulado


#### I030 — QR real codifica el contenido recibido

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** GeneradorQR real y decodificador independiente.
- **Preparación:** Biblioteca real del proyecto; payload técnico no vacío.
- **Datos:** "eco-fixture-inscripcion-1" y una cadena Unicode de fixture.
- **Acción:** Generar imagen y decodificarla.
- **Resultado esperado:** Contenido recuperado igual al original.
- **Aserciones:** Imagen no vacía y en formato admitido. No comparar bytes exactos de imagen: distintos codificadores pueden generar imágenes equivalentes.


#### I031 — QR adjunto en confirmación puede decodificarse

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Registro real → confirmación → QR real → bandeja.
- **Preparación:** BASE_REAL; payload técnico preparado con una política de prueba.
- **Datos:** Inscribir una solicitud válida.
- **Acción:** Recuperar mensaje y decodificar su QR, no el QR de un test aislado.
- **Resultado esperado:** El adjunto codifica el contenido preparado para esa inscripción del fixture.
- **Aserciones:** Un correo por inscripción y adjunto QR recuperable y decodificable. Sin adjunto corrupto ni QR de otro registro. Cantidad/payload de producción pendiente Q03; usar contrato técnico de prueba explícito.


#### I032 — Bandeja simulada conserva el mensaje completo

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** EmisorCorreoSimulado real.
- **Preparación:** Adaptador nuevo. Mensaje con destinatario, asunto, cuerpo y QR válidos.
- **Datos:** Enviar uno y luego otro mensaje distintos.
- **Acción:** Listar y recuperar cada detalle.
- **Resultado esperado:** Dos mensajes identificables con sus propios datos.
- **Aserciones:** No mezclar destinatarios ni adjuntos; no modificar el mensaje original al guardar.


#### I033 — Confirmación contiene todos los datos registrados

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Servicio → registro → generador → bandeja.
- **Preparación:** BASE_REAL con grupo de 3 participantes.
- **Datos:** Actividad, fecha, hora y personas conocidas.
- **Acción:** Inscribir y recuperar el mensaje.
- **Resultado esperado:** Resumen correcto de la inscripción guardada.
- **Aserciones:** Cantidad 3; tres personas; mismo día, hora y actividad. No exigir que el correo incluya DNI si el PO no lo pidió.


#### I034 — Registro fallido no deja correo de éxito

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Servicio → repositorio real → bandeja.
- **Preparación:** BASE_REAL; fallo de escritura antes del commit.
- **Datos:** Solicitud válida.
- **Acción:** Inscribir y consultar la bandeja.
- **Resultado esperado:** Error de registro y ningún mensaje nuevo.
- **Aserciones:** Se verifica estado real de bandeja y repositorio; el mensaje no se publica antes de confirmar el registro.


#### I035 — Mensajes independientes entre actividades

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** BASE_REAL y bandeja real.
- **Preparación:** Una inscripción Safari y otra Palestra.
- **Datos:** Personas, horario o fecha diferentes.
- **Acción:** Confirmar ambas y recuperar confirmaciones.
- **Resultado esperado:** Cada mensaje refleja únicamente su inscripción.
- **Aserciones:** No contaminación entre llamadas ni términos/edad/talle ajenos.


### 5.5 Casos adicionales de contrato y consulta


#### I036 — Forma de la solicitud rechazada en el límite real

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Adaptador de entrada → servicio real.
- **Preparación:** BASE_REAL; adaptador de entrada real si existe.
- **Datos:** Solicitud null, lista, cadena o booleano; un valor por ejecución.
- **Acción:** Enviar cada variante al límite sin tipar.
- **Resultado esperado:** Error estructural controlado antes del caso de uso.
- **Aserciones:** ERROR_DE_DATOS y RECHAZO_SIN_EFECTOS. En lenguaje estático, ejecutar desde el deserializador, no llamar al servicio con un tipo imposible.


#### I037 — Cantidad no válida en el caso de uso integrado

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Adaptador → validación real → repositorio.
- **Preparación:** BASE_REAL con lugares disponibles.
- **Datos:** Cantidad 0, -1, 1.5; lista de participantes válida y cada variante aislada.
- **Acción:** Inscribir a través del límite real.
- **Resultado esperado:** Cantidad inválida; nunca un registro válido.
- **Aserciones:** RECHAZO_SIN_EFECTOS. Aunque también exista inconsistencia de lista, debe quedar identificado el campo de cantidad.


#### I038 — Consulta filtra actividad y fecha

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** ServicioConsultaActividades y repositorio real.
- **Preparación:** Sembrar S1 y S2 en 16/10, S3 en 17/10 y P1 en 16/10.
- **Datos:** Consultar Safari para 2026-10-16.
- **Acción:** Recuperar turnos.
- **Resultado esperado:** Se obtienen S1 y S2; no S3 ni P1.
- **Aserciones:** Identidades, actividad, fecha y cupos coinciden con el registro. La lectura no cambia ocupación.


#### I039 — Reglas autoritativas frente a campos extra

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado bajo el contrato técnico propuesto; no implementado ni ejecutado.
- **Método o colaboración:** Adaptador real → servicio → repositorio.
- **Preparación:** BASE_REAL; propuesta técnica de rechazo de campos de servidor.
- **Datos:** Solicitar Palestra con edad 11 y añadir edadMinima=0; o turno lleno con cuposDisponibles=99.
- **Acción:** Enviar cada solicitud desde el límite.
- **Resultado esperado:** CAMPO_NO_ADMITIDO bajo contrato estricto; no se confirma.
- **Aserciones:** RECHAZO_SIN_EFECTOS. Si se elige ignorar extras, el rechazo debe ser por edad/cupos reales; nunca éxito por valores del cliente.



### 5.6 Reglas confirmadas incorporadas

#### I040 - Ventana de inscripción integrada

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y repositorio real.
- **Preparación:** Almacenamiento nuevo por variante, turno de día abierto, cupos y datos válidos.
- **Datos:** 24 h + 1 s: rechazo; 23 h 59 min 59 s: aceptación; 15 min + 1 s: aceptación; 14 min 59 s: rechazo.
- **Acción:** Inscribir en ejecuciones separadas.
- **Resultado esperado:** Decisión según cada variante; instantes exactos no se fijan.
- **Aserciones:** RECHAZO_SIN_EFECTOS o EXITO_REGISTRADO. Ajustar reloj, no fecha de otra regla.

#### I041 - Calendario cerrado en el servicio real

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y consulta de turnos.
- **Preparación:** Fixtures U101 con reloj dentro de ventana y almacenamiento nuevo por variante.
- **Datos:** Lunes, 25/12 y 01/01.
- **Acción:** Consultar e intentar inscripción directa evitando UI.
- **Resultado esperado:** Fecha no inscribible y rechazo por parque cerrado.
- **Aserciones:** RECHAZO_SIN_EFECTOS; UI no es la única protección.

#### I042 - Conflicto con inscripción previa

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y repositorio real.
- **Preparación:** Registrar persona válida en Safari 14/10 14:00; reloj fijo 10:00; identidad bajo contrato técnico declarado.
- **Datos:** Misma persona intenta Palestra 14/10 14:00 con edad/talle válidos.
- **Acción:** Inscribir segunda actividad.
- **Resultado esperado:** Segunda rechazada por simultaneidad.
- **Aserciones:** Primera recuperable; cupos y mensajes de Palestra sin cambios.

#### I043 - Actividades consecutivas permitidas

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y repositorio real.
- **Preparación:** Persona registrada a las 14:00; reloj 10:00.
- **Datos:** Otra actividad a las 15:00, requisitos válidos.
- **Acción:** Inscribir segunda actividad.
- **Resultado esperado:** Ambas confirmadas.
- **Aserciones:** Registros, cupos y correo independientes; no conflicto artificial por compartir día.

#### I044 - Misma hora en días diferentes

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion y repositorio real.
- **Preparación:** Registrar 14/10 14:00 con reloj 14/10 10:00; para segunda usar reloj 15/10 10:00.
- **Datos:** Misma persona, turno 15/10 14:00.
- **Acción:** Inscribir y recuperar ambas.
- **Resultado esperado:** Ambas válidas; no hay simultaneidad.
- **Aserciones:** Cada fecha conserva sus cupos; no reutilizar reloj fuera de ventana.

#### I045 - Solicitudes concurrentes para una persona

- **Nivel y origen:** Integración · T.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** Límite de confirmación del repositorio real.
- **Preparación:** Mismo almacenamiento, mismo día/horario, dos actividades con cupo; identidad explícita y sincronización controlada.
- **Datos:** Misma persona en dos solicitudes simultáneas.
- **Acción:** Confirmar ambas mediante barrera, no sleeps.
- **Resultado esperado:** Como máximo una inscripción confirmada.
- **Aserciones:** Solo una consume cupos y produce confirmación; no validar solo con fake ni proteger cupos ignorando identidad.

#### I046 - Correo grupal único con archivo adjunto

- **Nivel y origen:** Integración · R.
- **Estado:** Planificado documentalmente; no implementado ni ejecutado.
- **Método o colaboración:** ServicioInscripcion, QR real y bandeja simulada.
- **Preparación:** Grupo válido de tres personas, contrato de QR de prueba declarado y contacto resuelto.
- **Datos:** Inscripción confirmada.
- **Acción:** Recuperar correo y su adjunto por interfaces públicas.
- **Resultado esperado:** Un mensaje con resumen de las tres personas y adjunto QR decodificable.
- **Aserciones:** Adjunto corresponde a ese registro; contrato técnico no resuelve Q03. Presentación en otra ventana corresponde a F13.


<a id="6-consultas-y-decisiones"></a>

## 6 Consultas abiertas y decisiones técnicas

Q03-Q12 son el registro único de preguntas abiertas del PO. Las fronteras Q01 y Q02 fueron confirmadas como inclusivas el 14/10/2026. La zona y la persistencia son decisiones técnicas por acordar, no preguntas de negocio enteramente desconocidas. El documento CONSULTAS_Y_VALIDACION_PO.md incluye fuentes, respuestas confirmadas y acta de validación.

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

### Decisiones del equipo

| ID | Decisión por acordar | Restricción que debe respetar |
|---|---|---|
| D01 | Zona horaria del parque y representación de fechas/instantes | El prototipo usa `America/Argentina/Buenos_Aires` para demostrar la ventana de inscripción. La decisión técnica definitiva para la futura implementación sigue pendiente. |
| D02 | Almacenamiento y conservación al reiniciar | Cupos, registros y conflictos consistentes. Aclarar al PO el comportamiento observable de reinicio si forma parte de la entrega. No elegir aún BD. |
| D03 | Contrato de reintento, idempotencia y recuperación de mensajes | Evitar consumo duplicado por reintento técnico; respetar resultado visible acordado en Q11. |
| D04 | Mecanismo de atomicidad y demostración de concurrencia | No superar capacidad ni confirmar dos actividades simultáneas para una persona. Los dobles no demuestran garantías del adaptador real. |

### Correspondencia con los antiguos pendientes

| ID anterior | Estado actual |
|---|---|
| P001 | Resuelto: la inclusión de 24 h exactas está confirmada. |
| P002/P003/P004 | Resueltos: U096/U097/U100 e I040. |
| P005 | Calendario y ventana confirmados; U101/I041. Representación de agenda, decisión del equipo. |
| P006 | D01: zona y representación temporal; U107 como contrato técnico propuesto. |
| P007/P008 | Q07/Q06: referencia de edad y DNI. |
| P009/P010 | Q04/Q05: duplicación de reserva y dentro del grupo. |
| P011 | Resuelto: U102-U104 e I042-I045. |
| P012/P013/P014 | Q09/Q08: tratamiento del grupo y aceptación. |
| P015 | Campo o sesión permitidos; elegir alternativa visual. |
| P016 | Resuelto: un correo por inscripción, U105/I046. |
| P017 | Q03: cantidad y contenido QR. |
| P018/P019 | Q11/Q10: fallos de confirmación posteriores al registro. |
| P020/P021 | D03/D02 y resultado visible Q11: reintentos y conservación. |
| P022 | Resuelto: sin máximo adicional; U071/I022. |
| Nueva consulta | Resuelto: la inclusión de 15 min exactos está confirmada; Q12: momento del aviso de cupos. |

<a id="7-trazabilidad-y-ejecucion-con-tdd"></a>

## 7 Trazabilidad y ejecución con TDD

### Cobertura de las seis pruebas de usuario del TP6

| Criterio del enunciado | Integración sin frontend |
|---|---|
| Inscripción válida con confirmación y QR | I001, I002, I030, I031, I033. |
| Actividad sin cupos | I003, I004, I005. |
| Sin talle porque no corresponde | I006. |
| Parque cerrado o actividad no disponible | I011, I012. |
| Sin aceptar términos | I008. |
| Falta talle requerido | I007. |

### Orden práctico para la etapa futura

1. Un primer comportamiento de inscripción válida observable y recuperable.
2. Cupos exactos e insuficientes, con un comportamiento por ciclo.
3. Presencia/tipos en el límite, datos de cada persona, edad, talle y términos.
4. Calendario y ventana temporal con relojes válidos y causas aisladas.
5. Conflictos personales, registro real, atomicidad y concurrencia.
6. QR real y correo simulado; consulta del mensaje y adjunto.
7. Resolver Q03-Q12 e incorporar cada respuesta con su escenario antes de programarla.
8. Complementar con interfaz y aceptación del plan F01-F13.

En cada ciclo futuro: test que falle por la causa prevista, código mínimo para pasar y refactor con verificaciones en verde. No escribir toda la suite ni todas las clases primero. Esta etapa es solo documental; no se ejecutó ninguno de estos casos.

### Ejemplo detallado de un rechazo de cupos

```text
Dado S1 con capacidad 8 y seis personas registradas
y una solicitud válida para tres participantes distintos
y términos aceptados y contacto resuelto en el fixture

Cuando ServicioInscripcion.inscribir(solicitud) termina

Entonces el resultado es rechazo por SIN_CUPOS
y al consultar S1 siguen existiendo dos lugares disponibles
y la cantidad de inscripciones y participantes no aumenta
y no aparece un mensaje nuevo en la bandeja simulada
y ninguna persona de ese grupo se inscribe parcialmente
```

### Criterios para verificar la suite

- Tests independientes; los fixtures se crean y limpian por ejecución.
- Listas de variantes se ejecutan parametrizadas, no como una sola prueba que se detiene en el primer dato.
- Solo alterar el dato del caso y mantener válidos los demás.
- Los rechazos verifican errores y efectos; los éxitos verifican lectura posterior y diferencia exacta de cupos.
- No afirmar atomicidad por tener mocks: ejecutar el rollback sobre el adaptador real.
- Concurrencia mediante barreras o sincronización controlada; no sleeps como garantía.
- No comparar bytes exactos del QR ni exigir el texto completo del correo si no es parte del contrato.
- No exigir 100% de cobertura de líneas como sustituto de las reglas del dominio.
- Mantener identificadas las decisiones técnicas y los casos pendientes del PO.
- Los casos de transporte JSON, contacto, almacenamiento y concurrencia se adaptan al producto elegido; no obligan a introducir una API HTTP, login o base de datos específica.

### Fuentes y documentos relacionados

- PLAN_CLASES_METODOS_Y_ESCENARIOS.md: arquitectura orientativa, A01-H05, interfaz y trazabilidad.
- ISW_TP_ENUN_6.pdf, pp.1 y 3: TDD, US y seis pruebas de usuario.
- ISW_TP_ENUN_2.pdf, p.2: dominio y flujo de inscripción.
- FAQ PO TDD EcoHarmony Park -US_inscribirme_a_actividad.pdf, pp.1-4: reglas confirmadas; preguntas 1, 12-16, 23, 28-32.
- TP #6 ISW - G2.pdf, pp.1-3: respuestas específicas, correo separado, distribución y cupos.
- CONSULTAS_Y_VALIDACION_PO.md: Q03-Q12 y D01-D04, registro único de pendientes.
- CONVENCION_ESTILOS.md: Java/JavaScript, AAA, nomenclatura y accesibilidad.

La referencia anterior a ISW_TP_RES_02_G04.pdf se retiró como evidencia: no está disponible entre los adjuntos. No usarla para afirmar un rechazo de duplicados confirmado.

### Cantidad y estado

107 escenarios unitarios U001-U107 y 46 de integración I001-I046: 153 escenarios de backend documentados. Los IDs originales se conservan. Q03-Q12 son 10 consultas abiertas; D01-D04 son cuatro decisiones técnicas. Las variantes parametrizadas pueden producir más ejecuciones y varios casos dependen de adoptar el contrato técnico correspondiente. Ninguno está implementado ni ejecutado. El catálogo backend se complementa con los escenarios UI del plan; no reemplaza ese recorrido.
