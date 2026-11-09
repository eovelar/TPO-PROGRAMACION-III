
package distribucion.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    // colores principales del sistema
    private static final Color AZUL = new Color(27, 43, 58);
    private static final Color AZUL_CLARO = new Color(42, 63, 80);
    private static final Color TURQUESA = new Color(19, 157, 151);
    private static final Color FONDO = new Color(245, 248, 250);
    private static final Color TEXTO = new Color(35, 49, 61);
    private static final Color GRIS = new Color(112, 125, 137);
    private static final Color BLANCO = Color.WHITE;

    private JPanel panelCentral;
    private CardLayout tarjetas;
    private JLabel tituloSeccion;

    public VentanaPrincipal() {

        setTitle("Sistema de Distribucion de Ayuda");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        crearMenuLateral();
        crearContenido();
        mostrarSeccion("inicio", "Panel general");
    }

    // crear el menu lateral
    private void crearMenuLateral() {

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(AZUL);
        lateral.setPreferredSize(new Dimension(235, 0));
        lateral.setBorder(new EmptyBorder(28, 18, 22, 18));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JLabel marca = new JLabel("NEXO");
        marca.setFont(new Font("SansSerif", Font.BOLD, 28));
        marca.setForeground(BLANCO);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("GESTION LOGISTICA");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitulo.setForeground(new Color(164, 183, 195));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        superior.add(marca);
        superior.add(Box.createVerticalStrut(5));
        superior.add(subtitulo);
        superior.add(Box.createVerticalStrut(45));

        superior.add(crearBotonMenu(
                "Panel general", "inicio", "Panel general"
        ));

        superior.add(Box.createVerticalStrut(12));

        superior.add(crearBotonMenu(
                "Gestion de pedidos", "pedidos", "Gestion de pedidos"
        ));

        superior.add(Box.createVerticalStrut(12));

        superior.add(crearBotonMenu(
                "Planificacion de rutas", "rutas", "Planificacion de rutas"
        ));

        superior.add(Box.createVerticalStrut(12));

        superior.add(crearBotonMenu(
                "Seleccion de cargas", "cargas", "Seleccion de cargas"
        ));

        JPanel inferior = new JPanel();
        inferior.setOpaque(false);
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));

        JLabel version = new JLabel("PROGRAMACION III");
        version.setForeground(new Color(164, 183, 195));
        version.setFont(new Font("SansSerif", Font.PLAIN, 11));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton salir = crearBotonMenu("Salir", null, null);
        salir.addActionListener(e -> dispose());

        inferior.add(version);
        inferior.add(Box.createVerticalStrut(12));
        inferior.add(salir);

        lateral.add(superior, BorderLayout.NORTH);
        lateral.add(inferior, BorderLayout.SOUTH);

        add(lateral, BorderLayout.WEST);
    }

    // boton reutilizable para la navegacion
    private JButton crearBotonMenu(
            String texto, String seccion, String titulo) {

        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(BLANCO);
        boton.setBackground(AZUL_CLARO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(new EmptyBorder(14, 15, 14, 10));
        boton.setMaximumSize(new Dimension(200, 47));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (seccion != null) {
            boton.addActionListener(e -> mostrarSeccion(seccion, titulo));
        }

        return boton;
    }

    // crear la parte principal de la ventana
    private void crearContenido() {

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(30, 36, 30, 36));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 28, 0));

        tituloSeccion = new JLabel("Panel general");
        tituloSeccion.setFont(new Font("SansSerif", Font.BOLD, 26));
        tituloSeccion.setForeground(TEXTO);

        JLabel estado = new JLabel("SISTEMA DE DISTRIBUCION");
        estado.setFont(new Font("SansSerif", Font.BOLD, 11));
        estado.setForeground(TURQUESA);

        encabezado.add(tituloSeccion, BorderLayout.WEST);
        encabezado.add(estado, BorderLayout.EAST);

        tarjetas = new CardLayout();
        panelCentral = new JPanel(tarjetas);
        panelCentral.setOpaque(false);

        panelCentral.add(crearInicio(), "inicio");
        panelCentral.add(crearSeccion(
                "Gestion de pedidos",
                "Organizacion, prioridades y busqueda de pedidos.",
                "Algoritmos: MergeSort y Busqueda Binaria"
        ), "pedidos");

        panelCentral.add(crearSeccion(
                "Planificacion de rutas",
                "Calculo de recorridos y distribucion entre centros.",
                "Algoritmos: Dijkstra, Floyd-Warshall y Backtracking"
        ), "rutas");

        panelCentral.add(crearSeccion(
                "Seleccion de cargas",
                "Optimizacion de mercaderia segun la capacidad disponible.",
                "Algoritmo: Mochila 0/1"
        ), "cargas");

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(panelCentral, BorderLayout.CENTER);

        add(contenido, BorderLayout.CENTER);
    }

    // pantalla principal del sistema
    private JPanel crearInicio() {

        JPanel inicio = new JPanel(new BorderLayout(0, 25));
        inicio.setOpaque(false);

        JPanel bienvenida = crearTarjeta();
        bienvenida.setLayout(new BoxLayout(bienvenida, BoxLayout.Y_AXIS));

        JLabel encabezado = new JLabel("Bienvenido a NEXO");
        encabezado.setFont(new Font("SansSerif", Font.BOLD, 25));
        encabezado.setForeground(TEXTO);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "Sistema de planificacion y distribucion de ayuda."
        );
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descripcion.setForeground(GRIS);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detalle = new JLabel(
                "Gestion de pedidos, rutas y cargas en un mismo lugar."
        );
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detalle.setForeground(GRIS);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        bienvenida.add(encabezado);
        bienvenida.add(Box.createVerticalStrut(12));
        bienvenida.add(descripcion);
        bienvenida.add(Box.createVerticalStrut(5));
        bienvenida.add(detalle);

        JPanel indicadores = new JPanel(new GridLayout(1, 3, 18, 0));
        indicadores.setOpaque(false);

        indicadores.add(crearIndicador(
                "PEDIDOS", "MergeSort", "Ordenamiento y consultas"
        ));

        indicadores.add(crearIndicador(
                "RUTAS", "4 metodos", "Calculo y planificacion"
        ));

        indicadores.add(crearIndicador(
                "CARGAS", "Mochila 0/1", "Seleccion optimizada"
        ));

        JPanel superior = new JPanel(new BorderLayout(0, 22));
        superior.setOpaque(false);
        superior.add(bienvenida, BorderLayout.NORTH);
        superior.add(indicadores, BorderLayout.CENTER);

        JPanel informacion = crearTarjeta();
        informacion.setLayout(new BoxLayout(informacion, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sobre el sistema");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea texto = new JTextArea(
                "Esta aplicacion permite organizar pedidos de ayuda, "
                        + "calcular rutas de distribucion y seleccionar mercaderia "
                        + "segun la capacidad de los vehiculos.\n\n"
                        + "Integra algoritmos de ordenamiento, busqueda, "
                        + "caminos minimos, programacion dinamica y backtracking."
        );

        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        texto.setForeground(GRIS);
        texto.setAlignmentX(Component.LEFT_ALIGNMENT);

        informacion.add(titulo);
        informacion.add(Box.createVerticalStrut(15));
        informacion.add(texto);

        inicio.add(superior, BorderLayout.NORTH);
        inicio.add(informacion, BorderLayout.CENTER);

        return inicio;
    }

    // crear las tarjetas de indicadores
    private JPanel crearIndicador(
            String etiqueta, String valor, String descripcion) {

        JPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TURQUESA);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel numero = new JLabel(valor);
        numero.setFont(new Font("SansSerif", Font.BOLD, 22));
        numero.setForeground(TEXTO);
        numero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detalle = new JLabel(descripcion);
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        detalle.setForeground(GRIS);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(label);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(numero);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(detalle);

        return tarjeta;
    }

    // preparar las pantallas de cada funcionalidad
    private JPanel crearSeccion(
            String nombre, String descripcion, String algoritmos) {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);

        JPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(nombre);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detalle = new JLabel(descripcion);
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detalle.setForeground(GRIS);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel metodos = new JLabel(algoritmos);
        metodos.setFont(new Font("SansSerif", Font.BOLD, 13));
        metodos.setForeground(TURQUESA);
        metodos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel proximo = new JLabel(
                "Las operaciones se incorporaran en esta seccion."
        );
        proximo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        proximo.setForeground(GRIS);
        proximo.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(15));
        tarjeta.add(detalle);
        tarjeta.add(Box.createVerticalStrut(25));
        tarjeta.add(metodos);
        tarjeta.add(Box.createVerticalStrut(25));
        tarjeta.add(proximo);

        contenedor.add(tarjeta, BorderLayout.NORTH);

        return contenedor;
    }

    // estilo comun de las tarjetas
    private JPanel crearTarjeta() {

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 232, 237), 1
                ),
                new EmptyBorder(25, 25, 25, 25)
        ));

        return tarjeta;
    }

    // cambiar entre las secciones
    private void mostrarSeccion(String seccion, String titulo) {
        tarjetas.show(panelCentral, seccion);
        tituloSeccion.setText(titulo);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                // usar el estilo predeterminado si no esta disponible
            }

            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
