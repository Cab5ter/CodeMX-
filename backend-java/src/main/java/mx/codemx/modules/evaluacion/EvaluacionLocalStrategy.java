package mx.codemx.modules.evaluacion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import mx.codemx.modules.retos.CasoPrueba;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EvaluacionLocalStrategy implements EvaluacionStrategy {

    private static class TiempoAgotadoException extends RuntimeException {
        TiempoAgotadoException(String mensaje) {
            super(mensaje);
        }
    }

    private final int timeoutMs;

    public EvaluacionLocalStrategy(@Value("${evaluador.timeout-ms:5000}") int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos) {
        if (casos.isEmpty()) {
            return new ResultadoEvaluacion("ERROR_EN_EJECUCION",
                    "El reto no tiene casos de prueba configurados", 0);
        }

        long inicio = System.currentTimeMillis();

        for (CasoPrueba caso : casos) {
            String entrada = caso.getInputData() == null ? "" : caso.getInputData();
            try {
                String salida = ejecutarPython(solicitud.codigoFuente(), entrada);
                String esperado = caso.getOutputEsperado() == null ? "" : caso.getOutputEsperado().trim();

                if (!salida.equals(esperado)) {
                    return new ResultadoEvaluacion("INCORRECTO",
                            "Salida incorrecta para la entrada: " + entrada.replace("\n", " | "),
                            System.currentTimeMillis() - inicio);
                }
            } catch (TiempoAgotadoException e) {
                return new ResultadoEvaluacion("TIEMPO_LIMITE_EXCEDIDO", null, timeoutMs);
            } catch (Exception e) {
                return new ResultadoEvaluacion("ERROR_EN_EJECUCION", e.getMessage(),
                        System.currentTimeMillis() - inicio);
            }
        }

        return new ResultadoEvaluacion("ACEPTADO", null, System.currentTimeMillis() - inicio);
    }

    private String ejecutarPython(String codigo, String stdin) throws IOException, InterruptedException {
        Path archivo = Files.createTempFile("codemx-", ".py");
        try {
            Files.writeString(archivo, codigo, StandardCharsets.UTF_8);

            Process proc = new ProcessBuilder("python3", archivo.toString()).start();

            if (!stdin.isBlank()) {
                try (OutputStream in = proc.getOutputStream()) {
                    in.write(stdin.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                proc.getOutputStream().close();
            }

            var stdoutFuture = leerAsync(proc.getInputStream());
            var stderrFuture = leerAsync(proc.getErrorStream());

            if (!proc.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
                proc.descendants().forEach(ProcessHandle::destroyForcibly);
                proc.destroyForcibly();
                throw new TiempoAgotadoException("TLE");
            }

            String stdout = stdoutFuture.join();
            String stderr = stderrFuture.join();

            if (proc.exitValue() != 0) {
                String primeraLinea = Arrays.stream(stderr.split("\n"))
                        .filter(l -> !l.isBlank())
                        .filter(l -> !l.startsWith("  "))
                        .reduce((primera, ultima) -> ultima)
                        .map(String::trim)
                        .orElse("Error en tiempo de ejecución");
                throw new IOException(primeraLinea);
            }

            return stdout.trim();
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    private static java.util.concurrent.CompletableFuture<String> leerAsync(InputStream stream) {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            try (stream) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                return "";
            }
        });
    }
}
