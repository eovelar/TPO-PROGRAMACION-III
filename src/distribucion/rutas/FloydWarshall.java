
package distribucion.rutas;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.Ubicacion;

import java.util.ArrayList;
import java.util.List;

public class FloydWarshall {

    private static final int INFINITO = Integer.MAX_VALUE;

    private RedVial red;
    private List<Ubicacion> ubicaciones;
    private int[][] distancias;
    private int[][] siguientes;

    public FloydWarshall(RedVial red) {
        this.red = red;
    }

    // calcular los caminos minimos
    public void calcular() {

        ubicaciones = new ArrayList<>(red.getUbicaciones());
        int n = ubicaciones.size();

        distancias = new int[n][n];
        siguientes = new int[n][n];

        // inicializar las matrices
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = (i == j) ? 0 : INFINITO;
                siguientes[i][j] = (i == j) ? i : -1;
            }
        }

        // cargar las conexiones habilitadas
        for (ConexionVial conexion : red.getConexiones()) {

            if (!conexion.isHabilitada()) {
                continue;
            }

            int origen = buscarIndice(conexion.getOrigen().getId());
            int destino = buscarIndice(conexion.getDestino().getId());

            if (origen == -1 || destino == -1) {
                continue;
            }

            if (conexion.getTiempoMinutos() < distancias[origen][destino]) {
                distancias[origen][destino] = conexion.getTiempoMinutos();
                siguientes[origen][destino] = destino;
            }
        }

        // probar cada ubicacion como intermedia
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (distancias[i][k] == INFINITO ||
                            distancias[k][j] == INFINITO) {
                        continue;
                    }

                    long nuevoTiempo = (long) distancias[i][k]
                            + distancias[k][j];

                    if (nuevoTiempo < distancias[i][j]) {
                        distancias[i][j] = (int) nuevoTiempo;
                        siguientes[i][j] = siguientes[i][k];
                    }
                }
            }
        }
    }

    // buscar la posicion de una ubicacion
    private int buscarIndice(int id) {

        for (int i = 0; i < ubicaciones.size(); i++) {
            if (ubicaciones.get(i).getId() == id) {
                return i;
            }
        }

        return -1;
    }

    // consultar el camino entre dos ubicaciones
    public ResultadoRuta consultar(int idOrigen, int idDestino) {

        if (distancias == null) {
            throw new IllegalStateException("primero se deben calcular las rutas");
        }

        ResultadoRuta resultado = new ResultadoRuta();

        int origen = buscarIndice(idOrigen);
        int destino = buscarIndice(idDestino);

        if (origen == -1 || destino == -1 ||
                siguientes[origen][destino] == -1) {
            resultado.agregarDecision("no existe una ruta disponible");
            return resultado;
        }

        List<Ubicacion> recorrido = new ArrayList<>();
        int actual = origen;

        recorrido.add(ubicaciones.get(actual));

        // reconstruir el recorrido
        while (actual != destino) {
            actual = siguientes[actual][destino];
            recorrido.add(ubicaciones.get(actual));
        }

        resultado.setExisteRuta(true);
        resultado.setRecorrido(recorrido);
        resultado.setTiempoTotal(distancias[origen][destino]);
        resultado.agregarDecision("ruta obtenida de las matrices calculadas");

        return resultado;
    }
}
