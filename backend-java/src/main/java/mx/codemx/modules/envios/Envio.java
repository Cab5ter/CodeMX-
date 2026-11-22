package mx.codemx.modules.envios;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

@Entity
@Table(name = "envios", schema = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo asigna la base de datos al crear")
    private Long id;

    @Positive(message = "El usuarioId es obligatorio y debe ser mayor que cero")
    @Schema(description = "Estudiante que hace el envío", example = "1")
    @Column(name = "usuario_id", nullable = false)
    private long usuarioId;

    @Positive(message = "El retoId es obligatorio y debe ser mayor que cero")
    @Schema(description = "Reto que se está resolviendo", example = "1")
    @Column(name = "reto_id", nullable = false)
    private long retoId;

    @NotBlank(message = "El código fuente no puede estar vacío")
    @Schema(description = "Código Python a evaluar", example = "print(\"Hola, Mundo!\")")
    @Column(name = "codigo_fuente", nullable = false, columnDefinition = "text")
    private String codigoFuente = "";

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo determina el evaluador")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Veredicto veredicto = Veredicto.PENDIENTE;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Lo asigna el servidor al crear")
    @Column(name = "enviado_en", nullable = false)
    private Instant enviadoEn = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getRetoId() {
        return retoId;
    }

    public void setRetoId(long retoId) {
        this.retoId = retoId;
    }

    public String getCodigoFuente() {
        return codigoFuente;
    }

    public void setCodigoFuente(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    public Veredicto getVeredicto() {
        return veredicto;
    }

    public void setVeredicto(Veredicto veredicto) {
        this.veredicto = veredicto;
    }

    public Instant getEnviadoEn() {
        return enviadoEn;
    }

    public void setEnviadoEn(Instant enviadoEn) {
        this.enviadoEn = enviadoEn;
    }
}
