package main;
/**
 * Classe principale du programme.
 * 
 */
public class Main {
    /**
     * Méthode principale du programme.
     * 
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        
        // Crée une nouvelle instance de la classe Interface_Connexion.
        Interface_Connexion jeu = new Interface_Connexion();
        // Méthode pour afficher le menu de connexion.
        jeu.menu_connexion();
    }
}
