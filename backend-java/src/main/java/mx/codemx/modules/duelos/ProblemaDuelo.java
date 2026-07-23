package mx.codemx.modules.duelos;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;

public record ProblemaDuelo(
        String titulo,
        String enunciado,
        String ejemploEntrada,
        String ejemploSalida,
        List<CasoPrueba> casos) {
}
