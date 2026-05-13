package orange.odk.services;

import java.sql.SQLException;

import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.models_test.Utilisateur;
import orange.odk.repo_test.UtilisateurRepository;

public class AuthService {

    private final UtilisateurRepository utilisateurRepository;

    public AuthService() throws SQLException {
        this.utilisateurRepository = new UtilisateurRepository();
    }

    public Utilisateur inscrire(String nom, String prenom, String email, String motDePasse) throws DaoException {
        verifierChamp("nom", nom);
        verifierChamp("prenom", prenom);
        verifierChamp("email", email);
        verifierChamp("mot de passe", motDePasse);

        String emailNormalise = email.trim().toLowerCase();
        if (utilisateurRepository.findByEmail(emailNormalise) != null) {
            throw new IllegalArgumentException("Un utilisateur existe deja avec cet email.");
        }

        Utilisateur utilisateur = new Utilisateur(0, nom.trim(), prenom.trim(), emailNormalise, motDePasse.trim());
        utilisateurRepository.create(utilisateur);

        Utilisateur utilisateurCree = utilisateurRepository.findByEmail(emailNormalise);
        return utilisateurCree != null ? utilisateurCree : utilisateur;
    }

    public Utilisateur connecter(String email, String motDePasse) throws DaoException {
        verifierChamp("email", email);
        verifierChamp("mot de passe", motDePasse);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email.trim().toLowerCase());
        if (utilisateur == null) {
            throw new IllegalArgumentException("Aucun compte trouve avec cet email.");
        }

        if (!utilisateur.getMotDePasse().equals(motDePasse.trim())) {
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }

        return utilisateur;
    }

    private void verifierChamp(String nomChamp, String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ " + nomChamp + " est obligatoire.");
        }
    }
}
