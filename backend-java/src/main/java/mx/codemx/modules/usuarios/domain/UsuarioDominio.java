package mx.codemx.modules.usuarios.domain;

import java.time.Instant;

/**
 * Modelo de dominio del módulo Usuarios.
 *
 * <p>POJO puro: <b>no lleva ninguna anotación de JPA</b> y no sabe nada de tablas, esquemas
 * ni columnas. Es lo que expone el gateway; la entidad {@link mx.codemx.modules.usuarios.Usuario}
 * queda confinada a la capa de persistencia y la traducción entre ambos la hace
 * {@link mx.codemx.modules.usuarios.mapper.UsuarioMapper} (MapStruct).
 *
 * <p>El identificador es {@link Integer} —y no {@code int}— para que un usuario todavía no
 * persistido pueda representarse con {@code null} en vez de un 0 engañoso.
 */
public class UsuarioDominio {

    private Integer id;
    private String nombre;
    private String email;
    private String passwordHash;

    /** En la entidad este campo se llama {@code creadoEn}; el mapeador traduce el nombre. */
    private Instant fechaRegistro;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Instant fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
