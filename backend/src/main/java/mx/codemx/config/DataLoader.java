package mx.codemx.config;

import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import mx.codemx.retos.repository.RetoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final RetoRepository retoRepository;

    public DataLoader(RetoRepository retoRepository) {
        this.retoRepository = retoRepository;
    }

    @Override
    public void run(String... args) {
        if (retoRepository.count() > 0) return;

        retoRepository.saveAll(List.of(
            reto("Suma de dos números",
                "Dado dos enteros A y B, imprime su suma.\n\nEntrada: dos enteros en la misma línea.\nSalida: un entero con la suma.",
                Dificultad.BASICO),

            reto("FizzBuzz",
                "Imprime los números del 1 al N. Para múltiplos de 3 imprime 'Fizz', para múltiplos de 5 imprime 'Buzz', y para múltiplos de ambos imprime 'FizzBuzz'.",
                Dificultad.BASICO),

            reto("Número palindromo",
                "Dado un entero N, determina si es palíndromo (se lee igual de izquierda a derecha que de derecha a izquierda). Imprime 'SI' o 'NO'.",
                Dificultad.BASICO),

            reto("Números primos hasta N",
                "Dado un entero N, imprime todos los números primos menores o iguales a N, uno por línea.",
                Dificultad.INTERMEDIO),

            reto("Fibonacci iterativo",
                "Dado un entero N, imprime los primeros N términos de la sucesión de Fibonacci separados por espacios. No uses recursión.",
                Dificultad.INTERMEDIO),

            reto("Búsqueda binaria",
                "Dado un arreglo de N enteros ordenados de forma ascendente y un valor objetivo K, encuentra el índice de K en el arreglo. Si no existe, imprime -1.",
                Dificultad.INTERMEDIO),

            reto("Árbol binario de búsqueda",
                "Implementa un árbol binario de búsqueda (BST) con operaciones de inserción y búsqueda. Dado N operaciones, ejecuta cada una e imprime el resultado de cada búsqueda.",
                Dificultad.AVANZADO),

            reto("Problema de la mochila",
                "Dado un conjunto de N objetos con peso y valor, y una mochila con capacidad W, encuentra el valor máximo que puedes cargar sin superar el peso límite.",
                Dificultad.AVANZADO)
        ));
    }

    private Reto reto(String titulo, String descripcion, Dificultad dificultad) {
        Reto r = new Reto();
        r.setTitulo(titulo);
        r.setDescripcion(descripcion);
        r.setDificultad(dificultad);
        return r;
    }
}
