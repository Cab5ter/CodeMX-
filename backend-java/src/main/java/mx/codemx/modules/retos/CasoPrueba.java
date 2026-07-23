package mx.codemx.modules.retos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "casos_prueba", schema = "retos")
public class CasoPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reto_id")
    @JsonIgnore
    private Reto reto;

    @Column(name = "input_data", columnDefinition = "text")
    private String inputData;

    @Column(name = "output_esperado", nullable = false, columnDefinition = "text")
    private String outputEsperado = "";

    public CasoPrueba() {
    }

    public CasoPrueba(String inputData, String outputEsperado) {
        this.inputData = inputData;
        this.outputEsperado = outputEsperado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reto getReto() {
        return reto;
    }

    public void setReto(Reto reto) {
        this.reto = reto;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputEsperado() {
        return outputEsperado;
    }

    public void setOutputEsperado(String outputEsperado) {
        this.outputEsperado = outputEsperado;
    }
}
