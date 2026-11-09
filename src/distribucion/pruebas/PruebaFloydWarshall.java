
package distribucion.pruebas;

import distribucion.algoritmos.FloydWarshall;
import distribucion.modelo.Ubicacion;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;

import java.io.IOException;
import java.util.List;

public class PruebaFloydWarshall {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("=== PRUEBAS DE FLOYD-WARSHALL ===");

        try {
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            FloydWarshall floyd = new FloydWarshall(red);

            // prueba 1: consultar antes de calcular
            probarError("consulta sin calcular", () ->
                    floyd.consultar(1, 4)
            );

            // calcular las matrices una sola vez
            floyd.calcular();

            // prueba 2: camino minimo
            comprobarRuta(
                    "ruta mas rapida",
                    floyd.consultar(1, 4),
                    15,
                    1, 2, 4
            );

            // prueba 3: otra consulta sin recalcular
            comprobarRuta(
                    "segunda consulta",
                    floyd.consultar(1, 3),
                    15,
                    1, 3
            );

            // prueba 4: origen igual a destino
            comprobarRuta(
                    "origen igual a destino",
                    floyd.consultar(1, 1),
                    0,
                    1
            );

            // prueba 5: destino inalcanzable
            ResultadoRuta inaccesible = floyd.consultar(5, 4);

            registrar(
                    "destino inalcanzable",
                    !inaccesible.isExisteRuta()
            );

            // prueba 6: ubicacion inexistente
            ResultadoRuta inexistente = floyd.consultar(1, 999);

            registrar(
                    "ubicacion inexistente",
                    !inexistente.isExisteRuta()
            );

            // prueba 7: recalcular con una conexion deshabilitada
            red.deshabilitarConexion(2, 4);
            floyd.calcular();

            comprobarRuta(
                    "conexion deshabilitada",
                    floyd.consultar(1, 4),
                    23,
                    1, 3, 4
            );

            // prueba 8: restaurar la conexion
            red.habilitarConexion(2, 4);
            floyd.calcular();

            comprobarRuta(
                    "conexion restaurada",
                    floyd.consultar(1, 4),
                    15,
                    1, 2, 4
            );

            // prueba 9: consultar otro par de ubicaciones
            comprobarRuta(
                    "ruta hacia deposito secundario",
                    floyd.consultar(1, 5),
                    22,
                    1, 2, 5
            );

            // prueba 10: repetir una consulta
            comprobarRuta(
                    "consulta repetida sin recalcular",
                    floyd.consultar(1, 4),
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
                    "hay pruebas de floyd-warshall que no pasaron"
            );
        }
    }

    // comprobar tiempo y recorrido
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

    // comprobar que se rechace una consulta sin calcular
    private static void probarError(
            String nombre,
            Runnable prueba) {

        try {
            prueba.run();
            registrar(nombre, false);

        } catch (IllegalStateException e) {
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
