package billeterie;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UserHomePage {

    private VBox mainView = new VBox(30);
    private Connection conn;
    private String username;

    // Champ membre pour garder la référence à la section réservations
    private VBox reservationsSection;

    public UserHomePage(String username, Connection conn) {
        this.conn = conn;
        this.username = username;

        mainView.setPadding(new Insets(40));
        mainView.setStyle("-fx-background-color: #f9f9f9;");
        mainView.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Bienvenue sur votre espace Billetterie, " + username + " !");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.setStyle("-fx-text-fill: #2c3e50;");
        mainView.getChildren().add(welcomeLabel);

        // Initialise la section réservations et garde la référence
        reservationsSection = createReservationsSection();

        mainView.getChildren().addAll(
            reservationsSection,
            createFeaturedSpectaclesSection(),
            createAvailableSpectaclesSection()
        );
    }

    private VBox createReservationsSection() {
        VBox box = createSectionContainer();

        Label title = createSectionTitle("Mes Réservations");
        box.getChildren().add(title);

        // Charge les réservations dans la section
        loadReservations(box);

        return box;
    }

    // Méthode interne pour charger/rafraîchir la liste des réservations
    private void loadReservations(VBox box) {
        // Nettoie tout sauf le titre (index 0)
        if (box.getChildren().size() > 1) {
            box.getChildren().remove(1, box.getChildren().size());
        }

        try {
            ReservationDAO reservationDAO = new ReservationDAO(conn);
            List<Reservation> reservations = reservationDAO.findByUsername(username);

            if (reservations.isEmpty()) {
                box.getChildren().add(createInfoLabel("Vous n'avez pas encore de réservations."));
            } else {
                VBox listBox = new VBox(8);
                for (int i = 0; i < Math.min(3, reservations.size()); i++) {
                    Reservation r = reservations.get(i);
                    HBox resLine = new HBox(10);
                    Label lbl = new Label(r.getSpectacleName() + " le " + r.getDate() + ", " + r.getNombrePlaces() + " place(s)");
                    lbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e;");
                    resLine.getChildren().add(lbl);
                    listBox.getChildren().add(resLine);
                }
                box.getChildren().add(listBox);

                if (reservations.size() > 3) {
                    box.getChildren().add(createInfoLabel("... et " + (reservations.size() - 3) + " autres réservations."));
                }

                Button btnSeeAll = new Button("Voir toutes mes réservations");
                btnSeeAll.setStyle(buttonStyle());
                btnSeeAll.setOnAction(e -> {
                    // TODO: Naviguer vers la page réservations complète
                });
                box.getChildren().add(btnSeeAll);
            }

        } catch (SQLException e) {
            box.getChildren().add(createErrorLabel("Erreur lors du chargement des réservations."));
            e.printStackTrace();
        }
    }

    /**
     * Méthode publique à appeler pour rafraîchir dynamiquement la liste des réservations,
     * par exemple après une nouvelle réservation.
     */
    public void refreshReservations() {
        loadReservations(reservationsSection);
    }

    // ... Le reste de tes méthodes createFeaturedSpectaclesSection(), createAvailableSpectaclesSection(), etc.

    private VBox createFeaturedSpectaclesSection() {
        VBox box = createSectionContainer();

        Label title = createSectionTitle("Spectacles à la une");
        box.getChildren().add(title);

        try {
            SpectacleDAO spectacleDAO = new SpectacleDAO(conn);
            List<Spectacle> featured = spectacleDAO.findFeatured();

            if (featured.isEmpty()) {
                box.getChildren().add(createInfoLabel("Aucun spectacle à la une pour le moment."));
            } else {
                VBox listBox = new VBox(8);
                for (Spectacle s : featured) {
                    HBox hbox = new HBox(15);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    Label lblNom = new Label(s.getNom());
                    lblNom.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                    lblNom.setStyle("-fx-text-fill: #2c3e50;");

                    Label lblDateLieu = new Label(s.getDate() + " à " + s.getLieu());
                    lblDateLieu.setStyle("-fx-text-fill: #7f8c8d;");

                    Label lblPrix = new Label(String.format("%.2f €", s.getPrix()));
                    lblPrix.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

                    hbox.getChildren().addAll(lblNom, lblDateLieu, lblPrix);

                    listBox.getChildren().add(hbox);
                }
                box.getChildren().add(listBox);

                Button btnSeeAll = new Button("Voir tous les spectacles");
                btnSeeAll.setStyle(buttonStyle());
                btnSeeAll.setOnAction(e -> {
                    // TODO: Naviguer vers la page spectacles complète
                });
                box.getChildren().add(btnSeeAll);
            }
        } catch (SQLException e) {
            box.getChildren().add(createErrorLabel("Erreur lors du chargement des spectacles."));
            e.printStackTrace();
        }

        return box;
    }

    private VBox createAvailableSpectaclesSection() {
        VBox box = createSectionContainer();

        Label title = createSectionTitle("Places disponibles");
        box.getChildren().add(title);

        try {
            SpectacleDAO spectacleDAO = new SpectacleDAO(conn);
            int totalFreeSeats = spectacleDAO.countTotalFreeSeats();

            Label infoLabel = new Label("Nombre total de places disponibles : " + totalFreeSeats);
            infoLabel.setFont(Font.font(16));
            infoLabel.setStyle("-fx-text-fill: #27ae60;");

            box.getChildren().add(infoLabel);
        } catch (SQLException e) {
            box.getChildren().add(createErrorLabel("Erreur lors du calcul des places disponibles."));
            e.printStackTrace();
        }

        return box;
    }

    // Utilitaires

    private VBox createSectionContainer() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setMaxWidth(700);
        box.setStyle(
            "-fx-background-color: #fff; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );
        return box;
    }

    private Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #34495e;");
        title.setPadding(new Insets(0, 0, 10, 0));
        title.setUnderline(true);
        return title;
    }

    private Label createInfoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");
        lbl.setFont(Font.font(14));
        return lbl;
    }

    private Label createErrorLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: red;");
        lbl.setFont(Font.font(14));
        return lbl;
    }

    private String buttonStyle() {
        return "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 6; " +
               "-fx-font-size: 14px;";
    }

    public VBox getView() {
        return mainView;
    }
}
