
package distribucion.algoritmos;

import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Vehiculo;

public class SeleccionCargas {

    public static void seleccionar(LoteMercaderia[] lotes, Vehiculo vehiculo) {

        if (lotes == null || vehiculo == null) {
            throw new IllegalArgumentException(
                    "los lotes y el vehiculo no pueden ser nulos"
            );
        }

        int cantidad = lotes.length;
        int capacidad = vehiculo.getCapacidadMaxima();

        if (capacidad < 0) {
            throw new IllegalArgumentException(
                    "la capacidad no puede ser negativa"
            );
        }

        long sumaPesos = 0;

        // validar los lotes y calcular su peso total
        for (LoteMercaderia lote : lotes) {

            if (lote == null) {
                throw new IllegalArgumentException(
                        "no puede haber lotes nulos"
                );
            }

            if (lote.getPeso() <= 0 || lote.getPrioridad() < 0) {
                throw new IllegalArgumentException(
                        "peso o prioridad no validos"
                );
            }

            sumaPesos += lote.getPeso();
        }

        // resolver los casos sin carga posible
        if (cantidad == 0 || capacidad == 0) {
            System.out.println("lotes seleccionados: ninguno");
            System.out.println("peso total: 0 kg");
            System.out.println("prioridad total: 0");
            System.out.println(
                    "capacidad restante: " + capacidad + " kg"
            );
            return;
        }

        // no necesitamos mas capacidad que el peso total
        int capacidadEfectiva = (int) Math.min(
                (long) capacidad, sumaPesos
        );

        int[][] dp = new int[cantidad + 1][capacidadEfectiva + 1];

        // calcular la prioridad maxima para cada capacidad
        for (int i = 1; i <= cantidad; i++) {

            int peso = lotes[i - 1].getPeso();
            int prioridad = lotes[i - 1].getPrioridad();

            for (int j = 0; j <= capacidadEfectiva; j++) {

                dp[i][j] = dp[i - 1][j];

                if (peso <= j) {

                    long incluir = (long) prioridad
                            + dp[i - 1][j - peso];

                    if (incluir > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException(
                                "la prioridad acumulada supera el limite permitido"
                        );
                    }

                    dp[i][j] = Math.max(
                            dp[i - 1][j],
                            (int) incluir
                    );
                }
            }
        }

        int capacidadRestante = capacidadEfectiva;
        long pesoTotal = 0;
        boolean haySeleccionados = false;

        System.out.println("lotes seleccionados:");

        // reconstruir la solucion desde la ultima fila
        for (int i = cantidad; i > 0; i--) {

            if (dp[i][capacidadRestante]
                    != dp[i - 1][capacidadRestante]) {

                LoteMercaderia lote = lotes[i - 1];

                System.out.println("lote " + lote.getId());

                pesoTotal += lote.getPeso();
                capacidadRestante -= lote.getPeso();

                haySeleccionados = true;
            }
        }

        if (!haySeleccionados) {
            System.out.println("ninguno");
        }

        System.out.println("peso total: " + pesoTotal + " kg");

        System.out.println(
                "prioridad total: " + dp[cantidad][capacidadEfectiva]
        );

        System.out.println(
                "capacidad restante: " + (capacidad - pesoTotal) + " kg"
        );
    }
}
