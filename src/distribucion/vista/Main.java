
package distribucion.vista;

import distribucion.algoritmos.Dijkstra;
import distribucion.algoritmos.FloydWarshall;
import distribucion.algoritmos.RecorridoMultiplesEntregas;
import distribucion.algoritmos.RutasAlternativas;
import distribucion.algoritmos.SeleccionCargas;

import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Pedido;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.modelo.Vehiculo;

import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;
import distribucion.servicios.GestorPedidos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GestorPedidos gestor = new GestorPedidos();
        RedVial red;

        // cargar la red desde los archivos
        try {
            red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );
        } catch (IOException e) {
            System.out.println("error al cargar la red: " + e.getMessage());
            scanner.close();
            return;
        }

        Ubicacion centro = null;

        for (Ubicacion ubicacion : red.getUbicaciones()) {
            if (ubicacion.getTipo() == TipoUbicacion.CENTRO_COMUNITARIO) {
                centro = ubicacion;
                break;
            }
        }

        int opcion;

        do {
            System.out.println("\n=== SISTEMA DE DISTRIBUCION DE AYUDA ===");
            System.out.println("1. Gestion de pedidos");
            System.out.println("2. Calculo de rutas");
            System.out.println("3. Seleccion de cargas");
            System.out.println("4. Salir");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    if (centro != null) {
                        menuPedidos(scanner, gestor, centro);
                    } else {
                        System.out.println("no hay centros comunitarios cargados");
                    }
                    break;

                case 2:
                    menuRutas(scanner, red);
                    break;

                case 3:
                    menuCargas(scanner);
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

    // gestionar pedidos
    private static void menuPedidos(
            Scanner scanner,
            GestorPedidos gestor,
            Ubicacion centro) {

        int opcion;

        do {
            System.out.println("\n=== GESTION DE PEDIDOS ===");
            System.out.println("1. Agregar pedido");
            System.out.println("2. Ordenar por fecha");
            System.out.println("3. Ordenar por prioridad");
            System.out.println("4. Buscar por ID");
            System.out.println("5. Volver");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    agregarPedido(scanner, gestor, centro);
                    break;

                case 2:
                    for (Pedido pedido : gestor.obtenerPedidosPorFecha()) {
                        System.out.println(
                                pedido.getId() + " - " + pedido.getFechaLimite()
                        );
                    }
                    break;

                case 3:
                    for (Pedido pedido : gestor.obtenerPedidosPorPrioridad()) {
                        System.out.println(
                                pedido.getId()
                                        + " - prioridad: " + pedido.getPrioridad()
                                        + " - fecha: " + pedido.getFechaLimite()
                        );
                    }
                    break;

                case 4:
                    int idBuscado = leerEntero(
                            scanner, "ingrese el id del pedido: "
                    );

                    Pedido encontrado = gestor.buscarPedidoPorId(idBuscado);

                    if (encontrado != null) {
                        System.out.println("pedido encontrado:");
                        System.out.println("id: " + encontrado.getId());
                        System.out.println(
                                "centro: " + encontrado.getCentroDestino().getNombre()
                        );
                        System.out.println(
                                "fecha limite: " + encontrado.getFechaLimite()
                        );
                        System.out.println(
                                "prioridad: " + encontrado.getPrioridad()
                        );
                        System.out.println(
                                "peso: " + encontrado.getPesoTotal() + " kg"
                        );
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

    // agregar un pedido
    private static void agregarPedido(
            Scanner scanner,
            GestorPedidos gestor,
            Ubicacion centro) {

        int id = leerEntero(scanner, "id del pedido: ");

        System.out.print("fecha limite (AAAA-MM-DD): ");
        String fecha = scanner.nextLine();

        int prioridad = leerEntero(scanner, "prioridad: ");
        double peso = leerDouble(scanner, "peso total: ");

        try {
            Pedido pedido = new Pedido(
                    id,
                    centro,
                    LocalDate.parse(fecha),
                    prioridad,
                    peso
            );

            gestor.agregarPedido(pedido);
            System.out.println("pedido agregado correctamente");

        } catch (DateTimeParseException e) {
            System.out.println("error: formato de fecha incorrecto");

        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    // consultar rutas y modificar conexiones
    private static void menuRutas(Scanner scanner, RedVial red) {

        Dijkstra dijkstra = new Dijkstra(red);
        FloydWarshall floyd = new FloydWarshall(red);

        floyd.calcular();

        int opcion;

        do {
            System.out.println("\n=== CALCULO DE RUTAS ===");
            System.out.println("1. Mostrar red vial");
            System.out.println("2. Calcular ruta mas rapida (Dijkstra)");
            System.out.println("3. Consultar rutas entre ubicaciones (Floyd-Warshall)");
            System.out.println("4. Recorrido con multiples entregas (B1)");
            System.out.println("5. Rutas alternativas con restricciones (B3)");
            System.out.println("6. Deshabilitar conexion");
            System.out.println("7. Habilitar conexion");
            System.out.println("8. Volver");

            opcion = leerEntero(scanner, "Seleccione una opcion: ");

            switch (opcion) {

                case 1:
                    red.mostrarRed();
                    break;

                case 2: {
                    red.mostrarRed();

                    int origenId = leerEntero(scanner, "id de origen: ");
                    int destinoId = leerEntero(scanner, "id de destino: ");

                    Ubicacion origen = red.buscarUbicacionPorId(origenId);
                    Ubicacion destino = red.buscarUbicacionPorId(destinoId);

                    if (origen == null || destino == null) {
                        System.out.println("ubicacion no encontrada");
                        break;
                    }

                    ResultadoRuta resultado =
                            dijkstra.calcularRuta(origen, destino);

                    resultado.mostrar();
                    break;
                }

                case 3: {
                    red.mostrarRed();

                    int origenId = leerEntero(scanner, "id de origen: ");
                    int destinoId = leerEntero(scanner, "id de destino: ");

                    if (red.buscarUbicacionPorId(origenId) == null ||
                            red.buscarUbicacionPorId(destinoId) == null) {

                        System.out.println("ubicacion no encontrada");
                        break;
                    }

                    ResultadoRuta resultado =
                            floyd.consultar(origenId, destinoId);

                    resultado.mostrar();
                    break;
                }

                case 4:
                    calcularMultiplesEntregas(scanner, red, floyd);
                    break;

                case 5:
                    buscarRutasAlternativas(scanner, red);
                    break;

                case 6: {
                    int desde = leerEntero(
                            scanner, "id de origen de la conexion: "
                    );

                    int hacia = leerEntero(
                            scanner, "id de destino de la conexion: "
                    );

                    if (red.deshabilitarConexion(desde, hacia)) {
                        floyd.calcular();
                        System.out.println("conexion deshabilitada");
                    } else {
                        System.out.println("conexion no encontrada");
                    }
                    break;
                }

                case 7: {
                    int desde = leerEntero(
                            scanner, "id de origen de la conexion: "
                    );

                    int hacia = leerEntero(
                            scanner, "id de destino de la conexion: "
                    );

                    if (red.habilitarConexion(desde, hacia)) {
                        floyd.calcular();
                        System.out.println("conexion habilitada");
                    } else {
                        System.out.println("conexion no encontrada");
                    }
                    break;
                }

                case 8:
                    System.out.println("volviendo al menu principal...");
                    break;

                default:
                    System.out.println("opcion no valida");
            }

        } while (opcion != 8);
    }

    // calcular el orden de varias entregas
    private static void calcularMultiplesEntregas(
            Scanner scanner,
            RedVial red,
            FloydWarshall floyd) {

        System.out.println("\n=== RECORRIDO CON MULTIPLES ENTREGAS ===");

        List<Ubicacion> depositos = new ArrayList<>();
        List<Ubicacion> disponibles = new ArrayList<>();

        // separar depositos y centros comunitarios
        for (Ubicacion ubicacion : red.getUbicaciones()) {

            if (ubicacion.getTipo() == TipoUbicacion.DEPOSITO) {
                depositos.add(ubicacion);
            }

            if (ubicacion.getTipo() == TipoUbicacion.CENTRO_COMUNITARIO) {
                disponibles.add(ubicacion);
            }
        }

        if (depositos.isEmpty()) {
            System.out.println("no hay depositos disponibles");
            return;
        }

        if (disponibles.isEmpty()) {
            System.out.println("no hay centros comunitarios disponibles");
            return;
        }

        System.out.println("\ndepositos disponibles:");

        for (Ubicacion deposito : depositos) {
            System.out.println(
                    deposito.getId() + " - " + deposito.getNombre()
            );
        }

        System.out.println("\n0 - Cancelar");

        Ubicacion deposito;

        while (true) {

            int idDeposito = leerEntero(
                    scanner, "id del deposito de salida: "
            );

            if (idDeposito == 0) {
                System.out.println("operacion cancelada");
                return;
            }

            deposito = red.buscarUbicacionPorId(idDeposito);

            if (deposito != null &&
                    deposito.getTipo() == TipoUbicacion.DEPOSITO) {
                break;
            }

            System.out.println(
                    "deposito no valido. seleccione uno de la lista o 0 para cancelar"
            );
        }

        System.out.println("\ncentros comunitarios disponibles:");

        for (Ubicacion centro : disponibles) {
            System.out.println(
                    centro.getId() + " - " + centro.getNombre()
            );
        }

        int maximo = Math.min(8, disponibles.size());

        System.out.println(
                "\npuede seleccionar entre 1 y " + maximo + " centros"
        );

        System.out.println("0 - Cancelar");

        int cantidad;

        while (true) {

            cantidad = leerEntero(
                    scanner, "cantidad de centros a visitar: "
            );

            if (cantidad == 0) {
                System.out.println("operacion cancelada");
                return;
            }

            if (cantidad >= 1 && cantidad <= maximo) {
                break;
            }

            System.out.println(
                    "cantidad no valida. debe ingresar entre 1 y "
                            + maximo + ", o 0 para cancelar"
            );
        }

        List<Ubicacion> centros = new ArrayList<>();
        Set<Integer> seleccionados = new HashSet<>();

        // seleccionar centros sin repetirlos
        while (centros.size() < cantidad) {

            System.out.println(
                    "\ncentro " + (centros.size() + 1)
                            + " de " + cantidad
            );

            int restantes = 0;

            for (Ubicacion centro : disponibles) {

                if (!seleccionados.contains(centro.getId())) {
                    System.out.println(
                            centro.getId() + " - " + centro.getNombre()
                    );
                    restantes++;
                }
            }

            if (restantes == 0) {
                System.out.println("no quedan centros para seleccionar");
                return;
            }

            System.out.println("0 - Cancelar");

            int idCentro = leerEntero(
                    scanner, "id del centro: "
            );

            if (idCentro == 0) {
                System.out.println("operacion cancelada");
                return;
            }

            Ubicacion centro = red.buscarUbicacionPorId(idCentro);

            if (centro == null ||
                    centro.getTipo() != TipoUbicacion.CENTRO_COMUNITARIO) {

                System.out.println(
                        "centro no valido. seleccione uno de la lista"
                );
                continue;
            }

            if (seleccionados.contains(idCentro)) {
                System.out.println("ese centro ya fue seleccionado");
                continue;
            }

            seleccionados.add(idCentro);
            centros.add(centro);
        }

        // buscar el mejor recorrido
        RecorridoMultiplesEntregas recorrido =
                new RecorridoMultiplesEntregas(floyd);

        try {
            recorrido.calcular(deposito, centros);

        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    // buscar rutas con restricciones
    private static void buscarRutasAlternativas(
            Scanner scanner,
            RedVial red) {

        System.out.println("\n=== RUTAS ALTERNATIVAS ===");

        red.mostrarRed();

        int idOrigen = leerEntero(scanner, "id de origen: ");
        int idDestino = leerEntero(scanner, "id de destino: ");

        int tiempoMaximo = leerEntero(
                scanner, "tiempo maximo permitido (minutos): "
        );

        if (tiempoMaximo < 0) {
            System.out.println("el tiempo maximo no puede ser negativo");
            return;
        }

        int cantidadProhibidas = leerEntero(
                scanner, "cantidad de ubicaciones prohibidas: "
        );

        if (cantidadProhibidas < 0 ||
                cantidadProhibidas > red.getUbicaciones().size()) {

            System.out.println("cantidad de ubicaciones no valida");
            return;
        }

        Set<Integer> prohibidas = new HashSet<>();

        // cargar ubicaciones prohibidas
        for (int i = 0; i < cantidadProhibidas; i++) {

            int id = leerEntero(
                    scanner, "id de ubicacion prohibida " + (i + 1) + ": "
            );

            if (red.buscarUbicacionPorId(id) == null) {
                System.out.println("ubicacion no encontrada");
                i--;
                continue;
            }

            if (!prohibidas.add(id)) {
                System.out.println("esa ubicacion ya fue agregada");
                i--;
            }
        }

        RutasAlternativas rutas = new RutasAlternativas(red);

        try {
            rutas.buscar(
                    idOrigen,
                    idDestino,
                    tiempoMaximo,
                    prohibidas
            );

        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    // seleccionar cargas
    private static void menuCargas(Scanner scanner) {

        int capacidad = leerEntero(
                scanner, "\ncapacidad maxima del vehiculo: "
        );

        int cantidad = leerEntero(
                scanner, "cantidad de lotes: "
        );

        if (cantidad < 0) {
            System.out.println("la cantidad de lotes no puede ser negativa");
            return;
        }

        try {
            Vehiculo vehiculo = new Vehiculo(1, capacidad);
            LoteMercaderia[] lotes = new LoteMercaderia[cantidad];

            // cargar los lotes
            for (int i = 0; i < cantidad; i++) {

                System.out.println("\nlote " + (i + 1));

                int peso = leerEntero(scanner, "peso: ");
                int prioridad = leerEntero(scanner, "prioridad: ");

                lotes[i] = new LoteMercaderia(
                        i + 1, peso, prioridad
                );
            }

            SeleccionCargas.seleccionar(lotes, vehiculo);

        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    // validar enteros
    private static int leerEntero(
            Scanner scanner,
            String mensaje) {

        while (true) {

            System.out.print(mensaje);

            try {
                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {
                System.out.println(
                        "error: ingrese un numero entero valido"
                );
            }
        }
    }

    // validar decimales
    private static double leerDouble(
            Scanner scanner,
            String mensaje) {

        while (true) {

            System.out.print(mensaje);

            String entrada = scanner.nextLine()
                    .trim()
                    .replace(',', '.');

            try {
                double numero = Double.parseDouble(entrada);

                if (!Double.isFinite(numero)) {
                    throw new NumberFormatException();
                }

                return numero;

            } catch (NumberFormatException e) {
                System.out.println(
                        "error: ingrese un numero valido"
                );
            }
        }
    }
}
