package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Classe permettant de faire des manipulations sur les chemins
 *
 * @author Cheyenne, Livio
 */
public class Chercheur {

    public HashMap<String, Chemin> chemins;

    /**
     * Constructeur de la classe
     */
    public Chercheur() {
        chemins = new HashMap<>();
    }

    /**
     * Cette fonction renvoie l'objet Lieu correspondant à l'id en parametre
     *
     * @param liste
     * @param id
     * @return wanted est null si l'objet n'est pas dans listeLieux
     * @author Livio
     */
    public Lieu getLieuId(ArrayList<Lieu> liste, int id) {
        //cherche dans une arraylist de Lieux
        Lieu wanted = null;
        int i = 0;
        while (wanted == null && i < liste.size()) {
            if (liste.get(i).getId() == id) {
                wanted = liste.get(i);
            }
            i++;
        }
        return wanted;
    }

    /**
     * Appel la fonction rechercheGlouton() pour tous les sommets de départ
     * possible
     *
     * @param liste une liste de lieux
     * @return solution
     * @author Cheyenne
     */
    public ArrayList<Lieu> appelGlouton(ArrayList<Lieu> liste) {
        ArrayList<Lieu> solution = new ArrayList<>();
        double poidsMin = Double.POSITIVE_INFINITY;
        double poidsTest;
        for (int i = 0; i < liste.size(); i++) {
            ArrayList<Lieu> test = rechercheGlouton(liste, i);
            poidsTest = calculPoids(test);
            if (poidsMin > poidsTest) {
                solution = (ArrayList<Lieu>) test.clone();
                poidsMin = poidsTest;
            }
        }
        chemins.put("Glouton", new Chemin(solution, calculPoids(solution)));
        return solution;
    }

    /**
     * Cette fonction trouve le chemin selon la logique gloutonne
     *
     * @param liste une liste de lieux
     * @param premier le lieu de départ
     * @return solution
     * @author Livio
     */
    public ArrayList<Lieu> rechercheGlouton(ArrayList<Lieu> liste, int premier) {
        ArrayList<Lieu> solution = new ArrayList<>();
        int actuel = liste.get(premier).getId();

        HashSet<Integer> visites = new HashSet<>();

        while (solution.size() < liste.size() - 1) {
            //On ajoute dans le chemin le lieu actuel et on le marque comme visité
            solution.add(getLieuId(liste, actuel));
            visites.add(actuel);
            //On créé un hashmap contenant les lieux proches non visités
            HashMap<Integer, Double> modifiable = new HashMap<>(getLieuId(liste, actuel).distances);
            if (!modifiable.isEmpty()) {
                for (Integer id : visites) {
                    modifiable.remove(id);
                }
            }
            if (!modifiable.isEmpty()) {
                //On se positionne sur le lieu suivant
                actuel = compareDistances(modifiable);
            }
        }
        //On rajoute l'avant dernier lieu
        solution.add(getLieuId(liste, actuel));
        return solution;
    }

    /**
     * Cette fonction cherche dans un HashMap distances et renvoie l'id du lieu
     * le plus proche
     *
     * @param dist un hashmap de distances
     * @return l'id du lieu le plus proche
     * @author Livio
     */
    public int compareDistances(HashMap<Integer, Double> dist) {
        ArrayList<Integer> cles = new ArrayList<>();
        cles.addAll(dist.keySet());
        //Iterator<Integer> i = cles.iterator();
        double cadet = -1;
        //On dit que le plus petit est à l'indice 0
        int id = cles.getFirst();

        //On cherche le cadet
        for (Double val : dist.values()) {
            if (cadet == -1 || val < cadet) {
                cadet = val;
            }
        }

        for (int j = 0; j < cles.size(); j++) {
            //On cherche l'id avec la plus petite distance
            if (dist.get(cles.get(j)) == cadet) {
                id = cles.get(j);
            }
        }
        return id;
    }

