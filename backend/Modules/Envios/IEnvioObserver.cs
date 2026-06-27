namespace CodeMX.Api.Modules.Envios;

/// <summary>Datos del evento que el módulo Envíos publica cuando un envío es ACEPTADO.</summary>
public record EnvioAceptadoEvent(long UsuarioId, long RetoId);

/// <summary>
/// Patrón <b>Observer</b> (GoF). El módulo Envíos (sujeto) notifica a los observadores
/// registrados cuando un envío es ACEPTADO por primera vez, sin conocer quién reacciona.
/// El módulo Ranking implementa un observador; en el futuro podrían sumarse otros
/// (logros, notificaciones, etc.) sin tocar Envíos.
/// </summary>
public interface IEnvioObserver
{
    Task OnEnvioAceptadoAsync(EnvioAceptadoEvent evento);
}
