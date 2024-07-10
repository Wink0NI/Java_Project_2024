package main;
import java.util.InputMismatchException;
import java.util.Scanner;
import main.process.SysGestion;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Classe représentant l'interface de menu pour les administrateurs.
 * Fournit des méthodes pour gérer les questions, les administrateurs et voir les questions proposées.
 */
public class Interface_Menu_Administrateur {
    // Instance de Scanner pour lire les entrées de l'utilisateur
    private Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;
    private DBProcess dbProcess;

    /**
     * Constructeur de la classe Interface_Menu_Administrateur.
     * Initialise la variable dbProcess avec une nouvelle instance de DBProcess pour la gestion des données.
     */
    public Interface_Menu_Administrateur() {
        dbProcess = new DBProcess();
    }

    /**
     * Méthode principale du menu administrateur.
     * Affiche les options disponibles pour l'administrateur et appelle les méthodes appropriées en fonction du choix de l'utilisateur.
     *
     * @param user_id l'identifiant de l'utilisateur connecté
     */
    public void menu_administrateur(String user_id) {
        // Boucle infinie pour afficher le menu jusqu'à ce que l'utilisateur choisisse de revenir au menu principal
        while (true) {
            // Récupération des informations de l'utilisateur actuel via son ID
            Avatar user = dbProcess.getUserById(user_id);

            // Efface l'écran (ou simule un écran propre) pour le menu
            gestion.clear();
            // Affiche le message de bienvenue avec le nom de l'utilisateur
            System.out.println(String.format("Menu administrateur. Connecté en tant que %s", user.getName()));
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.println("A - Ajouter une question.");
            System.out.println("S - Retirer une question.");
            System.out.println("M - Ajouter un nouvel administrateur.");
            System.out.println("R - Retirer un administrateur.");
            System.out.println("U - Liste des questions proposées.");
            System.out.println("B - Retour au menu principal");

            // Lecture du choix de l'utilisateur
            switch (scanner.nextLine().toUpperCase()) {
                case "A":
                    // Appelle la méthode pour ajouter une nouvelle question
                    ajouter_question();
                    break;
                case "S":
                    // Appelle la méthode pour retirer une question existante
                    retirer_question();
                    break;
                case "M":
                    // Appelle la méthode pour ajouter un nouvel administrateur
                    ajouter_Administrateur(user);
                    break;
                case "R":
                    // Appelle la méthode pour retirer un administrateur existant
                    retirer_Administrateur(user);
                    break;
                case "U":
                    // Appelle la méthode pour voir les questions en attente
                    voir_question_proposer(user);
                    break;
                case "B":
                    // Affiche un message de retour et sort du menu pour revenir au menu principal
                    System.out.println("Retour au menu principal...");
                    gestion.wait(2000); // Attend 2 secondes avant de revenir au menu principal
                    return;
                default:
                    // Gère les commandes invalides
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000); // Attend 2 secondes avant de réafficher le menu
            }
        }
    }

    /**
     * Méthode qui permet d'ajouter une question.
     * Permet d'ajouter une question soit manuellement, soit en important depuis un fichier CSV.
     */
    private void ajouter_question() {
        System.out.println("Comment voulez-vous rajouter les questions ?");
        System.out.println("A - À la main\nB - Par fichier texte/csv");

        switch (scanner.nextLine().toUpperCase()) {
            case "A":
                // Ajout de question manuellement
                System.out.println("Tapez la question: ");
                String question = scanner.nextLine();

                System.out.println("Thème: ");
                String theme_question = scanner.nextLine();

                int nb = 2; // Nombre initial de réponses
                while (true) {
                    System.out.println("Nombre de réponses: ");
                    try {
                        nb = Integer.parseInt(scanner.nextLine()); // Lecture du nombre de réponses
                        if (nb < 2) {
                            // Le nombre de réponses est trop faible
                            System.out.println("Nombre invalide: Valeur insuffisante - 2 choix minimum.");
                        } else if (nb > 4) {
                            // Le nombre de réponses est trop élevé
                            System.out.println("Nombre invalide: 4 choix maximum.");
                        } else {
                            // Le nombre de réponses est valide
                            break;
                        }
                    } catch (Exception e) {
                        // Erreur lors de la conversion du nombre
                        System.out.println("ERREUR: Le nombre entré n'est pas un nombre.");
                        nb = 0;
                    }
                }

                // Création de la liste des options de réponses
                List<String> Options_reponses = new ArrayList<>();
                for (int i = 0; i < nb; i++) {
                    System.out.println(String.format("Choix %d", i + 1));
                    String choix = scanner.nextLine();
                    while (Options_reponses.contains(choix)) {
                        // Vérifie que le choix n'est pas déjà dans la liste
                        System.out.println("Hop pop pop, vous avez déjà ajouté ce choix dans votre liste !");
                        choix = scanner.nextLine();
                    }
                    Options_reponses.add(choix); // Ajoute le choix à la liste des options
                }

                // Saisie de la réponse correcte
                System.out.println("Réponse: ");
                String reponse = scanner.nextLine();
                while (!Options_reponses.contains(reponse)) {
                    // Vérifie que la réponse est bien dans la liste des options
                    System.out.println("Hop pop pop, vous n'avez pas mis ça comme réponse dans votre liste !");
                    reponse = scanner.nextLine();
                }

                // Saisie du nombre de points associés à la question
                int nb_point = 2;
                while (true) {
                    System.out.println("Nombre de points gagnés: ");
                    try {
                        nb_point = Integer.parseInt(scanner.nextLine()); // Lecture du nombre de points
                        if (nb_point < 0) {
                            // Le nombre de points est trop faible
                            System.out.println("Nombre invalide: Valeur insuffisante - 0 point minimum.");
                        } else if (nb_point > 50) {
                            // Le nombre de points est trop élevé
                            System.out.println("Nombre invalide: 50 points maximum.");
                        } else {
                            // Le nombre de points est valide
                            break;
                        }
                    } catch (Exception e) {
                        // Erreur lors de la conversion du nombre
                        System.out.println("ERREUR: Le nombre entré n'est pas un nombre.");
                        nb = 0;
                    }
                }

                // Ajout de la nouvelle question à la base de données
                dbProcess.addQuestion(new Question(question, Options_reponses, reponse, nb_point, theme_question));
                System.out.println("SUCCÈS: Question ajoutée avec succès.");
                break;

            case "B":
                // Ajout de questions via un fichier CSV
                System.out.println("Nom du fichier (à mettre dans le dépôt): ");
                String name_file = "src/depot/" + scanner.nextLine() + ".csv";
                try {
                    Scanner reader = new Scanner(new File(name_file)); // Ouvre le fichier CSV
                    int ligne_exc = 1; // Compteur de lignes pour les messages d'erreur
                    List<Question> questions = new ArrayList<Question>(); // Liste pour stocker les questions importées

                    while (reader.hasNextLine()) {
                        // Lit chaque ligne du fichier CSV
                        String[] ligne = reader.nextLine().split(";");
                        if (ligne[0].isEmpty()) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Réponse vide.");
                        if (ligne[1].isEmpty()) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Score vide.");
                        if (ligne[6].isEmpty()) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Réponse vide.");
                        if (ligne[7].isEmpty()) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Thème vide.");
                        if (ligne.length < 8) throw new StringIndexOutOfBoundsException("ERREUR: Ligne " + ligne_exc + " - Valeurs manquantes.");

                        String qn = ligne[0]; // Question
                        int score = Integer.parseInt(ligne[1]); // Score de la question
                        if (score <= 0) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Valeur de score trop petite.");
                        if (score > 50) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Valeur de score trop grande.");

                        // Création de la liste des options de réponses à partir du fichier CSV
                        List<String> choices = new ArrayList<String>();
                        for (int i = 2; i <= 5; i++) {
                            if (!ligne[i].isEmpty()) choices.add(ligne[i]);
                        }
                        if (choices.size() < 2) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Nombre de choix insuffisant.");

                        String response = ligne[6]; // Réponse correcte
                        if (!choices.contains(response)) throw new IllegalArgumentException("ERREUR: Ligne " + ligne_exc + " - Réponse différente parmi les choix données.");

                        String theme = ligne[7]; // Thème de la question

                        // Ajout de la question importée à la liste des questions
                        questions.add(new Question(qn, choices, response, score, theme));
                        ligne_exc++;
                    }

                    // Ajout de toutes les questions à la base de données
                    dbProcess.addQuestions(questions);
                    System.out.println("SUCCÈS: Questions ajoutées avec succès.");
                } catch (FileNotFoundException e) {
                    // Erreur si le fichier n'est pas trouvé
                    System.out.println("ERREUR: Fichier " + name_file + " non trouvé.");
                } catch (StringIndexOutOfBoundsException e) {
                    // Erreur si des valeurs sont manquantes dans le fichier CSV
                    System.out.println(e.getMessage());
                } catch (NumberFormatException e) {
                    // Erreur si le score n'est pas un nombre valide
                    System.out.println("Erreur: Le nombre donné n'est pas un chiffre.");
                } catch (IllegalArgumentException e) {
                    // Erreur si des validations échouent
                    System.out.println(e.getMessage());
                }
                break;

            default:
                // Choix invalide pour le mode d'ajout de la question
                System.out.println("ERREUR: Choix invalide.");
        }
        gestion.wait(2000); // Attend 2 secondes avant de revenir au menu
    }

    /**
     * Méthode qui permet de retirer une question.
     * Demande à l'utilisateur de saisir la question à retirer et vérifie si elle existe.
     */
    private void retirer_question() {
        System.out.println("Taper la question: ");
        String question = scanner.nextLine(); // Lecture de la question à retirer
        if (dbProcess.isQuestion(question)) {
            // Vérifie si la question existe dans la base de données
            dbProcess.removeQuestion(question); // Retire la question de la base de données
            System.out.println("SUCCÈS: Question retirée avec succès.");
        } else {
            // La question n'existe pas dans la base de données
            System.out.println("ERREUR: La question n'existe pas.");
        }
        gestion.wait(2000); // Attend 2 secondes avant de revenir au menu
    }

    /**
     * Méthode qui permet d'ajouter un nouvel administrateur.
     * Demande le nom de l'utilisateur à ajouter comme administrateur après avoir vérifié son existence.
     *
     * @param user l'utilisateur actuellement connecté
     */
    private void ajouter_Administrateur(Avatar user) {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Ajouter un utilisateur.\nEntrez un nom: ");
        String admin = scanner.nextLine(); // Lecture du nom de l'utilisateur à ajouter en tant qu'administrateur

        if (!dbProcess.isUser(admin)) {
            // Vérifie si l'utilisateur existe
            System.out.println(String.format("ERREUR: L'utilisateur %s n'existe pas.", admin));
        } else if (admin.equals(user.getName())) {
            // Vérifie que l'utilisateur ne tente pas de se rajouter lui-même
            System.out.println("AVERTISSEMENT: Vous êtes déjà administrateur.");
        } else if (dbProcess.isAdmin(admin)) {
            // Vérifie si l'utilisateur est déjà administrateur
            System.out.println(String.format("AVERTISSEMENT: %s est déjà administrateur.", admin));
        } else {
            // Ajoute l'utilisateur comme administrateur
            dbProcess.addAdmin(admin);
            System.out.println(String.format("SUCCÈS: %s ajouté dans la liste des administrateurs.", admin));
        }
        gestion.wait(2000); // Attend 2 secondes avant de revenir au menu
    }

    /**
     * Méthode qui permet de retirer un administrateur.
     * Demande le nom de l'administrateur à retirer après avoir vérifié son existence.
     *
     * @param user l'utilisateur actuellement connecté
     */
    private void retirer_Administrateur(Avatar user) {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Retirer un utilisateur.\nEntrez un nom: ");
        String admin = scanner.nextLine(); // Lecture du nom de l'administrateur à retirer

        if (!dbProcess.isUser(admin)) {
            // Vérifie si l'utilisateur existe
            System.out.println(String.format("ERREUR: L'utilisateur %s n'existe pas.", admin));
        } else if (admin.equals(user.getName())) {
            // Vérifie que l'utilisateur ne tente pas de se retirer lui-même
            System.out.println("AVERTISSEMENT: Vous ne pouvez pas vous retirer de la liste des administrateurs.");
        } else if (!dbProcess.isAdmin(admin)) {
            // Vérifie si l'utilisateur est déjà administrateur
            System.out.println(String.format("AVERTISSEMENT: %s n'est pas administrateur.", admin));
        } else {
            // Retire l'utilisateur de la liste des administrateurs
            dbProcess.removeAdmin(admin);
            System.out.println(String.format("SUCCÈS: %s retiré de la liste des administrateurs.", admin));
        }
        gestion.wait(2000); // Attend 2 secondes avant de revenir au menu
    }

    /**
     * Méthode qui permet de voir les questions proposées.
     * Affiche la liste des questions en attente et permet de les accepter, les refuser ou quitter le processus.
     *
     * @param user l'utilisateur actuellement connecté
     */
    private void voir_question_proposer(Avatar user) {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Liste des questions proposées...");
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        List<Question> questions = dbProcess.get_question_attente(); // Récupère la liste des questions en attente

        if (questions.size() == 0) {
            // Aucun question en attente
            System.out.println("Aucune question en attente...");
        } else {
            // Affiche chaque question en attente
            for (int i = 0; i < questions.size(); i++) {
                Avatar userDemande = dbProcess.getUserById(questions.get(i).getUserId()); // Récupère l'utilisateur qui a soumis la question

                // Affiche les détails de la question
                System.out.println(String.format("%d - %s (Score: %d) par %s", i + 1, questions.get(i).getQuestion(), questions.get(i).getPoints(), userDemande.getName()));
                System.out.println(String.format("Thème: %s", questions.get(i).getTheme()));
                System.out.println(String.format("Nombre de choix: %d", questions.get(i).getChoices().size()));
                for (int choix = 0; choix < questions.get(i).getChoices().size(); choix++) {
                    System.out.println(String.format("Choix %d: %s", choix + 1, questions.get(i).getChoices().get(choix)));
                }
                System.out.println(String.format("Réponse: %s", questions.get(i).getResponse()));
                System.out.println("----------------------------------------------------------------------------------------------------------------------");
                System.out.println("Accepter la question ? o/n/q (quitter)");

                boolean answer = false;
                while (!answer) {
                    // Lire la réponse de l'administrateur pour accepter, refuser ou quitter
                    String input = scanner.nextLine().toLowerCase();
                    switch (input) {
                        case "o":
                            // Accepter la question
                            System.out.println("Question acceptée.");
                            dbProcess.addQuestion(questions.get(i)); // Ajoute la question à la base de données
                            dbProcess.updateQuestionRequestStatus(questions.get(i).getId(), "ACCEPTED"); // Met à jour le statut de la question
                            answer = true;
                            break;
                        case "n":
                            // Refuser la question
                            System.out.println("Question refusée.");
                            dbProcess.updateQuestionRequestStatus(questions.get(i).getId(), "REFUSED"); // Met à jour le statut de la question
                            answer = true;
                            break;
                        case "q":
                            // Quitter le processus de gestion des questions
                            System.out.println("Retour au menu administrateur...");
                            return;
                        default:
                            // Entrée invalide, redemander
                            System.out.println("Entrée invalide o/n/q.");
                    }
                }
            }
        }
        gestion.wait(3000); // Attend 3 secondes avant de revenir au menu
    }
}
