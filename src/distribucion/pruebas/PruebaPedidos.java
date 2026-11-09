
package distribucion.pruebas;

import distribucion.algoritmos.BusquedaBinariaPedidos;
import distribucion.algoritmos.MergeSortPedidos;
import distribucion.modelo.Pedido;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.servicios.GestorPedidos;

import java.time.LocalDate;

public class PruebaPedidos {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("=== PRUEBAS DE PEDIDOS ===");

        Ubicacion centro = new Ubicacion(
                1, "Centro Norte", TipoUbicacion.CENTRO_COMUNITARIO
        );

        GestorPedidos gestor = new GestorPedidos();

        // prueba 1: lista vacia
        probar(
                "lista vacia",
                gestor.obtenerPedidosPorFecha().length == 0
        );

        // prueba 2: buscar sin pedidos
        probar(
                "busqueda en lista vacia",
                gestor.buscarPedidoPorId(100) == null
        );

        // cargar pedidos de prueba
        gestor.agregarPedido(
                new Pedido(103, centro, LocalDate.of(2026, 10, 20), 5, 25)
        );

        gestor.agregarPedido(
                new Pedido(104, centro, LocalDate.of(2026, 10, 17), 3, 30)
        );

        gestor.agregarPedido(
                new Pedido(102, centro, LocalDate.of(2026, 10, 18), 5, 15)
        );

        gestor.agregarPedido(
                new Pedido(101, centro, LocalDate.of(2026, 10, 18), 5, 20)
        );

        // prueba 3: ordenamiento por fecha
        probar(
                "ordenamiento por fecha",
                verificarOrden(
                        gestor.obtenerPedidosPorFecha(),
                        104, 101, 102, 103
                )
        );

        // prueba 4: prioridad con desempates
        probar(
                "prioridad y desempates",
                verificarOrden(
                        gestor.obtenerPedidosPorPrioridad(),
                        101, 102, 103, 104
                )
        );

        // prueba 5: buscar pedido existente
        Pedido encontrado = gestor.buscarPedidoPorId(102);

        probar(
                "busqueda de pedido existente",
                encontrado != null && encontrado.getId() == 102
        );

        // prueba 6: buscar primer id
        probar(
                "busqueda del primer id",
                gestor.buscarPedidoPorId(101) != null
        );

        // prueba 7: buscar ultimo id
        probar(
                "busqueda del ultimo id",
                gestor.buscarPedidoPorId(104) != null
        );

        // prueba 8: id inexistente
        probar(
                "busqueda de id inexistente",
                gestor.buscarPedidoPorId(999) == null
        );

        // prueba 9: arreglo de un elemento
        Pedido[] uno = {
                new Pedido(201, centro, LocalDate.of(2026, 11, 1), 2, 10)
        };

        MergeSortPedidos.ordenarPorFecha(uno);

        probar(
                "arreglo de un elemento",
                verificarOrden(uno, 201)
        );

        // prueba 10: arreglo en orden inverso
        Pedido[] inverso = {
                new Pedido(303, centro, LocalDate.of(2026, 11, 3), 2, 10),
                new Pedido(302, centro, LocalDate.of(2026, 11, 2), 2, 10),
                new Pedido(301, centro, LocalDate.of(2026, 11, 1), 2, 10)
        };

        MergeSortPedidos.ordenarPorFecha(inverso);

        probar(
                "ordenamiento inverso",
                verificarOrden(inverso, 301, 302, 303)
        );

        // prueba 11: ordenar arreglo vacio
        Pedido[] vacio = {};

        MergeSortPedidos.ordenarPorPrioridad(vacio);

        probar(
                "ordenamiento de arreglo vacio",
                vacio.length == 0
        );

        // prueba 12: ordenamiento por id
        Pedido[] porId = gestor.obtenerPedidosPorPrioridad();

        MergeSortPedidos.ordenarPorId(porId);

        probar(
                "ordenamiento por id",
                verificarOrden(porId, 101, 102, 103, 104)
        );

        // prueba 13: busqueda binaria directa
        Pedido resultado = BusquedaBinariaPedidos.buscarPorId(
                porId, 103
        );

        probar(
                "busqueda binaria directa",
                resultado != null && resultado.getId() == 103
        );

        // prueba 14: arreglo nulo
        probar(
                "arreglo nulo",
                BusquedaBinariaPedidos.buscarPorId(null, 100) == null
        );

        // prueba 15: peso negativo
        probarError("peso negativo", () -> {
            new Pedido(
                    105, centro, LocalDate.of(2026, 10, 20), 3, -10
            );
        });

        // prueba 16: destino invalido
        probarError("destino invalido", () -> {

            Ubicacion deposito = new Ubicacion(
                    2, "Deposito Central", TipoUbicacion.DEPOSITO
            );

            new Pedido(
                    106, deposito, LocalDate.of(2026, 10, 20), 3, 20
            );
        });

        // prueba 17: fecha nula
        probarError("fecha nula", () -> {
            new Pedido(107, centro, null, 3, 20);
        });

        // prueba 18: id duplicado
        probarError("pedido duplicado", () -> {

            gestor.agregarPedido(
                    new Pedido(
                            101, centro, LocalDate.of(2026, 10, 25), 4, 15
                    )
            );
        });

        // prueba 19: pedido nulo
        probarError("pedido nulo", () -> {
            gestor.agregarPedido(null);
        });

        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println("pruebas correctas: " + correctas);
        System.out.println("pruebas fallidas: " + fallidas);

        if (fallidas > 0) {
            throw new AssertionError(
                    "hay pruebas de pedidos que no pasaron"
            );
        }
    }

    // comprobar el orden de los identificadores
    private static boolean verificarOrden(
            Pedido[] pedidos,
            int... idsEsperados) {

        if (pedidos.length != idsEsperados.length) {
            return false;
        }

        for (int i = 0; i < pedidos.length; i++) {

            if (pedidos[i] == null ||
                    pedidos[i].getId() != idsEsperados[i]) {
                return false;
            }
        }

        return true;
    }

    // comprobar que se rechacen entradas invalidas
    private static void probarError(
            String nombre,
            Runnable prueba) {

        try {
            prueba.run();
            registrar(nombre, false);

        } catch (IllegalArgumentException e) {
            registrar(nombre, true);
        }
    }

    // registrar una condicion comprobada
    private static void probar(
            String nombre,
            boolean condicion) {

        registrar(nombre, condicion);
    }

    private static void registrar(
            String nombre,
            boolean correcta) {

        if (correcta) {
            correctas++;
            System.out.println("[OK] " + nombre);

        } else {
            fallidas++;
            System.out.println("[ERROR] " + nombre);
        }
    }
}
