package distribucion.pedidos;

import distribucion.modelo.Pedido;
import java.util.ArrayList;

public class GestorPedidos {

    private ArrayList<Pedido> pedidos;

    public GestorPedidos() {
        pedidos = new ArrayList<>();
    }

    // agregar un pedido
    public void agregarPedido(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException("el pedido no puede ser nulo");
        }

        if (buscarPedidoPorId(pedido.getId()) != null) {
            throw new IllegalArgumentException("ya existe un pedido con ese id");
        }

        pedidos.add(pedido);
    }
    // ordenar los pedidos por fecha
    public Pedido[] obtenerPedidosPorFecha() {

        Pedido[] arreglo = pedidos.toArray(new Pedido[0]);

        MergeSortPedidos.ordenarPorFecha(arreglo);

        return arreglo;
    }

    // ordenar los pedidos por prioridad
    public Pedido[] obtenerPedidosPorPrioridad() {

        Pedido[] arreglo = pedidos.toArray(new Pedido[0]);

        MergeSortPedidos.ordenarPorPrioridad(arreglo);

        return arreglo;
    }

    // buscar un pedido por id
    public Pedido buscarPedidoPorId(int id) {

        Pedido[] arreglo = pedidos.toArray(new Pedido[0]);

        MergeSortPedidos.ordenarPorId(arreglo);

        return BusquedaBinariaPedidos.buscarPorId(arreglo, id);
    }


}