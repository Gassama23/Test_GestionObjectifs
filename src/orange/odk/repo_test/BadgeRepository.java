package orange.odk.repo_test;


import java.sql.SQLException;
import java.util.*;
import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.dao_test.impl.DaoImpl;
import orange.odk.dao_test.interfaces.IDao;
import orange.odk.dao_test.query.QueryBuilder;
import orange.odk.models_test.Badge;

/**
 * BadgeRepository — accès aux donnees des badges et de leur attribution.
 *
 * Depend de l'interface IGenericDao, jamais de l'implementation concrète.
 */
public class BadgeRepository {

    private static final String TABLE         = "badges";
    private static final String TABLE_LIEN    = "utilisateur_badges";
    private static final String PK            = "id";

    private final IDao dao;

    public BadgeRepository() throws SQLException {
        this.dao = new DaoImpl();
    }

    /** Constructeur pour injection de dependance (tests, mock…) */
    public BadgeRepository(IDao dao) {
        this.dao = dao;
    }

    // -------------------------------------------------------------------------
    // CRUD — catalogue des badges
    // -------------------------------------------------------------------------

    public Badge create(Badge b) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("nom",         b.getNom());
        donnees.put("description", b.getDescription());

        int id = dao.insert(TABLE, donnees);
        b.setId(id);
        return b;
    }

    public Badge findById(int id) throws DaoException {
        Map<String, Object> row = dao.findById(TABLE, PK, id);
        return row == null ? null : mapper(row);
    }

    public List<Badge> findAll() throws DaoException {
        List<Badge> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findAll(TABLE)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public Badge update(Badge b) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("nom",         b.getNom());
        donnees.put("description", b.getDescription());

        dao.update(TABLE, PK, b.getId(), donnees);
        return b;
    }

    public boolean delete(int id) throws DaoException {
        return dao.delete(TABLE, PK, id);
    }

    // -------------------------------------------------------------------------
    // ATTRIBUTION — table utilisateur_badges
    // -------------------------------------------------------------------------

    /**
     * Attribue un badge à un utilisateur.
     */
    public void attribuer(int utilisateurId, int badgeId) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("utilisateur_id",  utilisateurId);
        donnees.put("badge_id",        badgeId);
        donnees.put("date_obtention",  new java.sql.Date(System.currentTimeMillis()));

        dao.insert(TABLE_LIEN, donnees);
    }

    /**
     * Retire un badge à un utilisateur.
     */
    public boolean retirer(int utilisateurId, int badgeId) throws DaoException {
        // DELETE avec deux colonnes — on utilise findByMultiple + delete manuel via QueryBuilder
        Map<String, Object> filtres = new LinkedHashMap<>();
        filtres.put("utilisateur_id", utilisateurId);
        filtres.put("badge_id",       badgeId);

        // On verifie d'abord l'existence
        List<Map<String, Object>> rows = dao.findByMultiple(TABLE_LIEN, filtres);
        if (rows.isEmpty()) return false;

        // Suppression via cle composite simulee (on delete par utilisateur_id ET badge_id)
        // Le GenericDao ne gère pas les cles composites nativement,
        // on passe donc par executeQuery avec un QueryBuilder de DELETE custom.
        // Alternative propre : on delègue au GenericDao via une requête directe.
        QueryBuilder query = new QueryBuilder()
                .from(TABLE_LIEN)
                .where("utilisateur_id = ? AND badge_id = ?", utilisateurId, badgeId);

        // On re-utilise findByMultiple + delete sur l'id si disponible
        // Ici la table n'a pas d'id auto, donc on utilise une requête DELETE directe
        // via une extension du GenericDao (deleteWhere).
        // Pour rester simple, on supprime en passant par JDBC directement dans le repo.
        throw new UnsupportedOperationException(
                "deleteWhere multi-colonnes non encore implemente dans GenericDao. " +
                        "Ajouter GenericDao.deleteWhere(String table, Map<String, Object> conditions)."
        );
    }

    /**
     * Verifie si un utilisateur possède dejà un badge.
     */
    public boolean utilisateurPossedeBadge(int utilisateurId, int badgeId) throws DaoException {
        Map<String, Object> filtres = new LinkedHashMap<>();
        filtres.put("utilisateur_id", utilisateurId);
        filtres.put("badge_id",       badgeId);

        return !dao.findByMultiple(TABLE_LIEN, filtres).isEmpty();
    }

    // -------------------------------------------------------------------------
    // REQUÊTES AVEC JOINTURES (QueryBuilder)
    // -------------------------------------------------------------------------

    /**
     * Recupère tous les badges d'un utilisateur avec la date d'obtention.
     * JOIN utilisateur_badges → badges
     */
    public List<Badge> findByUtilisateur(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("b.*", "ub.date_obtention")
                .from(TABLE, "b")
                .join(TABLE_LIEN, "ub", "ub.badge_id = b.id")
                .where("ub.utilisateur_id = ?", utilisateurId)
                .orderBy("ub.date_obtention DESC");

        List<Badge> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.executeQuery(query)) {
            Badge b = mapper(row);
            b.setDateObtention(toDate(row.get("date_obtention")));
            liste.add(b);
        }
        return liste;
    }

    /**
     * Compte le nombre de badges d'un utilisateur.
     */
    public int countBadgesUtilisateur(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("COUNT(*) AS total")
                .from(TABLE_LIEN)
                .where("utilisateur_id = ?", utilisateurId);

        return dao.executeCount(query);
    }

    /**
     * Recupère les utilisateurs ayant obtenu un badge donne.
     * JOIN utilisateur_badges → utilisateurs
     */
    public List<Map<String, Object>> findUtilisateursParBadge(int badgeId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("u.id", "u.nom", "u.email", "ub.date_obtention")
                .from("utilisateurs", "u")
                .join(TABLE_LIEN, "ub", "ub.utilisateur_id = u.id")
                .where("ub.badge_id = ?", badgeId)
                .orderBy("ub.date_obtention ASC");

        return dao.executeQuery(query);
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    private Badge mapper(Map<String, Object> row) {
        Badge b = new Badge();
        b.setId(          toInt(row.get("id")));
        b.setNom(         (String) row.get("nom"));
        b.setDescription( (String) row.get("description"));
        return b;
    }

    private int toInt(Object val) {
        return val instanceof Number ? ((Number) val).intValue() : 0;
    }

    private java.util.Date toDate(Object val) {
        if (val instanceof java.sql.Date) return new java.util.Date(((java.sql.Date) val).getTime());
        if (val instanceof java.util.Date) return (java.util.Date) val;
        return null;
    }
}