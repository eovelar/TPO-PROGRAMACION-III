
package distribucion.pedidos;

import distribucion.modelo.Pedido;

public class BusquedaBinariaPedidos {

    // buscar un pedido por id
    public static Pedido buscarPorId(Pedido[] pedidos, int id) {

        if (pedidos == null) {
            return null;
        }

        int inicio = 0;
        int fin = pedidos.length - 1;

        while (inicio <= fin) {

            int medio = inicio + (fin - inicio) / 2;

            if (pedidos[medio].getId() == id) {
                return pedidos[medio];
            }

            if (pedidos[medio].getId() < id) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return null;
    }
}
