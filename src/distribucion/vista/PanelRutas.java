package distribucion.vista;

import distribucion.algoritmos.Dijkstra;
import distribucion.algoritmos.FloydWarshall;
import distribucion.algoritmos.RecorridoMultiplesEntregas;
import distribucion.algoritmos.RutasAlternativas;
import distribucion.modelo.ConexionVial;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;
import distribucion.vista.Tema.Boton;
import distribucion.vista.Tema.Insignia;
import distribucion.vista.Tema.Interruptor;
import distribucion.vista.Tema.Segmentado;
import distribucion.vista.Tema.Tarjeta;
import distribucion.vista.Tema.Variante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PanelRutas extends JPanel {

    private static final int RUTA = 0;
    private static final int ENTREGAS = 1;
    private static final int ALTERNATIVAS = 2;
    private static final int RED = 3;

    private final RedVial red;
    private Dijkstra dijkstra;
    private FloydWarshall floyd;

    private PanelGrafo grafo;
    private int pestana = RUTA;
    private Runnable alCambiarRed;

    // ruta mas rapida
    private JComboBox<Ubicacion> comboOrigen;
    private JComboBox<Ubicacion> comboDestino;
    private Segmentado selectorAlgoritmo;
    private boolean proximoClickEsOrigen = true;
    private List<Ubicacion> rutaActual = new ArrayList<>();

    // resultado
    private Insignia estado;
    private JLabel detalleResultado;
    private JLabel tiempoResultado;
    private JPanel chipsRecorrido;
    private JComponent franjaResultado;

    // multiples entregas
    private JComboBox<Ubicacion> comboDeposito;
    private final Map<Integer, JCheckBox> casillasCentros = new LinkedHashMap<>();
    private JLabel contadorCentros;
    private JTextArea salidaEntregas;
    private int maximoCentros;

    // rutas alternativas
    private JComboBox<Ubicacion> altOrigen;
    private JComboBox<Ubicacion> altDestino;
    private JSpinner tiempoMaximo;
    private final Map<Integer, JCheckBox> casillasProhibidas = new LinkedHashMap<>();
    private JTextArea salidaAlternativas;

    // conexiones
    private final Map<ConexionVial, Interruptor> interruptores = new LinkedHashMap<>();
    private JLabel resumenConexiones;

    public PanelRutas(RedVial red) {
        this.red = red;
        setOpaque(false);
        setLayout(new BorderLayout(20, 0));

        if (red == null) {
            add(PanelInicio.sinDatos(), BorderLayout.CENTER);
            return;
        }

        dijkstra = new Dijkstra(red);
        floyd = new FloydWarshall(red);
        floyd.calcular();

        add(crearZonaGrafo(), BorderLayout.CENTER);
        add(crearPanelLateral(), BorderLayout.EAST);

        aplicarEstadoGrafo();
    }

    // avisa a la ventana cuando se habilita o deshabilita una conexion
    public void alCambiarRed(Runnable accion) {
        alCambiarRed = accion;
    }

    // ================= zona del grafo =================

    private JComponent crearZonaGrafo() {
        Insignia tamanoRed = new Insignia(
                red.getUbicaciones().size() + " nodos · " + red.getConexiones().size() + " conexiones",
                Tema.TEXTO_SUAVE, Tema.LINEA);

        Tarjeta tarjeta = new Tarjeta("Red vial", "Depositos, centros comunitarios y tiempos de viaje", tamanoRed);

        grafo = new PanelGrafo(red);
        grafo.setOyente(new PanelGrafo.Oyente() {
            @Override
            public void nodoSeleccionado(Ubicacion ubicacion) {
                clickEnNodo(ubicacion);
            }

            @Override
            public void conexionSeleccionada(ConexionVial conexion) {
                cambiarConexion(conexion);
            }
        });

        tarjeta.add(grafo, BorderLayout.CENTER);
        franjaResultado = crearResultado();
        tarjeta.add(franjaResultado, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JComponent crearResultado() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.LINEA),
                new EmptyBorder(16, 0, 0, 0)));

        Tarjeta tiempo = new Tarjeta();
        tiempo.setColores(Tema.TURQUESA_SUAVE, null);
        tiempo.setRadio(12);
        tiempo.setBorder(new EmptyBorder(10, 16, 10, 16));
        tiempo.setLayout(new GridLayout(2, 1, 0, 2));
        tiempo.setPreferredSize(new Dimension(128, 64));

        JLabel etiquetaTiempo = Tema.etiqueta("TIEMPO TOTAL");
        etiquetaTiempo.setForeground(Tema.TURQUESA_OSCURO);
        tiempoResultado = Tema.titulo("--", 22);
        tiempoResultado.setForeground(Tema.TURQUESA_OSCURO);
        tiempo.add(etiquetaTiempo);
        tiempo.add(tiempoResultado);

        JPanel lineaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lineaEstado.setOpaque(false);
        estado = new Insignia("SIN CONSULTAR", Tema.TEXTO_SUAVE, Tema.LINEA);
        detalleResultado = Tema.texto("Elija origen y destino, o haga click en dos nodos del grafo.");
        detalleResultado.setBorder(new EmptyBorder(0, 10, 0, 0));
        lineaEstado.add(estado);
        lineaEstado.add(detalleResultado);

        chipsRecorrido = new JPanel(new Tema.FlujoLayout(FlowLayout.LEFT, 4, 4));
        chipsRecorrido.setOpaque(false);

        JPanel textos = new JPanel(new BorderLayout(0, 6));
        textos.setOpaque(false);
        textos.add(lineaEstado, BorderLayout.NORTH);
        textos.add(chipsRecorrido, BorderLayout.CENTER);

        panel.add(tiempo, BorderLayout.WEST);
        panel.add(textos, BorderLayout.CENTER);
        return panel;
    }

    // ================= panel lateral =================

    private JComponent crearPanelLateral() {
        JPanel lateral = new JPanel(new BorderLayout(0, 14));
        lateral.setOpaque(false);
        lateral.setPreferredSize(new Dimension(330, 10));

        Segmentado pestanas = new Segmentado("Ruta", "Entregas", "Alternativas", "Red");

        CardLayout cartas = new CardLayout();
        JPanel contenedor = new JPanel(cartas);
        contenedor.setOpaque(false);
        contenedor.add(Tema.scrollVertical(crearPestanaRuta()), "0");
        contenedor.add(Tema.scrollVertical(crearPestanaEntregas()), "1");
        contenedor.add(Tema.scrollVertical(crearPestanaAlternativas()), "2");
        contenedor.add(Tema.scrollVertical(crearPestanaRed()), "3");

        pestanas.alCambiar(indice -> {
            pestana = indice;
            cartas.show(contenedor, String.valueOf(indice));
            aplicarEstadoGrafo();
        });

        lateral.add(pestanas, BorderLayout.NORTH);
        lateral.add(contenedor, BorderLayout.CENTER);
        return lateral;
    }

    private JComponent crearPestanaRuta() {
        Tarjeta tarjeta = new Tarjeta("Ruta mas rapida", "Camino de menor tiempo entre dos puntos");

        comboOrigen = comboUbicaciones(red.getUbicaciones());
        comboDestino = comboUbicaciones(red.getUbicaciones());
        if (comboDestino.getItemCount() > 1) {
            comboDestino.setSelectedIndex(1);
        }
        comboOrigen.addActionListener(e -> aplicarEstadoGrafo());
        comboDestino.addActionListener(e -> aplicarEstadoGrafo());

        Boton invertir = new Boton("Invertir", Icono.Tipo.INVERTIR, Variante.FANTASMA);
        invertir.setFont(Tema.fuente(Font.BOLD, 12));
        invertir.setBorder(new EmptyBorder(6, 12, 6, 12));
        invertir.addActionListener(e -> {
            Object origen = comboOrigen.getSelectedItem();
            comboOrigen.setSelectedItem(comboDestino.getSelectedItem());
            comboDestino.setSelectedItem(origen);
        });

        JPanel filaInvertir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaInvertir.setOpaque(false);
        filaInvertir.add(invertir);

        selectorAlgoritmo = new Segmentado("Dijkstra", "Floyd-Warshall");

        Boton calcular = new Boton("Calcular ruta", Icono.Tipo.RUTAS, Variante.PRIMARIO);
        calcular.addActionListener(e -> calcularRuta());

        JLabel nota = Tema.parrafo(
                "Dijkstra calcula el camino en el momento. Floyd-Warshall consulta la tabla "
                        + "de todos los pares, que se calcula una sola vez al iniciar.", 225);

        tarjeta.add(Tema.formulario(14,
                Tema.campo("ORIGEN", comboOrigen),
                Tema.campo("DESTINO", comboDestino),
                filaInvertir,
                Tema.campo("ALGORITMO", selectorAlgoritmo),
                calcular,
                nota), BorderLayout.CENTER);

        return tarjeta;
    }

    private JComponent crearPestanaEntregas() {
        Tarjeta tarjeta = new Tarjeta("Multiples entregas", "Mejor orden para visitar varios centros");

        List<Ubicacion> depositos = new ArrayList<>();
        List<Ubicacion> centros = new ArrayList<>();
        for (Ubicacion u : red.getUbicaciones()) {
            if (u.getTipo() == TipoUbicacion.DEPOSITO) {
                depositos.add(u);
            } else if (u.getTipo() == TipoUbicacion.CENTRO_COMUNITARIO) {
                centros.add(u);
            }
        }

        maximoCentros = Math.min(8, centros.size());

        comboDeposito = comboUbicaciones(depositos);
        comboDeposito.addActionListener(e -> aplicarEstadoGrafo());

        contadorCentros = Tema.etiqueta("0 de " + maximoCentros);

        JPanel encabezadoCentros = new JPanel(new BorderLayout());
        encabezadoCentros.setOpaque(false);
        encabezadoCentros.add(Tema.etiqueta("CENTROS A VISITAR"), BorderLayout.WEST);
        encabezadoCentros.add(contadorCentros, BorderLayout.EAST);

        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 2));
        lista.setOpaque(false);
        for (Ubicacion centro : centros) {
            JCheckBox casilla = Tema.casilla(centro.getId() + "  " + centro.getNombre());
            casilla.addActionListener(e -> actualizarCentros());
            casillasCentros.put(centro.getId(), casilla);
            lista.add(casilla);
        }

        Boton calcular = new Boton("Calcular recorrido", Icono.Tipo.CAMION, Variante.PRIMARIO);
        calcular.addActionListener(e -> calcularEntregas());

        salidaEntregas = Tema.consola("Elija un deposito y los centros a visitar.");
        salidaEntregas.setRows(6);

        tarjeta.add(Tema.formulario(14,
                Tema.campo("DEPOSITO DE SALIDA", comboDeposito),
                encabezadoCentros,
                lista,
                calcular,
                Tema.marcoConsola(salidaEntregas)), BorderLayout.CENTER);

        if (depositos.isEmpty() || centros.isEmpty()) {
            calcular.setEnabled(false);
            salidaEntregas.setText("La red necesita al menos un deposito y un centro comunitario.");
        }

        return tarjeta;
    }

    private JComponent crearPestanaAlternativas() {
        Tarjeta tarjeta = new Tarjeta("Rutas alternativas", "Con tiempo maximo y puntos a evitar");

        altOrigen = comboUbicaciones(red.getUbicaciones());
        altDestino = comboUbicaciones(red.getUbicaciones());
        if (altDestino.getItemCount() > 1) {
            altDestino.setSelectedIndex(altDestino.getItemCount() - 1);
        }
        altOrigen.addActionListener(e -> aplicarEstadoGrafo());
        altDestino.addActionListener(e -> aplicarEstadoGrafo());

        tiempoMaximo = Tema.spinner(60, 0, 100000, 5);

        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 2));
        lista.setOpaque(false);
        for (Ubicacion u : red.getUbicaciones()) {
            JCheckBox casilla = Tema.casilla(u.getId() + "  " + u.getNombre());
            casilla.addActionListener(e -> aplicarEstadoGrafo());
            casillasProhibidas.put(u.getId(), casilla);
            lista.add(casilla);
        }

        Boton buscar = new Boton("Buscar rutas", Icono.Tipo.BUSCAR, Variante.PRIMARIO);
        buscar.addActionListener(e -> buscarAlternativas());

        salidaAlternativas = Tema.consola("Defina las restricciones y busque las rutas posibles.");
        salidaAlternativas.setRows(6);

        tarjeta.add(Tema.formulario(14,
                Tema.campo("ORIGEN", altOrigen),
                Tema.campo("DESTINO", altDestino),
                Tema.campo("TIEMPO MAXIMO (MIN)", tiempoMaximo),
                Tema.campo("UBICACIONES A EVITAR", lista),
                buscar,
                Tema.marcoConsola(salidaAlternativas)), BorderLayout.CENTER);

        return tarjeta;
    }

    private JComponent crearPestanaRed() {
        resumenConexiones = new JLabel();
        Tarjeta tarjeta = new Tarjeta("Conexiones", "Habilite o deshabilite tramos de la red");

        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 0));
        lista.setOpaque(false);

        for (ConexionVial conexion : red.getConexiones()) {
            JPanel fila = new JPanel(new BorderLayout(10, 0));
            fila.setOpaque(false);
            fila.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.LINEA),
                    new EmptyBorder(9, 0, 9, 2)));

            JLabel nombres = new JLabel(conexion.getOrigen().getNombre() + "  →  "
                    + conexion.getDestino().getNombre());
            nombres.setFont(Tema.fuente(Font.BOLD, 12.5f));
            nombres.setForeground(Tema.TEXTO);

            JLabel detalle = Tema.texto(conexion.getOrigen().getId() + " → "
                    + conexion.getDestino().getId() + "  ·  " + conexion.getTiempoMinutos() + " min");
            detalle.setFont(Tema.fuente(Font.PLAIN, 11.5f));

            JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
            textos.setOpaque(false);
            textos.add(nombres);
            textos.add(detalle);

            Interruptor interruptor = new Interruptor(conexion.isHabilitada());
            interruptor.alPresionar(() -> cambiarConexion(conexion));
            interruptores.put(conexion, interruptor);

            fila.add(textos, BorderLayout.CENTER);
            fila.add(interruptor, BorderLayout.EAST);
            lista.add(fila);
        }

        resumenConexiones.setFont(Tema.fuente(Font.BOLD, 12));
        actualizarResumenConexiones();

        tarjeta.add(Tema.formulario(10,
                resumenConexiones,
                lista,
                Tema.parrafo("Tambien puede hacer click sobre una conexion del grafo mientras "
                        + "esta pestana esta abierta.", 225)), BorderLayout.CENTER);

        return tarjeta;
    }

    // ================= estado del grafo segun la pestana =================

    private void aplicarEstadoGrafo() {
        if (grafo == null) {
            return;
        }

        grafo.setConexionesEditables(pestana == RED);

        // el resultado de la ruta mas rapida solo tiene sentido en su pestana
        if (franjaResultado.isVisible() != (pestana == RUTA)) {
            franjaResultado.setVisible(pestana == RUTA);
            franjaResultado.getParent().revalidate();
        }
        grafo.setMarcados(new ArrayList<>());
        grafo.setProhibidos(new ArrayList<>());
        grafo.setExtremos(-1, -1);
        grafo.limpiarRecorrido();

        switch (pestana) {
            case RUTA:
                int origen = idSeleccionado(comboOrigen);
                int destino = idSeleccionado(comboDestino);
                grafo.setExtremos(origen, destino);

                // la ruta calculada solo se muestra si sigue coincidiendo con la seleccion
                if (!rutaActual.isEmpty()
                        && rutaActual.get(0).getId() == origen
                        && rutaActual.get(rutaActual.size() - 1).getId() == destino) {
                    grafo.mostrarRecorrido(rutaActual);
                }
                grafo.setAyuda(proximoClickEsOrigen
                        ? "Click en un nodo para elegir el origen"
                        : "Ahora haga click en el destino");
                break;

            case ENTREGAS:
                Set<Integer> marcados = new HashSet<>(seleccionados(casillasCentros));
                if (comboDeposito.getSelectedItem() != null) {
                    marcados.add(idSeleccionado(comboDeposito));
                }
                grafo.setMarcados(marcados);
                grafo.setAyuda("Click en un deposito para salir desde ahi, o en un centro para sumarlo");
                break;

            case ALTERNATIVAS:
                grafo.setExtremos(idSeleccionado(altOrigen), idSeleccionado(altDestino));
                grafo.setProhibidos(seleccionados(casillasProhibidas));
                grafo.setAyuda("Click en un nodo para evitarlo en la busqueda");
                break;

            default:
                grafo.setAyuda("Click en una conexion para habilitarla o deshabilitarla");
                break;
        }
    }

    private void clickEnNodo(Ubicacion ubicacion) {
        if (ubicacion == null) {
            return;
        }

        switch (pestana) {
            case RUTA:
                if (proximoClickEsOrigen) {
                    comboOrigen.setSelectedItem(ubicacion);
                    proximoClickEsOrigen = false;
                    aplicarEstadoGrafo();
                } else {
                    comboDestino.setSelectedItem(ubicacion);
                    proximoClickEsOrigen = true;
                    calcularRuta();
                }
                break;

            case ENTREGAS:
                if (ubicacion.getTipo() == TipoUbicacion.DEPOSITO) {
                    comboDeposito.setSelectedItem(ubicacion);
                } else {
                    JCheckBox casilla = casillasCentros.get(ubicacion.getId());
                    if (casilla != null && casilla.isEnabled()) {
                        casilla.setSelected(!casilla.isSelected());
                        actualizarCentros();
                    }
                }
                break;

            case ALTERNATIVAS:
                JCheckBox casilla = casillasProhibidas.get(ubicacion.getId());
                if (casilla != null) {
                    casilla.setSelected(!casilla.isSelected());
                    aplicarEstadoGrafo();
                }
                break;

            default:
                break;
        }
    }

    // ================= ruta mas rapida =================

    private void calcularRuta() {
        Ubicacion origen = (Ubicacion) comboOrigen.getSelectedItem();
        Ubicacion destino = (Ubicacion) comboDestino.getSelectedItem();

        if (origen == null || destino == null) {
            mostrarError("Seleccione un origen y un destino.");
            return;
        }

        if (origen.getId() == destino.getId()) {
            mostrarError("El origen y el destino son la misma ubicacion.");
            return;
        }

        boolean usarDijkstra = selectorAlgoritmo.getSeleccion() == 0;

        long inicio = System.nanoTime();
        ResultadoRuta resultado = usarDijkstra
                ? dijkstra.calcularRuta(origen, destino)
                : floyd.consultar(origen.getId(), destino.getId());
        double milisegundos = (System.nanoTime() - inicio) / 1_000_000.0;

        mostrarResultado(usarDijkstra ? "Dijkstra" : "Floyd-Warshall", milisegundos, resultado);
    }

    private void mostrarResultado(String algoritmo, double milisegundos, ResultadoRuta resultado) {
        String detalle = algoritmo + "  ·  " + String.format(Locale.US, "%.3f ms", milisegundos);
        chipsRecorrido.removeAll();

        if (!resultado.isExisteRuta()) {
            estado.cambiar("SIN RUTA", Tema.ROJO, Tema.ROJO_SUAVE);
            detalleResultado.setText(detalle + "  ·  No hay camino entre esas ubicaciones.");
            tiempoResultado.setText("--");
            rutaActual = new ArrayList<>();
            grafo.limpiarRecorrido();
            refrescar(chipsRecorrido);
            return;
        }

        estado.cambiar("RUTA ENCONTRADA", Tema.TURQUESA_OSCURO, Tema.TURQUESA_SUAVE);
        detalleResultado.setText(detalle);
        tiempoResultado.setText(resultado.getTiempoTotal() + " min");

        List<Ubicacion> recorrido = resultado.getRecorrido();
        for (int i = 0; i < recorrido.size(); i++) {
            Color fondo = i == 0 ? Tema.NAVY_2 : i == recorrido.size() - 1 ? Tema.TURQUESA : Tema.LINEA;
            Color frente = i == 0 || i == recorrido.size() - 1 ? Color.WHITE : Tema.TEXTO;
            chipsRecorrido.add(new Insignia(recorrido.get(i).getNombre(), frente, fondo));

            if (i < recorrido.size() - 1) {
                chipsRecorrido.add(new JLabel(new Icono(Icono.Tipo.FLECHA, 14, Tema.TEXTO_TENUE)));
            }
        }

        rutaActual = new ArrayList<>(recorrido);
        grafo.mostrarRecorrido(rutaActual);
        refrescar(chipsRecorrido);
    }

    private void mostrarError(String mensaje) {
        estado.cambiar("REVISAR", Tema.AMBAR, Tema.AMBAR_SUAVE);
        detalleResultado.setText(mensaje);
        tiempoResultado.setText("--");
        chipsRecorrido.removeAll();
        refrescar(chipsRecorrido);
        rutaActual = new ArrayList<>();
        grafo.limpiarRecorrido();
    }

    // ================= multiples entregas =================

    private void actualizarCentros() {
        List<Integer> elegidos = seleccionados(casillasCentros);
        boolean lleno = elegidos.size() >= maximoCentros;

        for (JCheckBox casilla : casillasCentros.values()) {
            casilla.setEnabled(casilla.isSelected() || !lleno);
        }

        contadorCentros.setText(elegidos.size() + " de " + maximoCentros);
        contadorCentros.setForeground(lleno ? Tema.AMBAR : Tema.TEXTO_SUAVE);
        aplicarEstadoGrafo();
    }

    private void calcularEntregas() {
        Ubicacion deposito = (Ubicacion) comboDeposito.getSelectedItem();
        List<Ubicacion> centros = new ArrayList<>();

        for (Integer id : seleccionados(casillasCentros)) {
            centros.add(red.buscarUbicacionPorId(id));
        }

        if (deposito == null || centros.isEmpty()) {
            salidaEntregas.setText("Seleccione un deposito y al menos un centro.");
            return;
        }

        try {
            salidaEntregas.setText(Tema.capturarSalida(
                    () -> new RecorridoMultiplesEntregas(floyd).calcular(deposito, centros)));
        } catch (IllegalArgumentException e) {
            salidaEntregas.setText("error: " + e.getMessage());
        }
        salidaEntregas.setCaretPosition(0);
    }

    // ================= rutas alternativas =================

    private void buscarAlternativas() {
        Ubicacion origen = (Ubicacion) altOrigen.getSelectedItem();
        Ubicacion destino = (Ubicacion) altDestino.getSelectedItem();

        if (origen == null || destino == null) {
            salidaAlternativas.setText("Seleccione un origen y un destino.");
            return;
        }

        int maximo = (Integer) tiempoMaximo.getValue();
        Set<Integer> prohibidas = new HashSet<>(seleccionados(casillasProhibidas));

        try {
            salidaAlternativas.setText(Tema.capturarSalida(
                    () -> new RutasAlternativas(red).buscar(origen.getId(), destino.getId(), maximo, prohibidas)));
        } catch (IllegalArgumentException e) {
            salidaAlternativas.setText("error: " + e.getMessage());
        }
        salidaAlternativas.setCaretPosition(0);
    }

    // ================= conexiones =================

    private void cambiarConexion(ConexionVial conexion) {
        int desde = conexion.getOrigen().getId();
        int hacia = conexion.getDestino().getId();

        boolean cambio = conexion.isHabilitada()
                ? red.deshabilitarConexion(desde, hacia)
                : red.habilitarConexion(desde, hacia);

        if (!cambio) {
            return;
        }

        // floyd guarda la tabla de distancias, hay que recalcularla
        floyd.calcular();

        for (Map.Entry<ConexionVial, Interruptor> entrada : interruptores.entrySet()) {
            entrada.getValue().setEncendido(entrada.getKey().isHabilitada());
        }
        actualizarResumenConexiones();

        if (!rutaActual.isEmpty()) {
            rutaActual = new ArrayList<>();
            estado.cambiar("RED MODIFICADA", Tema.AMBAR, Tema.AMBAR_SUAVE);
            detalleResultado.setText("Cambio una conexion. Vuelva a calcular la ruta.");
            tiempoResultado.setText("--");
            chipsRecorrido.removeAll();
            refrescar(chipsRecorrido);
        }

        aplicarEstadoGrafo();

        if (alCambiarRed != null) {
            alCambiarRed.run();
        }
    }

    private void actualizarResumenConexiones() {
        int habilitadas = 0;
        for (ConexionVial conexion : red.getConexiones()) {
            if (conexion.isHabilitada()) {
                habilitadas++;
            }
        }
        int total = red.getConexiones().size();
        resumenConexiones.setText(habilitadas + " de " + total + " conexiones habilitadas");
        resumenConexiones.setForeground(habilitadas == total ? Tema.TURQUESA_OSCURO : Tema.AMBAR);
    }

    // ================= utilidades =================

    private JComboBox<Ubicacion> comboUbicaciones(List<Ubicacion> ubicaciones) {
        JComboBox<Ubicacion> combo = Tema.combo(valor -> {
            Ubicacion u = (Ubicacion) valor;
            return u.getId() + "  ·  " + u.getNombre();
        });
        for (Ubicacion u : ubicaciones) {
            combo.addItem(u);
        }
        return combo;
    }

    private static int idSeleccionado(JComboBox<Ubicacion> combo) {
        Ubicacion u = (Ubicacion) combo.getSelectedItem();
        return u == null ? -1 : u.getId();
    }

    private static List<Integer> seleccionados(Map<Integer, JCheckBox> casillas) {
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, JCheckBox> entrada : casillas.entrySet()) {
            if (entrada.getValue().isSelected()) {
                ids.add(entrada.getKey());
            }
        }
        return ids;
    }

    private static void refrescar(JComponent componente) {
        componente.revalidate();
        componente.repaint();
    }
}
