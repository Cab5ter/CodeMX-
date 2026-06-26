package mx.codemx.retos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "casos_prueba", schema = "retos")
public class CasoPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reto_id", nullable = false)
    private Long retoId;

    @Column(name = "input_data", columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "output_esperado", nullable = false, columnDefinition = "TEXT")
    private String outputEsperado;

    public CasoPrueba() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRetoId() { return retoId; }
    public void setRetoId(Long retoId) { this.retoId = retoId; }

    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }

    public String getOutputEsperado() { return outputEsperado; }
    public void setOutputEsperado(String outputEsperado) { this.outputEsperado = outputEsperado; }
}
