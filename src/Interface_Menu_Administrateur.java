import process.SysGestion;
import java.util.InputMismatchException;


import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
public class Interface_Menu_Administrateur {
    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;
    private DBProcess dbProcess;

    public Interface_Menu_Administrateur() {
        dbProcess = new DBProcess();
    }

    private Interface_Jeu jeu = new Interface_Jeu();

    public void menu_administrateur(String user) {

        while (true) {
            gestion.clear();
            System.out.println(String.format("Menu administrateur. Connecté en tant que %s", user));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("A - Ajouter une questions.");
            System.out.println("S - Retirer une questions.");
            System.out.println("M - Ajouter un nouvel administrateur.");
            System.out.println("R - Retirer un administrateur.");

            System.out.println("B - Retour au menu principal");

            switch (scanner.next().toUpperCase()) {
                case "A":
                    ajouter_question(user);
                    break;
                case "M":
                    ajouter_Administrateur(user);
                    break;

                case "R":
                    retirer_Administrateur(user);
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

    private void ajouter_question(String user) {
        System.out.println("Comment voulez-vous rajouter les questions ?");
        System.out.println("A - A la Main\nB - Par fichier texte/csv");

        switch (scanner.next().toUpperCase()){
            case "A":
                System.out.println("Taper la question: ");
                String question = scanner.next();
                
                
                System.out.println("Thème: ");
                String theme_question = scanner.next();
                
                int nb = 2;
                
                while (true) {
                    System.out.println("Nombre de réponses: ");
                    try {
                        nb = scanner.nextInt();

                        if (nb < 2) {
                            System.out.println("Nombre invalide: Valeur insuffisante - 2 choix minimum.");
                        } else if (nb > 4) {
                            System.out.println("Nombre invalide: 4 choix maximum.");
                        } else {
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("ERREUR: Le nombre entré n'est pas un nombre.");
                        nb = 0;
                    }
                }
                


                List<String> Options_reponses = new ArrayList<>();
                for (int i = 0; i < nb; i++) {
                    System.out.println(String.format("Choix %d", i+1));
                        Options_reponses.add(scanner.next());
                }
                System.out.println("Réponse: ");
                String reponse = scanner.next();
                while(!Options_reponses.contains(reponse)){
                    System.out.println("Hop pop pop , vous n'avez pas mis ca comme reponses dans votre liste ! ");
                    reponse = scanner.next();
                }

                int nb_point = 2;
                
                while (true) {
                    System.out.println("Nombre de points gagnés: ");
                    try {
                        nb_point = scanner.nextInt();

                        if (nb_point < 0) {
                            System.out.println("Nombre invalide: Valeur insuffisante - 0 point minimum.");
                        } else if (nb_point > 50) {
                            System.out.println("Nombre invalide: 50 points maximum.");
                        } else {
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("ERREUR: Le nombre entré n'est pas un nombre.");
                        nb = 0;
                    }
                }

                dbProcess.addQuestion(new Question(question, Options_reponses, reponse, nb_point, theme_question));
                System.out.println("SUCCES: Question ajouté avec succès.");

                gestion.wait(2000);

                break;

            default:
                System.out.println("ERREUR: Choix invalide.");
                gestion.wait(1000);
            }
                    
                    
    }

    private void ajouter_Administrateur(String user) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Ajouter un utilisateur.\nEntrez un nom: ");
        String admin = scanner.next();

        if (!dbProcess.isUser(admin)) {
            System.out.println(String.format("ERROR: L'utilisateur %s n'existe pas.", admin));
        } else if (admin == user) {
            System.out.println("WARNING: Vous êtes déjà admninistrateur.");
        } else if (dbProcess.isAdmin(admin)) {
            System.out.println(String.format("WARNING: %s est déjà administrateur.", admin));
        } else {
            dbProcess.addAdmin(admin);
            System.out.println(String.format("SUCCES: %s ajouté dans la liste des administrateurs.", admin));
        }
        gestion.wait(10000);
    }

    private void retirer_Administrateur(String user) {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Retirer un utilisateur.\nEntrez un nom: ");
        String admin = scanner.next();

        if (!dbProcess.isUser(admin)) {
            System.out.println(String.format("ERROR: L'utilisateur %s n'existe pas.", admin));
        } else if (admin == user) {
            System.out.println("WARNING: Vous pouvez pas vous retirer de la liste des administrateurs.");
        } else if (!dbProcess.isAdmin(admin)) {
            System.out.println(String.format("WARNING: %s n'est pas administrateur.", admin));
        } else {
            dbProcess.removeAdmin(admin);
            System.out.println(String.format("SUCCES: %s retiré de la liste des administrateurs.", admin));
        }
        gestion.wait(10000);
    }
}
