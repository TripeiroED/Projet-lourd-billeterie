package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.controller.SpectacleController;
import billeterie.model.Reservation;
import billeterie.model.Spectacle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;

public class UserHomePage {

    private VBox mainView = new VBox(25);
    private final ReservationController reservationController;
    private final SpectacleController spectacleController;
    private final String username;

    private VBox reservationsSection;

    // 🔹 Label pour le compteur de réservations
    private Label reservationsCountLabel;

    public UserHomePage(App app, String username) {
        this.reservationController = app.getReservationController();
        this.spectacleController = app.getSpectacleController();
        this.username = username;

        mainView.setPadding(new Insets(30));
        mainView.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f4f8, #d9e2ec);");
        mainView.setAlignment(Pos.TOP_CENTER);

        reservationsSection = createReservationsSection();

        mainView.getChildren().addAll(
                createHeader(),
                createTopSummarySection(),
                reservationsSection,
                createFeaturedSpectaclesSection(),
                createAvailableSpectaclesSection());
    }

    private Label createHeader() {
        Label header = new Label("Bienvenue, " + username + " 👋");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        header.setStyle("-fx-text-fill: #1f2937;");
        return header;
    }

    private VBox createReservationsSection() {
        VBox box = createCard();
        box.getChildren().add(createSectionTitle("🎟 Mes Réservations"));
        loadReservations(box);
        return box;
    }

    private void loadReservations(VBox box) {
        // Supprime tout sauf le titre
        box.getChildren().remove(1, box.getChildren().size());

        try {
            List<Reservation> reservations = reservationController.findByUsername(username);

            if (reservations.isEmpty()) {
                box.getChildren().add(createInfoLabel("Aucune réservation pour le moment."));
                return;
            }

            for (Reservation r : reservations.stream().limit(3).toList()) {
                box.getChildren().add(createReservationCard(r));
            }

        } catch (SQLException e) {
            box.getChildren().add(createErrorLabel("Erreur chargement réservations"));
        }
    }

    private HBox createReservationCard(Reservation r) {
        Label title = new Label(r.getSpectacleName());
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        Label details = new Label(r.getDate() + " • " + r.getNombrePlaces() + " place(s)");
        details.setStyle("-fx-text-fill:#6b7280;");

        VBox text = new VBox(3, title, details);

        Button cancel = new Button("Annuler");
        cancel.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-background-radius:8;");

        cancel.setOnAction(e -> {
            try {
                boolean success = reservationController.annulerReservation(r.getId());
                if (success) {
                    loadReservations(reservationsSection); // recharge les réservations
                    refreshReservationCount(); // 🔹 met à jour le compteur
                } else {
                    showAlert("Erreur", "Impossible d'annuler la réservation.");
                }
            } catch (SQLException ex) {
                showAlert("Erreur", "Impossible d'annuler");
                ex.printStackTrace();
            }
        });

        HBox card = new HBox(15, text, cancel);
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:10;");

        return card;
    }

    private VBox createFeaturedSpectaclesSection() {
        VBox box = createCard();
        box.getChildren().add(createSectionTitle("🔥 Spectacles populaires"));

        try {
            for (Spectacle s : spectacleController.findFeatured()) {
                box.getChildren().add(createSpectacleCard(s));
            }
        } catch (SQLException e) {
            box.getChildren().add(createErrorLabel("Erreur spectacles"));
        }

        return box;
    }

    private HBox createSpectacleCard(Spectacle s) {
        Label name = new Label(s.getNom());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label info = new Label(s.getDate() + " • " + s.getLieu());
        Label price = new Label(s.getPrix() + " €");
        price.setStyle("-fx-text-fill:#10b981; -fx-font-weight:bold;");

        VBox text = new VBox(name, info);

        HBox card = new HBox(15, text, price);
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color:#ffffff; -fx-border-color:#e5e7eb; -fx-border-radius:10;");

        return card;
    }

    private VBox createAvailableSpectaclesSection() {
        VBox box = createCard();
        box.getChildren().add(createSectionTitle("📊 Disponibilité"));

        try {
            int seats = spectacleController.countTotalFreeSeats();
            Label lbl = new Label(seats + " places restantes");
            lbl.setFont(Font.font(20));
            lbl.setStyle("-fx-text-fill:#22c55e;");
            box.getChildren().add(lbl);
        } catch (SQLException e) {
            box.getChildren().add(createErrorLabel("Erreur"));
        }

        return box;
    }

    private HBox createTopSummarySection() {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER);

        // 🔹 compteur de réservations en temps réel
        reservationsCountLabel = new Label(String.valueOf(getReservationCount()));
        reservationsCountLabel.setFont(Font.font(22));

        VBox reservationsCard = new VBox(5, new Label("Réservations"), reservationsCountLabel);
        reservationsCard.setPadding(new Insets(15));
        reservationsCard.setAlignment(Pos.CENTER);
        reservationsCard.setStyle(
                "-fx-background-color:white; -fx-background-radius:12; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),6,0,0,2);");

        row.getChildren().addAll(
                reservationsCard,
                createStatCard("Spectacles", String.valueOf(getSpectacleCount())),
                createStatCard("Places", String.valueOf(getTotalFreeSeats())));

        return row;
    }

    // 🔹 Méthode pour mettre à jour le compteur
    public void refreshReservationCount() {
        reservationsCountLabel.setText(String.valueOf(getReservationCount()));
    }

    private VBox createStatCard(String title, String value) {
        Label t = new Label(title);
        Label v = new Label(value);
        v.setFont(Font.font(22));

        VBox box = new VBox(5, t, v);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);
        box.setStyle(
                "-fx-background-color:white; -fx-background-radius:12; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),6,0,0,2);");

        return box;
    }

    private VBox createCard() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setMaxWidth(750);
        box.setStyle(
                "-fx-background-color:white; -fx-background-radius:15; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,4);");
        return box;
    }

    private Label createSectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        return lbl;
    }

    private Label createInfoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:gray;");
        return lbl;
    }

    private Label createErrorLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:red;");
        return lbl;
    }

    private int getReservationCount() {
        try {
            return reservationController.findByUsername(username).size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getSpectacleCount() {
        try {
            return spectacleController.findAll().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getTotalFreeSeats() {
        try {
            return spectacleController.countTotalFreeSeats();
        } catch (Exception e) {
            return 0;
        }
    }

    private void showAlert(String title, String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public VBox getView() {
        return mainView;
    }
}
