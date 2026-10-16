# Guía de trabajo TDD con asistentes LLM

EcoHarmony Park | TP6 | US «Inscribirme a actividad»

Esta guía fija la dinámica que deben seguir los LLM que colaboren con el equipo. El mapa de reglas y escenarios está en [PLAN_TDD.md](PLAN_TDD.md); esta guía define cómo trabajar cada iteración y cómo dejar evidencia.

## Acuerdos del equipo

- El usuario indica qué casos se trabajarán. El LLM no agrega, cambia ni adelanta casos por iniciativa propia.
- **Se trabaja de a un test por iteración.** El usuario puede elegir un grupo de casos relacionados, pero se recorren de a uno: cada test atraviesa su propio ciclo completo antes de escribir el siguiente. No se escriben varios tests juntos ni se implementa la solución final de una sola vez.
- Cada iteración sigue este orden: **RED → commit del usuario → GREEN → commit del usuario → REFACTOR → commit del usuario**.
- El usuario ejecuta todos los commits. El LLM no hace git add, git commit, git push, reset, checkout ni reescribe el historial.
- Al terminar cada fase, el LLM informa los archivos y resultados y se detiene. Solo pasa a la fase siguiente cuando el usuario lo indique.
- No se empieza la iteración siguiente hasta que el usuario lo indique.
- No se cambia de repositorio, rama ni configuración Git. El repositorio acordado es [ConstantinoBottero/TP-ISW-TDD](https://github.com/ConstantinoBottero/TP-ISW-TDD); se respeta el checkout que ya preparó el usuario.

## Por qué iteraciones de un test

El objetivo del TP es mostrar el proceso, no llegar rápido al código final. Un verde que ya resuelve todos los casos previsibles deja la fase de refactor sin contenido y convierte el ciclo en un trámite.

Por eso el verde usa **el código más chico que haga pasar ese test, incluidos valores fijos escritos a mano**. El test siguiente elige datos que ese valor fijo no puede satisfacer y obliga a generalizarlo. Esa progresión —fijar primero, generalizar cuando un caso lo exige— es la que deja refactors reales y un historial donde se ve avanzar el diseño.

Antes de empezar un grupo de casos conviene anticipar, caso por caso, qué valor fijo introduce cada uno y cuál elimina. Si un caso no elimina ninguno y tampoco falla, no es una iteración: ver «Tests que pasan al escribirse».

## Antes de cada iteración

1. Leer el caso que indicó el usuario y mapearlo a su ID del plan y a un seam público acordado.
2. Revisar las respuestas pendientes al PO en [CONSULTAS_Y_VALIDACION_PO.md](CONSULTAS_Y_VALIDACION_PO.md). No inventar una regla para resolver una consulta abierta.
3. Mantener una sola causa de rechazo por test. Usar datos ficticios y concretos.
4. Si no está claro qué comportamiento se espera, preguntar antes de codificarlo.
5. No ampliar el alcance a otros tests, reglas, capas, interfaz o integraciones.

El backend usa Java 17, Maven Wrapper y JUnit Jupiter. Se ejercitan los seams S1, S2 y S3 descritos en PLAN_TDD.md; no se testean auxiliares privados directamente. Para el tiempo, se usa java.time.Clock fijo. Se conserva el repositorio en memoria real; no se mockean clases propias. El prototipo HTML/CSS/JavaScript es independiente del backend y no se integra como parte de una iteración salvo que el usuario lo pida.

## Fase RED

- Añadir **un solo test**, con nombre debe[Resultado]Cuando[Condicion] y estructura AAA.
- Se permite el código de producción mínimo necesario para compilar —por ejemplo, un tipo o firma—, pero no implementar la regla que se está probando.
- Ejecutar la suite y comprobar que el test nuevo falla por la aserción y por el comportamiento esperado. Un error de compilación no demuestra RED y debe corregirse sin implementar la regla.
- Registrar el nombre del test, su ID del plan, la salida real, la cantidad de éxitos/fallos/errores y el comando ejecutado.
- Al finalizar, informar que RED está listo para el commit del usuario y detenerse.

## Fase GREEN

Solo después de que el usuario confirme que hizo el commit RED:

- Implementar el cambio más pequeño que haga pasar el test, **aunque sea un valor escrito a mano**. Devolver una constante es una forma válida de llegar al verde cuando ningún caso exige todavía generalizarla.
- No generalizar lo que ningún test exige. Si un solo caso usa un dato, ese dato puede quedar fijo hasta que otro caso lo contradiga; adelantarlo deja sin rojo a la iteración que debía eliminarlo.
- No agregar tests de casos que el usuario no indicó ni anticipar reglas de iteraciones futuras.
- Ejecutar la suite completa, incluyendo los tests anteriores. Si falla, corregir únicamente el alcance acordado o informar el bloqueo.
- Registrar el cambio realizado, el resultado exacto de la ejecución y qué valores quedaron fijos a la espera de la iteración que los elimine.
- Dejar los cambios listos para el commit GREEN del usuario y detenerse.

## Fase REFACTOR

Solo después de que el usuario confirme que hizo el commit GREEN:

- Mejorar estructura, nombres o duplicación sin cambiar el comportamiento ni añadir casos.
- Ejecutar la suite antes y después del refactor; ambas ejecuciones deben pasar.
- **No eliminar los valores fijos que espera una iteración futura.** Generalizarlos aquí no es refactor, porque cambia el comportamiento y deja sin rojo al caso que debía provocarlo. El refactor actúa sobre la forma del código, no sobre lo que todavía no se decidió.
- Si no hay una mejora real y segura, no fabricar cambios ni un commit vacío: explicar por qué y esperar indicaciones. El código de test cuenta: duplicación entre tests y datos repetidos son candidatos legítimos.
- Registrar qué se refactorizó, qué candidatos se descartaron y por qué, y los resultados. Dejar el estado listo para el commit REFACTOR del usuario y detenerse.

## Tests que pasan al escribirse

Si un test pasa apenas se escribe, no hubo ciclo: sin rojo, ese test no guió ningún diseño. No se provoca un fallo artificial para simularlo ni se presenta como una iteración TDD.

Igual puede valer la pena conservarlo, como **test de regresión**: fija un comportamiento que ya existe para que una iteración futura no lo rompa. En ese caso se registra aparte, indicando qué iteración previa lo dejó cubierto, y su commit se identifica como regresión y no como RED o GREEN.

## Evidencia y comunicación

Mantener REGISTRO_TDD.md dentro de documentacion/editables/ desde la primera iteración. Registrar cada iteración y fase con el caso elegido por el usuario, seam, ID, comando, salida real, conteos, tests de regresión y hash del commit cuando el usuario lo confirme. Los hashes se verifican con git log antes de anotarlos; no se escriben de memoria. Si se guardan logs en documentacion/evidencias/tdd/, conservar la salida exacta y el código de salida; no reconstruir ni inventar resultados.

Al cerrar una fase, responder brevemente con:

- fase terminada y tests que abarca;
- comando y resultado observado;
- cambios concretos y archivos;
- siguiente paso que le corresponde al usuario.

No afirmar que se aplicó TDD a un caso si no existe evidencia de RED, GREEN y REFACTOR separada. La prueba base de persistencia que ya estaba migrada a Java no constituye por sí sola un ciclo nuevo RED/GREEN/REFACTOR.

## Control de versión de esta guía

Versión inicial: fijó la dinámica por tandas de hasta cinco tests y llamó «guardas» a los tests que pasan al escribirse.

Versión actual: se trabaja de a un test por iteración, con ciclo completo cada uno. Se incorpora el criterio de fijar valores a mano en el verde y generalizarlos cuando un caso posterior lo exija, que es lo que sostiene la fase de refactor. Se reemplaza «guarda» por «test de regresión», término reconocible fuera del equipo; el concepto no cambia. Se agrega la verificación de hashes antes de registrarlos.