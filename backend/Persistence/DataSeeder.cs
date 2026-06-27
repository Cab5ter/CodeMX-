using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Persistence;

/// <summary>Siembra los retos de ejemplo con sus casos de prueba si la base está vacía.</summary>
public static class DataSeeder
{
    public static void Seed(CodeMxDbContext db)
    {
        if (db.Retos.Any()) return;

        CrearReto(db, "Suma de dos números",
            "Dado dos enteros A y B, imprime su suma.\n\n" +
            "Entrada: dos enteros separados por un espacio en la misma línea.\nSalida: un entero con la suma.",
            Dificultad.BASICO,
            ("2 3", "5"), ("10 -5", "5"), ("0 0", "0"), ("-7 -3", "-10"));

        CrearReto(db, "FizzBuzz",
            "Dado un entero N, imprime los números del 1 al N. Múltiplos de 3 => 'Fizz', de 5 => 'Buzz', de ambos => 'FizzBuzz'.",
            Dificultad.BASICO,
            ("5", "1\n2\nFizz\n4\nBuzz"));

        CrearReto(db, "Número palíndromo",
            "Dado un entero N, imprime 'SI' si es palíndromo o 'NO' en caso contrario.",
            Dificultad.BASICO,
            ("121", "SI"), ("123", "NO"), ("1", "SI"));

        CrearReto(db, "Números primos hasta N",
            "Dado un entero N, imprime todos los números primos menores o iguales a N, uno por línea.",
            Dificultad.INTERMEDIO,
            ("10", "2\n3\n5\n7"), ("2", "2"));

        CrearReto(db, "Fibonacci iterativo",
            "Dado un entero N, imprime los primeros N términos de Fibonacci separados por espacios (empieza en 0 1).",
            Dificultad.INTERMEDIO,
            ("8", "0 1 1 2 3 5 8 13"), ("1", "0"));

        CrearReto(db, "Búsqueda binaria",
            "Dado N, un arreglo ordenado y un valor K, imprime el índice (base 0) de K o -1 si no existe.",
            Dificultad.INTERMEDIO,
            ("5\n1 3 5 7 9\n5", "2"), ("5\n1 3 5 7 9\n4", "-1"));

        db.SaveChanges();
    }

    private static void CrearReto(CodeMxDbContext db, string titulo, string descripcion,
        Dificultad dificultad, params (string input, string output)[] casos)
    {
        var reto = new Reto { Titulo = titulo, Descripcion = descripcion, Dificultad = dificultad };
        db.Retos.Add(reto);
        db.SaveChanges();

        foreach (var (input, output) in casos)
            db.CasosPrueba.Add(new CasoPrueba { RetoId = reto.Id, InputData = input, OutputEsperado = output });

        db.SaveChanges();
    }
}
