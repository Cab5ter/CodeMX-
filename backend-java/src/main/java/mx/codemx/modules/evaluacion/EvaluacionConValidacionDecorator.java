package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;

public class EvaluacionConValidacionDecorator extends EvaluacionDecorator {

    private final int limiteCaracteres;

    public EvaluacionConValidacionDecorator(EvaluacionStrategy envuelto, int limiteCaracteres) {
        super(envuelto);
        this.limiteCaracteres = limiteCaracteres;
    }

    @Override
    public ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos) {
        String codigo = solicitud.codigoFuente();

        if (codigo == null || codigo.isBlank()) {
            return rechazar("El código fuente está vacío");
        }

        if (codigo.length() > limiteCaracteres) {
            return rechazar("El código fuente excede el límite de " + limiteCaracteres + " caracteres");
        }

        return super.ejecutar(solicitud, casos);
    }

    private static ResultadoEvaluacion rechazar(String motivo) {
        return new ResultadoEvaluacion("ERROR_EN_EJECUCION", motivo, 0);
    }
}
