package mx.codemx.modules.retos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Caso de prueba de un reto (entrada y salida esperada).
 *
 * <p>También se usa (sin persistir) para los casos de un problema de duelo generado al
 * vuelo por Claude; en ese escenario el {@code id} y el {@code retoId} quedan nulos.
 */
@Entity
@Table(name = "casos_prueba", schema = "retos")
public class CasoPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reto_id")
    private Long retoId;

    @Column(name = "input_data", columnDefinition = "text")
    private String inputData;

    @Column(name = "output_esperado", nullable = false, columnDefinition = "text")
    private String outputEsperado = "";

    public CasoPrueba() {
    }

    /** Constructor de conveniencia para casos en memoria (duelos), que nunca se persisten. */
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

    public Long getRetoId() {
        return retoId;
    }

    public void setRetoId(Long retoId) {
        this.retoId = retoId;
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
