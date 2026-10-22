package distribucion.rutas;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.Ubicacion;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra la red vial: ubicaciones (depósitos y centros comunitarios)
 * y conexiones viales dirigidas entre ellas.
 */
public class RedVial {

    private List<Ubicacion> ubicaciones;
    private List<ConexionVial> conexiones;

    public RedVial() {
        this.ubicaciones = new ArrayList<>();
        this.conexiones = new ArrayList<>();
    }

    public void agregarUbicacion(Ubicacion ubicacion) {
        for (Ubicacion u : ubicaciones) {
            if (u.getId() == ubicacion.getId()) {
                throw new IllegalArgumentException("Ya existe una ubicación con ID " + ubicacion.getId());
            }
        }
        ubicaciones.add(ubicacion);
    }

    public Ubicacion buscarUbicacionPorId(int id) {
        for (Ubicacion u : ubicaciones) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public List<Ubicacion> getUbicaciones() { return ubicaciones; }

    public void agregarConexion(ConexionVial conexion) {
        conexiones.add(conexion);
    }

    public boolean deshabilitarConexion(int idOrigen, int idDestino) {
        for (ConexionVial c : conexiones) {
            if (c.getOrigen().getId() == idOrigen && c.getDestino().getId() == idDestino) {
                c.deshabilitar();
                return true;
            }
        }
        return false;
    }

    public boolean habilitarConexion(int idOrigen, int idDestino) {
        for (ConexionVial c : conexiones) {
            if (c.getOrigen().getId() == idOrigen && c.getDestino().getId() == idDestino) {
                c.habilitar();
                return true;
            }
        }
        return false;
    }

    public List<ConexionVial> getConexionesDesde(Ubicacion origen) {
        List<ConexionVial> resultado = new ArrayList<>();
        for (ConexionVial c : conexiones) {
            if (c.getOrigen().getId() == origen.getId()) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<ConexionVial> getConexiones() { return conexiones; }

    public void mostrarRed() {
        System.out.println("\n=== Red Vial ===");
        System.out.println("Ubicaciones:");
        for (Ubicacion u : ubicaciones) {
            System.out.println("  [" + u.getId() + "] " + u.getNombre() + " (" + u.getTipo() + ")");
        }
        System.out.println("Conexiones:");
        for (ConexionVial c : conexiones) {
            System.out.println("  " + c);
        }
    }
}
