package orange.odk.repo_test;

import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.dao_test.impl.DaoImpl;
import orange.odk.dao_test.interfaces.IDao;
import orange.odk.dao_test.query.QueryBuilder;
import orange.odk.models_test.Utilisateur;
import orange.odk.models_test.enums.Role;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * UtilisateurRepository — accès aux données des utilisateurs.
 *
 * Connaît : la table "utilisateurs", le mapping Map → Utilisateur.
 * Ne connaît pas : le SQL bas niveau (délégué à IGenericDao).
 * Dépend de l'interface IGenericDao, jamais de l'implémentation concrète.
 *
 * Règles métier appliquées ici :
 *   - create()  → insère uniquement nom, prenom, email, mot_de_passe
 *   - update()  → met à jour uniquement nom, prenom, email, mot_de_passe
 *   - role      → jamais écrit depuis le code, lu uniquement depuis la base
 *   - dateInscription → jamais écrite depuis le code, gérée par DEFAULT en base
 */
public class UtilisateurRepository {

    private static final String TABLE = "utilisateurs";
    private static final String PK    = "id";

    private final IDao dao;

    public UtilisateurRepository() throws SQLException {
        this.dao = new DaoImpl();
    }

    /** Constructeur pour injection de dépendance (tests, mock…) */
    public UtilisateurRepository(IDao dao) {
        this.dao = dao;
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    /**
     * Insère un nouvel utilisateur.
     * NE transmet PAS le rôle ni la dateInscription — gérés par la base.
     */
    public Utilisateur create(Utilisateur u) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("nom",          u.getNom());
        donnees.put("prenom",       u.getPrenom());
        donnees.put("email",        u.getEmail());
        donnees.put("mot_de_passe", u.getMotDePasse());

        int id = dao.insert(TABLE, donnees);
        u.setId(id);
        return u;
    }

    public Utilisateur findById(int id) throws DaoException {
        Map<String, Object> row = dao.findById(TABLE, PK, id);
        return row == null ? null : mapper(row);
    }

    public List<Utilisateur> findAll() throws DaoException {
        List<Utilisateur> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findAll(TABLE)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    /**
     * Met à jour les informations modifiables d'un utilisateur.
     * NE met PAS à jour le rôle ni la dateInscription.
     */
    public Utilisateur update(Utilisateur u) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("nom",          u.getNom());
        donnees.put("prenom",       u.getPrenom());
        donnees.put("email",        u.getEmail());
        donnees.put("mot_de_passe", u.getMotDePasse());

        dao.update(TABLE, PK, u.getId(), donnees);
        return u;
    }

    public boolean delete(int id) throws DaoException {
        return dao.delete(TABLE, PK, id);
    }

    public boolean exists(int id) throws DaoException {
        return dao.exists(TABLE, PK, id);
    }

    public int count() throws DaoException {
        return dao.count(TABLE);
    }

    // -------------------------------------------------------------------------
    // REQUÊTES MÉTIER
    // -------------------------------------------------------------------------

    public Utilisateur findByEmail(String email) throws DaoException {
        List<Map<String, Object>> rows = dao.findBy(TABLE, "email", email);
        return rows.isEmpty() ? null : mapper(rows.get(0));
    }

    public List<Utilisateur> findByNom(String nom) throws DaoException {
        List<Utilisateur> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findBy(TABLE, "nom", nom)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    /**
     * Récupère tous les utilisateurs ayant un rôle donné.
     * Utile pour lister les admins ou filtrer les users classiques.
     */
    public List<Utilisateur> findByRole(Role role) throws DaoException {
        List<Utilisateur> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findBy(TABLE, "role", role.name())) {
            liste.add(mapper(row));
        }
        return liste;
    }

    /**
     * Récupère un utilisateur avec le nombre de ses objectifs (jointure + agrégation).
     * Retourne une Map enrichie : { utilisateur, nb_objectifs }
     */
    public Map<String, Object> findAvecStats(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("u.*", "COUNT(o.id) AS nb_objectifs")
                .from(TABLE, "u")
                .leftJoin("objectifs", "o", "o.utilisateur_id = u.id")
                .where("u.id = ?", utilisateurId)
                .groupBy("u.id");

        Map<String, Object> row = dao.executeQueryOne(query);
        if (row == null) return null;

        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("utilisateur",  mapper(row));
        resultat.put("nb_objectifs", row.get("nb_objectifs"));
        return resultat;
    }

    // -------------------------------------------------------------------------
    // MAPPING — lit TOUT depuis la base, y compris role et dateInscription
    // -------------------------------------------------------------------------

    private Utilisateur mapper(Map<String, Object> row) {
        Utilisateur u = new Utilisateur();
        u.setId(        toInt(row.get("id")));
        u.setNom(       (String) row.get("nom"));
        u.setPrenom(    (String) row.get("prenom"));
        u.setEmail(     (String) row.get("email"));
        u.setMotDePasse((String) row.get("mot_de_passe"));

        // role — lu depuis la base, jamais écrit par le code applicatif
        Object role = row.get("role");
        if (role != null) {
            u.setRole(Role.valueOf((String) role));
        }

        // dateInscription — convertie de java.sql.Date vers LocalDate
        Object date = row.get("date_inscription");
        if (date instanceof java.sql.Date) {
            u.setDateInscription(((java.sql.Date) date).toLocalDate());
        }

        return u;
    }

    private int toInt(Object val) {
        return val instanceof Number ? ((Number) val).intValue() : 0;
    }
}