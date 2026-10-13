
package distribucion;

import distribucion.modelo.Pedido;
import distribucion.modelo.Ubicacion;
import distribucion.modelo.TipoUbicacion;
import distribucion.pedidos.MergeSortPedidos;
import distribucion.pedidos.BusquedaBinariaPedidos;

import java.time.LocalDate;

public class PruebaPedidos {

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

        // ordenar por id antes de buscar
        MergeSortPedidos.ordenarPorId(pedidos);

        // buscar un pedido existente
        Pedido encontrado = BusquedaBinariaPedidos.buscarPorId(pedidos, 102);

        if (encontrado != null) {
            System.out.println("pedido encontrado: " + encontrado.getId());
        } else {
            System.out.println("pedido no encontrado");
        }

        // buscar un pedido inexistente
        Pedido inexistente = BusquedaBinariaPedidos.buscarPorId(pedidos, 999);

        if (inexistente == null) {
            System.out.println("pedido 999 no encontrado");
        }
    }
}
