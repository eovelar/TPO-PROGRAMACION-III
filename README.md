
# TPO Programación III

## Planificación de rutas y distribución de ayuda

Trabajo Práctico Obligatorio desarrollado en **Java** para la materia Programación III.

**Integrantes:**
- Ovelar Evangelina
- Peralta Luka

### Descripción del proyecto

El sistema permite organizar pedidos de ayuda, calcular rutas de distribución entre depósitos y centros comunitarios, y seleccionar cargas prioritarias según la capacidad disponible de los vehículos.

Para resolver estas funcionalidades se implementaron distintos algoritmos utilizando las estrategias estudiadas en la materia.

## Funcionalidades implementadas

### 1. Organización y consulta de pedidos

Permite registrar pedidos, ordenarlos por fecha límite o prioridad y buscarlos mediante su identificador.

**Algoritmos utilizados:**
- Merge Sort para ordenar los pedidos.
- Búsqueda binaria para localizar pedidos por ID.

**Estrategia:** Divide y Conquista para el ordenamiento.

### 2. Cálculo de la ruta más rápida

Permite encontrar el recorrido de menor tiempo entre dos ubicaciones de una red vial dirigida.

También permite deshabilitar conexiones, recalcular rutas y detectar destinos inaccesibles.

**Algoritmo utilizado:** Dijkstra.

**Estrategia:** Greedy.

### 3. Selección de cargas prioritarias

Permite seleccionar los lotes de mercadería que alcanzan la mayor prioridad total sin superar la capacidad del vehículo.

Los lotes no pueden fraccionarse ni seleccionarse más de una vez.

**Algoritmo utilizado:** Mochila 0/1.

**Estrategia:** Programación Dinámica.

## Estructura del proyecto

```text
src/
└── distribucion/
    ├── Main.java
    ├── PruebaPedidos.java
    ├── PruebaCargas.java
    ├── TestAlgoritmos.java
    │
    ├── modelo/
    │   ├── TipoUbicacion.java
    │   ├── Ubicacion.java
    │   ├── ConexionVial.java
    │   ├── Pedido.java
    │   ├── LoteMercaderia.java
    │   └── Vehiculo.java
    │
    ├── pedidos/
    │   ├── GestorPedidos.java
    │   ├── MergeSortPedidos.java
    │   └── BusquedaBinariaPedidos.java
    │
    ├── cargas/
    │   └── SeleccionCargas.java
    │
    └── rutas/
        ├── RedVial.java
        ├── Dijkstra.java
        ├── ResultadoRuta.java
        └── PruebaRutas.java
```

## Ejecución

1. Clonar el repositorio.
2. Abrir el proyecto en IntelliJ IDEA.
3. Configurar un JDK compatible.
4. Ejecutar la clase `distribucion.Main`.

El programa utiliza un menú interactivo por consola con las tres funcionalidades principales.

## Pruebas

Se incluyeron clases de prueba para verificar los algoritmos y distintos casos particulares.

- `PruebaPedidos.java`: ordenamientos, búsquedas y validaciones.
- `PruebaCargas.java`: selección de lotes, capacidades y casos límite.
- `PruebaRutas.java`: rutas mínimas, conexiones deshabilitadas y destinos inaccesibles.
- `TestAlgoritmos.java`: pruebas automáticas de pedidos y cargas.

Los módulos se probaron individualmente y se integraron en el menú principal.

## Complejidades temporales

| Algoritmo | Complejidad |
|---|---|
| Merge Sort | O(n log n) |
| Búsqueda binaria | O(log n) |
| Dijkstra | O(V² + V·E) |
| Mochila 0/1 | O(n × C) |

La búsqueda binaria requiere ordenar previamente los pedidos por ID. En nuestra implementación, el método completo de búsqueda tiene un costo O(n log n).

## Funcionalidades para la segunda entrega

Se seleccionaron las siguientes funcionalidades adicionales:

- **A1:** Consultas de rutas entre todos los centros.
- **B1:** Recorrido con múltiples entregas.
- **B3:** Rutas alternativas con restricciones.

Estas funcionalidades corresponden a la segunda etapa del trabajo.

## Organización del equipo

El desarrollo se realizó utilizando Git y GitHub, con ramas individuales para registrar los avances de cada integrante.

- `eva`: pedidos, cargas y menú del sistema.
- `Luka`: red vial, Dijkstra y pruebas de rutas.
- `main`: integración de los módulos.

La documentación técnica, las justificaciones de los algoritmos y el análisis de complejidad se encuentran desarrollados en el informe parcial.
