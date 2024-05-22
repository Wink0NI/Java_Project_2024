import process.SysGestion;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.util.InputMismatchException;

public class Interface_Menu_Parametres {
    SysGestion gestion = new SysGestion();
    DBProcess dbProcess = new DBProcess();

    Scanner scanner = new Scanner(System.in);

    public void menu_parametres(String user_id) {
        while (true) {
            Avatar user = dbProcess.getUserById(user_id);

            gestion.clear();
            System.out.println(String.format("Menu parmètres. Connecté en tant que %s", user.getName()));
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            System.out.println("P - Voir le profil");
            System.out.println("R - Voir le profil d'une personne");

            if (dbProcess.isAdmin(user.getName())) {
                System.out.println("O - Ajouter une question");
            } else {
                System.out.println("O - Proposer une question");
            }

            System.out.println("I - Statut des questions...");

            System.out.println("N - Retour");
            switch (scanner.nextLine().toUpperCase()) {
                case "P":
                    afficher_statistiques(user);
                    break;

                case "R":
                    voir_profil();
                    break;

                case "O":
                    ajouter_question(user);
                    break;

                case "I":
                    afficher_questions_attente(user);
                    break;
                case "N":
                    System.out.println(String.format("Au revoir %s !!!", user.getName()));
                    gestion.wait(2000);
                    return;

                default:
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000);

            }
        }

    }

    private void afficher_statistiques(Avatar user) {
        try {
            ResultSet stats = dbProcess.getStat(user.getId());

            gestion.clear();
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("Statistiques");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            System.out.println(String.format("id: %s", user.getId()));
            System.out.println(String.format("Nom: %s", user.getName()));
            System.out.println(String.format("Points: %s", user.getPV()));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("Statistiques de parties");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println(String.format("Nombre de défi solo joué: %d", stats.getInt("defi_solo")));
            System.out.println(
                    String.format("Questions répondus en défi solo: %d", stats.getInt("tot_question_defi_solo")));
            System.out.println(
                    String.format("Questions répondus juste en défi solo: %d", stats.getInt("jus_question_defi_solo")));
            System.out.println(String.format("Moyenne des questions répondus juste en défi solo: %.2f",
                    stats.getInt("tot_question_defi_solo") == 0 ? 0
                            : (double) (stats.getInt("jus_question_defi_solo"))
                                    / (stats.getInt("tot_question_defi_solo"))
                                    * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés en défi solo: %d", stats.getInt("pt_gagne_defi_solo")));
            System.out.println(String.format("Points perdus en défi solo: %d", stats.getInt("pt_perdu_defi_solo")));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            System.out.println(String.format("Nombre de défi vs joué: %d", stats.getInt("defi_vs")));
            System.out.println(
                    String.format("Questions répondus en défi vs: %d", stats.getInt("tot_question_defi_vs")));
            System.out.println(
                    String.format("Questions répondus juste en défi vs: %d", stats.getInt("jus_question_defi_vs")));
            System.out.println(String.format("Moyenne des questions répondus juste en défi vs: %.2f",
                    stats.getInt("tot_question_defi_vs") == 0 ? 0
                            : (double) (stats.getInt("jus_question_defi_vs")) / (stats.getInt("tot_question_defi_vs"))
                                    * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés en défi vs: %d", stats.getInt("pt_gagne_defi_vs")));
            System.out.println(String.format("Points perdus en défi vs: %d", stats.getInt("pt_perdu_defi_vs")));

            System.out.println(String.format("Matchs réalisés en défi vs: %d", stats.getInt("match_vs")));
            System.out
                    .println(String.format("Victoires de match réalisés en défi vs: %d", stats.getInt("victoire_vs")));
            System.out.println(String.format("Pourcentage de victoire en match vs: %.2f",
                    stats.getInt("match_vs") == 0 ? 0
                            : (double) (stats.getInt("victoire_vs")) / (stats.getInt("match_vs"))
                                    * 100.0)
                    + "%");

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            System.out.println(
                    String.format("Nombre de défi effectués: %d", stats.getInt("defi_solo") + stats.getInt("defi_vs")));
            System.out.println(
                    String.format("Questions répondus: %d",
                            stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs")));
            System.out.println(
                    String.format("Questions répondus juste: %d",
                            stats.getInt("jus_question_defi_solo") + stats.getInt("jus_question_defi_vs")));
            System.out.println(String.format("Moyenne des questions dépondus juste: %.2f",
                    stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs") == 0 ? 0
                            : (double) (stats.getInt("jus_question_defi_solo") + stats.getInt("jus_question_defi_vs"))
                                    / (stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs"))
                                    * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés: %d",
                    stats.getInt("pt_gagne_defi_solo") + stats.getInt("pt_gagne_defi_vs")));
            System.out.println(String.format("Points perdus: %d",
                    stats.getInt("pt_perdu_defi_solo") + stats.getInt("pt_perdu_defi_vs")));

            stats.close();

            System.out.println();
            System.out.println("Taper sur la touche entrée pour quitter la page statistique.");
            scanner.nextLine();

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des statistiques.");
            System.out.println(e);
            gestion.wait(2000);

        }

    }

    public void voir_profil() {
        System.out.println("Quel utilisateur voulez-vous voir ?");
        Avatar user = dbProcess.getUserByName(scanner.nextLine());

        if (user != null) {
            afficher_statistiques(user);
        } else {
            System.out.println("L'utilisateur demandé n'existe pas...");
            gestion.wait(2000);
        }
    }

    private void ajouter_question(Avatar user) {

        System.out.println("Taper la question: ");
        String question = scanner.nextLine();

        System.out.println("Thème: ");
        String theme_question = scanner.nextLine();

        int nb = 2;

        while (true) {
            System.out.println("Nombre de réponses: ");
            try {
                nb = Integer.parseInt(scanner.nextLine());

                if (nb < 2) {
                    System.out.println("Nombre invalide: Valeur insuffisante - 2 choix minimum.");
                } else if (nb > 4) {
                    System.out.println("Nombre invalide: 4 choix maximum.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("ERREUR: Le nombre entré n'est pas un nombre.");
                nb = 0;
            }
        }

        List<String> Options_reponses = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            System.out.println(String.format("Choix %d", i + 1));
            String choix = scanner.nextLine();
            while (Options_reponses.contains(choix)) {
                System.out.println("Hop pop pop , vous avez deja ajouté ce choix dans votre liste ! ");
                choix = scanner.nextLine();
            }
            Options_reponses.add(choix);
        }
        System.out.println("Réponse: ");
        String reponse = scanner.nextLine();
        while (!Options_reponses.contains(reponse)) {
            System.out.println("Hop pop pop , vous n'avez pas mis ca comme reponses dans votre liste ! ");
            reponse = scanner.nextLine();
        }

        int nb_point = 2;

        while (true) {
            System.out.println("Nombre de points gagnés: ");
            try {
                nb_point = Integer.parseInt(scanner.nextLine());

                if (nb_point < 0) {
                    System.out.println("Nombre invalide: Valeur insuffisante - 0 point minimum.");
                } else if (nb_point > 50) {
                    System.out.println("Nombre invalide: 50 points maximum.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("ERREUR: Le nombre entré n'est pas un nombre.");
                nb = 0;
            }
        }

        if (dbProcess.isAdmin(user.getName())) {
            dbProcess.addQuestion(new Question(question, Options_reponses, reponse, nb_point, theme_question));
            System.out.println("SUCCES: Question ajouté avec succès.");
        } else {
            dbProcess.addQuestionRequest(new Question(question, Options_reponses, reponse, nb_point, theme_question),
                    user.getId());
            System.out.println(
                    "SUCCES: Question enregistré avec succès. Un administrateur jettera un coup d'oeil sur votre question.");
        }

        gestion.wait(5000);

    }

    public void afficher_questions_attente(Avatar user) {

        List<Question> listeAttente = dbProcess.get_question_user(user.getId());

        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");

        System.out.println("Liste de vos questions en attente..");

        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");

        if (listeAttente.size() == 0) {
            System.out.println("Liste de questions en attente vide...");
        } else {
            for (Question q : listeAttente) {
                System.out.println(
                        String.format(
                                "- Question %s (Score: %d)", q.getQuestion(),
                                q.getPoints()));
            }

        }
        System.out.println("Cliquez sur Entrée pour revenir dans les paramètres...");
        scanner.nextLine();

    }
}
