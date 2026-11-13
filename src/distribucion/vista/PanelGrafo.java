package distribucion.vista;

import distribucion.modelo.ConexionVial;
import distribucion.modelo.TipoUbicacion;
import distribucion.modelo.Ubicacion;
import distribucion.rutas.RedVial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// dibuja la red vial y permite elegir nodos y conexiones con el mouse
public class PanelGrafo extends JPanel {

    public interface Oyente {
        void nodoSeleccionado(Ubicacion ubicacion);

        void conexionSeleccionada(ConexionVial conexion);
    }

    private static final int RADIO = 22;
    private static final int SEPARACION = 10;
    private static final Color ARISTA = new Color(188, 199, 208);
    private static final Color ARISTA_INACTIVA = new Color(214, 221, 227);

    private final RedVial red;
    private final Map<Integer, Point2D.Double> posiciones = new HashMap<>();
    private final Map<ConexionVial, Line2D.Double> trazos = new HashMap<>();

    private List<Ubicacion> recorrido = new ArrayList<>();
    private final Set<Integer> marcados = new HashSet<>();
    private final Set<Integer> prohibidos = new HashSet<>();
    private int origenId = -1;
    private int destinoId = -1;

    private int nodoEncima = -1;
    private ConexionVial conexionEncima;
    private boolean conexionesEditables;
    private String ayuda = "";

    private double progreso = 1;
    private Timer animacion;
    private Oyente oyente;

