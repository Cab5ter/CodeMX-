using CodeMX.Api.Modules.Cursos;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Persistence;

/// <summary>Siembra los retos y los cursos de ejemplo si la base está vacía.</summary>
public static class DataSeeder
{
    public static void Seed(CodeMxDbContext db)
    {
        SeedRetos(db);
        SeedCursos(db);
    }

    private static void SeedRetos(CodeMxDbContext db)
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

    // ============================================================
    //  CURSOS: módulos, lecciones (teoría + ejercicio) y exámenes
    // ============================================================
    private static void SeedCursos(CodeMxDbContext db)
    {
        if (db.Modulos.Any()) return;

        // ---------- Módulo 1: Fundamentos de Python ----------
        var m1 = CrearModulo(db, "Fundamentos de Python", "🐍",
            "Aprende las bases del lenguaje: variables, datos, entrada/salida y operadores.", 1);

        Teoria(db, m1, 1, "Variables y tipos de datos",
            "Una variable es un nombre que guarda un valor. En Python no declaras el tipo: " +
            "se infiere del valor que asignas con el signo =.\n\n" +
            "Los tipos básicos son:\n• int: enteros (5, -3)\n• float: con decimales (3.14)\n" +
            "• str: texto entre comillas (\"hola\")\n• bool: True / False\n\n" +
            "Puedes ver el tipo con la función type().",
            "edad = 20          # int\npromedio = 8.5     # float\nnombre = \"Ana\"     # str\n\n" +
            "print(nombre, \"tiene\", edad, \"años\")");

        Teoria(db, m1, 2, "Entrada y salida: input() y print()",
            "Para mostrar información usas print(). Para leer lo que el usuario escribe usas input().\n\n" +
            "input() SIEMPRE devuelve texto (str). Si necesitas un número, conviértelo con int() o float().\n\n" +
            "Para leer varios valores de una línea separados por espacio se combina input().split() con map(). " +
            "Este patrón es clave para resolver los ejercicios de CodeMX.",
            "# Leer un número\nn = int(input())\n\n# Leer dos números: \"3 5\"\na, b = map(int, input().split())\nprint(a + b)");

        Teoria(db, m1, 3, "Operadores aritméticos",
            "Python incluye:\n• +  suma\n• -  resta\n• *  multiplicación\n• /  división (float)\n" +
            "• // división entera\n• %  módulo (residuo)\n• ** potencia\n\n" +
            "El operador % es muy útil: un número es par si n % 2 == 0.",
            "print(7 / 2)    # 3.5\nprint(7 // 2)   # 3\nprint(7 % 2)    # 1\nprint(2 ** 10)  # 1024");

        Ejercicio(db, m1, 4, "Practica: Suma de dos números", "Suma de dos números");

        Examen(db, m1, 1, "¿Qué tipo de dato devuelve siempre la función input()?", "int", "str", "float", "bool", 1);
        Examen(db, m1, 2, "¿Cuál es el resultado de 7 % 3?", "2", "1", "3", "0", 1);
        Examen(db, m1, 3, "¿Cómo lees dos enteros en la misma línea separados por espacio?",
            "a = int(input()); b = int(input())", "a, b = map(int, input().split())",
            "a, b = input()", "a, b = int(input().split())", 1);

        // ---------- Módulo 2: Control de flujo ----------
        var m2 = CrearModulo(db, "Control de flujo", "🔀",
            "Toma decisiones con condicionales y repite acciones con bucles.", 2);

        Teoria(db, m2, 1, "Condicionales: if, elif, else",
            "Los condicionales ejecutan código sólo si se cumple una condición. Los bloques se definen por " +
            "indentación (4 espacios), no por llaves.\n\n• if: si la condición es verdadera\n" +
            "• elif: prueba otra condición\n• else: si ninguna se cumplió\n\n" +
            "Comparadores: == != < > <= >=.",
            "n = int(input())\n\nif n > 0:\n    print(\"positivo\")\nelif n < 0:\n    print(\"negativo\")\nelse:\n    print(\"cero\")");

        Teoria(db, m2, 2, "Bucles: for y while",
            "• for se usa cuando sabes cuántas veces repetir. range(1, n+1) genera de 1 a n.\n" +
            "• while repite mientras una condición sea verdadera.\n\n" +
            "Recuerda que range(a, b) NO incluye b.",
            "n = int(input())\n\nfor i in range(1, n + 1):\n    print(i)");

        Ejercicio(db, m2, 3, "Practica: FizzBuzz", "FizzBuzz");
        Ejercicio(db, m2, 4, "Practica: Número palíndromo", "Número palíndromo");

        Examen(db, m2, 1, "¿Qué imprime range(1, 5)?", "1 2 3 4 5", "1 2 3 4", "0 1 2 3 4", "1 2 3", 1);
        Examen(db, m2, 2, "¿Cómo se definen los bloques de código en Python?",
            "Con llaves { }", "Con indentación (espacios)", "Con paréntesis ( )", "Con punto y coma ;", 1);
        Examen(db, m2, 3, "¿Qué palabra clave repite mientras una condición sea verdadera?",
            "for", "repeat", "while", "loop", 2);

        // ---------- Módulo 3: Datos y algoritmos ----------
        var m3 = CrearModulo(db, "Datos y algoritmos", "🧮",
            "Organiza información con listas y funciones, y resuelve algoritmos clásicos.", 3);

        Teoria(db, m3, 1, "Listas en Python",
            "Una lista guarda varios valores en orden, entre corchetes y separados por comas. Se accede por " +
            "índice empezando en 0.\n\n• lista.append(x): agrega al final\n• len(lista): cantidad\n" +
            "• lista[i]: elemento en la posición i\n\n" +
            "Para convertir una línea de números en lista: list(map(int, input().split())).",
            "numeros = [10, 20, 30]\nnumeros.append(40)\n\nprint(numeros[0])     # 10\nprint(len(numeros))   # 4");

        Teoria(db, m3, 2, "Funciones",
            "Una función agrupa código reutilizable. Se define con def, recibe parámetros y devuelve un " +
            "resultado con return.",
            "def es_primo(n):\n    if n < 2:\n        return False\n    for d in range(2, int(n ** 0.5) + 1):\n" +
            "        if n % d == 0:\n            return False\n    return True\n\nprint(es_primo(7))   # True");

        Ejercicio(db, m3, 3, "Practica: Fibonacci iterativo", "Fibonacci iterativo");
        Ejercicio(db, m3, 4, "Practica: Números primos hasta N", "Números primos hasta N");

        Examen(db, m3, 1, "¿Cuál es el índice del primer elemento de una lista?", "1", "0", "-1", "Depende", 1);
        Examen(db, m3, 2, "¿Qué palabra clave devuelve un valor desde una función?", "return", "yield", "give", "out", 0);
        Examen(db, m3, 3, "¿Qué hace numeros.append(5)?",
            "Elimina el 5", "Agrega el 5 al final", "Inserta el 5 al inicio", "Cuenta cuántos 5 hay", 1);
    }

