# ADR-07: Hashing de contraseñas con BCrypt y separación entre entidad y DTO

| Campo  | Valor |
|--------|-------|
| Autor  | Leonardo Balmes Solis |
| Fecha  | 30/07/2026 |
| Estado | `Aceptado` · Cierra la deuda técnica **DT-01** documentada en el README |

---

## Contexto

CodeMX tiene registro e inicio de sesión propios: el estudiante crea una cuenta con nombre,
correo y contraseña, y esa identidad es la que ata sus envíos, su progreso en los cursos y su
posición en el ranking.

Hasta esta decisión, el manejo de credenciales tenía tres fallos encadenados. El análisis de
deuda técnica del README ya los había identificado como **DT-01**, prioridad crítica:

1. **Contraseñas en texto plano.** El campo se llamaba `PasswordHash`, pero nunca se hasheaba
   nada. El frontend capturaba la contraseña en un campo llamado `passwordHash` y la enviaba
   tal cual; `UsuarioService.AutenticarAsync` la comparaba con `==` contra el valor guardado.
   El nombre del atributo describía una intención que el código no cumplía.
2. **Filtración del secreto en cada respuesta.** El gateway devolvía la entidad `Usuario`
   directamente. `GET /api/usuarios` entregaba la lista completa de correos **con su
   contraseña**, sin autenticación de por medio.
3. **Sin validación de entrada.** Se aceptaba cualquier nombre, cualquier correo y cualquier
   contraseña, incluida la cadena vacía.

Mientras el sistema corría en `localhost` para una demo en clase, el impacto era limitado. La
decisión de publicar una demo en internet accesible desde cualquier dispositivo (ADR-08)
cambia el cálculo por completo: cualquiera con la URL podría descargarse la tabla de usuarios.

Condición adicional que pesa en la decisión: los estudiantes reutilizan contraseñas. Una fuga
en un proyecto académico no compromete sólo a CodeMX, sino la cuenta de correo de quien se
registró.

---

## Decisión

**Hashear las contraseñas con BCrypt (factor de trabajo 11) y no exponer nunca el hash fuera
del módulo Usuarios.**

Concretamente:

1. **BCrypt en el registro, verificación en el login.** `UsuarioService.RegistrarAsync` hashea
   con `BCrypt.Net-Next`; `AutenticarAsync` compara con `BCrypt.Verify`. La contraseña en claro
   no se guarda ni se registra en logs en ningún punto.
2. **El factor de trabajo es 11** (2¹¹ iteraciones). BCrypt incorpora la sal en el propio hash,
   así que dos usuarios con la misma contraseña obtienen hashes distintos.
3. **DTOs en la frontera.** Entran `RegistroRequest` y `LoginRequest` (con un campo `Password`
   honesto, no `PasswordHash`); sale `UsuarioDto`, que no tiene campo de contraseña. El gateway
   nunca serializa la entidad.
4. **Defensa en profundidad.** La propiedad `Usuario.PasswordHash` lleva además `[JsonIgnore]`,
   de modo que aunque alguien serialice la entidad por descuido en el futuro, el hash no sale.
   Una prueba automatizada verifica exactamente eso.
5. **Validación en el registro.** Nombre no vacío, correo con `@` y contraseña de 8 caracteres
   como mínimo. El correo se normaliza (minúsculas, sin espacios) antes de guardar y de buscar,
   y un correo duplicado responde `409 Conflict`.
6. **Tiempo de respuesta constante ante correos desconocidos.** Si el correo no existe, el
   servicio verifica igualmente contra un hash señuelo. Sin esto, el login respondería mucho más
   rápido para correos inexistentes y ese margen permitiría enumerar qué cuentas están
   registradas.

### Dónde vive la decisión

El hashing queda **encapsulado dentro del módulo Usuarios**, coherente con el monolito modular
del ADR-03: ni el gateway ni los demás módulos saben cómo se almacena la credencial. La interfaz
pública `IUsuariosApi` recibe contraseñas en claro y devuelve entidades ya validadas.

---

## Alternativas consideradas

