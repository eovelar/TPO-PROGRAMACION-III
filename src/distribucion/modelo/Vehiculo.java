package distribucion.modelo;

public class Vehiculo {

    private int id;
    private int capacidadMaxima;

    public Vehiculo(int id, int capacidadMaxima) {
        this.id = id;
        this.capacidadMaxima = capacidadMaxima;
    }

    public int getId() {
        return id;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }
}