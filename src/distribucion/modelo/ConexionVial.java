package distribucion.modelo;

/**
 * Representa una conexión vial dirigida entre dos ubicaciones.
 * Una conexión de A hacia B no implica conexión de B hacia A.
 */
public class ConexionVial {

    private Ubicacion origen;
    private Ubicacion destino;
    private int tiempoMinutos;
    private boolean habilitada;

    public ConexionVial(Ubicacion origen, Ubicacion destino, int tiempoMinutos) {
        if (tiempoMinutos <= 0) {
            throw new IllegalArgumentException("El tiempo de viaje debe ser positivo.");
        }
        this.origen = origen;
        this.destino = destino;
        this.tiempoMinutos = tiempoMinutos;
        this.habilitada = true;
    }

    public Ubicacion getOrigen() { return origen; }
    public Ubicacion getDestino() { return destino; }
    public int getTiempoMinutos() { return tiempoMinutos; }
    public boolean isHabilitada() { return habilitada; }
    public void deshabilitar() { this.habilitada = false; }
    public void habilitar() { this.habilitada = true; }

    @Override
    public String toString() {
        return origen.getNombre() + " -> " + destino.getNombre()
                + " (" + tiempoMinutos + " min)"
                + (habilitada ? "" : " [DESHABILITADA]");
    }
}
