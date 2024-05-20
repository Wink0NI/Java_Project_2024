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
    }

    private Interface_Jeu jeu = new Interface_Jeu();
    private Interface_Menu_Administrateur admin = new Interface_Menu_Administrateur();

    private String afficherTempsRestant(Timestamp tps) {

        long temps_restant = tps.getTime() - System.currentTimeMillis();

        long jours = temps_restant / (24 * 60 * 60 * 1000);
        long heures = (temps_restant % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (temps_restant % (60 * 60 * 1000)) / (60 * 1000);
        long secondes = (temps_restant % (60 * 1000)) / 1000;

        String jours_str = jours == 1 ? String.valueOf(jours) + " jour" : String.valueOf(jours) + " jours";
        String heures_str = heures == 1 ? String.valueOf(heures) + " heure" : String.valueOf(heures) + " heures";
        String minutes_str = minutes == 1 ? String.valueOf(minutes) + " minute" : String.valueOf(minutes) + " minutes";
        String secondes_str = secondes == 1 ? String.valueOf(secondes) + " seconde"
                : String.valueOf(secondes) + " secondes";

        List<String> heures_list = new ArrayList<String>();
        if (jours > 0)
            heures_list.add(jours_str);
        if (heures > 0)
            heures_list.add(heures_str);
        if (minutes > 0)
            heures_list.add(minutes_str);
        if (secondes > 0)
            heures_list.add(secondes_str);

        return String.join(" ", heures_list);
    }

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
                    if (defi.get("vainqueur").equals(user_id)) {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, mais il n'a pas eu suffisament de puissance pour te dominer. Bravo !!!",
                                        defi.get("user_cible"))

                        );
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"));
                    } else if (defi.get("vainqueur").equals("Aucune")) {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, et il a eu suffisament de point pour ne pas perdre.",
                                        defi.get("user_cible"))

                        );
                    } else {
                        System.out.println(
                                String.format(
                                        "%s a accepté ton défi, et il a pas eu suffisament de puissance pour te dominer. Dommage !!!",
                                        defi.get("user_cible"))

                        );
                        dbProcess.updateStatsVictoire(user_id, defi.get("duel_id"));
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
                                        afficherTempsRestant((Timestamp) duel.get("temps_limite"))));

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
            System.out.println(String.format("Moyenne des questions répondus juste en défi solo: %.2f", stats.getInt("tot_question_defi_solo") == 0 ? 0 :
                    (double) (stats.getInt("jus_question_defi_solo")) / (stats.getInt("tot_question_defi_solo"))
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
            System.out.println(String.format("Moyenne des questions répondus juste en défi vs: %.2f", stats.getInt("tot_question_defi_vs") == 0 ? 0 :
                    (double) (stats.getInt("jus_question_defi_vs")) / (stats.getInt("tot_question_defi_vs"))
                            * 100.0)
                    + "%");
            System.out.println(String.format("Points gagnés en défi vs: %d", stats.getInt("pt_gagne_defi_vs")));
            System.out.println(String.format("Points perdus en défi vs: %d", stats.getInt("pt_perdu_defi_vs")));

            System.out.println(String.format("Matchs réalisés en défi vs: %d", stats.getInt("match_vs")));
            System.out
                    .println(String.format("Victoires de match réalisés en défi vs: %d", stats.getInt("victoire_vs")));
            System.out.println(String.format("Pourcentage de victoire en match vs: %.2f", stats.getInt("match_vs") == 0 ? 0 :
                    (double) (stats.getInt("victoire_vs")) / (stats.getInt("match_vs"))
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
            System.out.println(String.format("Moyenne des questions dépondus juste: %.2f", stats.getInt("tot_question_defi_solo") + stats.getInt("tot_question_defi_vs") == 0 ? 0 :
                    (double) (stats.getInt("jus_question_defi_solo") + stats.getInt("jus_question_defi_vs"))
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
}
