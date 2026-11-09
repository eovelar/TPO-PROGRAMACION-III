
package distribucion.algoritmos;

import distribucion.modelo.Ubicacion;
import distribucion.rutas.ResultadoRuta;

import java.util.ArrayList;
import java.util.List;

public class RecorridoMultiplesEntregas {

    private FloydWarshall floyd;
    private List<Ubicacion> mejorOrden;
    private long menorTiempo;

    public RecorridoMultiplesEntregas(FloydWarshall floyd) {
        this.floyd = floyd;
    }

    public void calcular(Ubicacion deposito, List<Ubicacion> centros) {

        if (deposito == null || centros == null) {
            throw new IllegalArgumentException("faltan datos del recorrido");
        }

        if (centros.isEmpty() || centros.size() > 8) {
            throw new IllegalArgumentException("debe haber entre 1 y 8 centros");
        }

        // verificar que no haya centros repetidos
        for (int i = 0; i < centros.size(); i++) {
            Ubicacion actual = centros.get(i);

            if (actual == null || actual.getId() == deposito.getId()) {
                throw new IllegalArgumentException("centro no valido");
            }

            for (int j = i + 1; j < centros.size(); j++) {
                if (centros.get(j) != null &&
                        actual.getId() == centros.get(j).getId()) {
                    throw new IllegalArgumentException("hay centros repetidos");
                }
            }
        }

        mejorOrden = new ArrayList<>();
        menorTiempo = Long.MAX_VALUE;

        boolean[] visitados = new boolean[centros.size()];
        List<Ubicacion> ordenActual = new ArrayList<>();

        // buscar el mejor orden de visita
        backtracking(deposito, deposito, centros,
                visitados, ordenActual, 0);

        if (menorTiempo == Long.MAX_VALUE) {
            System.out.println("no existe un recorrido completo disponible");
            return;
        }

        System.out.println("\n=== RECORRIDO CON MULTIPLES ENTREGAS ===");
        System.out.println("orden de entregas:");

        System.out.print(deposito.getNombre());

        for (Ubicacion centro : mejorOrden) {
            System.out.print(" -> " + centro.getNombre());
        }

        System.out.println(" -> " + deposito.getNombre());
        System.out.println("tiempo total: " + menorTiempo + " minutos");

        mostrarRecorridoCompleto(deposito);
    }

    // probar las distintas combinaciones
    private void backtracking(
            Ubicacion deposito,
            Ubicacion actual,
            List<Ubicacion> centros,
            boolean[] visitados,
            List<Ubicacion> ordenActual,
            long tiempoActual) {

        // todos los centros fueron visitados
        if (ordenActual.size() == centros.size()) {

            ResultadoRuta regreso = floyd.consultar(
                    actual.getId(), deposito.getId()
            );

            if (!regreso.isExisteRuta()) {
                return;
            }

            long tiempoTotal = tiempoActual + regreso.getTiempoTotal();

            if (tiempoTotal < menorTiempo) {
                menorTiempo = tiempoTotal;
                mejorOrden = new ArrayList<>(ordenActual);
            }

            return;
        }

        for (int i = 0; i < centros.size(); i++) {

            if (visitados[i]) {
                continue;
            }

            Ubicacion siguiente = centros.get(i);

            ResultadoRuta ruta = floyd.consultar(
                    actual.getId(), siguiente.getId()
            );

            if (!ruta.isExisteRuta()) {
                continue;
            }

            long nuevoTiempo = tiempoActual + ruta.getTiempoTotal();

            // descartar recorridos que ya son mas largos
            if (nuevoTiempo >= menorTiempo) {
                continue;
            }

            visitados[i] = true;
            ordenActual.add(siguiente);

            backtracking(deposito, siguiente, centros,
                    visitados, ordenActual, nuevoTiempo);

            // volver para probar otro camino
            ordenActual.remove(ordenActual.size() - 1);
            visitados[i] = false;
        }
    }

    // mostrar las ubicaciones atravesadas
    private void mostrarRecorridoCompleto(Ubicacion deposito) {

        List<Ubicacion> paradas = new ArrayList<>();
        paradas.add(deposito);
        paradas.addAll(mejorOrden);
        paradas.add(deposito);

        List<Ubicacion> recorrido = new ArrayList<>();

        for (int i = 0; i < paradas.size() - 1; i++) {

            ResultadoRuta tramo = floyd.consultar(
                    paradas.get(i).getId(),
                    paradas.get(i + 1).getId()
            );

            List<Ubicacion> ubicaciones = tramo.getRecorrido();

            for (int j = 0; j < ubicaciones.size(); j++) {

                // no repetir el inicio de cada tramo
                if (i > 0 && j == 0) {
                    continue;
                }

                recorrido.add(ubicaciones.get(j));
            }
        }

        System.out.println("\nrecorrido completo:");

        for (int i = 0; i < recorrido.size(); i++) {
            System.out.print(recorrido.get(i).getNombre());

            if (i < recorrido.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }
}
