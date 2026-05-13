package orange.odk.dao_test.connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC — un seul point de connexion à la base de donnees.
 * Toute la couche DAO passe par ici.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/gestion_objectifs";
    private static final String USER     = "morbin";
    private static final String PASSWORD = "programmerBOSS17789!";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}