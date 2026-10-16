# Guía de trabajo TDD con asistentes LLM

EcoHarmony Park | TP6 | US «Inscribirme a actividad»

Esta guía fija la dinámica que deben seguir los LLM que colaboren con el equipo. El mapa de reglas y escenarios está en [PLAN_TDD.md](PLAN_TDD.md); esta guía define cómo trabajar cada tanda y cómo dejar evidencia.

## Acuerdos del equipo

- El usuario indica qué casos se trabajarán. El LLM no agrega, cambia ni adelanta casos por iniciativa propia.
- Se trabaja en tandas pequeñas, normalmente de hasta cinco tests que cubran una misma regla o comportamiento. Si los casos no forman un grupo coherente, se propone dividirlos y se espera la decisión del usuario.
- Cada tanda sigue este orden: **RED → commit del usuario → GREEN → commit del usuario → REFACTOR → commit del usuario**.
- El usuario ejecuta todos los commits. El LLM no hace git add, git commit, git push, reset, checkout ni reescribe el historial.
- Al terminar cada fase, el LLM informa los archivos y resultados y se detiene. Solo pasa a la fase siguiente cuando el usuario lo indique.
- No se empieza la tanda siguiente hasta que el usuario indique los casos.
- No se cambia de repositorio, rama ni configuración Git. El repositorio acordado es [ConstantinoBottero/TP-ISW-TDD](https://github.com/ConstantinoBottero/TP-ISW-TDD); se respeta el checkout que ya preparó el usuario.

## Antes de cada tanda

1. Leer los casos que indicó el usuario y mapearlos a sus IDs del plan y a un seam público acordado.
2. Revisar las respuestas pendientes al PO en [CONSULTAS_Y_VALIDACION_PO.md](CONSULTAS_Y_VALIDACION_PO.md). No inventar una regla para resolver una consulta abierta.
3. Mantener una sola causa de rechazo por test. Usar datos ficticios y concretos.
4. Si no está claro qué comportamiento se espera, preguntar antes de codificarlo.
5. No ampliar el alcance a otros tests, reglas, capas, interfaz o integraciones.

El backend usa Java 17, Maven Wrapper y JUnit Jupiter. Se ejercitan los seams S1, S2 y S3 descritos en PLAN_TDD.md; no se testean auxiliares privados directamente. Para el tiempo, se usa java.time.Clock fijo. Se conserva el repositorio en memoria real; no se mockean clases propias. El prototipo HTML/CSS/JavaScript es independiente del backend y no se integra como parte de una tanda salvo que el usuario lo pida.

## Fase RED

- Añadir solo los tests de la tanda indicada, con nombres debe[Resultado]Cuando[Condicion] y estructura AAA.
- Se permite el código de producción mínimo necesario para compilar —por ejemplo, un tipo o firma—, pero no implementar la regla que se está probando.
- Ejecutar la suite y comprobar que los tests nuevos fallan por la aserción y por el comportamiento esperado. Un error de compilación no demuestra RED y debe corregirse sin implementar la regla.
- Registrar por separado los tests que ya pasan: son **guardas**, no fallos RED. Nunca provocar un fallo artificial para hacerlos parecer rojos.
- Registrar los nombres de los tests, IDs del plan, salida real, cantidad de éxitos/fallos/errores y comando ejecutado.
- Al finalizar, informar que RED está listo para el commit del usuario y detenerse.

## Fase GREEN

Solo después de que el usuario confirme que hizo el commit RED:

- Implementar el cambio más pequeño que haga pasar todos los tests de la tanda.
- No agregar tests de casos que el usuario no indicó ni anticipar reglas de tandas futuras.
- Ejecutar la suite completa, incluyendo regresiones anteriores. Si falla, corregir únicamente el alcance acordado o informar el bloqueo.
- Registrar el cambio realizado y el resultado exacto de la ejecución.
- Dejar los cambios listos para el commit GREEN del usuario y detenerse.

## Fase REFACTOR

Solo después de que el usuario confirme que hizo el commit GREEN:

- Mejorar estructura, nombres o duplicación sin cambiar el comportamiento ni añadir casos.
- Ejecutar la suite antes y después del refactor; ambas ejecuciones deben pasar.
- Si no hay una mejora real y segura, no fabricar cambios ni un commit vacío: explicar por qué y esperar indicaciones.
- Registrar qué se refactorizó y los resultados. Dejar el estado listo para el commit REFACTOR del usuario y detenerse.

## Evidencia y comunicación

Mantener REGISTRO_TDD.md dentro de documentacion/editables/ cuando empiecen las tandas. Registrar cada bloque y fase con los casos elegidos por el usuario, seam, IDs, comando, salida real, conteos, guardas y hash del commit cuando el usuario lo confirme. Si se guardan logs en documentacion/evidencias/tdd/, conservar la salida exacta y el código de salida; no reconstruir ni inventar resultados.

Al cerrar una fase, responder brevemente con:

- fase terminada y tests que abarca;
- comando y resultado observado;
- cambios concretos y archivos;
- siguiente paso que le corresponde al usuario.

No afirmar que se aplicó TDD a un caso si no existe evidencia de RED, GREEN y REFACTOR separada. La prueba base de persistencia que ya estaba migrada a Java no constituye por sí sola un ciclo nuevo RED/GREEN/REFACTOR.

## Estado al crear esta guía

La prueba base del servicio y repositorio en memoria está migrada a Java/JUnit. Todavía no se inició ninguna tanda nueva de reglas de negocio bajo este acuerdo. El trabajo queda en pausa hasta que el usuario indique los primeros casos.