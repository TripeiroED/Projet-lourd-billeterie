package billeterie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserReservations {

    private VBox root;
    private TableView<Reservation> table;
    private ReservationDAO reservationDAO;
    private String username;

    private TextField spectacleIdField;
    private TextField placesField;
    private Button btnReserver;

    public UserReservations(Connection conn, String username) {
        this.username = username;
        this.reservationDAO = new ReservationDAO(conn);

        root = new VBox(10);
        root.setPadding(new Insets(10));

        Label title = new Label("Mes Réservations");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        setupTable();

        HBox form = new HBox(10);
        spectacleIdField = new TextField();
        spectacleIdField.setPromptText("ID Spectacle");
        spectacleIdField.setMaxWidth(100);

        placesField = new TextField();
        placesField.setPromptText("Nombre de places");
        placesField.setMaxWidth(100);

        btnReserver = new Button("Réserver");
        btnReserver.setDisable(true);

        btnReserver.setOnAction(e -> handleReservation());

        spectacleIdField.textProperty().addListener((obs, oldV, newV) -> checkFields());
        placesField.textProperty().addListener((obs, oldV, newV) -> checkFields());

        form.getChildren().addAll(spectacleIdField, placesField, btnReserver);

        root.getChildren().addAll(title, table, form);

        loadReservations();
    }

    private void checkFields() {
        btnReserver.setDisable(
            spectacleIdField.getText().trim().isEmpty() ||
            placesField.getText().trim().isEmpty()
        );
    }

    private void setupTable() {
        TableColumn<Reservation, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colId.setPrefWidth(50);

        TableColumn<Reservation, String> colSpectacle = new TableColumn<>("Spectacle");
        colSpectacle.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSpectacleName()));
        colSpectacle.setPrefWidth(200);

        TableColumn<Reservation, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDate()));
        colDate.setPrefWidth(150);

        TableColumn<Reservation, Integer> colPlaces = new TableColumn<>("Places");
        colPlaces.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getNombrePlaces()).asObject());
        colPlaces.setPrefWidth(80);

        table.getColumns().addAll(colId, colSpectacle, colDate, colPlaces);
        table.setPrefHeight(300);
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationDAO.findByUsername(username);
            ObservableList<Reservation> data = FXCollections.observableArrayList(reservations);
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des réservations.");
        }
    }

    private void handleReservation() {
        int spectacleId;
        int places;

        try {
            spectacleId = Integer.parseInt(spectacleIdField.getText());
            places = Integer.parseInt(placesField.getText());
            if (places <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Veuillez saisir des nombres valides et positifs.");
            return;
        }

        try {
            boolean success = reservationDAO.reserverPlace(username, spectacleId, places);
            if (success) {
                showAlert("Réservation réussie !");
                loadReservations();
                spectacleIdField.clear();
                placesField.clear();
            } else {
                showAlert("Spectacle introuvable ou places insuffisantes.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la réservation.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return root;
    }
}
