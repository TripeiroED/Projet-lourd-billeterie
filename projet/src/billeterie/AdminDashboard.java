package billeterie;

import java.sql.Connection;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboard {

    private VBox view;
    private App app;
    private Connection conn;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Spectacle> spectacles = FXCollections.observableArrayList();

    private TableView<User> userTable = new TableView<>();
    private TableView<Spectacle> spectacleTable = new TableView<>();

    public AdminDashboard(App app, Connection conn) {
        this.app = app;
        this.conn = conn;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Bienvenue Admin !");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        view.getChildren().add(welcomeLabel);

        // Fake users (en attendant UserDAO)
        users.addAll(
            new User("admin", "ADMIN"),
            new User("user1", "USER"),
            new User("user2", "USER")
        );

        VBox usersSection = createUsersSection();
        VBox spectaclesSection = createSpectaclesSection();

        ScrollPane scrollPane = new ScrollPane(new VBox(20, usersSection, spectaclesSection));
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        view.getChildren().add(scrollPane);

        loadSpectaclesFromDB();
    }

    /** Charger spectacles depuis la BDD */
    private void loadSpectaclesFromDB() {
        try {
            SpectacleDAO dao = new SpectacleDAO(conn);
            spectacles.clear();
            spectacles.addAll(dao.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** -------- USERS SECTION -------- */
    private VBox createUsersSection() {
        Label title = new Label("Gestion des Utilisateurs");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        userTable.getColumns().clear();

        TableColumn<User, String> usernameCol = new TableColumn<>("Nom utilisateur");
        usernameCol.setCellValueFactory(data -> data.getValue().usernameProperty());

        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(data -> data.getValue().roleProperty());

        userTable.getColumns().addAll(usernameCol, roleCol);
        userTable.setItems(users);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnDeleteUser = new Button("Supprimer utilisateur");
        btnDeleteUser.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (confirm("Supprimer", "Voulez-vous supprimer cet utilisateur ?")) {
                    users.remove(selected); // Pas encore de UserDAO
                }
            }
        });

        HBox btnBox = new HBox(10, btnDeleteUser);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        VBox section = new VBox(10, title, userTable, btnBox);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #2196F3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #E3F2FD;");

        return section;
    }

    /** -------- SPECTACLES SECTION -------- */
    private VBox createSpectaclesSection() {
        Label title = new Label("Gestion des Spectacles");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        spectacleTable.getColumns().clear();

        TableColumn<Spectacle, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(data -> data.getValue().nomProperty());

        TableColumn<Spectacle, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());

        TableColumn<Spectacle, String> lieuCol = new TableColumn<>("Lieu");
        lieuCol.setCellValueFactory(data -> data.getValue().lieuProperty());

        TableColumn<Spectacle, Double> prixCol = new TableColumn<>("Prix (€)");
        prixCol.setCellValueFactory(data -> data.getValue().prixProperty().asObject());

        spectacleTable.getColumns().addAll(nomCol, dateCol, lieuCol, prixCol);
        spectacleTable.setItems(spectacles);
        spectacleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnAdd = new Button("Ajouter");
        Button btnEdit = new Button("Modifier");
        Button btnDelete = new Button("Supprimer");

        /** ---- Ajouter ---- */
        btnAdd.setOnAction(e -> {
            Stage stage = (Stage) view.getScene().getWindow();
            SpectacleForm form = new SpectacleForm(stage, null);

            form.show();
            if (form.isSaved()) {
                try {
                    SpectacleDAO dao = new SpectacleDAO(conn);
                    dao.add(form.getSpectacle());
                    loadSpectaclesFromDB();
                } catch (SQLException ex) {
                    showAlert("Erreur", "Impossible d'ajouter le spectacle.");
                }
            }
        });

        /** ---- Modifier ---- */
        btnEdit.setOnAction(e -> {
            Spectacle selected = spectacleTable.getSelectionModel().getSelectedItem();
            if (selected != null) {

                Stage stage = (Stage) view.getScene().getWindow();
                SpectacleForm form = new SpectacleForm(stage, selected);

                form.show();
                if (form.isSaved()) {
                    try {
                        SpectacleDAO dao = new SpectacleDAO(conn);
                        dao.update(selected);
                        loadSpectaclesFromDB();
                    } catch (SQLException ex) {
                        showAlert("Erreur", "Impossible de modifier le spectacle.");
                    }
                }
            }
        });

        /** ---- Supprimer ---- */
        btnDelete.setOnAction(e -> {
            Spectacle selected = spectacleTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (confirm("Supprimer", "Voulez-vous supprimer ce spectacle ?")) {
                    try {
                        SpectacleDAO dao = new SpectacleDAO(conn);
                        dao.delete(selected.getId());   // 🔥 suppression en BDD
                        loadSpectaclesFromDB();
                    } catch (SQLException ex) {
                        showAlert("Erreur", "Impossible de supprimer le spectacle.");
                    }
                }
            }
        });

        HBox btnBox = new HBox(10, btnAdd, btnEdit, btnDelete);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        VBox section = new VBox(10, title, spectacleTable, btnBox);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #E8F5E9;");

        return section;
    }

    /** ---------- Alerte simple ---------- */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /** ---------- Confirmation ---------- */
    private boolean confirm(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public VBox getView() {
        return view;
    }

    /** Classe User simplifiée */
    public static class User {
        private javafx.beans.property.SimpleStringProperty username;
        private javafx.beans.property.SimpleStringProperty role;

        public User(String username, String role) {
            this.username = new javafx.beans.property.SimpleStringProperty(username);
            this.role = new javafx.beans.property.SimpleStringProperty(role);
        }

        public String getUsername() { return username.get(); }
        public javafx.beans.property.SimpleStringProperty usernameProperty() { return username; }

        public String getRole() { return role.get(); }
        public javafx.beans.property.SimpleStringProperty roleProperty() { return role; }
    }
}
