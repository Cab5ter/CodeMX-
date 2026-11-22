package mx.codemx.persistence;

import java.util.List;
import mx.codemx.modules.cursos.Leccion;
import mx.codemx.modules.cursos.LeccionRepository;
import mx.codemx.modules.cursos.Modulo;
import mx.codemx.modules.cursos.ModuloRepository;
import mx.codemx.modules.cursos.PreguntaExamen;
import mx.codemx.modules.cursos.PreguntaExamenRepository;
import mx.codemx.modules.cursos.TipoLeccion;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;
import mx.codemx.modules.retos.Reto;
import mx.codemx.modules.retos.RetoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Siembra los retos y los cursos de ejemplo si la base está vacía.
 *
 * <p>No hace falta crear a mano los esquemas ni las tablas: Hibernate, con
 * {@code ddl-auto: update} y {@code create_namespaces: true}, crea el esquema de cada
 * módulo y sus tablas al arrancar.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RetoRepository retos;
    private final ModuloRepository modulos;
    private final LeccionRepository lecciones;
    private final PreguntaExamenRepository preguntas;
    private final JdbcTemplate jdbc;

    public DataSeeder(RetoRepository retos, ModuloRepository modulos,
                      LeccionRepository lecciones, PreguntaExamenRepository preguntas, JdbcTemplate jdbc) {
        this.retos = retos;
        this.modulos = modulos;
        this.lecciones = lecciones;
        this.preguntas = preguntas;
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedRetos();
        seedCursos();
    }

    private void seedRetos() {
        // Catálogo nuevo con progresión real para principiantes. El primer reto es "Hola, Mundo":
        // si ya está, no hace falta resembrar.
        String primero = retos.findAllByOrderByIdAsc().stream()
                .findFirst()
                .map(Reto::getTitulo)
                .orElse(null);
        if ("Hola, Mundo".equals(primero)) {
            return;
        }

        // Resembrado: borra el catálogo viejo y reinicia los IDs (no toca ranking ni duelos).
        jdbc.execute("TRUNCATE TABLE retos.casos_prueba, retos.retos RESTART IDENTITY CASCADE");

        // --- FÁCIL: solo print y variables, SIN entrada (así se empieza en Python) ---
        crearReto("Hola, Mundo",
                "Tu primer programa. Imprime exactamente este texto:\n\nHola, Mundo!\n\n"
                        + "No se lee nada de teclado; solo usa print().",
                Dificultad.BASICO, "", "Hola, Mundo!");

        crearReto("Tu primera variable",
                "Crea una variable llamada mensaje con el texto: Aprendiendo Python\n"
                        + "Luego imprímela.\n\nSalida esperada:\nAprendiendo Python",
                Dificultad.BASICO, "", "Aprendiendo Python");

        crearReto("Suma con variables",
                "Crea dos variables: a = 7 y b = 3. Imprime el resultado de sumarlas.\n\nSalida esperada:\n10",
                Dificultad.BASICO, "", "10");

        crearReto("Área del cuadrado",
                "Un cuadrado mide 5 de lado. Crea la variable lado = 5 e imprime su área "
                        + "(lado por lado).\n\nSalida esperada:\n25",
                Dificultad.BASICO, "", "25");

        // --- INTERMEDIO: variables fijas y condicionales, SIN entrada ---
        crearReto("El doble",
                "Crea la variable n = 4 e imprime su doble (n multiplicado por 2).\n\nSalida esperada:\n8",
                Dificultad.INTERMEDIO, "", "8");

        crearReto("Saludo personalizado",
                "Crea la variable nombre = \"Ana\" e imprime un saludo con el formato: Hola, NOMBRE!\n\n"
                        + "Salida esperada:\nHola, Ana!",
                Dificultad.INTERMEDIO, "", "Hola, Ana!");

        crearReto("Par o impar",
                "Crea la variable n = 7. Imprime PAR si es par o IMPAR si es impar "
                        + "(usa n % 2).\n\nSalida esperada:\nIMPAR",
                Dificultad.INTERMEDIO, "", "IMPAR");

        crearReto("Suma de dos números",
                "Crea dos variables a = 2 y b = 3. Imprime el resultado de sumarlas.\n\nSalida esperada:\n5",
                Dificultad.INTERMEDIO, "", "5");

        // --- DIFÍCIL: ciclos y algoritmos con valores fijos, SIN entrada ---
        crearReto("FizzBuzz",
                "Crea la variable n = 5. Imprime los números del 1 al n; múltiplos de 3 => 'Fizz', "
                        + "de 5 => 'Buzz', de ambos => 'FizzBuzz'.\n\nSalida esperada:\n1\n2\nFizz\n4\nBuzz",
                Dificultad.AVANZADO, "", "1\n2\nFizz\n4\nBuzz");

        crearReto("Números primos hasta N",
                "Crea la variable n = 10. Imprime todos los números primos menores o iguales a n, "
                        + "uno por línea.\n\nSalida esperada:\n2\n3\n5\n7",
                Dificultad.AVANZADO, "", "2\n3\n5\n7");

        crearReto("Fibonacci iterativo",
                "Crea la variable n = 8. Imprime los primeros n términos de Fibonacci separados por "
                        + "espacios (empieza en 0 1).\n\nSalida esperada:\n0 1 1 2 3 5 8 13",
                Dificultad.AVANZADO, "", "0 1 1 2 3 5 8 13");

        crearReto("Búsqueda binaria",
                "Crea el arreglo ordenado arr = [1, 3, 5, 7, 9] y el valor k = 5. Imprime el índice "
                        + "(base 0) de k en arr, o -1 si no existe.\n\nSalida esperada:\n2",
                Dificultad.AVANZADO, "", "2");
    }

    private void crearReto(String titulo, String descripcion, Dificultad dificultad,
                           String input, String output) {
        Reto reto = new Reto();
        reto.setTitulo(titulo);
        reto.setDescripcion(descripcion);
        reto.setDificultad(dificultad);

        // La relación uno a muchos se encarga del resto: al guardar el reto, la cascada
        // persiste sus casos con la clave foránea ya puesta.
        reto.agregarCaso(new CasoPrueba(input, output));
        retos.save(reto);
    }

    // ============================================================
    //  CURSOS: módulos, lecciones (teoría + ejercicio) y exámenes
    // ============================================================
    private void seedCursos() {
        if (modulos.count() > 0) {
            return;
        }

        // ---------- Módulo 1: Fundamentos de Python ----------
        Modulo m1 = crearModulo("Fundamentos de Python", "🐍",
                "Aprende las bases del lenguaje: variables, tipos de datos, texto y operadores.", 1);

        teoria(m1, 1, "Variables y tipos de datos",
                "Una variable es un nombre que guarda un valor. En Python no declaras el tipo: "
                        + "se infiere del valor que asignas con el signo =.\n\n"
                        + "Los tipos básicos son:\n• int: enteros (5, -3)\n• float: con decimales (3.14)\n"
                        + "• str: texto entre comillas (\"hola\")\n• bool: True / False\n\n"
                        + "Puedes ver el tipo con la función type().",
                "edad = 20          # int\npromedio = 8.5     # float\nnombre = \"Ana\"     # str\n\n"
                        + "print(nombre, \"tiene\", edad, \"años\")");

        teoria(m1, 2, "Cadenas de texto (str)",
                "Una cadena (str) es texto entre comillas. Puedes unir varias con el operador + "
                        + "(concatenar) y repetirlas con *.\n\n"
                        + "• len(texto): cuántos caracteres tiene\n• texto.upper(): todo en mayúsculas\n"
                        + "• texto.lower(): todo en minúsculas\n\n"
                        + "Con f-strings insertas variables dentro del texto poniendo una f antes de las comillas.",
                "nombre = \"Ana\"\nsaludo = \"Hola, \" + nombre + \"!\"\nprint(saludo)            # Hola, Ana!\n\n"
                        + "print(nombre.upper())    # ANA\nprint(f\"{nombre} tiene 3 letras\")");

        teoria(m1, 3, "Operadores aritméticos",
                "Python incluye:\n• +  suma\n• -  resta\n• *  multiplicación\n• /  división (float)\n"
                        + "• // división entera\n• %  módulo (residuo)\n• ** potencia\n\n"
                        + "El operador % es muy útil: un número es par si n % 2 == 0.",
                "print(7 / 2)    # 3.5\nprint(7 // 2)   # 3\nprint(7 % 2)    # 1\nprint(2 ** 10)  # 1024");

        ejercicio(m1, 4, "Practica: Suma de dos números", "Suma de dos números");

        examen(m1, 1, "¿Cuál es el resultado de 7 % 3?", "2", "1", "3", "0", 1);
        examen(m1, 2, "¿Qué función muestra información en pantalla?",
                "type()", "print()", "len()", "show()", 1);
        examen(m1, 3, "¿Qué hace \"ab\" + \"cd\" en Python?",
                "Suma 0", "Une las cadenas: \"abcd\"", "Da un error", "Repite \"ab\"", 1);

        // ---------- Módulo 2: Control de flujo ----------
        Modulo m2 = crearModulo("Control de flujo", "🔀",
                "Toma decisiones con condicionales y repite acciones con bucles.", 2);

        teoria(m2, 1, "Condicionales: if, elif, else",
                "Los condicionales ejecutan código sólo si se cumple una condición. Los bloques se definen "
                        + "por indentación (4 espacios), no por llaves.\n\n• if: si la condición es verdadera\n"
                        + "• elif: prueba otra condición\n• else: si ninguna se cumplió\n\n"
                        + "Comparadores: == != < > <= >=.",
                "n = -4\n\nif n > 0:\n    print(\"positivo\")\nelif n < 0:\n    print(\"negativo\")\n"
                        + "else:\n    print(\"cero\")");

        teoria(m2, 2, "Bucles: for y while",
                "• for se usa cuando sabes cuántas veces repetir. range(1, n+1) genera de 1 a n.\n"
                        + "• while repite mientras una condición sea verdadera.\n\n"
                        + "Recuerda que range(a, b) NO incluye b.",
                "n = 5\n\nfor i in range(1, n + 1):\n    print(i)");

        ejercicio(m2, 3, "Practica: FizzBuzz", "FizzBuzz");
        ejercicio(m2, 4, "Practica: Número palíndromo", "Número palíndromo");

        examen(m2, 1, "¿Qué imprime range(1, 5)?", "1 2 3 4 5", "1 2 3 4", "0 1 2 3 4", "1 2 3", 1);
        examen(m2, 2, "¿Cómo se definen los bloques de código en Python?",
                "Con llaves { }", "Con indentación (espacios)", "Con paréntesis ( )", "Con punto y coma ;", 1);
        examen(m2, 3, "¿Qué palabra clave repite mientras una condición sea verdadera?",
                "for", "repeat", "while", "loop", 2);

        // ---------- Módulo 3: Datos y algoritmos ----------
        Modulo m3 = crearModulo("Datos y algoritmos", "🧮",
                "Organiza información con listas y funciones, y resuelve algoritmos clásicos.", 3);

        teoria(m3, 1, "Listas en Python",
                "Una lista guarda varios valores en orden, entre corchetes y separados por comas. Se accede "
                        + "por índice empezando en 0.\n\n• lista.append(x): agrega al final\n"
                        + "• len(lista): cantidad\n• lista[i]: elemento en la posición i\n\n"
                        + "Puedes recorrer una lista con un bucle for para procesar cada elemento.",
                "numeros = [10, 20, 30]\nnumeros.append(40)\n\nprint(numeros[0])     # 10\n"
                        + "print(len(numeros))   # 4");

        teoria(m3, 2, "Funciones",
                "Una función agrupa código reutilizable. Se define con def, recibe parámetros y devuelve un "
                        + "resultado con return.",
                "def es_primo(n):\n    if n < 2:\n        return False\n"
                        + "    for d in range(2, int(n ** 0.5) + 1):\n"
                        + "        if n % d == 0:\n            return False\n    return True\n\n"
                        + "print(es_primo(7))   # True");

        ejercicio(m3, 3, "Practica: Fibonacci iterativo", "Fibonacci iterativo");
        ejercicio(m3, 4, "Practica: Números primos hasta N", "Números primos hasta N");

        examen(m3, 1, "¿Cuál es el índice del primer elemento de una lista?", "1", "0", "-1", "Depende", 1);
        examen(m3, 2, "¿Qué palabra clave devuelve un valor desde una función?",
                "return", "yield", "give", "out", 0);
        examen(m3, 3, "¿Qué hace numeros.append(5)?",
                "Elimina el 5", "Agrega el 5 al final", "Inserta el 5 al inicio", "Cuenta cuántos 5 hay", 1);
    }

    private Modulo crearModulo(String titulo, String icono, String descripcion, int orden) {
        Modulo m = new Modulo();
        m.setTitulo(titulo);
        m.setIcono(icono);
        m.setDescripcion(descripcion);
        m.setOrden(orden);
        return modulos.save(m);
    }

    private void teoria(Modulo modulo, int orden, String titulo, String contenido, String ejemplo) {
        Leccion l = new Leccion();
        l.setModuloId(modulo.getId());
        l.setOrden(orden);
        l.setTitulo(titulo);
        l.setTipo(TipoLeccion.TEORIA);
        l.setContenido(contenido);
        l.setEjemploCodigo(ejemplo);
        lecciones.save(l);
    }

    private void ejercicio(Modulo modulo, int orden, String titulo, String tituloReto) {
        Long retoId = retos.findByTitulo(tituloReto).map(Reto::getId).orElse(null);

        Leccion l = new Leccion();
        l.setModuloId(modulo.getId());
        l.setOrden(orden);
        l.setTitulo(titulo);
        l.setTipo(TipoLeccion.EJERCICIO);
        l.setRetoId(retoId);
        lecciones.save(l);
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
        preguntas.save(p);
    }
}
