# Evaluación ATAM de la arquitectura de CodeMX

| Campo | Valor |
|-------|-------|
| Autor | Leonardo Balmes Solis |
| Fecha | 30/07/2026 |
| Método | ATAM (*Architecture Tradeoff Analysis Method*), SEI |
| Sistema evaluado | CodeMX — monolito modular en ASP.NET Core (.NET 10) + React, PostgreSQL |
| Alcance | La arquitectura tal como está implementada y desplegada, no una versión ideal |

---

## 1. Objetivos de negocio

CodeMX existe para que un estudiante universitario mexicano de los primeros semestres pueda
practicar programación **en español** y a su nivel, con retroalimentación inmediata. De ahí salen
tres objetivos que mandan sobre las decisiones técnicas:

1. **Que el estudiante confíe en el resultado.** Si el veredicto de un envío o la posición en el
   ranking son incorrectos, la plataforma pierde su razón de ser.
2. **Que se pueda usar desde el teléfono, en cualquier red.** El público objetivo no siempre tiene
   una computadora disponible.
3. **Que una persona sola pueda mantenerlo** en un semestre, sin presupuesto de infraestructura.

---

## 2. Atributos de calidad priorizados

| Prioridad | Atributo | Por qué importa aquí |
|-----------|----------|----------------------|
| Alta | **Seguridad** | Se guardan credenciales y se ejecuta código arbitrario enviado por usuarios. |
| Alta | **Corrección funcional** | El veredicto y el puntaje son el producto; un error los invalida. |
| Alta | **Modificabilidad** | El proyecto cambió de lenguaje a mitad de camino y siguió creciendo por módulos. |
| Media | **Disponibilidad** | La demo debe responder cuando se la evalúe, pero no es un sistema crítico. |
| Media | **Rendimiento** | Basta con que la evaluación de un envío se sienta inmediata. |
| Baja | **Escalabilidad** | Decenas de usuarios simultáneos como mucho; no hay escenario de carga masiva. |

---

## 3. Enfoques arquitectónicos evaluados

| Enfoque | Decisión de origen | Atributos que pretende favorecer |
|---------|--------------------|----------------------------------|
| Monolito modular con interfaces públicas por módulo y un esquema de BD por módulo | ADR-03 | Modificabilidad, simplicidad operativa |
| API REST como única frontera, documentada con Swagger | ADR-04 | Modificabilidad, interoperabilidad |
| Strategy + Factory para la evaluación (remota / local) | ADR-05 | Disponibilidad, modificabilidad |
| Observer entre Envíos y Ranking | ADR-05 | Modificabilidad (bajo acoplamiento) |
| BCrypt + separación entidad/DTO | ADR-07 | Seguridad |
| Pipeline de CI de cinco jobs que arranca la imagen real | ADR-06 | Corrección funcional, disponibilidad |
| Imagen única desplegada en Render, la API sirve el frontend | ADR-08 | Simplicidad operativa, disponibilidad |
| Estado de duelos en memoria (singleton) + SignalR | ADR-05 | Rendimiento del modo 1 vs 1 |

---

## 4. Escenarios de atributos de calidad

Escenarios concretos usados para tensionar la arquitectura:

| Id | Escenario | Atributo | Respuesta esperada | Respuesta real |
|----|-----------|----------|--------------------|----------------|
| E-01 | Se filtra un respaldo de la base de datos | Seguridad | Las contraseñas no son utilizables | ✅ BCrypt factor 11 con sal por usuario (ADR-07) |
| E-02 | Un estudiante envía código que borra archivos o abre una conexión de red | Seguridad | El código se ejecuta aislado | ❌ Se ejecuta en el contenedor, sólo con timeout → **R-03** |
| E-03 | Un estudiante llama a la API con el id de otro usuario | Seguridad | La petición se rechaza | ❌ El id se confía desde el cliente → **R-01** |
| E-04 | Un cambio rompe el cálculo de puntaje | Corrección | El pipeline lo detecta antes de integrar | ⚠️ Cubierto en el dominio y en Usuarios; el resto sin pruebas → **R-02** |
| E-05 | Hay que sustituir el evaluador de código por otro servicio | Modificabilidad | Se cambia una clase, sin tocar el resto | ✅ Strategy + Factory (ADR-05) |
| E-06 | Hay que reimplementar el backend en otro lenguaje | Modificabilidad | Los módulos y contratos se conservan | ✅ Ya ocurrió: Spring Boot → ASP.NET Core (ADR-05) |
| E-07 | Un evaluador abre el link tras horas sin tráfico | Disponibilidad | La página carga en pocos segundos | ⚠️ Arranque en frío de ~50 s → **TO-01** |
| E-08 | Dos estudiantes se enfrentan en modo 1 vs 1 | Rendimiento | Ambos ven el problema al instante | ✅ SignalR + estado en memoria, pero → **SP-02** |
| E-09 | Se duplica el número de instancias del servicio | Escalabilidad | El sistema sigue funcionando igual | ❌ Los duelos se romperían → **SP-02** |
| E-10 | Un atacante prueba contraseñas por fuerza bruta | Seguridad | Cada intento es caro | ✅ Coste deliberado de BCrypt → **SP-01** |

