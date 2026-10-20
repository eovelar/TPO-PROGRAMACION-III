
package distribucion.modelo;

public class LoteMercaderia {

    private int id;
    private int peso;
    private int prioridad;

    public LoteMercaderia(int id, int peso, int prioridad) {

        if (id <= 0) {
            throw new IllegalArgumentException("el id debe ser positivo");
        }

        if (peso <= 0) {
            throw new IllegalArgumentException("el peso debe ser mayor a cero");
        }

        if (prioridad < 0) {
            throw new IllegalArgumentException("la prioridad no puede ser negativa");
        }

        this.id = id;
        this.peso = peso;
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public int getPeso() {
        return peso;
    }

    public int getPrioridad() {
        return prioridad;
    }
}
