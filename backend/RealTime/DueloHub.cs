using CodeMX.Api.Modules.Duelos;
using CodeMX.Api.Modules.Retos;
using CodeMX.Api.Modules.Usuarios;
using Microsoft.AspNetCore.SignalR;

namespace CodeMX.Api.RealTime;

/// <summary>
/// Hub de SignalR del modo 1 vs 1. Maneja el emparejamiento, el envío de soluciones (que
/// se evalúan en el servidor contra los casos del problema), el cierre del duelo con puntos
/// y el chat en vivo entre los dos jugadores.
///
/// Eventos que emite hacia el cliente:
///   EnEspera()                          → estás en la cola, esperando rival
///   DueloIniciado(dto)                  → hay rival y problema; empieza el duelo
///   ResultadoEnvio({veredicto,error})   → resultado de TU envío
///   RivalFallo({usuarioId,veredicto})   → tu rival envió y falló
///   RivalEscribiendo({usuarioId})       → tu rival está escribiendo en el chat
///   MensajeRecibido({usuarioId,...})    → mensaje de chat
///   DueloTerminado({ganadorId,...})     → alguien ganó (o hubo abandono)
///   DueloNoDisponible()                 → el duelo ya no existe
/// </summary>
public class DueloHub : Hub
{
    private readonly MatchmakingService _matchmaking;
    private readonly IGeneradorProblemas _generador;
    private readonly IDuelosApi _duelos;
    private readonly IUsuariosApi _usuarios;

    public DueloHub(
        MatchmakingService matchmaking,
        IGeneradorProblemas generador,
        IDuelosApi duelos,
        IUsuariosApi usuarios)
    {
        _matchmaking = matchmaking;
        _generador = generador;
        _duelos = duelos;
        _usuarios = usuarios;
    }

    /// <summary>Entra a la cola con una dificultad. Si hay rival esperando (misma dificultad), crea el duelo.</summary>
    public async Task BuscarDuelo(long usuarioId, string nombre, string dificultad)
    {
        if (!Enum.TryParse<Dificultad>(dificultad, ignoreCase: true, out var nivel))
            nivel = Dificultad.INTERMEDIO;

        var nombreReal = await ResolverNombre(usuarioId, nombre);
        var yo = new JugadorEnEspera(Context.ConnectionId, usuarioId, nombreReal, nivel);

        var rival = _matchmaking.EmparejarOEncolar(yo);
        if (rival is null)
        {
            await Clients.Caller.SendAsync("EnEspera");
            return;
        }

        // Soy el segundo en llegar: genero el problema (de la dificultad acordada) y abro el duelo.
        var problema = await _generador.GenerarAsync(nivel, Context.ConnectionAborted);
        var duelo = await _duelos.CrearAsync(rival.UsuarioId, usuarioId, problema.Titulo, nivel);

        var activo = new DueloActivo
        {
            DueloId = duelo.Id,
            Jugador1 = rival,
            Jugador2 = yo,
            Problema = problema
        };
        _matchmaking.RegistrarDuelo(activo);

        await Groups.AddToGroupAsync(rival.ConnectionId, activo.Grupo);
        await Groups.AddToGroupAsync(Context.ConnectionId, activo.Grupo);

        var (puntosGanar, puntosPerder) = DueloService.PuntosPorDificultad(nivel);

        // El payload NUNCA incluye los casos de prueba (sólo enunciado y un ejemplo).
        var dto = new
        {
            dueloId = duelo.Id,
            titulo = problema.Titulo,
            enunciado = problema.Enunciado,
            ejemploEntrada = problema.EjemploEntrada,
            ejemploSalida = problema.EjemploSalida,
            dificultad = nivel.ToString(),
            puntosGanar,
            puntosPerder,
            jugadores = new[]
            {
                new { id = rival.UsuarioId, nombre = rival.Nombre },
                new { id = yo.UsuarioId,    nombre = yo.Nombre }
            }
        };
        await Clients.Group(activo.Grupo).SendAsync("DueloIniciado", dto);
    }

