import process.SysGestion;
import java.sql.*;
import java.util.Scanner;

public class Interface_Menu_Principal {
    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;
    private DBProcess dbProcess;

    public Interface_Menu_Principal() {
        dbProcess = new DBProcess();
    }

    private Interface_Jeu jeu = new Interface_Jeu();
    private Interface_Menu_Administrateur admin = new Interface_Menu_Administrateur();

    public void menu_principal(String user_id) {

        while (true) {
            Avatar user = dbProcess.getUserById(user_id);
            gestion.clear();
            System.out.println(String.format("Menu principal du jeu. Connecté en tant que %s", user.getName()));

            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("J - Jouer");
            System.out.println("S - Voir les statistiques");

            if (dbProcess.isAdmin(user.getName())) {
                System.out.println("A - Mode Administrateur");
            }

            System.out.println("D - Se déconnecter");
            switch (scanner.nextLine().toUpperCase()) {
                case "J":
                    jeu.menu_jouer(user_id);
                    break;

                case "S":
                    afficher_statistiques(user);
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
            System.out.println(String.format("Moyenne des questions répondus juste en défi solo: %.2f", (double) (stats.getInt("jus_question_defi_solo"))/(stats.getInt("tot_question_defi_solo"))*100.0)+"%");
            System.out.println(String.format("Points gagnés en défi solo: %d", stats.getInt("pt_gagne_defi_solo")));
            System.out.println(String.format("Points perdus en défi solo: %d", stats.getInt("pt_perdu_defi_solo")));

            System.out.println();

            System.out.println(String.format("Nombre de défi effectués: %d", stats.getInt("defi_solo")));
            System.out.println(
                    String.format("Questions répondus: %d", stats.getInt("tot_question_defi_solo")));
            System.out.println(
                    String.format("Questions répondus juste: %d", stats.getInt("jus_question_defi_solo")));
            System.out.println(String.format("Moyenne des questions dépondus juste: %.2f", (double) (stats.getInt("jus_question_defi_solo"))/(stats.getInt("tot_question_defi_solo"))*100.0)+"%");
            System.out.println(String.format("Points gagnés: %d", stats.getInt("pt_gagne_defi_solo")));
            System.out.println(String.format("Points perdus: %d", stats.getInt("pt_perdu_defi_solo")));

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
}
