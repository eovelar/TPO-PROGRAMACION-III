# TPO Programación III — Planificación de rutas y distribución de ayuda

**Integrantes:** Evangelina Ovelar y Luka Peralta  
**Materia:** Programación III  
**Propuesta elegida:** 2 — Planificación de rutas y distribución de ayuda

## Descripción del proyecto

Desarrollamos un sistema en Java para organizar la distribución de ayuda a distintos centros comunitarios. El programa permite registrar y consultar pedidos, calcular rutas entre ubicaciones y seleccionar mercadería según la capacidad de los vehículos.

Trabajamos con depósitos, centros comunitarios, conexiones viales, pedidos, vehículos y lotes de mercadería.

El proyecto incluye las tres funcionalidades obligatorias de la primera entrega y las tres adicionales elegidas para la entrega final.

## Funcionalidades

### 1. Organización y consulta de pedidos

Permite registrar pedidos, ordenarlos por fecha límite o prioridad y buscarlos por ID.

Utilizamos:

- **Merge Sort:** ordenamiento mediante Divide y Conquista.
- **Búsqueda binaria:** búsqueda por identificador sobre los pedidos ordenados.

En caso de empate de prioridad, se ordena por fecha límite y luego por ID.

### 2. Cálculo de la ruta más rápida

Permite encontrar el recorrido de menor tiempo entre dos ubicaciones de la red vial.

Utilizamos **Dijkstra (Greedy)**.

El sistema muestra las ubicaciones recorridas, el tiempo total y las principales decisiones del cálculo. También permite habilitar y deshabilitar conexiones para volver a calcular las rutas.

### 3. Selección de cargas prioritarias

Permite elegir los lotes de mercadería que maximizan la prioridad total sin superar la capacidad del vehículo.

Utilizamos **Mochila 0/1 con Programación Dinámica**.

Cada lote es indivisible y puede seleccionarse una sola vez. El programa muestra los lotes elegidos, el peso utilizado, la prioridad acumulada y la capacidad restante.

### 4. A1 — Consultas de rutas entre todos los centros

Utilizamos **Floyd–Warshall**, mediante Programación Dinámica.

Se calculan las distancias mínimas entre todos los pares de ubicaciones y se guardan los datos necesarios para reconstruir los recorridos.

Después del cálculo inicial se pueden realizar distintas consultas sin volver a procesar toda la red. Si cambian las conexiones, se recalculan las matrices.

### 5. B1 — Recorrido con múltiples entregas

Utilizamos **Backtracking** junto con las distancias calculadas por Floyd–Warshall.

El vehículo sale de un depósito, visita los centros seleccionados y regresa al punto de partida. El algoritmo prueba diferentes órdenes de entrega y conserva el recorrido de menor tiempo.

Se pueden seleccionar hasta ocho centros, siempre que estén disponibles en la red.

### 6. B3 — Rutas alternativas con restricciones

Utilizamos **Backtracking** para encontrar todos los caminos válidos entre dos ubicaciones.

El usuario puede establecer un tiempo máximo y seleccionar ubicaciones prohibidas. Los recorridos no pueden repetir ubicaciones ni utilizar conexiones deshabilitadas.

## Organización del proyecto

El código está organizado en paquetes según la responsabilidad de cada clase, separando los algoritmos, los datos y las interfaces.

```text
TPO-PROGRAMACION-III/
│
├── datos/
│   ├── ubicaciones.txt
│   └── conexiones.txt
│
├── src/
│   └── distribucion/
│       ├── modelo/
│       │   ├── Ubicacion.java
│       │   ├── TipoUbicacion.java
│       │   ├── ConexionVial.java
│       │   ├── Pedido.java
│       │   ├── Vehiculo.java
│       │   └── LoteMercaderia.java
│       ├── algoritmos/
│       │   ├── MergeSortPedidos.java
│       │   ├── BusquedaBinariaPedidos.java
│       │   ├── Dijkstra.java
│       │   ├── FloydWarshall.java
│       │   ├── SeleccionCargas.java
│       │   ├── RecorridoMultiplesEntregas.java
│       │   └── RutasAlternativas.java
│       ├── servicios/
│       │   └── GestorPedidos.java
│       ├── rutas/
│       │   ├── RedVial.java
│       │   └── ResultadoRuta.java
│       ├── persistencia/
│       │   └── LectorDatos.java
│       ├── pruebas/
│       │   ├── PruebaPedidos.java
│       │   ├── PruebaCargas.java
│       │   ├── PruebaRutas.java
│       │   ├── PruebaFloydWarshall.java
│       │   ├── PruebaRecorridoMultiples.java
│       │   ├── PruebaRutasAlternativas.java
│       │   └── TestAlgoritmos.java
│       └── vista/
│           ├── Main.java
│           ├── VentanaPrincipal.java
│           ├── PanelPedidos.java
│           ├── PanelRutas.java
│           ├── PanelGrafo.java
│           ├── PanelCargas.java
│           └── Tema.java
│
├── TPO Programación III - INFORME FINAL.pdf
└── README.md
```

