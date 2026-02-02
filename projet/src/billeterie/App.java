package billeterie;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.application.Platform;
import java.sql.SQLException;

public class App extends Application {

    private Stage primaryStage;
    private Connection conn;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        connectDatabase();
        showLoginScreen();

        primaryStage.setTitle("Billetterie");
        primaryStage.setFullScreen(true);    // plein écran activé UNE SEULE FOIS
        primaryStage.show();
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/billeterie_db",
                "root",
                ""
            );
            System.out.println("Connexion BDD réussie !");
        } catch (SQLException e) {
            System.err.println("⚠ Impossible de se connecter à la base MySQL");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this);

        Scene scene = new Scene(loginScreen.getView());
        scene.getStylesheets().add(getClass().getResource("/resources/login.css").toExternalForm());

        primaryStage.setScene(scene);
    }

    public void showRegisterScreen() {
        RegisterScreen registerScreen = new RegisterScreen(this);
        Scene scene = new Scene(registerScreen.getView());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Inscription Billetterie");
    }

    public void showAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(this, conn);
        Scene scene = new Scene(adminDashboard.getView());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard Administrateur");
    }

    public void showUserHome(String username) {
        UserHome userHome = new UserHome(this, conn, username);
        Scene scene = new Scene(userHome.getView());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Accueil Utilisateur");
    }

    public void showProfilePage(String username) {
        UserProfile profilePage = new UserProfile(conn, username, () -> showUserHome(username));
        Scene scene = new Scene(profilePage.getView());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Profil Utilisateur");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
