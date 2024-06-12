package main;
import java.sql.*;
import java.util.Scanner;

import main.process.SysGestion;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
/**
 * Classe représentant l'interface principale du jeu.
 * 
 */
public class Interface_Menu_Principal {
        Scanner scanner = new Scanner(System.in);
        private SysGestion gestion;
        private DBProcess dbProcess;
        /**
         * Constructeur de la classe Interface_Menu_Principal.
         * 
         * @author Votre nom et prénom
         * @version 1.0
         */
        public Interface_Menu_Principal() {
                /**
                 * Méthode pour initialiser les objets de la classe.
                * 
                 * @param none
                */
                dbProcess = new DBProcess();
                gestion = new SysGestion();
        }

        private Interface_Jeu jeu = new Interface_Jeu();
        private Interface_Menu_Administrateur admin = new Interface_Menu_Administrateur();
        private Interface_Menu_Parametres parametres = new Interface_Menu_Parametres();

        Random rand = new Random();
        /**
        * Méthode pour afficher le menu principal.
        * 
        * @param user_id l'identifiant de l'utilisateur connecté
        */
        public void menu_principal(String user_id) {

                while (true) {
                        List<HashMap<String, Object>> defis = dbProcess.get_duels(user_id);
                        List<HashMap<String, String>> resultat_defi = dbProcess.get_duels_resultat(user_id);
                        List<Question> question_requete = dbProcess.get_question_response(user_id);
                        Avatar user = dbProcess.getUserById(user_id);

                        gestion.clear();
                        System.out.println(String.format("Menu principal du jeu. Connecté en tant que %s",
                                        user.getName()));
                        System.out.println(
                                        "----------------------------------------------------------------------------------------------------------------------");
                        System.out.println(
                                        "NOTIFICATIONS");

                        if (defis.size() == 0 && resultat_defi.size() == 0 && question_requete.size() == 0)
                                System.out.println("Rien à signaler.");

                        if (question_requete.size() > 0) {
                                for (Question question : question_requete) {
                                        if (question.getUserId().equals("ACCEPTED")) {
                                                System.out.println(
                                                                String.format(
                                                                                "La question %s (score %d) a été accepté par un modérateur. La question a été rajouté dans le thème %s.",
                                                                                question.getQuestion(),
                                                                                question.getPoints(),
                                                                                question.getTheme()));
                                        } else {
                                                System.out.println(
                                                                String.format(
                                                                                "La question %s (score %d) a été refusé par un modérateur.",
                                                                                question.getQuestion(),
                                                                                question.getPoints()));
                                        }
                                        dbProcess.removeQuestionRequest(question.getId());
                                }
                        }

                        if (resultat_defi.size() > 0) {
                                for (HashMap<String, String> defi : resultat_defi) {
                                        Avatar ennemi = dbProcess.getUserById(defi.get("user_cible"));
                                        if (defi.get("vainqueur").equals(user_id)) {
                                                System.out.println(
                                                                String.format(
                                                                                "%s a accepté ton défi, mais il n'a pas eu suffisament de puissance pour te dominer. Bravo !!!",
                                                                                ennemi.getName())

                                                );
                                                dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"), true);
                                        } else if (defi.get("vainqueur").equals("Aucune")) {
                                                System.out.println(
                                                                String.format(
                                                                                "%s a accepté ton défi, et il a eu suffisament de point pour ne pas perdre.",
                                                                                ennemi.getName())

                                                );
                                        } else {
                                                System.out.println(
                                                                String.format(
                                                                                "%s a accepté ton défi, et il a pas eu suffisament de puissance pour te dominer. Dommage !!!",
                                                                                ennemi.getName())

                                                );
                                                dbProcess.updateStatsDefaite(user_id, defi.get("duel_id"), true);
                                        }
                                }
                        }
                        if (defis.size() > 0) {
                                Timestamp tps = new Timestamp(System.currentTimeMillis());
                                for (HashMap<String, Object> duel : defis) {
                                        if (tps.before((Timestamp) duel.get("temps_limite"))) {
                                                System.out.println(
                                                                String.format("- %s veut te défier: %s restant.",
                                                                                dbProcess.getUserById((String) duel
                                                                                                .get("user_atq"))
                                                                                                .getName(),
                                                                                gestion.afficherTempsRestant(
                                                                                                (Timestamp) duel.get(
                                                                                                                "temps_limite"))));

                                        } else {
                                                int point_perdus = (int) duel.get("score_atq");

                                                System.out.println(
                                                                String.format(
                                                                                "- %s voulait te défier, mais le temps est écoulé... Tu as perdu %d points.",
                                                                                dbProcess.getUserById((String) duel
                                                                                                .get("user_atq"))
                                                                                                .getName(),
                                                                                point_perdus));

                                                dbProcess.updatePV(user_id, user.getPV() - point_perdus <= 0 ? 0
                                                                : user.getPV() - point_perdus);
                                                dbProcess.updateStatsOubli((String) duel.get("user_atq"), user_id,
                                                                (String) duel.get("duel_id"));

                                        }
                                }
                        }
                        System.out.println(
                                        "----------------------------------------------------------------------------------------------------------------------");
                        System.out.println("J - Jouer");
                        System.out.println("S - Rue sociale");
                        System.out.println("N - Ajouter des notes");

                        if (dbProcess.isAdmin(user.getName())) {
                                System.out.println("A - Mode Administrateur");
                        }

                        System.out.println("D - Se déconnecter");
                        switch (scanner.nextLine().toUpperCase()) {
                                case "J":
                                        jeu.menu_jouer(user_id);
                                        break;

                                case "S":
                                        parametres.menu_parametres(user_id);
                                        break;

                                case "D":
                                        System.out.println(String.format("Au revoir %s !!!", user.getName()));
                                        gestion.wait(2000);
                                        return;
                                case "A":
                                        if (dbProcess.isAdmin(user.getName()))
                                                admin.menu_administrateur(user_id);
                                        else {
                                                System.out.println("Commande saisie invalide...");
                                                gestion.wait(2000);
                                        }
                                        break;
                                case "N":
                                        ajouter_notes(user_id);
                                        gestion.wait(2000);
                                        break;
                                default:
                                        System.out.println("Commande saisie invalide...");
                                        gestion.wait(2000);

                        }
                }
        }
        /**
         * Méthode pour ajouter des notes.
        * 
        * @param user_id l'identifiant de l'utilisateur connecté
         */
        private void ajouter_notes(String user_id) {
                System.out.println(
                                "----------------------------------------------------------------------------------------------------------------------");
                System.out.println("Ajout des notes (entre 0 et 20).");
                System.out.println("R - Retour");

                while (true) {
                        System.out.println(
                                "----------------------------------------------------------------------------------------------------------------------");
                        String note = scanner.nextLine().toLowerCase();

                        if (note.equals("")) System.out.println("ERREUR: Note invalide.");
                        else if (note.equals("r")) {
                                System.out.println("Retour au menu principal.");
                                break;
                        }
                        
                        try {
                                int note_chiffre = Integer.parseInt(note);
                                if (note_chiffre < 0 || note_chiffre > 20) System.out.println("ERREUR: Note doit être entre 0 et 20.");
                                else {
                                        Avatar user = dbProcess.getUserById(user_id);

                                        note_chiffre = note_chiffre <10 ?  - rand.nextInt((note_chiffre + 10 + 1)*2) - 1 : rand.nextInt((note_chiffre + 10 + 1)*2) + 1;
                                        String info_score = note_chiffre < 10 ? "perdu" : "gagné";

                                         System.out.println(String.format("Note ajouté: Vous avez %s %s %s.", info_score, note_chiffre < 0 ? - note_chiffre : note_chiffre , note_chiffre > 1 || note_chiffre < 1 ? "points" : "point"));
                                        note_chiffre = user.getPV() + note_chiffre < 0 ? 0 : note_chiffre;
                                        dbProcess.updatePV(user_id, note_chiffre);
                                }
                        } catch (Exception e) {
                                System.out.println("ERREUR: Le nombre donné est invalide.");
                        }
                }

        }

}