| Alternativa | Por qué la descarté |
|-------------|---------------------|
| **Hashear en el cliente (JavaScript)** y enviar el hash | No aporta nada: el hash se convierte en la credencial efectiva, y quien intercepte la red puede reenviarlo igual. Además impide subir el factor de trabajo del lado del servidor. |
| **SHA-256 con sal propia** | SHA-256 está diseñado para ser rápido, que es justo lo contrario de lo que se necesita: una GPU prueba miles de millones por segundo. BCrypt es deliberadamente lento y ajustable. |
| **Argon2id** | Técnicamente superior (resistente también a ataques con hardware dedicado) y sería mi elección en un sistema en producción real. Lo descarté aquí porque BCrypt tiene una biblioteca de una línea, ampliamente usada en .NET, y el tiempo del semestre es limitado. Queda anotado como mejora futura. |
| **Delegar en un proveedor de identidad** (Auth0, Google) | Elimina el problema de raíz, pero añade una dependencia externa, configuración de OAuth y una cuenta que administrar, para un proyecto académico individual. |
| **ASP.NET Core Identity** | Trae hashing, tokens y gestión de usuarios ya resueltos, pero impone su propio esquema de base de datos y su modelo de entidades, lo que chocaría con la separación por esquemas y las interfaces por módulo del ADR-03. |

---

## Consecuencias

**Lo que gano:**

- Una fuga de la base de datos ya no entrega contraseñas utilizables: revertir un hash BCrypt
  de factor 11 es computacionalmente caro por cada contraseña, y la sal impide atacarlas todas
  a la vez con tablas precalculadas.
- `GET /api/usuarios` deja de ser una filtración de credenciales.
- La API pasa a dar mensajes de error útiles (`400` con el motivo, `409` si el correo existe)
  en lugar de fallar en silencio.
- Cierra DT-01, la deuda de prioridad crítica del README.

**Lo que sacrifico o asumo:**

- **Latencia.** Con factor 11 cada login y cada registro cuestan unos 100 ms de CPU. Es
  deliberado —ese coste es lo que frena la fuerza bruta— pero convierte el factor de trabajo en
  un **punto de sensibilidad**: subirlo a 14 multiplicaría el coste por ocho y el plan gratuito
  de Render tiene una sola CPU compartida. Se documenta como **SP-01** en la evaluación ATAM.
- **Las cuentas anteriores dejan de funcionar.** Los registros creados antes de este cambio
  guardan texto plano; `BCrypt.Verify` los rechaza. Es lo correcto —no se debe validar contra
  texto plano ni «migrarlos» aceptando la contraseña una última vez— pero implica que esos
  usuarios de prueba tienen que registrarse de nuevo.
- **Sigue sin haber sesiones reales.** El login devuelve el usuario y el frontend guarda el id en
  `localStorage`; no hay token ni verificación de identidad en las siguientes peticiones.
  Cualquiera puede llamar a la API haciéndose pasar por otro id. Esta decisión arregla el
  almacenamiento de la credencial, **no** la autorización, y esa brecha queda como riesgo
  **R-01** en la evaluación ATAM.
- Una dependencia más (`BCrypt.Net-Next`), que el job de seguridad del CI audita.

---

## Verificación

`tests/CodeMX.Api.Tests` cubre esta decisión con 16 pruebas:

- El hash guardado no es la contraseña y empieza por `$2` (prefijo BCrypt).
- Dos usuarios con la misma contraseña obtienen hashes distintos (la sal funciona).
- La contraseña correcta autentica; la incorrecta y el correo inexistente devuelven `null`.
- El correo se compara ignorando mayúsculas y espacios.
- Un correo duplicado lanza `EmailYaRegistradoException`.
- Contraseñas de menos de 8 caracteres, nombre vacío o correo sin `@` lanzan
  `RegistroInvalidoException`.
- Ni la entidad `Usuario` ni `UsuarioDto` incluyen el hash al serializarse a JSON.

---

## Declaración de uso de IA

Se utilizó una herramienta de IA para apoyar la implementación del hashing, la redacción de este
ADR y el diseño de las pruebas. La identificación de DT-01 y la decisión de resolverla antes de
publicar la demo son propias.
