package modelsTests;


import models.Lieu;
import models.LieuEuclidien;
import models.LieuGeographique;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Livio, Cheyenne
 */
public class LieuxTest {
    
    private Lieu testG = new LieuGeographique(1, 45.49, 56.25);
    private Lieu testE = new LieuEuclidien(1, 1, 0);
    
    public LieuxTest() {
    }
    
    @Test
    public void cooRadiansConversionGeographique() {
        assertEquals(Math.ceil(((LieuGeographique) testG).getLatitudeR()*10000)/10000, 0.7997);
        assertEquals(Math.ceil(((LieuGeographique) testG).getLongitudeR()*10000)/10000, 0.9847);
    }
    
    @Test
    public void testCalculDistance() {
        LieuGeographique testG2 = new LieuGeographique(2, 40.49, 50.25);
        LieuEuclidien testE2 = new LieuEuclidien(2, 0, 1);
        assertEquals(Math.ceil(((LieuGeographique) testG).calculDistance(testG2)*100)/100, 738.44);
        assertEquals(Math.ceil(((LieuEuclidien) testE).calculDistance(testE2)*100)/100, 1,414);
    }
}
