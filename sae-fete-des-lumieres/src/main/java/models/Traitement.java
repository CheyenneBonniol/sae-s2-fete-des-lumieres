package models;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Classe traitant les fichiers en entrée et en sortie, construit les lieux et
 * les sauvegardes
 *
 * @author Livio, Cheyenne
 */
public class Traitement {

    private String nom;
    public ArrayList<Lieu> listeLieux;

    /**
     * Constructeur de la classe
     */
    public Traitement() {
        listeLieux = new ArrayList<>();
    }

    //Getter
    public void getListeLieux() {
        System.out.println("Liste de lieux: ");
        for (int i = 0; i < listeLieux.size(); i++) {
            System.out.println(listeLieux.get(i).toString());
        }
    }

    /**
     * Cette fonction permet de lire le fichier passe en parametre et d'en
     * extraire le nom, les commentaires eventuels et de creer des lieux a
     * partir des coordonnees
     *
     * @param file le fichier a ouvrir a deja ete choisi
     * @return typeCoordonnées du fichier
     * @author Livio
     */
    public int LectureFichier(FileInputStream file) {
        //Si un fichier a déjà été lu avant
        if (!listeLieux.isEmpty()) {
            listeLieux.clear();
        }
        //Lecture de l'entête puis lecture des coordonnées
        //String commentaire;
        int typeCoordonnees = 19; //0 = euclidien, 1 = géographique
        int eucl = 19;
        int id;
        boolean coord = false;//on a pas encore regarde le format des coordonnees
        Scanner sc = new Scanner(file);//ouverture du flux du fichier file
        ArrayList<Lieu> ajoutsFinaux = new ArrayList<>();
        while (sc.hasNext()) {
            String ligne = sc.nextLine();
            if (!coord) {
                //lecture de l'entete
                String[] mots = ligne.split(":");
                switch (mots[0]) {
                    case "NAME", "NAME " ->
                        nom = mots[mots.length - 1];

                    case "EDGE_WEIGHT_TYPE", "EDGE_WEIGHT_TYPE " -> {

                        if (mots[mots.length - 1].equals(" GEO") || mots[mots.length - 1].equals("GEO")) {
                            typeCoordonnees = 1;
                        } else if (mots[mots.length - 1].equals(" EUC_2D") || mots[mots.length - 1].equals("EUC_2D")) {
                            typeCoordonnees = 0;
                        }
                    }
                    case "NODE_COORD_SECTION" ->
                        coord = true;
                }
            } else {
                //lecture des coordonnees
                String[] nombres = ligne.split(" ");
                int espace = (nombres[0].equals("")) ? 1 : 0;
                if (typeCoordonnees == 0 && eucl == 19) {
                    eucl = testCoord(nombres);
                }
                if (!nombres[0].equals("EOF") && !nombres[1].equals("EOF")) {//evite de lire la dernière ligne avec EOF
                    switch (typeCoordonnees) {
                        case 0:
                            double x;
                            double y;
                            if (eucl == 0) {
                                //creer un lieu avec des coordonnees euclidienne et l'insere dans la liste
                                id = Integer.parseInt(nombres[0 + espace]);
                                x = Double.parseDouble(nombres[1 + espace]);
                                y = Double.parseDouble(nombres[2 + espace]);

                            } else {
                                id = (int) Double.parseDouble(nombres[0 + espace]);
                                x = Double.parseDouble(nombres[1 + espace]);
                                y = Double.parseDouble(nombres[2 + espace]);

                            }
                            LieuEuclidien nouveauLieuE = new LieuEuclidien(id, x, y);
                            if (!listeLieux.contains(nouveauLieuE)) {
                                boolean coordPresent = false;
                                boolean idPresent = false;
                                for (Lieu lieu : listeLieux) {
                                    if (lieu.getId() == id) {
                                        idPresent = true;
                                    } else if (((LieuEuclidien) lieu).getX() == x && ((LieuEuclidien) lieu).getY() == y) {
                                        coordPresent = true;
                                    }
                                }
                                if (!coordPresent && !idPresent) {
                                    listeLieux.add(nouveauLieuE);
                                } else if (!coordPresent) {
                                    ajoutsFinaux.add(nouveauLieuE);
                                }
                            }
                            break;
                        case 1:
                            //Creer un lieu avec des coordonnees geographique et l'insere dans la liste
                            id = Integer.parseInt(nombres[0 + espace]);
                            double lat = Double.parseDouble(nombres[1 + espace]);
                            double lon = Double.parseDouble(nombres[2 + espace]);
                            LieuGeographique nouveauLieuG = new LieuGeographique(id, lat, lon);
                            //Si le lieu à ajouter n'est déjà dans la liste
                            if (!listeLieux.contains(nouveauLieuG)) {
                                boolean coordPresent = false;
                                boolean idPresent = false;
                                //On regarde si l'id ou les coordonnées sont pas déjà présentes dans listeLieux
                                for (Lieu lieu : listeLieux) {
                                    if (lieu.getId() == id) {
                                        idPresent = true;
                                    } else if (((LieuGeographique) lieu).getLatitude() == lat && ((LieuGeographique) lieu).getLongitude() == lon) {
                                        coordPresent = true;
                                    }
                                }
                                //Si les coordonnées sont déjà présentes on ajoute rien dans la liste
                                //Si l'id est déjà présent dans la liste on réserve l'objet Lieu pour l'ajouter à la fin
                                if (!coordPresent && !idPresent) {
                                    listeLieux.add(nouveauLieuG);
                                } else if (!coordPresent) {
                                    ajoutsFinaux.add(nouveauLieuG);
                                }
                            }
                            break;
                    }
                }
            }
        }
        //Ajout des points restants à la fin de listeLieux
        for (int i = listeLieux.size(); i < ajoutsFinaux.size() + listeLieux.size(); i++) {
            ajoutsFinaux.get(i - listeLieux.size()).setId(i);
            listeLieux.add(ajoutsFinaux.get(i - listeLieux.size()));
        }
        sc.close();//fermeture du flux du fichier
        return typeCoordonnees;
    }

