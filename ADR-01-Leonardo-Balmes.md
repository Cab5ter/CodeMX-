# ADR-01: [Plataforma de Retos de Programación para Estudiantes]

| Campo  | Valor |
|--------|-------|
| Autor  |Leonardo Balmes Solis|
| Fecha  | DD/MM/AAAA |
| Estado | `Propuesto` · `Aceptado` · `Rechazado` · `Reemplazado por ADR-NN` |

---

## Contexto

¿Qué estás construyendo, qué problema resuelve y para quién es? Describe también las condiciones o restricciones que influyeron en esta decisión — por ejemplo, el tiempo disponible, el equipo, las tecnologías que ya conoces o las que viste en clase.

El problema que quiero resolver es que en México los estudiantes de programación no tienen
un lugar en español donde practicar retos de código a su nivel. Plataformas como LeetCode o
HackerRank existen, pero están en inglés y pensadas para gente con experiencia. Un
estudiante de segundo o tercer semestre entra y no entiende ni por dónde empezar.
La idea es construir CodeMX, una plataforma web donde cualquier estudiante de universidad
en México pueda entrar, resolver retos de programación ordenados por dificultad, ver en qué
posición está respecto a otros y participar en torneos. Más adelante, los profesores podrían
asignar retos como tarea y las empresas podrían usarlo para encontrar talento, pero eso es a
futuro.

Condiciones con las que trabajo:
- Es un proyecto individual y solo tengo el cuatrimestre para entregarlo.
- Conozco Java básico, algo de Python y SQL. Spring Boot no lo manejo todavía pero
quiero aprenderlo.
- El sistema tiene que poder ejecutar código que manden los usuarios sin que eso
represente un riesgo de seguridad.

Para quién es:
- Estudiantes de universidades en México que quieran practicar programación (usuario
principal).
- Profesores que quieran poner retos como actividad de clase (más adelante).
- Empresas que busquen candidatos técnicos (fase futura).

---

## Decisión

¿Qué decidiste? Sé específico: nombra la tecnología, el patrón o el estilo arquitectónico que elegiste.

### ¿Por qué?

Argumenta tu decisión. No basta con decir "es lo que vimos en clase" — explica qué característica concreta de lo que elegiste resuelve tu problema.

- **2.1 Arquitectura monolítica modular**
Decidí hacer el sistema como un solo bloque con partes internas bien separadas: una parte
para usuarios, otra para los retos, otra para el evaluador de código y otra para el ranking. No
quise usar microservicios porque eso implica tener varios programas corriendo al mismo tiempo
comunicándose entre ellos, y para un proyecto individual en un cuatrimestre eso es demasiado
para manejar solo. Si el proyecto crece, se pueden separar esas partes más adelante sin tener
que reescribir todo desde cero.

- **2.2 Backend: Java con Spring Boot**
Elegí Spring Boot para el backend aunque todavía no lo sé usar bien. La razón es que quiero
aprenderlo durante el proyecto y tiene mucha documentación disponible. Además, Spring Boot
ya trae herramientas integradas para manejar seguridad, validación de datos y construcción de
APIs, lo que significa que no tengo que construir esas cosas desde cero. Es un framework que
se usa mucho en la industria, así que aprenderlo también tiene valor más allá de la materia.

- **2.3 Módulo de evaluación: Python**
La parte del sistema que ejecuta el código que mandan los usuarios la voy a hacer en Python.
Esto es porque Python tiene librerías que permiten correr código ajeno de forma controlada,
limitando el tiempo de ejecución y los recursos que puede usar. Además, la mayoría de los
estudiantes que van a resolver los retos probablemente usen Python, así que tiene sentido que
el evaluador también lo sea. Este módulo va a comunicarse con el backend de Java a través de
una petición HTTP interna.

- **2.4 Base de datos: PostgreSQL**
Escogí PostgreSQL porque los datos de este sistema están muy relacionados entre sí: un
usuario tiene varios envíos, cada reto tiene varios casos de prueba, cada torneo tiene varios
participantes. Una base de datos relacional como PostgreSQL permite hacer esas conexiones
de forma clara y garantiza que los datos no se corrompan si algo falla a la mitad de una
operación. Eso es importante en un sistema de evaluaciones donde un resultado mal guardado
podría afectar el ranking de alguien.

