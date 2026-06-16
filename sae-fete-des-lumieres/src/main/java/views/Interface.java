package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import models.AucunCheminException;
import models.Chercheur;
import models.Lieu;
import models.LieuGeographique;
import models.ListeDesLieuxVideException;
import models.Traitement;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Fenêtre principale
 *
 * @author Livio, Cheyenne
 */
public class Interface extends JFrame {

    private JButton btAlgoGlouton, btAlgoInsertion, btAlgoAleatoire, btAlgoChangePremier, btAfficheDistances, btSelectionOk, btDistancesLieu, btMeilleurChemin, btReinitialisation;

    private JCheckBox chBLieux;
    private JLabel lblAucuneDonneeCarte, lblListe, lblTableAlgos;
    private org.jxmapviewer.JXMapViewer map = new org.jxmapviewer.JXMapViewer();
    private Traitement traitement;
    private Chercheur chercheur;
    private JList jlListeLieux;
    protected DefaultListModel listModel;
    protected ArrayList<Lieu> cheminCalcul;
    private int typeCoordonnees; //1 pour geographique, 0 pour euclidienne
    protected JPanel panneauGlobal, panneauCarteRepere, panneauSelectionLieux, panneauListe, panneauBoutonAlgos, panneauCentreInteractif, panneauNordInteractif, panneauInteractif, panneauTables;
    private JOptionPane jopErreur;
    protected JTable tableDistances, tableAlgos;
    protected DefaultTableModel tableAlgoModel;
    private Set<MyWaypoint> points = new HashSet<>();
    protected JScrollPane scrlDistances, scrlAlgos;
    private Repere repere;
    private Dimension dimPanneauCarteRepere, dimPanneauInteractif;
    private Color couleurNoire, couleurRougeFoncee, couleurRouge, couleurBlanche;
    private JMenuBar menuBar;
    private JMenu menuFichier, menuAffichage;
    private JMenuItem menuImporter, menuExporter, menuGenAlea, menuFermerTableDistances, menuFermerTableAlgos;
    private WaypointRender wp;

    /**
     * Constructeur
     *
     * @param t traitement
     * @param c chercheur
     */
    public Interface(Traitement t, Chercheur c) {
        super("FÊTE DES LUMIÈRES 2025");
        traitement = t;
        chercheur = c;
        initComposants();
        initEcouteurs();
        this.setLocation(0, 0);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();
    }

    /**
     * Cette fonction initialise les composants de la fenêtre
     */
    public void initComposants() {
        couleurNoire = new Color(29, 22, 22);
        couleurRougeFoncee = new Color(142, 22, 22);
        couleurRouge = new Color(216, 64, 64);
        couleurBlanche = new Color(238, 238, 238);

        cheminCalcul = new ArrayList<>();

        lblAucuneDonneeCarte = new JLabel("Aucune donnée n'est importée, aller dans Fichier > Importer un fichier (.txt)");

        lblListe = new JLabel("Liste des lieux : selectionnez un ou des lieu(x)");
        lblTableAlgos = new JLabel("-");
        lblTableAlgos.setForeground(couleurBlanche);

        btAlgoGlouton = new JButton("Recherche gloutonne");
        btAlgoGlouton.setBackground(couleurBlanche);
        btAlgoInsertion = new JButton("Recherche par insertion");
        btAlgoInsertion.setBackground(couleurBlanche);
        btAlgoAleatoire = new JButton("Recherche aléatoire");
        btAlgoAleatoire.setBackground(couleurBlanche);
        btAlgoChangePremier = new JButton("Recherche optimisée");
        btAlgoChangePremier.setBackground(couleurBlanche);
        btAfficheDistances = new JButton("Visualiser les distances de chaque lieu");
        btSelectionOk = new JButton("OK");
        chBLieux = new JCheckBox("Tout sélectionner", false);
        btDistancesLieu = new JButton("Visualiser les distances");
        btMeilleurChemin = new JButton("Générer le meilleur chemin");
        btReinitialisation = new JButton("Réinitialiser");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Dimension screenSize = new Dimension(700, 500);
        dimPanneauCarteRepere = new Dimension((int) (screenSize.getWidth() * 0.70), (int) (screenSize.getHeight() * 0.9));
        dimPanneauInteractif = new Dimension((int) (screenSize.getWidth() * 0.30), (int) (screenSize.getHeight() * 0.9));
        //Panneau global - container
        panneauGlobal = new JPanel();
        panneauGlobal.setLayout(new BorderLayout());
        panneauGlobal.setPreferredSize(screenSize);

        //Sous panneau contenant la carte/le repère
        panneauCarteRepere = new JPanel();
        panneauCarteRepere.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 0);
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1.0;
        gc.weighty = 1.0;

        panneauCarteRepere.setSize(dimPanneauCarteRepere);
        panneauCarteRepere.add(lblAucuneDonneeCarte, gc);

        //Sous panneau contant la checkbox et le bouton OK de la JList
        panneauSelectionLieux = new JPanel();
        panneauSelectionLieux.setLayout(new FlowLayout());
        panneauSelectionLieux.add(chBLieux);
        panneauSelectionLieux.add(btDistancesLieu);
        panneauSelectionLieux.add(btSelectionOk);

