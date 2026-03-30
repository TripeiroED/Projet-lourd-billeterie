package billeterie.controller;

import billeterie.model.Spectacle;
import billeterie.model.SpectacleDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SpectacleController {
    private final SpectacleDAO spectacleDAO;

    public SpectacleController(Connection conn) {
        this.spectacleDAO = new SpectacleDAO(conn);
    }

    public List<Spectacle> findAll() throws SQLException {
        return spectacleDAO.findAll();
    }

    public List<Spectacle> findFeatured() throws SQLException {
        return spectacleDAO.findFeatured();
    }

    public int countTotalFreeSeats() throws SQLException {
        return spectacleDAO.countTotalFreeSeats();
    }

    public boolean addSpectacle(Spectacle s) throws SQLException {
        return spectacleDAO.add(s);
    }

    public boolean updateSpectacle(Spectacle s) throws SQLException {
        return spectacleDAO.update(s);
    }

    public boolean deleteSpectacle(int id) throws SQLException {
        return spectacleDAO.delete(id);
    }
}
