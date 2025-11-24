package billeterie;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;

public class RegisterScreen {

    private VBox view;
    private App app;

    public RegisterScreen(App app) {
        this.app = app;

        view = new VBox(10);
        view.setPadding(new Insets(20));

        Label labelUser = new Label("Nom d'utilisateur :");
        TextField userField = new TextField();

        Label labelPass = new Label("Mot de passe :");
        PasswordField passField = new PasswordField();

        Label labelPassConfirm = new Label("Confirmer mot de passe :");
        PasswordField passConfirmField = new PasswordField();

        Button btnRegister = new Button("S'inscrire");
        Button btnBack = new Button("Retour");

        Label message = new Label();

        btnRegister.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            String passwordConfirm = passConfirmField.getText();

            if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                message.setText("Tous les champs sont obligatoires.");
                return;
            }

            if (!password.equals(passwordConfirm)) {
                message.setText("Les mots de passe ne correspondent pas.");
                return;
            }

            try (Connection conn = Database.connect()) {
                UserDAO dao = new UserDAO(conn);
                boolean success = dao.register(username, password, "USER");
                if (success) {
                    message.setText("Inscription réussie ! Connectez-vous.");
                    // Optionnel : revenir à l'écran de connexion après inscription
                    app.showLoginScreen();
                } else {
                    message.setText("Erreur lors de l'inscription.");
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("UNIQUE")) {
                    message.setText("Ce nom d'utilisateur est déjà pris.");
                } else {
                    message.setText("Erreur base de données.");
                    ex.printStackTrace();
                }
            }
        });

        btnBack.setOnAction(e -> app.showLoginScreen());

        view.getChildren().addAll(labelUser, userField,
                                  labelPass, passField,
                                  labelPassConfirm, passConfirmField,
                                  btnRegister, btnBack, message);
    }

    public VBox getView() {
        return view;
    }
}
