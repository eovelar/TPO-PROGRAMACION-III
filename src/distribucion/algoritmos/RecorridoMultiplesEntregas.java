
package distribucion.algoritmos;

import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.ResultadoRuta;

import java.util.ArrayList;
import java.util.List;

public class RecorridoMultiplesEntregas {

    private final FloydWarshall floyd;
    private List<Ubicacion> mejorOrden;
    private long menorTiempo;
    private boolean existeRecorrido;

    public RecorridoMultiplesEntregas(FloydWarshall floyd) {

        if (floyd == null) {
            throw new IllegalArgumentException(
                    "falta el calculo de rutas"
            );
        }

        this.floyd = floyd;
        this.mejorOrden = new ArrayList<>();
        this.menorTiempo = Long.MAX_VALUE;
        this.existeRecorrido = false;
    }

    public void calcular(Ubicacion deposito, List<Ubicacion> centros) {

        mejorOrden = new ArrayList<>();
        menorTiempo = Long.MAX_VALUE;
        existeRecorrido = false;

        if (deposito == null || centros == null) {
            throw new IllegalArgumentException(
                    "faltan datos del recorrido"
            );
        }

        if (deposito.getTipo() != TipoUbicacion.DEPOSITO) {
            throw new IllegalArgumentException(
                    "la ubicacion inicial debe ser un deposito"
            );
        }

        if (centros.isEmpty() || centros.size() > 8) {
            throw new IllegalArgumentException(
                    "debe haber entre 1 y 8 centros"
            );
        }

        // validar los centros seleccionados
        for (int i = 0; i < centros.size(); i++) {

            Ubicacion actual = centros.get(i);

            if (actual == null ||
                    actual.getTipo() != TipoUbicacion.CENTRO_COMUNITARIO) {

                throw new IllegalArgumentException(
                        "centro no valido"
                );
            }

            if (actual.getId() == deposito.getId()) {
                throw new IllegalArgumentException(
                        "el deposito no puede ser una entrega"
                );
            }

            for (int j = i + 1; j < centros.size(); j++) {

                Ubicacion otro = centros.get(j);

                if (otro != null &&
                        actual.getId() == otro.getId()) {

                    throw new IllegalArgumentException(
                            "hay centros repetidos"
                    );
                }
            }
        }

        boolean[] visitados = new boolean[centros.size()];
        List<Ubicacion> ordenActual = new ArrayList<>();

        // buscar el orden con menor tiempo total
        backtracking(
                deposito,
                deposito,
                centros,
                visitados,
                ordenActual,
                0
        );

        if (!existeRecorrido) {
            System.out.println(
                    "no existe un recorrido completo disponible"
            );
            return;
        }

        System.out.println(
                "\n=== RECORRIDO CON MULTIPLES ENTREGAS ==="
        );

        System.out.println("orden de entregas:");
        System.out.print(deposito.getNombre());

        for (Ubicacion centro : mejorOrden) {
            System.out.print(" -> " + centro.getNombre());
        }

        System.out.println(" -> " + deposito.getNombre());

        System.out.println(
                "tiempo total: " + menorTiempo + " minutos"
        );

        mostrarRecorridoCompleto(deposito);
    }

    private void backtracking(
            Ubicacion deposito,
            Ubicacion actual,
            List<Ubicacion> centros,
            boolean[] visitados,
            List<Ubicacion> ordenActual,
            long tiempoActual) {

        // comprobar el regreso al deposito
        if (ordenActual.size() == centros.size()) {

            ResultadoRuta regreso = floyd.consultar(
                    actual.getId(),
                    deposito.getId()
            );

            if (!regreso.isExisteRuta()) {
                return;
            }

            long tiempoTotal =
                    tiempoActual + regreso.getTiempoTotal();

            if (tiempoTotal < menorTiempo) {
                menorTiempo = tiempoTotal;
                mejorOrden = new ArrayList<>(ordenActual);
                existeRecorrido = true;
            }

            return;
        }

        for (int i = 0; i < centros.size(); i++) {

            if (visitados[i]) {
                continue;
            }

            Ubicacion siguiente = centros.get(i);

            ResultadoRuta ruta = floyd.consultar(
                    actual.getId(),
                    siguiente.getId()
            );

            if (!ruta.isExisteRuta()) {
                continue;
            }

            long nuevoTiempo =
                    tiempoActual + ruta.getTiempoTotal();

            // descartar recorridos que no mejoran el resultado
            if (nuevoTiempo >= menorTiempo) {
                continue;
            }

            visitados[i] = true;
            ordenActual.add(siguiente);

            backtracking(
                    deposito,
                    siguiente,
                    centros,
                    visitados,
                    ordenActual,
                    nuevoTiempo
            );

            // retroceder y probar otro orden
            ordenActual.remove(ordenActual.size() - 1);
            visitados[i] = false;
        }
    }

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

                if (i > 0 && j == 0) {
                    continue;
                }

                recorrido.add(ubicaciones.get(j));
            }
        }

        System.out.println("\nrecorrido completo:");

        for (int i = 0; i < recorrido.size(); i++) {

            System.out.print(
                    recorrido.get(i).getNombre()
            );

            if (i < recorrido.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }

    public boolean isExisteRecorrido() {
        return existeRecorrido;
    }

    public long getMenorTiempo() {
        return menorTiempo;
    }

    public List<Ubicacion> getMejorOrden() {
        return new ArrayList<>(mejorOrden);
    }
}
