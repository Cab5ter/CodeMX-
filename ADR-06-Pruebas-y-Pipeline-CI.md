# ADR-06: Pruebas unitarias con xUnit y pipeline de integracion continua (CI)

| Campo  | Valor |
|--------|-------|
| Autor  | Leonardo Balmes Solis |
| Fecha  | 23/07/2026 · revisado el 30/07/2026 |
| Estado | Aceptado · ampliado en la revisión del 30/07/2026 (ver el final del documento) |

> **Nota de numeración.** Este ADR se publicó originalmente como «ADR-05», número que ya
> ocupaba el ADR-05 (Consolidación del stack definitivo, 26/06/2026). Se renumeró a ADR-06
> por orden cronológico; su contenido no cambió salvo por la revisión documentada al final.

## Contexto

CodeMX es una plataforma de retos de programacion para estudiantes universitarios en Mexico.
Hasta ahora el repositorio contenia unicamente documentacion (ADRs y diagramas), sin una base
de codigo con verificacion automatica. La logica de negocio del sistema (calcular puntos de un
reto, evaluar un envio segun los casos de prueba que pasa y ordenar el ranking) es la parte mas
sensible a errores: un calculo mal hecho afecta directamente la posicion de un estudiante en la
tabla de posiciones, como ya se advirtio como riesgo en el ADR-01.

La actividad exige incorporar una suite de pruebas xUnit para al menos tres clases del proyecto
y un pipeline de integracion continua (CI) que compile y ejecute esas pruebas automaticamente en
cada push y en cada Pull Request. El backend se documento con ASP.NET Core en el ADR-04, asi que
el ecosistema .NET y su framework de pruebas oficial, xUnit, son la opcion coherente.

## Decision

Se creo una solucion .NET (CodeMX.sln) con dos proyectos: src/CodeMX.Domain (biblioteca de
dominio) y tests/CodeMX.Domain.Tests (proyecto xUnit, net8.0). Y un workflow de GitHub Actions
(.github/workflows/ci.yml) que en cada push y pull_request instala el SDK de .NET 8, restaura,
compila en Release y ejecuta dotnet test. Todas las pruebas usan el atributo [Fact] y siguen el
patron Arrange-Act-Assert.

### Que clases se probaron y por que se eligieron esas

Se eligieron las tres clases que concentran las reglas de negocio y los calculos del sistema. Se
dejaron fuera las clases que solo transportan datos o dependen de infraestructura externa (base
de datos, red), porque no contienen logica propia que verificar en una prueba unitaria.

| Clase probada | Por que se eligio | Que se prueba |
|---------------|-------------------|---------------|
| Challenge (reto) | Define los puntos base segun la dificultad; un error aqui desajusta todo el sistema de puntaje. Ademas valida su entrada. | Puntos base por dificultad, normalizacion del titulo, y que un titulo vacio lance excepcion. |
| Submission (envio) | Es el corazon del evaluador: traduce cuantos casos pasaron en un estado y un puntaje. Es la logica con mas ramas. | Estado Accepted/PartiallyAccepted/Rejected, puntaje proporcional a los puntos base, y rechazo de conteos invalidos. |
| Leaderboard (ranking) | Ordena a los estudiantes por puntaje; el ADR-01 marco la integridad del ranking como critica. | Conserva el mejor puntaje por usuario, devuelve 0 para desconocidos, ordena de mayor a menor y desempata alfabeticamente. |

Estas tres clases suman 15 pruebas que cubren casos correctos, casos de borde y validaciones que
lanzan excepciones.

### Por que un pipeline de CI

El pipeline garantiza que las pruebas se ejecuten en un entorno limpio en cada cambio. Si un push
rompe la compilacion o una prueba, GitHub Actions lo marca en rojo; si todo pasa, muestra el check
verde en el Pull Request.

### Alternativas consideradas

| Alternativa | Por que la descarte |
|-------------|---------------------|
| No escribir pruebas y validar a mano | Lento, poco confiable y sin evidencia reproducible. |
| MSTest / NUnit en lugar de xUnit | xUnit es el recomendado por el ecosistema .NET moderno y el que pide la actividad. |
| Ejecutar las pruebas solo localmente | No hay garantia de que se ejecuten antes de integrar; el CI las vuelve obligatorias. |
| Probar clases de infraestructura (DB/red) | Corresponden a pruebas de integracion; harian la suite lenta y fragil. |

## Consecuencias

Lo que gano:
- Tecnica: cada cambio en la logica de puntaje, evaluacion o ranking queda respaldado por pruebas
  automaticas; si algo se rompe, el pipeline lo detecta antes de integrarse a main.
- Proceso: el check verde es evidencia objetiva de que la entrega compila y pasa las pruebas. El
  historial de commits documenta la evolucion.

Lo que sacrifico o asumo:
- Limitacion tecnica: las pruebas cubren la logica de dominio, no la capa web ni la integracion con
  PostgreSQL o el evaluador en Python.
- Deuda o riesgo: mantener las pruebas al dia tiene un costo; al crecer el proyecto habra que sumar
  pruebas de integracion y medicion de cobertura.

