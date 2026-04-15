package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.controller.SpectacleController;
import billeterie.model.Reservation;
import billeterie.model.Spectacle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;

public class UserHomePage {
    private final VBox mainView = new VBox(22);
    private final ReservationController reservationController;
    private final SpectacleController spectacleController;
    private final String username;
    private final Runnable onDataChanged;

    private final VBox reservationsSection = AppTheme.createCardBox();
    private final VBox featuredSection = AppTheme.createCardBox();
    private final VBox availabilitySection = AppTheme.createCardBox();

    private final Label reservationsCountValue = createMetricValue();
    private final Label spectacleCountValue = createMetricValue();
    private final Label freeSeatsValue = createMetricValue();

    public UserHomePage(App app, String username) {
        this(app, username, null);
    }

    public UserHomePage(App app, String username, Runnable onDataChanged) {
        this.reservationController = app.getReservationController();
        this.spectacleController = app.getSpectacleController();
        this.username = username;
        this.onDataChanged = onDataChanged;

        mainView.setPadding(new Insets(30));
        AppTheme.stylePage(mainView);

        reservationsSection.getChildren().add(AppTheme.sectionTitle("Mes reservations"));
        featuredSection.getChildren().add(AppTheme.sectionTitle("Spectacles a suivre"));
        availabilitySection.getChildren().add(AppTheme.sectionTitle("Disponibilite globale"));

        mainView.getChildren().addAll(
                createHeroSection(),
                createSummarySection(),
                reservationsSection,
                featuredSection,
                availabilitySection);

        refreshData();
    }

    public void refreshData() {
        refreshSummaryCards();
        refreshReservationsSection();
        refreshFeaturedSection();
        refreshAvailabilitySection();
    }

    private VBox createHeroSection() {
        VBox hero = AppTheme.createCardBox();
        hero.setSpacing(8);

        Label title = AppTheme.pageTitle("Bonjour, " + username);
        Label subtitle = AppTheme.mutedLabel(
                "Retrouve tes reservations, les disponibilites en direct et les prochains spectacles.");

        hero.getChildren().addAll(title, subtitle);
        return hero;
    }

