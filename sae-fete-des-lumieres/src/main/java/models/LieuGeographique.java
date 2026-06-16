

   package models;

/**
 * Classe qui construit un lieu avec des coordonnées géographiques et qui hérite de la classe Lieu
 * @author Livio, Cheyenne
 */
public class LieuGeographique extends Lieu {
    private static final int RT=6378;//rayon de la terre
    private double latitude;
    private double longitude;
    private double latitudeR;
    private double longitudeR;
    private int signeLat;
    private int signeLon;
    
    /**
     * Constructeur
     * @param id
     * @param lat
     * @param lon 
     */
    public LieuGeographique(int id, double lat, double lon) {
        super(id);
        latitude=lat;
        longitude=lon;
        signeLat=(lat<0)?-1:1;//garde l'information de si le signe de la latitude est positif ou negatif
        signeLon=(lon<0)?-1:1;//pareil mais pour la longitude
        cooRadians(Math.abs(lat), Math.abs(lon));
    }
    
    
    //Getters

    public double getLatitude() {
        return latitude;
    }

    public double getLatitudeR() {
        return latitudeR;
    }

    public double getLongitudeR() {
        return longitudeR;
    }

    public double getLongitude() {
        return longitude;
    }
    
    //Setters
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    
    //Methods    
    /**
     * Fonction calculant les coordonnées en radians d'un lieu et les stockants 
     * @param lat
     * @param lon
     */
    private void cooRadians(double lat, double lon){
        
        double dLa=(int)lat, dLo=(int)lon;//on recupere les degres D
        double mLa=(int)((lat-dLa)*100.0), mLo=(int)((lon-dLo)*100.0);//on recupere les minutes M
        double DD;
        
        DD=dLa+(mLa/60.0);//degre decimaux pour la latitude 
        this.latitudeR=signeLat*Math.PI*(DD/180.0);//conversion en radians
        
        DD=dLo+(mLo/60);//Degre decimaux pour la longitude
        this.longitudeR=signeLon*Math.PI*(DD/180.0);
    }
    
     /**
     * Fonction qui permet de calculer une distance entre deux points de coordonnées géographique
     * @param L2
     * @return un double qui est la distance entre les lieux
     */
    @Override
    public double calculDistance(Lieu L2){
        LieuGeographique B=(LieuGeographique)L2;
        
        double prodSin=Math.sin(this.latitudeR)*Math.sin(B.latitudeR);
        double prodCos=Math.cos(this.latitudeR)*Math.cos(B.latitudeR)*Math.cos(this.longitudeR-B.longitudeR);
        double res = (RT*Math.acos(prodSin+prodCos));
        return res;
    }

  
}
