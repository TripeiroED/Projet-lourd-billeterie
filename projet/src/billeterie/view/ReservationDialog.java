package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.model.Spectacle;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ReservationDialog extends Dialog<Boolean> {

    public ReservationDialog(ReservationController reservationController, String username, Spectacle spectacle) {
        setTitle("Reservation");
        setHeaderText("Confirmer la reservation pour " + spectacle.getNom());
        AppTheme.styleDialog(getDialogPane());

        ButtonType reserveButtonType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

        Label helper = AppTheme.mutedLabel("Places disponibles : " + spectacle.getPlacesDisponibles());
        TextField seatsField = new TextField();
        seatsField.setPromptText("Nombre de places");
        AppTheme.styleField(seatsField);

        VBox content = new VBox(10, helper, seatsField);
        content.setPadding(new Insets(8, 0, 0, 0));
        getDialogPane().setContent(content);

        Node reserveButton = getDialogPane().lookupButton(reserveButtonType);
        reserveButton.setDisable(true);

        seatsField.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                int value = Integer.parseInt(newValue);
                reserveButton.setDisable(value <= 0 || value > spectacle.getPlacesDisponibles());
            } catch (NumberFormatException e) {
                reserveButton.setDisable(true);
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton != reserveButtonType) {
                return false;
            }

            try {
                int requestedSeats = Integer.parseInt(seatsField.getText().trim());
                return requestedSeats > 0
                        && requestedSeats <= spectacle.getPlacesDisponibles()
                        && reservationController.reserverPlace(username, spectacle.getId(), requestedSeats);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
