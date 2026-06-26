package mx.codemx.cursos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lecciones", schema = "cursos")
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private Long moduloId;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLeccion tipo;

    // Para lecciones de TEORIA: el texto explicativo.
    @Column(columnDefinition = "TEXT")
    private String contenido;

    // Para lecciones de TEORIA: un ejemplo de código Python (opcional).
    @Column(name = "ejemplo_codigo", columnDefinition = "TEXT")
    private String ejemploCodigo;

    // Para lecciones de EJERCICIO: el reto que el alumno debe resolver.
    @Column(name = "reto_id")
    private Long retoId;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    public Leccion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getModuloId() { return moduloId; }
    public void setModuloId(Long moduloId) { this.moduloId = moduloId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public TipoLeccion getTipo() { return tipo; }
    public void setTipo(TipoLeccion tipo) { this.tipo = tipo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getEjemploCodigo() { return ejemploCodigo; }
    public void setEjemploCodigo(String ejemploCodigo) { this.ejemploCodigo = ejemploCodigo; }

    public Long getRetoId() { return retoId; }
    public void setRetoId(Long retoId) { this.retoId = retoId; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
