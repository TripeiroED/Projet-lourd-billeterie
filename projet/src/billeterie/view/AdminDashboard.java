package billeterie.view;

import billeterie.controller.SpectacleController;
import billeterie.controller.UserController;
import billeterie.model.Spectacle;
import billeterie.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Locale;

public class AdminDashboard {
    private static final String FONT = "Segoe UI";
    private static final String TITLE = "-fx-text-fill:#0f172a;-fx-font-family:'" + FONT + "';-fx-font-weight:bold;";
    private static final String MUTED = "-fx-text-fill:#64748b;-fx-font-family:'" + FONT + "';";
    private static final String CARD = "-fx-background-color:white;-fx-background-radius:18;-fx-border-radius:18;-fx-border-color:#e2e8f0;-fx-effect:dropshadow(gaussian, rgba(15,23,42,0.08), 20, 0, 0, 8);";
    private static final String FIELD = "-fx-background-color:white;-fx-border-color:#cbd5e1;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:10 12;-fx-font-family:'" + FONT + "';-fx-text-fill:#0f172a;";
    private static final String PRIMARY = "-fx-background-color:#1d4ed8;-fx-text-fill:white;-fx-background-radius:10;-fx-padding:10 18;-fx-font-family:'" + FONT + "';-fx-font-weight:bold;-fx-cursor:hand;";
    private static final String SECONDARY = "-fx-background-color:#e2e8f0;-fx-text-fill:#0f172a;-fx-background-radius:10;-fx-padding:10 18;-fx-font-family:'" + FONT + "';-fx-font-weight:bold;-fx-cursor:hand;";
    private static final String SUCCESS = "-fx-background-color:#0f766e;-fx-text-fill:white;-fx-background-radius:10;-fx-padding:10 18;-fx-font-family:'" + FONT + "';-fx-font-weight:bold;-fx-cursor:hand;";
    private static final String DANGER = "-fx-background-color:#dc2626;-fx-text-fill:white;-fx-background-radius:10;-fx-padding:10 18;-fx-font-family:'" + FONT + "';-fx-font-weight:bold;-fx-cursor:hand;";

    private final App app;
    private final UserController userController;
    private final SpectacleController spectacleController;
    private final BorderPane root = new BorderPane();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Spectacle> spectacles = FXCollections.observableArrayList();

    public AdminDashboard(App app) {
        this.app = app;
        this.userController = app.getUserController();
        this.spectacleController = app.getSpectacleController();
        root.setStyle("-fx-background-color:#f8fafc;");
        refreshData();
        root.setTop(createTopBar());
        root.setCenter(createContent());
        root.sceneProperty().addListener((obs, o, n) -> {
            if (n != null) {
                Platform.runLater(root::requestFocus);
            }
        });
    }

