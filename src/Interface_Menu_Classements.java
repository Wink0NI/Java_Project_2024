import process.SysGestion;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;
import java.util.InputMismatchException;

public class Interface_Menu_Classements {
    SysGestion gestion = new SysGestion();
    DBProcess dbProcess = new DBProcess();

    Scanner scanner = new Scanner(System.in);

    public void menu_classements(String user_id) {
        while (true) {
            Avatar user = dbProcess.getUserById(user_id);

            gestion.clear();
            System.out.println(String.format("Menu classement. Connecté en tant que %s", user.getName()));
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");

            System.out.println("P - Classement par points...");
            System.out.println("R - Classement par victoire...");

            System.out.println("T - Retour");
            switch (scanner.nextLine().toUpperCase()) {
                case "P":
                    afficher_classement_par_points();
                    break;

                case "R":
                    afficher_classement_par_victoire();
                    break;

                case "T":
                    System.out.println(String.format("Au revoir %s !!!", user.getName()));
                    gestion.wait(2000);
                    return;

                default:
                    System.out.println("Commande saisie invalide...");
                    gestion.wait(2000);

            }
        }

    }

    private void afficher_classement_par_points() {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        List<String> classement = dbProcess.get_classement_points();

        if (classement.size() == 0) {
            System.out.println("Aucun joueurs...");
            gestion.wait(2000);
        } else {
            for (String user : classement) {
                Avatar user_classment = dbProcess.getUserById(user);
                if (classement.indexOf(user) == 0) {
                    System.out
                            .println(String.format("Le roi des points est: %s avec %s %s !!!", user_classment.getName(),
                                    user_classment.getPV(), user_classment.getPV() > 1 ? "points" : "point"));
                } else {
                    System.out
                            .println(String.format("- %d: %s - %s %s", classement.indexOf(user) + 1, user_classment.getName(),
                            user_classment.getPV(), user_classment.getPV() > 1 ? "victoires" : "victoire"));
                }
            }
            System.out.println("Cliquez sur Entrée pour revenir...");
            scanner.nextLine();
        }
    }

    private void afficher_classement_par_victoire() {
        System.out.println(
                "----------------------------------------------------------------------------------------------------------------------");
        List<String> classement = dbProcess.get_classement_victoire();

        if (classement.size() == 0) {
            System.out.println("Aucun joueurs...");
            gestion.wait(2000);
        } else {
            for (String user : classement) {
                Avatar user_classment = dbProcess.getUserById(user);
                ResultSet stats = dbProcess.getStat(user);
                int victoire;
                try {
                    victoire = stats.getInt("victoire_vs");
                    stats.close();
                } catch (SQLException e) {
                    victoire = 0;
                }
                
                if (classement.indexOf(user) == 0) {
                    System.out
                            .println(String.format("Le roi du jeu est: %s avec %s %s !!!", user_classment.getName(),
                                    victoire, victoire > 1 ? "victoires" : "victoire"));
                } else {
                    System.out
                            .println(String.format("- %d: %s - %s %s", classement.indexOf(user) + 1, user_classment.getName(),
                                    victoire, victoire > 1 ? "victoires" : "victoire"));
                }

                
            }
            System.out.println("Cliquez sur Entrée pour revenir...");
            scanner.nextLine();
        }
    }
}
