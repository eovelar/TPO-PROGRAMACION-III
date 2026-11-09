
package distribucion.algoritmos;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RutasAlternativas {

    private final RedVial red;
    private final List<List<Ubicacion>> caminos;
    private final List<Integer> tiempos;

    public RutasAlternativas(RedVial red) {
        this.red = red;
        this.caminos = new ArrayList<>();
        this.tiempos = new ArrayList<>();
    }

    public void buscar(int idOrigen, int idDestino,
                       int tiempoMaximo,
                       Set<Integer> ubicacionesProhibidas) {

        // limpiar los resultados anteriores
        caminos.clear();
        tiempos.clear();

        if (tiempoMaximo < 0) {
            throw new IllegalArgumentException(
                    "el tiempo maximo no puede ser negativo"
            );
        }

        Ubicacion origen = red.buscarUbicacionPorId(idOrigen);
        Ubicacion destino = red.buscarUbicacionPorId(idDestino);

        if (origen == null || destino == null) {
            throw new IllegalArgumentException(
                    "el origen o destino no existe"
            );
        }

        Set<Integer> prohibidas = new HashSet<>();

        if (ubicacionesProhibidas != null) {
            prohibidas.addAll(ubicacionesProhibidas);
        }

        // comprobar que el origen y destino esten permitidos
        if (prohibidas.contains(idOrigen) ||
                prohibidas.contains(idDestino)) {

            System.out.println("el origen o destino esta prohibido");
            return;
        }

        Set<Integer> visitadas = new HashSet<>();
        List<Ubicacion> caminoActual = new ArrayList<>();

        visitadas.add(idOrigen);
        caminoActual.add(origen);

        // empezar a buscar todos los caminos posibles
        backtracking(
                origen,
                idDestino,
                tiempoMaximo,
                0,
                prohibidas,
                visitadas,
                caminoActual
        );

        mostrarResultados();
    }

    private void backtracking(Ubicacion actual,
                              int idDestino,
                              int tiempoMaximo,
                              int tiempoActual,
                              Set<Integer> prohibidas,
                              Set<Integer> visitadas,
                              List<Ubicacion> caminoActual) {

        // guardar el camino si llegamos al destino
        if (actual.getId() == idDestino) {
            caminos.add(new ArrayList<>(caminoActual));
            tiempos.add(tiempoActual);
            return;
        }

        // recorrer las conexiones de la ubicacion actual
        for (ConexionVial conexion : red.getConexionesDesde(actual)) {

            if (!conexion.isHabilitada()) {
                continue;
            }

            Ubicacion siguiente = conexion.getDestino();
            int idSiguiente = siguiente.getId();

            // evitar ubicaciones prohibidas o repetidas
            if (prohibidas.contains(idSiguiente) ||
                    visitadas.contains(idSiguiente)) {
                continue;
            }

            long nuevoTiempo =
                    (long) tiempoActual + conexion.getTiempoMinutos();

            // descartar caminos que superen el limite
            if (nuevoTiempo > tiempoMaximo) {
                continue;
            }

            visitadas.add(idSiguiente);
            caminoActual.add(siguiente);

            backtracking(
                    siguiente,
                    idDestino,
                    tiempoMaximo,
                    (int) nuevoTiempo,
                    prohibidas,
                    visitadas,
                    caminoActual
            );

            // retroceder para explorar otra alternativa
            caminoActual.remove(caminoActual.size() - 1);
            visitadas.remove(idSiguiente);
        }
    }

    private void mostrarResultados() {

        System.out.println(
                "\n=== RUTAS ALTERNATIVAS CON RESTRICCIONES ==="
        );

        if (caminos.isEmpty()) {
            System.out.println("no se encontraron caminos validos");
            return;
        }

        System.out.println("caminos encontrados: " + caminos.size());

        for (int i = 0; i < caminos.size(); i++) {

            System.out.println("\ncamino " + (i + 1) + ":");

            List<Ubicacion> camino = caminos.get(i);

            for (int j = 0; j < camino.size(); j++) {

                System.out.print(camino.get(j).getNombre());

                if (j < camino.size() - 1) {
                    System.out.print(" -> ");
                }
            }

            // separar el recorrido del tiempo total
            System.out.println();

            System.out.println(
                    "tiempo total: " + tiempos.get(i) + " minutos"
            );
        }
    }

    public List<List<Ubicacion>> getCaminos() {

        List<List<Ubicacion>> copia = new ArrayList<>();

        for (List<Ubicacion> camino : caminos) {
            copia.add(new ArrayList<>(camino));
        }

        return copia;
    }

    public List<Integer> getTiempos() {
        return new ArrayList<>(tiempos);
    }
}
