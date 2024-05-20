import process.SysGestion;
import java.util.Scanner;
import java.util.List;
import java.util.HashMap;
import java.sql.*;

public class Interface_Defis {

    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;

    protected DBProcess dbProcess;

    public Interface_Defis() {
        dbProcess = new DBProcess(); // Initialize dbProcess here
        gestion = new SysGestion();
    }

    public void menu_Defis(String user_id) {

        while (true) {
            Avatar user = dbProcess.getUserById(user_id);
            List<HashMap<String, Object>> defis = dbProcess.get_duels(user_id);

            List<HashMap<String, String>> resultat_defi = dbProcess.get_duels_resultat(user_id);

            gestion.clear();

            System.out.println("Menu des défis");

            if (resultat_defi.size() > 0) {
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------");
                System.out.println(
                        "NOTIFICATIONS");
                for (HashMap<String, String> defi : resultat_defi) {
                    if (defi.get("vainqueur").equals(user_id)) {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, mais il n'a pas eu suffisament de puissance pour te dominer. Bravo !!!",
                                        defi.get("user_cible"))

                        );
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"));
                    } else if (defi.get("vainqueur").equals("Aucune")) {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, et il a eu suffisament de point pour ne pas perdre.",
                                        defi.get("user_cible"))

                        );
                    } else {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, et il a pas eu suffisament de puissance pour te dominer. Dommage !!!",
                                        defi.get("user_cible"))

                        );
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"));
                    }
                }
            }
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            if (defis.size() == 0) {
                System.out.println("Aucun défi pour vous, revenez plus tard...");
                gestion.wait(3000);
                return;
            }

            if (defis.size() > 0) {
                Timestamp tps = new Timestamp(System.currentTimeMillis());
                int cpt = 1;
                for (HashMap<String, Object> duel : defis) {
                    if (tps.before((Timestamp) duel.get("temps_limite"))) {
                        System.out.println(
                                String.format("%d - %s veut te défier: %s restant.",
                                        cpt,
                                        dbProcess.getUserById((String) duel.get("user_atq")).getName(),
                                        gestion.afficherTempsRestant((Timestamp) duel.get("temps_limite"))));

                    } else {
                        int point_perdus = (int) duel.get("score_atq");

                        System.out.println(
                                String.format(
                                        "- %s voulait te défier, mais le temps est écoulé... Tu as perdu %d points.",
                                        dbProcess.getUserById((String) duel.get("user_atq")).getName(),
                                        point_perdus));

                        dbProcess.updatePV(user_id, user.getPV() - point_perdus <= 0 ? 0 : user.getPV() - point_perdus);
                        dbProcess.updateStatsOubli((String) duel.get("user_atq"), user_id,
                                (String) duel.get("duel_id"));

                        System.out.println("Appuyer sur Entrée pour continuer...");
                        scanner.nextLine();

                    }
                }
            }
            System.out.println("R - Retour");
            try {
                String choix_defi = scanner.nextLine();

                if (choix_defi.toUpperCase().equals("R")) {
                    System.out.println("Retour au menu principal.");
                    gestion.wait(2000);
                    return;
                }
                
                if (0 < Integer.parseInt(choix_defi) && defis.size() >= Integer.parseInt(choix_defi)) {
                    System.out.println("Le nombre est ok ");
                    gestion.wait(2000);
                }
                    
                else {
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000);
                }

            } catch (Exception e) {
                System.out.println("ERREUR: Nombre invalide...");
                gestion.wait(2000);
            }

        }
    }

    private void defi_solo(Avatar user) {

        String theme = "";
        System.out.println("Défi solo");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        while (true) {
            System.out.println(String.format("Choisissez le thème:\nThèmes:\n- Tout\n%s", dbProcess.getThemes()));
            theme = scanner.nextLine().toLowerCase();
            if (dbProcess.isTheme(theme) || theme.equals("tout")) {
                if (theme.equals("tout"))
                    theme = "";
                break;
            }

            System.out.println(String.format("Le thème %s n'esiste pas", theme));
            gestion.wait(1000);

        }

        int nb_questions = Math.min(20, dbProcess.get_nb_questions(theme));

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("Tu devras répondre à une série de %s questions.", nb_questions));
        System.out.println("Les questions auquelles tu répondras correctement seront ajoutées dans ton cerveau !");
        System.out.println(
                "Ton score te permettra d'augmenter ton savoir ou d'en perdre si tu obtiens ou non la moyenne.");
        System.out.println(
                "Cependant, n'oublie pas que chaque question te fera gagner ou perdre du savoir ! Le résultat du défi ne sera qu'un bonus/malus sur ton savoir !");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Le jeu va commencer. Taper n'importe quoi pour débuter\nR - Annuler");

        if (scanner.nextLine().toUpperCase().equals("R")) {
            return;
        } else { // Le défi commence
            int score = 0;
            int currentPV = user.getPV();
            int pt_gagne = 0;
            int pt_perdu = 0;

            List<Question> questions = dbProcess.generate_question(nb_questions, theme);

            System.out.println("Défi solo");

            for (int i = 0; i < nb_questions; i++) {
                Question question = questions.get(i);

                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println(String.format("Question n°%d - %s", i + 1, question.getQuestion()));

                for (int num_choix = 0; num_choix < question.getChoices().size(); num_choix++) {
                    System.out.println((num_choix + 1) + " - " + question.getChoices().get(num_choix));
                }
                System.out.print("Réponse -> ");
                String resp = scanner.nextLine();

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

                    gestion.wait(2000);
                }
            }

            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
            int changement_val_savoir = score - (nb_questions / 2);
            dbProcess.updatePV(user.getId(), currentPV + changement_val_savoir);
            dbProcess.updateStats(user.getId(), nb_questions, score, "solo", pt_gagne, pt_perdu);

            System.out.println("Résultat du défi: " + score + "/" + nb_questions);
            if (score > (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score supérieur à la moyenne ! Voici un bonus de savoir: +"
                        + changement_val_savoir);
            } else if (score < (nb_questions / 2)) {
                System.out
                        .println(
                                "Tu as obtenu un score inférieur à la moyenne ! Tu écopes donc d'un malus de savoir: "
                                        + changement_val_savoir);
            } else {
                System.out.println("Tu as obtenu la moyenne ! Tu n'obtiens ni de bonus ni de malus de savoir...");
            }
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println("Taper n'importe quoi pour quitter");
            scanner.nextLine();

        }
    }

    private void defi(Avatar user) {

        String theme = "";
        System.out.println("Défi");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        String cible;
        int nb_jours = 0;
        while (true) {
            System.out.println("Qui veux-tu défier ???");
            cible = scanner.nextLine();
            if (!dbProcess.isUser(cible))
                System.out.println("ERREUR: Cet utilisateur n'existe pas.");
            else {
                System.out.println("Confirmer: o/n");

                if (scanner.nextLine().toLowerCase().equals("o")) {
                    break;
                }
            }
        }

        while (true) {
            System.out.println(String.format("Combien de jours laisses-tu %s pour répondre aux questions ???", cible));
            try {
                nb_jours = Integer.parseInt(scanner.nextLine());

                if (nb_jours <= 0)
                    System.out.println("ERREUR: Temps invalide.");
                else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("ERREUR: Nombre invalide.");
            }
        }

        while (true) {
            System.out.println(String.format("Choisissez le thème:\nThèmes:\n- Tout\n%s", dbProcess.getThemes()));
            theme = scanner.nextLine().toLowerCase();
            if (dbProcess.isTheme(theme) || theme.equals("tout")) {
                if (theme.equals("tout"))
                    theme = "";
                break;
            }

            System.out.println(String.format("Le thème %s n'esiste pas", theme));
            gestion.wait(1000);

        }

        int nb_questions = Math.min(20, dbProcess.get_nb_questions(theme));

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("Tu devras répondre à une série de %s questions.", nb_questions));
        System.out.println("Les questions auquelles tu répondras correctement seront ajoutées dans ton cerveau !");
        System.out.println(
                "Ton score te permettra d'augmenter ton savoir ou d'en perdre si tu obtiens ou non la moyenne.");
        System.out.println(
                "Cependant, n'oublie pas que chaque question te fera gagner ou perdre du savoir ! Le résultat du défi ne sera qu'un bonus/malus sur ton savoir !");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Le jeu va commencer. Taper n'importe quoi pour débuter\nR - Annuler");

        if (scanner.nextLine().toUpperCase().equals("R")) {
            return;
        } else { // Le défi commence
            int score = 0;
            int currentPV = user.getPV();
            int pt_gagne = 0;
            int pt_perdu = 0;

            List<Question> questions = dbProcess.generate_question(nb_questions, theme);

            System.out.println("Défi solo");

            for (int i = 0; i < nb_questions; i++) {
                Question question = questions.get(i);

                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println(String.format("Question n°%d - %s", i + 1, question.getQuestion()));

                for (int num_choix = 0; num_choix < question.getChoices().size(); num_choix++) {
                    System.out.println((num_choix + 1) + " - " + question.getChoices().get(num_choix));
                }
                System.out.print("Réponse -> ");
                String resp = scanner.nextLine();

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

                    gestion.wait(2000);
                }
            }

            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
            int changement_val_savoir = score - (nb_questions / 2);
            dbProcess.updatePV(user.getId(), currentPV + changement_val_savoir);
            dbProcess.updateStats(user.getId(), nb_questions, score, "vs", pt_gagne, pt_perdu);

            Avatar cible_avatar = dbProcess.getUserByName(cible);
            int pts = pt_gagne - pt_perdu;
            if (pts < 0)
                pts = 0;

            dbProcess.addQuestionDuel(questions, user.getId(), cible_avatar.getId(), pts, nb_jours);

            System.out.println("Résultat du défi: " + score + "/" + nb_questions);
            if (score > (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score supérieur à la moyenne ! Voici un bonus de savoir: +"
                        + changement_val_savoir);
            } else if (score < (nb_questions / 2)) {
                System.out
                        .println(
                                "Tu as obtenu un score inférieur à la moyenne ! Tu écopes donc d'un malus de savoir: "
                                        + changement_val_savoir);
            } else {
                System.out.println("Tu as obtenu la moyenne ! Tu n'obtiens ni de bonus ni de malus de savoir...");
            }
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println(String.format("%s va recevoir une notification du combat.", cible));

            System.out.println("Taper n'importe quoi pour quitter");
            scanner.nextLine();

        }
    }

}
