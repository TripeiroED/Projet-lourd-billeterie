package billeterie.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BilletDAO {
    private final Connection connection;

    public BilletDAO(Connection connection) {
        this.connection = connection;
    }

    public void createBillet(Billet billet) throws SQLException {
        String sql = "INSERT INTO billets (reservation_id, qr_code, statut) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, billet.getReservationId());
            stmt.setString(2, billet.getQrCode());
            stmt.setString(3, billet.getStatut());
            stmt.executeUpdate();
        }
    }

    public List<Billet> findByReservationId(int reservationId) throws SQLException {
        List<Billet> billets = new ArrayList<>();
        String sql = "SELECT id, reservation_id, qr_code, statut FROM billets WHERE reservation_id = ? ORDER BY id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billets.add(new Billet(
                            rs.getInt("id"),
                            rs.getInt("reservation_id"),
                            rs.getString("qr_code"),
                            rs.getString("statut")));
                }
            }
        }

        return billets;
    }

    public void deleteByReservationId(int reservationId) throws SQLException {
        String sql = "DELETE FROM billets WHERE reservation_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.executeUpdate();
        }
    }
}
