package billeterie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    // Méthode pour hasher le mot de passe en SHA-256 (à améliorer plus tard)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public User authenticate(String username, String password) throws SQLException {
    String sql = "SELECT password, role FROM users WHERE username = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String storedPasswordHash = rs.getString("password");
            String inputPasswordHash = hashPassword(password);
            if (storedPasswordHash.equals(inputPasswordHash)) {
                String role = rs.getString("role");
                return new User(username, role);
            }
        }
        return null; // échec auth
    }
}

    public boolean register(String username, String password, String role) throws SQLException {
    String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        stmt.setString(2, hashPassword(password));
        stmt.setString(3, role);
        int affected = stmt.executeUpdate();
        return affected == 1;
    }
}

}
