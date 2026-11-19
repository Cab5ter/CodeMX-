package mx.codemx.modules.usuarios.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class UsuarioDominio {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo asigna la base de datos al crear")
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "Nombre visible del estudiante", example = "Tu nombre completo")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Schema(
            description = """
                    Correo único del estudiante. El valor de ejemplo es solo un marcador: sustitúyelo por
                    un correo real antes de enviar, porque el correo lleva restricción de unicidad y el
                    primero que se registre con él ocupa el lugar.""",
            example = "tu@correo.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    @Schema(
            accessMode = Schema.AccessMode.WRITE_ONLY,
            description = "Contraseña en texto plano; ver la deuda técnica DT-01. Nunca se devuelve en las respuestas.",
            example = "cambia-esto")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo asigna el servidor al crear")
    private Instant fechaRegistro;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Instant fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
