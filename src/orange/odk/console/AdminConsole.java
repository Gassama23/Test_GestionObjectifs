package orange.odk.console;

import java.util.Scanner;

import orange.odk.models_test.Utilisateur;

public class AdminConsole {

    private final Scanner scanner;

    public AdminConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public void boucle(Utilisateur utilisateur) {
        boolean deconnecte = false;

        while (!deconnecte) {
            afficherMenu(utilisateur);
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    System.out.println("Page admin : gestion des utilisateurs (placeholder).");
                    break;
                case "2":
                    System.out.println("Page admin : supervision des objectifs (placeholder).");
                    break;
                case "0":
                    deconnecte = true;
                    System.out.println("Deconnexion admin effectuee.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherMenu(Utilisateur utilisateur) {
        System.out.println();
        System.out.println("===== Menu Admin =====");
        System.out.println("Connecte : " + utilisateur.getPrenom() + " " + utilisateur.getNom());
        System.out.println("1. Gerer les utilisateurs");
        System.out.println("2. Voir les objectifs");
        System.out.println("0. Se deconnecter");
        System.out.print("Votre choix : ");
    }
}
