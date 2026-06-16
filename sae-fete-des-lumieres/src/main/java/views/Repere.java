package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import models.Chercheur;
import models.Lieu;
import models.LieuEuclidien;
import models.ListeDesLieuxVideException;

/**
 * Classe représentant le repère euclidien
 *
 * @author Cheyenne
 */
public class Repere extends JComponent {

    private ArrayList<Point2D> listePoints;
    private ArrayList<Points> listePointsClick;
    private ArrayList<Lieu> listL;
    private ArrayList<Arrete> arretes = new ArrayList<>();
    private double echelleX, echelleY, ajustX, ajustY;
    private double EcranH, EcranL;
    private Point2D.Double milieu;
    private double maxX = Integer.MIN_VALUE, minX = Integer.MAX_VALUE;
    private double maxY = Integer.MIN_VALUE, minY = Integer.MAX_VALUE;
    private boolean tracer = false;
    private Chercheur c;
    private Interface inter;

    /**
     * Constructeur
     *
     * @param liste la liste de lieux
     * @param tailleCarte Dimension
     * @param inter l'interface associée
     */
    public Repere(ArrayList<Lieu> liste, Dimension tailleCarte, Interface inter) {
        listePoints = new ArrayList<>();
        listePointsClick = new ArrayList<>();
        listL = liste;
        this.inter = inter;
        this.EcranH = tailleCarte.getHeight();
        this.EcranL = tailleCarte.getWidth();
        init(liste);
        initEcouteur();
        this.setPreferredSize(tailleCarte);
        this.setLayout(null);
    }

    /**
     * Cette fonction permet de tracer les arretes du chemin
     *
     * @param tracer booléen
     * @param c Chercheur
     * @param lieux la liste de lieux
     */
    public void setTracerChercheurLieu(boolean tracer, Chercheur c, ArrayList<Lieu> lieux) {
        this.tracer = tracer;
        listL = lieux;
        this.c = c;

        init(lieux);
        this.repaint();
    }

    /**
     * Cette fonction met à jour le repère
     *
     * @param lieux
     */
    public void init(ArrayList<Lieu> lieux) {
        listePoints.clear();
        for (Points p : listePointsClick) {
            this.remove(p.getPoint());
        }
        listePointsClick.clear();
        clearArretes();
        for (Lieu li : lieux) {
            LieuEuclidien l = (LieuEuclidien) li;
            if (maxX < l.getX()) {
                maxX = l.getX();
            }
            if (minX > l.getX()) {
                minX = l.getX();
            }
            if (maxY < l.getY()) {
                maxY = l.getY();
            }
            if (minY > l.getY()) {
                minY = l.getY();
            }
        }
        echelleX = EcranL / (maxX - minX + (maxX / 15));
        echelleY = EcranH / (maxY - minY + (maxY / 15));
        milieu = new Point2D.Double((maxX + minX) / 2, (maxY + minY) / 2);
        ajustX = EcranL / 2 - ((milieu.getX() * echelleX));//milieu x
        ajustY = EcranH / 2 - ((milieu.getY() * echelleY));

        for (Lieu l : listL) {
            LieuEuclidien le = (LieuEuclidien) l;

            double x = (le.getX() * echelleX) + ajustX;
            double y = (le.getY() * echelleY) + ajustY;
            listePoints.add(new Point2D.Double(x, y));

            Points point = new Points(Integer.toString(le.getId()), le.getX(), le.getY(), inter, p -> {
                int index = listePointsClick.indexOf(p);
                this.remove(p.getPoint());
                listePointsClick.remove(p);

                if (index >= 0 && index < listePoints.size()) {
                    listePoints.remove(index);
                }
                Iterator<Lieu> iterator = inter.cheminCalcul.iterator();
                while (iterator.hasNext()) {
                    Lieu lieu = iterator.next();
                    if (lieu.getId() == Integer.parseInt(p.getId())) {
                        inter.listModel.removeElement(lieu);
                        iterator.remove();

                        break;
                    }
                }

                for (int i = 0; i < inter.listModel.getSize(); i++) {
                    Lieu li = (Lieu) inter.listModel.getElementAt(i);
                    if (li.getId() == Integer.parseInt(p.getId())) {
                        inter.listModel.removeElementAt(i);
                        break;
                    }
                }
                clearArretes();
                while (inter.tableAlgoModel.getRowCount() != 0) {
                    inter.tableAlgoModel.removeRow(0);
                }
                listL = inter.cheminCalcul;
                tracer = false;
                inter.tableAlgos.removeAll();
                inter.scrlDistances.setViewportView(null);
                inter.panneauTables.revalidate();
                inter.panneauTables.repaint();
                try {
                    inter.chargerLieux(inter.cheminCalcul);
                } catch (ListeDesLieuxVideException ex) {
                    Logger.getLogger(Repere.class.getName()).log(Level.SEVERE, null, ex);
                }
                Repere.this.revalidate();
                Repere.this.repaint();
            });

            JButton btn = point.getPoint();

            btn.setBounds((int) x - 8, (int) y - 8, 16, 16);
            this.add(btn);
            listePointsClick.add(point);
        }
        initEcouteur();

    }