        //Sous panneau contenant la JList
        panneauListe = new JPanel();
        panneauListe.setLayout(new BorderLayout());
        listModel = new DefaultListModel();
        jlListeLieux = new JList(listModel);
        jlListeLieux.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jlListeLieux.setBackground(couleurBlanche);
        jlListeLieux.setSelectionBackground(couleurRouge);
        JScrollPane scroll = new JScrollPane(jlListeLieux);
        panneauListe.add(lblListe, BorderLayout.NORTH);
        panneauListe.add(scroll);
        panneauListe.add(panneauSelectionLieux, BorderLayout.SOUTH);

        //Sous panneau contenant les boutons permettant de lancer les algos
        panneauBoutonAlgos = new JPanel();
        panneauBoutonAlgos.setLayout(new GridBagLayout());
        panneauBoutonAlgos.setBackground(couleurRouge);

        gc.weightx = 0;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.insets = new Insets(3, 5, 3, 5); //top left bottom right
        gc.gridx = 0;
        gc.gridy = 0;
        panneauBoutonAlgos.add(btMeilleurChemin, gc);
        gc.gridwidth = 1;
        gc.gridy = 1;
        panneauBoutonAlgos.add(btAlgoGlouton, gc);
        gc.gridx = 1;
        panneauBoutonAlgos.add(btAlgoInsertion, gc);
        gc.gridx = 0;
        gc.gridy = 2;
        panneauBoutonAlgos.add(btAlgoAleatoire, gc);
        gc.gridx = 1;
        panneauBoutonAlgos.add(btAlgoChangePremier, gc);

        //Sous panneau allant contenir la table de distances d'un lieu et la table des chemins générés par algos
        panneauTables = new JPanel();
        panneauTables.setLayout(new BorderLayout());
        panneauTables.setBackground(couleurRougeFoncee);
        tableAlgoModel = new DefaultTableModel();
        tableAlgoModel.addColumn("Nom de l'algo");
        tableAlgoModel.addColumn("Poids");
        tableAlgos = new JTable(tableAlgoModel);
        tableAlgos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableAlgos.setBackground(couleurBlanche);
        tableAlgos.setSelectionBackground(couleurRouge);
        tableAlgos.setEnabled(false);
        tableAlgos.setSelectionMode(SINGLE_SELECTION);
        scrlAlgos = new JScrollPane();
        scrlAlgos.setViewportView(tableAlgos);
        tableDistances = new JTable();

        //Sous panneau contenant la liste et les boutons
        panneauCentreInteractif = new JPanel();
        panneauCentreInteractif.setLayout(new BorderLayout());
        panneauCentreInteractif.add(panneauListe, BorderLayout.NORTH);
        panneauCentreInteractif.add(panneauBoutonAlgos);

        //Sous panneau contenant le bouton import
        panneauNordInteractif = new JPanel();
        panneauNordInteractif.setLayout(new FlowLayout());

        panneauNordInteractif.setBackground(couleurRougeFoncee);
        panneauNordInteractif.add(btReinitialisation);
        panneauNordInteractif.add(btAfficheDistances);

        //Sous panneau contenant tous les boutons
        panneauInteractif = new JPanel();
        panneauInteractif.setLayout(new BorderLayout());
        panneauInteractif.setSize(dimPanneauInteractif);

        panneauInteractif.setBorder(BorderFactory.createLineBorder(couleurNoire, 3));
        panneauInteractif.add(panneauNordInteractif, BorderLayout.NORTH);
        panneauInteractif.add(panneauCentreInteractif);
        panneauInteractif.add(panneauTables, BorderLayout.SOUTH);
        panneauTables.add(lblTableAlgos, BorderLayout.NORTH);
        scrlAlgos.setPreferredSize(new Dimension(panneauInteractif.getWidth(), 150));
        panneauTables.add(scrlAlgos, BorderLayout.CENTER);
        scrlDistances = new JScrollPane();
        scrlDistances.setViewportView(tableDistances);
        scrlDistances.setPreferredSize(new Dimension(panneauInteractif.getWidth(), 70));
        panneauTables.add(scrlDistances, BorderLayout.SOUTH);
        panneauListe.setPreferredSize(new Dimension(panneauInteractif.getWidth(), 300));

        menuBar = new JMenuBar();
        panneauGlobal.add(menuBar, BorderLayout.NORTH);
        menuFichier = new JMenu("Fichier");
        menuBar.add(menuFichier);
        menuAffichage = new JMenu("Affichage");
        menuBar.add(menuAffichage);
        menuImporter = new JMenuItem("Importer un fichier (.txt)");
        menuImporter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuExporter = new JMenuItem("Exporter les données dans un fichier (.csv)");
        menuExporter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuGenAlea = new JMenuItem("Générer des lieux aléatoirement");
        menuGenAlea.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuFichier.add(menuImporter);
        menuFichier.add(menuExporter);
        menuFichier.add(menuGenAlea);
        menuFermerTableDistances = new JMenuItem("Fermer la table des distances");
        menuFermerTableAlgos = new JMenuItem("Fermer la table des chemins générés");
        menuAffichage.add(menuFermerTableDistances);
        menuAffichage.add(menuFermerTableAlgos);

