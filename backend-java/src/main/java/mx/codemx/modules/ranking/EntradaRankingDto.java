package mx.codemx.modules.ranking;

public record EntradaRankingDto(
        long id, long usuarioId, String nombre, int puntajeTotal, int retosResueltos) {
}
