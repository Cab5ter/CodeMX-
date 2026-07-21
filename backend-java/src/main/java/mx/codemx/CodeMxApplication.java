package mx.codemx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del monolito modular de CodeMX.
 *
 * <p>Un módulo por dominio, con interfaces públicas explícitas entre ellos (gateway +
 * APIs por módulo) y separación lógica en PostgreSQL: un esquema por módulo — ADR-03.
 */
@SpringBootApplication
public class CodeMxApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeMxApplication.class, args);
    }
}
