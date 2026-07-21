package mx.codemx.modules.cursos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Lección de un módulo: teoría o ejercicio enlazado a un reto. */
@Entity
@Table(name = "lecciones", schema = "cursos")
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private long moduloId;

    @Column(nullable = false)
    private String titulo = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLeccion tipo = TipoLeccion.TEORIA;

    // Para TEORIA: el texto explicativo y un ejemplo de código Python (opcional).
    @Column(columnDefinition = "text")
    private String contenido;

    @Column(name = "ejemplo_codigo", columnDefinition = "text")
    private String ejemploCodigo;

    // Para EJERCICIO: el reto que el alumno debe resolver.
    @Column(name = "reto_id")
    private Long retoId;

    @Column(name = "orden", nullable = false)
    private int orden;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getModuloId() {
        return moduloId;
    }

    public void setModuloId(long moduloId) {
        this.moduloId = moduloId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoLeccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoLeccion tipo) {
        this.tipo = tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getEjemploCodigo() {
        return ejemploCodigo;
    }

    public void setEjemploCodigo(String ejemploCodigo) {
        this.ejemploCodigo = ejemploCodigo;
    }

    public Long getRetoId() {
        return retoId;
    }

    public void setRetoId(Long retoId) {
        this.retoId = retoId;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