    private HBox createSummarySection() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(
                createMetricCard("Reservations", "Total de reservations actives", reservationsCountValue),
                createMetricCard("Spectacles", "Spectacles a venir", spectacleCountValue),
                createMetricCard("Places libres", "Total encore disponible", freeSeatsValue));
        return row;
    }

    private VBox createMetricCard(String title, String subtitle, Label valueLabel) {
        VBox card = AppTheme.createCardBox();
        card.setSpacing(6);
        card.setPrefWidth(220);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 14));
        titleLabel.setStyle(AppTheme.TITLE_TEXT_STYLE);

        Label subtitleLabel = AppTheme.mutedLabel(subtitle);
        valueLabel.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(titleLabel, subtitleLabel, valueLabel);
        return card;
    }

    private Label createMetricValue() {
        Label label = new Label("0");
        label.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 28));
        label.setStyle("-fx-text-fill: #1d4ed8; -fx-font-family: '" + AppTheme.FONT_FAMILY + "';");
        return label;
    }

    private void refreshSummaryCards() {
        reservationsCountValue.setText(String.valueOf(getReservationCount()));
        spectacleCountValue.setText(String.valueOf(getSpectacleCount()));
        freeSeatsValue.setText(String.valueOf(getTotalFreeSeats()));
    }

    private void refreshReservationsSection() {
        clearSection(reservationsSection);

        try {
            List<Reservation> reservations = reservationController.findForUser(username);
            if (reservations.isEmpty()) {
                reservationsSection.getChildren().add(AppTheme.mutedLabel("Aucune reservation pour le moment."));
                return;
            }

            int limit = Math.min(3, reservations.size());
            for (int i = 0; i < limit; i++) {
                reservationsSection.getChildren().add(createReservationCard(reservations.get(i)));
            }
        } catch (SQLException e) {
            reservationsSection.getChildren().add(createErrorLabel("Impossible de charger les reservations."));
        }
    }

    private HBox createReservationCard(Reservation reservation) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));
        AppTheme.styleSoftCard(card);

        VBox textBox = new VBox(4);
        Label title = new Label(reservation.getSpectacleName());
        title.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 15));
        title.setStyle(AppTheme.TITLE_TEXT_STYLE);

        String rawDate = reservation.getDate();

        String formattedDate;

        try {
            formattedDate = java.time.LocalDate
                    .parse(rawDate.substring(0, 10))
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            formattedDate = rawDate; // fallback si format bizarre
        }

        Label details = AppTheme.mutedLabel(
                formattedDate + " | " + reservation.getNombrePlaces() + " place(s)");
        textBox.getChildren().addAll(title, details);

        Button cancelButton = new Button("Annuler");
        AppTheme.styleDangerButton(cancelButton);
        cancelButton.setOnAction(event -> cancelReservation(reservation.getId()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        card.getChildren().addAll(textBox, spacer, cancelButton);
        return card;
    }

    private void refreshFeaturedSection() {
        clearSection(featuredSection);

        try {
            List<Spectacle> spectacles = spectacleController.findFeatured();
            if (spectacles.isEmpty()) {
                featuredSection.getChildren().add(AppTheme.mutedLabel("Aucun spectacle a la une."));
                return;
            }

            for (Spectacle spectacle : spectacles) {
                featuredSection.getChildren().add(createFeaturedCard(spectacle));
            }
        } catch (SQLException e) {
            featuredSection.getChildren().add(createErrorLabel("Impossible de charger les spectacles."));
        }
    }

    private HBox createFeaturedCard(Spectacle spectacle) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));
        AppTheme.styleSoftCard(card);

        VBox textBox = new VBox(4);
        Label name = new Label(spectacle.getNom());
        name.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 15));
        name.setStyle(AppTheme.TITLE_TEXT_STYLE);

        String rawDate = spectacle.getDate();

        String formattedDate;

        try {
            formattedDate = java.time.LocalDate
                    .parse(rawDate.substring(0, 10))
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            formattedDate = rawDate; // fallback si format différent
        }

        Label info = AppTheme.mutedLabel(formattedDate + " | " + spectacle.getLieu());
        Label seats = AppTheme.mutedLabel("Places dispo : " + spectacle.getPlacesDisponibles());
        textBox.getChildren().addAll(name, info, seats);

        Label price = new Label(AppTheme.formatPrice(spectacle.getPrix()));
        price.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 14));
        price.setStyle("-fx-text-fill: #0f766e; -fx-font-family: '" + AppTheme.FONT_FAMILY + "';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        card.getChildren().addAll(textBox, spacer, price);
        return card;
    }

    private void refreshAvailabilitySection() {
        clearSection(availabilitySection);

        try {
            int freeSeats = spectacleController.countTotalFreeSeats();
            Label totalLabel = new Label(freeSeats + " places encore disponibles");
            totalLabel.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 24));
            totalLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-family: '" + AppTheme.FONT_FAMILY + "';");

            Label helper = AppTheme.mutedLabel(
                    "Ce chiffre se met a jour des qu'une reservation est creee ou annulee.");
            availabilitySection.getChildren().addAll(totalLabel, helper);
        } catch (SQLException e) {
            availabilitySection.getChildren().add(createErrorLabel("Impossible de calculer la disponibilite."));
        }
    }

    private void cancelReservation(int reservationId) {
        try {
            boolean success = reservationController.annulerReservation(reservationId);
            if (success) {
                notifyDataChanged();
            } else {
                showAlert("Erreur", "La reservation n'a pas pu etre annulee.");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur est survenue pendant l'annulation.");
        }
    }

    private void notifyDataChanged() {
        if (onDataChanged != null) {
            onDataChanged.run();
        } else {
            refreshData();
        }
    }

    private void clearSection(VBox section) {
        if (section.getChildren().size() > 1) {
            section.getChildren().remove(1, section.getChildren().size());
        }
    }

    private Label createErrorLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #dc2626; -fx-font-family: '" + AppTheme.FONT_FAMILY + "';");
        return label;
    }

    private int getReservationCount() {
        try {
            return reservationController.findForUser(username).size();
        } catch (SQLException e) {
            return 0;
        }
    }

    private int getSpectacleCount() {
        try {
            return spectacleController.findAll().size();
        } catch (SQLException e) {
            return 0;
        }
    }

    private int getTotalFreeSeats() {
        try {
            return spectacleController.countTotalFreeSeats();
        } catch (SQLException e) {
            return 0;
        }
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    public VBox getView() {
        return mainView;
    }
}
