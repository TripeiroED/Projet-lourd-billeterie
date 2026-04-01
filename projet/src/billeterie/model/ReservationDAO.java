package billeterie.model;

import billeterie.utils.QRCodeGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private final Connection conn;
    private final BilletDAO billetDAO;

    public ReservationDAO(Connection conn) {
        this.conn = conn;
        this.billetDAO = new BilletDAO(conn);
    }

    public List<Reservation> findByUsername(String username) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.username, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees "
                + "FROM reservations r JOIN spectacles s ON r.spectacle_id = s.id WHERE r.username = ?";

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
                            rs.getInt("places_reservees"));
                    list.add(res);
                }
            }
        }

        return list;
    }

    public boolean reserverPlace(String username, int spectacleId, int placesDemandees) throws SQLException {
        String checkSql = "SELECT places_disponibles FROM spectacles WHERE id = ?";
        String insertSql = "INSERT INTO reservations (username, spectacle_id, places_reservees) VALUES (?, ?, ?)";
        String updateSql = "UPDATE spectacles SET places_reservees = places_reservees + ?, places_disponibles = places_disponibles - ? WHERE id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, spectacleId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    int placesDispo = rs.getInt("places_disponibles");
                    if (placesDispo < placesDemandees) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            int reservationId;
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, username);
                insertStmt.setInt(2, spectacleId);
                insertStmt.setInt(3, placesDemandees);
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException("Impossible de recuperer l'identifiant de la reservation.");
                    }
                    reservationId = generatedKeys.getInt(1);
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, placesDemandees);
                updateStmt.setInt(2, placesDemandees);
                updateStmt.setInt(3, spectacleId);
                updateStmt.executeUpdate();
            }

            createBilletsPourReservation(reservationId, spectacleId, username, placesDemandees);

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Billet> findBilletsByReservationId(int reservationId) throws SQLException {
        return billetDAO.findByReservationId(reservationId);
    }

    public boolean deleteReservation(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean annulerReservation(int reservationId) throws SQLException {
        String selectSql = "SELECT spectacle_id, places_reservees FROM reservations WHERE id = ?";
        String updateSql = "UPDATE spectacles SET places_reservees = places_reservees - ?, places_disponibles = places_disponibles + ? WHERE id = ?";
        String deleteSql = "DELETE FROM reservations WHERE id = ?";

        try {
            conn.setAutoCommit(false);

            int spectacleId;
            int placesRes;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, reservationId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    spectacleId = rs.getInt("spectacle_id");
                    placesRes = rs.getInt("places_reservees");
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, placesRes);
                updateStmt.setInt(2, placesRes);
                updateStmt.setInt(3, spectacleId);
                updateStmt.executeUpdate();
            }

            billetDAO.deleteByReservationId(reservationId);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, reservationId);
                deleteStmt.executeUpdate();
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

    private void createBilletsPourReservation(int reservationId, int spectacleId, String username, int nombreBillets)
            throws SQLException {
        Path qrDirectory = Path.of("projet", "qrcodes");

        try {
            Files.createDirectories(qrDirectory);
        } catch (IOException e) {
            throw new SQLException("Impossible de creer le dossier des QR codes.", e);
        }

        for (int i = 1; i <= nombreBillets; i++) {
            String qrContent = "reservation=" + reservationId
                    + ";spectacle=" + spectacleId
                    + ";utilisateur=" + username
                    + ";billet=" + i;
            Path qrPath = qrDirectory.resolve("reservation_" + reservationId + "_billet_" + i + ".png");
            String qrCodePath = QRCodeGenerator.generateQRCode(qrContent, qrPath.toString());

            if (qrCodePath == null) {
                throw new SQLException("La generation du QR code a echoue pour la reservation " + reservationId + ".");
            }

            billetDAO.createBillet(new Billet(reservationId, qrCodePath));
        }
    }
}
