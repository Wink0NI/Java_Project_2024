package main;

import java.util.Scanner;
import java.util.List;
import java.sql.*;
import main.process.*;

/**
 * Classe représentant l'interface de menu pour les classements.
 * Permet d'afficher les classements des joueurs selon différents critères.
 */
public class Interface_Menu_Classements {
    // Instance de SysGestion pour la gestion des tâches
    private SysGestion gestion = new SysGestion();

    // Instance de DBProcess pour l'accès aux données de la base
    private DBProcess dbProcess = new DBProcess();

    // Scanner pour lire les entrées de l'utilisateur
    private Scanner scanner = new Scanner(System.in);
    
    /**
     * Méthode qui affiche le menu des classements.
     * Ce menu permet de choisir entre afficher le classement par points ou par victoires, ou de retourner au menu principal.
     *
     * @param user_id l'identifiant de l'utilisateur connecté
     */
    public void menu_classements(String user_id) {
        while (true) {
            // Récupération des informations de l'utilisateur à partir de son identifiant
            Avatar user = dbProcess.getUserById(user_id);

            // Nettoyage de l'écran et affichage du menu
            gestion.clear();
            System.out.println(String.format("Menu classement. Connecté en tant que %s", user.getName()));
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            // Affichage des options disponibles
            System.out.println("P - Classement par points...");
            System.out.println("R - Classement par victoire...");
            System.out.println("T - Retour");

            // Lecture du choix de l'utilisateur et appel de la méthode appropriée
            switch (scanner.nextLine().toUpperCase()) {
                case "P":
                    afficher_classement_par_points(); // Affiche le classement par points
                    break;

                case "R":
                    afficher_classement_par_victoire(); // Affiche le classement par victoires
                    break;

                case "T":
                    System.out.println(String.format("Au revoir %s !!!", user.getName())); // Message d'adieu
                    gestion.wait(2000); // Attendre 2 secondes avant de revenir au menu
                    return; // Retour au menu principal

                default:
                    // Gestion d'une commande invalide
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000); // Attendre 2 secondes avant de redemander une commande
            }
        }
    }

    /**
     * Méthode qui affiche le classement par points.
     * Récupère les joueurs triés par leur nombre de points et les affiche.
     */
    private void afficher_classement_par_points() {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        // Récupération de la liste des joueurs triés par points
        List<String> classement = dbProcess.get_classement_points();

        if (classement.size() == 0) {
            // Aucun joueur dans le classement
            System.out.println("Aucun joueur...");
            gestion.wait(2000); // Attendre 2 secondes avant de revenir au menu
        } else {
            // Affichage des joueurs avec leur nombre de points
            for (String user : classement) {
                Avatar user_classment = dbProcess.getUserById(user); // Récupération des détails du joueur
                if (classement.indexOf(user) == 0) {
                    // Affiche le joueur avec le plus de points
                    System.out
                            .println(String.format("Le roi des points est: %s avec %s %s !!!", user_classment.getName(),
                                    user_classment.getPV(), user_classment.getPV() > 1 ? "points" : "point"));
                } else {
                    // Affiche les autres joueurs avec leurs points
                    System.out
                            .println(String.format("- %d: %s - %s %s", classement.indexOf(user) + 1, user_classment.getName(),
                            user_classment.getPV(), user_classment.getPV() > 1 ? "points" : "point"));
                }
            }
            System.out.println("Cliquez sur Entrée pour revenir...");
            scanner.nextLine(); // Attendre l'entrée de l'utilisateur pour revenir au menu
        }
    }

    /**
     * Méthode qui affiche le classement par victoire.
     * Récupère les joueurs triés par leur nombre de victoires et les affiche.
     */
    private void afficher_classement_par_victoire() {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        // Récupération de la liste des joueurs triés par nombre de victoires
        List<String> classement = dbProcess.get_classement_victoire();

        if (classement.size() == 0) {
            // Aucun joueur dans le classement
            System.out.println("Aucun joueur...");
            gestion.wait(2000); // Attendre 2 secondes avant de revenir au menu
        } else {
            // Affichage des joueurs avec leur nombre de victoires
            for (String user : classement) {
                Avatar user_classment = dbProcess.getUserById(user); // Récupération des détails du joueur
                ResultSet stats = dbProcess.getStat(user); // Récupération des statistiques du joueur
                int victoire;
                try {
                    victoire = stats.getInt("victoire_vs"); // Lecture du nombre de victoires
                    stats.close(); // Fermeture du ResultSet
                } catch (SQLException e) {
                    victoire = 0; // Si une erreur se produit, on considère qu'il n'y a pas de victoires
                }
                
                if (classement.indexOf(user) == 0) {
                    // Affiche le joueur avec le plus de victoires
                    System.out
                            .println(String.format("Le roi du jeu est: %s avec %s %s !!!", user_classment.getName(),
                                    victoire, victoire > 1 ? "victoires" : "victoire"));
                } else {
                    // Affiche les autres joueurs avec leurs victoires
                    System.out
                            .println(String.format("- %d: %s - %s %s", classement.indexOf(user) + 1, user_classment.getName(),
                                    victoire, victoire > 1 ? "victoires" : "victoire"));
                }
            }
            System.out.println("Cliquez sur Entrée pour revenir...");
            scanner.nextLine(); // Attendre l'entrée de l'utilisateur pour revenir au menu
        }
    }
}
