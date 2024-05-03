import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import process.*;

public class Jeu {
    protected SysGestion gestion;
    protected DBProcess dbProcess;

    protected String db_url = "jdbc:sqlite:BDD/BDD_MonstreDuSavoir.db";
    protected String nom_admin = "admin";

    private Avatar joueur;
    Scanner scanner = new Scanner(System.in);

    



    public void menu_connexion() {

        while (true) {
            gestion.clear();

            System.out.println("Vous jouez en ce moment à Question pour un champion: ");
            System.out.println("---------------------------------------------------");
            System.out.println("I - S'inscrire\nC - Se connecter\nQ - Quitter");
            System.out.print("-> ");

            switch (scanner.next().toUpperCase()) {
                case "Q":
                    System.out.println("Au revoir...");
                    System.exit(0);

                case "I":
                    processus_inscription();

                default:
                    System.out.println("Commande saisie invalide...");

            }
            gestion.wait(1000);

        }

    }

    public void processus_inscription() {
        System.out.println("Entrer le nom de votre avatar (VOUS NE POURREZ PLUS LE MODIFIER):\n-> ");
        String avatar = scanner.next();
        System.out.println("Entrer votre mot de passe:\n-> ");
        String mdp = scanner.next();
        System.out.println("Confirmer votre mot de passe:\n-> ");
        String mdp_verif = scanner.next();

        if (mdp.equals(mdp_verif)) {
            dbProcess.inscrire(new Avatar(avatar, mdp));
            System.out.println("\nInscription réussie !");
            gestion.wait(2000);
        } else {
            System.out.println("Erreur: Vérification non valide. Le mot de passe tapé ne correspond pas au mot de passe entré prédécemment.");
            gestion.wait(2000);
        }
    }
}