        panneauGlobal.add(panneauCarteRepere);
        panneauGlobal.add(panneauInteractif, BorderLayout.EAST);
        this.setContentPane(panneauGlobal);
    }

    /**
     * Cette fonciton initialise les écouteurs
     */
    public void initEcouteurs() {
        Ecouteur ecouteurGeneral = new Ecouteur();

        btAfficheDistances.addActionListener(ecouteurGeneral);
        btDistancesLieu.addActionListener(ecouteurGeneral);
        btMeilleurChemin.addActionListener(ecouteurGeneral);
        btReinitialisation.addActionListener(ecouteurGeneral);
        //Menus items
        menuImporter.addActionListener(ecouteurGeneral);
        menuGenAlea.addActionListener(ecouteurGeneral);
        menuFermerTableDistances.addActionListener(ecouteurGeneral);
        menuFermerTableAlgos.addActionListener(ecouteurGeneral);
        //Boutons algos
        btAlgoAleatoire.addActionListener(ecouteurGeneral);
        btAlgoGlouton.addActionListener(ecouteurGeneral);
        btAlgoInsertion.addActionListener(ecouteurGeneral);
        btAlgoChangePremier.addActionListener(ecouteurGeneral);
        btSelectionOk.addActionListener(ecouteurGeneral);
        //Carte
        MouseInputListener SourisEven = new PanMouseInputListener(map);
        map.addMouseListener(SourisEven);

        map.addMouseWheelListener(new ZoomMouseWheelListenerCenter(map));
        map.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePoint = e.getPoint();
                boolean found = false;

                for (MyWayLine line : wp.getLines()) {
                    if (line.contientPoint(mousePoint.x, mousePoint.y)) {
                        map.setToolTipText("Distance : " + line.getToolTipText(e));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    map.setToolTipText(null);
                }
            }
        });
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popupAjouter(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                popupAjouter(e);
            }

            private void popupAjouter(MouseEvent e) {
                System.out.println("liste lieux " + traitement.listeLieux);
                if (e.isPopupTrigger()) {//regarde si clic droit
                    JPopupMenu pop = new JPopupMenu();
                    JMenuItem ajouter = new JMenuItem("Ajouter");
                    pop.add(ajouter);

                    ajouter.addActionListener(ev -> {
                        GeoPosition geoP = map.convertPointToGeoPosition(e.getPoint());
                        int id = cheminCalcul.size() + 1;

                        Lieu lieu = new LieuGeographique(id, geoP.getLatitude(), geoP.getLongitude());

                        cheminCalcul.add(lieu);
                        try {
                            chargerLieux(cheminCalcul);
                        } catch (ListeDesLieuxVideException ex) {
                            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        listModel.addElement(lieu);

                        MyWaypoint w = new MyWaypoint(Integer.toString(id), geoP, Interface.this,
                                wpToRemove -> {
                                    try {
                                        map.remove(wpToRemove.getPoint());

                                        points.remove(wpToRemove);

                                        Iterator<Lieu> iterator = cheminCalcul.iterator();
                                        while (iterator.hasNext()) {
                                            Lieu l = iterator.next();
                                            if (l.getId() == Integer.parseInt(wpToRemove.getId())) {
                                                listModel.removeElement(l);
                                                iterator.remove();

                                                break;
                                            }
                                        }
                                        panneauListe.revalidate();
                                        panneauListe.repaint();
                                        chargerLieux(cheminCalcul);
                                        wp.setWaypoints(new HashSet<>(points));
                                        map.repaint();
                                        afficheCarteRepere();
                                    } catch (ListeDesLieuxVideException erreur) {
                                        jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    }

                                });

                        points.add(w);
                        afficheCarteRepere();

                    });
                    pop.show(map, e.getX(), e.getY());

                }
            }

        });
        map.addMouseMotionListener(SourisEven);

    }

    /**
     * Cette fonction charge les lieux dans la jlist et calcule les distances
     * entre les points dans les hashmap distances depuis une liste de lieux
     *
     * @param listeLieux
     * @author Livio
     * @throws models.ListeDesLieuxVideException
     */
    public void chargerLieux(ArrayList<Lieu> listeLieux) throws ListeDesLieuxVideException {
        traitement.remplirDistances(listeLieux);
        //Afficher distances lieux dans listeDistances
        System.out.println("Chargement des lieux...");
        System.out.println(listModel);
        if (!listModel.isEmpty()) {
            boolean contenuIdentitque = true;
            int i = 1;
            //On cherche à savoir si tous les elements de la liste sont contenus dans listeLieux
            while (i < listModel.getSize() && contenuIdentitque) {
                if (!listeLieux.contains(chercheur.getLieuId(listeLieux, Integer.parseInt(listModel.get(i).toString())))) {
                    contenuIdentitque = false;
                }
                i++;
            }
            if (!contenuIdentitque) {
                listModel.clear();
                ArrayList<Lieu> temp = listeLieux;
                for (int j = 0; j < temp.size(); j++) {
                    listModel.addElement(temp.get(j));
                }
            }
        } else {
            listModel.clear();
            ArrayList<Lieu> temp = listeLieux;
            for (int j = 0; j < temp.size(); j++) {
                listModel.addElement(temp.get(j));
            }
        }
    }

    /**
     * Cette fonction affiche la carte ou le repère selon le type de coordonnées
     *
     * @author Cheyenne
     */
    public void afficheCarteRepere() {
        panneauCarteRepere.removeAll();
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 0);
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        panneauCarteRepere.setSize(dimPanneauCarteRepere);
        if (typeCoordonnees == 1) {

            GeoPosition geo = new GeoPosition(45.7449789, 4.8416167);
            TileFactoryInfo info = new OSMTileFactoryInfo();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            map.setTileFactory(tileFactory);
            map.setAddressLocation(geo);
            map.setZoom(15);
            map.setName("Carte");

            map.setPreferredSize(panneauCarteRepere.getSize());

            initWayPoint();
            map.revalidate();
            map.repaint();
            panneauCarteRepere.add(map, gc);

        } else {
            repere = new Repere(cheminCalcul, panneauCarteRepere.getSize(), this);
            panneauCarteRepere.add(repere, gc);
        }
        panneauCarteRepere.revalidate();
        panneauCarteRepere.repaint();
    }

    /**
     * Cette fonction affiche les waypoints sur la carte
     *
     * @author Cheyenne
     */
    private void initWayPoint() {
        wp = new WaypointRender(map);
        wp.setWaypoints(points);
        for (MyWaypoint d : points) {
            map.add(d.getPoint());
        }
        map.setOverlayPainter(wp);
        map.revalidate();
        map.repaint();
    }

    /**
     * Cette fonction enlève les points de la carte
     *
     * @auhtor Cheyenne
     */
    private void clearWayPoint() {
        if (wp != null) {
            map.setOverlayPainter(null);
        }
        map.removeAll();
        points.clear();
    }

    /**
     * Cette fonction selectionne dans la jlist le lieu associé
     *
     * @param id l'id du lieu
     * @author Cheyenne
     */
    public void selectLieuListe(String id) {
        int index = -1;
        for (int i = 0; i < cheminCalcul.size() && index == -1; i++) {
            if (cheminCalcul.get(i).getId() == Integer.parseInt(id)) {
                index = listModel.indexOf(cheminCalcul.get(i));
            }
        }
        jlListeLieux.setSelectedIndex(index);
        jlListeLieux.ensureIndexIsVisible(index);
    }

    /**
     * Cette fonction rafraichit la table des chemins en ajoutant un chemin
     *
     * @param cheminGenere un chemin sous forme d'array de string
     * @author Livio
     */
    public void rafraichirTableAlgos(String[] cheminGenere) {
        boolean estPresent = false;
        if (cheminGenere != null || cheminGenere.length > 0) {
            if (tableAlgoModel.getColumnCount() < cheminGenere.length) {
                for (int i = 0; i < cheminGenere.length - 2; i++) {
                    tableAlgoModel.addColumn("Point " + (char) (i + 65));
                }
            }
            //Si le chemin a déjà été calculé, on le remplace par le nouveau
            for (int i = 0; i < tableAlgoModel.getRowCount(); i++) {
                if (tableAlgoModel.getValueAt(i, 0).equals(cheminGenere[0])) {
                    tableAlgoModel.removeRow(i);
                    tableAlgoModel.insertRow(0, cheminGenere);
                    estPresent = true;
                }
            }
            if (!estPresent) {
                tableAlgoModel.insertRow(0, cheminGenere);
            }
            panneauTables.revalidate();
            panneauTables.repaint();
        } else {
            System.out.println("Echec : chemin vide");
        }
    }

    /**
     * Classe interne Ecouteur
     */
    public class Ecouteur implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == menuImporter) {
                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int saveDialog = fileChooser.showSaveDialog(null);

                //Si l'utilisateur choisi un fichier:
                if (saveDialog == JFileChooser.APPROVE_OPTION) {

                    //Chemin absolu du fichier choisi
                    System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
                    String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
                    if (absolutePath.substring(absolutePath.indexOf('.') + 1).equals("txt")) {
                        try {
                            FileInputStream file = new FileInputStream(absolutePath);
                            typeCoordonnees = traitement.LectureFichier(file);
                            if (!listModel.isEmpty()) {
                                //On supprime les éléments avant si un fichier avait déjà été entré
                                listModel.clear();
                            }
                            chargerLieux(traitement.listeLieux);
                            cheminCalcul.addAll(traitement.listeLieux);
                            clearWayPoint();
                            if (typeCoordonnees == 1) {
                                for (Lieu l : cheminCalcul) {
                                    LieuGeographique li = (LieuGeographique) l;
                                    points.add(new MyWaypoint(Integer.toString(li.getId()),
                                            new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this,
                                            wpToRemove -> {
                                                map.remove(wpToRemove.getPoint());

                                                points.remove(wpToRemove);

                                                Iterator<Lieu> iterator = cheminCalcul.iterator();
                                                while (iterator.hasNext()) {
                                                    Lieu lieu = iterator.next();
                                                    if (lieu.getId() == Integer.parseInt(wpToRemove.getId())) {
                                                        listModel.removeElement(lieu);
                                                        iterator.remove();

                                                        break;
                                                    }
                                                }
                                                panneauListe.revalidate();
                                                panneauListe.repaint();
                                                try {
                                                    chargerLieux(cheminCalcul);
                                                    wp.setWaypoints(new HashSet<>(points));
                                                    map.repaint();
                                                    afficheCarteRepere();
                                                } catch (ListeDesLieuxVideException ex) {
                                                    jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                                }
                                            }
                                    ));
                                }
                            }

                            afficheCarteRepere();
                        } catch (FileNotFoundException ex) {
                            //Gérer l'exception
                            System.getLogger(Interface.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                            //boîte dialogue erreur sans exception
                            jopErreur.showMessageDialog(null, "Le fichier n'a pas été trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);

                        } catch (ListeDesLieuxVideException ex) {
                            jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        jopErreur.showMessageDialog(null, "Le fichier n'est pas un fichier texte.", "Erreur", JOptionPane.ERROR_MESSAGE);

                    }
                } else {
                    //Si l'utilisateur annule l'opération:
                    System.out.println("opération annulée");
                }
            }
            if (e.getSource() == menuGenAlea) {
                //Boite de dialogue avec texte pour récuperer le nombre de lieux
                JFrame frame = new JFrame();
                try {
                    int nombre = Integer.parseInt(JOptionPane.showInputDialog(frame, "Saisissez le nombre de lieux à générer : "));
                    typeCoordonnees = 0;

                    tableAlgoModel.setRowCount(0);
                    scrlDistances.setViewportView(null);
                    panneauTables.revalidate();
                    panneauTables.repaint();

                    traitement.generationLieuxAleatoire(nombre);
                    chargerLieux(traitement.listeLieux);
                    cheminCalcul.addAll(traitement.listeLieux);
                    afficheCarteRepere();
                } catch (java.lang.NumberFormatException n) {
                    //Si il y a une erreur d'entrée
                    jopErreur.showMessageDialog(null, "Les caractères entrés ne sont pas des chiffres", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (ListeDesLieuxVideException ex) {
                    jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (e.getSource() == btAfficheDistances) {
                TableListeDistances fen = new TableListeDistances(listModel);
            }

            if (e.getSource() == btAlgoAleatoire) {
                chercheur.rechercheAleatoire(cheminCalcul);
                if (typeCoordonnees == 0) {

                    repere.setTracerChercheurLieu(true, chercheur, chercheur.chemins.get("Aleatoire").chem);
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                    panneauBoutonAlgos.repaint();
                    panneauCentreInteractif.repaint();
                } else {
                    clearWayPoint();
                    for (Lieu l : chercheur.chemins.get("Aleatoire").chem) {
                        LieuGeographique li = (LieuGeographique) l;
                        points.add(new MyWaypoint(Integer.toString(li.getId()),
                                new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this,
                                wpToRemove -> {
                                    map.remove(wpToRemove.getPoint());

                                    points.remove(wpToRemove);

                                    Iterator<Lieu> iterator = cheminCalcul.iterator();
                                    while (iterator.hasNext()) {
                                        Lieu lieu = iterator.next();
                                        if (lieu.getId() == Integer.parseInt(wpToRemove.getId())) {
                                            listModel.removeElement(lieu);
                                            iterator.remove();

                                            break;
                                        }
                                    }
                                    panneauListe.revalidate();
                                    panneauListe.repaint();
                                    try {
                                        chargerLieux(cheminCalcul);
                                        wp.setWaypoints(new HashSet<>(points));
                                        map.repaint();
                                        afficheCarteRepere();
                                        wp.setTracerChercheur(true, chercheur, chercheur.rechercheAleatoire(cheminCalcul));
                                        String[] cheminGenere = new String[cheminCalcul.size() + 2];
                                        cheminGenere[0] = "Chemin algo aléatoire";
                                        cheminGenere[1] = Double.toString(chercheur.chemins.get("Aleatoire").poids);
                                        for (int i = 0; i < cheminCalcul.size(); i++) {
                                            cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Aleatoire").chem.get(i).getId());
                                        }
                                        rafraichirTableAlgos(cheminGenere);
                                    } catch (ListeDesLieuxVideException ex) {
                                        jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                        ));
                    }

                    afficheCarteRepere();
                    wp.setTracerChercheur(true, chercheur, chercheur.chemins.get("Aleatoire").chem);

                    map.repaint();
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                }
                String[] cheminGenere = new String[cheminCalcul.size() + 2];
                cheminGenere[0] = "Chemin algo Aleatoire";
                cheminGenere[1] = Double.toString(chercheur.chemins.get("Aleatoire").poids);
                for (int i = 0; i < cheminCalcul.size(); i++) {
                    cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Aleatoire").chem.get(i).getId());
                }
                rafraichirTableAlgos(cheminGenere);
            }
            if (e.getSource() == btAlgoChangePremier) {
                chercheur.rechercheChangePremier(chercheur.rechercheParInsertion(cheminCalcul));
                if (typeCoordonnees == 0) {
                    repere.setTracerChercheurLieu(true, chercheur, chercheur.chemins.get("Change Premier").chem);
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                    panneauBoutonAlgos.repaint();
                    panneauCentreInteractif.repaint();
                } else {
                    clearWayPoint();
                    for (Lieu l : chercheur.chemins.get("Change Premier").chem) {
                        LieuGeographique li = (LieuGeographique) l;
                        points.add(new MyWaypoint(Integer.toString(li.getId()),
                                new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this,
                                wpToRemove -> {
                                    map.remove(wpToRemove.getPoint());

                                    points.remove(wpToRemove);

                                    Iterator<Lieu> iterator = cheminCalcul.iterator();
                                    while (iterator.hasNext()) {
                                        Lieu lieu = iterator.next();
                                        if (lieu.getId() == Integer.parseInt(wpToRemove.getId())) {
                                            listModel.removeElement(lieu);
                                            iterator.remove();

                                            break;
                                        }
                                    }
                                    panneauListe.revalidate();
                                    panneauListe.repaint();
                                    try {
                                        chargerLieux(cheminCalcul);
                                        wp.setWaypoints(new HashSet<>(points));
                                        map.repaint();
                                        afficheCarteRepere();
                                        wp.setTracerChercheur(true, chercheur, chercheur.rechercheChangePremier(chercheur.rechercheParInsertion(cheminCalcul)));
                                        String[] cheminGenere = new String[cheminCalcul.size() + 2];
                                        cheminGenere[0] = "Chemin algo change premier";
                                        cheminGenere[1] = Double.toString(chercheur.chemins.get("Change Premier").poids);
                                        for (int i = 0; i < cheminCalcul.size(); i++) {
                                            cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Change Premier").chem.get(i).getId());
                                        }
                                        rafraichirTableAlgos(cheminGenere);
                                    } catch (ListeDesLieuxVideException ex) {
                                        jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                        ));
                    }

                    afficheCarteRepere();
                    wp.setTracerChercheur(true, chercheur, chercheur.chemins.get("Change Premier").chem);

                    map.repaint();
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                }

                String[] cheminGenere = new String[cheminCalcul.size() + 2];
                cheminGenere[0] = "Chemin algo Change Premier";
                cheminGenere[1] = Double.toString(chercheur.chemins.get("Change Premier").poids);
                for (int i = 0; i < cheminCalcul.size(); i++) {
                    cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Change Premier").chem.get(i).getId());
                }
                rafraichirTableAlgos(cheminGenere);
            }
            if (e.getSource() == btAlgoGlouton) {
                chercheur.appelGlouton(cheminCalcul);
                if (typeCoordonnees == 0) {
                    repere.setTracerChercheurLieu(true, chercheur, chercheur.chemins.get("Glouton").chem);
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                    panneauBoutonAlgos.revalidate();
                    panneauCentreInteractif.revalidate();
                    panneauBoutonAlgos.repaint();
                    panneauCentreInteractif.repaint();
                } else {
                    clearWayPoint();
                    for (Lieu l : chercheur.chemins.get("Glouton").chem) {
                        LieuGeographique li = (LieuGeographique) l;
                        points.add(new MyWaypoint(Integer.toString(li.getId()),
                                new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this,
                                wpToRemove -> {
                                    map.remove(wpToRemove.getPoint());

                                    points.remove(wpToRemove);

                                    Iterator<Lieu> iterator = cheminCalcul.iterator();
                                    while (iterator.hasNext()) {
                                        Lieu lieu = iterator.next();
                                        if (lieu.getId() == Integer.parseInt(wpToRemove.getId())) {
                                            listModel.removeElement(lieu);
                                            iterator.remove();

                                            break;
                                        }
                                    }
                                    panneauListe.revalidate();
                                    panneauListe.repaint();
                                    try {
                                        chargerLieux(cheminCalcul);
                                        wp.setWaypoints(new HashSet<>(points));
                                        map.repaint();
                                        afficheCarteRepere();
                                        wp.setTracerChercheur(true, chercheur, chercheur.appelGlouton(cheminCalcul));
                                        String[] cheminGenere = new String[cheminCalcul.size() + 2];
                                        cheminGenere[0] = "Chemin algo glouton";
                                        cheminGenere[1] = Double.toString(chercheur.chemins.get("Glouton").poids);
                                        for (int i = 0; i < cheminCalcul.size(); i++) {
                                            cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Glouton").chem.get(i).getId());
                                        }
                                        rafraichirTableAlgos(cheminGenere);
                                    } catch (ListeDesLieuxVideException ex) {
                                        jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                        ));
                    }

                    afficheCarteRepere();
                    wp.setTracerChercheur(true, chercheur, chercheur.chemins.get("Glouton").chem);

                    map.repaint();
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                }

                String[] cheminGenere = new String[cheminCalcul.size() + 2];
                cheminGenere[0] = "Chemin algo Glouton";
                cheminGenere[1] = Double.toString(chercheur.chemins.get("Glouton").poids);
                for (int i = 0; i < cheminCalcul.size(); i++) {
                    cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Glouton").chem.get(i).getId());
                }
                rafraichirTableAlgos(cheminGenere);
            }
            if (e.getSource() == btAlgoInsertion) {
                chercheur.rechercheParInsertion(cheminCalcul);
                if (typeCoordonnees == 0) {
                    repere.setTracerChercheurLieu(true, chercheur, chercheur.chemins.get("Insertion").chem);
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                    panneauBoutonAlgos.repaint();
                    panneauCentreInteractif.repaint();
                } else {
                    clearWayPoint();
                    for (Lieu l : chercheur.chemins.get("Insertion").chem) {
                        LieuGeographique li = (LieuGeographique) l;
                        points.add(new MyWaypoint(Integer.toString(li.getId()),
                                new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this,
                                wpToRemove -> {
                                    map.remove(wpToRemove.getPoint());

                                    points.remove(wpToRemove);

                                    Iterator<Lieu> iterator = cheminCalcul.iterator();
                                    while (iterator.hasNext()) {
                                        Lieu lieu = iterator.next();
                                        if (lieu.getId() == Integer.parseInt(wpToRemove.getId())) {
                                            listModel.removeElement(lieu);
                                            iterator.remove();

                                            break;
                                        }
                                    }
                                    panneauListe.revalidate();
                                    panneauListe.repaint();
                                    try {
                                        chargerLieux(cheminCalcul);
                                        wp.setWaypoints(new HashSet<>(points));
                                        map.repaint();
                                        afficheCarteRepere();
                                        wp.setTracerChercheur(true, chercheur, chercheur.rechercheParInsertion(cheminCalcul));
                                        String[] cheminGenere = new String[cheminCalcul.size() + 2];
                                        cheminGenere[0] = "Chemin algo insertion";
                                        cheminGenere[1] = Double.toString(chercheur.chemins.get("Insertion").poids);
                                        for (int i = 0; i < cheminCalcul.size(); i++) {
                                            cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Insertion").chem.get(i).getId());
                                        }
                                        rafraichirTableAlgos(cheminGenere);
                                    } catch (ListeDesLieuxVideException ex) {
                                        jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    }

                                }
                        ));
                    }

                    afficheCarteRepere();
                    wp.setTracerChercheur(true, chercheur, chercheur.chemins.get("Insertion").chem);

                    map.repaint();
                    panneauCarteRepere.revalidate();
                    panneauCarteRepere.repaint();
                }
                String[] cheminGenere = new String[cheminCalcul.size() + 2];
                cheminGenere[0] = "Chemin algo Insertion";
                cheminGenere[1] = Double.toString(chercheur.chemins.get("Insertion").poids);
                for (int i = 0; i < cheminCalcul.size(); i++) {
                    cheminGenere[i + 2] = Integer.toString(chercheur.chemins.get("Insertion").chem.get(i).getId());
                }
                rafraichirTableAlgos(cheminGenere);
            }
            if (e.getSource() == btSelectionOk) {
                chercheur.chemins.remove("Insertion");
                chercheur.chemins.remove("Glouton");
                chercheur.chemins.remove("Change Premier");
                chercheur.chemins.remove("Aleatoire");
                cheminCalcul.clear();
                if (chBLieux.isSelected()) {
                    for (int i = 0; i < listModel.getSize(); i++) {
                        cheminCalcul.add((Lieu) listModel.getElementAt(i));
                    }//VERSION A GARDER LES AUTRES NE MARCHENT PAAAAAAS
                } else if (!chBLieux.isSelected()) {
                    for (Object object : jlListeLieux.getSelectedValuesList()) {
                        cheminCalcul.add((Lieu) object);
                    }
                }
                try {
                    chargerLieux(cheminCalcul);
                    while (tableAlgoModel.getRowCount() != 0) {
                        tableAlgoModel.removeRow(0);
                    }
                    afficheCarteRepere();
                    tableAlgos.removeAll();
                    scrlDistances.setViewportView(null);
                    panneauTables.revalidate();
                    panneauTables.repaint();
                } catch (ListeDesLieuxVideException ex) {
                    jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (e.getSource() == btDistancesLieu) {
                if (jlListeLieux.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(null, "Vous n'avez pas selectionné de lieu", "Attention", JOptionPane.WARNING_MESSAGE);
                } else if (!cheminCalcul.contains(jlListeLieux.getSelectedValue())) {
                    JOptionPane.showMessageDialog(null, "Le lieu selectionné n'est pas compris dans le chemin choisi", "Attention", JOptionPane.WARNING_MESSAGE);
                } else {
                    String[] col = new String[cheminCalcul.size() + 1];
                    String[][] distances = new String[1][cheminCalcul.size()];

                    col[0] = "";
                    for (int i = 0; i < cheminCalcul.size(); i++) {
                        col[i + 1] = Integer.toString((cheminCalcul.get(i)).getId());
                    }
                    Lieu lieu1 = (Lieu) jlListeLieux.getSelectedValue();
                    String[] ligne = new String[cheminCalcul.size() + 1];
                    ligne[0] = Integer.toString(lieu1.getId());
                    for (int j = 0; j < cheminCalcul.size(); j++) {
                        Lieu lieu2 = cheminCalcul.get(j);
                        if (lieu1.getId() == lieu2.getId()) {
                            ligne[j + 1] = "X";
                        } else {
                            ligne[j + 1] = String.format("%.2f", lieu1.calculDistance(lieu2));
                        }
                    }
                    distances[0] = ligne;

                    tableDistances = new JTable(distances, col);
                    tableDistances.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    tableDistances.setBackground(couleurBlanche);

                    scrlDistances.setViewportView(tableDistances);

                    panneauTables.revalidate();
                    panneauTables.repaint();
                }
            }
            if (e.getSource() == menuExporter) {
                JOptionPane.showConfirmDialog(Interface.this, "Voulez-vous générer le fichier ResultatsX_Y.csv  ?");
                try {
                    traitement.sortieTexte(chercheur, 1);
                } catch (AucunCheminException ex) {
                    jopErreur.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (e.getSource() == btMeilleurChemin) {
                try {
                    chercheur.chemins.remove("Insertion");
                    chercheur.chemins.remove("Glouton");
                    chercheur.chemins.remove("Change Premier");
                    chercheur.chemins.remove("Aleatoire");

                    chercheur.rechercheParInsertion(cheminCalcul);
                    chercheur.rechercheAleatoire(cheminCalcul);
                    chercheur.rechercheChangePremier(cheminCalcul);
                    chercheur.appelGlouton(cheminCalcul);

                    //On trace le meilleur chemin comme convenu
                    if (typeCoordonnees == 0) {
                        repere.setTracerChercheurLieu(true, chercheur, chercheur.chemins.get(chercheur.compareAlgo()).chem);
                        panneauCarteRepere.revalidate();
                        panneauCarteRepere.repaint();
                        panneauBoutonAlgos.repaint();
                        panneauCentreInteractif.repaint();
                    } else {
                        clearWayPoint();
                        for (Lieu l : chercheur.chemins.get(chercheur.compareAlgo()).chem) {
                            LieuGeographique li = (LieuGeographique) l;
                            points.add(new MyWaypoint(Integer.toString(li.getId()), new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this, wpToRemove -> {
                                points.remove(wpToRemove);

                                map.repaint();
                            }));
                        }
                        afficheCarteRepere();
                        wp.setTracerChercheur(true, chercheur, chercheur.chemins.get(chercheur.compareAlgo()).chem);

                        map.repaint();
                        panneauCarteRepere.revalidate();
                        panneauCarteRepere.repaint();
                    }
                    //On rafraichit les algos qui ont tourné dans la jtable tableAlgos
                    for (String chem : chercheur.chemins.keySet()) {
                        if (!chem.equals(chercheur.compareAlgo())) {
                            String[] cheminGenere = new String[chercheur.chemins.get(chem).chem.size() + 2];
                            cheminGenere[0] = "Chemin algo " + chem;
                            cheminGenere[1] = Double.toString(chercheur.chemins.get(chem).poids);
                            for (int j = 0; j < chercheur.chemins.get(chem).chem.size(); j++) {
                                cheminGenere[j + 2] = Integer.toString(chercheur.chemins.get(chem).chem.get(j).getId());
                            }
                            rafraichirTableAlgos(cheminGenere);
                        }
                    }
                    //On insère le meilleur chemin
                    String[] cheminGenere = new String[chercheur.chemins.get(chercheur.compareAlgo()).chem.size() + 2];
                    cheminGenere[0] = "Chemin algo " + chercheur.compareAlgo();
                    cheminGenere[1] = Double.toString(chercheur.chemins.get(chercheur.compareAlgo()).poids);
                    for (int j = 0; j < chercheur.chemins.get(chercheur.compareAlgo()).chem.size(); j++) {
                        cheminGenere[j + 2] = Integer.toString(chercheur.chemins.get(chercheur.compareAlgo()).chem.get(j).getId());
                    }
                    rafraichirTableAlgos(cheminGenere);
                    lblTableAlgos.setText("MEILLEUR CHEMIN : algo " + chercheur.compareAlgo());
                    int indiceMeilleur = -1;
                    int parcours = 0;
                    while (parcours < tableAlgoModel.getRowCount() && indiceMeilleur == -1) {
                        if (tableAlgoModel.getValueAt(parcours, 0).toString().contains(chercheur.compareAlgo())) {
                            indiceMeilleur = parcours;
                        }
                        parcours++;
                    }
                    if (indiceMeilleur != -1) {
                        tableAlgos.setRowSelectionInterval(indiceMeilleur, indiceMeilleur);
                        panneauTables.revalidate();
                        panneauTables.repaint();
                    }
                } catch (AucunCheminException ex) {
                    jopErreur.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (e.getSource() == menuFermerTableDistances) {
                if (menuFermerTableDistances.getText().equals("Fermer la table des distances")) {
                    scrlDistances.setVisible(false);
                    panneauTables.revalidate();
                    panneauTables.repaint();
                    menuFermerTableDistances.setText("Ouvrir la table des distances");
                } else {
                    scrlDistances.setVisible(true);
                    panneauTables.revalidate();
                    panneauTables.repaint();
                    menuFermerTableDistances.setText("Fermer la table des distances");
                }
            }
            if (e.getSource() == menuFermerTableAlgos) {
                if (menuFermerTableAlgos.getText().equals("Fermer la table des chemins générés")) {
                    scrlAlgos.setVisible(false);
                    panneauTables.revalidate();
                    panneauTables.repaint();
                    menuFermerTableAlgos.setText("Ouvrir la table des chemins générés");
                } else {
                    scrlAlgos.setVisible(true);
                    panneauTables.revalidate();
                    panneauTables.repaint();
                    menuFermerTableAlgos.setText("Fermer la table des chemins générés");
                }
            }
            if (e.getSource() == btReinitialisation) {
                cheminCalcul.clear();
                cheminCalcul.addAll(traitement.listeLieux);
                System.out.println(cheminCalcul);
                try {

                    chargerLieux(cheminCalcul);
                    System.out.println("list model " + listModel.size());
                    panneauListe.revalidate();
                    panneauListe.repaint();
                    tableAlgoModel.setRowCount(0);
                    scrlDistances.setViewportView(null);

                    if (typeCoordonnees == 1) {
                        if (!listModel.isEmpty()) {
                            //On supprime les éléments avant si un fichier avait déjà été entré
                            listModel.clear();
                        }
                        chargerLieux(traitement.listeLieux);
                        cheminCalcul.addAll(traitement.listeLieux);
                        clearWayPoint();
                        if (typeCoordonnees == 1) {
                            for (Lieu l : cheminCalcul) {
                                LieuGeographique li = (LieuGeographique) l;
                                points.add(new MyWaypoint(Integer.toString(li.getId()),
                                        new GeoPosition(li.getLatitude(), li.getLongitude()), Interface.this,
                                        wpToRemove -> {
                                            map.remove(wpToRemove.getPoint());

                                            points.remove(wpToRemove);

                                            Iterator<Lieu> iterator = cheminCalcul.iterator();
                                            while (iterator.hasNext()) {
                                                Lieu lieu = iterator.next();
                                                if (lieu.getId() == Integer.parseInt(wpToRemove.getId())) {
                                                    listModel.removeElement(lieu);
                                                    iterator.remove();

                                                    break;
                                                }
                                            }
                                            panneauListe.revalidate();
                                            panneauListe.repaint();
                                            try {
                                                chargerLieux(cheminCalcul);
                                                wp.setWaypoints(new HashSet<>(points));
                                                map.repaint();
                                                afficheCarteRepere();
                                            } catch (ListeDesLieuxVideException ex) {
                                                jopErreur.showMessageDialog(null, "Aucun chemin n'a été entré. Veuillez selectionner les lieux dans la liste.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                ));
                            }
                        }

                        afficheCarteRepere();
                        wp.setTracerChercheur(false, chercheur, cheminCalcul);
                    }

                    afficheCarteRepere();
                } catch (ListeDesLieuxVideException ex) {
                    jopErreur.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
