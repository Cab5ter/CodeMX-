# ADR-02: Estilo arquitectónico de CodeMX

| Campo  | Valor |
|--------|-------|
| Autor  | Leonardo Balmes |
| Fecha  | 12/06/2026 |
| Estado | `Aceptado` |

---

## Contexto

CodeMX es una plataforma web de retos de programación dirigida a estudiantes universitarios mexicanos. El usuario escribe código, lo envía, y el sistema lo ejecuta contra una batería de casos de prueba para validar si la solución es correcta y medir su desempeño. El sistema se compone de un frontend en React, un backend en Spring Boot, un servicio de evaluación/ejecución de código en Python y una base de datos PostgreSQL.

El backend abarca varios dominios de negocio bien diferenciados: gestión de usuarios, catálogo de retos, envíos de soluciones, evaluación/calificación y ranking. Las restricciones que influyeron en la decisión: es un proyecto académico individual con tiempo limitado, en el que domino Java/Spring y PostgreSQL (vistos en clase) y manejo React a un nivel básico, y la ejecución de código no confiable del usuario debe quedar **aislada** del resto del sistema por motivos de seguridad. No se cuenta con infraestructura para operar y monitorear muchos despliegues independientes.

---

## Decisión

Se adopta una **arquitectura de monolito modular** sobre un esquema cliente-servidor, complementada con **un servicio independiente y aislado para la ejecución de código** (Python).

Concretamente:
- **Cliente:** React (SPA) que consume una API REST.
- **Servidor:** un único backend en Spring Boot (un solo despliegue) dividido internamente en **módulos por dominio de negocio**: `usuarios`, `retos`, `envios`, `evaluacion` y `ranking`. Cada módulo tiene sus propias capas internas (controller, servicio, dominio, repositorio) y expone una API/interfaz clara hacia los demás módulos, ocultando sus detalles internos.
- **Servicio de ejecución:** componente Python separado que recibe el código y lo corre en un entorno *sandbox* aislado; el módulo `evaluacion` se comunica con él.
- **Persistencia:** PostgreSQL como única fuente de verdad, con esquemas/tablas lógicamente separados por módulo.

### ¿Por qué?

El backend de CodeMX se organiza naturalmente en dominios de negocio claros. El monolito modular resuelve esto directamente: cada módulo encapsula un dominio y se comunica con los demás solo a través de interfaces explícitas, lo que da **alta cohesión interna y bajo acoplamiento entre módulos** sin pagar el costo operativo de los microservicios. Las fronteras entre módulos disciplinan el código y evitan que se convierta en un "gran lodazal" donde todo depende de todo.

Al ser un único despliegue, conservo la simplicidad operativa de un monolito (un solo build, una sola base de datos, transacciones locales, depuración sencilla), ideal para un proyecto individual con poco tiempo. Y como los módulos ya están separados por fronteras claras, si en el futuro el proyecto crece, **migrar un módulo a microservicio es mucho más fácil** que partir un monolito tradicional. El único componente que sí justifica salir del backend hoy es la ejecución de código del usuario, que por seguridad debe correr aislado y encaja mejor en Python.

### Alternativas consideradas

| Alternativa | Por qué la descarté |
|-------------|---------------------|
| **Monolito en capas (sin modularizar)** | Organiza el código por capas técnicas horizontales en lugar de por dominio. Funciona, pero a medida que crecen los dominios tiende a generar alto acoplamiento entre funcionalidades y dificulta una eventual extracción de servicios. El monolito modular da las mismas ventajas operativas con fronteras de negocio mucho más claras. |
| **Microservicios** (un servicio por dominio) | Sobredimensionado para un proyecto académico individual. Añade complejidad operativa (despliegues independientes, comunicación de red entre servicios, observabilidad distribuida, consistencia eventual de datos) que no aporta valor frente al tamaño real del problema. El monolito modular ofrece la misma separación de dominios sin ese costo, inmanejable para una sola persona. |
| **Arquitectura event-driven** (broker de mensajes entre módulos) | El flujo de CodeMX es mayormente síncrono y de petición-respuesta (envías código → esperas el veredicto). Introducir un broker y procesamiento asíncrono complica el desarrollo y la depuración sin necesidad real. |
| **Serverless** (funciones por endpoint) | Atractivo solo para el sandbox de ejecución, pero como estilo general implica vendor lock-in, arranque en frío y dificultad para depurar/probar localmente. No se justifica para todo el sistema con la infraestructura disponible. |

---

## Consecuencias

**✅ Lo que gano:**

- **Técnica:** los módulos por dominio dan alta cohesión y bajo acoplamiento, y al ser un único despliegue mantengo transacciones locales, una sola base de datos como fuente de verdad y depuración sencilla. Las fronteras entre módulos preparan el terreno para extraer microservicios si algún día hace falta. El servicio de ejecución aislado contiene el riesgo de seguridad del código no confiable.
- **Proceso de trabajo:** al ser una sola persona, la división por módulos me permite avanzar un dominio a la vez de forma ordenada, sobre tecnologías que ya manejo (Spring y PostgreSQL con soltura, React a nivel básico), y deja claro "dónde va cada cosa" sin perderme conforme crece el código.

**⚠️ Lo que sacrifico o asumo:**

- **Limitación técnica:** sigue siendo un único despliegue, así que no puedo escalar ni desplegar módulos de forma independiente; si un módulo recibe mucha carga, escala todo el backend junto.
- **Deuda/riesgo:** las fronteras entre módulos hay que **mantenerlas con disciplina**; sin revisión, es fácil que un módulo empiece a depender de los detalles internos de otro y se erosione la modularidad, regresando de facto a un monolito acoplado. Si el proyecto creciera mucho, tocaría extraer los módulos más exigidos a microservicios.

## Diagrama

![Diagrama del estilo arquitectónico de CodeMX](./diagrama-codemx.png)
