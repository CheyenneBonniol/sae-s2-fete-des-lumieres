
package models;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import views.Interface;


/**
 *
 * @author FERRANDEZ Livio, BONNIOL Cheyenne
 */
public class Main {
    
    public static void main(String[] args) throws UnsupportedLookAndFeelException{

            UIManager.setLookAndFeel(new FlatMacLightLaf());
            Traitement test = new Traitement();
            Chercheur chercheur = new Chercheur();
            Interface i = new Interface(test, chercheur);
            
            
            //RENDU FICHIER
            //A NE PAS SUPPRIMER
            /*       
            for(int j=1; j<=10; j++){
            Traitement test = new Traitement();
            Chercheur chercheur = new Chercheur();
            try {
            FileInputStream file = new FileInputStream("../DataTest/test"+j+".txt");
            test.LectureFichier(file);
            test.remplirDistances();
            chercheur.rechercheAleatoire(test.listeLieux);
            ArrayList<Lieu> test1=chercheur.appelGlouton(test.listeLieux);
            chercheur.rechercheParInsertion(test.listeLieux);
            chercheur.rechercheChangePremier(test1);
            test.sortieTexte(chercheur, j);
            
            }catch(Exception e){
                e.printStackTrace();  
            }}*/

           
    }
}
