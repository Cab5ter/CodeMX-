# Arquitectura de CodeMX — Modelo C4

Este documento describe la arquitectura de **CodeMX** (plataforma web de retos de
programación y cursos en español) usando el **Modelo C4**, versionada como código.
Todos los diagramas están escritos en **Mermaid** dentro de este `.md`; GitHub los
renderiza automáticamente.

El Modelo C4 describe un sistema en niveles de *zoom* progresivo:

| Nivel | Diagrama | Hace zoom en… |
|-------|----------|---------------|
| 1 | Contexto | El sistema completo y su entorno |
| 2 | Contenedores | Las piezas técnicas grandes que lo forman |
| 3 | Componentes | El interior de la pieza principal (la API) |

> Contexto técnico de referencia: monolito modular en **ASP.NET Core (.NET 10)** con
> **PostgreSQL** (EF Core), frontend **React + Vite**, un **evaluador de código en Python**
> por HTTP y la **API de Anthropic (Claude)** para generar problemas de duelo. Ver los
> `ADR-0X-*.md` para las decisiones de arquitectura.

---

## Nivel 1 — Contexto

> **¿Para quién es?** Para cualquiera (incluidos no técnicos: profesores, evaluadores,
> nuevos integrantes). **¿Qué pregunta responde?** *¿Qué es CodeMX, quién lo usa y con
> qué sistemas externos habla?* — sin entrar en tecnología interna.

```mermaid
C4Context
    title Nivel 1 - Contexto del sistema CodeMX

    Person(estudiante, "Estudiante universitario", "Resuelve retos, sigue cursos con lecciones y exámenes, compite en duelos 1v1 y en el ranking")

    System(codemx, "CodeMX", "Plataforma web de retos de programación y cursos en español, estilo SoloLearn")

    System_Ext(evaluador, "Servicio Evaluador Python", "Ejecuta el código enviado por el estudiante contra casos de prueba")
    System_Ext(claude, "Anthropic Claude API", "Genera problemas para los duelos 1 vs 1 bajo demanda")

    Rel(estudiante, codemx, "Resuelve retos y toma cursos", "HTTPS / navegador")
    Rel(codemx, evaluador, "Envía código a evaluar", "HTTP / JSON")
    Rel(codemx, claude, "Solicita generar un problema", "HTTPS / SDK oficial")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

**Lectura del diagrama:** el único usuario es el **estudiante**. CodeMX se apoya en dos
sistemas externos: el **evaluador Python** (decide si el código pasa los casos de prueba)
y la **API de Claude** (inventa los problemas de los duelos). Todo lo demás vive dentro
del sistema y se detalla en los niveles siguientes.

---

## Nivel 2 — Contenedores

> **¿Para quién es?** Para el equipo técnico (desarrolladores, DevOps). **¿Qué pregunta
> responde?** *¿De qué piezas ejecutables se compone CodeMX, qué tecnología usa cada una
> y cómo se comunican entre sí?* Un "contenedor" en C4 es algo que corre por separado
> (una app, una API, una base de datos), no un contenedor de Docker.

```mermaid
C4Container
    title Nivel 2 - Contenedores de CodeMX

    Person(estudiante, "Estudiante", "Usuario final, desde el navegador")

    System_Boundary(codemx, "CodeMX") {
        Container(spa, "SPA Frontend", "React 18 + Vite 5 + Tailwind + React Router", "Interfaz web; consume la API por rutas relativas /api mediante el proxy de Vite")
        Container(api, "API REST", "ASP.NET Core (.NET 10), monolito modular", "Toda la lógica de negocio; expone REST (Swagger/OpenAPI) y un hub SignalR para los duelos")
        ContainerDb(db, "Base de datos", "PostgreSQL + EF Core (Npgsql)", "Usuarios, retos, envíos, cursos, ranking y duelos; un esquema por módulo, creado con EnsureCreated")
    }

    System_Ext(evaluador, "Evaluador Python", "Ejecuta el código contra los casos de prueba")
    System_Ext(claude, "Anthropic Claude API", "Genera los problemas de los duelos")

    Rel(estudiante, spa, "Usa", "HTTPS")
    Rel(spa, api, "Llamadas de datos", "REST · HTTP/JSON (/api/*)")
    Rel(spa, api, "Duelos en tiempo real", "WebSocket (SignalR)")
    Rel(api, db, "Lee y escribe", "EF Core / Npgsql")
    Rel(api, evaluador, "Evalúa envíos", "HTTP / JSON")
    Rel(api, claude, "Genera problemas", "HTTPS / SDK")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

**Lectura del diagrama:** son **tres contenedores propios**. El **SPA de React** solo
pinta la interfaz y llama al backend (por el proxy `/api`, así no hay URLs incrustadas).
La **API .NET** concentra la lógica y habla con tres cosas: la **base PostgreSQL** (vía EF
Core), el **evaluador Python** (HTTP) y la **API de Claude** (SDK). Los duelos usan un
canal aparte en **tiempo real (SignalR/WebSocket)**, no REST.

---

## Nivel 3 — Componentes (dentro de la API REST)

