package billeterie.view;

import billeterie.model.Spectacle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SpectacleForm {
    private final Stage stage = new Stage();
    private final TextField nomField = new TextField();
    private final TextField dateField = new TextField();
    private final TextField lieuField = new TextField();
    private final TextField prixField = new TextField();
    private final TextField placesField = new TextField();

    private final Spectacle spectacle;
    private boolean saved;

    public SpectacleForm(Stage owner, Spectacle spectacle) {
        this.spectacle = spectacle != null ? spectacle : new Spectacle();

        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(spectacle == null ? "Ajouter un spectacle" : "Modifier le spectacle");

        AppTheme.styleField(nomField);
        AppTheme.styleField(dateField);
        AppTheme.styleField(lieuField);
        AppTheme.styleField(prixField);
        AppTheme.styleField(placesField);

        nomField.setPromptText("Nom du spectacle");
        dateField.setPromptText("YYYY-MM-DD HH:MM:SS");
        lieuField.setPromptText("Lieu");
        prixField.setPromptText("Prix");
        placesField.setPromptText("Places disponibles");

        if (spectacle != null) {
            nomField.setText(this.spectacle.getNom());
            dateField.setText(this.spectacle.getDate());
            lieuField.setText(this.spectacle.getLieu());
            prixField.setText(String.valueOf(this.spectacle.getPrix()));
            placesField.setText(String.valueOf(this.spectacle.getPlacesDisponibles()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        grid.add(createFieldLabel("Nom"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(createFieldLabel("Date"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(createFieldLabel("Lieu"), 0, 2);
        grid.add(lieuField, 1, 2);
        grid.add(createFieldLabel("Prix"), 0, 3);
        grid.add(prixField, 1, 3);
        grid.add(createFieldLabel("Places disponibles"), 0, 4);
        grid.add(placesField, 1, 4);

        Button saveButton = new Button("Enregistrer");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setOnAction(event -> save());

        Button cancelButton = new Button("Annuler");
        AppTheme.styleSecondaryButton(cancelButton);
        cancelButton.setOnAction(event -> stage.close());

        HBox buttons = new HBox(10, cancelButton, saveButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox card = AppTheme.createCardBox();
        card.getChildren().addAll(
                AppTheme.pageTitle(spectacle == null ? "Nouveau spectacle" : "Edition du spectacle"),
                AppTheme.mutedLabel("Renseigne les informations visibles par les utilisateurs."),
                grid,
                buttons);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));
        AppTheme.stylePage(root);

        stage.setScene(new Scene(root, 620, 440));
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

        spectacle.setNom(nomField.getText().trim());
        spectacle.setDate(dateField.getText().trim());
        spectacle.setLieu(lieuField.getText().trim());
        spectacle.setPrix(Double.parseDouble(prixField.getText().trim()));
        spectacle.setPlacesDisponibles(Integer.parseInt(placesField.getText().trim()));

        saved = true;
        stage.close();
    }

    private boolean validateInput() {
        if (nomField.getText().trim().isEmpty()
                || dateField.getText().trim().isEmpty()
                || lieuField.getText().trim().isEmpty()
                || prixField.getText().trim().isEmpty()
                || placesField.getText().trim().isEmpty()) {
            showAlert("Tous les champs sont obligatoires.");
            return false;
        }

        try {
            double price = Double.parseDouble(prixField.getText().trim());
            if (price < 0) {
                showAlert("Le prix doit etre positif.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Le prix doit etre un nombre valide.");
            return false;
        }

        try {
            int seats = Integer.parseInt(placesField.getText().trim());
            if (seats < 0) {
                showAlert("Le nombre de places doit etre positif ou nul.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Le nombre de places doit etre un entier valide.");
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

    public Spectacle getSpectacle() {
        return spectacle;
    }

    public void show() {
        stage.showAndWait();
    }
}
