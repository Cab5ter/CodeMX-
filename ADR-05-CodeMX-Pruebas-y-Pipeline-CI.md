# ADR-05: Pruebas unitarias con xUnit y pipeline de integracion continua (CI)

| Campo  | Valor |
|--------|-------|
| Autor  | Leonardo Balmes Solis |
| Fecha  | 23/07/2026 |
| Estado | Aceptado |

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

## Declaracion de uso de IA

Se utilizo una herramienta de IA para apoyar la redaccion de este ADR y la estructura del proyecto
de pruebas.
