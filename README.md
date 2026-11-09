
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

El sistema muestra las ubicaciones recorridas, el tiempo total y las principales decisiones del cálculo.

También permite habilitar y deshabilitar conexiones para volver a calcular las rutas.

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

El vehículo sale de un depósito, visita los centros seleccionados y regresa al punto de partida.

El algoritmo prueba diferentes órdenes de entrega y conserva el recorrido de menor tiempo.

Se pueden seleccionar hasta ocho centros, siempre que estén disponibles en la red.

### 6. B3 — Rutas alternativas con restricciones

Utilizamos **Backtracking** para encontrar todos los caminos válidos entre dos ubicaciones.

El usuario puede establecer un tiempo máximo y seleccionar ubicaciones prohibidas.

Los recorridos no pueden repetir ubicaciones ni utilizar conexiones deshabilitadas.

## Organización del proyecto

```text
TPO-PROGRAMACION-III/
│
├── datos/
│   ├── ubicaciones.txt
│   └── conexiones.txt
│
├── src/
│   └── distribucion/
│       │
│       ├── modelo/
│       │   ├── Ubicacion.java
│       │   ├── TipoUbicacion.java
│       │   ├── ConexionVial.java
│       │   ├── Pedido.java
│       │   ├── Vehiculo.java
│       │   └── LoteMercaderia.java
│       │
│       ├── algoritmos/
│       │   ├── MergeSortPedidos.java
│       │   ├── BusquedaBinariaPedidos.java
│       │   ├── Dijkstra.java
│       │   ├── FloydWarshall.java
│       │   ├── SeleccionCargas.java
│       │   ├── RecorridoMultiplesEntregas.java
│       │   └── RutasAlternativas.java
│       │
│       ├── servicios/
│       │   └── GestorPedidos.java
│       │
│       ├── rutas/
│       │   ├── RedVial.java
│       │   └── ResultadoRuta.java
│       │
│       ├── persistencia/
│       │   └── LectorDatos.java
│       │
│       ├── pruebas/
│       │   ├── PruebaPedidos.java
│       │   ├── PruebaCargas.java
│       │   ├── PruebaRutas.java
│       │   ├── PruebaFloydWarshall.java
│       │   ├── PruebaRecorridoMultiples.java
│       │   ├── PruebaRutasAlternativas.java
│       │   └── TestAlgoritmos.java
│       │
│       └── vista/
│           ├── Main.java
│           ├── VentanaPrincipal.java
│           ├── PanelRutas.java
│           └── PanelGrafo.java
│
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

5. Ejecutar la clase:

```text
distribucion.vista.Main
```

Desde el menú de consola se pueden utilizar las seis funcionalidades.

### Interfaz gráfica

También se desarrolló una interfaz con Java Swing.

Para abrirla, ejecutar:

```text
distribucion.vista.VentanaPrincipal
```

Esta interfaz permite consultar rutas mediante Dijkstra y Floyd–Warshall y visualizar la red vial mediante un grafo.

Las demás funcionalidades se encuentran disponibles desde el menú de consola.

No se requieren frameworks ni dependencias externas.

## Pruebas

El proyecto incluye clases de prueba para cada funcionalidad.

Para ejecutarlas, abrir IntelliJ y ejecutar el método `main()` de cada una:

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

## Documentación

El informe final desarrolla las seis funcionalidades, las estrategias utilizadas, sus complejidades, las alternativas consideradas y los resultados de las pruebas.

También incluye las correcciones realizadas a partir de la devolución de la primera entrega.

## Repositorio

https://github.com/eovelar/TPO-PROGRAMACION-III