    private HBox createTopBar() {
        HBox bar = new HBox(14);
        bar.setPadding(new Insets(18, 28, 18, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color:rgba(248,250,252,0.95);-fx-border-color:#dbe4f0;-fx-border-width:0 0 1 0;");

        VBox titleBox = new VBox(3);
        Label title = new Label("Administration");
        title.setFont(Font.font(FONT, FontWeight.BOLD, 26));
        title.setStyle(TITLE);
        titleBox.getChildren().addAll(title, muted("Pilotage global des comptes et des spectacles."));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refresh = btn("Rafraichir", SECONDARY);
        refresh.setOnAction(e -> rebuildContent());
        Button logout = btn("Deconnexion", DANGER);
        logout.setOnAction(e -> app.showLoginScreen());

        bar.getChildren().addAll(titleBox, spacer, refresh, logout);
        return bar;
    }

    private ScrollPane createContent() {
        VBox page = new VBox(22);
        page.setPadding(new Insets(28));
        page.setStyle("-fx-background-color:linear-gradient(to bottom, #f4f7fb 0%, #eef3f9 100%);");
        page.getChildren().addAll(createHero(), createMetrics(), createBody());

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        scroll.setPannable(false);
        scroll.setFocusTraversable(false);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;");
        scroll.viewportBoundsProperty().addListener((obs, o, n) -> page.setPrefWidth(Math.max(0, n.getWidth())));
        return scroll;
    }

    private HBox createHero() {
        HBox hero = new HBox(24);
        hero.setPadding(new Insets(28));
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setStyle("-fx-background-color:linear-gradient(to right, #0f172a 0%, #1d4ed8 58%, #0f766e 100%);-fx-background-radius:26;-fx-effect:dropshadow(gaussian, rgba(15,23,42,0.18), 26, 0, 0, 10);");

        VBox left = new VBox(10);
        Label over = new Label("CENTRE DE PILOTAGE");
        over.setStyle("-fx-text-fill:rgba(255,255,255,0.78);-fx-font-family:'" + FONT + "';-fx-font-weight:bold;");
        Label title = new Label("Dashboard admin clair, rapide et actionnable");
        title.setWrapText(true);
        title.setFont(Font.font(FONT, FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill:white;-fx-font-family:'" + FONT + "';");
        Label sub = new Label("Gere les comptes, les roles et le catalogue sans quitter l'ecran.");
        sub.setWrapText(true);
        sub.setStyle("-fx-text-fill:rgba(255,255,255,0.84);-fx-font-family:'" + FONT + "';");
        HBox pills = new HBox(12, pill(users.size() + " comptes"), pill(spectacles.size() + " spectacles"), pill(totalSeats() + " places libres"));
        left.getChildren().addAll(over, title, sub, pills);
        HBox.setHgrow(left, Priority.ALWAYS);

        VBox right = cardBox();
        right.setPrefWidth(270);
        right.setStyle("-fx-background-color:rgba(255,255,255,0.12);-fx-background-radius:20;-fx-border-color:rgba(255,255,255,0.16);-fx-border-radius:20;");
        Label t = new Label("A surveiller");
        t.setFont(Font.font(FONT, FontWeight.BOLD, 16));
        t.setStyle("-fx-text-fill:white;-fx-font-family:'" + FONT + "';");
        right.getChildren().addAll(t, focusLine("Admins", countAdmins()), focusLine("Utilisateurs", countUsers()), focusLine("Spectacles presque pleins", countLowSpectacles()), focusLine("Places libres", totalSeats()));
        hero.getChildren().addAll(left, right);
        return hero;
    }

    private TilePane createMetrics() {
        TilePane grid = new TilePane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPrefColumns(4);
        grid.getChildren().addAll(
                metric("Utilisateurs", users.size(), "Comptes actifs", "#1d4ed8", "#eff6ff"),
                metric("Admins", countAdmins(), "Acces de supervision", "#0f766e", "#ecfdf5"),
                metric("Spectacles", spectacles.size(), "Evenements publies", "#7c3aed", "#f5f3ff"),
                metric("Places libres", totalSeats(), "Disponibilite globale", "#ea580c", "#fff7ed"));
        return grid;
    }

    private VBox metric(String label, int value, String helper, String accent, String bg) {
        VBox box = new VBox(8);
        box.setPrefWidth(240);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color:linear-gradient(to bottom right, white 0%, " + bg + " 100%);-fx-background-radius:20;-fx-border-color:#dbe4f0;-fx-border-radius:20;");
        Label badge = badge(label, "rgba(255,255,255,0.92)", accent);
        Label number = new Label(String.valueOf(value));
        number.setFont(Font.font(FONT, FontWeight.BOLD, 30));
        number.setStyle("-fx-text-fill:" + accent + ";-fx-font-family:'" + FONT + "';");
        box.getChildren().addAll(badge, number, muted(helper));
        return box;
    }

    private HBox createBody() {
        HBox body = new HBox(22);
        VBox left = new VBox(22, createUsersCard(), createSpectaclesCard());
        VBox right = new VBox(22, createActionsCard(), createMonitoringCard());
        right.setPrefWidth(320);
        HBox.setHgrow(left, Priority.ALWAYS);
        body.getChildren().addAll(left, right);
        return body;
    }

    private VBox createUsersCard() {
        VBox box = cardBox();
        box.getChildren().add(sectionHeader("Gestion des utilisateurs", "Roles, comptes et nettoyage rapide.", users.size() + " comptes"));
        if (users.isEmpty()) {
            box.getChildren().add(empty("Aucun utilisateur charge pour le moment."));
            return box;
        }
        VBox list = new VBox(12);
        for (User user : users) {
            list.getChildren().add(createUserRow(user));
        }
        box.getChildren().add(list);
        return box;
    }

    private HBox createUserRow(User user) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(18));
        row.setStyle("-fx-background-color:linear-gradient(to right, white 0%, #f8fbff 100%);-fx-background-radius:18;-fx-border-color:#dbe4f0;-fx-border-radius:18;");

        StackPane avatar = avatar(initials(text(user.getFullname(), user.getUsername())), isAdmin(user) ? "#dbeafe" : "#dcfce7", isAdmin(user) ? "#1d4ed8" : "#15803d");
        VBox identity = new VBox(6);
        Label name = new Label(text(user.getFullname(), user.getUsername()));
        name.setFont(Font.font(FONT, FontWeight.BOLD, 16));
        name.setStyle(TITLE);
        HBox meta = new HBox(12, muted("@" + user.getUsername()), muted(text(user.getEmail(), "Email non renseigne")));
        identity.getChildren().addAll(name, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("ADMIN", "USER");
        role.setValue(user.getRole());
        role.setStyle(FIELD);
        role.setPrefWidth(136);
        role.setFocusTraversable(false);

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.getChildren().addAll(
                new HBox(10, badge(user.getRole(), isAdmin(user) ? "#dbeafe" : "#dcfce7", isAdmin(user) ? "#1d4ed8" : "#15803d"), role),
                new HBox(10, action("Mettre a jour", PRIMARY, e -> updateUserRole(user, role.getValue())), action("Supprimer", DANGER, e -> deleteUser(user))));

        row.getChildren().addAll(avatar, identity, spacer, controls);
        return row;
    }

    private VBox createSpectaclesCard() {
        VBox box = cardBox();
        HBox header = sectionHeader("Catalogue des spectacles", "Prix, disponibilite et publication.", spectacles.size() + " spectacles");
        Button add = btn("Ajouter un spectacle", PRIMARY);
        add.setOnAction(e -> openSpectacleForm(null));
        header.getChildren().add(add);
        box.getChildren().add(header);
        if (spectacles.isEmpty()) {
            box.getChildren().add(empty("Aucun spectacle disponible."));
            return box;
        }
        VBox list = new VBox(12);
        for (Spectacle spectacle : spectacles) {
            list.getChildren().add(createSpectacleRow(spectacle));
        }
        box.getChildren().add(list);
        return box;
    }

    private HBox createSpectacleRow(Spectacle spectacle) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(18));
        row.setStyle("-fx-background-color:linear-gradient(to right, white 0%, #fffdf8 100%);-fx-background-radius:18;-fx-border-color:#e8e4db;-fx-border-radius:18;");

        VBox details = new VBox(8);
        Label name = new Label(spectacle.getNom());
        name.setFont(Font.font(FONT, FontWeight.BOLD, 16));
        name.setStyle(TITLE);
        HBox tags = new HBox(10, badge(formatPrice(spectacle.getPrix()), "#eff6ff", "#1d4ed8"), badge(spectacle.getPlacesDisponibles() + " places", "#ecfdf5", "#15803d"));
        if (spectacle.getPlacesDisponibles() <= 10) {
            tags.getChildren().add(badge("A surveiller", "#fff7ed", "#c2410c"));
        }
        details.getChildren().addAll(name, muted(spectacle.getDate() + " | " + spectacle.getLieu()), tags);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox actions = new VBox(10, action("Modifier", SECONDARY, e -> openSpectacleForm(spectacle)), action("Supprimer", DANGER, e -> deleteSpectacle(spectacle)));
        actions.setAlignment(Pos.CENTER_RIGHT);
        row.getChildren().addAll(avatar(initials(spectacle.getNom()), "#fff7ed", "#ea580c"), details, spacer, actions);
        return row;
    }

    private VBox createActionsCard() {
        VBox box = cardBox();
        box.getChildren().addAll(section("Actions rapides"), muted("Les operations les plus utilisees."));
        Button addUser = btn("Ajouter un utilisateur", PRIMARY);
        addUser.setMaxWidth(Double.MAX_VALUE);
        addUser.setOnAction(e -> openUserForm());
        Button addSpectacle = btn("Ajouter un spectacle", SUCCESS);
        addSpectacle.setMaxWidth(Double.MAX_VALUE);
        addSpectacle.setOnAction(e -> openSpectacleForm(null));
        Button refresh = btn("Recharger les donnees", SECONDARY);
        refresh.setMaxWidth(Double.MAX_VALUE);
        refresh.setOnAction(e -> rebuildContent());
        box.getChildren().addAll(addUser, addSpectacle, refresh);
        return box;
    }

    private VBox createMonitoringCard() {
        VBox box = cardBox();
        box.getChildren().addAll(section("Etat de la plateforme"), muted("Resume rapide pour agir vite."));
        box.getChildren().addAll(monitor("Comptes admin", countAdmins(), "#1d4ed8"), monitor("Comptes utilisateur", countUsers(), "#0f766e"), monitor("Spectacles presque pleins", countLowSpectacles(), "#ea580c"), monitor("Places encore libres", totalSeats(), "#7c3aed"));
        return box;
    }

    private HBox monitor(String labelText, int value, String color) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:14;-fx-border-color:#e2e8f0;-fx-border-radius:14;");
        VBox textBox = new VBox(4);
        Label label = new Label(labelText);
        label.setFont(Font.font(FONT, FontWeight.BOLD, 14));
        label.setStyle(TITLE);
        textBox.getChildren().addAll(label, muted("Mise a jour en direct"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label number = new Label(String.valueOf(value));
        number.setFont(Font.font(FONT, FontWeight.BOLD, 22));
        number.setStyle("-fx-text-fill:" + color + ";-fx-font-family:'" + FONT + "';");
        row.getChildren().addAll(textBox, spacer, number);
        return row;
    }

    private HBox sectionHeader(String title, String helper, String badgeText) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        VBox textBox = new VBox(4, section(title), muted(helper));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(textBox, spacer, badge(badgeText, "#eff6ff", "#1d4ed8"));
        return row;
    }

    private VBox empty(String text) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:16;-fx-border-color:#e2e8f0;-fx-border-radius:16;");
        Label title = new Label("Rien a afficher");
        title.setFont(Font.font(FONT, FontWeight.BOLD, 15));
        title.setStyle(TITLE);
        box.getChildren().addAll(title, muted(text));
        return box;
    }