> **¿Para quién es?** Para quien programa dentro del backend. **¿Qué pregunta responde?**
> *¿Qué hay dentro del contenedor "API REST": qué controladores, servicios de dominio y
> patrones de diseño lo forman y cómo colaboran?* Aquí se ven los **patrones GoF** ya
> implementados: **Strategy**, **Factory Method** y **Observer**.

```mermaid
C4Component
    title Nivel 3 - Componentes dentro de la API REST (ASP.NET Core, .NET 10)

    Person(estudiante, "Estudiante")
    ContainerDb(db, "PostgreSQL", "EF Core / CodeMxDbContext")
    System_Ext(evaluador, "Evaluador Python", "HTTP")
    System_Ext(claude, "Anthropic Claude API", "SDK")

    Container_Boundary(api, "API REST (.NET 10)") {
        Component(gateway, "Gateway / Controllers", "REST Controllers", "Usuarios, Retos, Envios, Ranking, Cursos, Duelos — entrada HTTP")
        Component(hub, "DueloHub + MatchmakingService", "SignalR", "Canal en tiempo real y emparejamiento del modo 1 vs 1")

        Component(usuarios, "Módulo Usuarios", "IUsuariosApi → UsuarioService", "Registro y login")
        Component(retos, "Módulo Retos", "IRetosApi → RetoService", "Catálogo de retos y casos de prueba")
        Component(cursos, "Módulo Cursos", "ICursosApi → CursoService", "Cursos, lecciones y exámenes")
        Component(envios, "Módulo Envíos (Subject)", "IEnviosApi → EnvioService", "Orquesta el envío; publica el evento al ser ACEPTADO")
        Component(evaluacion, "Módulo Evaluación", "IEvaluacionApi → EvaluacionService", "Decide el veredicto del código")
        Component(ranking, "Módulo Ranking (Observer)", "IRankingApi + RankingEnvioObserver", "Puntajes; reacciona a los envíos aceptados")
        Component(duelos, "Módulo Duelos", "IDuelosApi → DueloService", "Lógica del modo 1 vs 1")

        Component(factory, "EvaluadorStrategyFactory", "Factory Method", "Crea la estrategia según TipoEvaluacion")
        Component(stratR, "EvaluacionRemotaStrategy", "Strategy", "Delega en el evaluador Python")
        Component(stratL, "EvaluacionLocalStrategy", "Strategy", "Respaldo local si la remota falla")
        Component(generador, "GeneradorProblemas", "Strategy + fallback", "Claude → respaldo sembrado (RetoSembradoGenerador)")
    }

    Rel(estudiante, gateway, "Peticiones REST", "HTTP/JSON")
    Rel(estudiante, hub, "Duelo en vivo", "WebSocket")

    Rel(gateway, usuarios, "usa")
    Rel(gateway, retos, "usa")
    Rel(gateway, cursos, "usa")
    Rel(gateway, envios, "usa")
    Rel(gateway, ranking, "usa")
    Rel(hub, duelos, "usa")

    Rel(envios, evaluacion, "pide evaluación", "IEvaluacionApi")
    Rel(envios, ranking, "notifica ACEPTADO", "IEnvioObserver (Observer)")
    Rel(evaluacion, factory, "pide una estrategia")
    Rel(factory, stratR, "crea")
    Rel(factory, stratL, "crea (fallback)")
    Rel(stratR, evaluador, "ejecuta código", "HTTP")
    Rel(duelos, generador, "genera problema")
    Rel(generador, claude, "prompt", "SDK")

    Rel(usuarios, db, "EF Core")
    Rel(retos, db, "EF Core")
    Rel(cursos, db, "EF Core")
    Rel(envios, db, "EF Core")
    Rel(ranking, db, "EF Core")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

**Lectura del diagrama y patrones GoF:**

- **Arquitectura por módulos.** Cada módulo expone una **interfaz pública** (`I…Api`) y
  esconde su `Service` + `Repository`. Los módulos se hablan solo por esas interfaces, no
  por sus clases internas (monolito modular, ADR-03).
- **Strategy** — `IEvaluacionStrategy` tiene dos implementaciones intercambiables:
  `EvaluacionRemotaStrategy` (evaluador Python) y `EvaluacionLocalStrategy` (respaldo). El
  mismo patrón aparece en Duelos con `IGeneradorProblemas` (Claude vs. sembrado).
- **Factory Method** — `EvaluadorStrategyFactory` decide **qué** estrategia crear según el
  `TipoEvaluacion`, e intenta la remota cayendo a la local.
- **Observer** — `EnvioService` (sujeto) publica `EnvioAceptadoEvent` cuando un envío es
  aceptado por primera vez; `RankingEnvioObserver` está suscrito como `IEnvioObserver` y
  actualiza el ranking. Así **Envíos no conoce a Ranking**: mañana podrían sumarse otros
  observadores (logros, notificaciones) sin tocar Envíos.

---

## Declaración de uso de IA

Los **diagramas y textos de este documento** se elaboraron con apoyo de **Claude Code
(Claude Opus 4.8)**, a partir de una lectura del código real del repositorio (`Program.cs`,
los módulos de `backend/Modules/`, los controladores del `Gateway/` y el hub de tiempo
real). El contenido fue revisado para que refleje la arquitectura efectivamente
implementada. El modelo C4 elegido, la validación de cada nivel y las decisiones de
diseño descritas corresponden al autor del proyecto.
