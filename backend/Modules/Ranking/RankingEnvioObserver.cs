using CodeMX.Api.Modules.Envios;

namespace CodeMX.Api.Modules.Ranking;

/// <summary>
/// Observador concreto (Observer): cuando Envíos publica un envío ACEPTADO, el módulo
/// Ranking reacciona registrando el acierto (que a su vez consulta los puntos a Retos).
/// </summary>
public class RankingEnvioObserver : IEnvioObserver
{
    private readonly IRankingApi _ranking;

    public RankingEnvioObserver(IRankingApi ranking) => _ranking = ranking;

    public Task OnEnvioAceptadoAsync(EnvioAceptadoEvent evento) =>
        _ranking.RegistrarAciertoAsync(evento.UsuarioId, evento.RetoId);
}
