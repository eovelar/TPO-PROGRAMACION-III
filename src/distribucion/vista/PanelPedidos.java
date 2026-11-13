package distribucion.vista;

import distribucion.modelo.Pedido;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;
import distribucion.servicios.GestorPedidos;
import distribucion.vista.Tema.Boton;
import distribucion.vista.Tema.CampoTexto;
import distribucion.vista.Tema.Insignia;
import distribucion.vista.Tema.Segmentado;
import distribucion.vista.Tema.Tarjeta;
import distribucion.vista.Tema.Variante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class PanelPedidos extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale ARGENTINA = new Locale("es", "AR");

    private final GestorPedidos gestor = new GestorPedidos();

    private CampoTexto campoId;
    private CampoTexto campoFecha;
    private CampoTexto campoPrioridad;
    private CampoTexto campoPeso;
    private CampoTexto campoBusqueda;
    private JComboBox<Ubicacion> comboCentro;
    private JLabel estado;

    private Segmentado orden;
    private DefaultTableModel modeloTabla;
    private Insignia contador;
    private JLabel pieTabla;

    public PanelPedidos(RedVial red) {
        setOpaque(false);
        setLayout(new BorderLayout(20, 0));

        add(crearFormulario(), BorderLayout.WEST);
        add(crearListado(), BorderLayout.CENTER);

        cargarCentros(red);
        mostrarTodos();
    }

    // ---------- formulario ----------

    private JComponent crearFormulario() {
        Tarjeta tarjeta = new Tarjeta("Nuevo pedido", "Complete los datos y agreguelo a la lista");
        tarjeta.setPreferredSize(new Dimension(310, 10));

        campoId = new CampoTexto("Ej. 101");
        campoFecha = new CampoTexto("AAAA-MM-DD");
        campoPrioridad = new CampoTexto("Ej. 3");
        campoPeso = new CampoTexto("Ej. 120,5");
        campoFecha.setText(LocalDate.now().plusDays(7).toString());

        comboCentro = Tema.combo(valor -> ((Ubicacion) valor).getNombre());

        // enter en cualquier campo agrega el pedido
        for (JTextField campo : new JTextField[]{campoId, campoFecha, campoPrioridad, campoPeso}) {
            campo.addActionListener(e -> agregarPedido());
        }

        JPanel atajosFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        atajosFecha.setOpaque(false);
        for (int dias : new int[]{1, 3, 7, 15}) {
            Boton atajo = new Boton(dias == 1 ? "Ma\u00f1ana" : "+" + dias + " dias", Variante.FANTASMA);
            atajo.setFont(Tema.fuente(Font.BOLD, 11));
            atajo.setBorder(new EmptyBorder(4, 9, 4, 9));
            atajo.addActionListener(e -> campoFecha.setText(LocalDate.now().plusDays(dias).toString()));
            atajosFecha.add(atajo);
            atajosFecha.add(Box.createHorizontalStrut(6));
        }

        JPanel fecha = new JPanel(new BorderLayout(0, 8));
        fecha.setOpaque(false);
        fecha.add(campoFecha, BorderLayout.CENTER);
        fecha.add(atajosFecha, BorderLayout.SOUTH);

        Boton agregar = new Boton("Agregar pedido", Icono.Tipo.MAS, Variante.PRIMARIO);
        agregar.addActionListener(e -> agregarPedido());

        estado = Tema.texto("Los campos se validan al agregar.");
        estado.setFont(Tema.fuente(Font.PLAIN, 12.5f));

        tarjeta.add(Tema.formulario(16,
                Tema.campo("ID DEL PEDIDO", campoId),
                Tema.campo("CENTRO DE DESTINO", comboCentro),
                Tema.campo("FECHA LIMITE", fecha),
                Tema.fila(12, Tema.campo("PRIORIDAD", campoPrioridad), Tema.campo("PESO (KG)", campoPeso)),
                agregar,
                estado), BorderLayout.CENTER);

        return tarjeta;
    }

    // ---------- listado ----------

    private JComponent crearListado() {
        contador = new Insignia("0 pedidos", Tema.TEXTO_SUAVE, Tema.LINEA);
        orden = new Segmentado("Por fecha", "Por prioridad");
        orden.alCambiar(indice -> mostrarTodos());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(orden);

        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titulo.setOpaque(false);
        titulo.add(Tema.titulo("Pedidos registrados", 17));
        titulo.add(Box.createHorizontalStrut(10));
        titulo.add(contador);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(derecha, BorderLayout.EAST);

        // barra de busqueda
        campoBusqueda = new CampoTexto("Buscar pedido por ID", Icono.Tipo.BUSCAR);
        campoBusqueda.addActionListener(e -> buscarPedido());

        Boton buscar = new Boton("Buscar", Variante.OSCURO);
        buscar.addActionListener(e -> buscarPedido());

        Boton verTodos = new Boton("Ver todos", Variante.FANTASMA);
        verTodos.addActionListener(e -> {
            campoBusqueda.setText("");
            mostrarTodos();
        });

        JPanel botones = new JPanel(new GridLayout(1, 2, 8, 0));
        botones.setOpaque(false);
        botones.add(buscar);
        botones.add(verTodos);

        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setOpaque(false);
        barra.add(campoBusqueda, BorderLayout.CENTER);
        barra.add(botones, BorderLayout.EAST);

        JPanel arriba = new JPanel(new BorderLayout(0, 16));
        arriba.setOpaque(false);
        arriba.add(encabezado, BorderLayout.NORTH);
        arriba.add(barra, BorderLayout.CENTER);

        // tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Centro de destino", "Fecha limite", "Vence", "Prioridad", "Peso"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new TablaConMensaje(modeloTabla,
                "Todavia no hay pedidos", "Los pedidos que agregue van a aparecer aca.");
        Tema.estilizarTabla(tabla);

        int[] anchos = {55, 150, 115, 105, 100, 90};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);

        tabla.getColumnModel().getColumn(2).setCellRenderer(new RendererTexto(
                valor -> ((LocalDate) valor).format(FORMATO_FECHA), SwingConstants.LEFT));
        tabla.getColumnModel().getColumn(3).setCellRenderer(new RendererVencimiento());
        tabla.getColumnModel().getColumn(4).setCellRenderer(new RendererPrioridad());
        tabla.getColumnModel().getColumn(5).setCellRenderer(new RendererTexto(
                valor -> String.format(ARGENTINA, "%.1f kg", (Double) valor), SwingConstants.LEFT));

        pieTabla = Tema.texto(" ");
        pieTabla.setFont(Tema.fuente(Font.PLAIN, 12));

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 16));
        tarjeta.add(arriba, BorderLayout.NORTH);
        tarjeta.add(Tema.marcoTabla(tabla), BorderLayout.CENTER);
        tarjeta.add(pieTabla, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void cargarCentros(RedVial red) {
        if (red == null) {
            Tema.estadoError(estado, "No se pudieron cargar los centros comunitarios.");
            return;
        }
        for (Ubicacion u : red.getUbicaciones()) {
            if (u.getTipo() == TipoUbicacion.CENTRO_COMUNITARIO) {
                comboCentro.addItem(u);
            }
        }
    }

    // ---------- acciones ----------

    private void agregarPedido() {
        try {
            int id = Integer.parseInt(campoId.getText().trim());
            LocalDate fecha = LocalDate.parse(campoFecha.getText().trim());
            int prioridad = Integer.parseInt(campoPrioridad.getText().trim());
            double peso = Double.parseDouble(campoPeso.getText().trim().replace(',', '.'));

            if (!Double.isFinite(peso)) {
                throw new IllegalArgumentException("peso no valido");
            }

            Ubicacion centro = (Ubicacion) comboCentro.getSelectedItem();
            if (centro == null) {
                throw new IllegalArgumentException("seleccione un centro comunitario");
            }

            gestor.agregarPedido(new Pedido(id, centro, fecha, prioridad, peso));

            campoBusqueda.setText("");
            mostrarTodos();

            campoId.setText("");
            campoPrioridad.setText("");
            campoPeso.setText("");
            campoId.requestFocusInWindow();

            Tema.estadoOk(estado, "Pedido " + id + " agregado.");

        } catch (NumberFormatException e) {
            Tema.estadoError(estado, "Ingrese valores numericos validos.");
        } catch (DateTimeParseException e) {
            Tema.estadoError(estado, "La fecha debe tener formato AAAA-MM-DD.");
        } catch (IllegalArgumentException e) {
            Tema.estadoError(estado, primeraMayuscula(e.getMessage()));
        }
    }

    private void buscarPedido() {
        try {
            int id = Integer.parseInt(campoBusqueda.getText().trim());
            Pedido pedido = gestor.buscarPedidoPorId(id);

            if (pedido == null) {
                pieTabla.setForeground(Tema.ROJO);
                pieTabla.setText("No se encontro el pedido " + id + ".");
                return;
            }

            llenarTabla(new Pedido[]{pedido});
            pieTabla.setForeground(Tema.TURQUESA_OSCURO);
            pieTabla.setText("Resultado de la busqueda: pedido " + id + ". Use \"Ver todos\" para volver al listado.");

        } catch (NumberFormatException e) {
            pieTabla.setForeground(Tema.ROJO);
            pieTabla.setText("Ingrese un ID numerico para buscar.");
        }
    }

    private void mostrarTodos() {
        boolean porFecha = orden.getSeleccion() == 0;
        Pedido[] pedidos = porFecha ? gestor.obtenerPedidosPorFecha() : gestor.obtenerPedidosPorPrioridad();
        llenarTabla(pedidos);

        pieTabla.setForeground(Tema.TEXTO_SUAVE);
        pieTabla.setText(pedidos.length == 0 ? " "
                : porFecha ? "Ordenados por fecha limite, del mas proximo al mas lejano."
                : "Ordenados por prioridad.");
    }

    private void llenarTabla(Pedido[] pedidos) {
        modeloTabla.setRowCount(0);

        for (Pedido p : pedidos) {
            modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getCentroDestino().getNombre(),
                    p.getFechaLimite(),
                    p.getFechaLimite(),
                    p.getPrioridad(),
                    p.getPesoTotal()
            });
        }

        int total = gestor.obtenerPedidosPorFecha().length;
        contador.setText(total + (total == 1 ? " pedido" : " pedidos"));
    }

    private static String primeraMayuscula(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "Datos no validos.";
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1) + ".";
    }

    // ================= tabla y renderers =================

    // tabla que muestra un mensaje cuando no tiene filas
    static class TablaConMensaje extends JTable {

        private final String titulo;
        private final String detalle;

        TablaConMensaje(DefaultTableModel modelo, String titulo, String detalle) {
            super(modelo);
            this.titulo = titulo;
            this.detalle = detalle;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getRowCount() > 0) {
                return;
            }

            Graphics2D g2 = Tema.suavizado(g);
            Rectangle visible = getVisibleRect();
            int centroX = visible.x + visible.width / 2;
            int centroY = visible.y + visible.height / 2;

            new Icono(Icono.Tipo.PEDIDOS, 34, Tema.BORDE).paintIcon(this, g2, centroX - 17, centroY - 48);

            g2.setFont(Tema.fuente(Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Tema.TEXTO);
            g2.drawString(titulo, centroX - fm.stringWidth(titulo) / 2, centroY + 6);

            g2.setFont(Tema.fuente(Font.PLAIN, 12.5f));
            fm = g2.getFontMetrics();
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.drawString(detalle, centroX - fm.stringWidth(detalle) / 2, centroY + 26);
            g2.dispose();
        }
    }

    // muestra un valor con el formato indicado
    static class RendererTexto extends DefaultTableCellRenderer {

        private final java.util.function.Function<Object, String> formato;

        RendererTexto(java.util.function.Function<Object, String> formato, int alineacion) {
            this.formato = formato;
            setHorizontalAlignment(alineacion);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionado, false, fila, columna);
            setText(valor == null ? "" : formato.apply(valor));
            setBorder(new EmptyBorder(0, 14, 0, 14));
            return this;
        }
    }

    // celda con una pastilla adentro
    private abstract static class RendererPastilla implements TableCellRenderer {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        private final Insignia insignia = new Insignia("", Tema.TEXTO, Tema.LINEA);

        RendererPastilla() {
            panel.add(insignia);
        }

        abstract void configurar(Insignia insignia, Object valor);

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
                                                       boolean foco, int fila, int columna) {
            panel.setBackground(seleccionado ? tabla.getSelectionBackground() : tabla.getBackground());
            configurar(insignia, valor);
            return panel;
        }
    }

    // dias que faltan hasta la fecha limite
    private static class RendererVencimiento extends RendererPastilla {

        @Override
        void configurar(Insignia insignia, Object valor) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), (LocalDate) valor);

            if (dias < 0) {
                insignia.cambiar("Vencido", Tema.ROJO, Tema.ROJO_SUAVE);
            } else if (dias == 0) {
                insignia.cambiar("Hoy", Tema.AMBAR, Tema.AMBAR_SUAVE);
            } else if (dias == 1) {
                insignia.cambiar("Ma\u00f1ana", Tema.AMBAR, Tema.AMBAR_SUAVE);
            } else if (dias <= 3) {
                insignia.cambiar("En " + dias + " dias", Tema.AMBAR, Tema.AMBAR_SUAVE);
            } else {
                insignia.cambiar("En " + dias + " dias", Tema.TEXTO_SUAVE, Tema.LINEA);
            }
        }
    }

    private static class RendererPrioridad extends RendererPastilla {

        @Override
        void configurar(Insignia insignia, Object valor) {
            insignia.cambiar("P" + valor, Tema.NAVY_2, new Color(228, 234, 240));
        }
    }
}
