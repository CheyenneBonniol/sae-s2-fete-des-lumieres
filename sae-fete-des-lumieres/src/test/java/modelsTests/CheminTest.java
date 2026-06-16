package modelsTests;

import java.util.ArrayList;
import models.Chemin;
import models.Chercheur;
import models.Lieu;
import models.LieuEuclidien;
import models.ListeDesLieuxVideException;
import models.Traitement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jauge_patrimoine
 */
public class CheminTest {
    
    public CheminTest() {
    }

    @Test
    public void creationCheminFonctionnelle() throws ListeDesLieuxVideException {
        ArrayList<Lieu> test = new ArrayList<>();
        test.add(new LieuEuclidien(1, 5, 6));
        test.add(new LieuEuclidien(2, 5, 0));
        test.add(new LieuEuclidien(3, 10, 0));
        Chercheur chercheur = new Chercheur();
        Traitement t = new Traitement();
        t.remplirDistances(test);
        Chemin c = new Chemin(test, chercheur.calculPoids(test));
        assertEquals(c.chem.size(), test.size());
    }
    
}
