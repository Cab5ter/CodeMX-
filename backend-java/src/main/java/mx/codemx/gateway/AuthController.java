package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mx.codemx.modules.auth.AuthApi;
import mx.codemx.modules.auth.LoginRequest;
import mx.codemx.modules.auth.LoginResponse;
import mx.codemx.security.UsuarioAutenticado;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = """
        Inicio de sesión y emisión de tokens. Es el único grupo de endpoints, junto con el alta de
        cuenta, que se puede llamar sin token.""")
public class AuthController {

    private final AuthApi auth;

    public AuthController(AuthApi auth) {
        this.auth = auth;
    }

    @Operation(
            summary = "Iniciar sesión y obtener un token",
            description = """
                    Compara la contraseña recibida contra el hash BCrypt guardado y, si coincide, emite
                    un JWT firmado con HS256 que vale 24 horas.

                    Para usar el token desde esta misma página: cópialo, pulsa el botón **Authorize** de
                    arriba y pégalo. Swagger lo mandará como `Authorization: Bearer <token>` en todas las
                    llamadas siguientes.

                    Si aún no tienes cuenta, créala primero en `POST /api/usuarios`, que también es
                    pública.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales válidas; devuelve el token"),
            @ApiResponse(responseCode = "400", description = "Falta el correo o la contraseña"),
            @ApiResponse(responseCode = "401", description = "Correo inexistente o contraseña incorrecta")
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest credenciales) {
        return ResponseEntity.ok(auth.iniciarSesion(credenciales));
    }

    @Operation(
            summary = "Ver quién soy",
            description = """
                    Devuelve el estudiante que va dentro del token de la petición. Sirve para que el
                    cliente compruebe que su token sigue siendo válido sin tener que adivinarlo por un
                    401 en otra ruta.""",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido; devuelve al estudiante"),
            @ApiResponse(responseCode = "401", description = "Sin token, o el token ya no es válido")
    })
    @GetMapping("/yo")
    public ResponseEntity<UsuarioAutenticado> yo(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(usuario);
    }
}
