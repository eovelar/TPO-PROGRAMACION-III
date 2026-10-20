
package distribucion;

import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Vehiculo;
import distribucion.cargas.SeleccionCargas;

public class PruebaCargas {

    public static void main(String[] args) {

        Vehiculo vehiculo = new Vehiculo(1, 50);

        LoteMercaderia[] lotes = {
                new LoteMercaderia(1, 10, 30),
                new LoteMercaderia(2, 20, 40),
                new LoteMercaderia(3, 30, 50),
                new LoteMercaderia(4, 40, 60)
        };

        SeleccionCargas.seleccionar(lotes, vehiculo);
        // probar un lote con peso negativo
        try {
            LoteMercaderia loteInvalido = new LoteMercaderia(5, -10, 20);
        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
        // probar un vehiculo con capacidad negativa
        try {
            Vehiculo vehiculoInvalido = new Vehiculo(2, -50);
        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
        // probar sin lotes disponibles
        LoteMercaderia[] lotesVacios = {};

        System.out.println("\nprueba sin lotes:");
        SeleccionCargas.seleccionar(lotesVacios, vehiculo);
    }
}
