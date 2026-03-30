package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.model.Reservation;
import billeterie.model.Spectacle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;

public class UserReservations {

    private VBox mainView = new VBox(20);
    private final ReservationController reservationController;
    private String username;
    private Spectacle selectedSpectacle; // peut être null

    // 🔹 Constructeur unique
    public UserReservations(App app, String username, Spectacle spectacle) {
        this.reservationController = app.getReservationController();
        this.username = username;
        this.selectedSpectacle = spectacle;

        mainView.setPadding(new Insets(30));
        mainView.setStyle("-fx-background-color:#f5f7fa;");

        if (spectacle != null) {
            showReservationForm();
        } else {
            showReservationsList();
        }
    }

    private void showReservationForm() {
        mainView.getChildren().clear();

        Label title = new Label("Réserver : " + selectedSpectacle.getNom());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#2c3e50"));
        mainView.getChildren().add(title);

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

        Label seatsLabel = new Label("Nombre de places :");
        TextField seatsField = new TextField();
        seatsField.setMaxWidth(80);

        Button reserveBtn = new Button("Réserver");
        reserveBtn.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white; -fx-background-radius:8;");

        reserveBtn.setOnAction(e -> {
            try {
                int nb = Integer.parseInt(seatsField.getText());
                reservationController.ajouterReservation(username, selectedSpectacle.getId(), nb);
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Réservation effectuée !");
                a.showAndWait();
                showReservationsList(); // recharge les réservations après réservation
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Impossible de réserver. Vérifiez les données.");
                a.showAndWait();
                ex.printStackTrace();
            }
        });

        form.getChildren().addAll(seatsLabel, seatsField, reserveBtn);
        mainView.getChildren().add(form);
    }

    private void showReservationsList() {
        mainView.getChildren().clear();
        Label title = new Label("Mes Réservations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#2c3e50"));
        mainView.getChildren().add(title);

        loadReservations();
    }

    private void loadReservations() {
        mainView.getChildren().removeIf(node -> node != mainView.getChildren().get(0));

        try {
            List<Reservation> reservations = reservationController.findByUsername(username);

            if (reservations.isEmpty()) {
                Label noResLabel = new Label("Vous n'avez aucune réservation pour le moment.");
                noResLabel.setFont(Font.font(16));
                noResLabel.setTextFill(Color.web("#7f8c8d"));
                mainView.getChildren().add(noResLabel);
            } else {
                VBox reservationsList = new VBox(15);
                for (Reservation r : reservations) {
                    reservationsList.getChildren().add(createReservationCard(r));
                }
                mainView.getChildren().add(reservationsList);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur lors du chargement des réservations.");
            errorLabel.setFont(Font.font(16));
            errorLabel.setTextFill(Color.RED);
            mainView.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private HBox createReservationCard(Reservation r) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color:white; -fx-border-radius:12; -fx-background-radius:12; -fx-effect:dropshadow(gaussian, rgba(0,0,0,0.1),5,0,0,2);");

        VBox detailsBox = new VBox(6);

        Label spectacleName = new Label(r.getSpectacleName());
        spectacleName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        spectacleName.setTextFill(Color.web("#34495e"));

        Label dateLabel = new Label("Date : " + r.getDate());
        dateLabel.setFont(Font.font(14));
        dateLabel.setTextFill(Color.web("#7f8c8d"));

        Label placesLabel = new Label("Nombre de places : " + r.getNombrePlaces());
        placesLabel.setFont(Font.font(14));
        placesLabel.setTextFill(Color.web("#7f8c8d"));

        detailsBox.getChildren().addAll(spectacleName, dateLabel, placesLabel);

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle(
                "-fx-background-color:#e74c3c; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:6;");

        cancelBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment annuler cette réservation ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        boolean success = reservationController.annulerReservation(r.getId());
                        if (success)
                            loadReservations();
                        else
                            new Alert(Alert.AlertType.ERROR, "La réservation n'a pas pu être annulée.").show();
                    } catch (SQLException ex) {
                        new Alert(Alert.AlertType.ERROR, "Erreur lors de l'annulation.").show();
                        ex.printStackTrace();
                    }
                }
            });
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(detailsBox, spacer, cancelBtn);
        return card;
    }

    public VBox getView() {
        return mainView;
    }
}
