package billeterie.model;

import billeterie.utils.QRCodeGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private final Connection conn;
    private final BilletDAO billetDAO;
    private Boolean reservationsHasUserIdColumn;
    private Boolean reservationsHasUsernameColumn;

    public ReservationDAO(Connection conn) {
        this.conn = conn;
        this.billetDAO = new BilletDAO(conn);
    }

    public List<Reservation> findForUser(String username) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        boolean hasUserId = hasReservationUserIdColumn();
        boolean hasUsername = hasReservationUsernameColumn();

        if (!hasUserId && !hasUsername) {
            throw new SQLException("La table reservations doit contenir user_id ou username.");
        }

        Integer userId = null;
        if (hasUserId) {
            userId = findUserIdByUsername(username);
            if (userId == null && !hasUsername) {
                return list;
            }
        }

        String sql;
        if (hasUserId && hasUsername && userId != null) {
            sql = "SELECT r.id, COALESCE(r.user_id, ?) AS user_id, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees "
                    + "FROM reservations r JOIN spectacles s ON r.spectacle_id = s.id "
                    + "WHERE r.user_id = ? OR (r.user_id IS NULL AND r.username = ?)";
        } else if (hasUserId && userId != null) {
            sql = "SELECT r.id, r.user_id, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees "
                    + "FROM reservations r JOIN spectacles s ON r.spectacle_id = s.id WHERE r.user_id = ?";
        } else {
            sql = "SELECT r.id, u.id AS user_id, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees "
                    + "FROM reservations r JOIN spectacles s ON r.spectacle_id = s.id "
                    + "LEFT JOIN users u ON r.username = u.username WHERE r.username = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasUserId && hasUsername && userId != null) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                stmt.setString(3, username);
            } else if (hasUserId && userId != null) {
                stmt.setInt(1, userId);
            } else {
                stmt.setString(1, username);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int reservationUserId = rs.getInt("user_id");
                    if (rs.wasNull() && userId != null) {
                        reservationUserId = userId;
                    }

                    Reservation reservation = new Reservation(
                            rs.getInt("id"),
                            reservationUserId,
                            rs.getInt("spectacle_id"),
                            rs.getString("spectacle_name"),
                            rs.getString("date"),
                            rs.getInt("places_reservees"));
                    list.add(reservation);
                }
            }
        }

        return list;
    }

    public List<Reservation> findByUsername(String username) throws SQLException {
        return findForUser(username);
    }

    public boolean reserverPlace(String username, int spectacleId, int placesDemandees) throws SQLException {
        String checkSql = "SELECT places_disponibles FROM spectacles WHERE id = ?";
        String updateSql = "UPDATE spectacles SET places_reservees = places_reservees + ?, places_disponibles = places_disponibles - ? WHERE id = ?";
        boolean hasUserId = hasReservationUserIdColumn();
        boolean hasUsername = hasReservationUsernameColumn();

        if (!hasUserId && !hasUsername) {
            throw new SQLException("La table reservations doit contenir user_id ou username.");
        }

        Integer userId = null;
        if (hasUserId) {
            userId = findUserIdByUsername(username);
            if (userId == null) {
                return false;
            }
        }

        String insertSql;
        if (hasUserId && hasUsername) {
            insertSql = "INSERT INTO reservations (user_id, username, spectacle_id, places_reservees) VALUES (?, ?, ?, ?)";
        } else if (hasUserId) {
            insertSql = "INSERT INTO reservations (user_id, spectacle_id, places_reservees) VALUES (?, ?, ?)";
        } else {
            insertSql = "INSERT INTO reservations (username, spectacle_id, places_reservees) VALUES (?, ?, ?)";
        }

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
                if (hasUserId && hasUsername) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, username);
                    insertStmt.setInt(3, spectacleId);
                    insertStmt.setInt(4, placesDemandees);
                } else if (hasUserId) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, spectacleId);
                    insertStmt.setInt(3, placesDemandees);
                } else {
                    insertStmt.setString(1, username);
                    insertStmt.setInt(2, spectacleId);
                    insertStmt.setInt(3, placesDemandees);
                }
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

    private Integer findUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }

    private boolean hasReservationUserIdColumn() throws SQLException {
        if (reservationsHasUserIdColumn == null) {
            reservationsHasUserIdColumn = hasColumn("reservations", "user_id");
        }
        return reservationsHasUserIdColumn;
    }

    private boolean hasReservationUsernameColumn() throws SQLException {
        if (reservationsHasUsernameColumn == null) {
            reservationsHasUsernameColumn = hasColumn("reservations", "username");
        }
        return reservationsHasUsernameColumn;
    }

    private boolean hasColumn(String tableName, String columnName) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();

        try (ResultSet rs = metadata.getColumns(conn.getCatalog(), null, tableName, columnName)) {
            if (rs.next()) {
                return true;
            }
        }

        try (ResultSet rs = metadata.getColumns(conn.getCatalog(), null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }

    public List<Reservation> findAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();

        String sql = """
                    SELECT r.id, r.user_id, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees
                    FROM reservations r
                    JOIN spectacles s ON r.spectacle_id = s.id
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("spectacle_id"),
                        rs.getString("spectacle_name"),
                        rs.getString("date"),
                        rs.getInt("places_reservees")));
            }
        }

        return list;
    }

    public void updateReservation(int id, int newPlaces) throws SQLException {

        // 1. récupérer réservation existante
        Reservation r = getReservationById(id);

        if (r == null) {
            throw new SQLException("Réservation introuvable");
        }

        int oldPlaces = r.getNombrePlaces();
        int diff = newPlaces - oldPlaces;

        // 2. update réservation
        String sql = "UPDATE reservations SET places_reservees = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newPlaces);
            ps.setInt(2, id);
            ps.executeUpdate();
        }

        // 3. ajuster places spectacle
        String updateSpectacle = "UPDATE spectacles SET " +
                "places_reservees = places_reservees + ?, " +
                "places_disponibles = places_disponibles - ? " +
                "WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(updateSpectacle)) {
            ps.setInt(1, diff);
            ps.setInt(2, diff);
            ps.setInt(3, r.getSpectacleId());
            ps.executeUpdate();
        }

        // 4. recréer billets
        billetDAO.deleteByReservationId(id);

        String username = findUsernameByUserId(r.getUserId());

        createBilletsPourReservation(
                id,
                r.getSpectacleId(),
                username,
                newPlaces);
    }

    public Reservation getReservationById(int id) throws SQLException {
        String sql = """
                    SELECT r.id, r.user_id, r.spectacle_id, s.nom AS spectacle_name, s.date, r.places_reservees
                    FROM reservations r
                    JOIN spectacles s ON r.spectacle_id = s.id
                    WHERE r.id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("spectacle_id"),
                            rs.getString("spectacle_name"),
                            rs.getString("date"),
                            rs.getInt("places_reservees"));
                }
            }
        }

        return null;
    }

    private String findUsernameByUserId(int userId) throws SQLException {
        String sql = "SELECT username FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }

}
