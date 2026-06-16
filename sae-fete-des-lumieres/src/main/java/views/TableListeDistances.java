/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import java.awt.FlowLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import models.Lieu;

/**
 *  Fenêtre contenant la table des distances intégrale
 * @author Livio
 */
public class TableListeDistances extends JFrame {
    private JTable table;
    
    /**
     * Constructeur
     * @param listModel le modèle de la jliste des lieux
     */
    public TableListeDistances(DefaultListModel listModel) {
        super("Liste intégrale des distances");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComposants(listModel);
        this.setVisible(true);
        this.pack();
    }
    
    /**
     * Initialisation des composants de la fenêtre
     * @param listModel  le modèle de la jliste des lieux
     */
    public void initComposants(DefaultListModel listModel) {
        JPanel panneauGlobal = new JPanel();
        panneauGlobal.setLayout(new FlowLayout());
        Object[] lieux = listModel.toArray();
        String[][] distances = new String[lieux.length][lieux.length];
        
        String[] col = new String[lieux.length+1];
        col[0] = "";
        for (int i = 0; i < lieux.length; i ++) {
            col[i+1] = Integer.toString( ((Lieu) lieux[i]).getId() );
        }
        
        for (int i = 0; i < lieux.length; i++) {
            Lieu lieu1 = (Lieu) lieux[i];
            String[] temp = new String[lieux.length+1];
            temp[0] = Integer.toString(lieu1.getId());
            for (int j = 0; j < lieux.length; j++) {
                Lieu lieu2 = (Lieu) lieux[j];
                if (lieu1.getId() == lieu2.getId()) {
                    temp[j+1] = "X";
                } else {
                    temp[j+1] = String.format("%.2f", lieu1.calculDistance(lieu2));
                }
            }
            distances[i] = temp;
        }
        
        table = new JTable(distances, col);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrl = new JScrollPane();
        scrl.setViewportView(table);
        panneauGlobal.add(scrl);
        
        this.setContentPane(panneauGlobal);
    }
}
