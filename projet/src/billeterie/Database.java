package billeterie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Exemple d'URL de connexion MySQL :
    // "jdbc:mysql://hostname:port/databaseName"
    private static final String URL = "jdbc:mysql://localhost:3306/billeterie_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Charge explicitement le driver MySQL
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL non trouvé !");
            e.printStackTrace();
            throw new SQLException("Impossible de charger le driver MySQL", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
