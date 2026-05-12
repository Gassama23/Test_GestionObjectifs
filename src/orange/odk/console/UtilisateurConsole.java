package orange.odk.console;

import java.util.Scanner;

import orange.odk.models_test.Utilisateur;

public class UtilisateurConsole {

    private final Scanner scanner;

    public UtilisateurConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public void boucle(Utilisateur utilisateur) {
        boolean deconnecte = false;

        while (!deconnecte) {
            afficherMenu(utilisateur);
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    System.out.println("Page utilisateur : mes objectifs (placeholder).");
                    break;
                case "2":
                    System.out.println("Page utilisateur : mes progressions (placeholder).");
                    break;
                case "0":
                    deconnecte = true;
                    System.out.println("Deconnexion utilisateur effectuee.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherMenu(Utilisateur utilisateur) {
        System.out.println();
        System.out.println("===== Menu Utilisateur =====");
        System.out.println("Connecte : " + utilisateur.getPrenom() + " " + utilisateur.getNom());
        System.out.println("1. Voir mes objectifs");
        System.out.println("2. Voir mes progressions");
        System.out.println("0. Se deconnecter");
        System.out.print("Votre choix : ");
    }
}
