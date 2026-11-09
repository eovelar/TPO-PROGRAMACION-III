
package distribucion.pruebas;

import distribucion.algoritmos.SeleccionCargas;
import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Vehiculo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class PruebaCargas {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("=== PRUEBAS DE MOCHILA 0/1 ===");

        // prueba 1: seleccion optima
        LoteMercaderia[] lotes = {
                new LoteMercaderia(1, 10, 30),
                new LoteMercaderia(2, 20, 40),
                new LoteMercaderia(3, 30, 50),
                new LoteMercaderia(4, 40, 60)
        };

        probarSeleccion(
                "seleccion optima",
                lotes,
                50,
                90,
                50,
                0,
                2, 3
        );

        // prueba 2: lista vacia
        probarSeleccion(
                "sin lotes disponibles",
                new LoteMercaderia[]{},
                50,
                0,
                0,
                50
        );

        // prueba 3: capacidad cero
        probarSeleccion(
                "capacidad cero",
                lotes,
                0,
                0,
                0,
                0
        );

        // prueba 4: capacidad mayor al peso total
        probarSeleccion(
                "capacidad muy grande",
                lotes,
                1000,
                180,
                100,
                900,
                1, 2, 3, 4
        );

        // prueba 5: ningun lote entra
        LoteMercaderia[] lotesPesados = {
                new LoteMercaderia(5, 30, 50),
                new LoteMercaderia(6, 40, 80)
        };

        probarSeleccion(
                "ningun lote entra",
                lotesPesados,
                10,
                0,
                0,
                10
        );

        // prueba 6: prioridades iguales
        LoteMercaderia[] lotesEmpate = {
                new LoteMercaderia(5, 10, 20),
                new LoteMercaderia(6, 20, 20)
        };

        probarEmpate(lotesEmpate);

        // prueba 7: greedy no encuentra el optimo
        LoteMercaderia[] lotesGreedy = {
                new LoteMercaderia(7, 10, 60),
                new LoteMercaderia(8, 20, 100),
                new LoteMercaderia(9, 30, 120)
        };

        probarSeleccion(
                "comparacion con greedy",
                lotesGreedy,
                50,
                220,
                50,
                0,
                8, 9
        );

        // prueba 8: peso negativo
        probarError("peso negativo", () -> {
            new LoteMercaderia(10, -10, 20);
        });

        // prueba 9: capacidad negativa
        probarError("capacidad negativa", () -> {
            new Vehiculo(11, -50);
        });

        // prueba 10: lote nulo
        probarError("lote nulo", () -> {
            LoteMercaderia[] datos = {null};

            SeleccionCargas.seleccionar(
                    datos,
                    new Vehiculo(12, 50)
            );
        });

        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println("pruebas correctas: " + correctas);
        System.out.println("pruebas fallidas: " + fallidas);

        if (fallidas > 0) {
            throw new AssertionError(
                    "hay pruebas de cargas que no pasaron"
            );
        }
    }

    // comprobar los resultados de la seleccion
    private static void probarSeleccion(
            String nombre,
            LoteMercaderia[] lotes,
            int capacidad,
            int prioridadEsperada,
            long pesoEsperado,
            long restanteEsperada,
            int... idsEsperados) {

        try {
            Vehiculo vehiculo = new Vehiculo(1, capacidad);

            String salida = ejecutarSeleccion(lotes, vehiculo);

            Set<Integer> seleccionados = obtenerSeleccionados(salida);
            Set<Integer> esperados = new HashSet<>();

            for (int id : idsEsperados) {
                esperados.add(id);
            }

            boolean prioridadCorrecta = salida.contains(
                    "prioridad total: " + prioridadEsperada
            );

            boolean pesoCorrecto = salida.contains(
                    "peso total: " + pesoEsperado + " kg"
            );

            boolean restanteCorrecta = salida.contains(
                    "capacidad restante: " + restanteEsperada + " kg"
            );

            boolean lotesCorrectos =
                    seleccionados.equals(esperados);

            if (prioridadCorrecta && pesoCorrecto
                    && restanteCorrecta && lotesCorrectos) {

                registrar(nombre, true);

            } else {
                registrar(nombre, false);
                System.out.println(
                        "  resultado inesperado:\n" + salida
                );
            }

        } catch (Exception e) {
            registrar(nombre, false);
            System.out.println("  error: " + e.getMessage());
        }
    }

    // aceptar cualquiera de las soluciones optimas
    private static void probarEmpate(LoteMercaderia[] lotes) {

        try {
            String salida = ejecutarSeleccion(
                    lotes,
                    new Vehiculo(3, 20)
            );

            Set<Integer> seleccionados = obtenerSeleccionados(salida);

            Set<Integer> opcionUno = new HashSet<>();
            opcionUno.add(5);

            Set<Integer> opcionDos = new HashSet<>();
            opcionDos.add(6);

            boolean prioridadCorrecta =
                    salida.contains("prioridad total: 20");

            boolean primerResultado =
                    seleccionados.equals(opcionUno)
                            && salida.contains("peso total: 10 kg")
                            && salida.contains("capacidad restante: 10 kg");

            boolean segundoResultado =
                    seleccionados.equals(opcionDos)
                            && salida.contains("peso total: 20 kg")
                            && salida.contains("capacidad restante: 0 kg");

            boolean correcta = prioridadCorrecta
                    && (primerResultado || segundoResultado);

            registrar("prioridades iguales", correcta);

            if (!correcta) {
                System.out.println(
                        "  resultado inesperado:\n" + salida
                );
            }

        } catch (Exception e) {
            registrar("prioridades iguales", false);
            System.out.println("  error: " + e.getMessage());
        }
    }

    // ejecutar el algoritmo y capturar su salida
    private static String ejecutarSeleccion(
            LoteMercaderia[] lotes,
            Vehiculo vehiculo) {

        PrintStream original = System.out;

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(
                    buffer,
                    true,
                    StandardCharsets.UTF_8
            ));

            SeleccionCargas.seleccionar(lotes, vehiculo);

        } finally {
            System.setOut(original);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    // obtener los ids seleccionados
    private static Set<Integer> obtenerSeleccionados(String salida) {

        Set<Integer> seleccionados = new HashSet<>();

        for (String linea : salida.split("\\R")) {

            linea = linea.trim();

            if (linea.matches("lote \\d+")) {

                int id = Integer.parseInt(
                        linea.substring(5)
                );

                seleccionados.add(id);
            }
        }

        return seleccionados;
    }

    // comprobar que las entradas invalidas se rechacen
    private static void probarError(
            String nombre,
            Runnable prueba) {

        try {
            prueba.run();
            registrar(nombre, false);

        } catch (IllegalArgumentException e) {
            registrar(nombre, true);
        }
    }

    // registrar cada resultado
    private static void registrar(
            String nombre,
            boolean correcta) {

        if (correcta) {
            correctas++;
            System.out.println("[OK] " + nombre);

        } else {
            fallidas++;
            System.out.println("[ERROR] " + nombre);
        }
    }
}