## Evidencia del pipeline

El workflow .github/workflows/ci.yml se ejecuta en cada push y pull_request; el check aparece en la
pestana Actions y en el Pull Request. Si se altera intencionalmente una asercion, dotnet test falla
y el pipeline muestra el check en rojo, demostrando que detecta regresiones.

---

## Revisión del 30/07/2026 — el pipeline pasa a validar la aplicación real

### Qué problema se detectó

La decisión original dejó una brecha que este ADR reconoció como limitación: *«las pruebas
cubren la logica de dominio, no la capa web»*. En la práctica fue peor de lo previsto. El
proyecto `src/CodeMX.Domain` es un **modelo de dominio paralelo** (`Challenge`, `Submission`,
`Leaderboard`) que no comparte una sola línea con el backend que se ejecuta en producción
(`backend/CodeMX.Api.csproj`, módulos `Retos`, `Envios`, `Ranking`). El pipeline mostraba un
check verde que no decía nada sobre la aplicación desplegada: se podía romper la API entera
sin que el CI se enterara.

Agravante: el trabajo vivía en dos ramas que nunca se fusionaron. `pipeline-ci` tenía la
solución y el workflow pero ningún backend ni frontend; `CodeSmells` tenía la aplicación
completa pero ningún CI. Ninguna rama contenía a la vez el código y su verificación.

### Decisión

1. **Unificar el repositorio.** Fusionar la aplicación completa en la rama del pipeline, de
   modo que exista una única línea de trabajo con código y verificación juntos.
2. **Meter el backend real en la solución.** `backend/CodeMX.Api.csproj` se agrega a
   `CodeMX.sln` y los proyectos de dominio y pruebas se recompilan sobre `net10.0`, el mismo
   target del backend. Antes apuntaban a `net8.0`, lo que además impedía ejecutar las pruebas
   en una máquina que sólo tuviera el runtime 10.
3. **Probar el backend real.** Se añade `tests/CodeMX.Api.Tests` con 16 pruebas sobre el
   módulo Usuarios, la parte con reglas de seguridad (ver ADR-07): hashing, validación de
   registro, autenticación y un guardia de regresión que verifica que el hash no aparece en
   el JSON de respuesta. Total: **31 pruebas**.
4. **Ampliar el pipeline a cinco jobs**, para que el check verde cubra algo más que una
   compilación:

| Job | Qué verifica | Por qué |
|-----|--------------|---------|
| `backend` | Compila la solución y ejecuta las 31 pruebas con cobertura; publica el resultado y el porcentaje en el *Job Summary*. | Es la verificación de fondo; el resumen la hace legible sin abrir los logs. |
| `frontend` | `npm ci` + build de producción, con tabla del tamaño de cada archivo del bundle. | Un build roto del frontend antes no lo detectaba nadie. |
| `docker` | Construye la imagen y **la arranca** contra un PostgreSQL de servicio, comprobando siete rutas reales (`/health`, API, frontend, ruta de React Router y Swagger). | Que compile no implica que arranque. Este job es el que de verdad prueba el artefacto que se despliega. |
| `seguridad` | Audita dependencias vulnerables en NuGet y npm. | Cierra el flanco de las dependencias, que ninguna prueba unitaria cubre. |
| `desplegar` | Sólo en `main` y sólo si los cuatro anteriores pasaron: dispara el deploy hook de Render y espera a que `/health` responda. | Hace del pipeline la única vía a producción. |

### Alternativas consideradas en esta revisión

| Alternativa | Por qué la descarté |
|-------------|---------------------|
| Borrar `src/CodeMX.Domain` y quedarse sólo con el backend | Es la evidencia de la entrega anterior y sus 15 pruebas siguen pasando. Se conserva y se documenta que es un modelo aparte. |
| Añadir pruebas de integración con PostgreSQL real en el job de pruebas | Alarga y vuelve frágil la suite unitaria. El job `docker` ya cubre el arranque real contra una base, que era el riesgo verdadero. |
| Dejar el despliegue manual | Reintroduce el paso humano que el CI existe para eliminar. |

### Consecuencias de la revisión

Lo que gano:
- El check verde ahora significa: compila, 31 pruebas pasan, el frontend construye, la imagen
  arranca y responde, y no hay vulnerabilidades altas conocidas.
- Las pruebas se ejecutan también en local, cosa que antes fallaba por el desajuste de target.

Lo que sacrifico o asumo:
- El pipeline tarda más (cinco jobs, uno de ellos construye una imagen Docker). Se mitiga con
  caché de NuGet, npm y capas de Docker, y con cancelación de ejecuciones antiguas.
- La cobertura medida es baja (~13 %) porque se calcula sobre todo el ensamblado de la API,
  del que sólo el módulo Usuarios tiene pruebas. Es un dato honesto, no un objetivo cumplido:
  queda como deuda abierta y aparece como riesgo **R-02** en la evaluación ATAM.

## Declaracion de uso de IA

Se utilizo una herramienta de IA para apoyar la redaccion de este ADR y la estructura del proyecto
de pruebas, y para la revision del 30/07/2026.
