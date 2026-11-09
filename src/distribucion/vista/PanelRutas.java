
package distribucion.vista;

import distribucion.algoritmos.Dijkstra;
import distribucion.algoritmos.FloydWarshall;
import distribucion.modelo.Ubicacion;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;
import distribucion.rutas.ResultadoRuta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PanelRutas extends JPanel {

    private static final Color AZUL = new Color(27, 43, 58);
    private static final Color TURQUESA = new Color(19, 157, 151);
    private static final Color FONDO = new Color(245, 248, 250);
    private static final Color GRIS = new Color(112, 125, 137);
    private static final Color BORDE = new Color(225, 232, 237);

    private JComboBox<Ubicacion> comboOrigen;
    private JComboBox<Ubicacion> comboDestino;

    private JTextArea areaResultado;
    private JLabel estadoResultado;

    private PanelGrafo panelGrafo;

    private RedVial red;
    private Dijkstra dijkstra;
    private FloydWarshall floyd;

    public PanelRutas() {
        setLayout(new BorderLayout());
        setBackground(FONDO);

        cargarDatos();
        crearInterfaz();
    }

    // cargar la red vial desde los txt
    private void cargarDatos() {
        try {
            red = LectorDatos.cargarRed(
                    "datos/ubicaciones.txt",
                    "datos/conexiones.txt"
            );

            dijkstra = new Dijkstra(red);
            floyd = new FloydWarshall(red);
            floyd.calcular();

        } catch (IOException | IllegalArgumentException e) {
            red = null;

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar la red vial:\n" + e.getMessage(),
                    "Error de carga",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // crear la interfaz de rutas
    private void crearInterfaz() {

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel formulario = crearTarjeta();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Consultar recorridos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(AZUL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "Seleccione dos ubicaciones para calcular la mejor ruta."
        );
        descripcion.setForeground(GRIS);
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboOrigen = crearSelector();
        comboDestino = crearSelector();

        if (red != null) {
            for (Ubicacion ubicacion : red.getUbicaciones()) {
                comboOrigen.addItem(ubicacion);
                comboDestino.addItem(ubicacion);
            }

            if (comboDestino.getItemCount() > 1) {
                comboDestino.setSelectedIndex(1);
            }
        }

        JPanel selectores = new JPanel(new GridLayout(1, 2, 14, 0));
        selectores.setOpaque(false);
        selectores.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectores.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 72)
        );

        selectores.add(crearCampo("ORIGEN", comboOrigen));
        selectores.add(crearCampo("DESTINO", comboDestino));

        JButton botonDijkstra = crearBoton(
                "Calcular con Dijkstra", TURQUESA
        );

        JButton botonFloyd = crearBoton(
                "Consultar Floyd-Warshall", AZUL
        );

        botonDijkstra.addActionListener(e -> calcularDijkstra());
        botonFloyd.addActionListener(e -> calcularFloyd());

        if (red == null) {
            botonDijkstra.setEnabled(false);
            botonFloyd.setEnabled(false);
        }

        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        botones.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        botones.add(botonDijkstra);
        botones.add(botonFloyd);

        formulario.add(titulo);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(descripcion);
        formulario.add(Box.createVerticalStrut(19));
        formulario.add(selectores);
        formulario.add(Box.createVerticalStrut(17));
        formulario.add(botones);

        // panel de visualizacion
        JPanel visualizacion = crearTarjeta();
        visualizacion.setLayout(new BorderLayout(0, 12));

        JLabel tituloGrafo = new JLabel("Red vial");
        tituloGrafo.setForeground(AZUL);
        tituloGrafo.setFont(new Font("SansSerif", Font.BOLD, 17));

        JLabel leyenda = new JLabel(
                "Azul: depositos     Turquesa: centros y ruta seleccionada"
        );
        leyenda.setForeground(GRIS);
        leyenda.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel encabezadoGrafo = new JPanel(new BorderLayout());
        encabezadoGrafo.setOpaque(false);
        encabezadoGrafo.add(tituloGrafo, BorderLayout.WEST);
        encabezadoGrafo.add(leyenda, BorderLayout.EAST);

        if (red != null) {
            panelGrafo = new PanelGrafo(red);
            visualizacion.add(panelGrafo, BorderLayout.CENTER);
        } else {
            visualizacion.add(
                    new JLabel("No hay una red vial disponible."),
                    BorderLayout.CENTER
            );
        }

        visualizacion.add(encabezadoGrafo, BorderLayout.NORTH);
        visualizacion.setPreferredSize(new Dimension(600, 400));

        // panel de resultados
        JPanel resultados = crearTarjeta();
        resultados.setLayout(new BorderLayout(0, 12));

        JLabel tituloResultado = new JLabel("Resultado del calculo");
        tituloResultado.setForeground(AZUL);
        tituloResultado.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );

        estadoResultado = new JLabel("SIN CONSULTAR");
        estadoResultado.setForeground(GRIS);
        estadoResultado.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        JPanel encabezadoResultado = new JPanel(new BorderLayout());
        encabezadoResultado.setOpaque(false);
        encabezadoResultado.add(
                tituloResultado, BorderLayout.WEST
        );
        encabezadoResultado.add(
                estadoResultado, BorderLayout.EAST
        );

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setFocusable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        areaResultado.setForeground(AZUL);
        areaResultado.setBackground(Color.WHITE);
        areaResultado.setText(
                "Seleccione un origen, un destino y ejecute un algoritmo."
        );

        JScrollPane scrollResultados = new JScrollPane(areaResultado);
        scrollResultados.setBorder(null);
        scrollResultados.setPreferredSize(new Dimension(500, 115));

        resultados.add(encabezadoResultado, BorderLayout.NORTH);
        resultados.add(scrollResultados, BorderLayout.CENTER);

        // organizar todos los elementos
        JPanel secciones = new JPanel();
        secciones.setLayout(new BoxLayout(secciones, BoxLayout.Y_AXIS));
        secciones.setBackground(FONDO);

        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        visualizacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultados.setAlignmentX(Component.LEFT_ALIGNMENT);

        formulario.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 245)
        );
        visualizacion.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 430)
        );
        resultados.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 200)
        );

        secciones.add(formulario);
        secciones.add(Box.createVerticalStrut(18));
        secciones.add(visualizacion);
        secciones.add(Box.createVerticalStrut(18));
        secciones.add(resultados);

        contenido.add(secciones, BorderLayout.CENTER);

        JScrollPane scrollPrincipal = new JScrollPane(contenido);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        scrollPrincipal.getViewport().setBackground(FONDO);

        add(scrollPrincipal, BorderLayout.CENTER);
    }

    // preparar un campo con etiqueta
    private JPanel crearCampo(
            String etiqueta,
            JComboBox<Ubicacion> selector) {

        JPanel campo = new JPanel(new BorderLayout(0, 7));
        campo.setOpaque(false);

        JLabel label = new JLabel(etiqueta);
        label.setForeground(GRIS);
        label.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        campo.add(label, BorderLayout.NORTH);
        campo.add(selector, BorderLayout.CENTER);

        return campo;
    }

    // mostrar nombres de ubicaciones en los selectores
    private JComboBox<Ubicacion> crearSelector() {

        JComboBox<Ubicacion> combo = new JComboBox<>();

        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(AZUL);
        combo.setFocusable(false);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean tieneFoco) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco
                );

                if (valor instanceof Ubicacion) {
                    Ubicacion ubicacion = (Ubicacion) valor;

                    setText(
                            ubicacion.getId() + " - "
                                    + ubicacion.getNombre()
                    );
                }

                return this;
            }
        });

        return combo;
    }

    // botones del panel
    private JButton crearBoton(String texto, Color color) {

        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    // estilo de tarjetas
    private JPanel crearTarjeta() {

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        new EmptyBorder(20, 22, 20, 22)
                )
        );

        return tarjeta;
    }

    // calcular ruta con dijkstra
    private void calcularDijkstra() {

        Ubicacion origen =
                (Ubicacion) comboOrigen.getSelectedItem();

        Ubicacion destino =
                (Ubicacion) comboDestino.getSelectedItem();

        if (origen == null || destino == null) {
            mostrarError("Seleccione un origen y un destino.");
            return;
        }

        ResultadoRuta resultado =
                dijkstra.calcularRuta(origen, destino);

        mostrarResultado("DIJKSTRA", resultado);
    }

    // consultar ruta con floyd-warshall
    private void calcularFloyd() {

        Ubicacion origen =
                (Ubicacion) comboOrigen.getSelectedItem();

        Ubicacion destino =
                (Ubicacion) comboDestino.getSelectedItem();

        if (origen == null || destino == null) {
            mostrarError("Seleccione un origen y un destino.");
            return;
        }

        ResultadoRuta resultado =
                floyd.consultar(
                        origen.getId(),
                        destino.getId()
                );

        mostrarResultado("FLOYD-WARSHALL", resultado);
    }

    // actualizar el texto y el grafo
    private void mostrarResultado(
            String algoritmo,
            ResultadoRuta resultado) {

        StringBuilder texto = new StringBuilder();

        texto.append("ALGORITMO UTILIZADO: ");
        texto.append(algoritmo);
        texto.append("\n\n");

        if (!resultado.isExisteRuta()) {

            estadoResultado.setText("SIN RUTA");
            estadoResultado.setForeground(
                    new Color(185, 82, 82)
            );

            texto.append(
                    "No existe un recorrido disponible entre las ubicaciones."
            );

            if (panelGrafo != null) {
                panelGrafo.limpiarRecorrido();
            }

            areaResultado.setText(texto.toString());
            return;
        }

        estadoResultado.setText("RUTA ENCONTRADA");
        estadoResultado.setForeground(TURQUESA);

        List<Ubicacion> recorrido = resultado.getRecorrido();

        texto.append("RECORRIDO:\n");

        for (int i = 0; i < recorrido.size(); i++) {
            texto.append(recorrido.get(i).getNombre());

            if (i < recorrido.size() - 1) {
                texto.append("  ->  ");
            }
        }

        texto.append("\n\nTIEMPO TOTAL: ");
        texto.append(resultado.getTiempoTotal());
        texto.append(" minutos");

        areaResultado.setText(texto.toString());
        areaResultado.setCaretPosition(0);

        // resaltar las conexiones del recorrido
        if (panelGrafo != null) {
            panelGrafo.mostrarRecorrido(recorrido);
        }
    }

    // informar errores y limpiar el grafo
    private void mostrarError(String mensaje) {

        estadoResultado.setText("ERROR");
        estadoResultado.setForeground(
                new Color(185, 82, 82)
        );

        areaResultado.setText(mensaje);

        if (panelGrafo != null) {
            panelGrafo.limpiarRecorrido();
        }
    }
}
