import process.SysGestion;

import java.util.Scanner;

public class Interface_Menu_Principal  {
    Scanner scanner = new Scanner(System.in);
    private SysGestion gestion;
    private DBProcess dbProcess;

    public Interface_Menu_Principal() {
        dbProcess = new DBProcess();
    }

    private Interface_Jeu jeu = new Interface_Jeu();
    private Interface_Menu_Administrateur admin = new Interface_Menu_Administrateur();

    public void menu_principal(String user) {


        
        while (true) {
            gestion.clear();
            System.out.println(String.format("Menu principal du jeu. Connecté en tant que %s", user));

            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.println("J - Jouer");

            if (dbProcess.isAdmin(user)) {
                System.out.println("A - Mode Administrateur");
            }

            System.out.println("D - Se déconnecter");
            switch (scanner.next().toUpperCase()) {
                case "J":
                    jeu.menu_jouer(user);
                    break;
                
                case "D":
                    System.out.println(String.format("Au revoir %s !!!", user));
                    gestion.wait(2000);
                    return;
                case "A":
                    if (dbProcess.isAdmin(user)) admin.menu_administrateur(user);
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
