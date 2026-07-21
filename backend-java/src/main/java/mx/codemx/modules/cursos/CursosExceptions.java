package mx.codemx.modules.cursos;

/** Excepciones de negocio del módulo Cursos, que el gateway traduce a códigos HTTP. */
public final class CursosExceptions {

    private CursosExceptions() {
    }

    /** Se lanza cuando el alumno intenta el examen sin el avance mínimo. Mapea a HTTP 403. */
    public static class ExamenBloqueadoException extends RuntimeException {
        public ExamenBloqueadoException(String mensaje) {
            super(mensaje);
        }
    }

    /** Recurso de cursos no encontrado. Mapea a HTTP 404. */
    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }
}
