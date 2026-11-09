
package distribucion.algoritmos;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dijkstra {

    private final RedVial red;

    public Dijkstra(RedVial red) {
        if (red == null) {
            throw new IllegalArgumentException("la red vial no puede ser nula");
        }

        this.red = red;
    }

    public ResultadoRuta calcularRuta(Ubicacion origen, Ubicacion destino) {

        if (origen == null || destino == null) {
            throw new IllegalArgumentException(
                    "el origen y el destino no pueden ser nulos"
            );
        }

        Ubicacion origenRed = red.buscarUbicacionPorId(origen.getId());
        Ubicacion destinoRed = red.buscarUbicacionPorId(destino.getId());

        if (origenRed == null || destinoRed == null) {
            throw new IllegalArgumentException(
                    "el origen o destino no pertenece a la red"
            );
        }

        origen = origenRed;
        destino = destinoRed;

        ResultadoRuta resultado = new ResultadoRuta();
        List<Ubicacion> ubicaciones = red.getUbicaciones();

        if (origen.getId() == destino.getId()) {
            resultado.setExisteRuta(true);

            List<Ubicacion> recorrido = new ArrayList<>();
            recorrido.add(origen);

            resultado.setRecorrido(recorrido);
            resultado.setTiempoTotal(0);
            resultado.agregarDecision(
                    "Origen igual a destino. Tiempo: 0 minutos."
            );

            return resultado;
        }

        Map<Integer, Long> distancia = new HashMap<>();
        Map<Integer, Ubicacion> anterior = new HashMap<>();
        List<Ubicacion> noVisitados = new ArrayList<>(ubicaciones);

        for (Ubicacion u : ubicaciones) {
            distancia.put(u.getId(), Long.MAX_VALUE);
            anterior.put(u.getId(), null);
        }

        distancia.put(origen.getId(), 0L);

        resultado.agregarDecision(
                "Inicio desde: " + origen.getNombre()
                        + ". Distancia inicial: 0."
        );

        while (!noVisitados.isEmpty()) {

            // elegir el nodo con menor tiempo acumulado
            Ubicacion actual = null;
            long menorDistancia = Long.MAX_VALUE;

            for (Ubicacion u : noVisitados) {
                long d = distancia.get(u.getId());

                if (d < menorDistancia) {
                    menorDistancia = d;
                    actual = u;
                }
            }

            if (actual == null) {
                resultado.agregarDecision(
                        "No quedan nodos alcanzables."
                );
                break;
            }

            if (actual.getId() == destino.getId()) {
                resultado.agregarDecision(
                        "Destino alcanzado: " + destino.getNombre()
                                + " con tiempo acumulado: "
                                + menorDistancia + " min."
                );
                break;
            }

            resultado.agregarDecision(
                    "Procesando: " + actual.getNombre()
                            + " (tiempo acumulado: "
                            + menorDistancia + " min)."
            );

            noVisitados.remove(actual);

            // actualizar las distancias de los vecinos
            for (ConexionVial conexion : red.getConexionesDesde(actual)) {

                if (!conexion.isHabilitada()) {
                    continue;
                }

                Ubicacion vecino = conexion.getDestino();

                if (!noVisitados.contains(vecino)) {
                    continue;
                }

                long nuevaDistancia =
                        menorDistancia + conexion.getTiempoMinutos();

                if (nuevaDistancia < distancia.get(vecino.getId())) {
                    distancia.put(vecino.getId(), nuevaDistancia);
                    anterior.put(vecino.getId(), actual);

                    resultado.agregarDecision(
                            "Actualizar " + vecino.getNombre()
                                    + ": nueva distancia = "
                                    + nuevaDistancia
                                    + " min (via " + actual.getNombre() + ")."
                    );
                }
            }
        }

        long tiempoFinal = distancia.get(destino.getId());

        if (tiempoFinal == Long.MAX_VALUE) {
            resultado.setExisteRuta(false);

            resultado.agregarDecision(
                    "El destino " + destino.getNombre()
                            + " no es alcanzable desde "
                            + origen.getNombre() + "."
            );

        } else {

            if (tiempoFinal > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "el tiempo total supera el limite permitido"
                );
            }

            resultado.setExisteRuta(true);
            resultado.setTiempoTotal((int) tiempoFinal);
            resultado.setRecorrido(
                    reconstruirRecorrido(anterior, destino)
            );
        }

        return resultado;
    }

    // reconstruir el camino desde el destino
    private List<Ubicacion> reconstruirRecorrido(
            Map<Integer, Ubicacion> anterior,
            Ubicacion destino) {

        List<Ubicacion> recorrido = new ArrayList<>();
        Ubicacion actual = destino;

        while (actual != null) {
            recorrido.add(0, actual);
            actual = anterior.get(actual.getId());
        }

        return recorrido;
    }
}
