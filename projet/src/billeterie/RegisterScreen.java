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

        VBox form = new VBox(10);  // espacement vertical réduit
        form.setPadding(new Insets(25, 30, 25, 30)); // padding légèrement réduit
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(420);

        form.setStyle(
            "-fx-font-family: 'Segoe UI', sans-serif;" +
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.3, 0, 6);"
        );

        Label title = new Label("Créer un compte");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 700; -fx-text-fill: #1c1c1e;");

        // Regroupement Nom complet + Nom d'utilisateur côte à côte
        HBox nameBox = new HBox(15);
        TextField fullnameField = createTextField("Nom complet");
        fullnameField.setMaxWidth(190);
        TextField usernameField = createTextField("Nom d'utilisateur");
        usernameField.setMaxWidth(190);
        nameBox.getChildren().addAll(
            new VBox(label("Nom complet"), fullnameField),
            new VBox(label("Nom d'utilisateur"), usernameField)
        );

        TextField emailField = createTextField("Email");
        TextField phoneField = createTextField("Téléphone");

        DatePicker birthdatePicker = new DatePicker();
        birthdatePicker.setPromptText("Date de naissance");
        styleDatePicker(birthdatePicker);

        TextField addressField = createTextField("Adresse");

        // Regroupement Mot de passe + Confirmer mot de passe côte à côte
        HBox passwordBox = new HBox(15);
        PasswordField passwordField = createPasswordField("Mot de passe");
        passwordField.setMaxWidth(190);
        PasswordField confirmPasswordField = createPasswordField("Confirmer mot de passe");
        confirmPasswordField.setMaxWidth(190);
        passwordBox.getChildren().addAll(
            new VBox(label("Mot de passe"), passwordField),
            new VBox(label("Confirmer mot de passe"), confirmPasswordField)
        );

        Label message = new Label();
        message.setWrapText(true);
        message.setMaxWidth(360);

        Button btnRegister = new Button("S'inscrire");
        stylePrimaryButton(btnRegister);

        Button btnBack = new Button("Retour");
        styleSecondaryButton(btnBack);

        // Supprimer contour focus bleu sur btnRegister
        btnRegister.focusedProperty().addListener((obs, oldV, newV) -> {
            if (newV) btnRegister.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 12 18;" +
                "-fx-background-color: #007aff;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-effect: none;"
            );
        });

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

        // Ajout des éléments dans le formulaire
        form.getChildren().addAll(
            title,
            nameBox,
            label("Email"), emailField,
            label("Téléphone"), phoneField,
            label("Date de naissance"), birthdatePicker,
            label("Adresse"), addressField,
            passwordBox,
            btnRegister,
            btnBack,
            message
        );

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

    private HBox label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #1c1c1e;");

        Label star = new Label(" *");
        star.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        HBox box = new HBox(l, star);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

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
            "-fx-padding: 12;" +
            "-fx-border-color: #d0d0d0;" +
            "-fx-border-radius: 10;" +
            "-fx-background-color: white;" +
            "-fx-text-fill: #1c1c1e;"
        );
        c.setMaxWidth(360);
    }

    private void styleDatePicker(DatePicker dp) {
        dp.setPrefWidth(360);
        dp.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-border-color: #d0d0d0;" +
            "-fx-padding: 12;" +
            "-fx-background-color: white;"
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
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-padding: 12 18;" +
            "-fx-background-color: #0051c3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-padding: 12 18;" +
            "-fx-background-color: #007aff;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        ));
    }

    private void styleSecondaryButton(Button b) {
        b.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 16;" +
            "-fx-background-color: #eeeeee;" +
            "-fx-text-fill: #1c1c1e;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 16;" +
            "-fx-background-color: #d0d5dc;" +
            "-fx-text-fill: #1c1c1e;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 16;" +
            "-fx-background-color: #eeeeee;" +
            "-fx-text-fill: #1c1c1e;"
        ));
    }

    public VBox getView() {
        return root;
    }
}
