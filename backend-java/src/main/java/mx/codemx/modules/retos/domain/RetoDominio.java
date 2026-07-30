package mx.codemx.modules.retos.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import mx.codemx.modules.retos.Dificultad;

public class RetoDominio {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo asigna la base de datos al crear")
    private Integer id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    @Schema(description = "Título corto del reto", example = "Suma de dos números")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Enunciado que ve el estudiante", example = "Lee dos enteros y imprime su suma.")
    private String descripcion;

    @NotNull(message = "La dificultad es obligatoria: BASICO, INTERMEDIO o AVANZADO")
    @Schema(description = "Nivel del reto; determina los puntos que otorga", example = "BASICO")
    private Dificultad dificultad;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo asigna el servidor al crear")
    private Instant fechaCreacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
