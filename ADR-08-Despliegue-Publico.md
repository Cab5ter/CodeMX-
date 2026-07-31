# ADR-08: Despliegue público como imagen única en Render

| Campo  | Valor |
|--------|-------|
| Autor  | Leonardo Balmes Solis |
| Fecha  | 30/07/2026 |
| Estado | `Aceptado` |

---

## Contexto

Hasta esta decisión, CodeMX sólo se podía usar de dos formas: en `localhost`, o desde otra
máquina de la **misma red local** mediante la configuración de acceso por LAN documentada en el
README (`host: true` en Vite y `0.0.0.0` en el backend).

La entrega final exige una **demo en vivo con link funcional**, accesible desde cualquier
dispositivo sin necesidad de estar en la misma red, incluidos teléfonos a través de un código QR.
El acceso por LAN no cubre eso: depende de una IP privada que cambia, exige estar físicamente en
la misma red y no funciona desde datos móviles.

Restricciones que pesan en la decisión:

- Proyecto académico individual, sin presupuesto: tiene que caber en un plan gratuito.
- El backend es ASP.NET Core sobre **.NET 10**, una versión reciente que no todas las
  plataformas ofrecen como runtime nativo.
- Necesita **PostgreSQL** administrado, porque es la única fuente de verdad (ADR-03).
- Usa **WebSockets** para el modo 1 vs 1 (SignalR), lo que descarta cualquier hosting que sólo
  sirva contenido estático o funciones sin estado.
- No quiero exponer credenciales de base de datos en el repositorio.

---

## Decisión

**Desplegar CodeMX en Render como un único servicio web construido desde un Dockerfile, donde
la API de ASP.NET Core sirve también el frontend de React, con una base PostgreSQL administrada
del mismo proveedor.**

### 1. Una sola imagen en lugar de dos servicios

El `Dockerfile` tiene tres etapas: Node construye el frontend, el SDK de .NET publica el backend,
y la imagen final copia el `dist/` de Vite dentro de `wwwroot/` de la API. `Program.cs` añade
`UseStaticFiles()` y `MapFallbackToFile("index.html")` para que React Router resuelva las rutas
del cliente.

Consecuencia directa: **frontend y API comparten origen**, así que desaparece el problema de
CORS, el proxy de Vite deja de ser necesario en producción y las rutas relativas `/api` del
cliente funcionan sin cambiar una línea.

### 2. Docker en lugar del runtime nativo de la plataforma

Render no ofrece .NET 10 como runtime gestionado. Con Docker el runtime lo fija el propio
repositorio (`mcr.microsoft.com/dotnet/aspnet:10.0`), lo que además hace el despliegue
reproducible y elimina la clase de fallo «funciona en mi máquina».

### 3. Configuración por variables de entorno

Render publica el puerto en `PORT` y la conexión en `DATABASE_URL`, esta última con formato URI
(`postgres://usuario:clave@host/base`) que Npgsql no entiende. `Program.cs` lee ambas y traduce
la segunda a una cadena de conexión de Npgsql al arrancar. **Ninguna credencial vive en el
repositorio**; `appsettings.json` conserva sólo los valores de desarrollo local.

### 4. Infraestructura declarada en el repositorio

`render.yaml` describe el servicio web y la base de datos, con `healthCheckPath: /health` (sonda
añadida en esta decisión) y `autoDeploy` desde `main`. La infraestructura queda versionada junto
al código, igual que los diagramas C4.

### 5. El pipeline es la única vía a producción

El job `desplegar` del CI (ADR-06) sólo se ejecuta en `main` y sólo si pasaron los cuatro jobs
anteriores —incluido el que arranca la imagen de verdad y comprueba siete rutas—. Después espera
a que `/health` de la demo responda. No hay despliegue manual.

---

### Resultado

La demo está publicada en **<https://codemx.onrender.com>**, con TLS gestionado por la
plataforma y un código QR (`img/qr-demo.png`) que la abre desde cualquier teléfono.

