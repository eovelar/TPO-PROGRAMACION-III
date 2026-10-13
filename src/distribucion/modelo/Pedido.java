package distribucion.modelo;

import java.time.LocalDate;

public class Pedido {

    private int id;
    private Ubicacion centroDestino;
    private LocalDate fechaLimite;
    private int prioridad;
    private double pesoTotal;

    public Pedido(int id, Ubicacion centroDestino, LocalDate fechaLimite,
                  int prioridad, double pesoTotal) {

        this.id = id;
        this.centroDestino = centroDestino;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.pesoTotal = pesoTotal;
    }

    public int getId() {
        return id;
    }

    public Ubicacion getCentroDestino() {
        return centroDestino;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public double getPesoTotal() {
        return pesoTotal;
    }
}