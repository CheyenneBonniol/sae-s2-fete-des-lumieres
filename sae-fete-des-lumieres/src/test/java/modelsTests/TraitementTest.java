package modelsTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import models.AucunCheminException;
import models.Chercheur;
import models.Lieu;
import models.LieuEuclidien;
import models.ListeDesLieuxVideException;
import models.Traitement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Livio, Cheyenne
 */
public class TraitementTest {

    private Traitement traitementTest = new Traitement();

    public TraitementTest() {
    }

    @Test
    public void renvoiTestCoord() {
        String[] coordEucli = {"6", "6", "6"};
        String[] coordGeo = {"4", "6", ".", "5", "8"};
        assertEquals(0, traitementTest.testCoord(coordEucli));
        assertEquals(1, traitementTest.testCoord(coordGeo));
    }

    @Test
    public void testLectureFichierSurFichierEuclidien() {
        FileInputStream file;
        try {
            file = new FileInputStream("../DataTest/test1.txt");
            int expResult = 0;
            int result = traitementTest.LectureFichier(file);
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail("Fichier non trouvable");
        }
    }
    
    @Test
    public void testLectureFichierSurFichierGeographique() {
        FileInputStream file;
        try {
            file = new FileInputStream("../DataTest/lumieres10.txt");
            int expResult = 1;
            int result = traitementTest.LectureFichier(file);
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail("Fichier non trouvable");
        }
    }

    @Test
    public void testSortieTexte() throws FileNotFoundException, AucunCheminException {
        
        File fichierASupprimer = new File("resultatsX_Y.csv");
        fichierASupprimer.delete();//on supprimer fichier resultatsX_Y.csv pour faire notre test
        Lieu lieu1 = new LieuEuclidien(1, 0, 0);
        Lieu lieu2 = new LieuEuclidien(2, 1, 0);
        traitementTest.listeLieux.add(lieu1);
        traitementTest.listeLieux.add(lieu2);
        try {
            traitementTest.remplirDistances(traitementTest.listeLieux);
            Chercheur c = new Chercheur();
            c.appelGlouton(traitementTest.listeLieux);
            c.rechercheParInsertion(traitementTest.listeLieux);
            int i = 0;
            traitementTest.sortieTexte(c, 1);
            FileInputStream fichierSortie = new FileInputStream("resultatsX_Y.csv");
            Scanner sc = new Scanner(fichierSortie);
            while (sc.hasNext()) {
                String ligne = sc.nextLine();
                i++;
            }
            assertEquals(1, i);
        } catch (ListeDesLieuxVideException ex) {
            fail("La liste des lieux est vide...");
        }
    }

    @Test
    public void testRemplirDistances() {
        Lieu lieu1 = new LieuEuclidien(1, 0, 0);
        Lieu lieu2 = new LieuEuclidien(2, 1, 0);
        traitementTest.listeLieux.add(lieu1);
        traitementTest.listeLieux.add(lieu2);
        try {
            traitementTest.remplirDistances(traitementTest.listeLieux);
            assertNotNull(lieu1.distances);
            assertNotNull(lieu2.distances);
            assertEquals(lieu1.distances.get(lieu2.getId()), lieu1.calculDistance(lieu2));
            assertEquals(lieu2.distances.get(lieu1.getId()), lieu2.calculDistance(lieu1));
        } catch (ListeDesLieuxVideException ex) {
            fail("La liste des lieux est vide...");
        }
    }
    
    @Test
    public void generationLieuxAleatoireGeneration() {
        traitementTest.generationLieuxAleatoire(10);
        assertEquals(10, traitementTest.listeLieux.size());
    }

}
