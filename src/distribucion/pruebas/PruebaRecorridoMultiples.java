
package distribucion.pruebas;

import distribucion.algoritmos.FloydWarshall;
import distribucion.algoritmos.RecorridoMultiplesEntregas;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PruebaRecorridoMultiples {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("=== PRUEBAS DE RECORRIDOS MULTIPLES ===");

        try {
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            FloydWarshall floyd = new FloydWarshall(red);
            floyd.calcular();

            Ubicacion deposito = red.buscarUbicacionPorId(1);
            Ubicacion norte = red.buscarUbicacionPorId(2);
            Ubicacion sur = red.buscarUbicacionPorId(3);
            Ubicacion este = red.buscarUbicacionPorId(4);

            List<Ubicacion> tresCentros =
                    Arrays.asList(norte, sur, este);

            // comprobar el circuito de tres entregas
            RecorridoMultiplesEntregas recorrido =
                    new RecorridoMultiplesEntregas(floyd);

            ejecutarSinMostrar(() ->
                    recorrido.calcular(deposito, tresCentros)
            );

            registrar(
                    "recorrido de tres centros",
                    recorrido.isExisteRecorrido()
                            && recorrido.getMenorTiempo() == 56
                            && ordenValido(recorrido.getMejorOrden(), tresCentros)
            );

            // comparar con todas las permutaciones posibles
            long tiempoExhaustivo = buscarSinPodas(
                    floyd, deposito, tresCentros
            );

            registrar(
                    "optimalidad frente a busqueda exhaustiva",
                    recorrido.isExisteRecorrido()
                            && recorrido.getMenorTiempo() == tiempoExhaustivo
            );

            // comprobar una sola entrega
            ejecutarSinMostrar(() ->
                    recorrido.calcular(deposito, Arrays.asList(norte))
            );

            registrar(
                    "una sola entrega",
                    recorrido.isExisteRecorrido()
                            && recorrido.getMenorTiempo() == 20
                            && ordenValido(
                            recorrido.getMejorOrden(),
                            Arrays.asList(norte)
                    )
            );

            // comprobar dos centros
            List<Ubicacion> dosCentros = Arrays.asList(norte, sur);

            ejecutarSinMostrar(() ->
                    recorrido.calcular(deposito, dosCentros)
            );

            registrar(
                    "dos centros",
                    recorrido.isExisteRecorrido()
                            && recorrido.getMenorTiempo()
                            == buscarSinPodas(floyd, deposito, dosCentros)
                            && ordenValido(
                            recorrido.getMejorOrden(), dosCentros
                    )
            );

            // comprobar que el resultado se reinicia
            registrar(
                    "reinicio entre ejecuciones",
                    recorrido.getMejorOrden().size() == 2
                            && recorrido.isExisteRecorrido()
            );

            // comprobar un deposito sin salida
            Ubicacion depositoSecundario =
                    red.buscarUbicacionPorId(5);

            ejecutarSinMostrar(() ->
                    recorrido.calcular(
                            depositoSecundario,
                            Arrays.asList(norte)
                    )
            );

            registrar(
                    "sin recorrido posible",
                    !recorrido.isExisteRecorrido()
                            && recorrido.getMejorOrden().isEmpty()
            );

            // comprobar que una conexion puede impedir el regreso
            red.deshabilitarConexion(4, 1);
            floyd.calcular();

            ejecutarSinMostrar(() ->
                    recorrido.calcular(deposito, tresCentros)
            );

            registrar(
                    "regreso al deposito bloqueado",
                    !recorrido.isExisteRecorrido()
            );

            red.habilitarConexion(4, 1);
            floyd.calcular();

            ejecutarSinMostrar(() ->
                    recorrido.calcular(deposito, tresCentros)
            );

            registrar(
                    "restaurar conexion",
                    recorrido.isExisteRecorrido()
                            && recorrido.getMenorTiempo() == 56
            );

            // comprobar centros repetidos
            probarError("centros repetidos", () ->
                    recorrido.calcular(
                            deposito,
                            Arrays.asList(norte, norte)
                    )
            );

            // comprobar lista vacia
            probarError("lista vacia", () ->
                    recorrido.calcular(deposito, new ArrayList<>())
            );

            // comprobar deposito nulo
            probarError("deposito nulo", () ->
                    recorrido.calcular(null, tresCentros)
            );

            // comprobar lista nula
            probarError("lista nula", () ->
                    recorrido.calcular(deposito, null)
            );

            // comprobar ubicacion incorrecta como deposito
            probarError("origen que no es deposito", () ->
                    recorrido.calcular(norte, Arrays.asList(sur))
            );

            // comprobar deposito usado como centro
            probarError("deposito como entrega", () ->
                    recorrido.calcular(deposito, Arrays.asList(deposito))
            );

            // comprobar mas de ocho centros
            List<Ubicacion> nueveCentros = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                nueveCentros.add(new Ubicacion(
                        100 + i,
                        "Centro " + i,
                        TipoUbicacion.CENTRO_COMUNITARIO
                ));
            }

            probarError("mas de ocho centros", () ->
                    recorrido.calcular(deposito, nueveCentros)
            );

            // comprobar que el algoritmo no acepte floyd nulo
            probarError("floyd nulo", () ->
                    new RecorridoMultiplesEntregas(null)
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
                    "hay pruebas de recorridos multiples que no pasaron"
            );
        }
    }

    // verificar que se visiten todos los centros una sola vez
    private static boolean ordenValido(
            List<Ubicacion> orden,
            List<Ubicacion> centros) {

        if (orden.size() != centros.size()) {
            return false;
        }

        Set<Integer> esperados = new HashSet<>();
        Set<Integer> obtenidos = new HashSet<>();

        for (Ubicacion centro : centros) {
            esperados.add(centro.getId());
        }

        for (Ubicacion centro : orden) {
            if (!obtenidos.add(centro.getId())) {
                return false;
            }
        }

        return obtenidos.equals(esperados);
    }

    // calcular el minimo sin utilizar podas
    private static long buscarSinPodas(
            FloydWarshall floyd,
            Ubicacion deposito,
            List<Ubicacion> centros) {

        return explorarTodas(
                floyd,
                deposito,
                deposito,
                centros,
                new boolean[centros.size()],
                0,
                0
        );
    }

    // probar todas las permutaciones posibles
    private static long explorarTodas(
            FloydWarshall floyd,
            Ubicacion deposito,
            Ubicacion actual,
            List<Ubicacion> centros,
            boolean[] visitados,
            int cantidadVisitada,
            long tiempo) {

        if (cantidadVisitada == centros.size()) {

            ResultadoRuta regreso = floyd.consultar(
                    actual.getId(), deposito.getId()
            );

            if (!regreso.isExisteRuta()) {
                return Long.MAX_VALUE;
            }

            return tiempo + regreso.getTiempoTotal();
        }

        long mejor = Long.MAX_VALUE;

        for (int i = 0; i < centros.size(); i++) {

            if (visitados[i]) {
                continue;
            }

            Ubicacion siguiente = centros.get(i);

            ResultadoRuta tramo = floyd.consultar(
                    actual.getId(), siguiente.getId()
            );

            if (!tramo.isExisteRuta()) {
                continue;
            }

            visitados[i] = true;

            long resultado = explorarTodas(
                    floyd,
                    deposito,
                    siguiente,
                    centros,
                    visitados,
                    cantidadVisitada + 1,
                    tiempo + tramo.getTiempoTotal()
            );

            mejor = Math.min(mejor, resultado);
            visitados[i] = false;
        }

        return mejor;
    }

    // ocultar el detalle del recorrido durante las pruebas
    private static void ejecutarSinMostrar(Runnable accion) {

        java.io.PrintStream original = System.out;
        java.io.ByteArrayOutputStream buffer =
                new java.io.ByteArrayOutputStream();

        try {
            System.setOut(new java.io.PrintStream(buffer));
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
