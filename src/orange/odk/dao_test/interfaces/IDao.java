package orange.odk.dao_test.interfaces;

import java.util.List;
import java.util.Map;
import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.dao_test.query.QueryBuilder;

/**
 * IGenericDao — contrat abstrait du moteur SQL.
 * Les Repositories dependent de cette interface, jamais de l'implementation concrète.
 * Permet de switcher l'implementation (JDBC, in-memory pour les tests, etc.)
 * sans toucher aux Repositories.
 */
public interface IDao {

    // =========================================================================
    // CRUD SIMPLE
    // =========================================================================

    /**
     * Insère une ligne dans une table.
     *
     * @param table   nom de la table cible
     * @param donnees map colonne → valeur
     * @return l'id genere
     */
    int insert(String table, Map<String, Object> donnees) throws DaoException;

    /**
     * Recupère une ligne par sa cle primaire.
     *
     * @param table      nom de la table
     * @param colonneId  nom de la colonne cle (ex: "id")
     * @param valeurId   valeur de la cle
     * @return la ligne en Map, ou null si introuvable
     */
    Map<String, Object> findById(String table, String colonneId, Object valeurId) throws DaoException;

    /**
     * Recupère toutes les lignes d'une table.
     */
    List<Map<String, Object>> findAll(String table) throws DaoException;

    /**
     * Recupère toutes les lignes correspondant à un filtre simple (colonne = valeur).
     */
    List<Map<String, Object>> findBy(String table, String colonne, Object valeur) throws DaoException;

    /**
     * Recupère toutes les lignes correspondant à plusieurs filtres (AND).
     *
     * @param filtres map colonne → valeur, tous combines avec AND
     */
    List<Map<String, Object>> findByMultiple(String table, Map<String, Object> filtres) throws DaoException;

    /**
     * Met à jour une ligne par sa cle primaire.
     *
     * @param table     nom de la table
     * @param colonneId colonne cle primaire
     * @param valeurId  valeur de la cle
     * @param donnees   map colonne → nouvelle valeur
     */
    void update(String table, String colonneId, Object valeurId, Map<String, Object> donnees) throws DaoException;

    /**
     * Supprime une ligne par sa cle primaire.
     *
     * @return true si au moins une ligne supprimee
     */
    boolean delete(String table, String colonneId, Object valeurId) throws DaoException;

    /**
     * Verifie l'existence d'une ligne par sa cle primaire.
     */
    boolean exists(String table, String colonneId, Object valeurId) throws DaoException;

    /**
     * Compte toutes les lignes d'une table.
     */
    int count(String table) throws DaoException;

    /**
     * Compte les lignes correspondant à un filtre simple.
     */
    int countBy(String table, String colonne, Object valeur) throws DaoException;

    // =========================================================================
    // REQUÊTES LIBRES — jointures, agregations
    // =========================================================================

    /**
     * Execute une requête SELECT construite par un QueryBuilder.
     * Gère les jointures, GROUP BY, HAVING, ORDER BY, LIMIT.
     *
     * @param query le QueryBuilder configure par le Repository
     * @return liste de lignes sous forme de Map<String, Object>
     */
    List<Map<String, Object>> executeQuery(QueryBuilder query) throws DaoException;

    /**
     * Execute un QueryBuilder et retourne uniquement le premier resultat (ou null).
     */
    Map<String, Object> executeQueryOne(QueryBuilder query) throws DaoException;

    /**
     * Execute un QueryBuilder de type COUNT et retourne la valeur entière.
     */
    int executeCount(QueryBuilder query) throws DaoException;
}