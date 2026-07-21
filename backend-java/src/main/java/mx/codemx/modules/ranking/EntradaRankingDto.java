package mx.codemx.modules.ranking;

/**
 * Entrada del ranking enriquecida con el nombre del jugador (Ranking consulta a Usuarios por
 * su interfaz pública). Es lo que ve el frontend, para no mostrar "Usuario #id".
 */
public record EntradaRankingDto(
        long id, long usuarioId, String nombre, int puntajeTotal, int retosResueltos) {
}
