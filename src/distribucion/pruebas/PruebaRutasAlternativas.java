
package distribucion.pruebas;

import distribucion.algoritmos.RutasAlternativas;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PruebaRutasAlternativas {

    public static void main(String[] args) {

        try {
            // cargar la red vial desde los archivos txt
            RedVial red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            RutasAlternativas rutas = new RutasAlternativas(red);

            // prueba 1: buscar caminos con un limite de 30 minutos
            System.out.println("=== PRUEBA 1: SIN UBICACIONES PROHIBIDAS ===");

            rutas.buscar(1, 4, 30, new HashSet<>());

            // prueba 2: prohibir el paso por centro norte
            System.out.println("\n=== PRUEBA 2: CENTRO NORTE PROHIBIDO ===");

            Set<Integer> prohibidas = new HashSet<>();
            prohibidas.add(2);

            rutas.buscar(1, 4, 30, prohibidas);

            // prueba 3: buscar con un tiempo insuficiente
            System.out.println("\n=== PRUEBA 3: TIEMPO MAXIMO DE 10 MINUTOS ===");

            rutas.buscar(1, 4, 10, new HashSet<>());

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
