package mx.codemx.config;

import mx.codemx.cursos.model.Leccion;
import mx.codemx.cursos.model.Modulo;
import mx.codemx.cursos.model.PreguntaExamen;
import mx.codemx.cursos.model.TipoLeccion;
import mx.codemx.cursos.repository.LeccionRepository;
import mx.codemx.cursos.repository.ModuloRepository;
import mx.codemx.cursos.repository.PreguntaExamenRepository;
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
    private final ModuloRepository moduloRepository;
    private final LeccionRepository leccionRepository;
    private final PreguntaExamenRepository preguntaRepository;

    public DataLoader(RetoRepository retoRepository,
                      CasoPruebaRepository casoPruebaRepository,
                      ModuloRepository moduloRepository,
                      LeccionRepository leccionRepository,
                      PreguntaExamenRepository preguntaRepository) {
        this.retoRepository = retoRepository;
        this.casoPruebaRepository = casoPruebaRepository;
        this.moduloRepository = moduloRepository;
        this.leccionRepository = leccionRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public void run(String... args) {
        seedRetos();
        seedCursos();
    }

    // ============================================================
    //  RETOS (ejercicios) y sus casos de prueba
    // ============================================================
    private void seedRetos() {
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

    // ============================================================
    //  CURSOS: módulos, lecciones (teoría + ejercicio) y exámenes
    // ============================================================
    private void seedCursos() {
        if (moduloRepository.count() > 0) return;

        // ---------- Módulo 1: Fundamentos de Python ----------
        Modulo m1 = crearModulo("Fundamentos de Python", "🐍",
            "Aprende las bases del lenguaje: variables, datos, entrada/salida y operadores.", 1);

        teoria(m1, 1, "Variables y tipos de datos",
            "Una variable es un nombre que guarda un valor. En Python no declaras el tipo: " +
            "se infiere del valor que asignas con el signo =.\n\n" +
            "Los tipos básicos son:\n" +
            "• int: números enteros (5, -3)\n" +
            "• float: números con decimales (3.14)\n" +
            "• str: texto entre comillas (\"hola\")\n" +
            "• bool: verdadero o falso (True / False)\n\n" +
            "Puedes ver el tipo de una variable con la función type().",
            "edad = 20          # int\n" +
            "promedio = 8.5     # float\n" +
            "nombre = \"Ana\"     # str\n" +
            "aprobado = True    # bool\n\n" +
            "print(nombre, \"tiene\", edad, \"años\")\n" +
            "print(type(promedio))");

        teoria(m1, 2, "Entrada y salida: input() y print()",
            "Para mostrar información usas print(). Para leer lo que el usuario escribe usas input().\n\n" +
            "input() SIEMPRE devuelve texto (str). Si necesitas un número, debes convertirlo con " +
            "int() o float().\n\n" +
            "Cuando una línea trae varios valores separados por espacio, puedes leerlos todos a la vez " +
            "combinando input().split() con map(). Este patrón es clave para resolver los ejercicios " +
            "de CodeMX.",
            "# Leer un solo número\n" +
            "n = int(input())\n\n" +
            "# Leer dos números en la misma línea: \"3 5\"\n" +
            "a, b = map(int, input().split())\n" +
            "print(a + b)");

        teoria(m1, 3, "Operadores aritméticos",
            "Python incluye los operadores matemáticos habituales:\n\n" +
            "• +  suma\n" +
            "• -  resta\n" +
            "• *  multiplicación\n" +
            "• /  división (resultado float)\n" +
            "• // división entera (descarta decimales)\n" +
            "• %  módulo (residuo de la división)\n" +
            "• ** potencia\n\n" +
            "El operador % es muy útil: un número es par si n % 2 == 0, y es múltiplo de 3 si n % 3 == 0.",
            "print(7 / 2)    # 3.5\n" +
            "print(7 // 2)   # 3\n" +
            "print(7 % 2)    # 1\n" +
            "print(2 ** 10)  # 1024");

        ejercicio(m1, 4, "Practica: Suma de dos números", "Suma de dos números");

        examen(m1, 1,
            "¿Qué tipo de dato devuelve siempre la función input()?",
            "int", "str", "float", "bool", 1);
        examen(m1, 2,
            "¿Cuál es el resultado de 7 % 3?",
            "2", "1", "3", "0", 1);
        examen(m1, 3,
            "¿Cómo lees dos enteros escritos en la misma línea separados por espacio?",
            "a = int(input()); b = int(input())",
            "a, b = map(int, input().split())",
            "a, b = input()",
            "a, b = int(input().split())",
            1);

        // ---------- Módulo 2: Control de flujo ----------
        Modulo m2 = crearModulo("Control de flujo", "🔀",
            "Toma decisiones con condicionales y repite acciones con bucles.", 2);

        teoria(m2, 1, "Condicionales: if, elif, else",
            "Los condicionales ejecutan código sólo si se cumple una condición. " +
            "En Python los bloques se definen por indentación (4 espacios), no por llaves.\n\n" +
            "• if: si la condición es verdadera\n" +
            "• elif: si la anterior fue falsa, prueba otra (puedes encadenar varias)\n" +
            "• else: si ninguna se cumplió\n\n" +
            "Los operadores de comparación son: == (igual), != (distinto), <, >, <=, >=.",
            "n = int(input())\n\n" +
            "if n > 0:\n" +
            "    print(\"positivo\")\n" +
            "elif n < 0:\n" +
            "    print(\"negativo\")\n" +
            "else:\n" +
            "    print(\"cero\")");

        teoria(m2, 2, "Bucles: for y while",
            "Un bucle repite instrucciones.\n\n" +
            "• for se usa cuando sabes cuántas veces repetir. range(1, n+1) genera los números " +
            "de 1 a n.\n" +
            "• while repite mientras una condición siga siendo verdadera.\n\n" +
            "Recuerda que range(a, b) NO incluye b, por eso para llegar hasta n se usa range(1, n+1).",
            "n = int(input())\n\n" +
            "# for: imprime 1..n\n" +
            "for i in range(1, n + 1):\n" +
            "    print(i)\n\n" +
            "# while equivalente\n" +
            "i = 1\n" +
            "while i <= n:\n" +
            "    print(i)\n" +
            "    i += 1");

        ejercicio(m2, 3, "Practica: FizzBuzz", "FizzBuzz");
        ejercicio(m2, 4, "Practica: Número palíndromo", "Número palíndromo");

        examen(m2, 1,
            "¿Qué imprime range(1, 5)?",
            "1 2 3 4 5", "1 2 3 4", "0 1 2 3 4", "1 2 3", 1);
        examen(m2, 2,
            "¿Cómo se definen los bloques de código en Python?",
            "Con llaves { }", "Con indentación (espacios)", "Con paréntesis ( )", "Con punto y coma ;", 1);
        examen(m2, 3,
            "¿Qué palabra clave repite mientras una condición sea verdadera?",
            "for", "repeat", "while", "loop", 2);

        // ---------- Módulo 3: Datos y algoritmos ----------
        Modulo m3 = crearModulo("Datos y algoritmos", "🧮",
            "Organiza información con listas y funciones, y resuelve algoritmos clásicos.", 3);

        teoria(m3, 1, "Listas en Python",
            "Una lista guarda varios valores en orden. Se escribe entre corchetes y los elementos " +
            "se separan por comas. Se accede a cada elemento por su índice, empezando en 0.\n\n" +
            "Operaciones comunes:\n" +
            "• lista.append(x): agrega x al final\n" +
            "• len(lista): cantidad de elementos\n" +
            "• lista[i]: elemento en la posición i\n\n" +
            "Para convertir una línea de números en una lista: list(map(int, input().split())).",
            "numeros = [10, 20, 30]\n" +
            "numeros.append(40)\n\n" +
            "print(numeros[0])     # 10\n" +
            "print(len(numeros))   # 4\n\n" +
            "# Leer una lista desde la entrada\n" +
            "datos = list(map(int, input().split()))\n" +
            "print(sum(datos))");

        teoria(m3, 2, "Funciones",
            "Una función agrupa código reutilizable bajo un nombre. Se define con def, puede recibir " +
            "parámetros y devolver un resultado con return.\n\n" +
            "Dividir tu solución en funciones la hace más clara y fácil de probar.",
            "def es_primo(n):\n" +
            "    if n < 2:\n" +
            "        return False\n" +
            "    for d in range(2, int(n ** 0.5) + 1):\n" +
            "        if n % d == 0:\n" +
            "            return False\n" +
            "    return True\n\n" +
            "print(es_primo(7))   # True\n" +
            "print(es_primo(8))   # False");

        ejercicio(m3, 3, "Practica: Fibonacci iterativo", "Fibonacci iterativo");
        ejercicio(m3, 4, "Practica: Números primos hasta N", "Números primos hasta N");

        examen(m3, 1,
            "¿Cuál es el índice del primer elemento de una lista en Python?",
            "1", "0", "-1", "Depende de la lista", 1);
        examen(m3, 2,
            "¿Qué palabra clave devuelve un valor desde una función?",
            "return", "yield", "give", "out", 0);
        examen(m3, 3,
            "¿Qué hace numeros.append(5)?",
            "Elimina el 5 de la lista",
            "Agrega el 5 al final de la lista",
            "Inserta el 5 al inicio",
            "Cuenta cuántos 5 hay",
            1);
    }

    private Modulo crearModulo(String titulo, String icono, String descripcion, int orden) {
        Modulo m = new Modulo();
        m.setTitulo(titulo);
        m.setIcono(icono);
        m.setDescripcion(descripcion);
        m.setOrden(orden);
        return moduloRepository.save(m);
    }

    private void teoria(Modulo modulo, int orden, String titulo, String contenido, String ejemplo) {
        Leccion l = new Leccion();
        l.setModuloId(modulo.getId());
        l.setOrden(orden);
        l.setTitulo(titulo);
        l.setTipo(TipoLeccion.TEORIA);
        l.setContenido(contenido);
        l.setEjemploCodigo(ejemplo);
        leccionRepository.save(l);
    }

    private void ejercicio(Modulo modulo, int orden, String titulo, String tituloReto) {
        Leccion l = new Leccion();
        l.setModuloId(modulo.getId());
        l.setOrden(orden);
        l.setTitulo(titulo);
        l.setTipo(TipoLeccion.EJERCICIO);
        retoRepository.findByTitulo(tituloReto).ifPresent(r -> l.setRetoId(r.getId()));
        leccionRepository.save(l);
    }

    private void examen(Modulo modulo, int orden, String enunciado,
                        String a, String b, String c, String d, int correcta) {
        PreguntaExamen p = new PreguntaExamen();
        p.setModuloId(modulo.getId());
        p.setOrden(orden);
        p.setEnunciado(enunciado);
        p.setOpcionA(a);
        p.setOpcionB(b);
        p.setOpcionC(c);
        p.setOpcionD(d);
        p.setCorrecta(correcta);
        preguntaRepository.save(p);
    }
}
