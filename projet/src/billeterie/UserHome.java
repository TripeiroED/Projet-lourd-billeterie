package billeterie;

import java.io.InputStream;
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

    private VBox menuBox = new VBox();

    private UserDashboard userDashboard;

    private Button currentSelectedButton = null;

    public UserHome(App app, Connection conn, String username) {
        this.app = app;
        this.conn = conn;
        this.username = username;

        // Header avec logo et texte d'accueil
        HBox headerBox = new HBox(15);
        headerBox.setPadding(new Insets(15, 30, 15, 30));
        headerBox.setStyle(
            "-fx-background-color: #f9f9f9;" +  // même gris clair que dans UserDashboard
            "-fx-border-color: #d1d8e0;" +     // bordure légère gris clair, cohérent
            "-fx-border-width: 0 0 1 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Logo
        ImageView logoView = createLogoImageView();

        // Label d'accueil
        Label lblWelcome = new Label("Bienvenue sur votre espace Billetterie, " + username + " !");
        lblWelcome.setFont(Font.font("Segoe UI", 22));
        lblWelcome.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: 600;"); // bleu foncé cohérent

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(logoView, lblWelcome, spacer);
        mainLayout.setTop(headerBox);

        // Menu latéral
        menuBox.setPadding(new Insets(20));
        menuBox.setSpacing(12);
        menuBox.setStyle("-fx-background-color: #2c3e50;");  // bleu foncé pour le menu, même couleur que UserDashboard btn sélectionné
        menuBox.setPrefWidth(220);

        Button btnAccueil = createMenuButton("Accueil");
        Button btnSpectacles = createMenuButton("Spectacles");
        Button btnReservations = createMenuButton("Mes Réservations");
        Button btnProfil = createMenuButton("Profil");
        Button btnLogout = createMenuButton("Déconnexion");

        menuBox.getChildren().addAll(btnAccueil, btnSpectacles, btnReservations, btnProfil, new Separator(), btnLogout);
        mainLayout.setLeft(menuBox);

        // Initialisation des pages
        userDashboard = new UserDashboard(app, conn, username);
        userHomePage = new UserHomePage(username, conn);

        setCenterContent(userHomePage.getView());
        setSelectedButton(btnAccueil);

        // Actions menu
        btnAccueil.setOnAction(e -> {
            setCenterContent(userHomePage.getView());
            setSelectedButton(btnAccueil);
        });

        btnSpectacles.setOnAction(e -> {
            setCenterContent(userDashboard.getView());
            setSelectedButton(btnSpectacles);
        });

        btnReservations.setOnAction(e -> {
            UserReservations reservationsView = new UserReservations(conn, username);
            setCenterContent(reservationsView.getView());
            setSelectedButton(btnReservations);
        });

        btnProfil.setOnAction(e -> {
            UserProfile profileView = new UserProfile(conn, username, () -> {
                setCenterContent(userHomePage.getView());
                setSelectedButton(btnAccueil);
            });
            setCenterContent(profileView.getView());
            setSelectedButton(btnProfil);
        });

        btnLogout.setOnAction(e -> app.showLoginScreen());
    }

    private ImageView createLogoImageView() {
        InputStream logoStream = getClass().getResourceAsStream("/resources/logo.png");
        if (logoStream == null) {
            System.err.println("Logo non trouvé à /resources/logo.png");
            return new ImageView();
        } else {
            System.out.println("Logo trouvé !");
        }
        Image logoImage = new Image(logoStream);
        ImageView imageView = new ImageView(logoImage);
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void setCenterContent(javafx.scene.Node node) {
        mainLayout.setCenter(node);
        BorderPane.setMargin(node, new Insets(20));
        if (node instanceof Region) {
            ((Region) node).setStyle(
                "-fx-background-color: #f9f9f9; " + // fond blanc cassé pour toutes les vues centrales
                "-fx-padding: 20; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;"
            );
        }
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("Segoe UI", 15));
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 20; " +
            "-fx-alignment: center-left; " +
            "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> {
            if (btn != currentSelectedButton) {
                btn.setStyle(
                    "-fx-background-color: #34495e; " +  // gris-bleu plus foncé au hover
                    "-fx-text-fill: white; " +
                    "-fx-padding: 12 20; " +
                    "-fx-alignment: center-left; " +
                    "-fx-cursor: hand;"
                );
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != currentSelectedButton) {
                btn.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 12 20; " +
                    "-fx-alignment: center-left; " +
                    "-fx-cursor: hand;"
                );
            }
        });

        return btn;
    }

    private void setSelectedButton(Button btn) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 12 20; " +
                "-fx-alignment: center-left; " +
                "-fx-cursor: hand;"
            );
        }
        btn.setStyle(
            "-fx-background-color: #2980b9; " +  // bleu primaire identique aux boutons dans UserDashboard
            "-fx-text-fill: white; " +
            "-fx-padding: 12 20; " +
            "-fx-alignment: center-left; " +
            "-fx-cursor: hand;"
        );
        currentSelectedButton = btn;
    }

    public BorderPane getView() {
        return mainLayout;
    }
}
