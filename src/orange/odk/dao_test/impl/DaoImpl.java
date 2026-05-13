package orange.odk.dao_test.impl;


import java.sql.*;
import java.util.*;
import orange.odk.dao_test.connexion.DatabaseConnection;
import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.dao_test.interfaces.IDao;
import orange.odk.dao_test.query.QueryBuilder;

/**
 * GenericDao — moteur SQL de l'application.

 * Deux modes :
 *   1. CRUD simple     → methodes dediees (insert, findById, update, delete…)
 *   2. Requêtes libres → executeQuery(QueryBuilder) pour jointures et agregations

 * Ne connaît aucune table, aucun modèle, aucune règle metier.
 * Reçoit tout en paramètre, retourne des Map<String, Object>.
 */
public  class DaoImpl implements IDao {

    private final Connection conn;

    public DaoImpl() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // =========================================================================
    // CRUD SIMPLE — une seule table à la fois
    // =========================================================================

    /**
     * INSERT — insère une ligne dans une table.
     *
     * @param table   nom de la table cible
     * @param donnees map colonne → valeur
     * @return l'id genere
     */
    public int insert(String table, Map<String, Object> donnees) throws DaoException {
        List<String> colonnes = new ArrayList<>(donnees.keySet());
        String cols         = String.join(", ", colonnes);
        String placeholders = String.join(", ", Collections.nCopies(colonnes.size(), "?"));
        String sql          = "INSERT INTO " + table + " (" + cols + ") VALUES (" + placeholders + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, colonnes, donnees, 1);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new DaoException("Aucun ID genere pour la table : " + table);

        } catch (SQLException e) {
            throw new DaoException("Erreur INSERT sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * SELECT par id — recupère une ligne par sa cle primaire.
     *
     * @param table      nom de la table
     * @param colonneId  nom de la colonne cle (ex: "id")
     * @param valeurId   valeur de la cle
     * @return la ligne en Map, ou null si introuvable
     */
    public Map<String, Object> findById(String table, String colonneId, Object valeurId) throws DaoException {
        List<Map<String, Object>> resultats = findBy(table, colonneId, valeurId);
        return resultats.isEmpty() ? null : resultats.get(0);
    }

    /**
     * SELECT * — recupère toutes les lignes d'une table.
     */
    public List<Map<String, Object>> findAll(String table) throws DaoException {
        String sql = "SELECT * FROM " + table;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return extraire(ps.executeQuery());
        } catch (SQLException e) {
            throw new DaoException("Erreur findAll sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * SELECT avec filtre simple — colonne = valeur.
     */
    public List<Map<String, Object>> findBy(String table, String colonne, Object valeur) throws DaoException {
        String sql = "SELECT * FROM " + table + " WHERE " + colonne + " = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, valeur);
            return extraire(ps.executeQuery());
        } catch (SQLException e) {
            throw new DaoException("Erreur findBy sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * SELECT avec plusieurs filtres (AND).
     *
     * @param filtres map colonne → valeur, tous combines avec AND
     */
    public List<Map<String, Object>> findByMultiple(String table, Map<String, Object> filtres) throws DaoException {
        List<String> colonnes = new ArrayList<>(filtres.keySet());
        String conditions = String.join(" AND ", colonnes.stream()
                .map(c -> c + " = ?")
                .toArray(String[]::new));
        String sql = "SELECT * FROM " + table + " WHERE " + conditions;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            remplir(ps, colonnes, filtres, 1);
            return extraire(ps.executeQuery());
        } catch (SQLException e) {
            throw new DaoException("Erreur findByMultiple sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * UPDATE — met à jour une ligne par sa cle primaire.
     *
     * @param table     nom de la table
     * @param colonneId colonne cle primaire
     * @param valeurId  valeur de la cle
     * @param donnees   map colonne → nouvelle valeur
     */
    public void update(String table, String colonneId, Object valeurId, Map<String, Object> donnees) throws DaoException {
        List<String> colonnes = new ArrayList<>(donnees.keySet());
        String setClause = String.join(", ", colonnes.stream()
                .map(c -> c + " = ?")
                .toArray(String[]::new));
        String sql = "UPDATE " + table + " SET " + setClause + " WHERE " + colonneId + " = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            remplir(ps, colonnes, donnees, 1);
            ps.setObject(colonnes.size() + 1, valeurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erreur UPDATE sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * DELETE — supprime une ligne par sa cle primaire.
     *
     * @return true si au moins une ligne supprimee
     */
    public boolean delete(String table, String colonneId, Object valeurId) throws DaoException {
        String sql = "DELETE FROM " + table + " WHERE " + colonneId + " = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, valeurId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur DELETE sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * EXISTS — verifie l'existence d'une ligne.
     */
    public boolean exists(String table, String colonneId, Object valeurId) throws DaoException {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + colonneId + " = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, valeurId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur exists sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * COUNT — compte toutes les lignes d'une table.
     */
    public int count(String table) throws DaoException {
        String sql = "SELECT COUNT(*) FROM " + table;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur count sur " + table + " : " + e.getMessage(), e);
        }
    }

    /**
     * COUNT avec filtre — compte les lignes correspondant à une condition.
     */
    public int countBy(String table, String colonne, Object valeur) throws DaoException {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + colonne + " = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, valeur);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur countBy sur " + table + " : " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // REQUÊTES LIBRES — jointures, agregations, tout ce que QueryBuilder permet
    // =========================================================================

    /**
     * Execute une requête SELECT construite par un QueryBuilder.
     * Gère les jointures, GROUP BY, HAVING, ORDER BY, LIMIT.
     *
     * @param query le QueryBuilder configure par le Repository
     * @return liste de lignes sous forme de Map<String, Object>
     */
    public List<Map<String, Object>> executeQuery(QueryBuilder query) throws DaoException {
        String sql = query.buildSql();
        List<Object> params = query.getParametres();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return extraire(ps.executeQuery());
        } catch (SQLException e) {
            throw new DaoException("Erreur executeQuery : " + e.getMessage() + " | SQL: " + sql, e);
        }
    }

    /**
     * Execute un QueryBuilder et retourne uniquement le premier resultat (ou null).
     */
    public Map<String, Object> executeQueryOne(QueryBuilder query) throws DaoException {
        List<Map<String, Object>> resultats = executeQuery(query);
        return resultats.isEmpty() ? null : resultats.get(0);
    }

    /**
     * Execute un QueryBuilder de type COUNT et retourne la valeur entière.
     *
     * Exemple :
     *   new QueryBuilder()
     *       .select("COUNT(*) AS total")
     *       .from("progressions", "p")
     *       .where("p.objectif_id = ? AND p.reussi = ?", objectifId, true)
     */
    public int executeCount(QueryBuilder query) throws DaoException {
        Map<String, Object> resultat = executeQueryOne(query);
        if (resultat == null || resultat.isEmpty()) return 0;

        // Prend la première valeur numerique (COUNT(*), COUNT(id), etc.)
        Object valeur = resultat.values().iterator().next();
        if (valeur instanceof Number) return ((Number) valeur).intValue();
        return 0;
    }

    // =========================================================================
    // MeTHODES PRIVeES
    // =========================================================================

    /**
     * Transforme un ResultSet en liste de Map<String, Object>.
     * Utilise getColumnLabel pour respecter les alias SQL (AS nom_alias).
     */
    private List<Map<String, Object>> extraire(ResultSet rs) throws SQLException {
        List<Map<String, Object>> resultats = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int nbCols = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> ligne = new LinkedHashMap<>();
            for (int i = 1; i <= nbCols; i++) {
                ligne.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            resultats.add(ligne);
        }
        return resultats;
    }

    /**
     * Remplit les paramètres d'un PreparedStatement à partir d'une Map ordonnee.
     *
     * @param ps       PreparedStatement à remplir
     * @param colonnes ordre des colonnes (correspond à l'ordre des ?)
     * @param donnees  map colonne → valeur
     * @param debut    index de depart (1 en SQL)
     */
    private void remplir(PreparedStatement ps, List<String> colonnes,
                         Map<String, Object> donnees, int debut) throws SQLException {
        for (int i = 0; i < colonnes.size(); i++) {
            ps.setObject(debut + i, donnees.get(colonnes.get(i)));
        }
    }
}