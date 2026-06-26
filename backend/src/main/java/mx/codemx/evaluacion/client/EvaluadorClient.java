package mx.codemx.evaluacion.client;

import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.retos.model.CasoPrueba;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import mx.codemx.evaluacion.model.SolicitudEvaluacion;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class EvaluadorClient {

    private final RestTemplate restTemplate;

    @Value("${evaluador.url}")
    private String evaluadorUrl;

    public EvaluadorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos) {
        try {
            return restTemplate.postForObject(evaluadorUrl + "/evaluar", solicitud, ResultadoEvaluacion.class);
        } catch (RestClientException e) {
            return evaluarConPython(solicitud.codigoFuente(), casos);
        }
    }

    private ResultadoEvaluacion evaluarConPython(String codigo, List<CasoPrueba> casos) {
        if (casos.isEmpty()) {
            return new ResultadoEvaluacion("ERROR_EN_EJECUCION", "El reto no tiene casos de prueba configurados", 0L);
        }

        long inicio = System.currentTimeMillis();

        for (CasoPrueba caso : casos) {
            try {
                String salida = ejecutarPython(codigo, caso.getInputData());
                String esperado = caso.getOutputEsperado().trim();

                if (!salida.equals(esperado)) {
                    return new ResultadoEvaluacion("INCORRECTO",
                            "Salida incorrecta para la entrada: " + caso.getInputData().replace("\n", " | "),
                            System.currentTimeMillis() - inicio);
                }

            } catch (RuntimeException e) {
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("TLE")) {
                    return new ResultadoEvaluacion("TIEMPO_LIMITE_EXCEDIDO", null, 5000L);
                }
                String detalle = msg != null && msg.startsWith("RTE:") ? msg.substring(4) : msg;
                return new ResultadoEvaluacion("ERROR_EN_EJECUCION", detalle, System.currentTimeMillis() - inicio);
            } catch (Exception e) {
                return new ResultadoEvaluacion("ERROR_EN_EJECUCION", e.getMessage(), System.currentTimeMillis() - inicio);
            }
        }

        return new ResultadoEvaluacion("ACEPTADO", null, System.currentTimeMillis() - inicio);
    }

    private String ejecutarPython(String codigo, String stdin) throws Exception {
        Path tempFile = Files.createTempFile("codemx_", ".py");
        try {
            Files.writeString(tempFile, codigo);

            ProcessBuilder pb = new ProcessBuilder("python3", tempFile.toString());
            Process proc = pb.start();

            if (stdin != null && !stdin.isBlank()) {
                try (OutputStream os = proc.getOutputStream()) {
                    os.write(stdin.getBytes(StandardCharsets.UTF_8));
                }
            }

            final byte[][] stdoutBytes = {new byte[0]};
            final byte[][] stderrBytes = {new byte[0]};

            Thread stdoutReader = new Thread(() -> {
                try { stdoutBytes[0] = proc.getInputStream().readAllBytes(); } catch (IOException ignored) {}
            });
            Thread stderrReader = new Thread(() -> {
                try { stderrBytes[0] = proc.getErrorStream().readAllBytes(); } catch (IOException ignored) {}
            });
            stdoutReader.start();
            stderrReader.start();

            boolean terminado = proc.waitFor(5, TimeUnit.SECONDS);
            stdoutReader.join(500);
            stderrReader.join(500);

            if (!terminado) {
                proc.destroyForcibly();
                throw new RuntimeException("TLE");
            }

            if (proc.exitValue() != 0) {
                String stderr = new String(stderrBytes[0], StandardCharsets.UTF_8);
                String primeraLinea = stderr.lines()
                        .filter(l -> !l.isBlank() && !l.startsWith("  "))
                        .reduce((a, b) -> b)
                        .orElse("Error en tiempo de ejecución");
                throw new RuntimeException("RTE:" + primeraLinea);
            }

            return new String(stdoutBytes[0], StandardCharsets.UTF_8).trim();

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
