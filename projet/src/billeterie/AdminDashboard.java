package billeterie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminDashboard {

    private BorderPane root;
    private Connection conn;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Spectacle> spectacles = FXCollections.observableArrayList();

    public AdminDashboard(App app, Connection conn) {
        this.conn = conn;

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f6fa;");

        // Charger les utilisateurs et spectacles depuis la BDD
        loadUsersFromDB();
        loadSpectaclesFromDB();

        root.setTop(createHeader(app));
        root.setCenter(createContent());
    }

    /* ================= HEADER ================= */
    private HBox createHeader(App app) {
        Label title = new Label("Dashboard Administrateur");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button logout = dangerButton("Déconnexion");
        logout.setOnAction(e -> app.showLoginScreen());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15, title, spacer, logout);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        return header;
    }

    /* ================= CONTENT ================= */
    private ScrollPane createContent() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(30));

        page.getChildren().addAll(
                createUsersCard(),
                createSpectaclesCard()
        );

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;");

        return scroll;
    }

    /* ================= USERS ================= */
    private VBox createUsersCard() {
        Label title = sectionTitle("Gestion des utilisateurs");

        VBox list = new VBox(12);

        if (users.isEmpty()) {
            list.getChildren().add(new Label("Aucun utilisateur"));
        } else {
            for (User u : users) {
                list.getChildren().add(userCard(u));
            }
        }

        return card(title, list);
    }

    private HBox userCard(User u) {
        Label name = new Label(u.getUsername());
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // ComboBox pour modifier le rôle
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMIN", "USER");
        roleCombo.setValue(u.getRole());
        roleCombo.setPrefWidth(120);

        // Bouton pour sauvegarder la modification du rôle
        Button saveRoleBtn = styledButton("Modifier rôle", "#007aff", "white");
        saveRoleBtn.setOnAction(e -> {
            String newRole = roleCombo.getValue();
            if (!newRole.equals(u.getRole())) {
                if (confirm("Modifier rôle", "Changer le rôle de " + u.getUsername() + " en " + newRole + " ?")) {
                    try {
                        UserDAO dao = new UserDAO(conn);
                        boolean success = dao.updateRole(u.getUsername(), newRole);
                        if (success) {
                            alert("Succès", "Rôle modifié avec succès.");
                            loadUsersFromDB();
                            root.setCenter(createContent());
                        } else {
                            alert("Erreur", "Modification du rôle échouée.");
                        }
                    } catch (SQLException ex) {
                        alert("Erreur", "Impossible de modifier le rôle");
                        ex.printStackTrace();
                    }
                }
            }
        });

        Button delete = dangerButton("Supprimer");
        delete.setOnAction(e -> {
            if (confirm("Supprimer", "Supprimer cet utilisateur ?")) {
                try {
                    UserDAO dao = new UserDAO(conn);
                    dao.delete(u.getUsername());  // suppression en BDD
                    users.remove(u);               // suppression en liste locale
                    root.setCenter(createContent());  // rafraîchir l'affichage
                } catch (SQLException ex) {
                    alert("Erreur", "Suppression impossible");
                    ex.printStackTrace();
                }
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox box = new HBox(15, name, roleCombo, saveRoleBtn, spacer, delete);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12));
        box.setStyle(cardItemStyle());

        return box;
    }

    /* ================= SPECTACLES ================= */
    private VBox createSpectaclesCard() {
        Label title = sectionTitle("Gestion des spectacles");

        Button add = primaryButton("➕ Ajouter un spectacle");
        add.setOnAction(e -> openForm(null));

        VBox list = new VBox(15);

        if (spectacles.isEmpty()) {
            list.getChildren().add(new Label("Aucun spectacle"));
        } else {
            for (Spectacle s : spectacles) {
                list.getChildren().add(spectacleCard(s));
            }
        }

        VBox content = new VBox(15, add, list);
        return card(title, content);
    }

    private VBox spectacleCard(Spectacle s) {
        Label name = new Label(s.getNom());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label info = new Label(
                "📅 " + s.getDate() +
                "   📍 " + s.getLieu() +
                "   💰 " + s.getPrix() + " €"
        );
        info.setStyle("-fx-text-fill: #555;");

        Button edit = secondaryButton("Modifier");
        Button delete = dangerButton("Supprimer");

        edit.setOnAction(e -> openForm(s));
        delete.setOnAction(e -> deleteSpectacle(s));

        HBox actions = new HBox(10, edit, delete);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox box = new VBox(8, name, info, actions);
        box.setPadding(new Insets(14));
        box.setStyle(cardItemStyle());

        return box;
    }

    /* ================= UI HELPERS ================= */
    private VBox card(Label title, Node content) {
        VBox box = new VBox(20, title, content);
        box.setPadding(new Insets(20));
        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 14, 0, 0, 6);"
        );
        return box;
    }

    private String cardItemStyle() {
        return
                "-fx-background-color: #fafafa;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #ddd;";
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return l;
    }

    private Button primaryButton(String t) {
        return styledButton(t, "#007aff", "white");
    }

    private Button secondaryButton(String t) {
        return styledButton(t, "#e0e0e0", "black");
    }

    private Button dangerButton(String t) {
        return styledButton(t, "#e53935", "white");
    }

    private Button styledButton(String t, String bg, String color) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + color + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-weight: bold;"
        );
        return b;
    }

    /* ================= DATA ================= */

    private void loadUsersFromDB() {
        try {
            UserDAO dao = new UserDAO(conn);
            users.setAll(dao.findAll());  // récupère la liste des utilisateurs depuis la BDD
        } catch (SQLException e) {
            e.printStackTrace();
            alert("Erreur", "Impossible de charger les utilisateurs depuis la base de données");
        }
    }

    private void loadSpectaclesFromDB() {
        try {
            SpectacleDAO dao = new SpectacleDAO(conn);
            spectacles.setAll(dao.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ================= ACTIONS ================= */
    private void openForm(Spectacle s) {
        Stage stage = (Stage) root.getScene().getWindow();
        SpectacleForm form = new SpectacleForm(stage, s);
        form.show();

        if (form.isSaved()) {
            try {
                SpectacleDAO dao = new SpectacleDAO(conn);
                if (s == null) dao.add(form.getSpectacle());
                else dao.update(form.getSpectacle());
                loadSpectaclesFromDB();
                root.setCenter(createContent());
            } catch (SQLException e) {
                alert("Erreur", "Action impossible");
            }
        }
    }

    private void deleteSpectacle(Spectacle s) {
        if (confirm("Supprimer", "Supprimer ce spectacle ?")) {
            try {
                new SpectacleDAO(conn).delete(s.getId());
                loadSpectaclesFromDB();
                root.setCenter(createContent());
            } catch (SQLException e) {
                alert("Erreur", "Suppression impossible");
            }
        }
    }

    /* ================= ALERTS ================= */
    private void alert(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    private boolean confirm(String t, String m) {
        return new Alert(Alert.AlertType.CONFIRMATION, m,
                ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public Pane getView() {
        return root;
    }
}
