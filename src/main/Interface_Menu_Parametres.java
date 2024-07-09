package main;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

/**
 * Classe représentant l'interface de menu pour les paramètres.
 * Ce menu permet à un utilisateur de voir son profil, proposer des questions, afficher les questions en attente,
 * changer son nom, et accéder au classement des joueurs.
 */
public class Interface_Menu_Parametres {
    // Instance de SysGestion pour la gestion des tâches (comme le nettoyage de l'écran et l'attente)
    SysGestion gestion = new SysGestion();

    // Instance de DBProcess pour l'accès aux données de la base de données
    DBProcess dbProcess = new DBProcess();

    // Instance de Interface_Menu_Classements pour accéder aux options de classement
    Interface_Menu_Classements classements = new Interface_Menu_Classements();

    // Scanner pour lire les entrées de l'utilisateur
    Scanner scanner = new Scanner(System.in);

    /**
     * Méthode qui affiche le menu des paramètres.
     * Ce menu offre diverses options telles que voir le profil, proposer une question, voir les questions en attente,
     * consulter les classements et changer le nom d'utilisateur.
     *
     * @param user_id l'identifiant de l'utilisateur connecté
     */
    public void menu_parametres(String user_id) {
        while (true) {
            // Récupération des informations de l'utilisateur à partir de son identifiant
            Avatar user = dbProcess.getUserById(user_id);

            // Nettoyage de l'écran et affichage du menu des paramètres
            gestion.clear();
            System.out.println(String.format("Menu paramètres. Connecté en tant que %s", user.getName()));
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            // Affichage des options disponibles dans le menu des paramètres
            System.out.println("P - Voir le profil");
            System.out.println("R - Voir le profil d'une personne");

            if (dbProcess.isAdmin(user.getName())) {
                // Si l'utilisateur est un administrateur, il peut ajouter une question
                System.out.println("O - Ajouter une question");
            } else {
                // Si l'utilisateur n'est pas administrateur, il peut proposer une question
                System.out.println("O - Proposer une question");
            }

            // Options supplémentaires disponibles pour tous les utilisateurs
            System.out.println("U - Statut des questions...");
            System.out.println("C - Classements...");
            System.out.println("N - Changer le nom de l'utilisateur...");
            System.out.println("T - Retour");

            // Lecture du choix de l'utilisateur et appel de la méthode appropriée
            switch (scanner.nextLine().toUpperCase()) {
                case "P":
                    // Affiche les statistiques de l'utilisateur connecté
                    afficher_statistiques(user);
                    break;

                case "R":
                    // Permet de voir le profil d'un autre utilisateur
                    voir_profil();
                    break;

                case "O":
                    // Permet d'ajouter ou de proposer une question en fonction du rôle de l'utilisateur
                    ajouter_question(user);
                    break;

                case "U":
                    // Affiche les questions en attente d'approbation
                    afficher_questions_attente(user);
                    break;

                case "C":
                    // Accède au menu des classements
                    classements.menu_classements(user_id);
                    break;

                case "T":
                    // Affiche un message d'adieu et retourne au menu principal
                    System.out.println(String.format("Au revoir %s !!!", user.getName()));
                    gestion.wait(2000); // Attendre 2 secondes avant de quitter
                    return;

                case "N":
                    // Permet de changer le nom d'utilisateur
                    changer_utilisateur(user);
                    break;

                default:
                    // Gestion d'une commande invalide
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000); // Attendre 2 secondes avant de redemander une commande
            }
        }
    }

    /**
     * Méthode qui affiche les statistiques de l'utilisateur.
     * Cette méthode récupère les statistiques de l'utilisateur à partir de la base de données et les affiche.
     *
     * @param user l'utilisateur dont les statistiques sont affichées
     */
    private void afficher_statistiques(Avatar user) {
        try {
            // Récupération des statistiques de l'utilisateur
            ResultSet stats = dbProcess.getStat(user.getId());

            // Nettoyage de l'écran et affichage des statistiques
            gestion.clear();
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("Statistiques");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            // Affichage des informations de base de l'utilisateur
            System.out.println(String.format("id: %s", user.getId()));
            System.out.println(String.format("Nom: %s", user.getName()));
            System.out.println(String.format("Points: %s", user.getPV()));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("Statistiques de parties");
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            // Affichage des statistiques de défi solo
            System.out.println(String.format("Nombre de défis solo joué: %d", stats.getInt("defi_solo")));
            System.out.println(String.format("Questions répondues en défi solo: %d", stats.getInt("tot_question_defi_solo")));
            System.out.println(String.format("Questions répondues juste en défi solo: %d", stats.getInt("jus_question_defi_solo")));
            System.out.println(String.format("Moyenne des questions répondues juste en défi solo: %.2f",
                    stats.getInt("tot_question_defi_solo") == 0 ? 0
                            : (double) (stats.getInt("jus_question_defi_solo")) / (stats.getInt("tot_question_defi_solo")) * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés en défi solo: %d", stats.getInt("pt_gagne_defi_solo")));
            System.out.println(String.format("Points perdus en défi solo: %d", stats.getInt("pt_perdu_defi_solo")));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            // Affichage des statistiques de défi vs
            System.out.println(String.format("Nombre de défis vs joué: %d", stats.getInt("defi_vs")));
            System.out.println(String.format("Questions répondues en défi vs: %d", stats.getInt("tot_question_defi_vs")));
            System.out.println(String.format("Questions répondues juste en défi vs: %d", stats.getInt("jus_question_defi_vs")));
            System.out.println(String.format("Moyenne des questions répondues juste en défi vs: %.2f",
                    stats.getInt("tot_question_defi_vs") == 0 ? 0
                            : (double) (stats.getInt("jus_question_defi_vs")) / (stats.getInt("tot_question_defi_vs")) * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés en défi vs: %d", stats.getInt("pt_gagne_defi_vs")));
            System.out.println(String.format("Points perdus en défi vs: %d", stats.getInt("pt_perdu_defi_vs")));

            System.out.println(String.format("Matchs réalisés en défi vs: %d", stats.getInt("match_vs")));
            System.out.println(String.format("Victoires de match réalisés en défi vs: %d", stats.getInt("victoire_vs")));
            System.out.println(String.format("Pourcentage de victoire en match vs: %.2f",
                    stats.getInt("match_vs") == 0 ? 0
                            : (double) (stats.getInt("victoire_vs")) / (stats.getInt("match_vs")) * 100.0)
                    + "%");

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            // Affichage des statistiques globales
            System.out.println(String.format("Nombre de défis effectués: %d", stats.getInt("defi_solo") + stats.getInt("defi_vs")));
            System.out.println(String.format("Questions répondues: %d",
                    stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs")));
            System.out.println(String.format("Questions répondues juste: %d",
                    stats.getInt("jus_question_defi_solo") + stats.getInt("jus_question_defi_vs")));
            System.out.println(String.format("Moyenne des questions répondues juste: %.2f",
                    (stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs")) == 0 ? 0
                            : (double) (stats.getInt("jus_question_defi_solo") + stats.getInt("jus_question_defi_vs"))
                                    / (stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs"))
                                    * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés: %d",
                    stats.getInt("pt_gagne_defi_solo") + stats.getInt("pt_gagne_defi_vs")));
            System.out.println(String.format("Points perdus: %d",
                    stats.getInt("pt_perdu_defi_solo") + stats.getInt("pt_perdu_defi_vs")));

            // Fermeture du ResultSet après l'utilisation
            stats.close();

            // Message pour revenir au menu des paramètres
            System.out.println();
            System.out.println("Tapez sur la touche entrée pour quitter la page statistique.");
            scanner.nextLine();

        } catch (Exception e) {
            // Gestion des erreurs lors de la récupération des statistiques
            System.out.println("Erreur lors du chargement des statistiques.");
            System.out.println(e);
            gestion.wait(2000); // Attendre 2 secondes avant de retourner au menu

        }
    }

    /**
     * Méthode qui permet de voir le profil d'un utilisateur.
     * L'utilisateur doit entrer le nom de l'utilisateur dont il souhaite voir le profil.
     */
    public void voir_profil() {
        System.out.println("Quel utilisateur voulez-vous voir ?");
        // Récupération de l'utilisateur par son nom
        Avatar user = dbProcess.getUserByName(scanner.nextLine());

        if (user != null) {
            // Affichage des statistiques de l'utilisateur demandé
            afficher_statistiques(user);
        } else {
            // Message d'erreur si l'utilisateur n'existe pas
            System.out.println("L'utilisateur demandé n'existe pas...");
            gestion.wait(2000); // Attendre 2 secondes avant de retourner au menu
        }
    }

    /**
     * Méthode qui permet d'ajouter une question.
     * Si l'utilisateur est un administrateur, il peut ajouter une question directement.
     * Sinon, il peut seulement proposer une question pour qu'un administrateur l'examine.
     *
     * @param user l'utilisateur qui ajoute ou propose une question
     */
    private void ajouter_question(Avatar user) {
        System.out.println("Tapez la question: ");
        String question = scanner.nextLine();

        System.out.println("Thème: ");
        String theme_question = scanner.nextLine();

        int nb = 2;

        // Validation du nombre de réponses
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

        // Collection des options de réponses
        List<String> Options_reponses = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            System.out.println(String.format("Choix %d", i + 1));
            String choix = scanner.nextLine();
            while (Options_reponses.contains(choix)) {
                System.out.println("Hop pop pop, vous avez déjà ajouté ce choix dans votre liste !");
                choix = scanner.nextLine();
            }
            Options_reponses.add(choix);
        }

        // Choix de la réponse correcte
        System.out.println("Réponse: ");
        String reponse = scanner.nextLine();
        while (!Options_reponses.contains(reponse)) {
            System.out.println("Hop pop pop, vous n'avez pas mis ça comme réponse dans votre liste !");
            reponse = scanner.nextLine();
        }

        int nb_point = 2;

        // Validation du nombre de points gagnés
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
            // Si l'utilisateur est un administrateur, il ajoute directement la question
            dbProcess.addQuestion(new Question(question, Options_reponses, reponse, nb_point, theme_question));
            System.out.println("SUCCES: Question ajoutée avec succès.");
        } else {
            // Si l'utilisateur n'est pas administrateur, il propose une question pour examen
            dbProcess.addQuestionRequest(new Question(question, Options_reponses, reponse, nb_point, theme_question),
                    user.getId());
            System.out.println(
                    "SUCCES: Question enregistrée avec succès. Un administrateur jettera un coup d'œil sur votre question.");
        }

        gestion.wait(5000); // Attendre 5 secondes avant de revenir au menu

    }

    /**
     * Méthode qui affiche les questions en attente.
     * L'utilisateur peut voir les questions qu'il a proposées et qui sont en attente d'examen.
     *
     * @param user l'utilisateur dont les questions en attente sont affichées
     */
    public void afficher_questions_attente(Avatar user) {
        // Récupération des questions en attente d'examen
        List<Question> listeAttente = dbProcess.get_question_user(user.getId());

        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");

        System.out.println("Liste de vos questions en attente..");

        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");

        if (listeAttente.size() == 0) {
            // Message si aucune question n'est en attente
            System.out.println("Liste de questions en attente vide...");
        } else {
            // Affichage des questions en attente
            for (Question q : listeAttente) {
                System.out.println(
                        String.format(
                                "- Question %s (Score: %d)", q.getQuestion(),
                                q.getPoints()));
            }
        }
        // Message pour revenir au menu des paramètres
        System.out.println("Cliquez sur Entrée pour revenir dans les paramètres...");
        scanner.nextLine();
    }

    /**
     * Méthode qui permet de changer le nom d'un utilisateur.
     * L'utilisateur entre un nouveau nom, et le système vérifie sa validité avant de le mettre à jour dans la base de données.
     *
     * @param user l'utilisateur dont le nom est changé
     */
    private void changer_utilisateur(Avatar user) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");

        System.out.println("Nouveau nom: ");
        while (true) {
            String nouveau_nom = scanner.nextLine();
            if (nouveau_nom.equals("")) {
                // Vérification que le nom n'est pas vide
                System.out.println("Erreur: Le nom n'est pas autorisé.");
            } else if (user.getName().equals(nouveau_nom)) {
                // Vérification que le nouveau nom est différent de l'ancien
                System.out.println("Nom non modifié.");
                gestion.wait(3000); // Attendre 3 secondes avant de retourner au menu
                break;
            } else if (dbProcess.getUserByName(nouveau_nom) != null) {
                // Vérification que le nom n'existe pas déjà
                System.out.println("Erreur: " + nouveau_nom + " existe déjà...");
            } else {
                // Mise à jour du nom d'utilisateur dans la base de données
                System.out.println(
                    "SUCCESS: Nouveau nom: " + nouveau_nom
                );
                dbProcess.updateUsername(user.getName(), nouveau_nom);
                gestion.wait(3000); // Attendre 3 secondes avant de retourner au menu
                break;
            }
        }
    }
}
