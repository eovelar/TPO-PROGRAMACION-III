package distribucion.rutas;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.Ubicacion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementa el algoritmo de Dijkstra para encontrar la ruta de menor tiempo
 * entre una ubicación de origen y una de destino en la red vial.
 *
 * Estrategia: Greedy (algoritmo voraz).
 * En cada paso se elige la ubicación no visitada con menor tiempo acumulado,
 * garantizando el camino óptimo dado que todos los tiempos son positivos.
 *
 * Complejidad temporal: O(V^2), donde V es la cantidad de ubicaciones.
 */
public class Dijkstra {

    private RedVial red;

    public Dijkstra(RedVial red) {
        this.red = red;
    }

    /**
     * Calcula la ruta de menor tiempo desde origen hasta destino.
     * Solo considera conexiones habilitadas.
     */
    public ResultadoRuta calcularRuta(Ubicacion origen, Ubicacion destino) {
        ResultadoRuta resultado = new ResultadoRuta();
        List<Ubicacion> ubicaciones = red.getUbicaciones();

        if (origen.getId() == destino.getId()) {
            resultado.setExisteRuta(true);
            List<Ubicacion> recorrido = new ArrayList<>();
            recorrido.add(origen);
            resultado.setRecorrido(recorrido);
            resultado.setTiempoTotal(0);
            resultado.agregarDecision("Origen igual a destino. Tiempo: 0 minutos.");
            return resultado;
        }

        Map<Integer, Integer> distancia = new HashMap<>();
        Map<Integer, Ubicacion> anterior = new HashMap<>();
        List<Ubicacion> noVisitados = new ArrayList<>(ubicaciones);

        for (Ubicacion u : ubicaciones) {
            distancia.put(u.getId(), Integer.MAX_VALUE);
            anterior.put(u.getId(), null);
        }
        distancia.put(origen.getId(), 0);
        resultado.agregarDecision("Inicio desde: " + origen.getNombre() + ". Distancia inicial: 0.");

        while (!noVisitados.isEmpty()) {
            // Paso greedy: elegir el nodo no visitado con menor distancia acumulada
            Ubicacion actual = null;
            int menorDistancia = Integer.MAX_VALUE;
            for (Ubicacion u : noVisitados) {
                int d = distancia.get(u.getId());
                if (d < menorDistancia) {
                    menorDistancia = d;
                    actual = u;
                }
            }

            if (actual == null || distancia.get(actual.getId()) == Integer.MAX_VALUE) {
                resultado.agregarDecision("No quedan nodos alcanzables. Se detiene la búsqueda.");
                break;
            }

            if (actual.getId() == destino.getId()) {
                resultado.agregarDecision("Destino alcanzado: " + destino.getNombre()
                        + " con tiempo acumulado: " + distancia.get(destino.getId()) + " min.");
                break;
            }

            resultado.agregarDecision("Procesando: " + actual.getNombre()
                    + " (tiempo acumulado: " + distancia.get(actual.getId()) + " min).");
            noVisitados.remove(actual);

            // Relajación: actualizar distancias a través de este nodo
            for (ConexionVial c : red.getConexionesDesde(actual)) {
                if (!c.isHabilitada()) continue;
                Ubicacion vecino = c.getDestino();
                if (!noVisitados.contains(vecino)) continue;

                int nuevaDist = distancia.get(actual.getId()) + c.getTiempoMinutos();
                if (nuevaDist < distancia.get(vecino.getId())) {
                    distancia.put(vecino.getId(), nuevaDist);
                    anterior.put(vecino.getId(), actual);
                    resultado.agregarDecision("  Actualizar " + vecino.getNombre()
                            + ": nueva distancia = " + nuevaDist + " min (vía " + actual.getNombre() + ").");
                }
            }
        }

        if (distancia.get(destino.getId()) == Integer.MAX_VALUE) {
            resultado.setExisteRuta(false);
            resultado.agregarDecision("El destino " + destino.getNombre()
                    + " no es alcanzable desde " + origen.getNombre() + ".");
        } else {
            resultado.setExisteRuta(true);
            resultado.setTiempoTotal(distancia.get(destino.getId()));
            resultado.setRecorrido(reconstruirRecorrido(anterior, destino));
        }

        return resultado;
    }

    private List<Ubicacion> reconstruirRecorrido(Map<Integer, Ubicacion> anterior, Ubicacion destino) {
        List<Ubicacion> recorrido = new ArrayList<>();
        Ubicacion actual = destino;
        while (actual != null) {
            recorrido.add(0, actual);
            actual = anterior.get(actual.getId());
        }
        return recorrido;
    }
}