Verificado en producción el 30/07/2026: responden `/health`, las rutas de React Router
(`/`, `/ranking`, `/vs`, `/ejercicios`), los endpoints de la API y Swagger. El flujo completo
funciona de extremo a extremo —registro, login correcto (200) y con contraseña errónea (401),
envío de una solución evaluada como `ACEPTADO` y actualización automática del ranking vía el
Observer— y `GET /api/usuarios` no expone ningún hash.

---

## Alternativas consideradas

| Alternativa | Por qué la descarté |
|-------------|---------------------|
| **Mantener sólo el acceso por LAN** | No cumple el requisito: exige estar en la misma red y depende de una IP privada que cambia. Un QR con una IP `192.168.x.x` no sirve fuera del aula. |
| **Frontend estático (Vercel/Netlify) + backend aparte** | Es la opción más común, pero implica dos despliegues, configurar CORS y exponer WebSockets entre dominios distintos. Para un proyecto individual, el doble de piezas que pueden romperse el día de la presentación. |
| **Fly.io** | Buen soporte de Docker y sin suspensión por inactividad, pero exige tarjeta de crédito aunque el consumo sea gratuito. |
| **Azure for Students** | $100 de crédito sin tarjeta y sin arranques en frío. Descartada por tiempo: el despliegue de contenedores en Azure requiere bastante más configuración (registro de contenedores, plan de App Service, red) para el mismo resultado visible. |
| **Un VPS propio con Docker Compose** | Máximo control, pero implica administrar el servidor, los certificados TLS y las copias de seguridad. Es trabajo de operación que no aporta nada a lo que evalúa la materia. |
| **SQLite en lugar de PostgreSQL** | Simplificaría el despliegue, pero el sistema de ficheros del plan gratuito es efímero: la base se perdería en cada reinicio, y contradiría el ADR-03. |

---

## Consecuencias

**Lo que gano:**

- Una URL HTTPS pública, estable, accesible desde cualquier dispositivo y red — que es lo que
  hace posible el código QR.
- Un solo artefacto que desplegar y un solo origen: sin CORS y sin desajustes entre la versión
  del frontend y la de la API, porque viajan en la misma imagen.
- Despliegue reproducible y automático: lo que se prueba en CI es exactamente la imagen que
  corre en producción.
- TLS gestionado por la plataforma, requisito para que la contraseña del ADR-07 viaje cifrada.

**Lo que sacrifico o asumo:**

- **Arranque en frío.** El plan gratuito suspende el servicio tras ~15 minutos sin tráfico y la
  primera petición puede tardar cerca de un minuto. Es el precio de no pagar hosting, y hay que
  tenerlo en cuenta el día de la demo: conviene abrir el link unos minutos antes. Queda
  documentado como **trade-off TO-01** en la evaluación ATAM.
- **Acoplamiento de escalado.** Al ir frontend y backend en la misma imagen, no se pueden
  escalar por separado. Para el volumen de un proyecto académico es irrelevante; en un sistema
  real sería un límite.
- **Dependencia de un proveedor.** `render.yaml` es específico de Render. El `Dockerfile`, en
  cambio, es portable: mudarse implicaría reescribir un fichero, no la aplicación.
- **La base gratuita tiene fecha de caducidad** y una capacidad reducida. Los datos de la demo
  son desechables; no se guarda nada que importe perder.
- El evaluador de código en Python no se despliega: en producción actúa la estrategia local de
  respaldo (`EvaluacionLocalStrategy`), gracias al patrón Strategy del ADR-05. La demo evalúa
  con menos fidelidad que el entorno de desarrollo, y así queda anotado.

---

## Declaración de uso de IA

Se utilizó una herramienta de IA para apoyar la construcción del `Dockerfile`, la traducción de
`DATABASE_URL` y la redacción de este ADR. La elección de plataforma y la decisión de servir el
frontend desde la propia API son propias.
