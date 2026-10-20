package distribucion.modelo;

public class LoteMercaderia {

    private int id;
    private int peso;
    private int prioridad;

    public LoteMercaderia(int id, int peso, int prioridad) {
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