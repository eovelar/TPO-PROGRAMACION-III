
package distribucion;

import distribucion.pedidos.GestorPedidos;
import distribucion.modelo.Pedido;
import distribucion.modelo.Ubicacion;
import distribucion.modelo.TipoUbicacion;

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
                    System.out.println("seleccion de cargas");
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
                    System.out.println("ordenar por fecha");
                    break;

                case 3:
                    System.out.println("ordenar por prioridad");
                    break;

                case 4:
                    System.out.println("buscar por id");
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
