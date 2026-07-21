package mx.codemx.modules.retos.domain;

import java.time.Instant;
import mx.codemx.modules.retos.Dificultad;

/**
 * Modelo de dominio del módulo Retos.
 *
 * <p>POJO puro: <b>no lleva ninguna anotación de JPA</b>. La entidad
 * {@link mx.codemx.modules.retos.Reto} vive sólo en la capa de persistencia y
 * {@link mx.codemx.modules.retos.mapper.RetoMapper} (MapStruct) traduce entre ambas.
 *
 * <p>{@link Dificultad} sí se comparte con la entidad: es un enum del dominio, no una
 * construcción de persistencia (la entidad decide aparte cómo almacenarlo).
 *
 * <p>El identificador es {@link Integer} para poder representar con {@code null} un reto
 * que aún no se ha persistido.
 */
public class RetoDominio {

    private Integer id;
    private String titulo;
    private String descripcion;
    private Dificultad dificultad;

    /** En la entidad este campo se llama {@code creadoEn}; el mapeador traduce el nombre. */
    private Instant fechaCreacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