    private void openUserForm() {
        Stage stage = (Stage) root.getScene().getWindow();
        UserForm form = new UserForm(stage);
        form.show();
        if (!form.isSaved()) {
            return;
        }
        try {
            boolean ok = userController.register(form.getUsername(), form.getPassword(), form.getRole(), form.getFullname(), form.getEmail(), form.getPhone(), form.getBirthdate(), form.getAddress());
            if (ok) {
                rebuildContent();
                info("Succes", "Utilisateur ajoute avec succes.");
            } else {
                info("Erreur", "L'utilisateur n'a pas pu etre ajoute.");
            }
        } catch (SQLException e) {
            info("Erreur", "Nom d'utilisateur ou email deja utilise.");
        }
    }

    private void updateUserRole(User user, String newRole) {
        if (newRole == null || newRole.equals(user.getRole())) {
            return;
        }
        if (!confirm("Modifier role", "Changer le role de " + user.getUsername() + " vers " + newRole + " ?")) {
            return;
        }
        try {
            if (userController.updateRole(user.getUsername(), newRole)) {
                rebuildContent();
                info("Succes", "Role mis a jour.");
            } else {
                info("Erreur", "La mise a jour du role a echoue.");
            }
        } catch (SQLException e) {
            info("Erreur", "Impossible de modifier le role.");
        }
    }

