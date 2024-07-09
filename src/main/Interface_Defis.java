package main;
import java.util.Scanner;
import main.process.SysGestion;
import java.util.List;
import java.util.HashMap;
import java.sql.*;

/**
 * Cette classe gère l'interface de défis pour le jeu "Question pour un champion".
 */
public class Interface_Defis {

    Scanner scanner = new Scanner(System.in); // Scanner pour lire les entrées utilisateur
    private SysGestion gestion; // Instance de SysGestion pour gérer les opérations du système

    protected DBProcess dbProcess; // Instance de DBProcess pour les opérations liées à la base de données

    /**
     * Constructeur pour initialiser une instance d'Interface_Defis.
     */
    public Interface_Defis() {
        dbProcess = new DBProcess(); // Initialisation de dbProcess
        gestion = new SysGestion(); // Initialisation de SysGestion
    }

    /**
     * Affiche le menu des défis et gère les actions de l'utilisateur.
     * 
     * @param user_id l'identifiant de l'utilisateur courant
     */
    public void menu_Defis(String user_id) {

        while (true) {
            Avatar user = dbProcess.getUserById(user_id); // Récupère les informations de l'utilisateur
            List<HashMap<String, Object>> defis = dbProcess.get_duels(user_id); // Récupère la liste des défis en cours pour l'utilisateur

            List<HashMap<String, String>> resultat_defi = dbProcess.get_duels_resultat(user_id); // Récupère les résultats des défis

            System.out.println("Menu des défis");

            // Affichage des notifications des résultats de défis
            if (resultat_defi.size() > 0) {
                System.out.println("----------------------------------------------------------------------------------------------------------------------");
                System.out.println("NOTIFICATIONS");
                for (HashMap<String, String> defi : resultat_defi) {
                    Avatar ennemi = dbProcess.getUserById(defi.get("user_cible"));
                    if (defi.get("vainqueur").equals(user_id)) {
                        System.out.println(String.format("%s a accepté ton défi, mais il n'a pas eu suffisament de puissance pour te dominer. Bravo !!!", ennemi.getName()));
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"), true); // Met à jour les statistiques de victoire
                    } else if (defi.get("vainqueur").equals("Aucune")) {
                        System.out.println(String.format("%s a accepté ton défi, et il a eu suffisament de point pour ne pas perdre.", ennemi.getName()));
                    } else {
                        System.out.println(String.format("%s a accepté ton défi, et il a pas eu suffisament de puissance pour te dominer. Dommage !!!", ennemi.getName()));
                        dbProcess.updateStatsDefaite(user_id, defi.get("duel_id"), true); // Met à jour les statistiques de défaite
                    }
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------");

            // Vérifie s'il y a des défis en cours
            if (defis.size() == 0) {
                System.out.println("Aucun défi pour vous, revenez plus tard...");
                gestion.wait(3000); // Attend 3 secondes avant de retourner au menu principal
                return;
            }

            // Affichage des défis en cours
            if (defis.size() > 0) {
                Timestamp tps = new Timestamp(System.currentTimeMillis());
                int cpt = 1;
                for (HashMap<String, Object> duel : defis) {
                    if (tps.before((Timestamp) duel.get("temps_limite"))) {
                        System.out.println(String.format("%d - %s veut te défier: %s restant.",
                                cpt,
                                dbProcess.getUserById((String) duel.get("user_atq")).getName(),
                                gestion.afficherTempsRestant((Timestamp) duel.get("temps_limite"))));
                    } else {
                        int point_perdus = (int) duel.get("score_atq");
                        System.out.println(String.format("- %s voulait te défier, mais le temps est écoulé... Tu as perdu %d points.",
                                dbProcess.getUserById((String) duel.get("user_atq")).getName(),
                                point_perdus));
                        dbProcess.updatePV(user_id, user.getPV() - point_perdus <= 0 ? 0 : user.getPV() - point_perdus); // Met à jour les PV de l'utilisateur
                        dbProcess.updateStatsOubli((String) duel.get("user_atq"), user_id, (String) duel.get("duel_id")); // Met à jour les statistiques de l'oubli du duel
                        System.out.println("Appuyer sur Entrée pour continuer...");
                        scanner.nextLine();
                    }
                }
            }
            System.out.println("R - Retour");

            // Gestion des entrées utilisateur pour choisir un défi
            try {
                String choix_defi = scanner.nextLine();

                if (choix_defi.toUpperCase().equals("R")) {
                    System.out.println("Retour au menu principal.");
                    gestion.wait(2000); // Attend 2 secondes avant de retourner au menu principal
                    return;
                }

                if (0 < Integer.parseInt(choix_defi) && defis.size() >= Integer.parseInt(choix_defi)) {
                    defi_vs(user, defis.get(Integer.parseInt(choix_defi) - 1)); // Lance le défi sélectionné
                    gestion.wait(2000); // Attend 2 secondes après le défi
                } else {
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000); // Attend 2 secondes en cas de commande invalide
                }

            } catch (Exception e) {
                System.out.println("ERREUR: Nombre invalide...");
                System.out.println(e);
                gestion.wait(2000); // Attend 2 secondes en cas d'erreur
            }
        }
    }

    /**
     * Gère le processus d'un défi vs.
     * 
     * @param user l'avatar de l'utilisateur courant
     * @param defi les informations du défi en cours
     */
    private void defi_vs(Avatar user, HashMap<String, Object> defi) {

        String theme = "";
        System.out.println("Défi vs");

        // Récupère les questions du duel
        List<Question> questions = dbProcess.get_question_duel((String) defi.get("duel_id"));
        int nb_questions = questions.size();
        Avatar atq_avatar = dbProcess.getUserById((String) defi.get("user_atq"));

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("Tu devras répondre à une série de %s questions.", nb_questions));
        System.out.println("Les questions auquelles tu répondras correctement seront ajoutées dans ton cerveau !");
        System.out.println("Ton score te permettra d'augmenter ton savoir ou d'en perdre si tu obtiens ou non la moyenne.");
        System.out.println("Cependant, n'oublie pas que chaque question te fera gagner ou perdre du savoir ! Le résultat du défi ne sera qu'un bonus/malus sur ton savoir !");
        System.out.println(String.format("Tente de répondre à un maximum de questions justes pour battre %s.", atq_avatar.getName()));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Le jeu va commencer. Taper n'importe quoi pour débuter\nR - Annuler");

        // Vérifie si l'utilisateur souhaite annuler le défi
        if (scanner.nextLine().toUpperCase().equals("R")) {
            return;
        } else { // Le défi commence
            int score = 0;
            int currentPV = user.getPV();
            int pt_gagne = 0;
            int pt_perdu = 0;

            System.out.println(String.format("Défi vs %s", atq_avatar.getName()));

            // Boucle pour chaque question du défi
            for (int i = 0; i < nb_questions; i++) {
                Question question = questions.get(i);

                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println(String.format("Question n°%d - %s", i + 1, question.getQuestion()));

                // Affiche les choix de réponses
                for (int num_choix = 0; num_choix < question.getChoices().size(); num_choix++) {
                    System.out.println((num_choix + 1) + " - " + question.getChoices().get(num_choix));
                }
                System.out.print("Réponse -> ");
                String resp = scanner.nextLine();

                // Vérifie si la réponse est correcte
                if (resp.equals(question.getResponse())) {
                    System.out.println("Bonne réponse !");
                    score++;
                    pt_gagne += question.getPoints();
                    currentPV += question.getPoints();
                } else {
                    System.out.println("Faux ! Réponse correcte: " + question.getResponse());
                    if (currentPV - question.getPoints() < 0) {
                        currentPV = 0;
                    } else {
                        currentPV -= question.getPoints();
                    }
                    pt_perdu += question.getPoints();
                    gestion.wait(2000); // Attend 2 secondes avant de passer à la question suivante
                }
            }

            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
            int changement_val_savoir = score - (nb_questions / 2);
            dbProcess.updatePV(user.getId(), currentPV + changement_val_savoir); // Met à jour les PV de l'utilisateur
            dbProcess.updateStats(user.getId(), nb_questions, score, "solo", pt_gagne, pt_perdu); // Met à jour les statistiques du défi

            System.out.println("Résultat du défi: " + score + "/" + nb_questions);
            if (score > (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score supérieur à la moyenne ! Voici un bonus de savoir: +" + changement_val_savoir);
            } else if (score < (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score inférieur à la moyenne ! Tu écopes donc d'un malus de savoir: " + changement_val_savoir);
            } else {
                System.out.println("Tu as obtenu la moyenne ! Tu n'obtiens ni de bonus ni de malus de savoir...");
            }
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");

            int pts = pt_gagne - pt_perdu;
            if (pts < 0)
                pts = 0;

            int pts_ennemi = (int) defi.get("score_atq");
            System.out.println(String.format("%s a marqué %s...", (String) atq_avatar.getName(), pts_ennemi > 1 ? String.format("%s points", pts_ennemi) : String.format("%s point", pts_ennemi)));
            gestion.wait(3000); // Attend 3 secondes avant d'afficher le score de l'utilisateur
            System.out.println(String.format("Et tu as marqué %s...", pts  > 1 ? String.format("%s points", pts) : String.format("%s point", pts)));

            // Détermine le vainqueur du défi
            if (pts_ennemi < pts) {
                System.out.println(String.format("Et le vainqueur est %s !!! Félicitations...", user.getName()));
                dbProcess.updateStatsVictoire(user.getId(), (String) defi.get("duel_id"), false); // Met à jour les statistiques de victoire
                dbProcess.updateDuelVainqueur((String) defi.get("duel_id"), user.getId()); // Met à jour le vainqueur du duel
            } else if (pts_ennemi > pts) {
                System.out.println(String.format("Et le vainqueur est %s !!! Dommage...", atq_avatar.getName()));
                dbProcess.updateStatsDefaite(user.getId(), (String) defi.get("duel_id"), false); // Met à jour les statistiques de défaite
                dbProcess.updateDuelVainqueur((String) defi.get("duel_id"), atq_avatar.getId()); // Met à jour le vainqueur du duel
            } else {
                System.out.println("Match nul...");
                dbProcess.updateDuelVainqueur((String) defi.get("duel_id"), "Aucune"); // Met à jour le duel comme nul
            }

            System.err.println();            
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println("Taper n'importe quoi pour quitter");
            scanner.nextLine(); // Attend que l'utilisateur appuie sur Entrée pour quitter
        }
    }
}
