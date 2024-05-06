import process.SysGestion;

import java.util.Scanner;

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
            System.out.println(String.format("Menu administrateur. Connecté en tant que %s", user));

            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            System.out.println("A - Ajouter une questions.");
            System.out.println("S - Retirer une questions.");
            System.out.println("M - Ajouter un nouvel administrateur.");
            System.out.println("R - Retirer un administrateur.");
            
            System.out.println("B - Retour au menu principal");

            switch (scanner.next().toUpperCase()) {
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


    private void ajouter_Administrateur(String user) {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
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
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
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
