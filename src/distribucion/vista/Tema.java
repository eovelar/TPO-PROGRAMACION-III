package distribucion.vista;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntConsumer;

// colores, fuentes y componentes compartidos por toda la interfaz
final class Tema {

    static final Color NAVY = new Color(16, 29, 42);
    static final Color NAVY_2 = new Color(27, 43, 58);
    static final Color NAVY_3 = new Color(42, 63, 80);
    static final Color TURQUESA = new Color(19, 157, 151);
    static final Color TURQUESA_OSCURO = new Color(13, 128, 123);
    static final Color TURQUESA_SUAVE = new Color(226, 245, 243);
    static final Color FONDO = new Color(243, 246, 249);
    static final Color FONDO_CAMPO = new Color(250, 251, 253);
    static final Color SUPERFICIE = Color.WHITE;
    static final Color BORDE = new Color(225, 231, 237);
    static final Color LINEA = new Color(236, 240, 244);
    static final Color TEXTO = new Color(27, 40, 52);
    static final Color TEXTO_SUAVE = new Color(108, 121, 134);
    static final Color TEXTO_TENUE = new Color(158, 170, 181);
    static final Color AMBAR = new Color(196, 124, 14);
    static final Color AMBAR_SUAVE = new Color(253, 243, 224);
    static final Color ROJO = new Color(196, 70, 70);
    static final Color ROJO_SUAVE = new Color(252, 233, 233);
    static final Color MENU_TEXTO = new Color(170, 188, 202);
    static final Color CONSOLA_TEXTO = new Color(200, 226, 232);

    private static final String FAMILIA = elegirFamilia();

    private Tema() {
    }

    // usar la mejor fuente disponible en el sistema
    private static String elegirFamilia() {
        String[] preferidas = {"Segoe UI", "Inter", "Helvetica Neue", "Roboto", "Noto Sans", "DejaVu Sans"};
        String[] instaladas = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        for (String preferida : preferidas) {
            for (String instalada : instaladas) {
                if (instalada.equalsIgnoreCase(preferida)) {
                    return instalada;
                }
            }
        }
        return Font.SANS_SERIF;
    }

    static Font fuente(int estilo, float tamano) {
        return new Font(FAMILIA, estilo, 1).deriveFont(tamano);
    }

    static Font monoespaciada(float tamano) {
        return new Font(Font.MONOSPACED, Font.PLAIN, 1).deriveFont(tamano);
    }

