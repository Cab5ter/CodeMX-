package mx.codemx.envios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios", schema = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "reto_id", nullable = false)
    private Long retoId;

    @Column(name = "codigo_fuente", nullable = false, columnDefinition = "TEXT")
    private String codigoFuente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Veredicto veredicto = Veredicto.PENDIENTE;

    @Column(name = "enviado_en", nullable = false, updatable = false)
    private LocalDateTime enviadoEn = LocalDateTime.now();

    public Envio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getRetoId() { return retoId; }
    public void setRetoId(Long retoId) { this.retoId = retoId; }

    public String getCodigoFuente() { return codigoFuente; }
    public void setCodigoFuente(String codigoFuente) { this.codigoFuente = codigoFuente; }

    public Veredicto getVeredicto() { return veredicto; }
    public void setVeredicto(Veredicto veredicto) { this.veredicto = veredicto; }

    public LocalDateTime getEnviadoEn() { return enviadoEn; }
    public void setEnviadoEn(LocalDateTime enviadoEn) { this.enviadoEn = enviadoEn; }
}
