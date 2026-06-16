package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import models.Chercheur;
import models.Lieu;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;

/**
 * Classe étant chargée de peindre les waypoints et les wayline sur la carte
 * @author Cheyenne
 */
public class WaypointRender extends WaypointPainter<MyWaypoint> {

    protected boolean tracer = false;
    private Map<String, Point2D> waypointPositions;
    private ArrayList<MyWayLine> lines;
    private ArrayList<Lieu> lieux;
    private Chercheur c;
    private JXMapViewer m;

    /**
     * Constructeur
     * @param m jxmapviewer
     */
    public WaypointRender(JXMapViewer m) {
        lines = new ArrayList<>();
        this.m = m;
    }
    
    /**
     * Fait repeindre la carte avec le chemin passé en paramêtre pour tracer les lignes
     * 
     * @param t booleen
     * @param c chercheur
     * @param lieux liste de lieux
     */
    public void setTracerChercheur(boolean t, Chercheur c, ArrayList<Lieu> lieux) {
        tracer = t;
        this.c = c;
        this.lieux = lieux;
        m.repaint();
    }
    
    /**
     * Cette classe peint les waypoints et les wayLines de la carte
     * @param g Graphics2D
     * @param map JXMapViewer
     * @param width int
     * @param height int
     */
    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        waypointPositions = new HashMap<>();
        lines.clear();
        Rectangle rec = map.getViewportBounds();

// Calcul des positions de tous les waypoints
        for (MyWaypoint wp : getWaypoints()) {
            Point2D p = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            waypointPositions.put(wp.getId(), p);
        }

        if (tracer && lieux.size() >= 2) {//si on veut tracer le chemin decoullant d'un algo
            g.setColor(Color.red);
            g.setStroke(new BasicStroke(2));
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < lieux.size() - 1; i++) {
                Lieu l1 = lieux.get(i);
                Lieu l2 = lieux.get(i + 1);
                Point2D p1 = waypointPositions.get(String.valueOf(l1.getId()));
                Point2D p2 = waypointPositions.get(String.valueOf(l2.getId()));

                double distance = l1.distances.get(l2.getId());//longueur de l'arrete correspondant a la distance entre les deux sommets/lieux
                MyWayLine arrete = new MyWayLine(distance);
                lines.add(arrete);
                arrete.updateGeometry(rec, p1, p2);
                arrete.paintComponent(g);

                // Boucle au premier si souhaité
                if (i == 0) {
                    Lieu lLast = lieux.get(lieux.size() - 1);
                    Point2D pLast = waypointPositions.get(String.valueOf(lLast.getId()));
                    double dist = l1.distances.get(lLast.getId());
                    MyWayLine last = new MyWayLine(dist);
                    lines.add(last);
                    last.updateGeometry(rec, p1, pLast);
                    last.paintComponent(g);
                }
            }
        }
        
        //on place tout les boutons
        for (MyWaypoint wp : getWaypoints()) {
            Point2D p = waypointPositions.get(wp.getId());
            if (p != null) {
                int x = (int) (p.getX() - rec.getX());
                int y = (int) (p.getY() - rec.getY());
                JButton cmd = wp.getPoint();
                cmd.setLocation(x - cmd.getWidth() / 2, y - cmd.getHeight() / 2);
            }
        }
    }

    public ArrayList<MyWayLine> getLines() {
        return lines;
    }
    
    public interface WaypointRemoveListener {
    void remove(MyWaypoint wp);
}
}
