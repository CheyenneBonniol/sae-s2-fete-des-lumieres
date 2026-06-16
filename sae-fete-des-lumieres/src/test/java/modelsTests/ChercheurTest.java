
package modelsTests;


import java.util.ArrayList;
import models.AucunCheminException;
import models.Chercheur;
import models.Lieu;
import models.LieuEuclidien;
import models.ListeDesLieuxVideException;
import models.Traitement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Livio, Cheyenne
 */
public class ChercheurTest {
    
    private static Chercheur chercheur = new Chercheur();
    private static Lieu testE1 = new LieuEuclidien(1, 0, 0);
    private static Lieu testE2 = new LieuEuclidien(2, 0, 1);
    private static Lieu testE3 = new LieuEuclidien(3, 1, 0);
    private static ArrayList<Lieu> listeLieux = new ArrayList<>();
    
    public ChercheurTest() {
    }
    
    @BeforeAll
    public static void setUpClass() throws ListeDesLieuxVideException {
        listeLieux.add(testE1);
        listeLieux.add(testE2);
        listeLieux.add(testE3);
        Traitement t = new Traitement();
        t.remplirDistances(listeLieux);
    }

   @Test
    public void getLieuIdRenvoieId() {
        assertEquals(testE1, chercheur.getLieuId(listeLieux, 1));
    }

    @Test
    public void getLieuIdRenvoieNull() {
        assertEquals(null, chercheur.getLieuId(listeLieux, 0));
    }
    
    @Test
    public void testAppelGlouton() {
        ArrayList<Lieu> sol = new ArrayList<>();
        sol.add(testE1);
        sol.add(testE3);
        sol.add(testE2);
        assertEquals(chercheur.appelGlouton(listeLieux), sol);
        assertEquals(chercheur.chemins.get("Glouton").chem.size(), 3);
    }
    
    @Test
    public void testRechercheGlouton() {
        ArrayList<Lieu> sol = new ArrayList<>();
        sol.add(testE2);
        sol.add(testE1);
        sol.add(testE3);
        assertEquals(chercheur.rechercheGlouton(listeLieux, 1), sol);
    }
    
    @Test
    public void testCompareDistances() {
        assertEquals(chercheur.compareDistances(testE1.distances), 3);
    }
    
    @Test
    public void testRechercheParInsertion() {
        ArrayList<Lieu> sol = new ArrayList<>();
        sol.add(testE2);
        sol.add(testE1);
        sol.add(testE3);
        assertEquals(chercheur.rechercheParInsertion(listeLieux), sol);
        assertEquals(chercheur.rechercheParInsertion(listeLieux).size(), listeLieux.size());
    }
    
    @Test
    public void testInsertion() throws ListeDesLieuxVideException {
        listeLieux.remove(testE1);
        chercheur.insertion(listeLieux, testE1);
        ArrayList<Lieu> sol = new ArrayList<>();
        sol.add(testE1);
        sol.add(testE2);
        sol.add(testE3);
        assertEquals(listeLieux, sol);
        
    }
    
    @Test
    public void testCalculPoids() {
        assertEquals(Math.round( (float) chercheur.calculPoids(listeLieux)), 3);
    }
    
    @Test
    public void testRechercheChangePremier() {
        ArrayList<Lieu> sol = new ArrayList<>();
        sol.add(testE1);
        sol.add(testE2);
        sol.add(testE3);
        assertEquals(chercheur.rechercheChangePremier(listeLieux), sol);
    }
    
    @Test
    public void testCompareAlgo() {
        chercheur.chemins.clear();
        try {
            chercheur.compareAlgo();
        } catch (AucunCheminException ex) {
            fail("Aucun chemin n'a été choisi");
        }
    }
    
    @Test
    public void testRechercheAleatoire() {
        assertEquals(chercheur.rechercheAleatoire(listeLieux).size(), listeLieux.size());
    }
}
