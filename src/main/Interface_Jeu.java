package main;
import java.util.Scanner;

import main.process.SysGestion;

import java.util.List;

public class Interface_Jeu {

    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;

    protected DBProcess dbProcess;

    protected Interface_Defis interface_Defis; // Interface du menu principal

    public Interface_Jeu() {
        dbProcess = new DBProcess(); // Initialize dbProcess here
        interface_Defis = new Interface_Defis(); //
    }

    public void menu_jouer(String user_id) {

        while (true) {
            Avatar user = dbProcess.getUserById(user_id);
            gestion.clear();

            System.out.println("Modes de jeu");
            System.out.println("----------------");
            System.out.println("S - Commencer un défi solo");
            System.out.println("D - Défier un jouer");
            System.out.println("F - Défis");

            System.out.println("R - Retour");
            switch (scanner.nextLine().toUpperCase()) {
                case "S":
                    defi_solo(user);
                    break;

                case "D":
                    defi(user);
                    break;

                case "F":
                interface_Defis.menu_Defis(user_id);
                    break;

                case "R":
                    System.out.println("Retour au menu principal.");
                    gestion.wait(2000);
                    return;

                default:
                    System.out.println("Commande saisie invalide...");
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
