package billeterie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class SpectacleForm {

    private Stage stage;
    private TextField nomField = new TextField();
    private TextField dateField = new TextField();
    private TextField lieuField = new TextField();
    private TextField prixField = new TextField();

    private Spectacle spectacle;

    private boolean saved = false;

    public SpectacleForm(Stage owner, Spectacle spectacle) {
        this.spectacle = spectacle != null ? spectacle : new Spectacle();

        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(spectacle == null ? "Ajouter Spectacle" : "Modifier Spectacle");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Date (YYYY-MM-DD):"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Lieu:"), 0, 2);
        grid.add(lieuField, 1, 2);
        grid.add(new Label("Prix (€):"), 0, 3);
        grid.add(prixField, 1, 3);

        if (spectacle != null) {
            nomField.setText(spectacle.getNom());
            dateField.setText(spectacle.getDate());
            lieuField.setText(spectacle.getLieu());
            prixField.setText(String.valueOf(spectacle.getPrix()));
        }

        Button btnSave = new Button("Enregistrer");
        btnSave.setOnAction(e -> {
            if (validateInput()) {
                spectacle.setNom(nomField.getText().trim());
                spectacle.setDate(dateField.getText().trim());
                spectacle.setLieu(lieuField.getText().trim());
                spectacle.setPrix(Double.parseDouble(prixField.getText().trim()));

                saved = true;
                stage.close();
            }
        });

        Button btnCancel = new Button("Annuler");
        btnCancel.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, btnSave, btnCancel);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox vbox = new VBox(10, grid, buttons);
        vbox.setPadding(new Insets(10));

        stage.setScene(new Scene(vbox));
    }

    private boolean validateInput() {
        if (nomField.getText().trim().isEmpty() ||
            dateField.getText().trim().isEmpty() ||
            lieuField.getText().trim().isEmpty() ||
            prixField.getText().trim().isEmpty()) {
            showAlert("Tous les champs sont obligatoires.");
            return false;
        }

        try {
            Double.parseDouble(prixField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Prix doit être un nombre valide.");
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

    public boolean isSaved() { return saved; }

    public Spectacle getSpectacle() { return spectacle; }

    public void show() { stage.showAndWait(); }
}
