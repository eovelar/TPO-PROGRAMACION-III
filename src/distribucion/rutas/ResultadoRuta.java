package distribucion.rutas;

import distribucion.modelo.Ubicacion;
import java.util.ArrayList;
import java.util.List;

/**
 * Almacena el resultado de un cálculo de ruta más rápida.
 * Guarda si existe ruta, las ubicaciones del recorrido,
 * el tiempo total y el registro de decisiones del cálculo.
 */
public class ResultadoRuta {

    private boolean existeRuta;
    private List<Ubicacion> recorrido;
    private int tiempoTotal;
    private List<String> decisiones;

    public ResultadoRuta() {
        this.existeRuta = false;
        this.recorrido = new ArrayList<>();
        this.tiempoTotal = 0;
        this.decisiones = new ArrayList<>();
    }

    public boolean isExisteRuta() { return existeRuta; }
    public void setExisteRuta(boolean existeRuta) { this.existeRuta = existeRuta; }
    public List<Ubicacion> getRecorrido() { return recorrido; }
    public void setRecorrido(List<Ubicacion> recorrido) { this.recorrido = recorrido; }
    public int getTiempoTotal() { return tiempoTotal; }
    public void setTiempoTotal(int tiempoTotal) { this.tiempoTotal = tiempoTotal; }
    public List<String> getDecisiones() { return decisiones; }
    public void agregarDecision(String decision) { this.decisiones.add(decision); }

    public void mostrar() {
        System.out.println("\n=== Resultado del cálculo de ruta ===");
        System.out.println("-- Decisiones tomadas durante el cálculo --");
        for (String d : decisiones) {
            System.out.println("  " + d);
        }
        if (!existeRuta) {
            System.out.println("No existe una ruta disponible entre las ubicaciones indicadas.");
        } else {
            System.out.print("Recorrido: ");
            for (int i = 0; i < recorrido.size(); i++) {
                System.out.print(recorrido.get(i).getNombre());
                if (i < recorrido.size() - 1) System.out.print(" -> ");
            }
            System.out.println();
            System.out.println("Tiempo total: " + tiempoTotal + " minutos");
        }
    }
}
