
package distribucion.modelo;

public class Vehiculo {

    private int id;
    private int capacidadMaxima;

    public Vehiculo(int id, int capacidadMaxima) {

        if (id <= 0) {
            throw new IllegalArgumentException("el id debe ser positivo");
        }

        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("la capacidad debe ser mayor a cero");
        }

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
