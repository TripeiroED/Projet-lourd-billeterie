package billeterie;

public class Reservation {
    private int id;
    private String username;
    private int spectacleId;
    private String spectacleName;
    private String date;
    private int nombrePlaces;

    public Reservation(int id, String username, int spectacleId, String spectacleName, String date, int nombrePlaces) {
        this.id = id;
        this.username = username;
        this.spectacleId = spectacleId;
        this.spectacleName = spectacleName;
        this.date = date;
        this.nombrePlaces = nombrePlaces;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getSpectacleId() { return spectacleId; }
    public String getSpectacleName() { return spectacleName; }
    public String getDate() { return date; }
    public int getNombrePlaces() { return nombrePlaces; }
}
