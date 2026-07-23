package mx.codemx.modules.duelos;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.TextBlock;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ClaudeGeneradorProblemas implements GeneradorProblemas {

    private static final String MODELO = "claude-opus-4-8";

    private static final List<String> TEMAS = List.of(
            "cadenas de texto", "matemática básica", "arreglos y listas", "lógica condicional",
            "ciclos y conteo", "ordenamiento simple", "búsqueda", "geometría sencilla",
            "manejo de dígitos", "secuencias numéricas");

    private final ObjectMapper json = new ObjectMapper();

    @Override
    public ProblemaDuelo generar(Dificultad dificultad) {
        String tema = TEMAS.get(ThreadLocalRandom.current().nextInt(TEMAS.size()));
        Estilo estilo = estiloDe(dificultad);

        String instruccion = """
                Eres el juez de una competencia de programación 1 vs 1 en español. Genera UN problema de \
                dificultad %s sobre el tema "%s". %s

                Devuelve EXCLUSIVAMENTE un objeto JSON válido (sin texto adicional, sin ```), con esta forma:
                {
                  "titulo": string corto,
                  "enunciado": string claro en español que describa qué imprimir (y la entrada, si la hay),
                  "ejemploEntrada": string (vacío si el problema no lee nada),
                  "ejemploSalida": string,
                  "casos": [ { "entrada": string, "salidaEsperada": string }, ... ]
                }

                Reglas estrictas:
                %s
                - Cada "salidaEsperada" debe ser EXACTAMENTE lo que un programa correcto imprime para esa \
                entrada, sin espacios ni saltos de línea finales sobrantes.
                - El problema debe poder resolverse en menos de 2 segundos y sin librerías externas.
                - No incluyas la solución ni explicaciones, sólo el JSON.\
                """.formatted(estilo.nivel(), tema, estilo.formato(), estilo.reglaCasos());

        AnthropicClient client = AnthropicOkHttpClient.fromEnv();

        MessageCreateParams params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(2048)
                .addUserMessage(instruccion)
                .build();

        Message respuesta = client.messages().create(params);

        String texto = respuesta.content().stream()
                .map(bloque -> bloque.text().map(TextBlock::text).orElse(""))
                .collect(Collectors.joining());

        return parsear(texto);
    }

    private static Estilo estiloDe(Dificultad dificultad) {
        return switch (dificultad) {
            case BASICO -> new Estilo(
                    "muy fácil, para alguien que apenas empieza en Python",
                    "El alumno SOLO debe usar variables y print(). NO debe leer nada de la entrada estándar "
                            + "(nada de input()): la salida es fija. El problema es de 1 o 2 líneas (p. ej. crear una "
                            + "variable e imprimirla, o imprimir la suma de dos números dados en el enunciado).",
                    "- Incluye EXACTAMENTE 1 caso con \"entrada\" vacía (\"\") y la \"salidaEsperada\" fija.");
            case AVANZADO -> new Estilo(
                    "difícil (requiere pensar el algoritmo: ciclos, condiciones, etc.)",
                    "Se resuelve en Python leyendo de la entrada estándar (stdin) e imprimiendo en stdout.",
                    "- Incluye entre 5 y 7 casos de prueba deterministas que cubran casos límite.");
            case INTERMEDIO -> new Estilo(
                    "intermedio (una entrada simple con input() y quizá una condición)",
                    "Se resuelve en Python leyendo de la entrada estándar (stdin) e imprimiendo en stdout.",
                    "- Incluye entre 4 y 6 casos de prueba deterministas que cubran casos límite.");
        };
    }

    private ProblemaDuelo parsear(String texto) {
        int inicio = texto.indexOf('{');
        int fin = texto.lastIndexOf('}');
        if (inicio < 0 || fin <= inicio) {
            throw new IllegalStateException("Claude no devolvió un JSON de problema válido.");
        }

        ProblemaDto dto;
        try {
            dto = json.readValue(texto.substring(inicio, fin + 1), ProblemaDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo deserializar el problema de Claude.", e);
        }

        List<CasoPrueba> casos = new ArrayList<>();
        if (dto.casos != null) {
            for (CasoDto c : dto.casos) {
                casos.add(new CasoPrueba(
                        c.entrada == null ? "" : c.entrada,
                        c.salidaEsperada == null ? "" : c.salidaEsperada.stripTrailing()));
            }
        }

        if (casos.isEmpty()) {
            throw new IllegalStateException("El problema de Claude no trae casos de prueba.");
        }

        return new ProblemaDuelo(
                dto.titulo == null ? "Reto relámpago" : dto.titulo,
                dto.enunciado == null ? "" : dto.enunciado,
                dto.ejemploEntrada == null ? casos.get(0).getInputData() : dto.ejemploEntrada,
                dto.ejemploSalida == null ? casos.get(0).getOutputEsperado() : dto.ejemploSalida,
                casos);
    }

    private record Estilo(String nivel, String formato, String reglaCasos) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class ProblemaDto {
        public String titulo;
        public String enunciado;
        public String ejemploEntrada;
        public String ejemploSalida;
        public List<CasoDto> casos;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class CasoDto {
        public String entrada;
        public String salidaEsperada;
    }
}
