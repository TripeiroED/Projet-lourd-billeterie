package billeterie;

import java.sql.Connection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class UserHome {

    private UserHomePage userHomePage;
    private String username;  
    private Connection conn;
    private App app;

    private BorderPane mainLayout = new BorderPane();

    private VBox menuBox = new VBox(15);

    private UserDashboard userDashboard;

    public UserHome(App app, Connection conn, String username) {
        this.app = app;
        this.conn = conn;
        this.username = username;

        // Header with logo and welcome label
        HBox headerBox = new HBox(15);
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;"); // bottom border
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Load logo image
        ImageView logoView = createLogoImageView();

        // Welcome label
        Label lblWelcome = new Label("Bienvenue, " + username + " !");
        lblWelcome.setFont(Font.font("Arial", 20));
        lblWelcome.setStyle("-fx-text-fill: #2c3e50;");

        headerBox.getChildren().addAll(logoView, lblWelcome);

        mainLayout.setTop(headerBox);

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
            UserProfile profileView = new UserProfile(conn, username, () -> {
                setCenterContent(userHomePage.getView());
            });
            setCenterContent(profileView.getView());
        });


        btnLogout.setOnAction(e -> app.showLoginScreen());
    }

    private ImageView createLogoImageView() {
        // Remplace le chemin par celui de ton logo
        Image logoImage;
        try {
            logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        } catch (Exception e) {
            // Si le logo n'est pas trouvé, crée un ImageView vide
            System.err.println("Logo non trouvé, vérifie le chemin de l'image.");
            return new ImageView();
        }
        ImageView imageView = new ImageView(logoImage);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);
        return imageView;
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
