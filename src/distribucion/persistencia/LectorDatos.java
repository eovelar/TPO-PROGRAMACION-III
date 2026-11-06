
package distribucion.persistencia;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LectorDatos {

    // cargar la red desde los archivos
    public static RedVial cargarRed(String archivoUbicaciones, String archivoConexiones)
            throws IOException {

        RedVial red = new RedVial();

        // leer las ubicaciones
        try (BufferedReader lector = Files.newBufferedReader(Path.of(archivoUbicaciones))) {

            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;

                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(";");

                if (datos.length != 3) {
                    throw new IOException("formato incorrecto en ubicaciones, linea " + numeroLinea);
                }

                try {
                    int id = Integer.parseInt(datos[0].trim());
                    String nombre = datos[1].trim();
                    TipoUbicacion tipo = TipoUbicacion.valueOf(datos[2].trim());

                    red.agregarUbicacion(new Ubicacion(id, nombre, tipo));

                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            "error en ubicaciones, linea " + numeroLinea + ": " + e.getMessage(), e
                    );
                }
            }
        }

        // leer las conexiones
        try (BufferedReader lector = Files.newBufferedReader(Path.of(archivoConexiones))) {

            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;

                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(";");

                if (datos.length != 3) {
                    throw new IOException("formato incorrecto en conexiones, linea " + numeroLinea);
                }

                try {
                    int idOrigen = Integer.parseInt(datos[0].trim());
                    int idDestino = Integer.parseInt(datos[1].trim());
                    int tiempo = Integer.parseInt(datos[2].trim());

                    Ubicacion origen = red.buscarUbicacionPorId(idOrigen);
                    Ubicacion destino = red.buscarUbicacionPorId(idDestino);

                    if (origen == null || destino == null) {
                        throw new IOException(
                                "ubicacion inexistente en conexiones, linea " + numeroLinea
                        );
                    }

                    red.agregarConexion(new ConexionVial(origen, destino, tiempo));

                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            "error en conexiones, linea " + numeroLinea + ": " + e.getMessage(), e
                    );
                }
            }
        }

        return red;
    }
}
