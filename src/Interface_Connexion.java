import java.sql.*;
import java.util.Scanner;

import process.*;

/**
 * Cette classe gère l'interface de connexion pour le jeu "Question pour un champion".
 */
public class Interface_Connexion {
    protected SysGestion gestion; // Gestion du système
    protected DBProcess dbProcess; // Processus de base de données

    protected Interface_Menu_Principal menu_Principal; // Interface du menu principal

    /**
     * Constructeur pour initialiser une instance d'Interface_Connexion.
     */
    public Interface_Connexion() {
        dbProcess = new DBProcess(); // Initialiser dbProcess ici
        menu_Principal = new Interface_Menu_Principal();
    }

    private Avatar joueur; // Avatar du joueur
    Scanner scanner = new Scanner(System.in);

    /**
     * Affiche le menu de connexion et gère les actions de l'utilisateur.
     */
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
                    break;

                case "C":
                    processus_connexion();
                    break;

                default:
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(1000);
            }
        }
    }

    /**
     * Gère le processus d'inscription d'un joueur.
     */
    public void processus_inscription() {
        System.out.println("Entrer le nom de votre avatar (VOUS NE POURREZ PLUS LE MODIFIER):");
        String avatar = scanner.next();
        System.out.println("Entrer votre mot de passe:");
        String mdp = scanner.next();
        System.out.println("Confirmer votre mot de passe:");
        String mdp_verif = scanner.next();

        if (mdp.equals(mdp_verif)) {
            dbProcess.inscrire(new Avatar(avatar, mdp));
            System.out.println("Inscription réussie !");
            gestion.wait(2000);
        } else {
            System.out.println("Erreur: Vérification non valide. Le mot de passe tapé ne correspond pas au mot de passe entré prédécemment.");
            gestion.wait(2000);
        }
    }

    /**
     * Gère le processus de connexion d'un joueur.
     */
    public void processus_connexion() {
        System.out.println("Entrer le nom de votre avatar:");
        String avatar = scanner.next();
        System.out.println("Entrer votre mot de passe:");
        String mdp = scanner.next();

        if (dbProcess.connecter(avatar, mdp)) {
            System.out.println(String.format("Connecxion réussi ! Vous êtes connecté en tant que %s.", avatar));
            gestion.wait(2000);
            menu_Principal.menu_principal(avatar);
        } 
        gestion.wait(2000);
    }
}