---

## 5. Riesgos

### R-01 · El id de usuario se confía desde el cliente (crítico)

**Decisión de origen:** ADR-04 (API REST sin capa de autenticación) y ADR-07, que resolvió el
almacenamiento de la credencial pero explícitamente **no** la autorización.

**Evidencia en el código.** `POST /api/usuarios/login` devuelve el usuario y el frontend guarda el
id en `localStorage` (`frontend/src/api/sesion.js`). A partir de ahí, cada petición envía ese id
como un dato más del cuerpo: `POST /api/envios` recibe `usuarioId` y lo acepta sin verificar nada.
No hay token, cookie de sesión ni comprobación de identidad.

**Consecuencia.** Cualquiera puede enviar una solución en nombre de otro estudiante, sumarle o
manipular puntos y alterar el ranking, con una sola petición HTTP y sin credenciales. Esto ataca
directamente el objetivo de negocio nº 1: la confianza en el resultado. El ADR-01 ya había marcado
la integridad del ranking como atributo crítico, así que el riesgo apunta al corazón del sistema.

**Por qué está abierto.** Hasta ahora el sistema vivía en `localhost`, donde el vector no existía.
El ADR-08 lo publica en internet, y ahí sí existe.

**Mitigación propuesta.** Emitir un JWT firmado en el login y derivar el `usuarioId` del token en
el servidor, ignorando el que venga en el cuerpo. Es un cambio acotado —afecta al gateway y al
cliente HTTP del frontend— porque los módulos ya están detrás de interfaces.

### R-02 · La verificación automática cubre una fracción del sistema (medio)

**Decisión de origen:** ADR-06 y su revisión.

**Evidencia.** 31 pruebas: 15 sobre un modelo de dominio que no se usa en producción y 16 sobre el
módulo Usuarios. La cobertura de línea medida sobre el ensamblado de la API es del **13,5 %**.
Módulos con reglas de negocio reales —`Envios`, `Ranking`, `Evaluacion`, `Cursos`— no tienen
ninguna prueba unitaria.

**Consecuencia.** El escenario E-04 (romper el cálculo de puntaje) no está cubierto: un error en
`EnvioService` o en `RankingService` pasaría el pipeline en verde. El job `docker` mitiga en parte
—comprueba que la aplicación arranca y responde— pero no valida ningún resultado de negocio.

**Mitigación propuesta.** Priorizar pruebas sobre `EnvioService` (traducción de casos aprobados a
veredicto y puntaje) y `RankingService` (acumulación y orden), que es donde un error es a la vez
probable y dañino.

### R-03 · El código del estudiante se ejecuta sin aislamiento (crítico)

**Decisión de origen:** ADR-05 (`EvaluacionLocalStrategy` como respaldo) y ADR-08, que en la práctica
la convierte en la estrategia **activa** en producción, porque el evaluador remoto en Python no se
despliega.

**Evidencia.** `EvaluacionLocalStrategy.EjecutarPython` escribe el código en un archivo temporal y
lanza `python3` como subproceso del backend. La única contención es un `WaitForExit(timeoutMs)` de
5 segundos. No hay contenedor separado, ni límite de memoria, ni restricción de red, ni de sistema
de archivos. El proceso corre con el mismo usuario que la API y ve sus variables de entorno —entre
ellas la cadena de conexión a PostgreSQL.

Este riesgo **empeoró al desplegar**: al añadir `python3` a la imagen para que la evaluación
funcionara (sin él fallaba con `ERROR_EN_EJECUCION`), la ejecución de código no confiable pasó a
estar disponible en una URL pública.

**Consecuencia.** Un envío con `import os` puede leer variables de entorno y abrir conexiones
salientes. El ADR-01 y el ADR-03 identificaron desde el principio que la ejecución de código no
confiable debía quedar **aislada** del resto del sistema; la implementación actual no cumple esa
intención de diseño.