    private static Modulo CrearModulo(CodeMxDbContext db, string titulo, string icono, string descripcion, int orden)
    {
        var m = new Modulo { Titulo = titulo, Icono = icono, Descripcion = descripcion, Orden = orden };
        db.Modulos.Add(m);
        db.SaveChanges();
        return m;
    }

    private static void Teoria(CodeMxDbContext db, Modulo modulo, int orden, string titulo, string contenido, string ejemplo)
    {
        db.Lecciones.Add(new Leccion
        {
            ModuloId = modulo.Id, Orden = orden, Titulo = titulo,
            Tipo = TipoLeccion.TEORIA, Contenido = contenido, EjemploCodigo = ejemplo
        });
        db.SaveChanges();
    }

    private static void Ejercicio(CodeMxDbContext db, Modulo modulo, int orden, string titulo, string tituloReto)
    {
        var reto = db.Retos.FirstOrDefault(r => r.Titulo == tituloReto);
        db.Lecciones.Add(new Leccion
        {
            ModuloId = modulo.Id, Orden = orden, Titulo = titulo,
            Tipo = TipoLeccion.EJERCICIO, RetoId = reto?.Id
        });
        db.SaveChanges();
    }

    private static void Examen(CodeMxDbContext db, Modulo modulo, int orden, string enunciado,
        string a, string b, string c, string d, int correcta)
    {
        db.PreguntasExamen.Add(new PreguntaExamen
        {
            ModuloId = modulo.Id, Orden = orden, Enunciado = enunciado,
            OpcionA = a, OpcionB = b, OpcionC = c, OpcionD = d, Correcta = correcta
        });
        db.SaveChanges();
    }
}
