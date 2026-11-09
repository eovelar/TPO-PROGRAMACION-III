
package distribucion.pruebas;

import distribucion.algoritmos.RutasAlternativas;
import distribucion.modelo.Ubicacion;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PruebaRutasAlternativas {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("=== PRUEBAS DE RUTAS ALTERNATIVAS ===");

        try {
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            RutasAlternativas rutas = new RutasAlternativas(red);

            // prueba 1: caminos con limite de 30 minutos
            comprobarCaminos(
                    "dos caminos validos",
                    rutas, 1, 4, 30, Set.of(),
                    "1-2-4", 15,
                    "1-3-4", 23
            );

            // prueba 2: prohibir centro norte
            comprobarCaminos(
                    "centro norte prohibido",
                    rutas, 1, 4, 30, Set.of(2),
                    "1-3-4", 23
            );

            // prueba 3: tiempo insuficiente
            comprobarCaminos(
                    "tiempo maximo insuficiente",
                    rutas, 1, 4, 10, Set.of()
            );

            // prueba 4: tiempo exactamente igual al limite
            comprobarCaminos(
                    "tiempo igual al limite",
                    rutas, 1, 4, 15, Set.of(),
                    "1-2-4", 15
            );

            // prueba 5: permitir un camino mas largo
            comprobarCaminos(
                    "tres caminos validos",
                    rutas, 1, 4, 40, Set.of(),
                    "1-2-4", 15,
                    "1-3-4", 23,
                    "1-2-3-4", 38
            );

            // prueba 6: prohibir centro sur
            comprobarCaminos(
                    "centro sur prohibido",
                    rutas, 1, 4, 40, Set.of(3),
                    "1-2-4", 15
            );

            // prueba 7: prohibir ambos centros intermedios
            comprobarCaminos(
                    "sin alternativas permitidas",
                    rutas, 1, 4, 40, Set.of(2, 3)
            );

            // prueba 8: origen prohibido
            comprobarCaminos(
                    "origen prohibido",
                    rutas, 1, 4, 30, Set.of(1)
            );

            // prueba 9: destino prohibido
            comprobarCaminos(
                    "destino prohibido",
                    rutas, 1, 4, 30, Set.of(4)
            );

            // prueba 10: origen igual a destino
            comprobarCaminos(
                    "origen igual a destino",
                    rutas, 1, 1, 0, Set.of(),
                    "1", 0
            );

            // prueba 11: deshabilitar una conexion
            red.deshabilitarConexion(2, 4);

            comprobarCaminos(
                    "conexion deshabilitada",
                    rutas, 1, 4, 30, Set.of(),
                    "1-3-4", 23
            );

            // prueba 12: restaurar la conexion
            red.habilitarConexion(2, 4);

            comprobarCaminos(
                    "conexion restaurada",
                    rutas, 1, 4, 30, Set.of(),
                    "1-2-4", 15,
                    "1-3-4", 23
            );

            // prueba 13: resultados anteriores eliminados
            comprobarCaminos(
                    "reinicio de resultados",
                    rutas, 1, 4, 10, Set.of()
            );

            // prueba 14: origen inexistente
            probarError("origen inexistente", () ->
                    rutas.buscar(999, 4, 30, Set.of())
            );

            // prueba 15: destino inexistente
            probarError("destino inexistente", () ->
                    rutas.buscar(1, 999, 30, Set.of())
            );

            // prueba 16: tiempo negativo
            probarError("tiempo negativo", () ->
                    rutas.buscar(1, 4, -10, Set.of())
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "no se pudieron cargar los archivos de prueba", e
            );
        }

        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println("pruebas correctas: " + correctas);
        System.out.println("pruebas fallidas: " + fallidas);

        if (fallidas > 0) {
            throw new AssertionError(
                    "hay pruebas de rutas alternativas que no pasaron"
            );
        }
    }

    // comprobar los caminos y sus tiempos
    private static void comprobarCaminos(
            String nombre,
            RutasAlternativas rutas,
            int origen,
            int destino,
            int tiempoMaximo,
            Set<Integer> prohibidas,
            Object... resultadosEsperados) {

        try {
            ejecutarSinMostrar(() ->
                    rutas.buscar(origen, destino, tiempoMaximo, prohibidas)
            );

            Map<String, Integer> esperados = new HashMap<>();

            for (int i = 0; i < resultadosEsperados.length; i += 2) {
                String camino = (String) resultadosEsperados[i];
                int tiempo = (Integer) resultadosEsperados[i + 1];

                esperados.put(camino, tiempo);
            }

            List<List<Ubicacion>> caminos = rutas.getCaminos();
            List<Integer> tiempos = rutas.getTiempos();

            Map<String, Integer> obtenidos = new HashMap<>();

            boolean sinRepetidos = true;

            for (int i = 0; i < caminos.size(); i++) {

                String camino = convertirCamino(caminos.get(i));

                if (obtenidos.containsKey(camino)) {
                    sinRepetidos = false;
                }

                obtenidos.put(camino, tiempos.get(i));
            }

            boolean correcta =
                    caminos.size() == tiempos.size()
                            && caminos.size() == esperados.size()
                            && sinRepetidos
                            && obtenidos.equals(esperados);

            registrar(nombre, correcta);

            if (!correcta) {
                System.out.println("  esperado: " + esperados);
                System.out.println("  obtenido: " + obtenidos);
            }

        } catch (Exception e) {
            registrar(nombre, false);
            System.out.println("  error: " + e.getMessage());
        }
    }

    // convertir un recorrido en una secuencia de ids
    private static String convertirCamino(List<Ubicacion> camino) {

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < camino.size(); i++) {

            if (i > 0) {
                resultado.append("-");
            }

            resultado.append(camino.get(i).getId());
        }

        return resultado.toString();
    }

    // ocultar los detalles durante las pruebas
    private static void ejecutarSinMostrar(Runnable accion) {

        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(
                    buffer, true, StandardCharsets.UTF_8
            ));

            accion.run();

        } finally {
            System.setOut(original);
        }
    }

    // comprobar entradas invalidas
    private static void probarError(String nombre, Runnable accion) {

        try {
            ejecutarSinMostrar(accion);
            registrar(nombre, false);

        } catch (IllegalArgumentException e) {
            registrar(nombre, true);
        }
    }

    private static void registrar(String nombre, boolean correcta) {

        if (correcta) {
            correctas++;
            System.out.println("[OK] " + nombre);

        } else {
            fallidas++;
            System.out.println("[ERROR] " + nombre);
        }
    }
}
