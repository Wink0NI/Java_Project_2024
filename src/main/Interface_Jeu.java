package main;
import java.util.Scanner;
import main.process.SysGestion;
import java.util.List;

/**
 * Cette classe gère l'interface de jeu pour le jeu "Question pour un champion".
 */
public class Interface_Jeu {

    private Scanner scanner = new Scanner(System.in); // Scanner pour lire les entrées utilisateur
    private SysGestion gestion; // Instance de SysGestion pour gérer les opérations du système

    private DBProcess dbProcess; // Instance de DBProcess pour les opérations liées à la base de données

    private Interface_Defis interface_Defis; // Interface pour les défis

    /**
     * Constructeur pour initialiser une instance d'Interface_Jeu.
     */
    public Interface_Jeu() {
        dbProcess = new DBProcess(); // Initialisation de dbProcess
        interface_Defis = new Interface_Defis(); // Initialisation de Interface_Defis
    }

    /**
     * Affiche le menu de jeu et gère les actions de l'utilisateur.
     * 
     * @param user_id l'identifiant de l'utilisateur courant
     */
    public void menu_jouer(String user_id) {

        while (true) {
            Avatar user = dbProcess.getUserById(user_id); // Récupère les informations de l'utilisateur
            gestion.clear(); // Nettoie l'écran

            System.out.println("Modes de jeu");
            System.out.println("----------------");
            System.out.println("S - Commencer un défi solo");
            System.out.println("D - Défier un joueur");
            System.out.println("F - Défis");
            System.out.println("R - Retour");

            // Gestion des entrées utilisateur pour choisir un mode de jeu
            switch (scanner.nextLine().toUpperCase()) {
                case "S":
                    defi_solo(user); // Lance un défi solo
                    break;

                case "D":
                    defi(user); // Lance un défi contre un autre joueur
                    break;

                case "F":
                    interface_Defis.menu_Defis(user_id); // Affiche le menu des défis
                    break;

                case "R":
                    System.out.println("Retour au menu principal.");
                    gestion.wait(2000); // Attend 2 secondes avant de retourner au menu principal
                    return;

                default:
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000); // Attend 2 secondes en cas de commande invalide
            }

        }
    }

    /**
     * Gère le processus d'un défi solo.
     * 
     * @param user l'avatar de l'utilisateur courant
     */
    private void defi_solo(Avatar user) {

        String theme = "";
        System.out.println("Défi solo");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        // Boucle pour choisir le thème
        while (true) {
            System.out.println(String.format("Choisissez le thème:\nThèmes:\n- Tout\n%s", dbProcess.getThemes()));
            theme = scanner.nextLine().toLowerCase();
            if (dbProcess.isTheme(theme) || theme.equals("tout")) {
                if (theme.equals("tout"))
                    theme = "";
                break;
            }

            System.out.println(String.format("Le thème %s n'existe pas", theme));
            gestion.wait(1000); // Attend 1 seconde en cas de thème invalide
        }

        int nb_questions = Math.min(20, dbProcess.get_nb_questions(theme)); // Limite le nombre de questions à 20

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("Tu devras répondre à une série de %s questions.", nb_questions));
        System.out.println("Les questions auxquelles tu répondras correctement seront ajoutées dans ton cerveau !");
        System.out.println(
                "Ton score te permettra d'augmenter ton savoir ou d'en perdre si tu obtiens ou non la moyenne.");
        System.out.println(
                "Cependant, n'oublie pas que chaque question te fera gagner ou perdre du savoir ! Le résultat du défi ne sera qu'un bonus/malus sur ton savoir !");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Le jeu va commencer. Tape n'importe quoi pour débuter\nR - Annuler");

        // Vérifie si l'utilisateur souhaite annuler le défi
        if (scanner.nextLine().toUpperCase().equals("R")) {
            return;
        } else { // Le défi commence
            int score = 0;
            int currentPV = user.getPV();
            int pt_gagne = 0;
            int pt_perdu = 0;

            List<Question> questions = dbProcess.generate_question(nb_questions, theme); // Génère les questions

            System.out.println("Défi solo");

            // Boucle pour chaque question du défi
            for (int i = 0; i < nb_questions; i++) {
                Question question = questions.get(i);

                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println(String.format("Question n°%d - %s", i + 1, question.getQuestion()));

                // Affiche les choix de réponses
                for (int num_choix = 0; num_choix < question.getChoices().size(); num_choix++) {
                    System.out.println((num_choix + 1) + " - " + question.getChoices().get(num_choix));
                }
                System.out.print("Réponse -> ");
                String resp = scanner.nextLine();

                // Vérifie si la réponse est correcte
                if (resp.equalsIgnoreCase(question.getResponse())) {
                    System.out.println("Bonne réponse !");
                    score++;
                    pt_gagne += question.getPoints();
                    currentPV += question.getPoints();
                    continue;
                }
                
                try {
                    
                    if (question.getChoices().get(Integer.parseInt(resp)-1).equals(question.getResponse())) {
                        System.out.println("Bonne réponse !");
                        score++;
                        pt_gagne += question.getPoints();
                        currentPV += question.getPoints();
                        continue;
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
                } catch (Exception e) {
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

            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
            int changement_val_savoir = score - (nb_questions / 2);
            dbProcess.updatePV(user.getId(), currentPV + changement_val_savoir); // Met à jour les PV de l'utilisateur
            dbProcess.updateStats(user.getId(), nb_questions, score, "solo", pt_gagne, pt_perdu); // Met à jour les statistiques du défi

            System.out.println("Résultat du défi: " + score + "/" + nb_questions);
            if (score > (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score supérieur à la moyenne ! Voici un bonus de savoir: +"
                        + changement_val_savoir);
            } else if (score < (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score inférieur à la moyenne ! Tu écopes donc d'un malus de savoir: "
                        + changement_val_savoir);
            } else {
                System.out.println("Tu as obtenu la moyenne ! Tu n'obtiens ni de bonus ni de malus de savoir...");
            }
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println("Tape n'importe quoi pour quitter");
            scanner.nextLine(); // Attend que l'utilisateur appuie sur Entrée pour quitter

        }
    }

    /**
     * Gère le processus d'un défi contre un autre joueur.
     * 
     * @param user l'avatar de l'utilisateur courant
     */
    private void defi(Avatar user) {

        String theme = "";
        System.out.println("Défi VS");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        String cible;
        int nb_jours = 0;
        // Boucle pour choisir la cible du défi
        while (true) {
            System.out.println("Qui veux-tu défier ???");
            cible = scanner.nextLine();
            if (!dbProcess.isUser(cible))
                System.out.println("ERREUR: Cet utilisateur n'existe pas.");
            if (!user.getName().equalsIgnoreCase(cible))
                System.out.println("ERREUR: Vous ne pouvez pas défier vous-même.");
            else {
                System.out.println("Confirmer: o/n");

                if (scanner.nextLine().equalsIgnoreCase("o")) {
                    break;
                }
            }
        }

        // Boucle pour choisir le nombre de jours pour répondre
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

        // Boucle pour choisir le thème
        while (true) {
            System.out.println(String.format("Choisissez le thème:\nThèmes:\n- Tout\n%s", dbProcess.getThemes()));
            theme = scanner.nextLine().toLowerCase();
            if (dbProcess.isTheme(theme) || theme.equals("tout")) {
                if (theme.equals("tout"))
                    theme = "";
                break;
            }

            System.out.println(String.format("Le thème %s n'existe pas", theme));
            gestion.wait(1000); // Attend 1 seconde en cas de thème invalide
        }

        int nb_questions = Math.min(20, dbProcess.get_nb_questions(theme)); // Limite le nombre de questions à 20

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("Tu devras répondre à une série de %s questions.", nb_questions));
        System.out.println("Les questions auxquelles tu répondras correctement seront ajoutées dans ton cerveau !");
        System.out.println(
                "Ton score te permettra d'augmenter ton savoir ou d'en perdre si tu obtiens ou non la moyenne.");
        System.out.println(
                "Cependant, n'oublie pas que chaque question te fera gagner ou perdre du savoir ! Le résultat du défi ne sera qu'un bonus/malus sur ton savoir !");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("Le jeu va commencer. Tape n'importe quoi pour débuter\nR - Annuler");

        // Vérifie si l'utilisateur souhaite annuler le défi
        if (scanner.nextLine().toUpperCase().equals("R")) {
            return;
        } else { // Le défi commence
            int score = 0;
            int currentPV = user.getPV();
            int pt_gagne = 0;
            int pt_perdu = 0;

            List<Question> questions = dbProcess.generate_question(nb_questions, theme); // Génère les questions

            System.out.println("Défi solo");

            // Boucle pour chaque question du défi
            for (int i = 0; i < nb_questions; i++) {
                Question question = questions.get(i);

                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println(String.format("Question n°%d - %s", i + 1, question.getQuestion()));

                // Affiche les choix de réponses
                for (int num_choix = 0; num_choix < question.getChoices().size(); num_choix++) {
                    System.out.println((num_choix + 1) + " - " + question.getChoices().get(num_choix));
                }
                System.out.print("Réponse -> ");
                String resp = scanner.nextLine();
                
                // Vérifie si la réponse est correcte
                if (resp.equalsIgnoreCase(question.getResponse())) {
                    System.out.println("Bonne réponse !");
                    score++;
                    pt_gagne += question.getPoints();
                    currentPV += question.getPoints();
                    continue;
                }
                
                try {
                    
                    if (question.getChoices().get(Integer.parseInt(resp)-1).equals(question.getResponse())) {
                        System.out.println("Bonne réponse !");
                        score++;
                        pt_gagne += question.getPoints();
                        currentPV += question.getPoints();
                        continue;
                    } else {
                        System.out.println(resp);
                            System.out.println("Faux ! Réponse correcte: " + question.getResponse());
                            if (currentPV - question.getPoints() < 0) {
                                currentPV = 0;
                            } else {
                                currentPV -= question.getPoints();
                            }
                            pt_perdu += question.getPoints();

                            gestion.wait(2000); // Attend 2 secondes avant de passer à la question suivante
                        }
                } catch (Exception e) {
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

            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");
            int changement_val_savoir = score - (nb_questions / 2);
            dbProcess.updatePV(user.getId(), currentPV + changement_val_savoir); // Met à jour les PV de l'utilisateur
            dbProcess.updateStats(user.getId(), nb_questions, score, "vs", pt_gagne, pt_perdu); // Met à jour les statistiques du défi

            Avatar cible_avatar = dbProcess.getUserByName(cible); // Récupère les informations de l'utilisateur ciblé
            int pts = pt_gagne - pt_perdu;
            if (pts < 0)
                pts = 0;

            dbProcess.addQuestionDuel(questions, user.getId(), cible_avatar.getId(), pts, nb_jours); // Ajoute les questions du duel dans la base de données

            System.out.println("Résultat du défi: " + score + "/" + nb_questions);
            if (score > (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score supérieur à la moyenne ! Voici un bonus de savoir: +"
                        + changement_val_savoir);
            } else if (score < (nb_questions / 2)) {
                System.out.println("Tu as obtenu un score inférieur à la moyenne ! Tu écopes donc d'un malus de savoir: "
                        + changement_val_savoir);
            } else {
                System.out.println("Tu as obtenu la moyenne ! Tu n'obtiens ni de bonus ni de malus de savoir...");
            }
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.println(String.format("%s va recevoir une notification du combat.", cible));

            System.out.println("Tape n'importe quoi pour quitter");
            scanner.nextLine(); // Attend que l'utilisateur appuie sur Entrée pour quitter

        }
    }
}
