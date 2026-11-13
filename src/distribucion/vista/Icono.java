package distribucion.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

// iconos de linea dibujados con Java2D, sin archivos de imagen
final class Icono implements Icon {

    enum Tipo {
        INICIO, PEDIDOS, RUTAS, CARGAS, CAMION, SALIR, BUSCAR, DEPOSITO, CENTRO,
        CONEXION, RELOJ, FLECHA, ABAJO, INVERTIR, MAS, PAPELERA
    }

    private final Tipo tipo;
    private final int tamano;
    private final Color color;

    Icono(Tipo tipo, int tamano, Color color) {
        this.tipo = tipo;
        this.tamano = tamano;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return tamano;
    }

    @Override
    public int getIconHeight() {
        return tamano;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = Tema.suavizado(g);
        g2.translate(x, y);
        g2.scale(tamano / 24.0, tamano / 24.0);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // todas las figuras se dibujan en una grilla de 24x24
        switch (tipo) {
            case INICIO:
                g2.draw(caja(3, 3, 7.5, 7.5, 2.5));
                g2.draw(caja(13.5, 3, 7.5, 7.5, 2.5));
                g2.draw(caja(3, 13.5, 7.5, 7.5, 2.5));
                g2.draw(caja(13.5, 13.5, 7.5, 7.5, 2.5));
                break;

            case PEDIDOS:
                g2.draw(caja(5, 4.5, 14, 16.5, 3));
                g2.draw(caja(9, 2.5, 6, 4, 1.5));
                linea(g2, 9, 11, 15, 11);
                linea(g2, 9, 15, 13, 15);
                break;

            case RUTAS:
                g2.draw(circulo(6, 18, 2.5));
                g2.draw(circulo(18, 6, 2.5));
                g2.draw(trazo(6, 15.5, 6, 12, 18, 12, 18, 8.5, -1, -1));
                break;

            case CARGAS:
                g2.draw(trazo(3.5, 8, 12, 3.5, 20.5, 8, 20.5, 16, 12, 20.5, 3.5, 16, 3.5, 8, -1, -1));
                g2.draw(trazo(3.5, 8, 12, 12.5, 20.5, 8, -1, -1));
                linea(g2, 12, 12.5, 12, 20.5);
                break;

            case CAMION:
                g2.draw(caja(2, 6, 12, 10, 1.5));
                g2.draw(trazo(14, 9, 18, 9, 21.5, 12.5, 21.5, 16, 14, 16, -1, -1));
                g2.draw(circulo(6.5, 17.5, 2));
                g2.draw(circulo(17, 17.5, 2));
                break;

            case SALIR:
                g2.draw(trazo(13, 4, 6, 4, 6, 20, 13, 20, -1, -1));
                linea(g2, 10.5, 12, 20.5, 12);
                g2.draw(trazo(17, 8.5, 20.5, 12, 17, 15.5, -1, -1));
                break;

            case BUSCAR:
                g2.draw(circulo(10.5, 10.5, 6.5));
                linea(g2, 15.5, 15.5, 20.5, 20.5);
                break;

            case DEPOSITO:
                g2.draw(trazo(3, 20, 3, 9, 12, 4, 21, 9, 21, 20, -1, -1));
                g2.draw(trazo(8, 20, 8, 13, 16, 13, 16, 20, -1, -1));
                linea(g2, 2, 20, 22, 20);
                break;

            case CENTRO: {
                Path2D pin = new Path2D.Double();
                pin.moveTo(12, 21.5);
                pin.curveTo(6.5, 15.5, 5, 12.5, 5, 10);
                pin.curveTo(5, 6, 8, 3, 12, 3);
                pin.curveTo(16, 3, 19, 6, 19, 10);
                pin.curveTo(19, 12.5, 17.5, 15.5, 12, 21.5);
                g2.draw(pin);
                g2.draw(circulo(12, 10, 2.5));
                break;
            }

            case CONEXION:
                g2.draw(circulo(5, 17, 2.5));
                g2.draw(circulo(19, 7, 2.5));
                g2.draw(circulo(17, 18, 2.5));
                linea(g2, 7, 15.5, 17, 8.5);
                linea(g2, 7.5, 17.2, 14.5, 17.8);
                break;

            case RELOJ:
                g2.draw(circulo(12, 12, 9));
                g2.draw(trazo(12, 7, 12, 12, 15.5, 14, -1, -1));
                break;

            case FLECHA:
                g2.draw(trazo(9, 6, 15, 12, 9, 18, -1, -1));
                break;

            case ABAJO:
                g2.draw(trazo(6, 9, 12, 15, 18, 9, -1, -1));
                break;

            case INVERTIR:
                linea(g2, 8, 4, 8, 20);
                g2.draw(trazo(4.5, 7.5, 8, 4, 11.5, 7.5, -1, -1));
                linea(g2, 16, 4, 16, 20);
                g2.draw(trazo(12.5, 16.5, 16, 20, 19.5, 16.5, -1, -1));
                break;

            case MAS:
                linea(g2, 12, 5, 12, 19);
                linea(g2, 5, 12, 19, 12);
                break;

            case PAPELERA:
                linea(g2, 4, 7, 20, 7);
                g2.draw(trazo(9, 7, 9, 4.5, 15, 4.5, 15, 7, -1, -1));
                g2.draw(trazo(6, 7, 7, 20, 17, 20, 18, 7, -1, -1));
                break;

            default:
                break;
        }

        g2.dispose();
    }

    private static Shape caja(double x, double y, double w, double h, double r) {
        return new RoundRectangle2D.Double(x, y, w, h, r * 2, r * 2);
    }

    private static Shape circulo(double cx, double cy, double r) {
        return new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2);
    }

    private static void linea(Graphics2D g2, double x1, double y1, double x2, double y2) {
        g2.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    // polilinea a partir de pares x,y que termina en -1,-1
    private static Shape trazo(double... puntos) {
        Path2D camino = new Path2D.Double();
        camino.moveTo(puntos[0], puntos[1]);
        for (int i = 2; i + 1 < puntos.length && puntos[i] >= 0; i += 2) {
            camino.lineTo(puntos[i], puntos[i + 1]);
        }
        return camino;
    }
}
