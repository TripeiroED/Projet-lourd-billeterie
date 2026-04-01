package billeterie.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UserForm {
    private final Stage stage = new Stage();

    private final TextField fullnameField = new TextField();
    private final TextField usernameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField phoneField = new TextField();
    private final DatePicker birthdatePicker = new DatePicker();
    private final TextField addressField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();
    private final ComboBox<String> roleCombo = new ComboBox<>();

    private boolean saved;

    public UserForm(Stage owner) {
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ajouter un utilisateur");

        configureFields();

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);

        form.add(createFieldLabel("Nom complet"), 0, 0);
        form.add(fullnameField, 1, 0);
        form.add(createFieldLabel("Nom d'utilisateur"), 0, 1);
        form.add(usernameField, 1, 1);
        form.add(createFieldLabel("Email"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(createFieldLabel("Telephone"), 0, 3);
        form.add(phoneField, 1, 3);
        form.add(createFieldLabel("Date de naissance"), 0, 4);
        form.add(birthdatePicker, 1, 4);
        form.add(createFieldLabel("Adresse"), 0, 5);
        form.add(addressField, 1, 5);
        form.add(createFieldLabel("Role"), 0, 6);
        form.add(roleCombo, 1, 6);
        form.add(createFieldLabel("Mot de passe"), 0, 7);
        form.add(passwordField, 1, 7);
        form.add(createFieldLabel("Confirmer le mot de passe"), 0, 8);
        form.add(confirmPasswordField, 1, 8);

        GridPaneHelper.setGrow(form);

        Button cancelButton = new Button("Annuler");
        AppTheme.styleSecondaryButton(cancelButton);
        cancelButton.setOnAction(event -> stage.close());

        Button saveButton = new Button("Ajouter");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setOnAction(event -> save());

        HBox actions = new HBox(10, cancelButton, saveButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = AppTheme.createCardBox();
        card.getChildren().addAll(
                AppTheme.pageTitle("Nouvel utilisateur"),
                AppTheme.mutedLabel("Creer un compte utilisateur ou administrateur directement depuis le dashboard."),
                form,
                actions);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));
        AppTheme.stylePage(root);

        stage.setScene(new Scene(root, 700, 620));
    }

    private void configureFields() {
        AppTheme.styleField(fullnameField);
        AppTheme.styleField(usernameField);
        AppTheme.styleField(emailField);
        AppTheme.styleField(phoneField);
        AppTheme.styleField(addressField);
        AppTheme.styleField(passwordField);
        AppTheme.styleField(confirmPasswordField);

        birthdatePicker.setStyle(AppTheme.FIELD_STYLE);
        birthdatePicker.setMaxWidth(Double.MAX_VALUE);

        roleCombo.getItems().addAll("USER", "ADMIN");
        roleCombo.setValue("USER");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setStyle(AppTheme.FIELD_STYLE);

        fullnameField.setPromptText("Nom complet");
        usernameField.setPromptText("Nom d'utilisateur");
        emailField.setPromptText("Email");
        phoneField.setPromptText("Telephone");
        addressField.setPromptText("Adresse");
        passwordField.setPromptText("Mot de passe");
        confirmPasswordField.setPromptText("Confirmer le mot de passe");
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(AppTheme.TITLE_TEXT_STYLE);
        return label;
    }

    private void save() {
        if (!validateInput()) {
            return;
        }

        saved = true;
        stage.close();
    }

    private boolean validateInput() {
        if (getFullname().isEmpty()
                || getUsername().isEmpty()
                || getEmail().isEmpty()
                || getPhone().isEmpty()
                || getAddress().isEmpty()
                || getPassword().isEmpty()
                || confirmPasswordField.getText().isEmpty()
                || getBirthdate() == null) {
            showAlert("Tous les champs sont obligatoires.");
            return false;
        }

        if (!getPassword().equals(confirmPasswordField.getText())) {
            showAlert("Les mots de passe ne correspondent pas.");
            return false;
        }

        if (!getEmail().contains("@")) {
            showAlert("L'email saisi n'est pas valide.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getFullname() {
        return fullnameField.getText().trim();
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public LocalDate getBirthdate() {
        return birthdatePicker.getValue();
    }

    public String getAddress() {
        return addressField.getText().trim();
    }

    public String getRole() {
        return roleCombo.getValue();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void show() {
        stage.showAndWait();
    }

    private static final class GridPaneHelper {
        private GridPaneHelper() {
        }

        private static void setGrow(GridPane grid) {
            javafx.scene.layout.ColumnConstraints left = new javafx.scene.layout.ColumnConstraints();
            left.setMinWidth(180);

            javafx.scene.layout.ColumnConstraints right = new javafx.scene.layout.ColumnConstraints();
            right.setHgrow(Priority.ALWAYS);

            grid.getColumnConstraints().setAll(left, right);
        }
    }
}
