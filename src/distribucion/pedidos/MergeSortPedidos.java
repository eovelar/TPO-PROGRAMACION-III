
package distribucion.pedidos;

import distribucion.modelo.Pedido;

public class MergeSortPedidos {

    // ordenar por fecha
    public static void ordenarPorFecha(Pedido[] pedidos) {
        ordenar(pedidos, 0);
    }

    // ordenar por prioridad
    public static void ordenarPorPrioridad(Pedido[] pedidos) {
        ordenar(pedidos, 1);
    }

    // ordenar por id
    public static void ordenarPorId(Pedido[] pedidos) {
        ordenar(pedidos, 2);
    }

    private static void ordenar(Pedido[] pedidos, int criterio) {
        if (pedidos == null || pedidos.length <= 1) {
            return;
        }

        mergeSort(pedidos, 0, pedidos.length - 1, criterio);
    }

    // dividir el arreglo
    private static void mergeSort(Pedido[] pedidos, int inicio, int fin, int criterio) {
        if (inicio < fin) {
            int medio = inicio + (fin - inicio) / 2;

            mergeSort(pedidos, inicio, medio, criterio);
            mergeSort(pedidos, medio + 1, fin, criterio);

            merge(pedidos, inicio, medio, fin, criterio);
        }
    }

    // comparar los pedidos
    private static int comparar(Pedido a, Pedido b, int criterio) {

        if (criterio == 2) {
            return Integer.compare(a.getId(), b.getId());
        }

        if (criterio == 1) {
            int resultado = Integer.compare(b.getPrioridad(), a.getPrioridad());

            if (resultado != 0) {
                return resultado;
            }
        }

        int resultadoFecha = a.getFechaLimite().compareTo(b.getFechaLimite());

        if (resultadoFecha != 0) {
            return resultadoFecha;
        }

        return Integer.compare(a.getId(), b.getId());
    }

    // unir las mitades
    private static void merge(Pedido[] pedidos, int inicio, int medio, int fin, int criterio) {

        int n1 = medio - inicio + 1;
        int n2 = fin - medio;

        Pedido[] izquierda = new Pedido[n1];
        Pedido[] derecha = new Pedido[n2];

        // copiar en arreglos auxiliares
        for (int i = 0; i < n1; i++) {
            izquierda[i] = pedidos[inicio + i];
        }

        for (int j = 0; j < n2; j++) {
            derecha[j] = pedidos[medio + 1 + j];
        }

        int i = 0;
        int j = 0;
        int k = inicio;

        // comparar y ordenar
        while (i < n1 && j < n2) {
            if (comparar(izquierda[i], derecha[j], criterio) <= 0) {
                pedidos[k] = izquierda[i];
                i++;
            } else {
                pedidos[k] = derecha[j];
                j++;
            }
            k++;
        }

        // copiar los restantes
        while (i < n1) {
            pedidos[k] = izquierda[i];
            i++;
            k++;
        }

        while (j < n2) {
            pedidos[k] = derecha[j];
            j++;
            k++;
        }
    }
}
