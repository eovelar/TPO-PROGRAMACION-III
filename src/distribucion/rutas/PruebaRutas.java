package distribucion.rutas;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;

/**
 * Pruebas del módulo de rutas sobre una red vial ficticia.
 *
 * Red de prueba:
 *   Depósito Central (1) --10--> Centro Norte (2) --5--> Centro Este (4)
 *        |                            |
 *       15                           20
 *        v                            v
 *   Centro Sur (3) ----8-----------> Centro Este (4)
 *   Centro Norte (2) --12--> Depósito Secundario (5)
 *
 * Casos:
 *   1. Ruta normal (ruta óptima: 1->2->4, 15 min)
 *   2. Recálculo al deshabilitar conexión Norte->Este (alternativa: 1->3->4, 23 min)
 *   3. Destino inaccesible (no hay vuelta desde Centro Este a Depósito Central)
 *   4. Origen igual a destino (tiempo 0)
 */
public class PruebaRutas {

    private static int pruebas = 0;
    private static int errores = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  PRUEBAS - MÓDULO RUTAS VIALES");
        System.out.println("========================================");

        RedVial red = construirRedFicticia();
        red.mostrarRed();

        Dijkstra dijkstra = new Dijkstra(red);

        prueba1_RutaNormal(dijkstra, red);
        prueba2_DeshabilitarConexion(dijkstra, red);
        prueba3_DestinoInaccesible(dijkstra, red);
        prueba4_OrigenIgualDestino(dijkstra, red);

        System.out.println("\n========================================");
        System.out.println("Resultado: " + pruebas + " pruebas, " + errores + " errores.");
        System.out.println("========================================");
    }

    private static RedVial construirRedFicticia() {
        RedVial red = new RedVial();

        Ubicacion deposito1   = new Ubicacion(1, "Deposito Central",    TipoUbicacion.DEPOSITO);
        Ubicacion centroNorte = new Ubicacion(2, "Centro Norte",         TipoUbicacion.CENTRO_COMUNITARIO);
        Ubicacion centroSur   = new Ubicacion(3, "Centro Sur",           TipoUbicacion.CENTRO_COMUNITARIO);
        Ubicacion centroEste  = new Ubicacion(4, "Centro Este",          TipoUbicacion.CENTRO_COMUNITARIO);
        Ubicacion deposito2   = new Ubicacion(5, "Deposito Secundario",  TipoUbicacion.DEPOSITO);

        red.agregarUbicacion(deposito1);
        red.agregarUbicacion(centroNorte);
        red.agregarUbicacion(centroSur);
        red.agregarUbicacion(centroEste);
        red.agregarUbicacion(deposito2);

        red.agregarConexion(new ConexionVial(deposito1,   centroNorte, 10));
        red.agregarConexion(new ConexionVial(deposito1,   centroSur,   15));
        red.agregarConexion(new ConexionVial(centroNorte, centroEste,   5));
        red.agregarConexion(new ConexionVial(centroNorte, deposito2,   12));
        red.agregarConexion(new ConexionVial(centroSur,   centroEste,   8));
        red.agregarConexion(new ConexionVial(centroNorte, centroSur,   20));

        return red;
    }

    private static void prueba1_RutaNormal(Dijkstra dijkstra, RedVial red) {
        pruebas++;
        System.out.println("\n--- Prueba 1: Ruta normal (Deposito Central -> Centro Este) ---");
        Ubicacion origen  = red.buscarUbicacionPorId(1);
        Ubicacion destino = red.buscarUbicacionPorId(4);
        ResultadoRuta resultado = dijkstra.calcularRuta(origen, destino);
        resultado.mostrar();
        // Optimo: 1->2->4 = 10+5 = 15 min
        if (!resultado.isExisteRuta() || resultado.getTiempoTotal() != 15) {
            System.out.println("[ERROR] Se esperaba ruta con tiempo 15. Obtenido: "
                    + (resultado.isExisteRuta() ? resultado.getTiempoTotal() : "sin ruta"));
            errores++;
        } else {
            System.out.println("[OK] Ruta encontrada: 15 minutos.");
        }
    }

    private static void prueba2_DeshabilitarConexion(Dijkstra dijkstra, RedVial red) {
        pruebas++;
        System.out.println("\n--- Prueba 2: Deshabilitar Norte->Este y recalcular ---");
        red.deshabilitarConexion(2, 4);
        Ubicacion origen  = red.buscarUbicacionPorId(1);
        Ubicacion destino = red.buscarUbicacionPorId(4);
        ResultadoRuta resultado = dijkstra.calcularRuta(origen, destino);
        resultado.mostrar();
        // Alternativa: 1->3->4 = 15+8 = 23 min
        if (!resultado.isExisteRuta() || resultado.getTiempoTotal() != 23) {
            System.out.println("[ERROR] Se esperaba ruta alternativa con tiempo 23. Obtenido: "
                    + (resultado.isExisteRuta() ? resultado.getTiempoTotal() : "sin ruta"));
            errores++;
        } else {
            System.out.println("[OK] Ruta alternativa correcta: 23 minutos.");
        }
        red.habilitarConexion(2, 4);
    }

    private static void prueba3_DestinoInaccesible(Dijkstra dijkstra, RedVial red) {
        pruebas++;
        System.out.println("\n--- Prueba 3: Destino inaccesible (Centro Este -> Deposito Central) ---");
        Ubicacion origen  = red.buscarUbicacionPorId(4);
        Ubicacion destino = red.buscarUbicacionPorId(1);
        ResultadoRuta resultado = dijkstra.calcularRuta(origen, destino);
        resultado.mostrar();
        if (resultado.isExisteRuta()) {
            System.out.println("[ERROR] Se esperaba destino inaccesible.");
            errores++;
        } else {
            System.out.println("[OK] Destino correctamente identificado como inaccesible.");
        }
    }

    private static void prueba4_OrigenIgualDestino(Dijkstra dijkstra, RedVial red) {
        pruebas++;
        System.out.println("\n--- Prueba 4: Origen igual a destino ---");
        Ubicacion origen  = red.buscarUbicacionPorId(1);
        Ubicacion destino = red.buscarUbicacionPorId(1);
        ResultadoRuta resultado = dijkstra.calcularRuta(origen, destino);
        resultado.mostrar();
        if (!resultado.isExisteRuta() || resultado.getTiempoTotal() != 0) {
            System.out.println("[ERROR] Se esperaba ruta con tiempo 0.");
            errores++;
        } else {
            System.out.println("[OK] Origen igual a destino: tiempo 0 correcto.");
        }
    }
}
