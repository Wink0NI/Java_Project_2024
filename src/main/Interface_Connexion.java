package main;

import java.util.Scanner;
import main.process.*;

/**
 * Cette classe gère l'interface de connexion pour le jeu "Question pour un champion".
 */
public class Interface_Connexion {
    private SysGestion gestion; // Gestion du système (probablement pour des fonctions utilitaires)
    private DBProcess dbProcess; // Processus de gestion de la base de données

    private Interface_Menu_Principal menu_Principal; // Interface du menu principal du jeu

    /**
     * Constructeur pour initialiser une instance d'Interface_Connexion.
     */
    public Interface_Connexion() {
        dbProcess = new DBProcess(); // Initialiser le processus de base de données
        menu_Principal = new Interface_Menu_Principal(); // Initialiser le menu principal
    }

    Scanner scanner = new Scanner(System.in); // Scanner pour lire les entrées de l'utilisateur

    /**
     * Affiche le menu de connexion et gère les actions de l'utilisateur.
     */
    public void menu_connexion() {
        while (true) {
            gestion.clear(); // Efface l'écran (ou effectue une action similaire)

            System.out.println("Vous jouez en ce moment à Question pour un champion: ");
            System.out.println("---------------------------------------------------");
            System.out.println("I - S'inscrire\nC - Se connecter\nQ - Quitter");
            System.out.print("-> ");

            // Lire l'entrée de l'utilisateur et exécuter l'action appropriée
            switch (scanner.nextLine().toUpperCase()) {
                case "Q":
                    // Quitter le programme
                    System.out.println("Au revoir...");
                    System.exit(0);

                case "I":
                    // Lancer le processus d'inscription
                    processus_inscription();
                    break;

                case "C":
                    // Lancer le processus de connexion
                    processus_connexion();
                    break;

                default:
                    // Si la commande est invalide, afficher un message d'erreur
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(1000); // Attendre 1 seconde avant de continuer
            }
        }
    }

    /**
     * Gère le processus d'inscription d'un joueur.
     */
    public void processus_inscription() {
        System.out.println("Entrer le nom de votre avatar (VOUS NE POURREZ PLUS LE MODIFIER):");
        String avatar = scanner.nextLine(); // Lire le nom de l'avatar
        System.out.println("Entrer votre mot de passe:");
        String mdp = scanner.nextLine(); // Lire le mot de passe
        System.out.println("Confirmer votre mot de passe:");
        String mdp_verif = scanner.nextLine(); // Lire la confirmation du mot de passe

        if (avatar.contains("?") || mdp.contains("?")) {
            System.out.println("Identifiant non autorisée, prsence de caractère non autorisée");
            gestion.wait(2000); // Attendre 2 secondes avant de continuer
        }
        // Vérifier si le mot de passe et sa confirmation correspondent
        else if (mdp.equals(mdp_verif)) {
            // Inscrire le nouvel avatar dans la base de données
            dbProcess.inscrire(new Avatar(avatar, mdp));
            System.out.println("Inscription réussie !");
            gestion.wait(2000); // Attendre 2 secondes avant de continuer
        } else {
            // Afficher un message d'erreur si les mots de passe ne correspondent pas
            System.out.println("Erreur: Vérification non valide. Le mot de passe tapé ne correspond pas au mot de passe entré prédécemment.");
            gestion.wait(2000); // Attendre 2 secondes avant de continuer
        }
    }

    /**
     * Gère le processus de connexion d'un joueur.
     */
    public void processus_connexion() {
        System.out.println("Entrer le nom de votre avatar:");
        String avatar = scanner.nextLine(); // Lire le nom de l'avatar
        System.out.println("Entrer votre mot de passe:");
        String mdp = scanner.nextLine(); // Lire le mot de passe

        // Vérifier les informations de connexion
        if (dbProcess.connecter(avatar, mdp)) {
            // Si la connexion réussit, afficher un message de succès
            System.out.println(String.format("Connexion réussie ! Vous êtes connecté en tant que %s.", avatar));
            gestion.wait(2000); // Attendre 2 secondes avant de continuer

            // Lancer le menu principal avec l'ID de l'utilisateur connecté
            menu_Principal.menu_principal(dbProcess.getUserByName(avatar).getId());
        } 
        gestion.wait(2000); // Attendre 2 secondes avant de continuer
    }
    
}

