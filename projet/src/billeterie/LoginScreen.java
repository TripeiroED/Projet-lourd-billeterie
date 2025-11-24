package billeterie;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginScreen {

    private VBox view;
    private App app;

    public LoginScreen(App app) {
        this.app = app;

        view = new VBox(10);
        view.setPadding(new Insets(20));

        Label labelUser = new Label("Utilisateur :");
        TextField userField = new TextField();

        Label labelPass = new Label("Mot de passe :");
        PasswordField passField = new PasswordField();

        Button btnLogin = new Button("Se connecter");
        Button btnRegister = new Button("S'inscrire");  // <-- Nouveau bouton
        Label message = new Label();

        btnLogin.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                message.setText("Veuillez remplir tous les champs");
                return;
            }

            try (Connection conn = Database.connect()) {
                UserDAO dao = new UserDAO(conn);
                User loggedUser = dao.authenticate(user, pass);  // retourne User ou null
                if (loggedUser != null) {
                    message.setText("Connexion réussie !");
                    if ("ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
                        app.showAdminDashboard();
                    } else {
                        app.showUserHome(user);
                    }
                } else {
                    message.setText("Utilisateur ou mot de passe incorrect");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                message.setText("Erreur lors de la connexion à la base");
            }
        });

        // Action pour le bouton "S'inscrire"
        btnRegister.setOnAction(e -> {
            app.showRegisterScreen();  // Affiche l'écran d'inscription
        });

        // Ajoute les deux boutons et le message dans la vue
        view.getChildren().addAll(labelUser, userField, labelPass, passField, btnLogin, btnRegister, message);
    }

    public VBox getView() {
        return view;
    }
}