## Archivos de datos

La red vial se carga desde archivos TXT ubicados en la carpeta `datos`.

**ubicaciones.txt**

Formato:

```text
id;nombre;tipo
```

Ejemplo:

```text
1;Deposito Central;DEPOSITO
2;Centro Norte;CENTRO_COMUNITARIO
3;Centro Sur;CENTRO_COMUNITARIO
4;Centro Este;CENTRO_COMUNITARIO
5;Deposito Secundario;DEPOSITO
```

**conexiones.txt**

Formato:

```text
idOrigen;idDestino;tiempoMinutos
```

Ejemplo:

```text
1;2;10
1;3;15
2;4;5
2;5;12
3;4;8
2;3;20
4;1;18
3;1;15
2;1;10
```

Las conexiones son dirigidas. Una conexión desde A hacia B no implica que exista una conexión de B hacia A.

Los archivos se pueden modificar para probar otras redes sin recompilar el programa.

## Cómo ejecutar el proyecto

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/eovelar/TPO-PROGRAMACION-III.git
   ```

2. Abrir la carpeta del proyecto en IntelliJ IDEA.
3. Configurar un JDK compatible con el proyecto. Se utilizó OpenJDK 27 durante las últimas pruebas.
4. Verificar que la carpeta `datos` esté en la raíz del proyecto y que el directorio de trabajo de la configuración de ejecución sea esa raíz.

### Ejecución desde consola

Ejecutar la clase:

```text
distribucion.vista.Main
```

Desde el menú de consola se pueden utilizar las seis funcionalidades del sistema.

### Interfaz gráfica — NEXO

También desarrollamos una interfaz gráfica con Java Swing.

Para abrirla, ejecutar:

```text
distribucion.vista.VentanaPrincipal
```

La interfaz cuenta con un menú lateral y cuatro secciones principales:

- **Panel general:** presentación del sistema y sus funcionalidades.
- **Gestión de pedidos:** registro, ordenamiento por fecha o prioridad y búsqueda por ID.
- **Planificación de rutas:** consultas mediante Dijkstra y Floyd–Warshall, con visualización de la red vial.
- **Selección de cargas:** ingreso de lotes y cálculo de la mejor combinación mediante Mochila 0/1.

La interfaz utiliza las clases y algoritmos del proyecto, manteniendo separada la lógica de la presentación. Las funcionalidades adicionales B1 y B3 se ejecutan desde el menú de consola.

No se requieren frameworks ni dependencias externas.

## Pruebas

El proyecto incluye clases de prueba para cada funcionalidad. Para ejecutarlas, abrir IntelliJ IDEA y ejecutar el método `main()` de cada una.

| Clase | Pruebas correctas |
|---|---:|
| PruebaPedidos | 19 |
| PruebaCargas | 10 |
| PruebaRutas | 10 |
| PruebaFloydWarshall | 10 |
| PruebaRecorridoMultiples | 16 |
| PruebaRutasAlternativas | 16 |
| TestAlgoritmos | 10 |
| **Total** | **91** |

En las últimas ejecuciones registradas, las 91 comprobaciones finalizaron correctamente y ninguna falló.

Se probaron casos habituales, entradas inválidas, empates, restricciones, ausencia de soluciones y modificaciones de las conexiones.

## Estrategias utilizadas

| Estrategia | Algoritmo |
|---|---|
| Divide y Conquista | Merge Sort |
| Greedy | Dijkstra |
| Programación Dinámica | Mochila 0/1 y Floyd–Warshall |
| Backtracking | Recorrido con múltiples entregas y rutas alternativas |

La búsqueda binaria se utiliza para consultar pedidos previamente ordenados por ID.

## Documentación

El informe final incluye la explicación de las seis funcionalidades, la justificación de los algoritmos, sus complejidades, las alternativas consideradas y los resultados de las pruebas.

También documenta la estructura del proyecto y las correcciones realizadas a partir de la devolución de la primera entrega.

**Informe final:** [TPO Programación III - INFORME FINAL.pdf](TPO%20Programaci%C3%B3n%20III%20-%20INFORME%20FINAL.pdf)

## Repositorio

https://github.com/eovelar/TPO-PROGRAMACION-III
