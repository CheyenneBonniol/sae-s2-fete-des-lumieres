package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;

/**
 * Classe représentant les arrêtes entre les points
 * @author Cheyenne
 */
public class Arrete extends JComponent {
    private Point2D p1, p2;
    private Line2D ligne;
    private String toolTipText;
    private static final int TOLERANCE = 8;
    
    /**
     * Constructeur
     * @param p1 point de départ
     * @param p2 point d'arrivée
     * @param distance distances entre les points
     */
    public Arrete(Point2D p1, Point2D p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.toolTipText = "Distance : " + String.format("%.2f", distance);
        this.ligne = new Line2D.Double(p1, p2);

        setToolTipText(toolTipText);
        ToolTipManager.sharedInstance().registerComponent(this);

        updateBounds();
        
        // Écouteur pour le mouvement de la souris
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (ligne.ptSegDist(e.getX(), e.getY()) <= TOLERANCE) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
    
    /**
     * Cette fonction ajuste les axes et les boutons par rapport au repère
     */
    private void updateBounds() {
        int x = (int) Math.min(p1.getX(), p2.getX());
        int y = (int) Math.min(p1.getY(), p2.getY());
        int width = (int) Math.abs(p1.getX() - p2.getX());
        int height = (int) Math.abs(p1.getY() - p2.getY());
        setBounds(x , y , width + 7, height + 7); 
    }
    
    /**
     * Cette fonction dessine l'arrête
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);

        g2.setColor(new Color(142, 22, 22));
        g2.setStroke(new BasicStroke(2));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double x1 = p1.getX() - getX();
        double y1 = p1.getY() - getY();
        double x2 = p2.getX() - getX();
        double y2 = p2.getY() - getY();

        ligne.setLine((int)x1 + 8, (int)y1 + 8, (int)x2 + 8, (int)y2 + 8);
        g2.draw(ligne);
    }
    
    @Override
    public String getToolTipText(MouseEvent e) {
        if (ligne.ptSegDist(e.getX() + 8, e.getY() + 8) <= TOLERANCE) {
            return toolTipText;
        }
        return null;
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        // Ignore les clics pour permettre au parent de recevoir les événements
        if (e.getID() == MouseEvent.MOUSE_PRESSED || e.getID() == MouseEvent.MOUSE_RELEASED) {
            return; // Ne rien faire
        }
        super.processMouseEvent(e); // Appeler la méthode parente pour d'autres événements
    }
}
