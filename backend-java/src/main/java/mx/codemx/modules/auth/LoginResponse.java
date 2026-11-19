package mx.codemx.modules.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sesión iniciada: token JWT y datos básicos del estudiante.")
public record LoginResponse(

        @Schema(description = "JWT firmado con HS256. Mándalo en 'Authorization: Bearer <token>'.")
        String token,

        @Schema(description = "Esquema con el que se manda el token", example = "Bearer")
        String tipo,

        @Schema(description = "Segundos que le quedan de vigencia al token", example = "86400")
        long expiraEnSegundos,

        @Schema(description = "Identificador del estudiante", example = "1")
        long usuarioId,

        @Schema(description = "Nombre visible del estudiante", example = "Tu nombre completo")
        String nombre,

        @Schema(description = "Correo de la cuenta", example = "tu@correo.com")
        String email) {

    public static LoginResponse de(String token, long expiraEnSegundos,
                                   long usuarioId, String nombre, String email) {
        return new LoginResponse(token, "Bearer", expiraEnSegundos, usuarioId, nombre, email);
    }
}
