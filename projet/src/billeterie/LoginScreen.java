package billeterie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginScreen {

    private StackPane root;
    private App app;

    public LoginScreen(App app) {
        this.app = app;

        /* =================== TITRE =================== */
        Label logo = new Label("🎟️ Billetterie");
        logo.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #0f2027;"   // bleu foncé comme WelcomeScreen
        );

        Label subtitle = new Label("Accédez à votre espace");
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #6b6b70;"
        );

        /* =================== FORM =================== */
        VBox form = new VBox(14);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.setMaxWidth(420);

        form.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 30, 0.3, 0, 10);"
        );

        Label labelUser = new Label("Utilisateur");
        labelUser.getStyleClass().add("label");

        TextField userField = new TextField();
        userField.setPromptText("Nom d'utilisateur");
        userField.getStyleClass().add("text-field");

        Label labelPass = new Label("Mot de passe");
        labelPass.getStyleClass().add("label");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Mot de passe");
        passField.getStyleClass().add("password-field");

        Button btnLogin = new Button("Se connecter");
        btnLogin.setStyle(
                "-fx-background-color: #007aff;" +  // bleu vif
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 12 0;" +
                "-fx-cursor: hand;"
        );
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        Button btnRegister = new Button("Créer un compte");
        btnRegister.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #007aff;" +    // même bleu vif
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        btnRegister.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label();

        btnLogin.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                message.setStyle("-fx-text-fill: #ff4c4c;");
                message.setText("Veuillez remplir tous les champs");
                return;
            }

            try (Connection conn = Database.connect()) {
                UserDAO dao = new UserDAO(conn);
                User loggedUser = dao.authenticate(user, pass);

                if (loggedUser != null) {
                    if ("ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
                        app.showAdminDashboard();
                    } else {
                        app.showUserHome(user);
                    }
                } else {
                    message.setStyle("-fx-text-fill: #ff4c4c;");
                    message.setText("Identifiants incorrects");
                }
            } catch (SQLException ex) {
                message.setStyle("-fx-text-fill: #ff4c4c;");
                message.setText("Erreur serveur");
            }
        });

        btnRegister.setOnAction(e -> app.showRegisterScreen());

        form.getChildren().addAll(
                logo,
                subtitle,
                new Separator(),
                labelUser, userField,
                labelPass, passField,
                btnLogin,
                btnRegister,
                message
        );

        /* =================== FOND =================== */
        root = new StackPane(form);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " +
                "#0f2027 0%, " +
                "#203a43 35%, " +
                "#e4e8f0 100%);"  // même dégradé que WelcomeScreen
        );
    }

    public Pane getView() {
        return root;
    }
}
