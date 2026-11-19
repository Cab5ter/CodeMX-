package mx.codemx.modules.envios;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Código a evaluar contra los casos de prueba de un reto.")
public record EnvioRequest(

        @Positive(message = "El retoId es obligatorio y debe ser mayor que cero")
        @Schema(description = "Reto que se está resolviendo; lo da GET /api/retos", example = "1")
        long retoId,

        @NotBlank(message = "El código fuente no puede estar vacío")
        @Schema(description = "Código Python a evaluar", example = "print(\"Hola, Mundo!\")")
        String codigoFuente) {

    public Envio aEnvio(long usuarioId) {
        Envio envio = new Envio();
        envio.setUsuarioId(usuarioId);
        envio.setRetoId(retoId);
        envio.setCodigoFuente(codigoFuente);
        return envio;
    }
}
