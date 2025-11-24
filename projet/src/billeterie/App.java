package billeterie;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App extends Application {

    private Stage primaryStage;
    private Connection conn;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        connectDatabase();
        showLoginScreen();
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
        Scene scene = new Scene(loginScreen.getView(), 300, 200);
        primaryStage.setTitle("Connexion Billetterie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showRegisterScreen() {
        RegisterScreen registerScreen = new RegisterScreen(this);
        Scene scene = new Scene(registerScreen.getView(), 300, 300);
        primaryStage.setTitle("Inscription Billetterie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(this, conn);
        Scene scene = new Scene(adminDashboard.getView(), 800, 600);
        primaryStage.setTitle("Dashboard Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Nouvelle méthode pour afficher la page d'accueil utilisateur avec menu
    public void showUserHome(String username) {
        UserHome userHome = new UserHome(this, conn, username);
        Scene scene = new Scene(userHome.getView(), 1000, 700);
        primaryStage.setTitle("Espace Utilisateur");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
