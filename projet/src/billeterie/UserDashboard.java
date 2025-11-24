package billeterie;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class UserDashboard {

    private Connection conn; // Connexion à la BDD
    private String username; // Utilisateur connecté

    private ObservableList<Spectacle> spectacles = FXCollections.observableArrayList();

    private VBox mainView = new VBox(30);
    private FlowPane cardsPane = new FlowPane();
    private TextArea detailArea = new TextArea();

    private TextField searchField = new TextField();

    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 8;
    private Button btnPrev = new Button("Précédent");
    private Button btnNext = new Button("Suivant");

    public UserDashboard(App app, Connection conn, String username) {
        this.conn = conn;
        this.username = username;

        loadSpectaclesFromDB();

        searchField.setPromptText("Rechercher un spectacle...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 8 12;");
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            currentPage = 0;
            updateCards();
        });

        detailArea.setPrefHeight(150);
        detailArea.setEditable(false);
        detailArea.setWrapText(true);
        detailArea.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6;");

        cardsPane.setHgap(30);
        cardsPane.setVgap(30);
        cardsPane.setPadding(new Insets(20));
        cardsPane.setPrefWrapLength(900);

        btnPrev.setStyle("-fx-font-size: 14px; -fx-padding: 8 18; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");
        btnNext.setStyle("-fx-font-size: 14px; -fx-padding: 8 18; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");

        btnPrev.setOnAction(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateCards();
            }
        });

        btnNext.setOnAction(e -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < getFilteredSpectacles().size()) {
                currentPage++;
                updateCards();
            }
        });

        HBox paginationBox = new HBox(20, btnPrev, btnNext);
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.setPadding(new Insets(15, 0, 15, 0));

        mainView.setPadding(new Insets(40));
        mainView.getChildren().addAll(searchField, cardsPane, paginationBox, detailArea);

        updateCards();
    }

    private void loadSpectaclesFromDB() {
        try {
            SpectacleDAO dao = new SpectacleDAO(conn);
            spectacles.clear();
            spectacles.addAll(dao.findAll());
            System.out.println("Spectacles chargés : " + spectacles.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Spectacle> getFilteredSpectacles() {
        String filter = searchField.getText();
        if (filter == null || filter.isEmpty()) {
            return spectacles;
        }
        String lowerFilter = filter.toLowerCase();
        return spectacles.stream()
                .filter(s -> s.getNom().toLowerCase().contains(lowerFilter))
                .collect(Collectors.toList());
    }

    private void updateCards() {
        cardsPane.getChildren().clear();

        List<Spectacle> filtered = getFilteredSpectacles();

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filtered.size());

        if (start >= filtered.size()) {
            currentPage = 0;
            start = 0;
            end = Math.min(ITEMS_PER_PAGE, filtered.size());
        }

        for (int i = start; i < end; i++) {
            cardsPane.getChildren().add(createCard(filtered.get(i)));
        }

        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(end >= filtered.size());
    }

    private VBox createCard(Spectacle spectacle) {
        Label title = new Label(spectacle.getNom());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label date = new Label("Date : " + spectacle.getDate());
        date.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

        Label location = new Label("Lieu : " + spectacle.getLieu());
        location.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

        Label price = new Label(String.format("Prix : %.2f €", spectacle.getPrix()));
        price.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Button reserveBtn = new Button("Réserver");
        reserveBtn.setStyle(
            "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 8;"
        );

        // Désactive le bouton si plus de places disponibles
        if (spectacle.getPlacesDisponibles() <= 0) {
            reserveBtn.setDisable(true);
            reserveBtn.setText("Complet");
        }

        reserveBtn.setOnAction(e -> {
            // Ouvre une boîte de dialogue de réservation
            ReservationDialog dialog = new ReservationDialog(conn, username, spectacle);
            dialog.showAndWait().ifPresent(success -> {
                if (success) {
                    loadSpectaclesFromDB(); // recharge la liste pour mise à jour des places
                    updateCards();
                    detailArea.setText("Réservation réussie pour : " + spectacle.getNom());
                }
            });
        });

        VBox card = new VBox(12, title, date, location, price, reserveBtn);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-border-color: #d1d8e0; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);"
        );
        card.setPrefWidth(240);

        card.setOnMouseClicked(e -> {
            detailArea.setText(
                    spectacle.getNom() + "\n" +
                    "Date : " + spectacle.getDate() + "\n" +
                    "Lieu : " + spectacle.getLieu() + "\n" +
                    String.format("Prix : %.2f €\n\n", spectacle.getPrix()) +
                    "Description : Spectacle à ne pas manquer !"
            );
        });

        return card;
    }

    public VBox getView() {
        return mainView;
    }
}
