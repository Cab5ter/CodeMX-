package mx.codemx.cursos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "preguntas_examen", schema = "cursos")
public class PreguntaExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private Long moduloId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @Column(name = "opcion_a", nullable = false)
    private String opcionA;

    @Column(name = "opcion_b", nullable = false)
    private String opcionB;

    @Column(name = "opcion_c", nullable = false)
    private String opcionC;

    @Column(name = "opcion_d", nullable = false)
    private String opcionD;

    // Índice de la opción correcta (0 = A, 1 = B, 2 = C, 3 = D).
    @Column(nullable = false)
    private Integer correcta;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    public PreguntaExamen() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getModuloId() { return moduloId; }
    public void setModuloId(Long moduloId) { this.moduloId = moduloId; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getOpcionA() { return opcionA; }
    public void setOpcionA(String opcionA) { this.opcionA = opcionA; }

    public String getOpcionB() { return opcionB; }
    public void setOpcionB(String opcionB) { this.opcionB = opcionB; }

    public String getOpcionC() { return opcionC; }
    public void setOpcionC(String opcionC) { this.opcionC = opcionC; }

    public String getOpcionD() { return opcionD; }
    public void setOpcionD(String opcionD) { this.opcionD = opcionD; }

    public Integer getCorrecta() { return correcta; }
    public void setCorrecta(Integer correcta) { this.correcta = correcta; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
