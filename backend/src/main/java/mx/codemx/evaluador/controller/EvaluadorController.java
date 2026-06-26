package mx.codemx.evaluador.controller;

import mx.codemx.evaluador.model.ResultadoEvaluacion;
import mx.codemx.evaluador.model.SolicitudEvaluacion;
import mx.codemx.evaluador.service.EvaluadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint interno del módulo Evaluador. El flujo principal de envío entra por el módulo
 * Retos (POST /api/retos/{id}/submit), que invoca a EvaluadorService directamente. Este
 * controller queda expuesto para pruebas y para evaluar código de forma aislada.
 */
@RestController
@RequestMapping("/api/evaluador")
public class EvaluadorController {

    private final EvaluadorService evaluadorService;

    public EvaluadorController(EvaluadorService evaluadorService) {
        this.evaluadorService = evaluadorService;
    }

    @PostMapping("/evaluar")
    public ResponseEntity<ResultadoEvaluacion> evaluar(@RequestBody SolicitudEvaluacion solicitud) {
        return ResponseEntity.ok(evaluadorService.evaluar(solicitud));
    }
}
