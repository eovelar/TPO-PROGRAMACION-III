
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
    }
}
