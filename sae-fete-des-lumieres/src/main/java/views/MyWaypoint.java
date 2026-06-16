
package views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Classe représentant un point sur la carte
 * @author Cheyenne
 */
public class MyWaypoint extends DefaultWaypoint{
    private String id;
    private JButton point;
    private final Interface inter;
    private WaypointRender.WaypointRemoveListener supprEcouteur;
    private Color color;
    
    /**
     * Constructeur
     * @param id l'id correspondant à un Lieu
     * @param coord Geoposition coordonnées
     * @param inter l'interface associée
     * @param supprEcouteur 
     */
    public MyWaypoint(String id, GeoPosition coord, Interface inter, WaypointRender.WaypointRemoveListener supprEcouteur) {
        super(coord);
        this.id = id;
        this.inter = inter;
        this.supprEcouteur = supprEcouteur;
        point = new JButton("");
        color=new Color(216, 64, 64);
        point.setContentAreaFilled(false);
        point.setIcon(new ImageIcon(createIcon(color)));
        point.setCursor(new Cursor(Cursor.HAND_CURSOR));
        point.setSize(15,15);
        
        point.setToolTipText(id+"\nlat : "+coord.getLatitude()+"\nlong: "+coord.getLongitude());
        //selectionne le lieu cliqué dans la liste de l'interface
        point.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inter.selectLieuListe(id);
            }
        });
        point.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem supprimer = new JMenuItem("Supprimer");
                popup.add(supprimer);

                supprimer.addActionListener(ev -> {
                    if (supprEcouteur != null) {
                        supprEcouteur.remove(MyWaypoint.this);//supprime le lieu
                    }
                });

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    });
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public JButton getPoint() {
        return point;
    }

    public void setPoint(JButton point) {
        this.point = point;
    }
    
    /**
     * Cette fonction créé l'image associée au point
     * @param color
     * @return 
     */
    private Image createIcon(Color color){
        int size = 15;
        BufferedImage icone = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icone.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, size-1, size-1);
        g2.dispose();
        
        return icone;
    }
    
    
}