    private void initEcouteur() {
        Repere.this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                popupAjouterRepere(e);

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                popupAjouterRepere(e);
            }

            private void popupAjouterRepere(MouseEvent e) {
                if (e.isPopupTrigger()) {//regarde si clic droit
                    JPopupMenu pop = new JPopupMenu();
                    JMenuItem ajouter = new JMenuItem("Ajouter");
                    pop.add(ajouter);

                    ajouter.addActionListener(ev -> {
                        int id = listL.size() + 1;
                        double x = Math.round((e.getX() - ajustX) / echelleX * 100.0) / 100.0;
                        double y = Math.round((e.getY() - ajustY) / echelleY * 100.0) / 100.0;
                        Lieu lieu = new LieuEuclidien(id, x, y);
                        listePoints.add(new Point2D.Double(e.getX(), e.getY()));
                        Points point = new Points(Integer.toString(id), x, y, inter, p -> {
                            int index = listePointsClick.indexOf(p);
                            remove(p.getPoint());
                            listePointsClick.remove(p);

                            if (index >= 0 && index < listePoints.size()) {
                                listePoints.remove(index);
                            }
                            Iterator<Lieu> iterator = inter.cheminCalcul.iterator();
                            while (iterator.hasNext()) {
                                Lieu po = iterator.next();
                                if (po.getId() == Integer.parseInt(p.getId())) {
                                    inter.listModel.removeElement(po);
                                    iterator.remove();

                                    break;
                                }
                            }

                            for (int i = 0; i < inter.listModel.getSize(); i++) {
                                Lieu li = (Lieu) inter.listModel.getElementAt(i);
                                if (li.getId() == Integer.parseInt(p.getId())) {
                                    inter.listModel.removeElementAt(i);
                                    break;
                                }
                            }

                            clearArretes();
                            listL = inter.cheminCalcul;
                            listL = inter.cheminCalcul;
                            tracer = false;
                            while (inter.tableAlgoModel.getRowCount() != 0) {
                                inter.tableAlgoModel.removeRow(0);
                            }
                            try {
                                inter.chargerLieux(inter.cheminCalcul);
                            } catch (ListeDesLieuxVideException ex) {
                                Logger.getLogger(Repere.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            inter.tableAlgos.removeAll();
                            inter.scrlDistances.setViewportView(null);
                            inter.panneauTables.revalidate();
                            inter.panneauTables.repaint();
                            revalidate();
                            repaint();
                        });
                        inter.cheminCalcul.add(lieu);
                        inter.listModel.addElement(lieu);
                        JButton btn = point.getPoint();

                        btn.setBounds((int) e.getX() - 8, (int) e.getY() - 8, 16, 16);
                        Repere.this.add(btn);
                        listePointsClick.add(point);

                        clearArretes();
                        listL = inter.cheminCalcul;
                        listL = inter.cheminCalcul;
                        tracer = false;
                        while (inter.tableAlgoModel.getRowCount() != 0) {
                            inter.tableAlgoModel.removeRow(0);
                        }
                        try {
                            inter.chargerLieux(inter.cheminCalcul);
                        } catch (ListeDesLieuxVideException ex) {
                            Logger.getLogger(Repere.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        inter.tableAlgos.removeAll();
                        inter.scrlDistances.setViewportView(null);
                        inter.panneauTables.revalidate();
                        inter.panneauTables.repaint();
                        revalidate();
                        repaint();

                    });
                    pop.show(Repere.this, e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Cette fonction dessine le repère
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D) g;
        super.paintComponent(gr);

        Iterator<Point2D> it = listePoints.iterator();
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point2D p;
        gr.setStroke(new BasicStroke(1));
        for (int i = 1; i <= 10; i++) {
            int gridX = (int) (minX + (maxX - minX) * i / 10);
            int gridY = (int) (minY + (maxY - minY) * i / 10);

            gr.setColor(Color.LIGHT_GRAY);
            gr.drawLine((int) (gridX * echelleX + ajustX + 8), (int) (minY * echelleY + ajustY + 8),
                    (int) (gridX * echelleX + ajustX + 8), (int) (maxY * echelleY + ajustY + 8)); // Graduation Vertical
            gr.drawLine((int) (minX * echelleX + ajustX + 8), (int) (gridY * echelleY + ajustY + 8),
                    (int) (maxX * echelleX + ajustX + 8), (int) (gridY * echelleY + ajustY + 8)); // Graduation horizontal
            gr.setColor(Color.black);
            gr.drawString(Integer.toString(gridX), (int) (gridX * echelleX + ajustX), (int) (minY * echelleY + ajustY));
            gr.drawString(Integer.toString(gridY), (int) (minX * echelleX + ajustX - 12), (int) (gridY * echelleY + ajustY));
        }
        // On trace les axes
        gr.setStroke(new BasicStroke(4));
        gr.drawLine((int) (minX * echelleX + ajustX + 8), (int) (minY * echelleY + ajustY + 8),
                (int) (minX * echelleX + ajustX + 8), (int) (maxY * echelleY + ajustY + 8)); // ligne verticale
        gr.drawLine((int) (minX * echelleX + ajustX + 8), (int) (minY * echelleY + ajustY + 8),
                (int) (maxX * echelleX + ajustX + 8), (int) (minY * echelleY + ajustY + 8)); // ligne horizontale

        gr.setStroke(new BasicStroke(2));
        gr.setColor(new Color(142, 22, 22));
        if (tracer) {
            for (Arrete a : arretes) {
                this.remove(a);
            }
            arretes.clear();
            for (int i = 0; i < listePointsClick.size() - 1; i++) {
                Points p1 = listePointsClick.get(i);
                Points p2 = listePointsClick.get(i + 1);
                String id1 = p1.getId();
                String id2 = p2.getId();
                Lieu l1 = c.getLieuId(listL, Integer.parseInt(id1));
                double distance = l1.distances.get(Integer.parseInt(id2));
                Arrete arrete = new Arrete(listePoints.get(i), listePoints.get(i + 1), distance);
                arretes.add(arrete);
                this.add(arrete);
                if (i == 0) {
                    p1 = listePointsClick.get(i);
                    p2 = listePointsClick.get(listePointsClick.size() - 1);
                    id1 = p1.getId();
                    id2 = p2.getId();
                    l1 = c.getLieuId(listL, Integer.parseInt(id1));
                    distance = l1.distances.get(Integer.parseInt(id2));

                    arrete = new Arrete(listePoints.get(i), listePoints.get(listePoints.size() - 1), distance);
                    arretes.add(arrete);
                    this.add(arrete);
                }
            }

        }
        initEcouteur();

        gr.setColor(new Color(216, 64, 64));
        while (it.hasNext()) {
            p = (it.next());
            gr.fillOval((int) (p.getX()), (int) (p.getY()), 16, 16);
        }
    }

    /**
     * Vide arretes et enleve toutes les arretes du repere
     */
    public void clearArretes() {

        for (Arrete a : arretes) {
            this.remove(a);
        }

        arretes.clear();

        // Réinstalle le mouse listener pour le clic droit
        initEcouteur();

        // Force un recalcul de l'affichage
        this.revalidate();
        this.repaint();

    }

    public interface RepereRemoveListener {

        void remove(Points p);
    }
}