    /**
     * Cette fonction recherche le meilleur chemin par insertion (le moins
     * couteux possible) Fait appel pour cela à la fonction insertion
     *
     * @param liste
     * @return cheminTrie
     * @author Cheyenne
     */
    public ArrayList<Lieu> rechercheParInsertion(ArrayList<Lieu> liste) {
        Lieu lieuPremier, lieuProche;
        ArrayList<Lieu> cheminTrie = new ArrayList<>();
        double poidsMin = Double.POSITIVE_INFINITY;

        double poidsTest;
        for (Lieu next : liste) {
            ArrayList<Lieu> test = new ArrayList<>();
            //choix du premier sommet
            lieuPremier = next;
            lieuProche = getLieuId(liste, compareDistances(lieuPremier.distances));
            test.add(lieuPremier);
            test.add(lieuProche);
            //on construit le meilleur chemin a partir de ce sommet
            for (Lieu nextLieu : liste) {

                if (nextLieu != lieuPremier && nextLieu != lieuProche) {
                    insertion(test, nextLieu);
                }
            }
            poidsTest = calculPoids(test);
            //on test si le chemin construit est meilleur que les precedents
            if (poidsTest < poidsMin) {
                cheminTrie = (ArrayList< Lieu>) test.clone();
                poidsMin = poidsTest;
            }
        }
        chemins.put("Insertion", new Chemin(cheminTrie, calculPoids(cheminTrie)));
        return cheminTrie;
    }

    /**
     * Cette fonction permet d'inserer un lieu dans la liste chemin a l'endroit
     * le moins couteux
     *
     * @param cheminTemp est de taille d'au moins 2 et le Lieu aInserer n'est
     * pas null
     * @param aInserer une ArrayList avec le Lieu aInserer insere au bon endroit
     * @author Cheyenne
     */
    public void insertion(ArrayList<Lieu> cheminTemp, Lieu aInserer) {
        if (!cheminTemp.contains(aInserer)) {
            int place = cheminTemp.size();
            int aIId = aInserer.getId();
            double minDist = Double.POSITIVE_INFINITY;
            double distBase = calculPoids(cheminTemp);
            double test;
            Lieu t1, t2;
            for (int i = 0; i <= cheminTemp.size(); i++) {
                t1 = (i == 0 || i == cheminTemp.size()) ? cheminTemp.get(cheminTemp.size() - 1) : cheminTemp.get(i - 1);
                t2 = (i < cheminTemp.size()) ? cheminTemp.get(i) : cheminTemp.get(0);

                //On calcul la distance si notre lieu a inserer est insere a cet id
                test = (distBase - (t1.distances.get(t2.getId())))
                        + aInserer.distances.get(t1.getId())
                        + aInserer.distances.get(t2.getId());
                if (test < minDist) {
                    place = i;//on insere avant t1
                    minDist = test;
                }
            }
            cheminTemp.add(place, aInserer);
        }
    }

    /**
     * Cette fonction retourne le poids total du chemin pris en paramètre
     *
     * @param chemin
     * @return poids étant le poids total
     * @author Cheyenne
     */
    public double calculPoids(ArrayList<Lieu> chemin) {
        double poids = 0.0;
        for (int i = 1; i < chemin.size(); i++) {
            poids += chemin.get(i).distances.get(chemin.get(i - 1).getId());
        }
        //rajoute au poids total la distance entre le premier sommet et le dernier du chemin
        poids += chemin.get(0).distances.get(chemin.get(chemin.size() - 1).getId());
        return poids;//Math.ceil(poids * 1000) / 1000;
    }

