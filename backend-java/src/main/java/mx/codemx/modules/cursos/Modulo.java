package mx.codemx.modules.cursos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modulos", schema = "cursos")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo = "";

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion = "";

    @Column(nullable = false)
    private String icono = "";

    @Column(name = "orden", nullable = false)
    private int orden;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<Leccion> lecciones = new ArrayList<>();

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<PreguntaExamen> preguntas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public List<Leccion> getLecciones() {
        return lecciones;
    }

    public void setLecciones(List<Leccion> lecciones) {
        this.lecciones = lecciones;
    }

    public List<PreguntaExamen> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<PreguntaExamen> preguntas) {
        this.preguntas = preguntas;
    }

    public void agregarLeccion(Leccion leccion) {
        lecciones.add(leccion);
        leccion.setModulo(this);
    }

    public void agregarPregunta(PreguntaExamen pregunta) {
        preguntas.add(pregunta);
        pregunta.setModulo(this);
    }
}
