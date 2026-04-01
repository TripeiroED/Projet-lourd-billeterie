package billeterie.model;

public class Billet {
    private final int id;
    private final int reservationId;
    private final String qrCode;
    private final String statut;

    public Billet(int reservationId, String qrCode) {
        this(0, reservationId, qrCode, "VALIDE");
    }

    public Billet(int id, int reservationId, String qrCode, String statut) {
        this.id = id;
        this.reservationId = reservationId;
        this.qrCode = qrCode;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getStatut() {
        return statut;
    }
}
