import process.SysGestion;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Interface_Menu_Principal {
    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;
    private DBProcess dbProcess;

    public Interface_Menu_Principal() {
        dbProcess = new DBProcess();
        gestion = new SysGestion();
    }

    private Interface_Jeu jeu = new Interface_Jeu();
    private Interface_Menu_Administrateur admin = new Interface_Menu_Administrateur();
    private Interface_Menu_Parametres parametres = new Interface_Menu_Parametres();


    public void menu_principal(String user_id) {

        while (true) {
            List<HashMap<String, Object>> defis = dbProcess.get_duels(user_id);
            List<HashMap<String, String>> resultat_defi = dbProcess.get_duels_resultat(user_id);
            Avatar user = dbProcess.getUserById(user_id);

            gestion.clear();
            System.out.println(String.format("Menu principal du jeu. Connecté en tant que %s", user.getName()));
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println(
                    "NOTIFICATIONS");

            if (defis.size() == 0 && resultat_defi.size() == 0)
                System.out.println("Rien à signaler.");

            if (resultat_defi.size() > 0) {
                for (HashMap<String, String> defi : resultat_defi) {
                        Avatar ennemi = dbProcess.getUserById(defi.get("user_cible"));
                    if (defi.get("vainqueur").equals(user_id)) {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, mais il n'a pas eu suffisament de puissance pour te dominer. Bravo !!!",
                                        ennemi.getName())

                        );
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"), true);
                    } else if (defi.get("vainqueur").equals("Aucune")) {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, et il a eu suffisament de point pour ne pas perdre.",
                                        ennemi.getName())

                        );
                    } else {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, et il a pas eu suffisament de puissance pour te dominer. Dommage !!!",
                                        ennemi.getName())

                        );
                        dbProcess.updateStatsDefaite(user_id, defi.get("duel_id"), true);
                    }
                }
            }
            if (defis.size() > 0) {
                Timestamp tps = new Timestamp(System.currentTimeMillis());
                for (HashMap<String, Object> duel : defis) {
                    if (tps.before((Timestamp) duel.get("temps_limite"))) {
                        System.out.println(
                                String.format("- %s veut te défier: %s restant.",
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

                    }
                }
            }
            System.out.println(
                    "----------------------------------------------------------------------------------------------------------------------");
            System.out.println("J - Jouer");
            System.out.println("S - Rue sociale");

            if (dbProcess.isAdmin(user.getName())) {
                System.out.println("A - Mode Administrateur");
            }

            System.out.println("D - Se déconnecter");
            switch (scanner.nextLine().toUpperCase()) {
                case "J":
                    jeu.menu_jouer(user_id);
                    break;

                case "S":
                parametres.menu_parametres(user_id);
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

    
}
