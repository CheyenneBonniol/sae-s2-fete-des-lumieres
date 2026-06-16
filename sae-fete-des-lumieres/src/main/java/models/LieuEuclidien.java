
package models;

/**
 * Classe qui construit un lieu avec des coordonnées euclidiennes et qui hérite de la classe Lieu
 * @author Livio, Cheyenne
 */
public class LieuEuclidien extends Lieu {
    private double x;
    private double y;
    
    /**
     * Constructeur qui enregistre les coordonnées x et y du lieu ainsi que son id
     * @param id
     * @param x
     * @param y 
     */
    public LieuEuclidien(int id, double x, double y) {
        super(id);
        this.x = x;
        this.y = y;
    }
    

    //Getters

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    //Setters
    
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    
    /**
     * Fonction qui permet de calculer une distance entre deux points euclidien
     * @param L2 le lieu d'arrivée
     * @return un double qui est la distance entre les lieux
     */
    @Override
    public double calculDistance(Lieu L2){
        LieuEuclidien B= (LieuEuclidien)L2;
        double distance=Math.sqrt((B.x -this.x)*(B.x -this.x)+(B.y-this.y)*(B.y-this.y));
        return distance;
    }
    
}
