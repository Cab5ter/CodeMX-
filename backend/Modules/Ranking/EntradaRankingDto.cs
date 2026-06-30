namespace CodeMX.Api.Modules.Ranking;

/// <summary>
/// Entrada del ranking enriquecida con el nombre del jugador (Ranking consulta a Usuarios por
/// su interfaz pública). Es lo que ve el frontend, para no mostrar "Usuario #id".
/// </summary>
public record EntradaRankingDto(long Id, long UsuarioId, string Nombre, int PuntajeTotal, int RetosResueltos);
