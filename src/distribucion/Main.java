package distribucion;

import distribucion.cargas.SeleccionCargas;
import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Pedido;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.modelo.Vehiculo;
import distribucion.pedidos.GestorPedidos;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GestorPedidos gestor = new GestorPedidos();

        Ubicacion centro = new Ubicacion(
                1, "Centro Norte", TipoUbicacion.CENTRO_COMUNITARIO
        );

        int opcion;

        do {
            System.out.println("\n=== SISTEMA DE DISTRIBUCION DE AYUDA ===");
            System.out.println("1. Gestion de pedidos");
            System.out.println("2. Calculo de rutas");
            System.out.println("3. Seleccion de cargas");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    menuPedidos(scanner, gestor, centro);
                    break;

                case 2:
                    System.out.println("rutas pendientes de implementar");
                    break;

                case 3:
                    // seleccionar cargas
                    Vehiculo vehiculo = new Vehiculo(1, 50);

                    LoteMercaderia[] lotes = {
                            new LoteMercaderia(1, 10, 30),
                            new LoteMercaderia(2, 20, 40),
                            new LoteMercaderia(3, 30, 50),
                            new LoteMercaderia(4, 40, 60)
                    };

                    SeleccionCargas.seleccionar(lotes, vehiculo);
                    break;

                case 4:
                    System.out.println("saliendo del sistema...");
                    break;

                default:
                    System.out.println("opcion no valida");
            }

        } while (opcion != 4);

        scanner.close();
    }

    private static void menuPedidos(Scanner scanner, GestorPedidos gestor, Ubicacion centro) {

        int opcion;

        do {
            System.out.println("\n=== GESTION DE PEDIDOS ===");
            System.out.println("1. Agregar pedido");
            System.out.println("2. Ordenar por fecha");
            System.out.println("3. Ordenar por prioridad");
            System.out.println("4. Buscar por ID");
            System.out.println("5. Volver");
            System.out.print("Seleccione una opcion: ");

            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    agregarPedido(scanner, gestor, centro);
                    break;

                case 2:
                    // mostrar pedidos por fecha
                    for (Pedido pedido : gestor.obtenerPedidosPorFecha()) {
                        System.out.println(pedido.getId() + " - " + pedido.getFechaLimite());
                    }
                    break;

                case 3:
                    // mostrar pedidos por prioridad
                    for (Pedido pedido : gestor.obtenerPedidosPorPrioridad()) {
                        System.out.println(pedido.getId() + " - prioridad: "
                                + pedido.getPrioridad() + " - fecha: "
                                + pedido.getFechaLimite());
                    }
                    break;

                case 4:
                    // buscar pedido por id
                    System.out.print("ingrese el id del pedido: ");
                    int idBuscado = scanner.nextInt();

                    Pedido encontrado = gestor.buscarPedidoPorId(idBuscado);

                    if (encontrado != null) {
                        System.out.println("pedido encontrado:");
                        System.out.println("id: " + encontrado.getId());
                        System.out.println("centro: " + encontrado.getCentroDestino().getNombre());
                        System.out.println("fecha limite: " + encontrado.getFechaLimite());
                        System.out.println("prioridad: " + encontrado.getPrioridad());
                        System.out.println("peso: " + encontrado.getPesoTotal() + " kg");
                    } else {
                        System.out.println("pedido no encontrado");
                    }
                    break;

                case 5:
                    System.out.println("volviendo al menu principal...");
                    break;

                default:
                    System.out.println("opcion no valida");
            }

        } while (opcion != 5);
    }

    // cargar un pedido
    private static void agregarPedido(Scanner scanner, GestorPedidos gestor, Ubicacion centro) {

        System.out.print("id del pedido: ");
        int id = scanner.nextInt();

        System.out.print("fecha limite (AAAA-MM-DD): ");
        String fecha = scanner.next();

        System.out.print("prioridad: ");
        int prioridad = scanner.nextInt();

        System.out.print("peso total: ");
        double peso = scanner.nextDouble();

        try {
            Pedido pedido = new Pedido(
                    id, centro, LocalDate.parse(fecha), prioridad, peso
            );

            gestor.agregarPedido(pedido);
            System.out.println("pedido agregado correctamente");

        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
