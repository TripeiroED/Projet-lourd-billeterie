package billeterie.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class UserDAO {

    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    // ================= HASH MOT DE PASSE =================

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
            throw new RuntimeException("Erreur hash mot de passe", e);
        }
    }

    // ================= AUTHENTIFICATION =================

    public User authenticate(String username, String password) throws SQLException {

        String sql = "SELECT password, role FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String inputHash = hashPassword(password);

                if (storedHash.equals(inputHash)) {
                    String role = rs.getString("role");
                    return new User(username, role);
                }
            }
        }
        return null;
    }

    // ================= INSCRIPTION SIMPLE (ancienne) =================

    public boolean register(String username, String password, String role) throws SQLException {

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role);

            return stmt.executeUpdate() == 1;
        }
    }

    // ================= INSCRIPTION COMPLÈTE =================

    public boolean register(
            String username,
            String password,
            String role,
            String fullname,
            String email,
            String phone,
            LocalDate birthdate,
            String address) throws SQLException {

        String sql = """
                    INSERT INTO users
                    (username, password, role, fullname, email, phone, birthdate, address, profile_image_path)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role);
            stmt.setString(4, fullname);
            stmt.setString(5, email);
            stmt.setString(6, phone);

            if (birthdate != null) {
                stmt.setDate(7, java.sql.Date.valueOf(birthdate));
            } else {
                stmt.setDate(7, null);
            }

            stmt.setString(8, address);
            stmt.setString(9, null); // image profil par défaut

            return stmt.executeUpdate() == 1;
        }
    }

    // ================= MISE À JOUR PROFIL =================

    public boolean updateProfile(
            String username,
            String fullname,
            String email,
            String phone,
            LocalDate birthdate,
            String address) throws SQLException {

        String sql = """
                    UPDATE users SET
                    fullname = ?,
                    email = ?,
                    phone = ?,
                    birthdate = ?,
                    address = ?
                    WHERE username = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullname);
            stmt.setString(2, email);
            stmt.setString(3, phone);

            if (birthdate != null) {
                stmt.setDate(4, java.sql.Date.valueOf(birthdate));
            } else {
                stmt.setDate(4, null);
            }

            stmt.setString(5, address);
            stmt.setString(6, username);

            return stmt.executeUpdate() == 1;
        }
    }

    // ================= IMAGE PROFIL =================

    public void updateProfileImage(String username, String imagePath) throws SQLException {

        String sql = "UPDATE users SET profile_image_path = ? WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imagePath);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    // ================= FIND ALL USERS =================

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();

        String sql = "SELECT username, role, fullname, email FROM users ORDER BY role DESC, username ASC";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                users.add(new User(username, role, fullname, email));
            }
        }

        return users;
    }

    // ================= FIND PROFILE =================

    public UserProfileData findProfileByUsername(String username) throws SQLException {
        String sql = "SELECT username, fullname, email, phone, birthdate, address, profile_image_path FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fullname = rs.getString("fullname");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    java.sql.Date birthdateSql = rs.getDate("birthdate");
                    String birthdate = birthdateSql != null ? birthdateSql.toLocalDate().toString() : null;
                    String address = rs.getString("address");
                    String profileImagePath = rs.getString("profile_image_path");
                    return new UserProfileData(username, fullname, email, phone, birthdate, address, profileImagePath);
                }
            }
        }
        return null;
    }

    // ================= DELETE USER =================

    public void delete(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    // ================= UPDATE USER ROLE =================

    public boolean updateRole(String username, String newRole) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setString(2, username);
            return stmt.executeUpdate() == 1;
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT username, role, fullname, email FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    String fullname = rs.getString("fullname");
                    String email = rs.getString("email");

                    return new User(username, role, fullname, email);
                }
            }
        }

        return null;
    }
}
