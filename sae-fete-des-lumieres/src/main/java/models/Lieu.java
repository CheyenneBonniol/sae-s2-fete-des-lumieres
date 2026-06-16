
package models;

import java.util.HashMap;

/**
 *Classe qui construit un lieu grace à son id et qui stock ses distances avec les autres lieux
 * @author Livio, Cheyenne
 */
public abstract class Lieu {
    private int id;
    public HashMap<Integer, Double> distances;
    
    
    /**
     * Constructeur de la classe Lieu qui recupère l'id entré en paramètre
     * @param id 
     */
    public Lieu(int id){
        this.id = id;
        distances = new HashMap<>();
    }
    
    
    
    /**
     * Outrepassement de la méthode toString afin d'afficher l'id
     * @return une chaîne de caractères contenant l'id du lieu
     */
    public String toString() {
        return Integer.toString(id);
    }
    
    

    /**
     * Renvoie l'id du lieu
     * @return un int qui est l'id du lieu
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    /**
     * Cette fonction renvoie les distances entre le lieu actuel et les autres
     * @return un hashMap rempli des distances entre le lieu actuel et les autres
     */
    public HashMap<Integer, Double> getDistances() {
        return distances;
    }
    
    
    
    /**
     * Fonction qui permet de calculer une distance entre deux points
     * @param L2 lieu d'arrivée
     * @return un double qui est la distance entre les lieux
     */
    public abstract double calculDistance(Lieu L2);
}
