
package models;

/**
 * Cette exception traduit une erreur car une liste de lieux est vide
 * @author Livio
 */
public class ListeDesLieuxVideException extends Exception {
    public ListeDesLieuxVideException() {
        super("La liste des lieux ne contient aucun objet Lieu");
    }
}
