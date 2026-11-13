package distribucion.vista;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;
import distribucion.vista.Tema.Boton;
import distribucion.vista.Tema.Insignia;
import distribucion.vista.Tema.Tarjeta;
import distribucion.vista.Tema.Variante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class PanelInicio extends JPanel {

    private final RedVial red;
    private final Consumer<String> navegar;

    private JLabel valorUbicaciones;
    private JLabel valorDepositos;
    private JLabel valorCentros;
    private JLabel valorConexiones;
    private JLabel detalleConexiones;

    public PanelInicio(RedVial red, Consumer<String> navegar) {
        this.red = red;
        this.navegar = navegar;

        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel contenido = Tema.formulario(18,
                crearBanner(),
                crearIndicadores(),
                crearModulos(),
                crearTecnicas());
        contenido.setBorder(new EmptyBorder(0, 0, 4, 4));

        add(Tema.scrollVertical(contenido), BorderLayout.CENTER);
        actualizar();
    }

    // vuelve a contar la red (por ejemplo, despues de deshabilitar conexiones)
    public void actualizar() {
        if (red == null) {
            for (JLabel valor : new JLabel[]{valorUbicaciones, valorDepositos, valorCentros, valorConexiones}) {
                valor.setText("--");
            }
            detalleConexiones.setText("red no disponible");
            return;
        }

        int depositos = 0;
        int centros = 0;
        for (Ubicacion u : red.getUbicaciones()) {
            if (u.getTipo() == TipoUbicacion.DEPOSITO) {
                depositos++;
            } else if (u.getTipo() == TipoUbicacion.CENTRO_COMUNITARIO) {
                centros++;
            }
        }

        int habilitadas = 0;
        for (ConexionVial c : red.getConexiones()) {
            if (c.isHabilitada()) {
                habilitadas++;
            }
        }
        int deshabilitadas = red.getConexiones().size() - habilitadas;

        valorUbicaciones.setText(String.valueOf(red.getUbicaciones().size()));
        valorDepositos.setText(String.valueOf(depositos));
        valorCentros.setText(String.valueOf(centros));
        valorConexiones.setText(String.valueOf(habilitadas));

        if (deshabilitadas == 0) {
            detalleConexiones.setText("todas habilitadas");
            detalleConexiones.setForeground(Tema.TEXTO_SUAVE);
        } else {
            detalleConexiones.setText(deshabilitadas + (deshabilitadas == 1 ? " deshabilitada" : " deshabilitadas"));
            detalleConexiones.setForeground(Tema.AMBAR);
        }
    }

    // ---------- banner de bienvenida ----------

    private JComponent crearBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizado(g);
                int w = getWidth();
                int h = getHeight();

                g2.setPaint(new GradientPaint(0, 0, Tema.NAVY_2, w, h, new Color(18, 96, 102)));
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 18, 18));
                dibujarRedDecorativa(g2, w, h);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(28, 30, 28, 30));

        Insignia materia = new Insignia("PROGRAMACION III", Color.WHITE, new Color(255, 255, 255, 34));

        JLabel titulo = Tema.titulo("Bienvenido a NEXO", 27);
        titulo.setForeground(Color.WHITE);

        JLabel descripcion = new JLabel("<html><div style='width:420px'>Planifique la distribucion de ayuda: "
                + "registre pedidos, calcule rutas entre depositos y centros comunitarios "
                + "y elija que lotes cargar en cada vehiculo.</div></html>");
        descripcion.setFont(Tema.fuente(Font.PLAIN, 13.5f));
        descripcion.setForeground(new Color(206, 224, 230));

        Boton rutas = new Boton("Calcular una ruta", Icono.Tipo.RUTAS, Variante.PRIMARIO);
        rutas.addActionListener(e -> navegar.accept("rutas"));

        Boton pedidos = new Boton("Registrar pedido", Icono.Tipo.PEDIDOS, Variante.CLARO);
        pedidos.addActionListener(e -> navegar.accept("pedidos"));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botones.setOpaque(false);
        botones.add(rutas);
        botones.add(Box.createHorizontalStrut(10));
        botones.add(pedidos);

        JPanel insigniaFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        insigniaFila.setOpaque(false);
        insigniaFila.add(materia);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{insigniaFila, titulo, descripcion, botones}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        textos.add(insigniaFila);
        textos.add(Box.createVerticalStrut(12));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(8));
        textos.add(descripcion);
        textos.add(Box.createVerticalStrut(20));
        textos.add(botones);

        banner.add(textos, BorderLayout.WEST);
        return banner;
    }

    // pequeno grafo de adorno en el lado derecho del banner
    private static void dibujarRedDecorativa(Graphics2D g2, int w, int h) {
        double[][] nodos = {
                {0.70, 0.30}, {0.80, 0.18}, {0.92, 0.32}, {0.86, 0.62},
                {0.74, 0.74}, {0.96, 0.80}, {0.63, 0.55}
        };
        int[][] aristas = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 6}, {6, 0}, {0, 3}, {3, 5}, {2, 5}};

        if (w < 760) {
            return;
        }

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 255, 255, 40));
        for (int[] a : aristas) {
            g2.draw(new Line2D.Double(nodos[a[0]][0] * w, nodos[a[0]][1] * h,
                    nodos[a[1]][0] * w, nodos[a[1]][1] * h));
        }

        g2.setColor(new Color(19, 157, 151, 200));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Line2D.Double(nodos[6][0] * w, nodos[6][1] * h, nodos[0][0] * w, nodos[0][1] * h));
        g2.draw(new Line2D.Double(nodos[0][0] * w, nodos[0][1] * h, nodos[3][0] * w, nodos[3][1] * h));
        g2.draw(new Line2D.Double(nodos[3][0] * w, nodos[3][1] * h, nodos[5][0] * w, nodos[5][1] * h));

        for (int i = 0; i < nodos.length; i++) {
            double x = nodos[i][0] * w;
            double y = nodos[i][1] * h;
            boolean enRuta = i == 6 || i == 0 || i == 3 || i == 5;
            g2.setColor(enRuta ? new Color(52, 196, 188) : new Color(255, 255, 255, 90));
            g2.fillOval((int) x - 7, (int) y - 7, 14, 14);
        }
    }

    // ---------- indicadores ----------

    private JComponent crearIndicadores() {
        valorUbicaciones = Tema.titulo("--", 26);
        valorDepositos = Tema.titulo("--", 26);
        valorCentros = Tema.titulo("--", 26);
        valorConexiones = Tema.titulo("--", 26);
        detalleConexiones = Tema.texto("");

        JPanel fila = new JPanel(new GridLayout(1, 4, 16, 0));
        fila.setOpaque(false);
        fila.add(indicador(Icono.Tipo.CENTRO, "UBICACIONES", valorUbicaciones, Tema.texto("en la red vial")));
        fila.add(indicador(Icono.Tipo.DEPOSITO, "DEPOSITOS", valorDepositos, Tema.texto("puntos de salida")));
        fila.add(indicador(Icono.Tipo.PEDIDOS, "CENTROS", valorCentros, Tema.texto("puntos de entrega")));
        fila.add(indicador(Icono.Tipo.CONEXION, "CONEXIONES", valorConexiones, detalleConexiones));
        return fila;
    }

    private JComponent indicador(Icono.Tipo icono, String nombre, JLabel valor, JLabel detalle) {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new BorderLayout(14, 0));
        tarjeta.setBorder(new EmptyBorder(18, 18, 18, 18));

        detalle.setFont(Tema.fuente(Font.PLAIN, 12));

        JPanel textos = new JPanel(new BorderLayout(0, 2));
        textos.setOpaque(false);
        textos.add(Tema.etiqueta(nombre), BorderLayout.NORTH);
        textos.add(valor, BorderLayout.CENTER);
        textos.add(detalle, BorderLayout.SOUTH);

        JPanel contenedorIcono = new JPanel(new GridBagLayout());
        contenedorIcono.setOpaque(false);
        contenedorIcono.add(circuloIcono(icono, Tema.TURQUESA_SUAVE, Tema.TURQUESA_OSCURO));

        tarjeta.add(contenedorIcono, BorderLayout.WEST);
        tarjeta.add(textos, BorderLayout.CENTER);
        return tarjeta;
    }

    static JComponent circuloIcono(Icono.Tipo tipo, Color fondo, Color color) {
        Icono icono = new Icono(tipo, 20, color);
        JComponent circulo = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizado(g);
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Double(0, 0, 42, 42, 14, 14));
                icono.paintIcon(this, g2, 11, 11);
                g2.dispose();
            }
        };
        circulo.setPreferredSize(new Dimension(42, 42));
        return circulo;
    }

    // ---------- accesos a los modulos ----------

    private JComponent crearModulos() {
        JPanel fila = new JPanel(new GridLayout(1, 3, 16, 0));
        fila.setOpaque(false);

        fila.add(modulo("pedidos", Icono.Tipo.PEDIDOS, "Gestion de pedidos",
                "Registre pedidos con fecha limite y prioridad, ordenelos y busquelos por ID.",
                "Merge Sort", "Busqueda por ID"));

        fila.add(modulo("rutas", Icono.Tipo.RUTAS, "Planificacion de rutas",
                "Camino mas rapido, recorridos con varias entregas y rutas con restricciones.",
                "Dijkstra", "Floyd-Warshall", "Multiples entregas", "Alternativas"));

        fila.add(modulo("cargas", Icono.Tipo.CARGAS, "Seleccion de cargas",
                "Elija la combinacion de lotes de mayor prioridad que entra en el vehiculo.",
                "Mochila 0/1"));

        return fila;
    }

    private JComponent modulo(String seccion, Icono.Tipo icono, String titulo,
                              String descripcion, String... etiquetas) {

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 14));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel arriba = new JPanel(new BorderLayout(0, 12));
        arriba.setOpaque(false);

        JPanel filaIcono = new JPanel(new BorderLayout());
        filaIcono.setOpaque(false);
        filaIcono.add(circuloIcono(icono, Tema.LINEA, Tema.NAVY_2), BorderLayout.WEST);

        JLabel flecha = new JLabel(new Icono(Icono.Tipo.FLECHA, 18, Tema.TEXTO_TENUE));
        filaIcono.add(flecha, BorderLayout.EAST);

        JPanel textos = new JPanel(new BorderLayout(0, 6));
        textos.setOpaque(false);
        textos.add(Tema.titulo(titulo, 16), BorderLayout.NORTH);
        textos.add(Tema.parrafo(descripcion, 210), BorderLayout.CENTER);

        arriba.add(filaIcono, BorderLayout.NORTH);
        arriba.add(textos, BorderLayout.CENTER);

        JPanel chips = new JPanel(new Tema.FlujoLayout(FlowLayout.LEFT, 0, 0));
        chips.setOpaque(false);
        for (String etiqueta : etiquetas) {
            Insignia chip = new Insignia(etiqueta, Tema.TURQUESA_OSCURO, Tema.TURQUESA_SUAVE);
            JPanel espacio = new JPanel(new BorderLayout());
            espacio.setOpaque(false);
            espacio.setBorder(new EmptyBorder(0, 0, 6, 6));
            espacio.add(chip);
            chips.add(espacio);
        }

        tarjeta.add(arriba, BorderLayout.NORTH);
        tarjeta.add(chips, BorderLayout.CENTER);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setColores(Tema.SUPERFICIE, Tema.TURQUESA);
                flecha.setIcon(new Icono(Icono.Tipo.FLECHA, 18, Tema.TURQUESA));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!tarjeta.contains(e.getPoint())) {
                    tarjeta.setColores(Tema.SUPERFICIE, Tema.BORDE);
                    flecha.setIcon(new Icono(Icono.Tipo.FLECHA, 18, Tema.TEXTO_TENUE));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                navegar.accept(seccion);
            }
        });

        return tarjeta;
    }

    // ---------- tecnicas del proyecto ----------

    private JComponent crearTecnicas() {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new BorderLayout(18, 0));
        tarjeta.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel consola = Tema.texto("Todas las funciones tambien estan en el menu de consola.");
        consola.setFont(Tema.fuente(Font.PLAIN, 12));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(Tema.titulo("Tecnicas aplicadas", 14));
        textos.add(consola);

        JPanel chips = new JPanel(new Tema.FlujoLayout(FlowLayout.RIGHT, 8, 0));
        chips.setOpaque(false);
        for (String tecnica : new String[]{"Divide y Conquista", "Greedy", "Programacion Dinamica", "Backtracking"}) {
            chips.add(new Insignia(tecnica, Tema.NAVY_2, Tema.LINEA));
        }

        JPanel contenedorChips = new JPanel(new GridBagLayout());
        contenedorChips.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        contenedorChips.add(chips, c);

        tarjeta.add(textos, BorderLayout.WEST);
        tarjeta.add(contenedorChips, BorderLayout.CENTER);
        return tarjeta;
    }

    // mensaje para los modulos que dependen de la red cuando no se pudo cargar
    static JComponent sinDatos() {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new GridBagLayout());

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setOpaque(false);

        JPanel icono = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        icono.setOpaque(false);
        icono.add(circuloIcono(Icono.Tipo.CONEXION, Tema.ROJO_SUAVE, Tema.ROJO));

        JLabel titulo = Tema.titulo("No se pudo cargar la red vial", 17);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel detalle = Tema.texto("Revise los archivos datos/ubicaciones.txt y datos/conexiones.txt.");
        detalle.setHorizontalAlignment(SwingConstants.CENTER);

        contenido.add(icono, BorderLayout.NORTH);
        contenido.add(titulo, BorderLayout.CENTER);
        contenido.add(detalle, BorderLayout.SOUTH);
        tarjeta.add(contenido);
        return tarjeta;
    }
}
