package mx.codemx.config;

import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import mx.codemx.retos.repository.CasoPruebaRepository;
import mx.codemx.retos.repository.RetoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RetoRepository retoRepository;
    private final CasoPruebaRepository casoPruebaRepository;

    public DataLoader(RetoRepository retoRepository, CasoPruebaRepository casoPruebaRepository) {
        this.retoRepository = retoRepository;
        this.casoPruebaRepository = casoPruebaRepository;
    }

    @Override
    public void run(String... args) {
        if (retoRepository.count() > 0) return;

        crearReto("Suma de dos números",
            "Dado dos enteros A y B, imprime su suma.\n\n" +
            "Entrada: dos enteros separados por un espacio en la misma línea.\n" +
            "Salida: un entero con la suma.",
            Dificultad.BASICO,
            caso("2 3", "5"),
            caso("10 -5", "5"),
            caso("0 0", "0"),
            caso("-7 -3", "-10")
        );

        crearReto("FizzBuzz",
            "Dado un entero N, imprime los números del 1 al N.\n" +
            "Para múltiplos de 3 imprime 'Fizz', para múltiplos de 5 imprime 'Buzz',\n" +
            "y para múltiplos de ambos imprime 'FizzBuzz'.\n\n" +
            "Entrada: un entero N.\n" +
            "Salida: N líneas con el número o la palabra correspondiente.",
            Dificultad.BASICO,
            caso("5", "1\n2\nFizz\n4\nBuzz"),
            caso("15", "1\n2\nFizz\n4\nBuzz\nFizz\n7\n8\nFizz\nBuzz\n11\nFizz\n13\n14\nFizzBuzz")
        );

        crearReto("Número palíndromo",
            "Dado un entero N, determina si es palíndromo " +
            "(se lee igual de izquierda a derecha que de derecha a izquierda).\n\n" +
            "Entrada: un entero N.\n" +
            "Salida: 'SI' si es palíndromo, 'NO' en caso contrario.",
            Dificultad.BASICO,
            caso("121", "SI"),
            caso("123", "NO"),
            caso("1", "SI"),
            caso("10", "NO"),
            caso("99", "SI")
        );

        crearReto("Números primos hasta N",
            "Dado un entero N, imprime todos los números primos menores o iguales a N, uno por línea.\n\n" +
            "Entrada: un entero N.\n" +
            "Salida: los números primos hasta N, uno por línea.",
            Dificultad.INTERMEDIO,
            caso("10", "2\n3\n5\n7"),
            caso("20", "2\n3\n5\n7\n11\n13\n17\n19"),
            caso("2", "2")
        );

        crearReto("Fibonacci iterativo",
            "Dado un entero N, imprime los primeros N términos de la sucesión de Fibonacci " +
            "separados por espacios. La sucesión comienza con 0 y 1.\n" +
            "No uses recursión.\n\n" +
            "Entrada: un entero N.\n" +
            "Salida: los primeros N términos de Fibonacci separados por espacios.",
            Dificultad.INTERMEDIO,
            caso("8", "0 1 1 2 3 5 8 13"),
            caso("1", "0"),
            caso("2", "0 1"),
            caso("6", "0 1 1 2 3 5")
        );

        crearReto("Búsqueda binaria",
            "Dado un arreglo de N enteros ordenados de forma ascendente y un valor K,\n" +
            "encuentra el índice (base 0) de K en el arreglo. Si no existe, imprime -1.\n\n" +
            "Entrada:\n" +
            "  Línea 1: entero N\n" +
            "  Línea 2: N enteros separados por espacio\n" +
            "  Línea 3: entero K\n" +
            "Salida: el índice de K o -1.",
            Dificultad.INTERMEDIO,
            caso("5\n1 3 5 7 9\n5", "2"),
            caso("5\n1 3 5 7 9\n4", "-1"),
            caso("5\n1 3 5 7 9\n1", "0"),
            caso("5\n1 3 5 7 9\n9", "4")
        );

        crearReto("Árbol binario de búsqueda",
            "Implementa un árbol binario de búsqueda (BST). " +
            "Procesa N operaciones de inserción o búsqueda.\n\n" +
            "Entrada:\n" +
            "  Línea 1: entero N\n" +
            "  Líneas siguientes: 'I x' para insertar x, 'B x' para buscar x\n" +
            "Salida: para cada operación B, imprime 'SI' si x existe en el árbol, 'NO' si no.",
            Dificultad.AVANZADO,
            caso("5\nI 10\nI 5\nI 15\nB 5\nB 8", "SI\nNO"),
            caso("6\nI 4\nI 2\nI 6\nI 1\nB 1\nB 3", "SI\nNO")
        );

        crearReto("Problema de la mochila",
            "Dado un conjunto de N objetos con valor y peso, y una mochila con capacidad W,\n" +
            "encuentra el valor máximo que puedes cargar sin superar el peso límite.\n\n" +
            "Entrada:\n" +
            "  Línea 1: enteros N y W separados por espacio\n" +
            "  Línea 2: N valores separados por espacio\n" +
            "  Línea 3: N pesos separados por espacio\n" +
            "Salida: el valor máximo alcanzable.",
            Dificultad.AVANZADO,
            caso("4 5\n2 3 4 5\n1 2 3 2", "10"),
            caso("3 4\n1 4 5\n1 3 4", "5"),
            caso("1 10\n100\n5", "100")
        );
    }

    private void crearReto(String titulo, String descripcion, Dificultad dificultad, CasoPrueba... casos) {
        Reto reto = new Reto();
        reto.setTitulo(titulo);
        reto.setDescripcion(descripcion);
        reto.setDificultad(dificultad);
        Reto guardado = retoRepository.save(reto);

        for (CasoPrueba caso : casos) {
            caso.setRetoId(guardado.getId());
            casoPruebaRepository.save(caso);
        }
    }

    private CasoPrueba caso(String input, String output) {
        CasoPrueba c = new CasoPrueba();
        c.setInputData(input);
        c.setOutputEsperado(output);
        return c;
    }
}
