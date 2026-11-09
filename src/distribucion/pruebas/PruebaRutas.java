
package distribucion.pruebas;

import distribucion.algoritmos.Dijkstra;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;

import java.io.IOException;
import java.util.List;

public class PruebaRutas {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("=== PRUEBAS DE DIJKSTRA ===");

        try {
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            Dijkstra dijkstra = new Dijkstra(red);

            Ubicacion deposito = red.buscarUbicacionPorId(1);
            Ubicacion norte = red.buscarUbicacionPorId(2);
            Ubicacion sur = red.buscarUbicacionPorId(3);
            Ubicacion este = red.buscarUbicacionPorId(4);

            if (deposito == null || norte == null ||
                    sur == null || este == null) {
                throw new IllegalStateException(
                        "faltan ubicaciones de prueba en los txt"
                );
            }

            // prueba 1: camino minimo
            ResultadoRuta normal =
                    dijkstra.calcularRuta(deposito, este);

            comprobarRuta(
                    "ruta mas rapida",
                    normal,
                    15,
                    1, 2, 4
            );

            // prueba 2: conexion deshabilitada
            red.deshabilitarConexion(2, 4);

            ResultadoRuta alternativa =
                    dijkstra.calcularRuta(deposito, este);

            comprobarRuta(
                    "recalculo con conexion deshabilitada",
                    alternativa,
                    23,
                    1, 3, 4
            );

            // prueba 3: restaurar la conexion
            red.habilitarConexion(2, 4);

            ResultadoRuta restaurada =
                    dijkstra.calcularRuta(deposito, este);

            comprobarRuta(
                    "conexion habilitada nuevamente",
                    restaurada,
                    15,
                    1, 2, 4
            );

            // prueba 4: origen igual a destino
            ResultadoRuta mismoLugar =
                    dijkstra.calcularRuta(deposito, deposito);

            comprobarRuta(
                    "origen igual a destino",
                    mismoLugar,
                    0,
                    1
            );

            // prueba 5: destino sin camino posible
            ResultadoRuta inaccesible =
                    dijkstra.calcularRuta(este, norte);

            // aislar una ubicacion para probar inaccesibilidad
            boolean inaccesibilidadComprobada =
                    probarDestinoInaccesible();

            registrar(
                    "destino inalcanzable",
                    inaccesibilidadComprobada
            );

            // prueba 6: origen nulo
            probarError("origen nulo", () ->
                    dijkstra.calcularRuta(null, este)
            );

            // prueba 7: destino nulo
            probarError("destino nulo", () ->
                    dijkstra.calcularRuta(deposito, null)
            );

            // prueba 8: ubicacion inexistente
            Ubicacion desconocida = new Ubicacion(
                    999,
                    "Ubicacion desconocida",
                    TipoUbicacion.CENTRO_COMUNITARIO
            );

            probarError("ubicacion fuera de la red", () ->
                    dijkstra.calcularRuta(deposito, desconocida)
            );

            // prueba 9: red nula
            probarError("red nula", () ->
                    new Dijkstra(null)
            );

            // prueba 10: comparar distintas alternativas
            comprobarRuta(
                    "mejor camino frente a una alternativa mas larga",
                    dijkstra.calcularRuta(deposito, este),
                    15,
                    1, 2, 4
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
                    "hay pruebas de rutas que no pasaron"
            );
        }
    }

    // comprobar el tiempo y el recorrido
    private static void comprobarRuta(
            String nombre,
            ResultadoRuta resultado,
            int tiempoEsperado,
            int... idsEsperados) {

        boolean correcta = resultado.isExisteRuta()
                && resultado.getTiempoTotal() == tiempoEsperado;

        if (correcta) {
            List<Ubicacion> recorrido = resultado.getRecorrido();

            correcta = recorrido != null
                    && recorrido.size() == idsEsperados.length;

            if (correcta) {
                for (int i = 0; i < idsEsperados.length; i++) {
                    if (recorrido.get(i).getId() != idsEsperados[i]) {
                        correcta = false;
                        break;
                    }
                }
            }
        }

        registrar(nombre, correcta);
    }

    // comprobar una red sin conexiones
    private static boolean probarDestinoInaccesible() {

        RedVial red = new RedVial();

        Ubicacion origen = new Ubicacion(
                1, "Deposito", TipoUbicacion.DEPOSITO
        );

        Ubicacion destino = new Ubicacion(
                2, "Centro aislado", TipoUbicacion.CENTRO_COMUNITARIO
        );

        red.agregarUbicacion(origen);
        red.agregarUbicacion(destino);

        Dijkstra dijkstra = new Dijkstra(red);

        ResultadoRuta resultado =
                dijkstra.calcularRuta(origen, destino);

        return !resultado.isExisteRuta();
    }

    // comprobar entradas invalidas
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
