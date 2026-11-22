package mx.codemx.modules.envios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Entidad de dominio del módulo Envíos. */
@Entity
@Table(name = "envios", schema = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private long usuarioId;

    @Column(name = "reto_id", nullable = false)
    private long retoId;

    @Column(name = "codigo_fuente", nullable = false, columnDefinition = "text")
    private String codigoFuente = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Veredicto veredicto = Veredicto.PENDIENTE;

    @Column(name = "enviado_en", nullable = false)
    private Instant enviadoEn = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getRetoId() {
        return retoId;
    }

    public void setRetoId(long retoId) {
        this.retoId = retoId;
    }

    public String getCodigoFuente() {
        return codigoFuente;
    }

    public void setCodigoFuente(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    public Veredicto getVeredicto() {
        return veredicto;
    }

    public void setVeredicto(Veredicto veredicto) {
        this.veredicto = veredicto;
    }

    public Instant getEnviadoEn() {
        return enviadoEn;
    }

    public void setEnviadoEn(Instant enviadoEn) {
        this.enviadoEn = enviadoEn;
    }
}
