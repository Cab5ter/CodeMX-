package mx.codemx.cursos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Marca que un usuario completó una lección de TEORIA.
 * Las lecciones de EJERCICIO no usan esta tabla: su avance se deduce de los
 * envíos ACEPTADOS en el módulo Retos.
 */
@Entity
@Table(name = "progreso_lecciones",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "leccion_id"}))
public class ProgresoLeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "leccion_id", nullable = false)
    private Long leccionId;

    @Column(name = "completada_en", nullable = false)
    private LocalDateTime completadaEn = LocalDateTime.now();

    public ProgresoLeccion() {}

    public ProgresoLeccion(Long usuarioId, Long leccionId) {
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLeccionId() { return leccionId; }
    public void setLeccionId(Long leccionId) { this.leccionId = leccionId; }

    public LocalDateTime getCompletadaEn() { return completadaEn; }
    public void setCompletadaEn(LocalDateTime completadaEn) { this.completadaEn = completadaEn; }
}
