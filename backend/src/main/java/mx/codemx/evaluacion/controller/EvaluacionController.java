package mx.codemx.evaluacion.controller;

import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import mx.codemx.evaluacion.service.EvaluacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluacion")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping("/evaluar")
    public ResponseEntity<ResultadoEvaluacion> evaluar(@RequestBody SolicitudEvaluacion solicitud) {
        return ResponseEntity.ok(evaluacionService.evaluar(solicitud));
    }
}
