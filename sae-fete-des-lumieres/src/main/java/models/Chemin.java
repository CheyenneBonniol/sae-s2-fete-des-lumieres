
package models;

import java.util.ArrayList;

/**
 * Classe représentant un chemin compose de lieux et d'un poids
 * @author Cheyenne
 */
public class Chemin {
    public ArrayList<Lieu> chem;
    public double poids;
    
    /**
     * Constructeur
     * @param chem
     * @param poids 
     */
    public Chemin(ArrayList<Lieu> chem, double poids){
        this.chem=chem;
        this.poids=poids;
    }
    
}
