package distribucion.vista;

import distribucion.modelo.ConexionVial;
import distribucion.persistencia.LectorDatos;
import distribucion.rutas.RedVial;
import distribucion.vista.Tema.Insignia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class VentanaPrincipal extends JFrame {

    private static final String[][] SECCIONES = {
            {"inicio", "Panel general", "Resumen de la red y accesos rapidos"},
            {"pedidos", "Gestion de pedidos", "Registro, ordenamiento y busqueda de pedidos"},
            {"rutas", "Planificacion de rutas", "Recorridos sobre la red vial entre depositos y centros"},
            {"cargas", "Seleccion de cargas", "Lotes que conviene llevar segun la capacidad del vehiculo"}
    };

    private static final Icono.Tipo[] ICONOS = {
            Icono.Tipo.INICIO, Icono.Tipo.PEDIDOS, Icono.Tipo.RUTAS, Icono.Tipo.CARGAS
    };

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelCentral = new JPanel(tarjetas);
    private final Map<String, ItemMenu> items = new LinkedHashMap<>();

    private JLabel tituloSeccion;
    private JLabel subtituloSeccion;
    private JLabel resumenRed;
    private JLabel estadoRed;

    // la red se carga una sola vez y la comparten todos los paneles
    private RedVial red;
    private PanelInicio panelInicio;

    public VentanaPrincipal() {
        setTitle("NEXO | Sistema de Distribucion de Ayuda");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cargarRed();

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.FONDO);
        raiz.add(crearMenu(), BorderLayout.WEST);
        raiz.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(raiz);

        mostrarSeccion("inicio");
    }

    private void cargarRed() {
        try {
            red = LectorDatos.cargarRed("datos/ubicaciones.txt", "datos/conexiones.txt");
        } catch (IOException | IllegalArgumentException e) {
            red = null;
            JOptionPane.showMessageDialog(null,
                    "No se pudo cargar la red vial:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= menu lateral =================

    private JComponent crearMenu() {
        JPanel lateral = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Tema.NAVY_2, 0, getHeight(), Tema.NAVY));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        lateral.setPreferredSize(new Dimension(240, 0));
        lateral.setBorder(new EmptyBorder(26, 16, 20, 16));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JComponent marca = crearMarca();
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(marca);
        superior.add(Box.createVerticalStrut(36));

        JLabel modulos = new JLabel("MODULOS");
        modulos.setFont(Tema.fuente(Font.BOLD, 10.5f));
        modulos.setForeground(new Color(110, 132, 150));
        modulos.setBorder(new EmptyBorder(0, 14, 10, 0));
        modulos.setAlignmentX(Component.LEFT_ALIGNMENT);
        superior.add(modulos);

        for (int i = 0; i < SECCIONES.length; i++) {
            String id = SECCIONES[i][0];
            ItemMenu item = new ItemMenu(SECCIONES[i][1], ICONOS[i], () -> mostrarSeccion(id));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            items.put(id, item);
            superior.add(item);
            superior.add(Box.createVerticalStrut(4));
        }

        JPanel inferior = new JPanel();
        inferior.setOpaque(false);
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));

        JComponent tarjetaRed = crearEstadoRed();
        tarjetaRed.setAlignmentX(Component.LEFT_ALIGNMENT);

        ItemMenu salir = new ItemMenu("Salir", Icono.Tipo.SALIR, this::dispose);
        salir.setAlignmentX(Component.LEFT_ALIGNMENT);

        inferior.add(tarjetaRed);
        inferior.add(Box.createVerticalStrut(12));
        inferior.add(salir);

        lateral.add(superior, BorderLayout.NORTH);
        lateral.add(inferior, BorderLayout.SOUTH);
        return lateral;
    }

    private JComponent crearMarca() {
        JComponent logo = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizado(g);
                g2.setPaint(new GradientPaint(0, 0, new Color(38, 190, 182), 38, 38, Tema.TURQUESA_OSCURO));
                g2.fill(new RoundRectangle2D.Double(0, 0, 38, 38, 12, 12));

                // tres nodos conectados como isotipo
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(11, 26, 19, 12);
                g2.drawLine(19, 12, 27, 26);
                g2.fillOval(7, 22, 8, 8);
                g2.fillOval(15, 8, 8, 8);
                g2.fillOval(23, 22, 8, 8);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(38, 38));

        JLabel nombre = new JLabel("NEXO");
        nombre.setFont(Tema.fuente(Font.BOLD, 21));
        nombre.setForeground(Color.WHITE);

        JLabel bajada = new JLabel("Gestion logistica");
        bajada.setFont(Tema.fuente(Font.PLAIN, 11.5f));
        bajada.setForeground(Tema.MENU_TEXTO);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 0));
        textos.setOpaque(false);
        textos.add(nombre);
        textos.add(bajada);

        JPanel contenedorLogo = new JPanel(new GridBagLayout());
        contenedorLogo.setOpaque(false);
        contenedorLogo.add(logo);

        JPanel marca = new JPanel(new BorderLayout(12, 0));
        marca.setOpaque(false);
        marca.setBorder(new EmptyBorder(0, 8, 0, 0));
        marca.add(contenedorLogo, BorderLayout.WEST);
        marca.add(textos, BorderLayout.CENTER);
        marca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return marca;
    }

    private JComponent crearEstadoRed() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizado(g);
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(12, 14, 12, 14));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        estadoRed = new JLabel();
        estadoRed.setFont(Tema.fuente(Font.BOLD, 12));
        estadoRed.setForeground(Color.WHITE);
        estadoRed.setIconTextGap(8);

        resumenRed = new JLabel();
        resumenRed.setFont(Tema.fuente(Font.PLAIN, 11.5f));
        resumenRed.setForeground(Tema.MENU_TEXTO);

        tarjeta.add(estadoRed, BorderLayout.NORTH);
        tarjeta.add(resumenRed, BorderLayout.CENTER);
        return tarjeta;
    }

    private void actualizarEstadoRed() {
        if (red == null) {
            estadoRed.setIcon(punto(Tema.ROJO));
            estadoRed.setText("Red no disponible");
            resumenRed.setText("Revise los archivos de datos");
            return;
        }

        int habilitadas = 0;
        for (ConexionVial c : red.getConexiones()) {
            if (c.isHabilitada()) {
                habilitadas++;
            }
        }
        boolean completa = habilitadas == red.getConexiones().size();

        estadoRed.setIcon(punto(completa ? new Color(52, 196, 188) : new Color(232, 168, 56)));
        estadoRed.setText(completa ? "Red vial cargada" : "Red con cortes");
        resumenRed.setText(red.getUbicaciones().size() + " ubicaciones · "
                + habilitadas + "/" + red.getConexiones().size() + " conexiones");
    }

    private static Icon punto(Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = Tema.suavizado(g);
                g2.setColor(Tema.conAlfa(color, 60));
                g2.fillOval(x, y, 10, 10);
                g2.setColor(color);
                g2.fillOval(x + 2, y + 2, 6, 6);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 10;
            }

            @Override
            public int getIconHeight() {
                return 10;
            }
        };
    }

    // ================= contenido =================

    private JComponent crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 22));
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(26, 32, 26, 32));

        tituloSeccion = Tema.titulo("", 25);
        subtituloSeccion = Tema.texto("");

        JPanel textos = new JPanel(new BorderLayout(0, 4));
        textos.setOpaque(false);
        textos.add(tituloSeccion, BorderLayout.NORTH);
        textos.add(subtituloSeccion, BorderLayout.CENTER);

        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", new Locale("es", "AR")));
        hoy = Character.toUpperCase(hoy.charAt(0)) + hoy.substring(1);

        Insignia fecha = new Insignia(hoy, Tema.TEXTO_SUAVE, Tema.SUPERFICIE);
        fecha.setFont(Tema.fuente(Font.BOLD, 12));
        fecha.setBorder(new EmptyBorder(7, 14, 7, 14));
        fecha.setIcon(new Icono(Icono.Tipo.RELOJ, 14, Tema.TEXTO_TENUE));
        fecha.setIconTextGap(8);

        JPanel derecha = new JPanel(new GridBagLayout());
        derecha.setOpaque(false);
        derecha.add(fecha);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(derecha, BorderLayout.EAST);

        panelInicio = new PanelInicio(red, this::mostrarSeccion);
        PanelRutas panelRutas = new PanelRutas(red);
        panelRutas.alCambiarRed(this::actualizarEstadoRed);

        panelCentral.setOpaque(false);
        panelCentral.add(panelInicio, "inicio");
        panelCentral.add(new PanelPedidos(red), "pedidos");
        panelCentral.add(panelRutas, "rutas");
        panelCentral.add(new PanelCargas(), "cargas");

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(panelCentral, BorderLayout.CENTER);
        return contenido;
    }

    private void mostrarSeccion(String seccion) {
        for (String[] datos : SECCIONES) {
            if (datos[0].equals(seccion)) {
                tituloSeccion.setText(datos[1]);
                subtituloSeccion.setText(datos[2]);
            }
        }

        for (Map.Entry<String, ItemMenu> entrada : items.entrySet()) {
            entrada.getValue().setActivo(entrada.getKey().equals(seccion));
        }

        if (seccion.equals("inicio")) {
            panelInicio.actualizar();
        }

        actualizarEstadoRed();
        tarjetas.show(panelCentral, seccion);
    }

    // ================= item del menu =================

    private static class ItemMenu extends JComponent {

        private final String texto;
        private final Icono.Tipo icono;
        private boolean activo;
        private boolean encima;

        ItemMenu(String texto, Icono.Tipo icono, Runnable accion) {
            this.texto = texto;
            this.icono = icono;

            setPreferredSize(new Dimension(208, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    encima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    encima = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    accion.run();
                }
            });
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suavizado(g);
            int w = getWidth();
            int h = getHeight();

            if (activo) {
                g2.setColor(new Color(19, 157, 151, 46));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));
                g2.setColor(Tema.TURQUESA);
                g2.fill(new RoundRectangle2D.Double(0, 12, 4, h - 24, 4, 4));
            } else if (encima) {
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));
            }

            Color color = activo ? Color.WHITE : encima ? new Color(222, 232, 239) : Tema.MENU_TEXTO;
            new Icono(icono, 18, activo ? new Color(64, 212, 203) : color).paintIcon(this, g2, 16, (h - 18) / 2);

            g2.setFont(Tema.fuente(activo ? Font.BOLD : Font.PLAIN, 13.5f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(color);
            g2.drawString(texto, 46, (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        // suavizado de texto en todos los componentes (tiene que ir antes de crear la ventana)
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // usar el estilo disponible
            }

            Tema.aplicarDefaults();
            new VentanaPrincipal().setVisible(true);
        });
    }
}