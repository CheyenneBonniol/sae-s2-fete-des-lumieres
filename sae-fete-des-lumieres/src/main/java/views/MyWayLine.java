package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Classe traçant les distances entre 2points
 * @author Cheyenne
 */
public class MyWayLine extends JComponent implements MouseListener, MouseMotionListener {

    private String toolTipText;
    private Color couleur;
    private Line2D ligne;

    private Point2D p1Pixel, p2Pixel;
    private Rectangle rec; 

    private static final int TOLERANCE_CLIC = 5;

    /**
     * Constructeur
     * @param distance 
     */
    public MyWayLine(double distance) {

        this.couleur = new Color(142, 22, 22);
        this.ligne = new Line2D.Double();
        this.toolTipText = String.format("%.2f", distance);

        addMouseListener(this);
        addMouseMotionListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void updateGeometry(Rectangle viewport, Point2D p1, Point2D p2) {
        this.rec = viewport;
        this.p1Pixel = p1;
        this.p2Pixel = p2;
        repaint(); 
    }
    
    /**
     * Cette fonction dessine les lignes entre deux points
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (p1Pixel == null || p2Pixel == null || rec == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        int x1 = (int) (p1Pixel.getX() - rec.getX());
        int y1 = (int) (p1Pixel.getY() - rec.getY());
        int x2 = (int) (p2Pixel.getX() - rec.getX());
        int y2 = (int) (p2Pixel.getY() - rec.getY());

        ligne.setLine(x1, y1, x2, y2);

        g2.setColor(couleur);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x1, y1, x2, y2);//on dessine les arretes

        g2.dispose();
    }

    /**
     * Cette fonction dit si le wayline contient le point
     * @param x
     * @param y
     * @return un booleen
     */
    public boolean contientPoint(int x, int y) {
        return ligne.ptSegDist(x, y) <= TOLERANCE_CLIC;
    }

    /**
     * Cette fonction renvoie le toomTipText si on est sur l'arrete actuelle
     * @param event
     * @return toolTipText String 
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        if (contientPoint(event.getX(), event.getY())) {
            return toolTipText;
        }
        return null;
    }

    @Override public void mouseClicked(MouseEvent e) {
        if (contientPoint(e.getX(), e.getY())) {
            setToolTipText(toolTipText);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {

        if (contientPoint(e.getX(), e.getY())) {
            setToolTipText(toolTipText);
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
}