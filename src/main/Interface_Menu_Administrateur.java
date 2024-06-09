package main;
import java.util.InputMismatchException;

import java.util.Scanner;

import main.process.SysGestion;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;

public class Interface_Menu_Administrateur {
    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;
    private DBProcess dbProcess;

    public Interface_Menu_Administrateur() {
        dbProcess = new DBProcess();
    }

    public void menu_administrateur(String user_id) {

        while (true) {
            Avatar user = dbProcess.getUserById(user_id);

            gestion.clear();
            System.out.println(String.format("Menu administrateur. Connecté en tant que %s", user.getName()));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("A - Ajouter une questions.");
            System.out.println("S - Retirer une questions.");
            System.out.println("M - Ajouter un nouvel administrateur.");
            System.out.println("R - Retirer un administrateur.");
            System.out.println("U - Liste des questions proposés.");

            System.out.println("B - Retour au menu principal");

            switch (scanner.nextLine().toUpperCase()) {
                case "A":
                    ajouter_question();
                    break;
                case "S":
                    retirer_question();
                    break;

                case "M":
                    ajouter_Administrateur(user);
                    break;

                case "R":
                    retirer_Administrateur(user);
                    break;

                case "U":
                    voir_question_proposer(user);
                    break;

                case "B":
                    System.out.println(String.format("Retour au menu principal..."));
                    gestion.wait(2000);
                    return;

                default:
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000);

            }
        }
    }

    private void ajouter_question() {
        System.out.println("Comment voulez-vous rajouter les questions ?");
        System.out.println("A - A la Main\nB - Par fichier texte/csv");

        switch (scanner.nextLine().toUpperCase()) {
            case "A":
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

                dbProcess.addQuestion(new Question(question, Options_reponses, reponse, nb_point, theme_question));
                System.out.println("SUCCES: Question ajouté avec succès.");

                break;

            case "B":
                System.out.println("Nom du fichier (A mettre dans le dépôt): ");
                String name_file = "src/depot/" + scanner.nextLine() + ".csv";

                try {
                    Scanner reader = new Scanner(new File(name_file));

                    int ligne_exc = 1;
                    List<Question> questions = new ArrayList<Question>();
                    while (reader.hasNextLine()) {

                        String[] ligne = reader.nextLine().split(";");

                        if (ligne[0] == "")
                            throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Réponse vide.");
                        if (ligne[1] == "")
                            throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Score vide.");
                        if (ligne[6] == "")
                            throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Réponse vide.");
                        if (ligne[7] == "")
                            throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Thème vide.");

                        if (ligne.length < 8)
                            throw new StringIndexOutOfBoundsException(
                                    "ERREUR: Ligne " + ligne_exc + " - Valeurs manquantes.");

                        String qn = ligne[0];

                        int score = Integer.parseInt(ligne[1]);
                        if (score <= 0)
                            throw new IllegalArgumentException(
                                    "ERREUR: Ligne " + ligne_exc + " - Valeur de score trop petite.");
                        if (score > 50)
                            throw new IllegalArgumentException(
                                    "ERREUR: Ligne " + ligne_exc + " - Valeur de score trop grande.");

                        List<String> choices = new ArrayList<String>();
                        for (int i = 2; i <= 5; i++) {
                            if (ligne[i] != "")
                                choices.add(ligne[i]);
                        }
                        if (choices.size() < 2)
                            throw new IllegalArgumentException(
                                    "ERREUR: Ligne " + ligne_exc + " - Nombre de choix insuffisante.");

                        String response = ligne[6];
                        if (!choices.contains(response))
                            throw new IllegalArgumentException(
                                    "ERREUR: Ligne " + ligne_exc + " - Réponse différente parmi les choix données.");

                        String theme = ligne[7];

                        questions.add(new Question(qn, choices, response, score, theme));
                        ligne_exc++;

                    }
                    dbProcess.addQuestions(questions);

                    System.out.println("SUCCES: Questions ajoutées avec succès.");

                } catch (FileNotFoundException e) {
                    System.out.println("ERREUR: Fichier " + name_file + " non trouvé.");
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Erreur: Le nombre donné n'est pas un chiffre.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
                break;

            default:
                System.out.println("ERREUR: Choix invalide.");

        }
        gestion.wait(2000);

    }

    private void retirer_question() {

        System.out.println("Taper la question: ");

        String question = scanner.nextLine();
        if (dbProcess.isQuestion(question)) {
            dbProcess.removeQuestion(question);
            System.out.println("SUCCES: Question retirée avec succès.");
        } else {
            System.out.println("ERREUR: La question n'existe pas.");
        }

        gestion.wait(2000);
    }

    private void ajouter_Administrateur(Avatar user) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Ajouter un utilisateur.\nEntrez un nom: ");
        String admin = scanner.nextLine();

        if (!dbProcess.isUser(admin)) {
            System.out.println(String.format("ERROR: L'utilisateur %s n'existe pas.", admin));
        } else if (admin == user.getName()) {
            System.out.println("WARNING: Vous êtes déjà admninistrateur.");
        } else if (dbProcess.isAdmin(admin)) {
            System.out.println(String.format("WARNING: %s est déjà administrateur.", admin));
        } else {
            dbProcess.addAdmin(admin);
            System.out.println(String.format("SUCCES: %s ajouté dans la liste des administrateurs.", admin));
        }
        gestion.wait(2000);
    }

    private void retirer_Administrateur(Avatar user) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Retirer un utilisateur.\nEntrez un nom: ");
        String admin = scanner.nextLine();

        if (!dbProcess.isUser(admin)) {
            System.out.println(String.format("ERROR: L'utilisateur %s n'existe pas.", admin));
        } else if (admin == user.getName()) {
            System.out.println("WARNING: Vous pouvez pas vous retirer de la liste des administrateurs.");
        } else if (!dbProcess.isAdmin(admin)) {
            System.out.println(String.format("WARNING: %s n'est pas administrateur.", admin));
        } else {
            dbProcess.removeAdmin(admin);
            System.out.println(String.format("SUCCES: %s retiré de la liste des administrateurs.", admin));
        }
        gestion.wait(2000);
    }

    private void voir_question_proposer(Avatar user) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Liste des questions proposés...");
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        List<Question> questions = dbProcess.get_question_attente();

        if (questions.size() == 0) {
            System.out.println("Aucune question en attente...");
        } else {
            for (int i = 0; i < questions.size(); i++) {
                Avatar userDemande = dbProcess.getUserById(questions.get(i).getUserId());

                System.out.println(
                        String.format(
                                "%d - %s (Score: %d) par %s",
                                i + 1, questions.get(i).getQuestion(), questions.get(i).getPoints(),
                                userDemande.getName()));
                System.out.println(
                        String.format(
                                "Theme: %s",
                                questions.get(i).getTheme()));
                System.out.println(
                        String.format(
                                "Nombre de choix: %d",
                                questions.get(i).getChoices().size()));
                for (int choix = 0; choix < questions.get(i).getChoices().size(); choix++) {
                    System.out.println(
                            String.format(
                                    "Choix %d: %s",
                                    choix + 1, questions.get(i).getChoices().get(choix)));

                    

                }
                System.out.println(
                            String.format(
                                    "Réponse: %s",
                                    questions.get(i).getResponse()));

                System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
                System.out.println("Accepter la question ?? o/n/q (quitter)");
                boolean answer = false;

                while (!answer) {
                    String input = scanner.nextLine().toLowerCase();
                    
                    
                    switch (input) {
                        case "o":
                            // Proceed with accepting the question
                            System.out.println("Question accepté.");
                            dbProcess.addQuestion(questions.get(i));
                            dbProcess.updateQuestionRequestStatus(questions.get(i).getId(), "ACCEPTED");
                            answer = true;
                            break; // Exit the loop
                        case "n":
                            // Decline the question
                            System.out.println("Question refusé.");
                            dbProcess.updateQuestionRequestStatus(questions.get(i).getId(), "REFUSED");
                            answer = true;
                            break; // Exit the loop
                        case "q":
                            // Quit the process
                            System.out.println("Retour au menu administrateur...");
                            return; // Exit the loop
                        default:
                            // Invalid input, ask again
                            System.out.println("Entrée invalide o/n/q.");
                    }
                }
                

            }
        }

        gestion.wait(3000);
    }

}
