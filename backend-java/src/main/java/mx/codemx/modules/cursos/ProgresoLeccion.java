package mx.codemx.modules.cursos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * Marca que un usuario completó una lección de TEORIA. Las de EJERCICIO no usan
 * esta tabla: su avance se deduce de los envíos ACEPTADOS (módulo Envíos).
 */
@Entity
@Table(name = "progreso_lecciones", schema = "cursos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "leccion_id"}))
public class ProgresoLeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private long usuarioId;

    @Column(name = "leccion_id", nullable = false)
    private long leccionId;

    @Column(name = "completada_en", nullable = false)
    private Instant completadaEn = Instant.now();

    public ProgresoLeccion() {
    }

    public ProgresoLeccion(long usuarioId, long leccionId) {
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
    }

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

    public long getLeccionId() {
        return leccionId;
    }

    public void setLeccionId(long leccionId) {
        this.leccionId = leccionId;
    }

    public Instant getCompletadaEn() {
        return completadaEn;
    }

    public void setCompletadaEn(Instant completadaEn) {
        this.completadaEn = completadaEn;
    }
}