    private void deleteUser(User user) {
        if (!confirm("Supprimer", "Supprimer l'utilisateur " + user.getUsername() + " ?")) {
            return;
        }
        try {
            userController.deleteUser(user.getUsername());
            rebuildContent();
            info("Succes", "Utilisateur supprime.");
        } catch (SQLException e) {
            info("Erreur", "Suppression impossible.");
        }
    }

    private void openSpectacleForm(Spectacle spectacle) {
        Stage stage = (Stage) root.getScene().getWindow();
        SpectacleEditor form = new SpectacleEditor(stage, spectacle);
        form.show();
        if (!form.isSaved()) {
            return;
        }
        try {
            if (spectacle == null) {
                spectacleController.addSpectacle(form.getSpectacle());
                info("Succes", "Spectacle ajoute.");
            } else {
                spectacleController.updateSpectacle(form.getSpectacle());
                info("Succes", "Spectacle mis a jour.");
            }
            rebuildContent();
        } catch (SQLException e) {
            info("Erreur", "Action impossible.");
        }
    }

    private void deleteSpectacle(Spectacle spectacle) {
        if (!confirm("Supprimer", "Supprimer le spectacle " + spectacle.getNom() + " ?")) {
            return;
        }
        try {
            spectacleController.deleteSpectacle(spectacle.getId());
            rebuildContent();
            info("Succes", "Spectacle supprime.");
        } catch (SQLException e) {
            info("Erreur", "Suppression impossible.");
        }
    }