**Atenuantes reales.** El contenedor corre como usuario sin privilegios (`USER $APP_UID`), el
timeout corta bucles infinitos, la base de la demo es desechable y el alcance del daño está
acotado al contenedor, que Render recrea en cada despliegue.

**Mitigación propuesta.** Desplegar el evaluador Python como servicio separado con contenedor
efímero por ejecución, sin red y con límites de CPU y memoria — que es exactamente lo que el
`EvaluacionRemotaStrategy` ya contempla. La arquitectura está preparada; falta la infraestructura.

---

## 6. Punto de sensibilidad

### SP-01 · Factor de trabajo de BCrypt

**Decisión de origen:** ADR-07 — `UsuarioService.CostoBCrypt = 11`.

Es un punto de sensibilidad porque **una sola constante determina dos atributos de calidad en
direcciones opuestas**, y ninguna otra decisión de la arquitectura los toca:

| Factor | Tiempo aproximado por hash | Efecto en seguridad | Efecto en rendimiento |
|--------|---------------------------|---------------------|-----------------------|
| 8 | ~12 ms | Fuerza bruta barata | Login imperceptible |
| **11 (elegido)** | **~100 ms** | Coste alto por intento | Aceptable en login/registro |
| 14 | ~800 ms | Muy resistente | Perceptible; riesgo de saturar la CPU |

El valor 11 se eligió por el contexto de despliegue: el plan gratuito de Render da **una CPU
compartida** (ADR-08). Con factor 14, una decena de inicios de sesión simultáneos bastaría para
saturarla y degradar toda la aplicación, no sólo el login — el hashing es síncrono y compite con
el resto de las peticiones.

**Por qué es sensibilidad y no trade-off:** aquí se ajusta *un parámetro* dentro de una decisión ya
tomada. La respuesta del sistema varía de forma continua y marcada con ese número, sin que cambie
ninguna otra decisión arquitectónica.

**Implicación práctica:** si el despliegue migrara a un plan con CPU dedicada, subir a 12 o 13 sería
inmediato y no exigiría tocar nada más. La constante está aislada y documentada en un único punto.

### SP-02 · Ubicación del estado de los duelos

**Decisión de origen:** ADR-05 — `MatchmakingService` registrado como **singleton** en memoria.

La cola de emparejamiento y los duelos activos viven en un diccionario en memoria del proceso. Con
una instancia funciona perfectamente y con latencia mínima (escenario E-08). Con dos instancias, dos
jugadores emparejados podrían caer en procesos distintos y no verse nunca (escenario E-09).

El sistema es sensible al **número de instancias**: pasar de 1 a 2 no degrada el rendimiento, rompe
la funcionalidad. Mientras el despliegue sea de una sola instancia —que es el caso del plan gratuito
de Render— la decisión es correcta y la más simple. Mover ese estado a Redis sólo se justifica si
alguna vez hace falta escalar horizontalmente.

---

## 7. Trade-offs

### TO-01 · Coste cero contra disponibilidad inmediata

**Decisión de origen:** ADR-08 — plan gratuito de Render.

Es un trade-off porque **una misma decisión mejora un atributo y empeora otro**, sin que exista
ajuste que dé las dos cosas:

| | Plan gratuito (elegido) | Plan de pago |
|---|---|---|
| Coste | $0 | ~$7 USD/mes por servicio |
| Primer acceso tras inactividad | ~50 s (arranque en frío) | Inmediato |
| Base de datos | Gratuita, con caducidad | Persistente |

El servicio se suspende tras unos 15 minutos sin tráfico. El escenario E-07 —un evaluador abre el
link a las 11 de la noche— tarda casi un minuto en responder, lo que en una demo se lee como «no
funciona».

**Por qué acepté este lado:** el objetivo de negocio nº 3 dice explícitamente *sin presupuesto de
infraestructura*, y es un proyecto académico individual. La disponibilidad es un atributo de
prioridad media, no alta. La mitigación es de proceso, no de arquitectura: abrir el link unos
minutos antes de la presentación.

### TO-02 · Simplicidad operativa contra independencia de despliegue

**Decisión de origen:** ADR-08 — frontend y backend en una sola imagen.

Lo que gana: un solo artefacto, un solo origen (sin CORS), imposible que la versión del frontend
y la de la API se desincronicen, y un solo servicio que vigilar.

Lo que pierde: no se pueden escalar ni desplegar por separado. Un cambio de una palabra en un
texto del frontend obliga a reconstruir y redesplegar el backend completo, con su arranque en frío
correspondiente. Y el frontend, que es contenido estático, no puede servirse desde una CDN.