    static void aplicarDefaults() {
        UIManager.put("ToolTip.font", fuente(Font.PLAIN, 12));
        UIManager.put("ToolTip.background", NAVY_2);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", new EmptyBorder(7, 10, 7, 10));
        UIManager.put("OptionPane.messageFont", fuente(Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", fuente(Font.BOLD, 12));
        ToolTipManager.sharedInstance().setInitialDelay(250);
    }

    static Graphics2D suavizado(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    static Color conAlfa(Color color, int alfa) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa);
    }

    // ---------- textos ----------

    static JLabel titulo(String texto, float tamano) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.BOLD, tamano));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    static JLabel texto(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.PLAIN, 13));
        etiqueta.setForeground(TEXTO_SUAVE);
        return etiqueta;
    }

    static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.BOLD, 11));
        etiqueta.setForeground(TEXTO_SUAVE);
        return etiqueta;
    }

    // texto que se ajusta al ancho disponible
    static JLabel parrafo(String texto, int ancho) {
        JLabel etiqueta = texto("<html><div style='width:" + ancho + "px'>" + texto + "</div></html>");
        etiqueta.setFont(fuente(Font.PLAIN, 12.5f));
        return etiqueta;
    }

    static void estadoOk(JLabel etiqueta, String mensaje) {
        etiqueta.setText(mensaje);
        etiqueta.setForeground(TURQUESA_OSCURO);
    }

    static void estadoError(JLabel etiqueta, String mensaje) {
        etiqueta.setText(mensaje);
        etiqueta.setForeground(ROJO);
    }

    static void estadoNeutro(JLabel etiqueta, String mensaje) {
        etiqueta.setText(mensaje);
        etiqueta.setForeground(TEXTO_SUAVE);
    }

    // ---------- estructura ----------

    static JPanel encabezado(String titulo, String subtitulo, JComponent derecha) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        JPanel textos = new JPanel(new GridLayout(subtitulo == null ? 1 : 2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(titulo(titulo, 17));

        if (subtitulo != null) {
            JLabel sub = texto(subtitulo);
            sub.setFont(fuente(Font.PLAIN, 12.5f));
            textos.add(sub);
        }

        panel.add(textos, BorderLayout.CENTER);

        if (derecha != null) {
            JPanel contenedor = new JPanel(new GridBagLayout());
            contenedor.setOpaque(false);
            contenedor.add(derecha);
            panel.add(contenedor, BorderLayout.EAST);
        }

        return panel;
    }

    static JPanel campo(String nombre, JComponent componente) {
        JPanel campo = new JPanel(new BorderLayout(0, 6));
        campo.setOpaque(false);
        campo.add(etiqueta(nombre), BorderLayout.NORTH);
        campo.add(componente, BorderLayout.CENTER);
        return campo;
    }

    static JPanel fila(int separacion, JComponent... componentes) {
        JPanel fila = new JPanel(new GridLayout(1, componentes.length, separacion, 0));
        fila.setOpaque(false);
        for (JComponent componente : componentes) {
            fila.add(componente);
        }
        return fila;
    }

    // apila filas arriba de todo, con todo el ancho y su alto preferido
    static JPanel formulario(int separacion, JComponent... filas) {
        JPanel panel = new JPanel(new ColumnaLayout(separacion));
        panel.setOpaque(false);
        for (JComponent fila : filas) {
            panel.add(fila);
        }
        return panel;
    }

    // a diferencia de GridBagLayout, nunca cambia a los tamanos minimos si falta ancho
    static class ColumnaLayout implements LayoutManager {

        private final int separacion;

        ColumnaLayout(int separacion) {
            this.separacion = separacion;
        }

        @Override
        public void addLayoutComponent(String nombre, Component componente) {
        }

        @Override
        public void removeLayoutComponent(Component componente) {
        }

        @Override
        public Dimension preferredLayoutSize(Container padre) {
            Insets bordes = padre.getInsets();
            int ancho = 0;
            int alto = 0;
            int visibles = 0;

            for (Component c : padre.getComponents()) {
                if (c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    ancho = Math.max(ancho, d.width);
                    alto += d.height;
                    visibles++;
                }
            }

            alto += Math.max(0, visibles - 1) * separacion;
            return new Dimension(ancho + bordes.left + bordes.right, alto + bordes.top + bordes.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container padre) {
            Dimension preferido = preferredLayoutSize(padre);
            return new Dimension(0, preferido.height);
        }

        @Override
        public void layoutContainer(Container padre) {
            Insets bordes = padre.getInsets();
            int ancho = padre.getWidth() - bordes.left - bordes.right;
            int y = bordes.top;

            for (Component c : padre.getComponents()) {
                if (c.isVisible()) {
                    int alto = c.getPreferredSize().height;
                    c.setBounds(bordes.left, y, ancho, alto);
                    y += alto + separacion;
                }
            }
        }
    }

    static JComponent separador() {
        JComponent linea = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(LINEA);
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        linea.setPreferredSize(new Dimension(10, 9));
        return linea;
    }

    // scroll vertical para paneles: el contenido se ajusta al ancho visible
    static JScrollPane scrollVertical(JComponent contenido) {
        JPanel ajustado = new PanelAjustado();
        ajustado.add(contenido, BorderLayout.CENTER);

        JScrollPane scroll = scroll(ajustado);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    static JScrollPane scroll(JComponent contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        // un borde vacio propio evita que la tabla le ponga el borde del look and feel
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setUI(new BarraFina());
        scroll.getHorizontalScrollBar().setUI(new BarraFina());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scroll.getVerticalScrollBar().setOpaque(false);
        return scroll;
    }

    // ---------- campos ----------

    static <T> JComboBox<T> combo(Function<Object, String> texto) {
        JComboBox<T> combo = new JComboBox<>();
        combo.setUI(new ComboPlano());
        combo.setFont(fuente(Font.PLAIN, 13));
        combo.setForeground(TEXTO);
        combo.setBackground(FONDO_CAMPO);
        combo.setBorder(new BordeRedondeado(BORDE, 10, new Insets(7, 10, 7, 6)));
        combo.setFocusable(false);
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean foco) {

                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                setText(valor == null ? "" : texto.apply(valor));
                setFont(fuente(Font.PLAIN, 13));
                setBorder(new EmptyBorder(6, indice < 0 ? 2 : 10, 6, 10));
                setForeground(TEXTO);
                setBackground(seleccionado && indice >= 0 ? TURQUESA_SUAVE
                        : indice < 0 ? FONDO_CAMPO : SUPERFICIE);
                return this;
            }
        });

        return combo;
    }

    static JSpinner spinner(int valor, int minimo, int maximo, int paso) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(valor, minimo, maximo, paso));
        spinner.setFont(fuente(Font.PLAIN, 13));
        spinner.setBorder(new BordeRedondeado(BORDE, 10, new Insets(4, 8, 4, 4)));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField texto = ((JSpinner.DefaultEditor) editor).getTextField();
            texto.setFont(fuente(Font.PLAIN, 13));
            texto.setForeground(TEXTO);
            texto.setBorder(new EmptyBorder(4, 2, 4, 2));
            texto.setHorizontalAlignment(JTextField.LEFT);
        }
        return spinner;
    }

    static JCheckBox casilla(String texto) {
        JCheckBox casilla = new JCheckBox(texto);
        casilla.setFont(fuente(Font.PLAIN, 13));
        casilla.setForeground(TEXTO);
        casilla.setOpaque(false);
        casilla.setFocusPainted(false);
        casilla.setIconTextGap(10);
        casilla.setBorder(new EmptyBorder(3, 0, 3, 0));
        casilla.setIcon(new IconoCasilla(false, true));
        casilla.setSelectedIcon(new IconoCasilla(true, true));
        casilla.setDisabledIcon(new IconoCasilla(false, false));
        casilla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return casilla;
    }

    static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setFont(fuente(Font.PLAIN, 13));
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);
        tabla.setSelectionBackground(TURQUESA_SUAVE);
        tabla.setSelectionForeground(TEXTO);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(LINEA);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer celda = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object valor, boolean seleccionado,
                    boolean foco, int fila, int columna) {

                super.getTableCellRendererComponent(t, valor, seleccionado, false, fila, columna);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        };
        tabla.setDefaultRenderer(Object.class, celda);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(10, 38));
        encabezado.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object valor, boolean seleccionado,
                    boolean foco, int fila, int columna) {

                JLabel etiqueta = new JLabel(String.valueOf(valor).toUpperCase());
                etiqueta.setFont(fuente(Font.BOLD, 11));
                etiqueta.setForeground(TEXTO_SUAVE);
                etiqueta.setOpaque(true);
                etiqueta.setBackground(FONDO_CAMPO);
                etiqueta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                        new EmptyBorder(0, 14, 0, 14)));
                return etiqueta;
            }
        });
    }

    // envuelve una tabla en un contenedor con borde redondeado
    static JComponent marcoTabla(JTable tabla) {
        JScrollPane scroll = scroll(tabla);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(SUPERFICIE);
        scroll.setColumnHeaderView(tabla.getTableHeader());

        Tarjeta marco = new Tarjeta();
        marco.setLayout(new BorderLayout());
        marco.setBorder(new EmptyBorder(1, 1, 1, 1));
        marco.setRadio(12);
        marco.add(scroll, BorderLayout.CENTER);
        return marco;
    }

    static JTextArea consola(String textoInicial) {
        JTextArea area = new JTextArea(textoInicial);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(monoespaciada(12.5f));
        area.setForeground(CONSOLA_TEXTO);
        area.setBackground(NAVY);
        area.setCaretColor(NAVY);
        area.setBorder(new EmptyBorder(12, 14, 12, 14));
        return area;
    }

    static JComponent marcoConsola(JTextArea area) {
        JScrollPane scroll = scroll(area);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(NAVY);

        Tarjeta marco = new Tarjeta();
        marco.setColores(NAVY, null);
        marco.setRadio(12);
        marco.setLayout(new BorderLayout());
        marco.setBorder(new EmptyBorder(4, 2, 4, 2));
        marco.add(scroll, BorderLayout.CENTER);
        return marco;
    }

    // ejecuta un algoritmo que imprime por consola y devuelve lo que imprimio
    static String capturarSalida(Runnable algoritmo) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            algoritmo.run();
        } finally {
            System.setOut(original);
        }

        String salida = buffer.toString(StandardCharsets.UTF_8).trim();
        return salida.isEmpty() ? "(el algoritmo no genero salida)" : salida;
    }

    // ================= componentes =================

    // panel blanco con esquinas redondeadas
    static class Tarjeta extends JPanel {

        private Color fondo = SUPERFICIE;
        private Color borde = BORDE;
        private int radio = 16;

        Tarjeta() {
            setOpaque(false);
            setBorder(new EmptyBorder(20, 22, 20, 22));
        }

        Tarjeta(String titulo, String subtitulo) {
            this(titulo, subtitulo, null);
        }

        Tarjeta(String titulo, String subtitulo, JComponent derecha) {
            this();
            setLayout(new BorderLayout(0, 16));
            add(encabezado(titulo, subtitulo, derecha), BorderLayout.NORTH);
        }

        void setColores(Color fondo, Color borde) {
            this.fondo = fondo;
            this.borde = borde;
            repaint();
        }

        void setRadio(int radio) {
            this.radio = radio;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            int w = getWidth();
            int h = getHeight();

            if (borde != null) {
                g2.setColor(new Color(16, 29, 42, 10));
                g2.fill(new RoundRectangle2D.Double(0, 1.5, w, h - 1.5, radio, radio));
            }

            g2.setColor(fondo);
            g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 2, radio, radio));

            if (borde != null) {
                g2.setColor(borde);
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, radio, radio));
            }

            g2.dispose();
        }
    }

    enum Variante { PRIMARIO, OSCURO, FANTASMA, CLARO }

    // boton plano dibujado a mano para que se vea igual en cualquier sistema
    static class Boton extends JButton {

        private final Variante variante;
        private boolean encima;
        private boolean presionado;

        Boton(String texto, Variante variante) {
            this(texto, null, variante);
        }

        Boton(String texto, Icono.Tipo icono, Variante variante) {
            super(texto);
            this.variante = variante;

            if (icono != null) {
                setIcon(new Icono(icono, 16, colorTexto()));
            }

            setFont(fuente(Font.BOLD, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 16, 10, 16));
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
                    presionado = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    presionado = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    presionado = false;
                    repaint();
                }
            });
        }

        private Color colorTexto() {
            switch (variante) {
                case FANTASMA:
                    return TEXTO;
                default:
                    return Color.WHITE;
            }
        }

        private Color colorFondo() {
            if (!isEnabled()) {
                return variante == Variante.FANTASMA ? SUPERFICIE : BORDE;
            }
            switch (variante) {
                case PRIMARIO:
                    return presionado ? new Color(10, 110, 106)
                            : encima ? TURQUESA_OSCURO : TURQUESA;
                case OSCURO:
                    return presionado ? NAVY : encima ? NAVY_3 : NAVY_2;
                case CLARO:
                    return new Color(255, 255, 255, presionado ? 70 : encima ? 50 : 32);
                default:
                    return presionado ? LINEA : encima ? FONDO : SUPERFICIE;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(colorFondo());
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));

            if (variante == Variante.FANTASMA) {
                g2.setColor(encima && isEnabled() ? TEXTO_TENUE : BORDE);
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 10, 10));
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String texto = getText() == null ? "" : getText();
            Icon icono = getIcon();

            int anchoTexto = fm.stringWidth(texto);
            int anchoIcono = icono == null ? 0 : icono.getIconWidth() + (texto.isEmpty() ? 0 : 8);
            int x = (w - anchoTexto - anchoIcono) / 2;

            if (icono != null) {
                icono.paintIcon(this, g2, x, (h - icono.getIconHeight()) / 2);
            }

            g2.setColor(isEnabled() ? colorTexto() : TEXTO_TENUE);
            g2.drawString(texto, x + anchoIcono, (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    // campo de texto redondeado con texto de ayuda
    static class CampoTexto extends JTextField {

        private final String ayuda;
        private final Icono icono;

        CampoTexto(String ayuda) {
            this(ayuda, null);
        }

        CampoTexto(String ayuda, Icono.Tipo tipoIcono) {
            this.ayuda = ayuda;
            this.icono = tipoIcono == null ? null : new Icono(tipoIcono, 16, TEXTO_TENUE);

            setFont(fuente(Font.PLAIN, 13));
            setForeground(TEXTO);
            setCaretColor(TURQUESA);
            setSelectionColor(TURQUESA_SUAVE);
            setOpaque(false);
            setBorder(new EmptyBorder(9, icono == null ? 12 : 36, 9, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            int w = getWidth();
            int h = getHeight();
            boolean foco = isFocusOwner();

            g2.setColor(foco ? SUPERFICIE : FONDO_CAMPO);
            g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 10, 10));

            if (foco) {
                g2.setColor(conAlfa(TURQUESA, 45));
                g2.setStroke(new BasicStroke(3f));
                g2.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 4, h - 4, 10, 10));
            }

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(foco ? TURQUESA : BORDE);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 2, 10, 10));

            if (icono != null) {
                icono.paintIcon(this, g2, 12, (h - 16) / 2);
            }
            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty() && ayuda != null) {
                Graphics2D g3 = suavizado(g);
                g3.setFont(getFont());
                g3.setColor(TEXTO_TENUE);
                FontMetrics fm = g3.getFontMetrics();
                g3.drawString(ayuda, getInsets().left, (h - fm.getHeight()) / 2 + fm.getAscent());
                g3.dispose();
            }
        }
    }

    // etiqueta con forma de pastilla
    static class Insignia extends JLabel {

        private Color fondo;

        Insignia(String texto, Color frente, Color fondo) {
            setFont(fuente(Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 10, 4, 10));
            setHorizontalAlignment(CENTER);
            cambiar(texto, frente, fondo);
        }

        void cambiar(String texto, Color frente, Color fondo) {
            setText(texto);
            setForeground(frente);
            this.fondo = fondo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            g2.setColor(fondo);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // selector de opciones tipo pestanas
    static class Segmentado extends JComponent {

        private final String[] opciones;
        private final List<IntConsumer> oyentes = new ArrayList<>();
        private int seleccion;
        private int encima = -1;

        Segmentado(String... opciones) {
            this.opciones = opciones;
            setFont(fuente(Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int indice = indiceEn(e.getX());
                    if (indice != encima) {
                        encima = indice;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    encima = -1;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    setSeleccion(indiceEn(e.getX()));
                }
            };

            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        private int indiceEn(int x) {
            int ancho = Math.max(1, getWidth() / opciones.length);
            return Math.max(0, Math.min(opciones.length - 1, x / ancho));
        }

        void alCambiar(IntConsumer oyente) {
            oyentes.add(oyente);
        }

        int getSeleccion() {
            return seleccion;
        }

        void setSeleccion(int indice) {
            if (indice == seleccion) {
                return;
            }
            seleccion = indice;
            repaint();
            for (IntConsumer oyente : oyentes) {
                oyente.accept(indice);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int maximo = 0;
            for (String opcion : opciones) {
                maximo = Math.max(maximo, fm.stringWidth(opcion));
            }
            return new Dimension((maximo + 26) * opciones.length + 8, 38);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            int w = getWidth();
            int h = getHeight();
            double ancho = (w - 8) / (double) opciones.length;

            g2.setColor(new Color(234, 239, 243));
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < opciones.length; i++) {
                double x = 4 + i * ancho;

                if (i == seleccion) {
                    g2.setColor(new Color(16, 29, 42, 18));
                    g2.fill(new RoundRectangle2D.Double(x, 5, ancho, h - 8, 9, 9));
                    g2.setColor(SUPERFICIE);
                    g2.fill(new RoundRectangle2D.Double(x, 4, ancho, h - 8, 9, 9));
                }

                g2.setColor(i == seleccion ? TEXTO : i == encima ? TEXTO : TEXTO_SUAVE);
                int anchoTexto = fm.stringWidth(opciones[i]);
                g2.drawString(opciones[i],
                        (float) (x + (ancho - anchoTexto) / 2),
                        (h - fm.getHeight()) / 2f + fm.getAscent());
            }
            g2.dispose();
        }
    }

    // interruptor de encendido y apagado
    static class Interruptor extends JComponent {

        private boolean encendido;
        private Runnable accion;

        Interruptor(boolean encendido) {
            this.encendido = encendido;
            setPreferredSize(new Dimension(40, 22));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (accion != null) {
                        accion.run();
                    }
                }
            });
        }

        void alPresionar(Runnable accion) {
            this.accion = accion;
        }

        void setEncendido(boolean encendido) {
            this.encendido = encendido;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            int h = 22;
            int y = (getHeight() - h) / 2;

            g2.setColor(encendido ? TURQUESA : new Color(205, 213, 221));
            g2.fill(new RoundRectangle2D.Double(0, y, 40, h, h, h));

            g2.setColor(Color.WHITE);
            g2.fillOval(encendido ? 21 : 3, y + 3, 16, 16);
            g2.dispose();
        }
    }

    // FlowLayout que pasa a la linea siguiente cuando no hay lugar
    static class FlujoLayout extends FlowLayout {

        private int ultimoAncho = -1;

        FlujoLayout(int alineacion, int horizontal, int vertical) {
            super(alineacion, horizontal, vertical);
        }

        // cuando cambia el ancho se vuelve a pedir el layout para recalcular el alto
        @Override
        public void layoutContainer(Container destino) {
            super.layoutContainer(destino);
            int ancho = destino.getWidth();
            if (ancho != ultimoAncho && destino instanceof JComponent) {
                ultimoAncho = ancho;
                SwingUtilities.invokeLater(((JComponent) destino)::revalidate);
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container destino) {
            return medir(destino, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container destino) {
            Dimension minimo = medir(destino, false);
            minimo.width -= getHgap() + 1;
            return minimo;
        }

        private Dimension medir(Container destino, boolean preferido) {
            synchronized (destino.getTreeLock()) {
                Container referencia = destino;
                while (referencia.getSize().width == 0 && referencia.getParent() != null) {
                    referencia = referencia.getParent();
                }

                int ancho = referencia.getSize().width;
                if (ancho == 0) {
                    ancho = Integer.MAX_VALUE;
                }

                Insets bordes = destino.getInsets();
                int maximo = ancho - (bordes.left + bordes.right + getHgap() * 2);

                Dimension total = new Dimension(0, 0);
                int anchoFila = 0;
                int altoFila = 0;

                for (Component componente : destino.getComponents()) {
                    if (!componente.isVisible()) {
                        continue;
                    }

                    Dimension d = preferido ? componente.getPreferredSize() : componente.getMinimumSize();

                    if (anchoFila + d.width > maximo && anchoFila > 0) {
                        agregarFila(total, anchoFila, altoFila);
                        anchoFila = 0;
                        altoFila = 0;
                    }

                    if (anchoFila != 0) {
                        anchoFila += getHgap();
                    }

                    anchoFila += d.width;
                    altoFila = Math.max(altoFila, d.height);
                }

                agregarFila(total, anchoFila, altoFila);
                total.width += bordes.left + bordes.right + getHgap() * 2;
                total.height += bordes.top + bordes.bottom + getVgap() * 2;
                return total;
            }
        }

        private void agregarFila(Dimension total, int ancho, int alto) {
            total.width = Math.max(total.width, ancho);
            if (total.height > 0) {
                total.height += getVgap();
            }
            total.height += alto;
        }
    }

    // ---------- piezas internas ----------

    private static class PanelAjustado extends JPanel implements Scrollable {

        PanelAjustado() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
            return visible.height - 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            Container padre = getParent();
            return padre instanceof JViewport && padre.getHeight() > getPreferredSize().height;
        }
    }

    static class BordeRedondeado extends AbstractBorder {

        private final Color color;
        private final int radio;
        private final Insets margen;

        BordeRedondeado(Color color, int radio, Insets margen) {
            this.color = color;
            this.radio = radio;
            this.margen = margen;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = suavizado(g);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, w - 1, h - 1, radio, radio));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(margen.top, margen.left, margen.bottom, margen.right);
        }
    }

    private static class ComboPlano extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            JButton boton = new JButton(new Icono(Icono.Tipo.ABAJO, 14, TEXTO_SUAVE));
            boton.setContentAreaFilled(false);
            boton.setBorderPainted(false);
            boton.setFocusPainted(false);
            boton.setOpaque(false);
            boton.setBorder(new EmptyBorder(0, 4, 0, 4));
            return boton;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean foco) {
            g.setColor(FONDO_CAMPO);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = suavizado(g);
            g2.setColor(FONDO_CAMPO);
            g2.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), 10, 10));
            g2.dispose();
            super.paint(g, c);
        }
    }

    private static class BarraFina extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(200, 209, 217);
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int orientacion) {
            return botonVacio();
        }

        @Override
        protected JButton createIncreaseButton(int orientacion) {
            return botonVacio();
        }

        private JButton botonVacio() {
            JButton boton = new JButton();
            boton.setPreferredSize(new Dimension(0, 0));
            return boton;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) {
                return;
            }
            Graphics2D g2 = suavizado(g);
            g2.setColor(isDragging ? TEXTO_TENUE : thumbColor);
            g2.fill(new RoundRectangle2D.Double(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6));
            g2.dispose();
        }
    }

    private static class IconoCasilla implements Icon {

        private final boolean marcada;
        private final boolean habilitada;

        IconoCasilla(boolean marcada, boolean habilitada) {
            this.marcada = marcada;
            this.habilitada = habilitada;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = suavizado(g);
            g2.translate(x, y);

            if (marcada) {
                g2.setColor(TURQUESA);
                g2.fill(new RoundRectangle2D.Double(0, 0, 18, 18, 6, 6));

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D tilde = new Path2D.Double();
                tilde.moveTo(4.5, 9.5);
                tilde.lineTo(7.8, 12.6);
                tilde.lineTo(13.5, 5.8);
                g2.draw(tilde);
            } else {
                g2.setColor(habilitada ? SUPERFICIE : LINEA);
                g2.fill(new RoundRectangle2D.Double(0, 0, 18, 18, 6, 6));
                g2.setColor(habilitada ? new Color(196, 206, 215) : BORDE);
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, 17, 17, 6, 6));
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }
}