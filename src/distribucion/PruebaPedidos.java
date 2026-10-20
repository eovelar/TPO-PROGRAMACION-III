
package distribucion;

import distribucion.modelo.Pedido;
import distribucion.modelo.Ubicacion;
import distribucion.modelo.TipoUbicacion;
import distribucion.pedidos.GestorPedidos;

import java.time.LocalDate;

public class PruebaPedidos {

    public static void main(String[] args) {
        // probar con la lista vacía
        GestorPedidos gestorVacio = new GestorPedidos();

        System.out.println("pedidos vacios: "
                + gestorVacio.obtenerPedidosPorFecha().length);

        System.out.println("busqueda sin pedidos: "
                + gestorVacio.buscarPedidoPorId(100));
        Ubicacion centro = new Ubicacion(
                1, "Centro Norte", TipoUbicacion.CENTRO_COMUNITARIO
        );

        GestorPedidos gestor = new GestorPedidos();

        gestor.agregarPedido(new Pedido(103, centro, LocalDate.of(2026, 10, 20), 5, 25));
        gestor.agregarPedido(new Pedido(104, centro, LocalDate.of(2026, 10, 17), 3, 30));
        gestor.agregarPedido(new Pedido(102, centro, LocalDate.of(2026, 10, 18), 5, 15));
        gestor.agregarPedido(new Pedido(101, centro, LocalDate.of(2026, 10, 18), 5, 20));

        // ordenar por fecha
        System.out.println("pedidos por fecha:");

        for (Pedido pedido : gestor.obtenerPedidosPorFecha()) {
            System.out.println(pedido.getId() + " - " + pedido.getFechaLimite());
        }

        // ordenar por prioridad
        System.out.println("\npedidos por prioridad:");

        for (Pedido pedido : gestor.obtenerPedidosPorPrioridad()) {
            System.out.println(pedido.getId() + " - prioridad: " + pedido.getPrioridad());
        }

        // buscar por id
        Pedido encontrado = gestor.buscarPedidoPorId(102);

        if (encontrado != null) {
            System.out.println("\npedido encontrado: " + encontrado.getId());
        } else {
            System.out.println("\npedido no encontrado");
        }

        Pedido inexistente = gestor.buscarPedidoPorId(999);

        if (inexistente == null) {
            System.out.println("pedido 999 no encontrado");
        }
    }
}
