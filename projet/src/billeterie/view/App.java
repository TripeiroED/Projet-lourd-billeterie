package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.controller.SpectacleController;
import billeterie.controller.UserController;
import billeterie.model.Database;
import billeterie.model.Spectacle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class App extends Application {

    private Stage primaryStage;
    private Connection conn;

    private UserController userController;
    private SpectacleController spectacleController;
    private ReservationController reservationController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        connectDatabase();
        showWelcomeScreen();

        primaryStage.setTitle("Billetterie");
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private void connectDatabase() {
        try {
            conn = Database.connect();
            userController = new UserController(conn);
            spectacleController = new SpectacleController(conn);
            reservationController = new ReservationController(conn);
            System.out.println("Connexion BDD réussie !");
        } catch (SQLException e) {
            System.err.println("⚠ Impossible de se connecter à la base MySQL");
            e.printStackTrace();
        }
    }

    public UserController getUserController() { return userController; }
    public SpectacleController getSpectacleController() { return spectacleController; }
    public ReservationController getReservationController() { return reservationController; }

    public void showWelcomeScreen() {
        WelcomeScreen welcome = new WelcomeScreen(this);
        primaryStage.setScene(new Scene(welcome.getView()));
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this);
        Scene scene = new Scene(loginScreen.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }

    public void showRegisterScreen() {
        RegisterScreen registerScreen = new RegisterScreen(this);
        Scene scene = new Scene(registerScreen.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Inscription Billetterie");
    }

    public void showAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(this);
        Scene scene = new Scene(adminDashboard.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard Administrateur");
    }

    public void showUserHome(String username) {
        UserHome userHome = new UserHome(this, username);
        Scene scene = new Scene(userHome.getView(), primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Accueil Utilisateur");
    }

    public void showUserProfile(String username) {
        UserProfile profile = new UserProfile(this, username, () -> showUserHome(username));
        primaryStage.setScene(new Scene(profile.getView()));
    }

    public void showUserReservations(String username, Spectacle spectacle) {
        // spectacle peut être null pour juste afficher les réservations
        UserReservations reservations = new UserReservations(this, username, spectacle);
        primaryStage.setScene(new Scene(reservations.getView()));
    }

    public void showSpectacles(String username) {
        showUserReservations(username, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
