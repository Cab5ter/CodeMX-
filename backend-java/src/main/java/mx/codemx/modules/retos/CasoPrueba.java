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

/**
 * Caso de prueba de un reto (entrada y salida esperada).
 *
 * <p>También se usa (sin persistir) para los casos de un problema de duelo generado al
 * vuelo por Claude; en ese escenario el {@code id} y el {@code reto} quedan nulos.
 */
@Entity
@Table(name = "casos_prueba", schema = "retos")
public class CasoPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lado <b>muchos</b> —y dueño— de la relación muchos&nbsp;a&nbsp;uno con {@link Reto}:
     * este extremo es el que mantiene la columna {@code reto_id}.
     *
     * <p>{@code LAZY} evita traer el reto entero cada vez que se leen los casos para
     * evaluar un envío, que es el camino caliente. {@code @JsonIgnore} corta la recursión
     * infinita al serializar: sin él, {@code GET /api/retos/{id}/ejemplo} devolvería el
     * caso → su reto → los casos del reto → … indefinidamente.
     */
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
