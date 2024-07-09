package main;

import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.HashMap;
import java.util.Random;

/**
 * Classe représentant l'interface principale du jeu.
 * 
 * Ce menu est le point d'entrée principal pour l'utilisateur connecté.
 * Il permet de jouer, accéder au menu des paramètres, ajouter des notes, se déconnecter,
 * et pour les administrateurs, accéder aux fonctionnalités d'administration.
 */
public class Interface_Menu_Principal {
    // Scanner pour lire les entrées de l'utilisateur
    Scanner scanner = new Scanner(System.in);

    // Instances des classes nécessaires pour la gestion des fonctionnalités
    private SysGestion gestion;
    private DBProcess dbProcess;
    private Interface_Jeu jeu = new Interface_Jeu();
    private Interface_Menu_Administrateur admin = new Interface_Menu_Administrateur();
    private Interface_Menu_Parametres parametres = new Interface_Menu_Parametres();
    private Random rand = new Random();

    /**
     * Constructeur de la classe Interface_Menu_Principal.
     * 
     * Initialise les objets nécessaires pour la gestion des tâches et l'accès aux données.
     * 
     * @author Votre nom et prénom
     * @version 1.0
     */
    public Interface_Menu_Principal() {
        dbProcess = new DBProcess();
        gestion = new SysGestion();
    }

