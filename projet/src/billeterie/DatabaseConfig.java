package billeterie;
public class DatabaseConfig {
    public static String getDbUrl() {
        return "jdbc:mysql://localhost:3306/billeterie_db?serverTimezone=UTC";
    }

    public static String getDbUsername() {
        return "root";
    }

    public static String getDbPassword() {
        return "";
    }
}
