
package distribucion.vista;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelGrafo extends JPanel {

    private static final Color AZUL = new Color(27, 43, 58);
    private static final Color TURQUESA = new Color(19, 157, 151);
    private static final Color FONDO = Color.WHITE;
    private static final Color GRIS = new Color(170, 181, 190);
    private static final Color TEXTO = new Color(59, 73, 85);
    private static final Color INACTIVA = new Color(215, 221, 226);

    private final RedVial red;
    private final Map<Integer, Point> posiciones;
    private List<Ubicacion> recorrido;

    private static final int RADIO = 24;

    public PanelGrafo(RedVial red) {

        this.red = red;
        this.posiciones = new HashMap<>();
        this.recorrido = new ArrayList<>();

        setBackground(FONDO);
        setOpaque(true);
        setPreferredSize(new Dimension(650, 360));
        setMinimumSize(new Dimension(400, 260));
        setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    // actualizar el recorrido resaltado
    public void mostrarRecorrido(List<Ubicacion> recorrido) {

        if (recorrido == null) {
            this.recorrido = new ArrayList<>();
        } else {
            this.recorrido = new ArrayList<>(recorrido);
        }

        repaint();
    }

    // borrar el recorrido seleccionado
    public void limpiarRecorrido() {
        recorrido.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        calcularPosiciones();

        // dibujar primero las conexiones
        dibujarConexiones(g2);

        // dibujar luego las ubicaciones
        dibujarUbicaciones(g2);

        g2.dispose();
    }

    // distribuir las ubicaciones en el panel
    private void calcularPosiciones() {

        posiciones.clear();

        List<Ubicacion> ubicaciones = red.getUbicaciones();

        int cantidad = ubicaciones.size();

        if (cantidad == 0) {
            return;
        }

        int ancho = getWidth();
        int alto = getHeight();

        int centroX = ancho / 2;
        int centroY = alto / 2;

        int radioX = Math.max(60, (ancho - 190) / 2);
        int radioY = Math.max(55, (alto - 130) / 2);

        // distribuir los nodos en forma circular
        for (int i = 0; i < cantidad; i++) {

            double angulo =
                    (2 * Math.PI * i / cantidad) - Math.PI / 2;

            int x = centroX +
                    (int) (Math.cos(angulo) * radioX);

            int y = centroY +
                    (int) (Math.sin(angulo) * radioY);

            posiciones.put(
                    ubicaciones.get(i).getId(),
                    new Point(x, y)
            );
        }
    }

    // representar las conexiones de la red vial
    private void dibujarConexiones(Graphics2D g2) {

        for (ConexionVial conexion : red.getConexiones()) {

            Point origen = posiciones.get(
                    conexion.getOrigen().getId()
            );

            Point destino = posiciones.get(
                    conexion.getDestino().getId()
            );

            if (origen == null || destino == null) {
                continue;
            }

            boolean activa = conexion.isHabilitada();
            boolean destacada =
                    activa && perteneceAlRecorrido(conexion);

            Color color;

            if (!activa) {
                color = INACTIVA;
            } else if (destacada) {
                color = TURQUESA;
            } else {
                color = GRIS;
            }

            g2.setColor(color);

            if (!activa) {
                g2.setStroke(
                        new BasicStroke(
                                1.5f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND,
                                0,
                                new float[]{6f, 6f},
                                0
                        )
                );
            } else {
                g2.setStroke(
                        new BasicStroke(
                                destacada ? 3.5f : 1.7f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );
            }

            dibujarFlecha(g2, origen, destino);

            // mostrar el tiempo de la conexion
            int medioX = (origen.x + destino.x) / 2;
            int medioY = (origen.y + destino.y) / 2;

            g2.setFont(
                    new Font("SansSerif", Font.BOLD, 11)
            );

            String tiempo =
                    conexion.getTiempoMinutos() + " min";

            FontMetrics fm = g2.getFontMetrics();

            int textoAncho = fm.stringWidth(tiempo);

            g2.setColor(FONDO);
            g2.fillRoundRect(
                    medioX - textoAncho / 2 - 5,
                    medioY - 17,
                    textoAncho + 10,
                    19,
                    8,
                    8
            );

            g2.setColor(
                    destacada ? TURQUESA :
                            activa ? TEXTO : INACTIVA
            );

            g2.drawString(
                    tiempo,
                    medioX - textoAncho / 2,
                    medioY - 3
            );
        }
    }

    // dibujar una flecha entre dos ubicaciones
    private void dibujarFlecha(
            Graphics2D g2,
            Point origen,
            Point destino) {

        double dx = destino.x - origen.x;
        double dy = destino.y - origen.y;

        double distancia = Math.sqrt(dx * dx + dy * dy);

        if (distancia == 0) {
            return;
        }

        double ux = dx / distancia;
        double uy = dy / distancia;

        double inicioX = origen.x + ux * RADIO;
        double inicioY = origen.y + uy * RADIO;

        double finX = destino.x - ux * (RADIO + 3);
        double finY = destino.y - uy * (RADIO + 3);

        g2.draw(
                new Line2D.Double(
                        inicioX,
                        inicioY,
                        finX,
                        finY
                )
        );

        double tamano = 10;
        double apertura = 5;

        Path2D flecha = new Path2D.Double();

        flecha.moveTo(finX, finY);

        flecha.lineTo(
                finX - ux * tamano - uy * apertura,
                finY - uy * tamano + ux * apertura
        );

        flecha.lineTo(
                finX - ux * tamano + uy * apertura,
                finY - uy * tamano - ux * apertura
        );

        flecha.closePath();

        g2.fill(flecha);
    }

    // comprobar si una conexion pertenece al camino
    private boolean perteneceAlRecorrido(ConexionVial conexion) {

        for (int i = 0; i < recorrido.size() - 1; i++) {

            Ubicacion actual = recorrido.get(i);
            Ubicacion siguiente = recorrido.get(i + 1);

            if (actual.getId() == conexion.getOrigen().getId()
                    && siguiente.getId() == conexion.getDestino().getId()) {

                return true;
            }
        }

        return false;
    }

    // dibujar depositos y centros
    private void dibujarUbicaciones(Graphics2D g2) {

        for (Ubicacion ubicacion : red.getUbicaciones()) {

            Point posicion = posiciones.get(ubicacion.getId());

            if (posicion == null) {
                continue;
            }

            boolean deposito =
                    ubicacion.getTipo() == TipoUbicacion.DEPOSITO;

            Color color = deposito ? AZUL : TURQUESA;

            // circulo exterior
            g2.setColor(new Color(230, 237, 240));

            g2.fill(
                    new Ellipse2D.Double(
                            posicion.x - RADIO - 4,
                            posicion.y - RADIO - 4,
                            (RADIO + 4) * 2,
                            (RADIO + 4) * 2
                    )
            );

            // circulo principal
            g2.setColor(color);

            g2.fill(
                    new Ellipse2D.Double(
                            posicion.x - RADIO,
                            posicion.y - RADIO,
                            RADIO * 2,
                            RADIO * 2
                    )
            );

            // identificador del nodo
            String id = String.valueOf(ubicacion.getId());

            g2.setColor(Color.WHITE);
            g2.setFont(
                    new Font("SansSerif", Font.BOLD, 16)
            );

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                    id,
                    posicion.x - fm.stringWidth(id) / 2,
                    posicion.y + fm.getAscent() / 2 - 3
            );

            // nombre de la ubicación
            String nombre = ubicacion.getNombre();

            g2.setFont(
                    new Font("SansSerif", Font.BOLD, 11)
            );

            FontMetrics fmNombre = g2.getFontMetrics();

            g2.setColor(TEXTO);

            g2.drawString(
                    nombre,
                    posicion.x - fmNombre.stringWidth(nombre) / 2,
                    posicion.y + RADIO + 20
            );
        }
    }
}
