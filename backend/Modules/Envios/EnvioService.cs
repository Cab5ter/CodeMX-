using CodeMX.Api.Modules.Evaluacion;

namespace CodeMX.Api.Modules.Envios;

/// <summary>
/// Implementación del módulo Envíos. Coordina el flujo de un envío:
///   1. Guarda el envío como PENDIENTE.
///   2. Pide la evaluación al módulo Evaluación (IEvaluacionApi).
///   3. Actualiza el veredicto.
///   4. Si es el primer ACEPTADO del usuario en ese reto, <b>publica el evento</b> a los
///      observadores registrados (patrón Observer). Envíos ya no conoce a Ranking: solo
///      notifica; quien quiera reaccionar se suscribe como IEnvioObserver.
/// </summary>
public class EnvioService : IEnviosApi
{
    private readonly EnvioRepository _repo;
    private readonly IEvaluacionApi _evaluacion;
    private readonly IEnumerable<IEnvioObserver> _observers;

    public EnvioService(EnvioRepository repo, IEvaluacionApi evaluacion, IEnumerable<IEnvioObserver> observers)
    {
        _repo = repo;
        _evaluacion = evaluacion;
        _observers = observers;
    }

    public async Task<Envio> EnviarAsync(Envio envio)
    {
        envio.Veredicto = Veredicto.PENDIENTE;
        var guardado = await _repo.Guardar(envio);

        try
        {
            var solicitud = new SolicitudEvaluacion(envio.RetoId, envio.CodigoFuente);
            var resultado = await _evaluacion.EvaluarAsync(solicitud);

            var veredicto = Enum.Parse<Veredicto>(resultado.Veredicto);

            bool esPrimerAceptado = veredicto == Veredicto.ACEPTADO
                && !await _repo.TieneAceptado(envio.UsuarioId, envio.RetoId);

            guardado.Veredicto = veredicto;
            guardado = await _repo.Guardar(guardado);

            if (esPrimerAceptado)
            {
                var evento = new EnvioAceptadoEvent(envio.UsuarioId, envio.RetoId);
                foreach (var observer in _observers)
                    await observer.OnEnvioAceptadoAsync(evento);
            }
        }
        catch
        {
            guardado.Veredicto = Veredicto.ERROR_EN_EJECUCION;
            await _repo.Guardar(guardado);
        }

        return guardado;
    }

    public Task<Envio?> BuscarPorIdAsync(long id) => _repo.BuscarPorId(id);

    public Task<List<Envio>> ListarPorUsuarioAsync(long usuarioId) => _repo.ListarPorUsuario(usuarioId);

    public Task<List<Envio>> ListarPorRetoAsync(long retoId) => _repo.ListarPorReto(retoId);

    public Task<bool> TieneAceptadoAsync(long usuarioId, long retoId) => _repo.TieneAceptado(usuarioId, retoId);
}
