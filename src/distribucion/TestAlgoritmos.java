
package distribucion;

import distribucion.cargas.SeleccionCargas;
import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Pedido;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.modelo.Vehiculo;
import distribucion.pedidos.BusquedaBinariaPedidos;
import distribucion.pedidos.MergeSortPedidos;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

public class TestAlgoritmos {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        Ubicacion centro = new Ubicacion(
                1, "Centro Norte", TipoUbicacion.CENTRO_COMUNITARIO
        );

        Pedido[] pedidos = {
                new Pedido(103, centro, LocalDate.of(2026, 10, 20), 5, 25),
                new Pedido(104, centro, LocalDate.of(2026, 10, 17), 3, 30),
                new Pedido(102, centro, LocalDate.of(2026, 10, 18), 5, 15),
                new Pedido(101, centro, LocalDate.of(2026, 10, 18), 5, 20)
        };

        // probar ordenamientos
        MergeSortPedidos.ordenarPorFecha(pedidos);
        verificar("orden por fecha", idsCorrectos(pedidos, 104, 101, 102, 103));

        MergeSortPedidos.ordenarPorPrioridad(pedidos);
        verificar("orden por prioridad", idsCorrectos(pedidos, 101, 102, 103, 104));

        MergeSortPedidos.ordenarPorId(pedidos);
        verificar("orden por id", idsCorrectos(pedidos, 101, 102, 103, 104));

        // probar busqueda binaria
        Pedido encontrado = BusquedaBinariaPedidos.buscarPorId(pedidos, 102);

        verificar("buscar pedido existente",
                encontrado != null && encontrado.getId() == 102);

        verificar("buscar pedido inexistente",
                BusquedaBinariaPedidos.buscarPorId(pedidos, 999) == null);

        // probar cargas
        Vehiculo vehiculo = new Vehiculo(1, 50);

        LoteMercaderia[] lotes = {
                new LoteMercaderia(1, 10, 30),
                new LoteMercaderia(2, 20, 40),
                new LoteMercaderia(3, 30, 50),
                new LoteMercaderia(4, 40, 60)
        };

        String resultado = ejecutarCargas(lotes, vehiculo);

        verificar("prioridad maxima",
                resultado.contains("prioridad total: 90"));

        verificar("peso total",
                resultado.contains("peso total: 50 kg"));

        verificar("capacidad restante",
                resultado.contains("capacidad restante: 0 kg"));

        // probar arreglo vacio
        String vacio = ejecutarCargas(new LoteMercaderia[0], vehiculo);

        verificar("cargas vacias",
                vacio.contains("prioridad total: 0")
                        && vacio.contains("capacidad restante: 50 kg"));

        // probar ubicacion con nombre vacio
        try {
            new Ubicacion(2, "", TipoUbicacion.CENTRO_COMUNITARIO);
            verificar("nombre de ubicacion vacio", false);
        } catch (IllegalArgumentException e) {
            verificar("nombre de ubicacion vacio", true);
        }

        System.out.println("\npruebas correctas: " + correctas);
        System.out.println("pruebas fallidas: " + fallidas);

        if (fallidas > 0) {
            throw new AssertionError("hay pruebas fallidas");
        }
    }

    private static boolean idsCorrectos(Pedido[] pedidos, int... ids) {

        if (pedidos.length != ids.length) {
            return false;
        }

        for (int i = 0; i < ids.length; i++) {
            if (pedidos[i].getId() != ids[i]) {
                return false;
            }
        }

        return true;
    }

    // ejecutar cargas y guardar la salida
    private static String ejecutarCargas(LoteMercaderia[] lotes, Vehiculo vehiculo) {

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(salida));
            SeleccionCargas.seleccionar(lotes, vehiculo);
        } finally {
            System.setOut(original);
        }

        return salida.toString();
    }

    private static void verificar(String nombre, boolean resultado) {

        if (resultado) {
            System.out.println("ok - " + nombre);
            correctas++;
        } else {
            System.out.println("error - " + nombre);
            fallidas++;
        }
    }
}
