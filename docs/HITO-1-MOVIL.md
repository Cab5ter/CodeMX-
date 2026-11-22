# CodeMx móvil · Hito 1

Guía: https://proyecto-integrador-font-mobile-26-3.netlify.app/#hito-1

## Proyectos y recorrido

La app nativa está en `../FRONTEND-CELLPHONE/mobile/login_rive_animation`. Abre
`CodeMx-mobile.code-workspace` para trabajar con ambos repositorios. Se conserva
el nombre de paquete Dart para no romper el proyecto existente; el nombre visible
de la aplicación es **CodeMx**.

Al abrir la app aparece `LoginScreen` con el osito Rive. Firebase autentica al
usuario. Flutter envía su ID token a `POST /auth/firebase` de CodeMx; el servidor
verifica firma RS256, emisor, audiencia, caducidad, UID y fecha de autenticación
contra los certificados públicos de Google. El ID del proyecto debe coincidir en
Flutter y Spring. No se entrega la contraseña de Firebase al backend de CodeMx.

La API vincula el UID de Firebase a un estudiante de PostgreSQL y devuelve el JWT
que ya utiliza CodeMx. Flutter lo mantiene en memoria y lo adjunta como
`Authorization: Bearer ...` en las llamadas a `/api/retos`, `/api/cursos`,
`/api/envios` y `/api/ranking`. Después de celebrar, el osito da paso a CodeMx con
`pushReplacement`. No hay WebView ni dependencia del backend anterior.

El cierre de sesión borra la sesión de CodeMx, llama a Firebase `signOut` y limpia
la pila de rutas. Un 401 en el catálogo también devuelve al login. Cada apertura
empieza en el login, aunque Firebase aún conserve su sesión local.

## Alcance de Hito 1

- Login: solo comprueba que existan ambos valores. Todos los fallos muestran
  «Credenciales inválidas. Verifica tu correo y contraseña.» en un único banner y
  disparan `trigFail`. Conserva carga, bloqueo de duplicados y `trigSuccess`.
- Registro: `SignUpScreen`, correo exacto `@tecdesoftware.edu.mx`, mínimo 8
  caracteres, mayúscula, minúscula, número, símbolo y confirmación. Usa
  `createUserWithEmailAndPassword`, cierra la sesión automática de Firebase y vuelve
  al login con aviso.
- Recuperación: pantalla que llama a `sendPasswordResetEmail`. La confirmación no
  revela si el correo existe. La recepción debe comprobarse en el buzón real.
- Catálogo CodeMx: los **13 retos** que ya define `DataSeeder`, con título,
  categoría/dificultad y puntos. Incluye detalle, ejemplo y envío de solución.
  Cursos permite consultar módulos/lecciones y registrar teoría completada; Ranking
  consulta la tabla del mismo backend. No incluye duelos ni exámenes móviles.
- Configuración local de Firebase, archivos de servicio y perfiles de desarrollo
  excluidos de Git. No se alteran ni publican los valores Firebase locales.

**Diferencia respecto a la rúbrica:** la guía literalmente exige 10 productos con
precio, stock y categoría del proyecto anterior. Por la petición de conectar
exclusivamente CodeMx, se conserva su temática educativa y su catálogo de retos
con dificultad y puntos. Esto requiere validar esa equivalencia con el docente;
no representa cumplimiento literal del requisito de productos/precio/stock.

Si ya existe un estudiante web con el mismo correo, solo se vincula si Firebase
indica que ese correo está verificado. Esto conserva su progreso y evita que una
cuenta sin verificar se apropie de la identidad anterior. Un UID ya vinculado no
puede ser reemplazado desde el cliente.

## Ejecutar

1. Inicia PostgreSQL con la base `codemx_spring` según la configuración existente.
   Puedes sobrescribir `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y
   `SPRING_DATASOURCE_PASSWORD` sin guardar claves en el repositorio.
2. En `backend-java`, ejecuta `mvn spring-boot:run`. Escucha en `0.0.0.0:8080`.
   `CODEMX_FIREBASE_PROJECT_ID` debe ser el ID del mismo proyecto que Flutter. El
   valor local predeterminado es `flutter-spring-test`, un identificador público.
3. En la carpeta Flutter, conserva tu `lib/firebase_options.dart` local o créalo
   desde el ejemplo. Habilita Correo/Contraseña en Firebase Authentication y usa
   las opciones de la app registrada para la plataforma que vayas a ejecutar.
4. Ejecuta `flutter pub get` y `flutter run -d emulator-5554`. También puedes usar
   F5 con la tarea de arranque Android existente.

El emulador usa `http://10.0.2.2:8080`. En celular físico, teléfono y computadora
deben estar en la misma red y el firewall debe permitir el puerto 8080:

```sh
flutter run --dart-define=CODEMX_API_BASE_URL=http://192.168.1.100:8080
```

La versión web de Flutter usa `http://localhost:8080`; el CORS existente de CodeMx
admite localhost. La demostración de la entrega debe hacerse en Android/iOS.

## Comprobaciones

Backend, desde `backend-java`:

```sh
mvn test
```

App, desde la carpeta Flutter:

```sh
flutter analyze
flutter test
flutter build apk --debug
```

Las pruebas Java verifican firmas RSA, emisor/audiencia, expiración, tokens
incompletos, creación de identidades, reutilización de UID y protección de cuentas
previas. Las pruebas Flutter verifican el contrato HTTP real de CodeMx con respuestas
controladas, los errores globales, el registro, la recuperación, cierre de sesión,
navegación y peticiones repetidas. No sustituyen una prueba de Firebase en Android.

