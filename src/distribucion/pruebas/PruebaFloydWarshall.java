
package distribucion.pruebas;

import distribucion.persistencia.LectorDatos;
import distribucion.algoritmos.FloydWarshall;
import distribucion.rutas.RedVial;

import java.io.IOException;

public class PruebaFloydWarshall {

    public static void main(String[] args) {

        try {
            // cargar la red desde los archivos
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            FloydWarshall floyd = new FloydWarshall(red);

            // calcular las rutas una sola vez
            floyd.calcular();

            System.out.println("=== ruta de deposito a centro este ===");
            floyd.consultar(1, 4).mostrar();

            // consultar sin volver a calcular
            System.out.println("\n=== ruta de deposito a centro sur ===");
            floyd.consultar(1, 3).mostrar();

            // destino inaccesible
            System.out.println("\n=== destino inaccesible ===");
            floyd.consultar(4, 1).mostrar();

            // deshabilitar una conexion y recalcular
            red.deshabilitarConexion(2, 4);
            floyd.calcular();

            System.out.println("\n=== ruta con conexion deshabilitada ===");
            floyd.consultar(1, 4).mostrar();

        } catch (IOException e) {
            System.out.println("error al cargar los archivos: " + e.getMessage());
        }
    }
}
