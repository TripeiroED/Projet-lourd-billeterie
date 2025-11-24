package billeterie;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class UserHomePage {

    private VBox mainView = new VBox(25);
    private Connection conn;
    private String username;

    public UserHomePage(String username, Connection conn) {
        this.conn = conn;
        this.username = username;

        mainView.setPadding(new Insets(40));
        mainView.setStyle("-fx-background-color: #f9f9f9;");
        mainView.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Bienvenue sur votre espace Billetterie, " + username + " !");
        welcomeLabel.setFont(Font.font("Arial", 24));
        welcomeLabel.setStyle("-fx-text-fill: #34495e;");

        mainView.getChildren().add(welcomeLabel);

        // Ajout des sections
        mainView.getChildren().addAll(
            createReservationsSection(),
            createFeaturedSpectaclesSection(),
            createAvailableSpectaclesSection()
        );
    }

    private VBox createReservationsSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6,0,0,1);");
        box.setPrefWidth(700);

        Label title = new Label("Mes Réservations");
        title.setFont(Font.font(20));
        title.setStyle("-fx-text-fill: #2c3e50;");
        box.getChildren().add(title);

        try {
            ReservationDAO reservationDAO = new ReservationDAO(conn);
            List<Reservation> reservations = reservationDAO.findByUsername(username);

            if (reservations.isEmpty()) {
                Label noResLabel = new Label("Vous n'avez pas encore de réservations.");
                noResLabel.setStyle("-fx-text-fill: #7f8c8d;");
                box.getChildren().add(noResLabel);
            } else {
                for (int i = 0; i < Math.min(3, reservations.size()); i++) {
                    Reservation r = reservations.get(i);
                    Label resLabel = new Label("- " + r.getSpectacleName() + " le " + r.getDate() + ", " + r.getNombrePlaces() + " place(s)");
                    resLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                    box.getChildren().add(resLabel);
                }
                if (reservations.size() > 3) {
                    Label moreLabel = new Label("... et " + (reservations.size() - 3) + " autres réservations.");
                    moreLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #95a5a6;");
                    box.getChildren().add(moreLabel);
                }
            }

        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur lors du chargement des réservations.");
            errorLabel.setStyle("-fx-text-fill: red;");
            box.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        return box;
    }

    private VBox createFeaturedSpectaclesSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6,0,0,1);");
        box.setPrefWidth(700);

        Label title = new Label("Spectacles à la une");
        title.setFont(Font.font(20));
        title.setStyle("-fx-text-fill: #2c3e50;");
        box.getChildren().add(title);

        try {
            SpectacleDAO spectacleDAO = new SpectacleDAO(conn);
            List<Spectacle> featured = spectacleDAO.findFeatured();

            if (featured.isEmpty()) {
                Label noSpecLabel = new Label("Aucun spectacle à la une pour le moment.");
                noSpecLabel.setStyle("-fx-text-fill: #7f8c8d;");
                box.getChildren().add(noSpecLabel);
            } else {
                for (int i = 0; i < Math.min(3, featured.size()); i++) {
                    Spectacle s = featured.get(i);
                    Label specLabel = new Label("- " + s.getNom() + " le " + s.getDate() + " à " + s.getLieu());
                    specLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                    box.getChildren().add(specLabel);
                }
            }

        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur lors du chargement des spectacles.");
            errorLabel.setStyle("-fx-text-fill: red;");
            box.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        return box;
    }

    private VBox createAvailableSpectaclesSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6,0,0,1);");
        box.setPrefWidth(700);

        Label title = new Label("Spectacles libres");
        title.setFont(Font.font(20));
        title.setStyle("-fx-text-fill: #2c3e50;");
        box.getChildren().add(title);

        try {
            SpectacleDAO spectacleDAO = new SpectacleDAO(conn);
            int totalFreeSeats = spectacleDAO.countTotalFreeSeats();

            Label infoLabel = new Label("Nombre total de places disponibles : " + totalFreeSeats);
            infoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");

            box.getChildren().add(infoLabel);

        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur lors du calcul des places disponibles.");
            errorLabel.setStyle("-fx-text-fill: red;");
            box.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        return box;
    }

    public VBox getView() {
        return mainView;
    }
}
