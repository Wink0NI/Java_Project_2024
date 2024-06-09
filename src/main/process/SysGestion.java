package main.process;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class SysGestion {
    public static void clear() {

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String afficherTempsRestant(Timestamp tps) {

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
}