    /**
     * Cette fonction teste l'échange du premier sommet avec tout les autres
     * sommets en regardant si le chemin qui en ressort est plus optimisé
     *
     * @param chemin
     * @return cheminTrie etant le nouveau chemin si on a trouve un meilleur
     * chemin, ou sinon le chemin precedent
     * @author Cheyenne
     */
    public ArrayList<Lieu> rechercheChangePremier(ArrayList<Lieu> chemin) {
        ArrayList<Lieu> cheminTrie = (ArrayList<Lieu>) chemin.clone();
        double poidsDepart = calculPoids(chemin);
        double poidsMin = Double.POSITIVE_INFINITY;
        double poidsTest, test, sommeDebut;
        Lieu l1, l2, l3;
        Lieu aInverser = null;
        int idAInverser = 0;

        for (int i = 1; i < cheminTrie.size() - 1; i++) {
            poidsTest = poidsDepart;

            l2 = cheminTrie.get(cheminTrie.size() - 1);
            l3 = cheminTrie.get(1);
            //la distance entre le premier Lieu, son suivant et son precedent
            sommeDebut = cheminTrie.get(0).distances.get(l2.getId()) + cheminTrie.get(0).distances.get(l3.getId());
            l1 = cheminTrie.get(i);
            if (i == 1) {
                l3 = cheminTrie.get(2);
            }
            //la meme distance si on change le premier Lieu par le lieu actuel (d'indice i)
            test = l1.distances.get(l2.getId()) + l1.distances.get(l3.getId());

            l2 = cheminTrie.get(i - 1);
            l3 = cheminTrie.get(i + 1);
            //la distance entre le lieu tester son suivant et son precedent
            sommeDebut += l1.distances.get(l2.getId()) + l1.distances.get(l3.getId());

            if (i == 1) {
                l2 = cheminTrie.get(i);
            }

            test += cheminTrie.get(0).distances.get(l2.getId()) + cheminTrie.get(0).distances.get(l3.getId());//la meme distance si on inverse avec le premier lieu de base

            poidsTest += test;//on ajoute les distances rajoute à la liste si elle etait modifiee
            poidsTest -= sommeDebut;//on enleve les distances de la liste de base

            if (poidsTest < poidsDepart && poidsTest < poidsMin) {
                aInverser = cheminTrie.get(i);
                idAInverser = i;
                poidsMin = poidsTest;
            }
        }

        if (poidsMin < poidsDepart) {//on inverse seulement si on a trouve une meilleure solution
            l1 = cheminTrie.get(0);
            cheminTrie.remove(0);
            cheminTrie.remove(aInverser);
            cheminTrie.addFirst(aInverser);

            cheminTrie.add(idAInverser, l1);
        }
        chemins.put("Change Premier", new Chemin(cheminTrie, calculPoids(cheminTrie)));

        return cheminTrie;

    }

    /**
     * Compare les différents chemin calculés et renvoie le nom du meilleur algo
     *
     * @throws AucunCheminException
     * @return algoMin le nom de l'algorithme qui a trouvé le meilleur chemin
     * @author Cheyenne
     */
    public String compareAlgo() throws AucunCheminException {
        try {
            if (chemins.isEmpty()) {
                //Si aucun chemin n'a encore ete recherche on leve une exception
                throw new AucunCheminException();
            }
            double poidsMin = Double.POSITIVE_INFINITY;
            String algoMin = "";
            for (String algo : chemins.keySet()) {
                double test = chemins.get(algo).poids;
                if (test < poidsMin) {
                    poidsMin = test;
                    algoMin = algo;
                }
            }
            return algoMin;
        } catch (AucunCheminException e) {
            return e.getMessage();
        }

    }

    /**
     * Cette fonction créé un chemin aléatoire à partir d'un chemin rentré en
     * paramètre
     *
     * @param chemin
     * @return solution
     * @author Livio
     */
    public ArrayList<Lieu> rechercheAleatoire(ArrayList<Lieu> chemin) {
        ArrayList<Lieu> pourAjout = (ArrayList<Lieu>) chemin.clone();
        Random rand = new Random();
        int intRandom;
        ArrayList<Lieu> solution = new ArrayList<>();
        for (int i = 0; i < chemin.size(); i++) {
            intRandom = rand.nextInt(pourAjout.size());//on pioche un lieu aleatoirement
            Lieu ajouter = pourAjout.get(intRandom);
            solution.add(ajouter);
            pourAjout.remove(ajouter);//on l'enleve de la liste
        }
        chemins.put("Aleatoire", new Chemin(solution, calculPoids(solution)));
        return solution;
    }
}
