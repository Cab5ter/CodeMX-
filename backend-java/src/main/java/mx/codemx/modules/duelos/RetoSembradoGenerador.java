package mx.codemx.modules.duelos;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;
import mx.codemx.modules.retos.Reto;
import mx.codemx.modules.retos.RetosApi;
import org.springframework.stereotype.Component;

/**
 * Respaldo sin IA: arma el problema del duelo a partir de un reto ya sembrado en la base
 * y sus casos de prueba. Permite que el modo 1 vs 1 funcione aunque no haya ANTHROPIC_API_KEY.
 */
@Component
public class RetoSembradoGenerador implements GeneradorProblemas {

    private final RetosApi retos;

    public RetoSembradoGenerador(RetosApi retos) {
        this.retos = retos;
    }

    @Override
    public ProblemaDuelo generar(Dificultad dificultad) {
        List<Reto> todos = retos.listarTodos();
        if (todos.isEmpty()) {
            throw new IllegalStateException("No hay retos sembrados para generar un duelo.");
        }

        // Prefiere retos de la dificultad pedida; si no hay (p. ej. AVANZADO sin sembrar), usa cualquiera.
        List<Reto> candidatos = todos.stream()
                .filter(r -> r.getDificultad() == dificultad)
                .toList();
        if (candidatos.isEmpty()) {
            candidatos = todos;
        }

        Reto reto = candidatos.get(ThreadLocalRandom.current().nextInt(candidatos.size()));

        List<CasoPrueba> casos = retos.obtenerCasos(reto.getId());
        if (casos.isEmpty()) {
            throw new IllegalStateException("El reto '" + reto.getTitulo() + "' no tiene casos de prueba.");
        }

        CasoPrueba ejemplo = retos.obtenerEjemplo(reto.getId()).orElse(casos.get(0));

        return new ProblemaDuelo(
                reto.getTitulo(),
                reto.getDescripcion(),
                ejemplo.getInputData() == null ? "" : ejemplo.getInputData(),
                ejemplo.getOutputEsperado(),
                casos);
    }
}
