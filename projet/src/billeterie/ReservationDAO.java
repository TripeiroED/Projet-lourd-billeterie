package billeterie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private Connection conn;

    public ReservationDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Reservation> findByUsername(String username) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.username, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees " +
                     "FROM reservations r JOIN spectacles s ON r.spectacle_id = s.id WHERE r.username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation res = new Reservation(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("spectacle_id"),
                        rs.getString("spectacle_name"),
                        rs.getString("date"),
                        rs.getInt("places_reservees")
                    );
                    list.add(res);
                }
            }
        }
        return list;
    }

    public boolean reserverPlace(String username, int spectacleId, int placesDemandees) throws SQLException {
        String checkSql = "SELECT places_disponibles, places_reservees, places_totales FROM spectacles WHERE id = ?";
        String insertSql = "INSERT INTO reservations (username, spectacle_id, places_reservees) VALUES (?, ?, ?)";
        String updateSql = "UPDATE spectacles SET places_reservees = places_reservees + ?, places_disponibles = places_disponibles - ? WHERE id = ?";
        
        try {
            conn.setAutoCommit(false); // début transaction

            // Vérifier places disponibles
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, spectacleId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // spectacle non trouvé
                    }
                    int placesDispo = rs.getInt("places_disponibles");
                    if (placesDispo < placesDemandees) {
                        conn.rollback();
                        return false; // pas assez de places
                    }
                }
            }

            // Insérer réservation
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setInt(2, spectacleId);
                insertStmt.setInt(3, placesDemandees);
                insertStmt.executeUpdate();
            }

            // Mettre à jour spectacle
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, placesDemandees);
                updateStmt.setInt(2, placesDemandees);
                updateStmt.setInt(3, spectacleId);
                updateStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}