    /**
     * Méthode pour afficher le menu principal du jeu.
     * 
     * Affiche les notifications pour les défis, les résultats des défis, et les demandes de question.
     * Propose des options telles que jouer, accéder au menu des paramètres, ajouter des notes,
     * entrer en mode administrateur (si l'utilisateur est administrateur), et se déconnecter.
     * 
     * @param user_id l'identifiant de l'utilisateur connecté
     */
    public void menu_principal(String user_id) {

        while (true) {
            // Récupération des informations nécessaires depuis la base de données
            List<HashMap<String, Object>> defis = dbProcess.get_duels(user_id);
            List<HashMap<String, String>> resultat_defi = dbProcess.get_duels_resultat(user_id);
            List<Question> question_requete = dbProcess.get_question_response(user_id);
            Avatar user = dbProcess.getUserById(user_id);

            // Nettoyage de l'écran et affichage du menu principal
            gestion.clear();
            System.out.println(String.format("Menu principal du jeu. Connecté en tant que %s", user.getName()));
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.println("NOTIFICATIONS");

            // Affichage des notifications liées aux questions
            if (defis.size() == 0 && resultat_defi.size() == 0 && question_requete.size() == 0)
                System.out.println("Rien à signaler.");

            if (question_requete.size() > 0) {
                for (Question question : question_requete) {
                    if (question.getUserId().equals("ACCEPTED")) {
                        System.out.println(String.format(
                            "La question %s (score %d) a été acceptée par un modérateur. La question a été rajoutée dans le thème %s.",
                            question.getQuestion(),
                            question.getPoints(),
                            question.getTheme()
                        ));
                    } else {
                        System.out.println(String.format(
                            "La question %s (score %d) a été refusée par un modérateur.",
                            question.getQuestion(),
                            question.getPoints()
                        ));
                    }
                    // Suppression de la demande de question après traitement
                    dbProcess.removeQuestionRequest(question.getId());
                }
            }

            // Affichage des notifications liées aux résultats des défis
            if (resultat_defi.size() > 0) {
                for (HashMap<String, String> defi : resultat_defi) {
                    Avatar ennemi = dbProcess.getUserById(defi.get("user_cible"));
                    if (defi.get("vainqueur").equals(user_id)) {
                        System.out.println(String.format(
                            "%s a accepté ton défi, mais il n'a pas eu suffisamment de puissance pour te dominer. Bravo !!!",
                            ennemi.getName()
                        ));
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"), true);
                    } else if (defi.get("vainqueur").equals("Aucune")) {
                        System.out.println(String.format(
                            "%s a accepté ton défi, et il a eu suffisamment de points pour ne pas perdre.",
                            ennemi.getName()
                        ));
                    } else {
                        System.out.println(String.format(
                            "%s a accepté ton défi, et il n'a pas eu suffisamment de puissance pour te dominer. Dommage !!!",
                            ennemi.getName()
                        ));
                        dbProcess.updateStatsDefaite(user_id, defi.get("duel_id"), true);
                    }
                }
            }

            // Affichage des notifications liées aux défis en attente
            if (defis.size() > 0) {
                Timestamp tps = new Timestamp(System.currentTimeMillis());
                for (HashMap<String, Object> duel : defis) {
                    if (tps.before((Timestamp) duel.get("temps_limite"))) {
                        System.out.println(String.format(
                            "- %s veut te défier: %s restant.",
                            dbProcess.getUserById((String) duel.get("user_atq")).getName(),
                            gestion.afficherTempsRestant((Timestamp) duel.get("temps_limite"))
                        ));
                    } else {
                        int point_perdus = (int) duel.get("score_atq");
                        System.out.println(String.format(
                            "- %s voulait te défier, mais le temps est écoulé... Tu as perdu %d points.",
                            dbProcess.getUserById((String) duel.get("user_atq")).getName(),
                            point_perdus
                        ));
                        // Mise à jour des points perdus et des statistiques de défi oublié
                        dbProcess.updatePV(user_id, user.getPV() - point_perdus <= 0 ? 0 : user.getPV() - point_perdus);
                        dbProcess.updateStatsOubli((String) duel.get("user_atq"), user_id, (String) duel.get("duel_id"));
                    }
                }
            }

            // Affichage du menu principal
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.println("J - Jouer");
            System.out.println("S - Rue sociale");
            System.out.println("N - Ajouter des notes");

            if (dbProcess.isAdmin(user.getName())) {
                System.out.println("A - Mode Administrateur");
            }

            System.out.println("D - Se déconnecter");

            // Traitement de l'entrée utilisateur pour accéder aux différentes options
            switch (scanner.nextLine().toUpperCase()) {
                case "J":
                    // Accède au menu de jeu
                    jeu.menu_jouer(user_id);
                    break;

                case "S":
                    // Accède au menu des paramètres
                    parametres.menu_parametres(user_id);
                    break;

                case "D":
                    // Affiche un message d'adieu et se déconnecte
                    System.out.println(String.format("Au revoir %s !!!", user.getName()));
                    gestion.wait(2000);
                    return;

                case "A":
                    // Accède au mode administrateur si l'utilisateur est un administrateur
                    if (dbProcess.isAdmin(user.getName()))
                        admin.menu_administrateur(user_id);
                    else {
                        System.out.println("Commande saisie invalide...");
                        gestion.wait(2000);
                    }
                    break;

                case "N":
                    // Accède à la fonctionnalité d'ajout de notes
                    ajouter_notes(user_id);
                    gestion.wait(2000);
                    break;

                default:
                    // Gestion d'une commande invalide
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000);
            }
        }
    }

    /**
     * Méthode pour ajouter des notes au joueur.
     * 
     * Permet à l'utilisateur de saisir une note (entre 0 et 20), qui est ensuite utilisée pour ajuster les points de vie (PV) de l'utilisateur.
     * La note est ajustée de manière aléatoire pour refléter un gain ou une perte de points.
     * 
     * @param user_id l'identifiant de l'utilisateur connecté
     */
    private void ajouter_notes(String user_id) {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Ajout des notes (entre 0 et 20).");
        System.out.println("R - Retour");

        while (true) {
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            String note = scanner.nextLine().toLowerCase();

            if (note.equals("")) {
                System.out.println("ERREUR: Note invalide.");
            } else if (note.equals("r")) {
                System.out.println("Retour au menu principal.");
                break;
            } 

            try {
                int note_chiffre = Integer.parseInt(note);
                if (note_chiffre < 0 || note_chiffre > 20) {
                    System.out.println("ERREUR: Note doit être entre 0 et 20.");
                } else {
                    // Calcul aléatoire du résultat de la note
                    note_chiffre = note_chiffre < 10 ? -rand.nextInt((note_chiffre + 10 + 1) * 2) - 1 : rand.nextInt((note_chiffre + 10 + 1) * 2) + 1;
                    String info_score = note_chiffre < 0 ? "perdu" : "gagné";

                    System.out.println(String.format(
                        "Note ajoutée: Vous avez %s %s %s.",
                        info_score,
                        note_chiffre < 0 ? -note_chiffre : note_chiffre,
                        note_chiffre > 1 || note_chiffre < 1 ? "points" : "point"
                    ));
                    
                    // Mise à jour des points de vie de l'utilisateur
                    Avatar user = dbProcess.getUserById(user_id);
                    note_chiffre = user.getPV() + note_chiffre < 0 ? 0 : note_chiffre;
                    dbProcess.updatePV(user_id, note_chiffre);
                }
            } catch (Exception e) {
                System.out.println("ERREUR: Le nombre donné est invalide.");
            }
        }
    }
}
