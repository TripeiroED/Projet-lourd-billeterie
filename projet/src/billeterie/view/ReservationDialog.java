package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.model.Spectacle;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ReservationDialog extends Dialog<Boolean> {

    private final ReservationController reservationController;
    private String username;
    private Spectacle spectacle;

    public ReservationDialog(ReservationController reservationController, String username, Spectacle spectacle) {
        this.reservationController = reservationController;
        this.username = username;
        this.spectacle = spectacle;

        setTitle("Réservation");
        setHeaderText("Réserver pour : " + spectacle.getNom());

        ButtonType reserverButtonType = new ButtonType("Réserver", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(reserverButtonType, ButtonType.CANCEL);

        TextField placesField = new TextField();
        placesField.setPromptText("Nombre de places");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Nombre de places à réserver (disponibles : " + spectacle.getPlacesDisponibles() + ") :"),
                placesField);
        getDialogPane().setContent(content);

        Node reserverButton = getDialogPane().lookupButton(reserverButtonType);
        reserverButton.setDisable(true);

        placesField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int val = Integer.parseInt(newVal);
                reserverButton.setDisable(val <= 0 || val > spectacle.getPlacesDisponibles());
            } catch (NumberFormatException e) {
                reserverButton.setDisable(true);
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == reserverButtonType) {
                try {
                    int placesDemandees = Integer.parseInt(placesField.getText());
                    if (placesDemandees > 0 && placesDemandees <= spectacle.getPlacesDisponibles()) {
                        boolean success = reservationController.reserverPlace(username, spectacle.getId(),
                                placesDemandees);
                        return success;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }
}
