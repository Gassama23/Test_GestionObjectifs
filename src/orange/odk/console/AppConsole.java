package orange.odk.console;

import java.sql.SQLException;
import java.util.Scanner;

import orange.odk.models_test.Utilisateur;
import orange.odk.models_test.enums.Role;
import orange.odk.services.AuthService;

public class AppConsole {

    private final Scanner scanner;
    private final AuthConsole authConsole;
    private final AdminConsole adminConsole;
    private final UtilisateurConsole utilisateurConsole;

    public AppConsole() throws SQLException {
        this.scanner = new Scanner(System.in);

        AuthService authService = new AuthService();
        this.authConsole = new AuthConsole(scanner, authService);
        this.adminConsole = new AdminConsole(scanner);
        this.utilisateurConsole = new UtilisateurConsole(scanner);
    }

    public void demarrer() {
        boolean quitter = false;

        while (!quitter) {
            afficherMenuAccueil();
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    ouvrirEspace(authConsole.gererConnexion());
                    break;
                case "2":
                    ouvrirEspace(authConsole.gererInscription());
                    break;
                case "0":
                    quitter = true;
                    System.out.println("Fermeture de l'application.");
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void ouvrirEspace(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return;
        }

        Role role = utilisateur.getRole();
        if (role == null) {
            System.out.println("Impossible de determiner le role de l'utilisateur.");
            return;
        }

        switch (role) {
            case ADMIN:
                adminConsole.boucle(utilisateur);
                break;
            case UTILISATEUR:
                utilisateurConsole.boucle(utilisateur);
                break;
            default:
                System.out.println("Role non pris en charge : " + role);
        }
    }

    private void afficherMenuAccueil() {
        System.out.println();
        System.out.println("===== Gestion Objectifs =====");
        System.out.println("1. Connexion");
        System.out.println("2. Inscription");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }
}
