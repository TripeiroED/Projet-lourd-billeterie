package billeterie.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class UserHome {
    private final BorderPane mainLayout = new BorderPane();
    private final VBox menuBox = new VBox(12);

    private final UserHomePage userHomePage;
    private final UserDashboard userDashboard;
    private final UserReservations userReservationsView;

    private Button currentSelectedButton;

    public UserHome(App app, String username) {
        Runnable refreshCallback = this::refreshUserData;

        this.userHomePage = new UserHomePage(app, username, refreshCallback);
        this.userDashboard = new UserDashboard(app, username, refreshCallback);
        this.userReservationsView = new UserReservations(app, username, null, refreshCallback);

        AppTheme.styleShell(mainLayout);
        mainLayout.setTop(createHeader(app, username));
        mainLayout.setLeft(createSidebar(app, username));

        showPage(userHomePage.getView());
    }

    private HBox createHeader(App app, String username) {
        HBox header = new HBox(16);
        header.setPadding(new Insets(18, 28, 18, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 0 0 1 0;");

        ImageView logoView = createLogoImageView();

        VBox titleBox = new VBox(2);
        Label appName = new Label("BILLETTERIE");
        appName.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 20));
        appName.setStyle(AppTheme.TITLE_TEXT_STYLE);

        Label subtitle = AppTheme.mutedLabel("Espace utilisateur de " + username);
        titleBox.getChildren().addAll(appName, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("Rafraichir");
        AppTheme.styleSecondaryButton(refreshButton);
        refreshButton.setOnAction(event -> refreshUserData());

        Button logoutButton = new Button("Deconnexion");
        AppTheme.styleDangerButton(logoutButton);
        logoutButton.setOnAction(event -> app.showLoginScreen());

        header.getChildren().addAll(logoView, titleBox, spacer, refreshButton, logoutButton);
        return header;
    }

    private VBox createSidebar(App app, String username) {
        menuBox.setPadding(new Insets(24));
        menuBox.setPrefWidth(230);
        menuBox.setStyle(AppTheme.NAV_STYLE);

        Button homeButton = createMenuButton("Accueil");
        Button spectaclesButton = createMenuButton("Spectacles");
        Button reservationsButton = createMenuButton("Mes reservations");
        Button profileButton = createMenuButton("Profil");

        homeButton.setOnAction(event -> {
            showPage(userHomePage.getView());
            setSelectedButton(homeButton);
        });

        spectaclesButton.setOnAction(event -> {
            showPage(userDashboard.getView());
            setSelectedButton(spectaclesButton);
        });

        reservationsButton.setOnAction(event -> {
            showPage(userReservationsView.getView());
            setSelectedButton(reservationsButton);
        });

        profileButton.setOnAction(event -> {
            UserProfile profileView = new UserProfile(app, username, () -> {
                refreshUserData();
                showPage(userHomePage.getView());
                setSelectedButton(homeButton);
            });
            showPage(profileView.getView());
            setSelectedButton(profileButton);
        });

        menuBox.getChildren().addAll(homeButton, spectaclesButton, reservationsButton, profileButton, new Separator());
        setSelectedButton(homeButton);
        return menuBox;
    }

    private ImageView createLogoImageView() {
        InputStream logoStream = AppResources.openStream("logo.png");
        if (logoStream == null) {
            return new ImageView();
        }

        ImageView imageView = new ImageView(new Image(logoStream));
        imageView.setFitWidth(54);
        imageView.setFitHeight(54);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        AppTheme.styleNavButton(button, false);

        button.setOnMouseEntered(event -> {
            if (button != currentSelectedButton) {
                button.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.08);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 12 18;" +
                        "-fx-font-family: '" + AppTheme.FONT_FAMILY + "';" +
                        "-fx-font-weight: bold;" +
                        "-fx-alignment: center-left;" +
                        "-fx-cursor: hand;");
            }
        });

        button.setOnMouseExited(event -> {
            if (button != currentSelectedButton) {
                AppTheme.styleNavButton(button, false);
            }
        });

        return button;
    }

    private void setSelectedButton(Button button) {
        if (currentSelectedButton != null) {
            AppTheme.styleNavButton(currentSelectedButton, false);
        }
        AppTheme.styleNavButton(button, true);
        currentSelectedButton = button;
    }

    private void showPage(Region view) {
        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);
        BorderPane.setMargin(scrollPane, new Insets(22));
    }

    private void refreshUserData() {
        userHomePage.refreshData();
        userDashboard.refreshData();
        userReservationsView.refreshData();
    }

    public BorderPane getView() {
        return mainLayout;
    }
}
