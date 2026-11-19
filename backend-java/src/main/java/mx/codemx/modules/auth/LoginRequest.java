package mx.codemx.modules.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Correo y contraseña con los que se registró el estudiante.")
public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Schema(description = "Correo de la cuenta", example = "tu@correo.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña en texto plano; viaja solo por el cuerpo de la petición",
                example = "cambia-esto")
        String contrasena) {
}
