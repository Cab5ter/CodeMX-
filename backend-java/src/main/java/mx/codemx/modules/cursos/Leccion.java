package mx.codemx.modules.cursos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/** Lección de un módulo: teoría o ejercicio enlazado a un reto. */
@Entity
@Table(name = "lecciones", schema = "cursos")
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lado <b>muchos</b> —y dueño— de la relación con {@link Modulo}: mantiene la
     * columna {@code modulo_id}.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

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

    /**
     * Para EJERCICIO: el reto que el alumno debe resolver.
     *
     * <p>Se queda como columna suelta a propósito: apunta al módulo <b>Retos</b>, y una
     * foreign key entre esquemas soldaría los dos módulos en la base de datos, que es
     * justo lo que el ADR-03 evita. La resolución va por {@code RetosApi}.
     */
    @Column(name = "reto_id")
    private Long retoId;

    @Column(name = "orden", nullable = false)
    private int orden;

    /**
     * Lado <b>uno</b> de la relación con {@link ProgresoLeccion}: una lección acumula el
     * avance de varios alumnos. Si se borra la lección, su avance se va con ella.
     */
    @OneToMany(mappedBy = "leccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgresoLeccion> progresos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public List<ProgresoLeccion> getProgresos() {
        return progresos;
    }

    public void setProgresos(List<ProgresoLeccion> progresos) {
        this.progresos = progresos;
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
