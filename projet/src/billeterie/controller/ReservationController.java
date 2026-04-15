package billeterie.controller;

import billeterie.model.Billet;
import billeterie.model.Reservation;
import billeterie.model.ReservationDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReservationController {

    private final ReservationDAO reservationDAO;

    public ReservationController(Connection conn) {
        this.reservationDAO = new ReservationDAO(conn);
    }

    public List<Reservation> findByUsername(String username) throws SQLException {
        return reservationDAO.findByUsername(username);
    }

    public List<Reservation> findForUser(String username) throws SQLException {
        return reservationDAO.findForUser(username);
    }

    public List<Billet> findBilletsByReservationId(int reservationId) throws SQLException {
        return reservationDAO.findBilletsByReservationId(reservationId);
    }

    public boolean reserverPlace(String username, int spectacleId, int nbPlaces) throws SQLException {
        return reservationDAO.reserverPlace(username, spectacleId, nbPlaces);
    }

    public boolean annulerReservation(int reservationId) throws SQLException {
        return reservationDAO.annulerReservation(reservationId);
    }

    public boolean ajouterReservation(String username, int spectacleId, int nbPlaces) throws SQLException {
        return reserverPlace(username, spectacleId, nbPlaces);
    }

    public List<Reservation> findAll() throws SQLException {
        return reservationDAO.findAll();
    }

    public void modifierReservation(int id, int nombrePlaces) throws SQLException {
        reservationDAO.updateReservation(id, nombrePlaces);
    }
}
