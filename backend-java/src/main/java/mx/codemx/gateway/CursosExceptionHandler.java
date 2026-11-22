package mx.codemx.gateway;

import mx.codemx.modules.cursos.CursosExceptions.ExamenBloqueadoException;
import mx.codemx.modules.cursos.CursosExceptions.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce los errores de negocio del módulo Cursos a códigos HTTP. */
@RestControllerAdvice(assignableTypes = CursosController.class)
public class CursosExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> noEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ExamenBloqueadoException.class)
    public ResponseEntity<String> bloqueado(ExamenBloqueadoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    /** Completar una lección de ejercicio "a mano" es una petición inválida. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> peticionInvalida(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
