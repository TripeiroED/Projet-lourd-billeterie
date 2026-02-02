package billeterie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegisterScreen {

    private VBox root;
    private App app;

    public RegisterScreen(App app) {
        this.app = app;

        // ===== FORMULAIRE =====
        VBox form = new VBox(12);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(420);

        form.setStyle(
            "-fx-font-family: \"Segoe UI\", sans-serif;" +
            "-fx-background-color: white;" +
            "-fx-background-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.3, 0, 6);"
        );

        Label title = new Label("Créer un compte");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // ===== CHAMPS =====
        TextField fullnameField = createTextField("Nom complet");
        TextField usernameField = createTextField("Nom d'utilisateur");
        TextField emailField = createTextField("Email");
        TextField phoneField = createTextField("Téléphone");

        DatePicker birthdatePicker = new DatePicker();
        birthdatePicker.setPromptText("Date de naissance");
        styleDatePicker(birthdatePicker);

        TextField addressField = createTextField("Adresse");
        PasswordField passwordField = createPasswordField("Mot de passe");
        PasswordField confirmPasswordField = createPasswordField("Confirmer mot de passe");

        Label message = new Label();
        message.setWrapText(true);
        message.setMaxWidth(360);

        Button btnRegister = new Button("S'inscrire");
        stylePrimaryButton(btnRegister);

        Button btnBack = new Button("Retour");
        styleButton(btnBack);

        // ===== ACTION =====
        btnRegister.setOnAction(e -> {
            message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            String fullname = fullnameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            LocalDate birthdate = birthdatePicker.getValue();
            String address = addressField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (fullname.isEmpty() || username.isEmpty() || email.isEmpty()
                    || phone.isEmpty() || birthdate == null || address.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                message.setText("Veuillez remplir tous les champs obligatoires (*)");
                return;
            }

            if (!password.equals(confirmPassword)) {
                message.setText("Les mots de passe ne correspondent pas.");
                return;
            }

            try (Connection conn = Database.connect()) {
                UserDAO dao = new UserDAO(conn);

                boolean success = dao.register(
                        username,
                        password,
                        "USER",
                        fullname,
                        email,
                        phone,
                        birthdate,
                        address
                );

                if (success) {
                    message.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    message.setText("Inscription réussie !");
                    app.showLoginScreen();
                } else {
                    message.setText("Erreur lors de l'inscription.");
                }

            } catch (SQLException ex) {
                message.setText("Nom d'utilisateur ou email déjà utilisé.");
            }
        });

        btnBack.setOnAction(e -> app.showLoginScreen());

        // ===== AJOUT FORM =====
        form.getChildren().addAll(
                title,
                label("Nom complet"), fullnameField,
                label("Nom d'utilisateur"), usernameField,
                label("Email"), emailField,
                label("Téléphone"), phoneField,
                label("Date de naissance"), birthdatePicker,
                label("Adresse"), addressField,
                label("Mot de passe"), passwordField,
                label("Confirmer mot de passe"), confirmPasswordField,
                btnRegister,
                btnBack,
                message
        );

        // ===== WRAPPER CENTRÉ + SCROLL =====
        VBox wrapper = new VBox(form);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(40));

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: transparent;");

        root = new VBox(scroll);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f6f7f9;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
    }

    // ===== LABEL AVEC * =====
    private HBox label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");

        Label star = new Label(" *");
        star.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        HBox box = new HBox(l, star);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // ===== STYLES =====
    private TextField createTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        styleField(tf);
        return tf;
    }

    private PasswordField createPasswordField(String placeholder) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        styleField(pf);
        return pf;
    }

    private void styleField(Control c) {
        c.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;" +
                "-fx-border-color: #d0d0d0;" +
                "-fx-border-radius: 10;"
        );
        c.setMaxWidth(360);
    }

    private void styleDatePicker(DatePicker dp) {
        dp.setPrefWidth(360);
    }

    private void styleButton(Button b) {
        b.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 16;" +
                "-fx-background-color: #eeeeee;"
        );
    }

    private void stylePrimaryButton(Button b) {
        b.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 12 18;" +
                "-fx-background-color: #007aff;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
        );
    }

    public VBox getView() {
        return root;
    }
}
