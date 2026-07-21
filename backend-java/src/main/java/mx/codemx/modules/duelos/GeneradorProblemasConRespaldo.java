package mx.codemx.modules.duelos;

import mx.codemx.modules.retos.Dificultad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Orquestador registrado como {@link GeneradorProblemas} principal. Decide la fuente del
 * problema: si hay ANTHROPIC_API_KEY usa Claude y, ante cualquier fallo (sin key, error de
 * red, JSON inválido), recurre al respaldo de retos sembrados. Así el duelo nunca se queda
 * sin problema y la IA se activa sola cuando configuras la key.
 */
@Component
@Primary
public class GeneradorProblemasConRespaldo implements GeneradorProblemas {

    private static final Logger log = LoggerFactory.getLogger(GeneradorProblemasConRespaldo.class);

    private final ClaudeGeneradorProblemas claude;
    private final RetoSembradoGenerador respaldo;

    public GeneradorProblemasConRespaldo(ClaudeGeneradorProblemas claude, RetoSembradoGenerador respaldo) {
        this.claude = claude;
        this.respaldo = respaldo;
    }

    private static boolean hayApiKey() {
        String key = System.getenv("ANTHROPIC_API_KEY");
        return key != null && !key.isBlank();
    }

    @Override
    public ProblemaDuelo generar(Dificultad dificultad) {
        if (hayApiKey()) {
            try {
                return claude.generar(dificultad);
            } catch (Exception e) {
                log.warn("Generación con Claude falló; usando retos sembrados de respaldo.", e);
            }
        }
        return respaldo.generar(dificultad);
    }
}
