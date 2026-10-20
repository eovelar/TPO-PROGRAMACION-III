
package distribucion.modelo;

public class Ubicacion {

    private int id;
    private String nombre;
    private TipoUbicacion tipo;

    public Ubicacion(int id, String nombre, TipoUbicacion tipo) {

        if (id <= 0) {
            throw new IllegalArgumentException("el id debe ser positivo");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("el nombre no puede estar vacio");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("el tipo de ubicacion no puede ser nulo");
        }

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
