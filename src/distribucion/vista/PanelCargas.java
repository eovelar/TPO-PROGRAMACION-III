package distribucion.vista;

import distribucion.algoritmos.SeleccionCargas;
import distribucion.modelo.LoteMercaderia;
import distribucion.modelo.Vehiculo;
import distribucion.vista.Tema.Boton;
import distribucion.vista.Tema.CampoTexto;
import distribucion.vista.Tema.Insignia;
import distribucion.vista.Tema.Tarjeta;
import distribucion.vista.Tema.Variante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PanelCargas extends JPanel {

    private CampoTexto campoCapacidad;
    private CampoTexto campoPeso;
    private CampoTexto campoPrioridad;
    private JLabel estado;

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private Insignia contador;
    private MedidorCapacidad medidor;

    private JTextArea areaResultado;
    private Insignia estadoResultado;

    private final List<LoteMercaderia> lotes = new ArrayList<>();
    private int proximoId = 1;

    public PanelCargas() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 0));

        add(crearFormulario(), BorderLayout.WEST);
        add(crearCentro(), BorderLayout.CENTER);

        actualizarMedidor();
    }

    // ---------- formulario ----------

    private JComponent crearFormulario() {
        Tarjeta tarjeta = new Tarjeta("Vehiculo y lotes", "Defina la capacidad y cargue los lotes");
        tarjeta.setPreferredSize(new Dimension(310, 10));

        campoCapacidad = new CampoTexto("Ej. 50");
        campoCapacidad.setText("50");
        campoCapacidad.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarMedidor();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarMedidor();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarMedidor();
            }
        });

        campoPeso = new CampoTexto("Ej. 12");
        campoPrioridad = new CampoTexto("Ej. 4");
        campoPeso.addActionListener(e -> agregarLote());
        campoPrioridad.addActionListener(e -> agregarLote());

        Boton agregar = new Boton("Agregar lote", Icono.Tipo.MAS, Variante.PRIMARIO);
        agregar.addActionListener(e -> agregarLote());

        estado = Tema.texto("Agregue lotes con su peso y prioridad.");
        estado.setFont(Tema.fuente(Font.PLAIN, 12.5f));

        JLabel subtitulo = Tema.titulo("Nuevo lote", 14);

        tarjeta.add(Tema.formulario(16,
                Tema.campo("CAPACIDAD DEL VEHICULO (KG)", campoCapacidad),
                Tema.separador(),
                subtitulo,
                Tema.fila(12, Tema.campo("PESO (KG)", campoPeso), Tema.campo("PRIORIDAD", campoPrioridad)),
                agregar,
                estado), BorderLayout.CENTER);

        return tarjeta;
    }

    // ---------- lotes y resultado ----------

    private JComponent crearCentro() {
        // la tabla de lotes se queda con el espacio libre; el resultado tiene alto fijo
        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        centro.add(crearListado(), BorderLayout.CENTER);
        centro.add(crearResultado(), BorderLayout.SOUTH);
        return centro;
    }

    private JComponent crearListado() {
        contador = new Insignia("0 lotes", Tema.TEXTO_SUAVE, Tema.LINEA);

        Boton quitar = new Boton("Quitar", Icono.Tipo.PAPELERA, Variante.FANTASMA);
        quitar.setFont(Tema.fuente(Font.BOLD, 12));
        quitar.setBorder(new EmptyBorder(7, 12, 7, 12));
        quitar.addActionListener(e -> quitarSeleccionado());

        Boton vaciar = new Boton("Vaciar", Variante.FANTASMA);
        vaciar.setFont(Tema.fuente(Font.BOLD, 12));
        vaciar.setBorder(new EmptyBorder(7, 12, 7, 12));
        vaciar.addActionListener(e -> vaciar());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(quitar);
        acciones.add(vaciar);

        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titulo.setOpaque(false);
        titulo.add(Tema.titulo("Lotes disponibles", 17));
        titulo.add(Box.createHorizontalStrut(10));
        titulo.add(contador);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(acciones, BorderLayout.EAST);

        medidor = new MedidorCapacidad();

        JPanel arriba = new JPanel(new BorderLayout(0, 14));
        arriba.setOpaque(false);
        arriba.add(encabezado, BorderLayout.NORTH);
        arriba.add(medidor, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(new Object[]{"Lote", "Peso", "Prioridad"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new PanelPedidos.TablaConMensaje(modeloTabla,
                "Sin lotes cargados", "Use el formulario de la izquierda para agregarlos.");
        Tema.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(1).setCellRenderer(new PanelPedidos.RendererTexto(
                valor -> valor + " kg", SwingConstants.LEFT));
        tabla.getColumnModel().getColumn(0).setCellRenderer(new PanelPedidos.RendererTexto(
                valor -> "Lote " + valor, SwingConstants.LEFT));

        Boton calcular = new Boton("Calcular seleccion optima", Icono.Tipo.CAMION, Variante.PRIMARIO);
        calcular.addActionListener(e -> calcularSeleccion());

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 14));
        tarjeta.add(arriba, BorderLayout.NORTH);
        tarjeta.add(Tema.marcoTabla(tabla), BorderLayout.CENTER);
        tarjeta.add(calcular, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JComponent crearResultado() {
        estadoResultado = new Insignia("SIN CALCULAR", Tema.TEXTO_SUAVE, Tema.LINEA);

        Tarjeta tarjeta = new Tarjeta("Resultado de Mochila 0/1",
                "Combinacion de lotes con mayor prioridad total", estadoResultado);

        areaResultado = Tema.consola("Agregue lotes y calcule la seleccion.");

        JComponent consola = Tema.marcoConsola(areaResultado);
        consola.setPreferredSize(new Dimension(300, 120));
        tarjeta.add(consola, BorderLayout.CENTER);
        return tarjeta;
    }

    // ---------- acciones ----------

    private void agregarLote() {
        try {
            int peso = Integer.parseInt(campoPeso.getText().trim());
            int prioridad = Integer.parseInt(campoPrioridad.getText().trim());

            LoteMercaderia lote = new LoteMercaderia(proximoId, peso, prioridad);
            lotes.add(lote);
            modeloTabla.addRow(new Object[]{lote.getId(), lote.getPeso(), lote.getPrioridad()});
            proximoId++;

            campoPeso.setText("");
            campoPrioridad.setText("");
            campoPeso.requestFocusInWindow();

            Tema.estadoOk(estado, "Lote " + lote.getId() + " agregado.");
            cambioLaLista();

        } catch (NumberFormatException e) {
            Tema.estadoError(estado, "Ingrese valores numericos enteros.");
        } catch (IllegalArgumentException e) {
            Tema.estadoError(estado, e.getMessage());
        }
    }

    private void quitarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Tema.estadoNeutro(estado, "Seleccione un lote de la tabla para quitarlo.");
            return;
        }

        LoteMercaderia lote = lotes.remove(fila);
        modeloTabla.removeRow(fila);
        Tema.estadoNeutro(estado, "Lote " + lote.getId() + " quitado.");
        cambioLaLista();
    }

    private void vaciar() {
        lotes.clear();
        modeloTabla.setRowCount(0);
        proximoId = 1;
        Tema.estadoNeutro(estado, "Lista de lotes vaciada.");
        cambioLaLista();
    }

    // cualquier cambio en los lotes invalida el resultado anterior
    private void cambioLaLista() {
        contador.setText(lotes.size() + (lotes.size() == 1 ? " lote" : " lotes"));
        estadoResultado.cambiar("SIN CALCULAR", Tema.TEXTO_SUAVE, Tema.LINEA);
        areaResultado.setText(lotes.isEmpty()
                ? "Agregue lotes y calcule la seleccion."
                : "La lista cambio. Calcule la seleccion para ver el resultado.");
        actualizarMedidor();
    }

    private void calcularSeleccion() {
        try {
            int capacidad = Integer.parseInt(campoCapacidad.getText().trim());

            if (lotes.isEmpty()) {
                Tema.estadoError(estado, "Agregue al menos un lote.");
                return;
            }

            Vehiculo vehiculo = new Vehiculo(1, capacidad);
            LoteMercaderia[] arreglo = lotes.toArray(new LoteMercaderia[0]);

            areaResultado.setText(Tema.capturarSalida(() -> SeleccionCargas.seleccionar(arreglo, vehiculo)));
            areaResultado.setCaretPosition(0);

            estadoResultado.cambiar("CALCULADO", Tema.TURQUESA_OSCURO, Tema.TURQUESA_SUAVE);
            Tema.estadoOk(estado, "Seleccion calculada.");

        } catch (NumberFormatException e) {
            Tema.estadoError(estado, "Ingrese una capacidad valida.");
        } catch (IllegalArgumentException e) {
            Tema.estadoError(estado, e.getMessage());
        } catch (OutOfMemoryError e) {
            Tema.estadoError(estado, "Los datos superan la memoria disponible.");
        }
    }

    private void actualizarMedidor() {
        if (medidor == null) {
            return;
        }

        int total = 0;
        for (LoteMercaderia lote : lotes) {
            total += lote.getPeso();
        }

        int capacidad;
        try {
            capacidad = Integer.parseInt(campoCapacidad.getText().trim());
        } catch (NumberFormatException e) {
            capacidad = -1;
        }

        medidor.setValores(total, capacidad);
    }

    // barra que compara el peso de todos los lotes con la capacidad del vehiculo
    private static class MedidorCapacidad extends JComponent {

        private int total;
        private int capacidad = -1;

        MedidorCapacidad() {
            setPreferredSize(new Dimension(300, 60));
        }

        void setValores(int total, int capacidad) {
            this.total = total;
            this.capacidad = capacidad;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suavizado(g);
            int w = getWidth();

            g2.setFont(Tema.fuente(Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            int base = fm.getAscent();

            String izquierda = "Peso de todos los lotes: ";
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.drawString(izquierda, 0, base);
            g2.setFont(Tema.fuente(Font.BOLD, 12));
            g2.setColor(Tema.TEXTO);
            g2.drawString(total + " kg", fm.stringWidth(izquierda), base);

            String derecha = capacidad > 0 ? "Capacidad: " + capacidad + " kg" : "Capacidad no valida";
            g2.setColor(capacidad > 0 ? Tema.TEXTO : Tema.ROJO);
            g2.drawString(derecha, w - g2.getFontMetrics().stringWidth(derecha), base);

            // barra
            int y = 24;
            int alto = 10;
            g2.setColor(Tema.LINEA);
            g2.fill(new RoundRectangle2D.Double(0, y, w, alto, alto, alto));

            String mensaje;

            if (capacidad > 0) {
                double escala = Math.max(total, capacidad);
                double anchoCapacidad = w * capacidad / escala;
                double anchoTotal = w * total / escala;

                g2.setColor(Tema.TURQUESA);
                g2.fill(new RoundRectangle2D.Double(0, y, Math.min(anchoTotal, anchoCapacidad), alto, alto, alto));

                if (total > capacidad) {
                    g2.setColor(Tema.AMBAR);
                    g2.fill(new RoundRectangle2D.Double(anchoCapacidad - alto, y, anchoTotal - anchoCapacidad + alto,
                            alto, alto, alto));
                    g2.setColor(Tema.TURQUESA);
                    g2.fillRect((int) anchoCapacidad - alto, y, alto, alto);

                    g2.setColor(Tema.NAVY_2);
                    g2.fillRect((int) anchoCapacidad - 1, y - 4, 2, alto + 8);

                    mensaje = "Sobran " + (total - capacidad) + " kg: el algoritmo elige que lotes llevar.";
                } else if (total == 0) {
                    mensaje = "Todavia no hay lotes cargados.";
                } else {
                    mensaje = "Todos los lotes entran en el vehiculo (quedan " + (capacidad - total) + " kg libres).";
                }
            } else {
                mensaje = "Ingrese una capacidad mayor a cero.";
            }

            g2.setFont(Tema.fuente(Font.PLAIN, 12));
            g2.setColor(total > capacidad && capacidad > 0 ? Tema.AMBAR : Tema.TEXTO_SUAVE);
            g2.drawString(mensaje, 0, y + alto + 8 + fm.getAscent());
            g2.dispose();
        }
    }
}
