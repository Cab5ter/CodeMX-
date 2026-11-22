using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Problema generado para un duelo. Lo produce un <see cref="IGeneradorProblemas"/>
/// (Claude cuando hay API key, o un reto sembrado como respaldo). Los <see cref="Casos"/>
/// son la fuente de verdad para decidir si una solución es correcta y NUNCA se envían al
/// cliente: el frontend sólo ve el enunciado y un ejemplo.
/// </summary>
public record ProblemaDuelo(
    string Titulo,
    string Enunciado,
    string EjemploEntrada,
    string EjemploSalida,
    List<CasoPrueba> Casos
);
