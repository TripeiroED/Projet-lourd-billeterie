package billeterie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpectacleDAO {
    private Connection conn;

    public SpectacleDAO(Connection conn) {
        this.conn = conn;
    }

    // Récupère les 3 spectacles à venir les plus récents à la une
    public List<Spectacle> findFeatured() throws SQLException {
        List<Spectacle> list = new ArrayList<>();
        String sql = "SELECT * FROM spectacles WHERE date >= CURRENT_DATE ORDER BY date ASC LIMIT 3";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Spectacle s = new Spectacle(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("date"),
                    rs.getString("lieu"),
                    rs.getDouble("prix"),
                    rs.getInt("places_disponibles")
                );
                list.add(s);
            }
        }
        return list;
    }

    // Compte total des places disponibles pour tous les spectacles à venir
    public int countTotalFreeSeats() throws SQLException {
        String sql = "SELECT SUM(places_disponibles) AS total FROM spectacles WHERE date >= CURRENT_DATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // Récupère tous les spectacles à venir
    public List<Spectacle> findAll() throws SQLException {
        List<Spectacle> list = new ArrayList<>();
        String sql = "SELECT * FROM spectacles WHERE date >= CURRENT_DATE ORDER BY date ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Spectacle s = new Spectacle(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("date"),
                    rs.getString("lieu"),
                    rs.getDouble("prix"),
                    rs.getInt("places_disponibles")
                );
                list.add(s);
            }
        }
        return list;
    }

    public boolean add(Spectacle s) throws SQLException {
        String sql = "INSERT INTO spectacles (nom, date, lieu, prix, places_disponibles) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNom());
            stmt.setString(2, s.getDate());
            stmt.setString(3, s.getLieu());
            stmt.setDouble(4, s.getPrix());
            stmt.setInt(5, s.getPlacesDisponibles());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean update(Spectacle s) throws SQLException {
        String sql = "UPDATE spectacles SET nom=?, date=?, lieu=?, prix=?, places_disponibles=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNom());
            stmt.setString(2, s.getDate());
            stmt.setString(3, s.getLieu());
            stmt.setDouble(4, s.getPrix());
            stmt.setInt(5, s.getPlacesDisponibles());
            stmt.setInt(6, s.getId());
            return stmt.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM spectacles WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }
}
