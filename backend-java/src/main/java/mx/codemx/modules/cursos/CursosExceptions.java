package mx.codemx.modules.cursos;

public final class CursosExceptions {

    private CursosExceptions() {
    }

    public static class ExamenBloqueadoException extends RuntimeException {
        public ExamenBloqueadoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }
}
