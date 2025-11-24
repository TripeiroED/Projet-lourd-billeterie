package billeterie;

import javafx.beans.property.*;

public class Spectacle {
    private IntegerProperty id;
    private StringProperty nom;
    private StringProperty date;
    private StringProperty lieu;
    private DoubleProperty prix;
    private IntegerProperty placesDisponibles;

    public Spectacle() {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.lieu = new SimpleStringProperty();
        this.prix = new SimpleDoubleProperty();
        this.placesDisponibles = new SimpleIntegerProperty();
    }

    public Spectacle(int id, String nom, String date, String lieu, double prix, int placesDisponibles) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.date = new SimpleStringProperty(date);
        this.lieu = new SimpleStringProperty(lieu);
        this.prix = new SimpleDoubleProperty(prix);
        this.placesDisponibles = new SimpleIntegerProperty(placesDisponibles);
    }

    // getters et setters

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getDate() { return date.get(); }
    public void setDate(String date) { this.date.set(date); }
    public StringProperty dateProperty() { return date; }

    public String getLieu() { return lieu.get(); }
    public void setLieu(String lieu) { this.lieu.set(lieu); }
    public StringProperty lieuProperty() { return lieu; }

    public double getPrix() { return prix.get(); }
    public void setPrix(double prix) { this.prix.set(prix); }
    public DoubleProperty prixProperty() { return prix; }

    public int getPlacesDisponibles() { return placesDisponibles.get(); }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles.set(placesDisponibles); }
    public IntegerProperty placesDisponiblesProperty() { return placesDisponibles; }
}
