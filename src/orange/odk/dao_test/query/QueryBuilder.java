package orange.odk.dao_test.query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QueryBuilder — construit une requête SQL SELECT dynamiquement.
 *
 * Le DAO ne sait pas ce qu'il execute.
 * Le Repository ne sait pas comment c'est execute.
 * Chacun son rôle.
 *
 * Utilisation :
 *   new QueryBuilder()
 *       .from("objectifs", "o")
 *       .join("utilisateurs", "u", "o.utilisateur_id = u.id")
 *       .where("o.utilisateur_id = ?", userId)
 *       .select("o.*", "u.nom AS utilisateur_nom")
 *       .orderBy("o.date_debut DESC")
 *       .limit(10);
 */
public class QueryBuilder {

    // Colonnes à selectionner (SELECT ...)
    private final List<String> colonnes = new ArrayList<>();

    // Table principale (FROM ...)
    private String tableFrom;
    private String aliasFrom;

    // Jointures
    private final List<String> jointures = new ArrayList<>();

    // Conditions WHERE
    private final List<String> conditions = new ArrayList<>();

    // Paramètres des conditions (pour PreparedStatement)
    private final List<Object> parametres = new ArrayList<>();

    // GROUP BY
    private final List<String> groupBy = new ArrayList<>();

    // HAVING
    private String having;
    private final List<Object> parametresHaving = new ArrayList<>();

    // ORDER BY
    private final List<String> orderBy = new ArrayList<>();

    // LIMIT / OFFSET
    private Integer limite;
    private Integer offset;

    // -------------------------------------------------------------------------
    // SELECT
    // -------------------------------------------------------------------------

    /**
     * Definit les colonnes à selectionner.
     * Accepte plusieurs colonnes : select("o.*", "u.nom AS utilisateur_nom")
     * Par defaut si non appele : SELECT *
     */
    public QueryBuilder select(String... cols) {
        colonnes.addAll(Arrays.asList(cols));
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM
    // -------------------------------------------------------------------------

    /**
     * Definit la table principale.
     *
     * @param table nom de la table
     * @param alias alias SQL (ex: "o" pour "objectifs o")
     */
    public QueryBuilder from(String table, String alias) {
        this.tableFrom = table;
        this.aliasFrom = alias;
        return this;
    }

    /**
     * Definit la table principale sans alias.
     */
    public QueryBuilder from(String table) {
        return from(table, null);
    }

    // -------------------------------------------------------------------------
    // JOINTURES
    // -------------------------------------------------------------------------

    /**
     * INNER JOIN — ne retourne que les lignes qui ont une correspondance.
     *
     * @param table     table à joindre
     * @param alias     alias de la table jointe
     * @param condition condition ON (ex: "o.utilisateur_id = u.id")
     */
    public QueryBuilder join(String table, String alias, String condition) {
        jointures.add("INNER JOIN " + table + " " + alias + " ON " + condition);
        return this;
    }

    /**
     * LEFT JOIN — retourne toutes les lignes de gauche, même sans correspondance.
     */
    public QueryBuilder leftJoin(String table, String alias, String condition) {
        jointures.add("LEFT JOIN " + table + " " + alias + " ON " + condition);
        return this;
    }

    /**
     * RIGHT JOIN.
     */
    public QueryBuilder rightJoin(String table, String alias, String condition) {
        jointures.add("RIGHT JOIN " + table + " " + alias + " ON " + condition);
        return this;
    }

    // -------------------------------------------------------------------------
    // WHERE
    // -------------------------------------------------------------------------

    /**
     * Ajoute une condition WHERE (combinees avec AND).
     *
     * @param condition  expression SQL avec placeholders ? (ex: "o.statut = ?")
     * @param valeurs    valeurs correspondant aux ? dans la condition
     */
    public QueryBuilder where(String condition, Object... valeurs) {
        conditions.add(condition);
        parametres.addAll(Arrays.asList(valeurs));
        return this;
    }

    // -------------------------------------------------------------------------
    // GROUP BY / HAVING
    // -------------------------------------------------------------------------

    /**
     * Ajoute une colonne au GROUP BY.
     */
    public QueryBuilder groupBy(String... colonnes) {
        groupBy.addAll(Arrays.asList(colonnes));
        return this;
    }

    /**
     * Ajoute une condition HAVING (après GROUP BY).
     */
    public QueryBuilder having(String condition, Object... valeurs) {
        this.having = condition;
        parametresHaving.addAll(Arrays.asList(valeurs));
        return this;
    }

    // -------------------------------------------------------------------------
    // ORDER BY
    // -------------------------------------------------------------------------

    /**
     * Ajoute un critère de tri.
     * Exemple : orderBy("o.date_debut DESC")
     */
    public QueryBuilder orderBy(String... criteres) {
        orderBy.addAll(Arrays.asList(criteres));
        return this;
    }

    // -------------------------------------------------------------------------
    // LIMIT / OFFSET
    // -------------------------------------------------------------------------

    public QueryBuilder limit(int limite) {
        this.limite = limite;
        return this;
    }

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    // -------------------------------------------------------------------------
    // BUILD — assemblage final du SQL
    // -------------------------------------------------------------------------

    /**
     * Construit la chaîne SQL finale.
     * Appele par le GenericDao au moment de l'execution.
     */
    public String buildSql() {
        if (tableFrom == null) {
            throw new IllegalStateException("QueryBuilder : from() est obligatoire.");
        }

        StringBuilder sql = new StringBuilder();

        // SELECT
        sql.append("SELECT ");
        sql.append(colonnes.isEmpty() ? "*" : String.join(", ", colonnes));

        // FROM
        sql.append(" FROM ").append(tableFrom);
        if (aliasFrom != null) sql.append(" ").append(aliasFrom);

        // JOIN
        for (String jointure : jointures) {
            sql.append(" ").append(jointure);
        }

        // WHERE
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // GROUP BY
        if (!groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", groupBy));
        }

        // HAVING
        if (having != null) {
            sql.append(" HAVING ").append(having);
        }

        // ORDER BY
        if (!orderBy.isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", orderBy));
        }

        // LIMIT / OFFSET
        if (limite != null) {
            sql.append(" LIMIT ").append(limite);
        }
        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }

        return sql.toString();
    }

    /**
     * Retourne tous les paramètres dans l'ordre (WHERE + HAVING).
     * Utilise par le GenericDao pour remplir le PreparedStatement.
     */
    public List<Object> getParametres() {
        List<Object> tous = new ArrayList<>(parametres);
        tous.addAll(parametresHaving);
        return tous;
    }

    @Override
    public String toString() {
        return buildSql();
    }
}