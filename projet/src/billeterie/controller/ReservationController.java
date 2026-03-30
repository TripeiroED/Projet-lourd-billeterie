package billeterie.controller;

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

    /**
     * Récupère toutes les réservations d'un utilisateur
     */
    public List<Reservation> findByUsername(String username) throws SQLException {
        return reservationDAO.findByUsername(username);
    }

    /**
     * Réserver un nombre de places pour un spectacle
     */
    public boolean reserverPlace(String username, int spectacleId, int nbPlaces) throws SQLException {
        return reservationDAO.reserverPlace(username, spectacleId, nbPlaces);
    }

    /**
     * Annuler une réservation
     */
    public boolean annulerReservation(int reservationId) throws SQLException {
        return reservationDAO.annulerReservation(reservationId);
    }

    /**
     * Méthode de compatibilité pour UserReservations
     * (anciennement ajouterReservation)
     */
    public boolean ajouterReservation(String username, int spectacleId, int nbPlaces) throws SQLException {
        return reserverPlace(username, spectacleId, nbPlaces);
    }
}
