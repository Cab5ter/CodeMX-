package mx.codemx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del monolito modular de CodeMX.
 *
 * <p>Equivalente funcional del backend ASP.NET Core: los mismos módulos, las mismas
 * interfaces públicas entre ellos (gateway + APIs por módulo) y la misma separación
 * lógica en PostgreSQL (un esquema por módulo) — ADR-03.
 */
@SpringBootApplication
public class CodeMxApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeMxApplication.class, args);
    }
}