La prueba optativa `integration_test/hito1_live_test.dart` usa servicios reales,
crea una cuenta y solicita un correo. Solo debe ejecutarse con una dirección propia
u otra dirección autorizada. Proporciona `TEST_EMAIL`, `TEST_PASSWORD` y
`TEST_CREATE_ACCOUNT` en un JSON privado fuera del repositorio. Para repetir con
una cuenta ya creada, cambia `TEST_CREATE_ACCOUNT` a `false`.

```sh
flutter drive --driver=test_driver/integration_test.dart \
  --target=integration_test/hito1_live_test.dart -d emulator-5554 \
  --dart-define-from-file=/ruta/privada/hito1.json
```

Para activar las capturas del driver agrega `TEST_SCREENSHOTS: "true"` al JSON privado. Por defecto están desactivadas porque el capturador integrado puede bloquear algunos emuladores; también puedes capturar directamente desde Android. Las capturas del driver se guardan en `build/evidence/hito1/`. No se guardan tokens ni
contraseñas en las capturas. La evidencia de solicitud del correo no demuestra su
recepción: hay que abrir el buzón y documentarla para Teams.

## Entrega académica

Hito 1: Teams **24 de noviembre**, revisión **25 de noviembre**. La guía pide un
video de máximo un minuto o capturas en dispositivo móvil mostrando registro,
error global y correo recibido, además de tres commits en tres días distintos.
El historial Flutter ya contiene trabajo en días distintos; no se han fabricado
fechas ni creado commits automáticamente. Hito 2 queda fuera de este cambio.

## Resultado de la verificación del 22 de noviembre de 2026

- `mvn test`: **11 pruebas correctas**.
- `flutter test`: **22 pruebas correctas**.
- `flutter analyze`: **sin incidencias**.
- Primera prueba en `Contador_Leonardo` / `emulator-5554`: registro real de
  `test.test@tecdesoftware.edu.mx`, rechazo genérico, login, intercambio de token,
  consulta de retos/cursos/ranking, cierre de sesión y recuperación solicitada:
  **correcta**.
- Firebase aceptó la solicitud de recuperación. El usuario aún no ha revisado el
  buzón, por lo que **no está confirmada la recepción**.
- Base de prueba: PostgreSQL temporal `codemx_hito1` en `127.0.0.1:55432`, con
  **13 retos** y un estudiante vinculado a Firebase. No se modificó la base
  habitual `codemx_spring`; no estaba disponible en el puerto 5432.

El backend de esta sesión se inició con:

```sh
mvn spring-boot:run \
  -Dspring-boot.run.arguments='--spring.datasource.url=jdbc:postgresql://127.0.0.1:55432/codemx_hito1 --spring.datasource.username=codemx'
```

La configuración privada de la prueba está en `/tmp/codemx-hito1-live.json`, con
permisos de lectura/escritura solo para su propietario. Contiene la contraseña
aleatoria de la cuenta de prueba y no forma parte de los repositorios. El directorio
`/tmp` puede vaciarse al reiniciar; para conservar la cuenta usa la recuperación de
contraseña. No es necesario repetir el registro ni enviar otro correo para
repetir la prueba de acceso: usa `TEST_CREATE_ACCOUNT: "false"` y
`TEST_SEND_RECOVERY: "false"`.

## APK para teléfono físico

La IP Wi-Fi de esta computadora durante la verificación fue `192.168.101.98`.
El APK `codemx-telefono-debug.apk` se compila con
`CODEMX_API_BASE_URL=http://192.168.101.98:8080`. El teléfono debe estar en la misma
red Wi-Fi, la API debe seguir encendida y el puerto 8080 debe ser accesible. Si la
IP cambia, vuelve a compilar:

```sh
flutter build apk --debug --target=lib/main.dart \
  --dart-define=CODEMX_API_BASE_URL=http://192.168.101.98:8080
```

`codemx-debug.apk` conserva la dirección `10.0.2.2:8080` para Android Emulator.
Ambos son builds de desarrollo para esta práctica. La prueba se ejecutó en
emulador Android; no había un teléfono físico conectado para verificar la red
Wi-Fi desde ese dispositivo.

También se comprobó directamente el ciclo real de la API: Firebase → sesión
CodeMx → 13 retos / 3 módulos → solución `ACEPTADO` → 10 puntos en ranking. La
API respondió HTTP 200 en `http://192.168.101.98:8080/v3/api-docs`.

Las capturas de la primera prueba real están en `docs/evidencias/hito1/`. Incluyen
el registro, error global, cuenta creada, catálogo, cursos, ranking y solicitud
de recuperación.

El APK para teléfono necesita que la computadora conserve esa IP y que el
backend siga disponible. La API de esta sesión usa una base temporal de prueba;
para uso habitual prepara `codemx_spring` o configura tu servidor permanente.

### Límite de la verificación móvil

La primera ejecución real completa pasó y produjo las diez capturas originales.
Los reintentos tras compactar la interfaz se interrumpieron porque el proceso del
emulador desapareció, tanto con el capturador integrado como sin él. Por ello no
se presenta la última ejecución de integración como aprobada. Los 22 tests de
Flutter, 11 tests Java, análisis estático y comprobación directa con Firebase/API
sí pasaron. Los dos APK normales compilaron correctamente. El teléfono físico no
se verificó por no haber uno conectado.

Finalmente se instaló el APK normal `codemx-debug.apk` en Android con resultado
`Success` y se abrió su actividad principal. Se inició el emulador con Vulkan
desactivado, gráficos por software para esta comprobación. Estos
ajustes son locales del emulador y no cambian los APK del proyecto.
