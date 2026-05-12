package orange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
	private static final String URL =  "jdbc:mysql://localhost:3306/gestion_personnel1";
    private static final String USER = "root";
    private static final String PASSWORD = "saq32397";
    private static Connection connection;

    private DataBaseConnection() {
    }

    public static Connection getConnection() {

        try {

            if (connection == null
                    || connection.isClosed()) {

                connection =
                        DriverManager.getConnection(
                                URL,
                                USER,
                                PASSWORD
                        );

                System.out.println(
                        "✓ Connexion à la base de données réussie !"
                );
            }

            return connection;

        } catch (SQLException e) {

            System.err.println(
                    "✗ Erreur de connexion à la base de données !"
            );

            throw new RuntimeException(
                    "Impossible de se connecter à MySQL.",
                    e
            );
        }
    }

    public static void closeConnection() {

        try {

            if (connection != null
                    && !connection.isClosed()) {

                connection.close();

                System.out.println(
                        "✓ Connexion fermée."
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erreur fermeture connexion : "
                            + e.getMessage()
            );
        }
    }
}