    public PanelGrafo(RedVial red) {
        this.red = red;

        setOpaque(false);
        setPreferredSize(new Dimension(560, 420));
        setMinimumSize(new Dimension(380, 280));
        ToolTipManager.sharedInstance().registerComponent(this);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                actualizarEncima(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                nodoEncima = -1;
                conexionEncima = null;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (oyente == null) {
                    return;
                }
                if (nodoEncima >= 0) {
                    oyente.nodoSeleccionado(red.buscarUbicacionPorId(nodoEncima));
                } else if (conexionEncima != null && conexionesEditables) {
                    oyente.conexionSeleccionada(conexionEncima);
                }
            }
        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    // ---------- api para el panel de rutas ----------

    public void setOyente(Oyente oyente) {
        this.oyente = oyente;
    }

    public void mostrarRecorrido(List<Ubicacion> recorrido) {
        this.recorrido = recorrido == null ? new ArrayList<>() : new ArrayList<>(recorrido);
        animar();
    }

    public void limpiarRecorrido() {
        recorrido = new ArrayList<>();
        repaint();
    }

    public void setMarcados(Collection<Integer> ids) {
        marcados.clear();
        marcados.addAll(ids);
        repaint();
    }

    public void setProhibidos(Collection<Integer> ids) {
        prohibidos.clear();
        prohibidos.addAll(ids);
        repaint();
    }

    public void setExtremos(int origenId, int destinoId) {
        this.origenId = origenId;
        this.destinoId = destinoId;
        repaint();
    }

    public void setConexionesEditables(boolean editables) {
        conexionesEditables = editables;
        repaint();
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
        repaint();
    }

    // la ruta se dibuja tramo por tramo
    private void animar() {
        if (animacion != null) {
            animacion.stop();
        }

        int tramos = Math.max(1, recorrido.size() - 1);
        double duracion = Math.min(1400, 280.0 * tramos);
        progreso = 0;

        animacion = new Timer(16, e -> {
            progreso = Math.min(1, progreso + 16 / duracion);
            if (progreso >= 1) {
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        animacion.start();
    }

    // ---------- interaccion ----------

    private void actualizarEncima(Point punto) {
        int nodo = -1;
        ConexionVial conexion = null;

        for (Map.Entry<Integer, Point2D.Double> entrada : posiciones.entrySet()) {
            if (entrada.getValue().distance(punto) <= RADIO + 4) {
                nodo = entrada.getKey();
                break;
            }
        }

        if (nodo < 0) {
            double mejor = 7;
            for (Map.Entry<ConexionVial, Line2D.Double> entrada : trazos.entrySet()) {
                double distancia = entrada.getValue().ptSegDist(punto);
                if (distancia < mejor) {
                    mejor = distancia;
                    conexion = entrada.getKey();
                }
            }
        }

        if (nodo != nodoEncima || conexion != conexionEncima) {
            nodoEncima = nodo;
            conexionEncima = conexion;

            boolean clickeable = nodo >= 0 || (conexion != null && conexionesEditables);
            setCursor(Cursor.getPredefinedCursor(clickeable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            repaint();
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (nodoEncima >= 0) {
            Ubicacion u = red.buscarUbicacionPorId(nodoEncima);
            if (u == null) {
                return null;
            }
            String tipo = u.getTipo() == TipoUbicacion.DEPOSITO ? "Deposito" : "Centro comunitario";
            return "<html><b>" + u.getNombre() + "</b><br>" + tipo + " · ID " + u.getId() + "</html>";
        }

        if (conexionEncima != null) {
            String texto = "<html><b>" + conexionEncima.getOrigen().getNombre() + " → "
                    + conexionEncima.getDestino().getNombre() + "</b><br>"
                    + conexionEncima.getTiempoMinutos() + " min";

            if (!conexionEncima.isHabilitada()) {
                texto += " · deshabilitada";
            }
            if (conexionesEditables) {
                texto += "<br><i>Click para " + (conexionEncima.isHabilitada() ? "deshabilitar" : "habilitar") + "</i>";
            }
            return texto + "</html>";
        }

        return null;
    }

    // ---------- dibujo ----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = Tema.suavizado(g);

        dibujarCuadricula(g2);
        calcularPosiciones();
        trazos.clear();

        for (ConexionVial conexion : red.getConexiones()) {
            if (indiceEnRecorrido(conexion) < 0) {
                dibujarConexion(g2, conexion, -1);
            }
        }

        for (ConexionVial conexion : red.getConexiones()) {
            int indice = indiceEnRecorrido(conexion);
            if (indice >= 0) {
                dibujarConexion(g2, conexion, indice);
            }
        }

        for (Ubicacion ubicacion : red.getUbicaciones()) {
            dibujarUbicacion(g2, ubicacion);
        }

        dibujarLeyenda(g2);
        dibujarAyuda(g2);
        g2.dispose();
    }

    private void dibujarCuadricula(Graphics2D g2) {
        g2.setColor(new Color(232, 237, 242));
        for (int x = 12; x < getWidth(); x += 24) {
            for (int y = 12; y < getHeight(); y += 24) {
                g2.fillRect(x, y, 2, 2);
            }
        }
    }

    // ubicar los nodos en forma de elipse
    private void calcularPosiciones() {
        posiciones.clear();

        List<Ubicacion> ubicaciones = red.getUbicaciones();
        int cantidad = ubicaciones.size();

        double centroX = getWidth() / 2.0;
        double centroY = getHeight() / 2.0 + 4;
        double radioX = Math.max(60, (getWidth() - 190) / 2.0);
        double radioY = Math.max(50, (getHeight() - 150) / 2.0);

        for (int i = 0; i < cantidad; i++) {
            double angulo = 2 * Math.PI * i / cantidad - Math.PI / 2;
            posiciones.put(ubicaciones.get(i).getId(), new Point2D.Double(
                    centroX + Math.cos(angulo) * radioX,
                    centroY + Math.sin(angulo) * radioY));
        }
    }

    private void dibujarConexion(Graphics2D g2, ConexionVial conexion, int indiceRuta) {
        Point2D.Double origen = posiciones.get(conexion.getOrigen().getId());
        Point2D.Double destino = posiciones.get(conexion.getDestino().getId());

        if (origen == null || destino == null) {
            return;
        }

        double dx = destino.x - origen.x;
        double dy = destino.y - origen.y;
        double distancia = Math.hypot(dx, dy);

        if (distancia == 0) {
            return;
        }

        double ux = dx / distancia;
        double uy = dy / distancia;

        // separar las conexiones de ida y vuelta
        double desplazamiento = tieneConexionInversa(conexion) ? SEPARACION : 0;
        double px = -uy * desplazamiento;
        double py = ux * desplazamiento;

        double inicioX = origen.x + ux * RADIO + px;
        double inicioY = origen.y + uy * RADIO + py;
        double finX = destino.x - ux * (RADIO + 5) + px;
        double finY = destino.y - uy * (RADIO + 5) + py;

        trazos.put(conexion, new Line2D.Double(inicioX, inicioY, finX, finY));

        boolean activa = conexion.isHabilitada();
        boolean encima = conexion == conexionEncima;
        boolean destacada = indiceRuta >= 0;

        // cuanto de este tramo ya se dibujo en la animacion
        double avance = 1;
        if (destacada) {
            int tramos = Math.max(1, recorrido.size() - 1);
            avance = Math.max(0, Math.min(1, progreso * tramos - indiceRuta));
            if (avance <= 0) {
                destacada = false;
                avance = 1;
            }
        }

        double hastaX = inicioX + (finX - inicioX) * avance;
        double hastaY = inicioY + (finY - inicioY) * avance;

        if (destacada) {
            g2.setColor(Tema.conAlfa(Tema.TURQUESA, 45));
            g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Line2D.Double(inicioX, inicioY, hastaX, hastaY));
        }

        if (!activa) {
            g2.setColor(encima && conexionesEditables ? Tema.ROJO : ARISTA_INACTIVA);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{5f, 5f}, 0));
        } else if (destacada) {
            g2.setColor(Tema.TURQUESA);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        } else {
            g2.setColor(encima ? Tema.TEXTO_SUAVE : ARISTA);
            g2.setStroke(new BasicStroke(encima ? 2.6f : 1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }

        g2.draw(new Line2D.Double(inicioX, inicioY, hastaX, hastaY));

        if (avance >= 1) {
            dibujarPunta(g2, finX, finY, ux, uy, destacada ? 11 : 9);
            dibujarTiempo(g2, conexion, (inicioX + finX) / 2, (inicioY + finY) / 2, destacada, activa, encima);
        }
    }

    private void dibujarPunta(Graphics2D g2, double x, double y, double ux, double uy, double tamano) {
        double apertura = tamano * 0.5;
        Path2D punta = new Path2D.Double();
        punta.moveTo(x, y);
        punta.lineTo(x - ux * tamano - uy * apertura, y - uy * tamano + ux * apertura);
        punta.lineTo(x - ux * tamano + uy * apertura, y - uy * tamano - ux * apertura);
        punta.closePath();
        g2.fill(punta);
    }

    private void dibujarTiempo(Graphics2D g2, ConexionVial conexion, double x, double y,
                               boolean destacada, boolean activa, boolean encima) {

        String tiempo = conexion.getTiempoMinutos() + " min";
        g2.setFont(Tema.fuente(Font.BOLD, 10.5f));
        FontMetrics fm = g2.getFontMetrics();

        double ancho = fm.stringWidth(tiempo) + 12;
        double alto = 18;
        RoundRectangle2D pastilla = new RoundRectangle2D.Double(x - ancho / 2, y - alto / 2, ancho, alto, alto, alto);

        if (destacada) {
            g2.setColor(Tema.TURQUESA);
            g2.fill(pastilla);
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.WHITE);
            g2.fill(pastilla);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(encima ? Tema.TEXTO_TENUE : Tema.BORDE);
            g2.draw(pastilla);
            g2.setColor(activa ? Tema.TEXTO_SUAVE : Tema.TEXTO_TENUE);
        }

        g2.drawString(tiempo, (float) (x - fm.stringWidth(tiempo) / 2.0),
                (float) (y - fm.getHeight() / 2.0 + fm.getAscent()));
    }

    private void dibujarUbicacion(Graphics2D g2, Ubicacion ubicacion) {
        Point2D.Double p = posiciones.get(ubicacion.getId());
        if (p == null) {
            return;
        }

        int id = ubicacion.getId();
        boolean deposito = ubicacion.getTipo() == TipoUbicacion.DEPOSITO;
        boolean enRuta = nodoAlcanzado(id);
        boolean prohibido = prohibidos.contains(id);
        boolean marcado = marcados.contains(id);
        boolean encima = id == nodoEncima;

        // halo de estado
        Color halo = prohibido ? Tema.ROJO_SUAVE
                : enRuta ? new Color(200, 238, 233)
                : marcado ? Tema.AMBAR_SUAVE
                : encima ? Tema.LINEA : null;

        if (halo != null) {
            g2.setColor(halo);
            g2.fill(forma(deposito, p, RADIO + 8));
        }

        if (marcado && !prohibido) {
            g2.setColor(Tema.AMBAR);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(forma(deposito, p, RADIO + 4));
        }

        // sombra y cuerpo
        g2.setColor(new Color(16, 29, 42, 30));
        g2.fill(forma(deposito, new Point2D.Double(p.x, p.y + 2), RADIO));

        Color cuerpo = prohibido ? new Color(186, 196, 205) : deposito ? Tema.NAVY_2 : Tema.TURQUESA;
        g2.setColor(cuerpo);
        g2.fill(forma(deposito, p, RADIO));

        if (prohibido) {
            g2.setColor(Tema.ROJO);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Line2D.Double(p.x - RADIO * 0.7, p.y + RADIO * 0.7, p.x + RADIO * 0.7, p.y - RADIO * 0.7));
        }

        String texto = String.valueOf(id);
        g2.setColor(Color.WHITE);
        g2.setFont(Tema.fuente(Font.BOLD, 15));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (float) (p.x - fm.stringWidth(texto) / 2.0),
                (float) (p.y - fm.getHeight() / 2.0 + fm.getAscent()));

        // nombre debajo del nodo
        String nombre = ubicacion.getNombre();
        g2.setFont(Tema.fuente(Font.BOLD, 11.5f));
        FontMetrics fmNombre = g2.getFontMetrics();
        double anchoNombre = fmNombre.stringWidth(nombre) + 10;
        double yNombre = p.y + RADIO + 8;

        g2.setColor(new Color(255, 255, 255, 225));
        g2.fill(new RoundRectangle2D.Double(p.x - anchoNombre / 2, yNombre, anchoNombre, 18, 8, 8));
        g2.setColor(enRuta ? Tema.TURQUESA_OSCURO : prohibido ? Tema.ROJO : Tema.TEXTO);
        g2.drawString(nombre, (float) (p.x - fmNombre.stringWidth(nombre) / 2.0),
                (float) (yNombre + 9 - fmNombre.getHeight() / 2.0 + fmNombre.getAscent()));

        // marcas de origen y destino
        if (id == origenId) {
            dibujarMarca(g2, "ORIGEN", Tema.NAVY_2, p);
        } else if (id == destinoId) {
            dibujarMarca(g2, "DESTINO", Tema.TURQUESA_OSCURO, p);
        }
    }

    private void dibujarMarca(Graphics2D g2, String texto, Color color, Point2D.Double p) {
        g2.setFont(Tema.fuente(Font.BOLD, 9.5f));
        FontMetrics fm = g2.getFontMetrics();
        double ancho = fm.stringWidth(texto) + 12;
        double y = p.y - RADIO - 25;

        g2.setColor(color);
        g2.fill(new RoundRectangle2D.Double(p.x - ancho / 2, y, ancho, 17, 17, 17));
        g2.setColor(Color.WHITE);
        g2.drawString(texto, (float) (p.x - fm.stringWidth(texto) / 2.0),
                (float) (y + 8.5 - fm.getHeight() / 2.0 + fm.getAscent()));
    }

    // los depositos son cuadrados redondeados y los centros, circulos
    private Shape forma(boolean deposito, Point2D.Double p, double radio) {
        if (deposito) {
            return new RoundRectangle2D.Double(p.x - radio, p.y - radio, radio * 2, radio * 2, radio * 0.9, radio * 0.9);
        }
        return new Ellipse2D.Double(p.x - radio, p.y - radio, radio * 2, radio * 2);
    }

    private void dibujarLeyenda(Graphics2D g2) {
        g2.setFont(Tema.fuente(Font.PLAIN, 11.5f));
        FontMetrics fm = g2.getFontMetrics();
        int x = 4;
        int y = 14;

        g2.setColor(Tema.NAVY_2);
        g2.fill(new RoundRectangle2D.Double(x, y - 9, 11, 11, 4, 4));
        x = textoLeyenda(g2, fm, "Deposito", x + 17, y);

        g2.setColor(Tema.TURQUESA);
        g2.fillOval(x, y - 9, 11, 11);
        x = textoLeyenda(g2, fm, "Centro", x + 17, y);

        g2.setColor(Tema.TURQUESA);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x, y - 4, x + 20, y - 4);
        x = textoLeyenda(g2, fm, "Ruta", x + 26, y);

        g2.setColor(ARISTA_INACTIVA);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4f, 4f}, 0));
        g2.drawLine(x, y - 4, x + 20, y - 4);
        textoLeyenda(g2, fm, "Deshabilitada", x + 26, y);
    }

    private int textoLeyenda(Graphics2D g2, FontMetrics fm, String texto, int x, int y) {
        g2.setColor(Tema.TEXTO_SUAVE);
        g2.drawString(texto, x, y);
        return x + fm.stringWidth(texto) + 18;
    }

    private void dibujarAyuda(Graphics2D g2) {
        if (ayuda == null || ayuda.isEmpty()) {
            return;
        }
        g2.setFont(Tema.fuente(Font.PLAIN, 11.5f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Tema.TEXTO_TENUE);
        g2.drawString(ayuda, getWidth() - fm.stringWidth(ayuda) - 4, getHeight() - 6);
    }

    // ---------- consultas sobre la ruta ----------

    private boolean tieneConexionInversa(ConexionVial conexion) {
        int origen = conexion.getOrigen().getId();
        int destino = conexion.getDestino().getId();

        for (ConexionVial otra : red.getConexiones()) {
            if (otra.getOrigen().getId() == destino && otra.getDestino().getId() == origen) {
                return true;
            }
        }
        return false;
    }

    // posicion del tramo dentro del recorrido, o -1 si no pertenece
    private int indiceEnRecorrido(ConexionVial conexion) {
        if (!conexion.isHabilitada()) {
            return -1;
        }
        for (int i = 0; i < recorrido.size() - 1; i++) {
            if (recorrido.get(i).getId() == conexion.getOrigen().getId()
                    && recorrido.get(i + 1).getId() == conexion.getDestino().getId()) {
                return i;
            }
        }
        return -1;
    }

    // un nodo se resalta cuando la animacion llega hasta el
    private boolean nodoAlcanzado(int id) {
        int tramos = Math.max(1, recorrido.size() - 1);
        for (int i = 0; i < recorrido.size(); i++) {
            if (recorrido.get(i).getId() == id) {
                return i <= progreso * tramos + 0.0001;
            }
        }
        return false;
    }
}
