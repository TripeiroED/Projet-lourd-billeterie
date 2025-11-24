package billeterie;

import java.sql.Connection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class UserHome {

    private UserHomePage userHomePage;
    private String username;  
    private Connection conn;
    private App app;

    private BorderPane mainLayout = new BorderPane();

    private VBox menuBox = new VBox(15);
    private Label lblWelcome = new Label();

    private UserDashboard userDashboard;

    public UserHome(App app, Connection conn, String username) {
        this.app = app;
        this.conn = conn;
        this.username = username;

        // Header
        lblWelcome.setText("Bienvenue, " + username + " !");
        lblWelcome.setFont(Font.font(20));
        lblWelcome.setPadding(new Insets(15));
        BorderPane.setAlignment(lblWelcome, Pos.CENTER_LEFT);

        mainLayout.setTop(lblWelcome);

        // Menu latéral
        menuBox.setPadding(new Insets(20));
        menuBox.setStyle("-fx-background-color: #2c3e50;");
        menuBox.setPrefWidth(180);

        Button btnAccueil = createMenuButton("Accueil");
        Button btnSpectacles = createMenuButton("Spectacles");
        Button btnReservations = createMenuButton("Mes Réservations");
        Button btnProfil = createMenuButton("Profil");
        Button btnLogout = createMenuButton("Déconnexion");

        menuBox.getChildren().addAll(btnAccueil, btnSpectacles, btnReservations, btnProfil, btnLogout);

        mainLayout.setLeft(menuBox);

        // Initialisation des pages
        userDashboard = new UserDashboard(app, conn, username);
        userHomePage = new UserHomePage(username, conn);

        // Affiche la page d’accueil par défaut
        setCenterContent(userHomePage.getView());

        // Actions menu
        btnAccueil.setOnAction(e -> setCenterContent(userHomePage.getView())); // page accueil
        btnSpectacles.setOnAction(e -> setCenterContent(userDashboard.getView())); // page spectacles

        btnReservations.setOnAction(e -> {
            UserReservations reservationsView = new UserReservations(conn, username);
            setCenterContent(reservationsView.getView());
        });

        btnProfil.setOnAction(e -> {
            Label lbl = new Label("Profil utilisateur (à implémenter)");
            lbl.setPadding(new Insets(20));
            lbl.setStyle("-fx-font-size: 16px;");
            setCenterContent(new StackPane(lbl));
        });

        btnLogout.setOnAction(e -> app.showLoginScreen());
    }

    private void setCenterContent(javafx.scene.Node node) {
        mainLayout.setCenter(node);
        BorderPane.setMargin(node, new Insets(20));
        if (node instanceof Region) {
            ((Region) node).setStyle("-fx-background-color: #f9f9f9; -fx-padding: 15; -fx-border-radius: 8; -fx-background-radius: 8;");
        }
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10 15; " +
            "-fx-alignment: center-left;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #34495e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10 15; " +
            "-fx-alignment: center-left;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10 15; " +
            "-fx-alignment: center-left;"
        ));

        return btn;
    }

    public BorderPane getView() {
        return mainLayout;
    }
}
