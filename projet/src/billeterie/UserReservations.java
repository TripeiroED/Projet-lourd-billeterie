package billeterie;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UserReservations {

    private VBox mainView = new VBox(20);
    private Connection conn;
    private String username;

    public UserReservations(Connection conn, String username) {
        this.conn = conn;
        this.username = username;

        mainView.setPadding(new Insets(30));
        mainView.setStyle("-fx-background-color: #f5f7fa;");

        Label title = new Label("Mes Réservations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#2c3e50"));

        mainView.getChildren().add(title);

        loadReservations();
    }

    private void loadReservations() {
        mainView.getChildren().removeIf(node -> node != mainView.getChildren().get(0)); // garde juste le titre

        try {
            ReservationDAO reservationDAO = new ReservationDAO(conn);
            List<Reservation> reservations = reservationDAO.findByUsername(username);

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
            "-fx-background-color: white; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5,0,0,2);"
        );

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
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6;"
        );
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(
            "-fx-background-color: #c0392b; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6;"
        ));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6;"
        ));

        cancelBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment annuler cette réservation ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        ReservationDAO reservationDAO = new ReservationDAO(conn);
                        boolean success = reservationDAO.annulerReservation(r.getId());
                        if (success) {
                            // Recharge la liste complète des réservations pour mise à jour
                            loadReservations();
                        } else {
                            Alert error = new Alert(Alert.AlertType.ERROR, "La réservation n'a pas pu être annulée.");
                            error.show();
                        }
                    } catch (SQLException ex) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'annulation.");
                        error.show();
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
