package mx.codemx.modules.cursos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "progreso_lecciones", schema = "cursos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "leccion_id"}))
public class ProgresoLeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private long usuarioId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leccion_id", nullable = false)
    private Leccion leccion;

    @Column(name = "completada_en", nullable = false)
    private Instant completadaEn = Instant.now();

    public ProgresoLeccion() {
    }

    public ProgresoLeccion(long usuarioId, Leccion leccion) {
        this.usuarioId = usuarioId;
        this.leccion = leccion;
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

    public Leccion getLeccion() {
        return leccion;
    }

    public void setLeccion(Leccion leccion) {
        this.leccion = leccion;
    }

    public Instant getCompletadaEn() {
        return completadaEn;
    }

    public void setCompletadaEn(Instant completadaEn) {
        this.completadaEn = completadaEn;
    }
}