**Por qué acepté este lado:** con un solo desarrollador y decenas de usuarios, el coste operativo
de dos servicios (dos despliegues, CORS, WebSockets entre dominios, dos puntos de fallo el día de
la demo) supera al beneficio de una independencia que nadie va a aprovechar. En un equipo con
frontend y backend separados, la balanza se invertiría.

### TO-03 · Monolito modular contra despliegue independiente

**Decisión de origen:** ADR-03 — el trade-off estructural del proyecto.

Los módulos (`usuarios`, `retos`, `envios`, `evaluacion`, `ranking`, `cursos`, `duelos`) están
separados por interfaces explícitas y esquemas de base de datos propios, pero se compilan y
despliegan juntos.

Lo que gana: una transacción, una base de datos, un despliegue, sin latencia de red ni consistencia
eventual entre módulos. Y algo que el proyecto **demostró en la práctica**: cuando el ADR-05 obligó
a reescribir el backend entero de Spring Boot a ASP.NET Core, los módulos y sus contratos
sobrevivieron intactos. La modularidad dio su beneficio principal sin cobrar el precio de los
microservicios.

Lo que pierde: un fallo en cualquier módulo tumba todo el proceso, no se escala por módulo, y el
módulo de evaluación —el único que ejecuta código no confiable— **no puede aislarse a nivel de
proceso**, que es precisamente lo que alimenta el riesgo R-03.

**Por qué acepté este lado:** el ADR-03 lo justificó por el contexto (proyecto individual, sin
infraestructura para operar varios despliegues) y sigue siendo correcto. La excepción es el
evaluador: ahí el aislamiento no es una optimización, es seguridad, y por eso la mitigación de
R-03 pasa por extraer ese módulo y sólo ese.

---

## 8. No-riesgos

Decisiones evaluadas que **no** representan riesgo en el contexto actual:

- **Strategy + Factory en la evaluación (ADR-05).** El escenario E-05 se resuelve cambiando una
  clase. Además demostró su valor: al no desplegarse el evaluador remoto, el respaldo local entró
  sin tocar nada.
- **Observer entre Envíos y Ranking (ADR-05).** Envíos no conoce a Ranking; añadir otro suscriptor
  (por ejemplo, logros) no exige tocar Envíos.
- **Un esquema de PostgreSQL por módulo (ADR-03).** Impone la frontera entre módulos a nivel de
  datos y haría directa una eventual extracción.
- **Swagger siempre activo (ADR-04).** Expone el contrato, no datos; es documentación útil para la
  evaluación de la materia.
- **Conservar `src/CodeMX.Domain` (ADR-06).** Es un modelo paralelo que no se usa en producción,
  pero está documentado como tal y sus pruebas siguen pasando: no engaña a nadie.

---

## 9. Resumen

| Categoría | Id | Enunciado | Severidad |
|-----------|----|-----------|-----------|
| Riesgo | R-01 | El id de usuario se confía desde el cliente: cualquiera puede suplantar a otro | Crítica |
| Riesgo | R-02 | La verificación automática cubre el 13,5 % del backend | Media |
| Riesgo | R-03 | El código del estudiante se ejecuta sin aislamiento, en un servicio público | Crítica |
| Sensibilidad | SP-01 | El factor de trabajo de BCrypt fija seguridad y latencia de login a la vez | — |
| Sensibilidad | SP-02 | El estado de los duelos en memoria ata el sistema a una sola instancia | — |
| Trade-off | TO-01 | Coste cero a cambio de ~50 s de arranque en frío | — |
| Trade-off | TO-02 | Imagen única: simplicidad operativa a cambio de despliegue independiente | — |
| Trade-off | TO-03 | Monolito modular: simplicidad a cambio de aislamiento por módulo | — |

**Conclusión.** La arquitectura cumple bien con **modificabilidad** —lo demostró sobreviviendo a un
cambio completo de lenguaje— y ha cerrado su deuda más grave de seguridad en el almacenamiento de
credenciales. Sus dos riesgos críticos comparten una causa: decisiones que eran razonables mientras
el sistema vivía en `localhost` y que dejaron de serlo al publicarlo en internet. Ninguno de los dos
exige rediseñar la arquitectura: R-01 se resuelve con un token derivado en el servidor, y R-03 con
la infraestructura que el patrón Strategy ya tiene prevista. Ese es el orden de trabajo si el
proyecto continúa.

---

## Declaración de uso de IA

Se utilizó una herramienta de IA para apoyar la estructura de este documento y la redacción. La
identificación de los riesgos parte de la revisión del código del proyecto; R-03 se detectó al
comprobar que la imagen de producción no incluía `python3` y que añadirlo exponía la ejecución de
código no confiable en una URL pública.
