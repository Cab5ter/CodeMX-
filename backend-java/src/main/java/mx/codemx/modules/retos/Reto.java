package mx.codemx.modules.retos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "retos", schema = "retos")
public class    Reto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo = "";

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad = Dificultad.BASICO;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn = Instant.now();

    @OneToMany(mappedBy = "reto", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CasoPrueba> casos = new ArrayList<>();

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

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public List<CasoPrueba> getCasos() {
        return casos;
    }

    public void setCasos(List<CasoPrueba> casos) {
        this.casos = casos;
    }

    public void agregarCaso(CasoPrueba caso) {
        casos.add(caso);
        caso.setReto(this);
    }
}
