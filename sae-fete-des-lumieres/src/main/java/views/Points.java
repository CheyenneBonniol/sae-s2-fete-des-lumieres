/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Classe représentant un point du repère
 * @author Cheyenne
 */
public class Points {
    private String id;
    private JButton point;
    private Interface inter;
    private double x, y;
    private Repere.RepereRemoveListener supprEcouteur;
    
    /**
     * Constructeur
     * @param id l'id associé au Lieu
     * @param x
     * @param y
     * @param inter l'interface associée
     * @param supprEcouteur 
     */
    public Points(String id, double x, double y, Interface inter, Repere.RepereRemoveListener supprEcouteur){
        this.id = id;
        this.inter = inter;
        this.x = x;
        this.y = y;
        this.supprEcouteur = supprEcouteur;
        point = new JButton("");
        point.setContentAreaFilled(false);
        point.setCursor(new Cursor(Cursor.HAND_CURSOR));
        point.setSize(15,15);
        point.setToolTipText(id+"\nx : "+String.format("%.2f", x)+"\ny: "+String.format("%.2f", y));
        
        
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
                        supprEcouteur.remove(Points.this);
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
    
}
