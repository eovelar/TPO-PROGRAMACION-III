# TPO Programacion III
## Planificacion de rutas y distribucion de ayuda

Proyecto desarrollado en **Java** para gestionar pedidos, calcular rutas de distribucion y optimizar la carga de vehiculos.

**Integrantes:** Ovelar Evangelina, Peralta Luka

---

## Propuesta elegida

Propuesta 2: Planificacion de rutas y distribucion de ayuda.

El sistema administra depositos, centros comunitarios, conexiones viales, vehiculos, pedidos y lotes de mercaderia.

---

## Funcionalidades obligatorias (primera etapa)

1. **Organizacion y consulta de pedidos** - Ordenamiento por fecha limite y prioridad, busqueda por identificador.
2. **Calculo de la ruta mas rapida** - Algoritmo de Dijkstra sobre la red vial dirigida.
3. **Seleccion de cargas prioritarias** - Problema de la mochila 0/1 mediante Programacion Dinamica.

## Funcionalidades adicionales (segunda etapa)

- **A1:** Consultas de rutas entre todos los centros (Floyd-Warshall).
- **B1:** Recorrido con multiples entregas (Backtracking).
- **B3:** Rutas alternativas con restricciones (Backtracking).

---

## Estrategias algoritmicas aplicadas

| Funcionalidad | Estrategia | Justificacion |
|---|---|---|
| Ordenamiento de pedidos | Divide y Conquista (Merge Sort) | Permite ordenar volumenes crecientes de pedidos en O(n log n) sin depender de metodos provistos por Java. |
| Busqueda de pedidos | Busqueda binaria | Requiere el arreglo previamente ordenado; complejidad O(log n). |
| Calculo de ruta mas rapida | Greedy (Dijkstra) | Los tiempos de viaje son positivos, lo que garantiza la optimalidad del enfoque voraz. Complejidad O(V^2). |
| Seleccion de cargas | Programacion Dinamica (mochila 0/1) | Los lotes no se fraccionan; la programacion dinamica explora todas las combinaciones sin redundancia. |

---

## Estructura del proyecto

```
src/distribucion/
    modelo/
        TipoUbicacion.java        Enum: DEPOSITO, CENTRO_COMUNITARIO
        Ubicacion.java             Atributos: id, nombre, tipo. Getters y validaciones.
        ConexionVial.java          Conexion dirigida con tiempo de viaje y estado habilitado/deshabilitado.
        Pedido.java                Atributos del pedido: id, centro destino, fecha limite, prioridad, peso.
        LoteMercaderia.java        Lote con identificador, peso y puntaje de prioridad.
        Vehiculo.java              Vehiculo con capacidad maxima de carga.
    pedidos/
        MergeSortPedidos.java      Ordenamiento por fecha, prioridad e ID (Divide y Conquista).
        BusquedaBinariaPedidos.java Busqueda por ID sobre arreglo ordenado.
        GestorPedidos.java         Registro, ordenamiento, busqueda y rechazo de duplicados.
    cargas/
        SeleccionCargas.java       Mochila 0/1 mediante Programacion Dinamica.
    rutas/
        RedVial.java               Grafo dirigido: ubicaciones y conexiones con soporte de deshabilitacion.
        Dijkstra.java              Algoritmo de ruta de menor tiempo (Greedy). Complejidad O(V^2).
        ResultadoRuta.java         Resultado del calculo: existencia de ruta, recorrido, tiempo y decisiones.
        PruebaRutas.java           Pruebas del modulo de rutas sobre red ficticia.
    Main.java                      Menu interactivo del sistema.
    PruebaPedidos.java             Pruebas manuales del modulo de pedidos.
    PruebaCargas.java              Pruebas manuales del modulo de cargas.
    TestAlgoritmos.java            Pruebas automaticas del sistema.
```

---

## Modulo de rutas (Luka)

### Descripcion

El modulo de rutas resuelve la funcionalidad obligatoria numero 2: calculo de la ruta mas rapida entre dos ubicaciones de la red vial.

La red vial se modela como un **grafo dirigido con pesos positivos**, donde los nodos son ubicaciones (depositos y centros comunitarios) y las aristas son conexiones viales con un tiempo de viaje en minutos. Una conexion de A hacia B no implica la existencia de una conexion de B hacia A.

### Algoritmo utilizado: Dijkstra

**Estrategia:** Greedy (algoritmo voraz).

En cada iteracion, el algoritmo selecciona el nodo no visitado con menor tiempo acumulado y relaja las distancias de sus vecinos. Esta decision localmente optima conduce a la solucion global optima porque todos los pesos son estrictamente positivos, condicion garantizada por la consigna.

**Complejidad temporal:** O(V^2), donde V es la cantidad de ubicaciones en la red. En cada iteracion se recorre la lista de nodos no visitados para encontrar el minimo.

**Alternativas consideradas y descartadas:**
- BFS: no considera pesos diferentes en las aristas.
- Bellman-Ford: soporta pesos negativos pero tiene complejidad O(V * E), innecesaria dado que todos los tiempos son positivos.
- Floyd-Warshall: calcula las distancias entre todos los pares de nodos; resulta excesivo para una consulta puntual entre un origen y un destino.

### Funcionalidades implementadas

- Calculo de la ruta de menor tiempo entre un origen y un destino.
- Reconstruccion del recorrido completo (secuencia de ubicaciones).
- Visualizacion de las decisiones tomadas durante el calculo.
- Deshabilitacion de conexiones y recalculo de la ruta.
- Deteccion de destinos inaccesibles.
- Manejo del caso en que origen y destino coinciden.

### Casos de prueba (PruebaRutas.java)

Se utiliza una red ficticia con 5 ubicaciones y 6 conexiones dirigidas.

| Caso | Descripcion | Resultado esperado | Estado |
|---|---|---|---|
| 1 | Ruta normal: Deposito Central a Centro Este | Ruta 1-2-4, tiempo 15 min | Correcto |
| 2 | Deshabilitar conexion Norte-Este y recalcular | Ruta alternativa 1-3-4, tiempo 23 min | Correcto |
| 3 | Destino inaccesible: Centro Este a Deposito Central | Sin ruta disponible | Correcto |
| 4 | Origen igual a destino | Tiempo 0 min | Correcto |

Resultado: 4 pruebas ejecutadas, 0 errores.

---

## Division del trabajo

| Integrante | Rama | Responsabilidades |
|---|---|---|
| Ovelar Evangelina | `eva` | Organizacion y consulta de pedidos, Merge Sort, busqueda binaria, seleccion de cargas prioritarias (Programacion Dinamica), pruebas y menu interactivo. |
| Peralta Luka | `Luka` | Red vial, calculo de rutas con Dijkstra, deshabilitacion de conexiones y pruebas de rutas. |

La rama `main` contiene la base compartida y la integracion de avances probados.