- **2.5 Frontend: React con Vite**
Para la interfaz elegí React porque permite dividir la pantalla en componentes reutilizables, por
ejemplo el editor de código, las tarjetas de retos y la tabla del ranking pueden ser piezas
independientes que se usan en varios lugares. Vite hace que el proyecto cargue rápido
mientras lo desarrollo. Hay mucha documentación y ejemplos disponibles, lo que me ayuda
cuando me trabo en algo.

### Alternativas consideradas

*(Mínimo 3 filas)*

| Alternativa | Por qué la descarté |
|-------------|---------------------|
|Microservicios desde el inicio|Manejar varios servicios independientes comunicándose entre sí es demasiado complejo para hacerlo solo en un cuatrimestre. Prefiero empezar simple y separar partes después si el proyecto crece.|
|MongoDB (base de datosNoSQL)|Los datos del sistema tienen muchas relaciones entre sí. Con MongoDB haría consultas como 'todos los envíos de un usuario para un reto' de forma complicada. SQL lo resuelve más fácil y de forma más segura.|
|Node.js + Express|Lo consideré porque es más fácil de aprender, pero al final decidí quedarme con Spring Boot porque quiero aprenderlo y tiene más herramientas integradas para seguridad y validación que en Node tendría que configurar por separado.|
|Django como backendprincipal|Ya voy a usar Python para el evaluador de código. Añadirlo también como backend me complicaría la arquitectura sin ninguna ventaja real. Es mejor tener cada tecnología con una responsabilidad clara.|
|Arquitectura serverless (AWSLambda)|Requiere saber configurar infraestructura en la nube, que es algo que no manejo. Además los costos pueden ser impredecibles. Para este proyecto es más práctico tener todo corriendo en un servidor normal.|
---

## Consecuencias

**✅ Lo que gano:**

Menciona al menos:
- Una consecuencia **técnica** — qué se vuelve más fácil de construir, mantener o escalar en tu sistema
- Una consecuencia sobre el **proceso o el equipo** — cómo afecta la forma en que vas a trabajar

Técnico:
- Al tener todo en un solo sistema, puedo desarrollar y probar el proyecto completo sin
necesidad de levantar varios servicios al mismo tiempo. Eso hace que avanzar sea más
rápido.
- PostgreSQL me garantiza que los datos del ranking y los envíos se guarden
correctamente aunque algo falle a la mitad. No voy a tener resultados a medias en la
base de datos.
- Separar el evaluador de código en su propio módulo de Python hace que si algo sale
mal al ejecutar código de un usuario, el problema no afecte al resto del sistema.

Proceso:
- Aunque no sé Spring Boot todavía, hay tanta documentación disponible que puedo
aprender mientras construyo. Eso convierte el proyecto en una oportunidad real de
aprendizaje.
- Como el sistema está dividido en módulos internos, puedo construir y probar cada parte
por separado sin romper lo que ya funciona.

**⚠️ Lo que sacrifico o asumo:**

Menciona al menos:
- Una **limitación técnica** — qué no podrás hacer fácilmente con esta decisión
- Una **deuda o riesgo** — qué podrías tener que resolver más adelante si el proyecto crece

Limitaciones técnicas:
- Si en algún momento el sistema tuviera miles de usuarios al mismo tiempo, escalar solo
la parte del evaluador sin tocar el resto sería difícil con esta arquitectura. Habría que
separarlo en un servicio independiente.
- El editor de código que voy a poner en el navegador puede volverse lento en
computadoras viejas o con poca memoria, porque las librerías de edición de código son
pesadas.

Deudas y riesgos:
- El mayor riesgo del proyecto es la seguridad del evaluador. Si no configuro bien el
entorno donde se ejecuta el código de los usuarios, alguien podría enviar código
malicioso que afecte el servidor. Eso lo tengo que resolver antes de que el sistema sea
público.
- La comunicación entre el backend de Java y el evaluador de Python es un punto débil:
si el evaluador falla o se cae, los envíos de código dejan de funcionar. A futuro habría
que poner una cola de mensajes para que los envíos no se pierdan aunque el evaluador
esté caído.

## Diagrama

Un boceto de cómo se estructura tu sistema (draw.io, Mermaid o a mano escaneado)

![Diagrama](img/Untitled%20diagram-2026-05-15-231326.png)
