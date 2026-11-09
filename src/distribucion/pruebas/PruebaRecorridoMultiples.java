
package distribucion.pruebas;

import distribucion.modelo.Ubicacion;
import distribucion.persistencia.LectorDatos;
import distribucion.algoritmos.FloydWarshall;
import distribucion.algoritmos.RecorridoMultiplesEntregas;
import distribucion.rutas.RedVial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PruebaRecorridoMultiples {

    public static void main(String[] args) {

        try {
            // cargar la red desde los txt
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            FloydWarshall floyd = new FloydWarshall(red);
            floyd.calcular();

            Ubicacion deposito = red.buscarUbicacionPorId(1);

            List<Ubicacion> centros = new ArrayList<>();
            centros.add(red.buscarUbicacionPorId(2));
            centros.add(red.buscarUbicacionPorId(3));
            centros.add(red.buscarUbicacionPorId(4));

            RecorridoMultiplesEntregas recorrido =
                    new RecorridoMultiplesEntregas(floyd);

            recorrido.calcular(deposito, centros);

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
