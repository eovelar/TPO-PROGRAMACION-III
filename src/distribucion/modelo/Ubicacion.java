package distribucion.modelo;

public class Ubicacion {

    private int id;
    private String nombre;
    private TipoUbicacion tipo;

    public Ubicacion(int id, String nombre, TipoUbicacion tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoUbicacion getTipo() {
        return tipo;
    }
}