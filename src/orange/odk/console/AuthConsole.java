package orange.odk.console;

import java.util.Scanner;

import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.models_test.Utilisateur;
import orange.odk.services.AuthService;

public class AuthConsole {

    private final Scanner scanner;
    private final AuthService authService;

    public AuthConsole(Scanner scanner, AuthService authService) {
        this.scanner = scanner;
        this.authService = authService;
    }

    public Utilisateur gererConnexion() {
        afficherTitre("Connexion");

        String email = lireValeurObligatoire("Email : ");
        String motDePasse = lireValeurObligatoire("Mot de passe : ");

        try {
            Utilisateur utilisateur = authService.connecter(email, motDePasse);
            System.out.println("Connexion reussie. Bienvenue " + utilisateur.getPrenom() + ".");
            return utilisateur;
        } catch (IllegalArgumentException | DaoException e) {
            System.out.println("Echec de la connexion : " + e.getMessage());
            return null;
        }
    }

    public Utilisateur gererInscription() {
        afficherTitre("Inscription");

        String nom = lireValeurObligatoire("Nom : ");
        String prenom = lireValeurObligatoire("Prenom : ");
        String email = lireValeurObligatoire("Email : ");
        String motDePasse = lireValeurObligatoire("Mot de passe : ");

        try {
            Utilisateur utilisateur = authService.inscrire(nom, prenom, email, motDePasse);
            System.out.println("Inscription reussie. Vous etes maintenant connecte.");
            return utilisateur;
        } catch (IllegalArgumentException | DaoException e) {
            System.out.println("Echec de l'inscription : " + e.getMessage());
            return null;
        }
    }

    private void afficherTitre(String titre) {
        System.out.println();
        System.out.println("===== " + titre + " =====");
    }

    private String lireValeurObligatoire(String message) {
        String valeur;

        do {
            System.out.print(message);
            valeur = scanner.nextLine();
            if (valeur.trim().isEmpty()) {
                System.out.println("Ce champ est obligatoire.");
            }
        } while (valeur.trim().isEmpty());

        return valeur.trim();
    }
}
