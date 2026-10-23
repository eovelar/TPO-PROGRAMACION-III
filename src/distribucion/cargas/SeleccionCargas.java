package distribucion.cargas;

import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Vehiculo;

public class SeleccionCargas {
    public static void seleccionar(LoteMercaderia[] lotes, Vehiculo vehiculo) {

        int cantidad = lotes.length;
        int capacidad = vehiculo.getCapacidadMaxima();

        int[][] dp = new int[cantidad + 1][capacidad + 1];
        // calcular la mejor prioridad para cada capacidad
        for (int i = 1; i <= cantidad; i++) {

            int peso = lotes[i - 1].getPeso();
            int prioridad = lotes[i - 1].getPrioridad();

            for (int j = 0; j <= capacidad; j++) {

                if (peso <= j) {
                    dp[i][j] = Math.max(
                            dp[i - 1][j],
                            prioridad + dp[i - 1][j - peso]
                    );
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        int capacidadRestante = capacidad;
        int pesoTotal = 0;

        System.out.println("lotes seleccionados:");

// recorrer la matriz desde el final
        for (int i = cantidad; i > 0; i--) {

            if (dp[i][capacidadRestante] != dp[i - 1][capacidadRestante]) {

                LoteMercaderia lote = lotes[i - 1];

                System.out.println("lote " + lote.getId());

                pesoTotal += lote.getPeso();
                capacidadRestante -= lote.getPeso();
            }
        }

        System.out.println("peso total: " + pesoTotal + " kg");
        System.out.println("prioridad total: " + dp[cantidad][capacidad]);
        System.out.println("capacidad restante: " + (capacidad - pesoTotal) + " kg");
    }

}