    /**
     * Sort un fichier resultatsX_Y.csv contenant le nom du fichier, le poids du
     * chemin glouton, le poids du chemin par insertion et le meilleur obtenu
     *
     * @param c
     * @param i
     * @throws models.AucunCheminException
     * @author Cheyenne
     */
    public void sortieTexte(Chercheur c, int i) throws AucunCheminException {
        try {
            FileWriter sortie = new FileWriter("resultatsX_Y.csv", true);
            sortie.write(nom + ";" + c.chemins.get("Glouton").poids + ";" + c.chemins.get("Insertion").poids + ";" + c.chemins.get(c.compareAlgo()).poids + "\n");
            sortie.close();
            FileWriter cheminFinal = new FileWriter("Voyage" + i + ".txt");
            for (Lieu l : c.chemins.get(c.compareAlgo()).chem) {
                cheminFinal.write(Integer.toString(l.getId()) + "\n");
            }
            cheminFinal.close();
        } catch (IOException e) {
            System.out.println("Erreur d'ouverture du fichier");
        }
    }

    /**
     * Cette fonction détermine quel est le type de coordonnées du fichier entré
     *
     * @param nb est de taille d'au moins 3
     * @return 1 si les coordonnées euclidiennes sont des double, 0 si normales
     * @author Cheyenne
     */
    public int testCoord(String[] nb) {
        return ((nb[2].contains(".")) ? 1 : 0);
    }

    /**
     * Cette fonction range dans les HashMap distances des lieux de listeLieux
     * les distances correspondantes
     *
     * @param liste une liste de lieux
     * @throws models.ListeDesLieuxVideException
     * @author Cheyenne
     */
    public void remplirDistances(ArrayList<Lieu> liste) throws ListeDesLieuxVideException {
        if (liste.isEmpty()) {
            throw new ListeDesLieuxVideException();
        } else if (!liste.isEmpty()) {
            for (int i = 0; i < liste.size(); i++) {
                liste.get(i).distances.clear();
                for (int j = 0; j < liste.size(); j++) {
                    if (i != j) {
                        liste.get(i).distances.put(liste.get(j).getId(), liste.get(i).calculDistance(liste.get(j)));
                    }
                }
            }
        }
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Cette fonction génere des lieux aléatoires à coordonnées euclidiennes
     *
     * @param nombre le nombre de lieux à générer
     * @author Livio
     */
    public void generationLieuxAleatoire(int nombre) {
        listeLieux.clear();
        Random rand = new Random();
        int id = 1;
        for (int i = 0; i < nombre; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            listeLieux.add(new LieuEuclidien(id, x, y));
            id++;
        }
        System.out.println("ListeLieux remplie : " + listeLieux);
    }

}
