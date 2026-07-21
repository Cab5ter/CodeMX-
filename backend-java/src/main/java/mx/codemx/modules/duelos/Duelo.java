package mx.codemx.modules.duelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import mx.codemx.modules.retos.Dificultad;

/**
 * Entidad de dominio del módulo Duelos. Registra un enfrentamiento 1 vs 1 entre dos
 * usuarios sobre un problema de programación. El enunciado y los casos de prueba del
 * problema viven en memoria mientras el duelo está en curso (módulo de tiempo real);
 * aquí sólo persiste lo necesario para el ranking y el historial.
 */
@Entity
@Table(name = "duelos", schema = "duelos")
public class Duelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jugador1_id", nullable = false)
    private long jugador1Id;

    @Column(name = "jugador2_id", nullable = false)
    private long jugador2Id;

    /** Quién resolvió primero. {@code null} mientras el duelo sigue en curso. */
    @Column(name = "ganador_id")
    private Long ganadorId;

    @Column(nullable = false)
    private String titulo = "";

    /** Dificultad elegida por ambos jugadores; define los puntos en juego. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad = Dificultad.INTERMEDIO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDuelo estado = EstadoDuelo.EN_CURSO;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "terminado_en")
    private Instant terminadoEn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getJugador1Id() {
        return jugador1Id;
    }

    public void setJugador1Id(long jugador1Id) {
        this.jugador1Id = jugador1Id;
    }

    public long getJugador2Id() {
        return jugador2Id;
    }

    public void setJugador2Id(long jugador2Id) {
        this.jugador2Id = jugador2Id;
    }

    public Long getGanadorId() {
        return ganadorId;
    }

    public void setGanadorId(Long ganadorId) {
        this.ganadorId = ganadorId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public EstadoDuelo getEstado() {
        return estado;
    }

    public void setEstado(EstadoDuelo estado) {
        this.estado = estado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Instant getTerminadoEn() {
        return terminadoEn;
    }

    public void setTerminadoEn(Instant terminadoEn) {
        this.terminadoEn = terminadoEn;
    }
}
