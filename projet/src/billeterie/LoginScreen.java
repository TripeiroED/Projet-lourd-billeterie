package billeterie;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginScreen {

    private VBox view;
    private App app;

    public LoginScreen(App app) {
        this.app = app;

        view = new VBox();
        view.getStyleClass().add("root");  // applique la classe CSS root

        Label labelUser = new Label("Utilisateur :");
        labelUser.getStyleClass().add("label");

        TextField userField = new TextField();
        userField.setPromptText("Nom d'utilisateur");
        userField.getStyleClass().add("text-field");

        Label labelPass = new Label("Mot de passe :");
        labelPass.getStyleClass().add("label");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Mot de passe");
        passField.getStyleClass().add("password-field");

        Button btnLogin = new Button("Se connecter");
        btnLogin.getStyleClass().add("button");

        Button btnRegister = new Button("S'inscrire");
        btnRegister.getStyleClass().add("register-button");

        Label message = new Label();

        btnLogin.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                message.setStyle("-fx-text-fill: #ff4c4c; -fx-font-size: 12px;");
                message.setText("Veuillez remplir tous les champs");
                return;
            }

            try (Connection conn = Database.connect()) {
                UserDAO dao = new UserDAO(conn);
                User loggedUser = dao.authenticate(user, pass);

                if (loggedUser != null) {
                    message.setStyle("-fx-text-fill: #00ff00; -fx-font-size: 14px;");
                    message.setText("Connexion réussie !");
                    if ("ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
                        app.showAdminDashboard();
                    } else {
                        app.showUserHome(user);
                    }
                } else {
                    message.setStyle("-fx-text-fill: #ff4c4c; -fx-font-size: 12px;");
                    message.setText("Utilisateur ou mot de passe incorrect");
                }
            } catch (SQLException ex) {
                message.setStyle("-fx-text-fill: #ff4c4c; -fx-font-size: 12px;");
                message.setText("Erreur lors de la connexion à la base");
                ex.printStackTrace();
            }
        });

        btnRegister.setOnAction(e -> app.showRegisterScreen());

        view.getChildren().addAll(
            labelUser, userField,
            labelPass, passField,
            btnLogin, btnRegister,
            message
        );
    }

    public VBox getView() {
        return view;
    }
}
