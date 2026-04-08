package billeterie.model;

public class Reservation {
    private int id;
    private int userId;
    private int spectacleId;
    private String spectacleName;
    private String date;
    private int nombrePlaces;

    public Reservation(int id, int userId, int spectacleId, String spectacleName, String date, int nombrePlaces) {
        this.id = id;
        this.userId = userId;
        this.spectacleId = spectacleId;
        this.spectacleName = spectacleName;
        this.date = date;
        this.nombrePlaces = nombrePlaces;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getSpectacleId() { return spectacleId; }
    public String getSpectacleName() { return spectacleName; }
    public String getDate() { return date; }
    public int getNombrePlaces() { return nombrePlaces; }
}