    /// <summary>Evalúa una solución. El primero en lograr ACEPTADO gana el duelo.</summary>
    public async Task EnviarSolucion(long dueloId, long usuarioId, string codigo)
    {
        var activo = _matchmaking.Obtener(dueloId);
        if (activo is null)
        {
            await Clients.Caller.SendAsync("DueloNoDisponible");
            return;
        }

        var resultado = await _duelos.EvaluarAsync(codigo, activo.Problema.Casos);

        if (resultado.Veredicto == "ACEPTADO" && activo.IntentarGanar(usuarioId))
        {
            await _duelos.RegistrarGanadorAsync(dueloId, usuarioId);
            await Clients.Group(activo.Grupo).SendAsync("DueloTerminado", new
            {
                ganadorId = usuarioId,
                ganadorNombre = activo.NombreDe(usuarioId),
                motivo = "resuelto"
            });
            _matchmaking.Quitar(dueloId);
            return;
        }

        // No ganó: o falló, o alguien ya había ganado.
        await Clients.Caller.SendAsync("ResultadoEnvio", new
        {
            veredicto = resultado.Veredicto,
            mensajeError = resultado.MensajeError
        });

        if (resultado.Veredicto != "ACEPTADO")
        {
            await Clients.GroupExcept(activo.Grupo, Context.ConnectionId)
                .SendAsync("RivalFallo", new { usuarioId, veredicto = resultado.Veredicto });
        }
    }

    /// <summary>Chat en vivo entre los dos jugadores del duelo.</summary>
    public async Task EnviarMensaje(long dueloId, long usuarioId, string texto)
    {
        var activo = _matchmaking.Obtener(dueloId);
        if (activo is null || string.IsNullOrWhiteSpace(texto)) return;

        await Clients.Group(activo.Grupo).SendAsync("MensajeRecibido", new
        {
            usuarioId,
            nombre = activo.NombreDe(usuarioId),
            texto = texto.Trim(),
            ts = DateTime.UtcNow
        });
    }

    /// <summary>Indicador "está escribiendo…".</summary>
    public async Task Escribiendo(long dueloId, long usuarioId)
    {
        var activo = _matchmaking.Obtener(dueloId);
        if (activo is null) return;
        await Clients.GroupExcept(activo.Grupo, Context.ConnectionId)
            .SendAsync("RivalEscribiendo", new { usuarioId });
    }

    /// <summary>Si alguien abandona un duelo en curso, el rival gana por abandono.</summary>
    public override async Task OnDisconnectedAsync(Exception? exception)
    {
        var activo = _matchmaking.QuitarConexion(Context.ConnectionId);
        if (activo is not null)
        {
            var rival = activo.Jugador1.ConnectionId == Context.ConnectionId
                ? activo.Jugador2
                : activo.Jugador1;

            if (activo.IntentarGanar(rival.UsuarioId))
            {
                await _duelos.RegistrarGanadorAsync(activo.DueloId, rival.UsuarioId);
                await Clients.Group(activo.Grupo).SendAsync("DueloTerminado", new
                {
                    ganadorId = rival.UsuarioId,
                    ganadorNombre = rival.Nombre,
                    motivo = "abandono"
                });
                _matchmaking.Quitar(activo.DueloId);
            }
        }

        await base.OnDisconnectedAsync(exception);
    }

    private async Task<string> ResolverNombre(long usuarioId, string nombreFallback)
    {
        var usuario = await _usuarios.BuscarPorIdAsync(usuarioId);
        if (!string.IsNullOrWhiteSpace(usuario?.Nombre)) return usuario!.Nombre;
        return string.IsNullOrWhiteSpace(nombreFallback) ? $"Usuario #{usuarioId}" : nombreFallback;
    }
}
