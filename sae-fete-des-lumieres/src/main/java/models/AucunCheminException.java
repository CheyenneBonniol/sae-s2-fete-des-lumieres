
package models;

/**
 * Exception lorsqu'on ne trouve pas de chemin
 * 
 * @author cheyenne
 */
public class AucunCheminException extends Exception{
    public AucunCheminException(){
        super("Pas de chemin trouvé");
    }
}
