using System.Text.Json;
using Anthropic;
using Anthropic.Models.Messages;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Genera el problema del duelo con Claude (SDK oficial de C#, modelo claude-opus-4-8).
/// Claude inventa el reto, su solución de referencia y los casos de prueba; la corrección
/// se decide después ejecutando el código de cada jugador contra esos casos (motor Python),
/// así que la revisión es determinista y no depende de una nota subjetiva del modelo.
///
/// Requiere la variable de entorno ANTHROPIC_API_KEY. Si no está o la llamada falla,
/// <see cref="GeneradorProblemas"/> recurre automáticamente al respaldo.
/// </summary>
public class ClaudeGeneradorProblemas : IGeneradorProblemas
{
    private const string Modelo = "claude-opus-4-8";

    // Temas para variar los duelos en cada partida.
    private static readonly string[] Temas =
    {
        "cadenas de texto", "matemática básica", "arreglos y listas", "lógica condicional",
        "ciclos y conteo", "ordenamiento simple", "búsqueda", "geometría sencilla",
        "manejo de dígitos", "secuencias numéricas"
    };

    public async Task<ProblemaDuelo> GenerarAsync(Dificultad dificultad, CancellationToken ct = default)
    {
        var tema = Temas[Random.Shared.Next(Temas.Length)];

        // El nivel cambia el ESTILO del problema, no sólo el adjetivo: el "fácil" para un
        // principiante no usa entrada estándar (solo print y variables); eso se aprende después.
        var (nivel, formato, reglaCasos) = dificultad switch
        {
            Dificultad.BASICO => (
                "muy fácil, para alguien que apenas empieza en Python",
                "El alumno SOLO debe usar variables y print(). NO debe leer nada de la entrada estándar " +
                "(nada de input()): la salida es fija. El problema es de 1 o 2 líneas (p. ej. crear una " +
                "variable e imprimirla, o imprimir la suma de dos números dados en el enunciado).",
                "- Incluye EXACTAMENTE 1 caso con \"entrada\" vacía (\"\") y la \"salidaEsperada\" fija."),
            Dificultad.AVANZADO => (
                "difícil (requiere pensar el algoritmo: ciclos, condiciones, etc.)",
                "Se resuelve en Python leyendo de la entrada estándar (stdin) e imprimiendo en stdout.",
                "- Incluye entre 5 y 7 casos de prueba deterministas que cubran casos límite."),
            _ => (
                "intermedio (una entrada simple con input() y quizá una condición)",
                "Se resuelve en Python leyendo de la entrada estándar (stdin) e imprimiendo en stdout.",
                "- Incluye entre 4 y 6 casos de prueba deterministas que cubran casos límite.")
        };

        // El cliente lee ANTHROPIC_API_KEY del entorno automáticamente.
        var client = new AnthropicClient();

        var instruccion =
            $"Eres el juez de una competencia de programación 1 vs 1 en español. Genera UN problema de " +
            $"dificultad {nivel} sobre el tema \"{tema}\". {formato}\n\n" +
            "Devuelve EXCLUSIVAMENTE un objeto JSON válido (sin texto adicional, sin ```), con esta forma:\n" +
            "{\n" +
            "  \"titulo\": string corto,\n" +
            "  \"enunciado\": string claro en español que describa qué imprimir (y la entrada, si la hay),\n" +
            "  \"ejemploEntrada\": string (vacío si el problema no lee nada),\n" +
            "  \"ejemploSalida\": string,\n" +
            "  \"casos\": [ { \"entrada\": string, \"salidaEsperada\": string }, ... ]\n" +
            "}\n\n" +
            "Reglas estrictas:\n" +
            reglaCasos + "\n" +
            "- Cada \"salidaEsperada\" debe ser EXACTAMENTE lo que un programa correcto imprime para esa entrada, " +
            "sin espacios ni saltos de línea finales sobrantes.\n" +
            "- El problema debe poder resolverse en menos de 2 segundos y sin librerías externas.\n" +
            "- No incluyas la solución ni explicaciones, sólo el JSON.";

        MessageCreateParams parameters = new()
        {
            MaxTokens = 2048,
            Model = Modelo,
            Messages = [new() { Role = Role.User, Content = instruccion }],
        };

        var respuesta = await client.Messages.Create(parameters, cancellationToken: ct);

        var texto = string.Join(
            "",
            respuesta.Content.Select(b => b.Value).OfType<TextBlock>().Select(t => t.Text));

        return Parsear(texto);
    }

    /// <summary>Extrae el objeto JSON de la respuesta y lo convierte en un ProblemaDuelo.</summary>
    private static ProblemaDuelo Parsear(string texto)
    {
        int inicio = texto.IndexOf('{');
        int fin = texto.LastIndexOf('}');
        if (inicio < 0 || fin <= inicio)
            throw new InvalidOperationException("Claude no devolvió un JSON de problema válido.");

        var json = texto.Substring(inicio, fin - inicio + 1);
        var dto = JsonSerializer.Deserialize<ProblemaDto>(json,
            new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
            ?? throw new InvalidOperationException("No se pudo deserializar el problema de Claude.");

        var casos = (dto.Casos ?? new())
            .Select(c => new CasoPrueba { InputData = c.Entrada ?? "", OutputEsperado = (c.SalidaEsperada ?? "").TrimEnd() })
            .ToList();

        if (casos.Count == 0)
            throw new InvalidOperationException("El problema de Claude no trae casos de prueba.");

        return new ProblemaDuelo(
            dto.Titulo ?? "Reto relámpago",
            dto.Enunciado ?? "",
            dto.EjemploEntrada ?? casos[0].InputData ?? "",
            dto.EjemploSalida ?? casos[0].OutputEsperado,
            casos);
    }

    // DTOs sólo para deserializar la respuesta de Claude.
    private sealed class ProblemaDto
    {
        public string? Titulo { get; set; }
        public string? Enunciado { get; set; }
        public string? EjemploEntrada { get; set; }
        public string? EjemploSalida { get; set; }
        public List<CasoDto>? Casos { get; set; }
    }

    private sealed class CasoDto
    {
        public string? Entrada { get; set; }
        public string? SalidaEsperada { get; set; }
    }
}