    private void rebuildContent() {
        refreshData();
        root.setCenter(createContent());
        Platform.runLater(root::requestFocus);
    }

    private void refreshData() {
        try {
            users.setAll(userController.findAll());
        } catch (SQLException e) {
            users.clear();
        }
        try {
            spectacles.setAll(spectacleController.findAll());
        } catch (SQLException e) {
            spectacles.clear();
        }
    }

    private void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public Pane getView() {
        return root;
    }

    private Button action(String text, String style, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = btn(text, style);
        button.setOnAction(handler);
        return button;
    }

    private Button btn(String text, String style) {
        Button button = new Button(text);
        button.setStyle(style);
        button.setFocusTraversable(false);
        return button;
    }

    private VBox cardBox() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(22));
        box.setStyle(CARD);
        return box;
    }

    private Label section(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT, FontWeight.BOLD, 18));
        label.setStyle(TITLE);
        return label;
    }

    private Label muted(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT, 13));
        label.setStyle(MUTED);
        return label;
    }

    private Label badge(String text, String bg, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:999;-fx-text-fill:" + color + ";-fx-font-family:'" + FONT + "';-fx-font-weight:bold;-fx-padding:6 12;");
        return label;
    }

    private Label pill(String text) {
        return badge(text, "rgba(255,255,255,0.14)", "white");
    }

    private HBox focusLine(String text, int value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(text);
        label.setStyle("-fx-text-fill:rgba(255,255,255,0.78);-fx-font-family:'" + FONT + "';");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label number = new Label(String.valueOf(value));
        number.setFont(Font.font(FONT, FontWeight.BOLD, 16));
        number.setStyle("-fx-text-fill:white;-fx-font-family:'" + FONT + "';");
        row.getChildren().addAll(label, spacer, number);
        return row;
    }

    private StackPane avatar(String text, String bg, String color) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT, FontWeight.BOLD, 15));
        label.setStyle("-fx-text-fill:" + color + ";-fx-font-family:'" + FONT + "';");
        StackPane box = new StackPane(label);
        box.setMinSize(48, 48);
        box.setPrefSize(48, 48);
        box.setMaxSize(48, 48);
        box.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:999;-fx-border-color:rgba(15,23,42,0.06);-fx-border-radius:999;");
        return box;
    }

    private boolean isAdmin(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private int countAdmins() {
        return (int) users.stream().filter(this::isAdmin).count();
    }

    private int countUsers() {
        return (int) users.stream().filter(user -> !isAdmin(user)).count();
    }

    private int countLowSpectacles() {
        return (int) spectacles.stream().filter(s -> s.getPlacesDisponibles() <= 10).count();
    }

    private int totalSeats() {
        return spectacles.stream().mapToInt(Spectacle::getPlacesDisponibles).sum();
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String initials(String value) {
        String cleaned = text(value, "?").trim();
        String[] parts = cleaned.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    private String formatPrice(double value) {
        return String.format(Locale.US, "%.2f EUR", value);
    }

    private final class SpectacleEditor {
        private final Stage stage = new Stage();
        private final Spectacle spectacle;
        private final TextField nom = new TextField();
        private final TextField date = new TextField();
        private final TextField lieu = new TextField();
        private final TextField prix = new TextField();
        private final TextField places = new TextField();
        private boolean saved;

        private SpectacleEditor(Stage owner, Spectacle spectacle) {
            this.spectacle = spectacle != null ? spectacle : new Spectacle();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(spectacle == null ? "Ajouter un spectacle" : "Modifier le spectacle");

            styleField(nom);
            styleField(date);
            styleField(lieu);
            styleField(prix);
            styleField(places);
            nom.setPromptText("Nom du spectacle");
            date.setPromptText("YYYY-MM-DD HH:MM:SS");
            lieu.setPromptText("Lieu");
            prix.setPromptText("Prix");
            places.setPromptText("Places disponibles");

            if (spectacle != null) {
                nom.setText(this.spectacle.getNom());
                date.setText(this.spectacle.getDate());
                lieu.setText(this.spectacle.getLieu());
                prix.setText(String.valueOf(this.spectacle.getPrix()));
                places.setText(String.valueOf(this.spectacle.getPlacesDisponibles()));
            }

            GridPane grid = new GridPane();
            grid.setHgap(12);
            grid.setVgap(12);
            grid.add(fieldLabel("Nom"), 0, 0);
            grid.add(nom, 1, 0);
            grid.add(fieldLabel("Date"), 0, 1);
            grid.add(date, 1, 1);
            grid.add(fieldLabel("Lieu"), 0, 2);
            grid.add(lieu, 1, 2);
            grid.add(fieldLabel("Prix"), 0, 3);
            grid.add(prix, 1, 3);
            grid.add(fieldLabel("Places disponibles"), 0, 4);
            grid.add(places, 1, 4);

            javafx.scene.layout.ColumnConstraints c1 = new javafx.scene.layout.ColumnConstraints();
            c1.setMinWidth(170);
            javafx.scene.layout.ColumnConstraints c2 = new javafx.scene.layout.ColumnConstraints();
            c2.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().setAll(c1, c2);

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            Button cancel = btn("Annuler", SECONDARY);
            cancel.setOnAction(e -> stage.close());
            Button save = btn("Enregistrer", PRIMARY);
            save.setOnAction(e -> save());
            buttons.getChildren().addAll(cancel, save);

            VBox card = cardBox();
            card.getChildren().addAll(section(spectacle == null ? "Nouveau spectacle" : "Edition du spectacle"), muted("Renseigne les informations visibles par les utilisateurs."), grid, buttons);
            StackPane rootPane = new StackPane(card);
            rootPane.setPadding(new Insets(24));
            rootPane.setStyle("-fx-background-color:linear-gradient(to bottom, #f4f7fb 0%, #eef3f9 100%);");
            stage.setScene(new Scene(rootPane, 620, 440));
        }

        private Label fieldLabel(String text) {
            Label label = new Label(text);
            label.setStyle(TITLE);
            return label;
        }

        private void save() {
            if (!valid()) {
                return;
            }
            spectacle.setNom(nom.getText().trim());
            spectacle.setDate(date.getText().trim());
            spectacle.setLieu(lieu.getText().trim());
            spectacle.setPrix(Double.parseDouble(prix.getText().trim()));
            spectacle.setPlacesDisponibles(Integer.parseInt(places.getText().trim()));
            saved = true;
            stage.close();
        }

        private boolean valid() {
            if (nom.getText().trim().isEmpty() || date.getText().trim().isEmpty() || lieu.getText().trim().isEmpty() || prix.getText().trim().isEmpty() || places.getText().trim().isEmpty()) {
                warn("Tous les champs sont obligatoires.");
                return false;
            }
            try {
                if (Double.parseDouble(prix.getText().trim()) < 0) {
                    warn("Le prix doit etre positif.");
                    return false;
                }
            } catch (NumberFormatException e) {
                warn("Le prix doit etre un nombre valide.");
                return false;
            }
            try {
                if (Integer.parseInt(places.getText().trim()) < 0) {
                    warn("Le nombre de places doit etre positif ou nul.");
                    return false;
                }
            } catch (NumberFormatException e) {
                warn("Le nombre de places doit etre un entier valide.");
                return false;
            }
            return true;
        }

        private void warn(String message) {
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        }

        private boolean isSaved() {
            return saved;
        }

        private Spectacle getSpectacle() {
            return spectacle;
        }

        private void show() {
            stage.showAndWait();
        }
    }

    private void styleField(TextField field) {
        field.setStyle(FIELD);
    }
}
