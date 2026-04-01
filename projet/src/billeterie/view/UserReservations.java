package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.model.Billet;
import billeterie.model.Reservation;
import billeterie.model.Spectacle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class UserReservations {
    private final VBox mainView = new VBox(18);
    private final ReservationController reservationController;
    private final String username;
    private final Spectacle selectedSpectacle;
    private final Runnable onDataChanged;

    private boolean listMode;

    public UserReservations(App app, String username, Spectacle spectacle) {
        this(app, username, spectacle, null);
    }

    public UserReservations(App app, String username, Spectacle spectacle, Runnable onDataChanged) {
        this.reservationController = app.getReservationController();
        this.username = username;
        this.selectedSpectacle = spectacle;
        this.onDataChanged = onDataChanged;

        mainView.setPadding(new Insets(30));
        AppTheme.stylePage(mainView);

        if (spectacle != null) {
            showReservationForm();
        } else {
            showReservationsList();
        }
    }

    public void refreshData() {
        if (listMode) {
            loadReservations();
        }
    }

    private void showReservationForm() {
        listMode = false;
        mainView.getChildren().clear();

        VBox formCard = AppTheme.createCardBox();
        Label title = AppTheme.pageTitle("Reserver un spectacle");
        Label subtitle = AppTheme.mutedLabel("Confirme le nombre de places pour " + selectedSpectacle.getNom() + ".");

        Label seatsLabel = new Label("Nombre de places");
        seatsLabel.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 13));
        seatsLabel.setStyle(AppTheme.TITLE_TEXT_STYLE);

        TextField seatsField = new TextField();
        seatsField.setPromptText("Ex: 2");
        seatsField.setMaxWidth(160);
        AppTheme.styleField(seatsField);

        Button reserveButton = new Button("Generer mes billets");
        AppTheme.stylePrimaryButton(reserveButton);
        reserveButton.setOnAction(event -> handleReservation(seatsField));

        formCard.getChildren().addAll(title, subtitle, seatsLabel, seatsField, reserveButton);
        mainView.getChildren().add(formCard);
    }

    private void handleReservation(TextField seatsField) {
        try {
            int requestedSeats = Integer.parseInt(seatsField.getText().trim());
            boolean success = reservationController.ajouterReservation(username, selectedSpectacle.getId(), requestedSeats);

            if (!success) {
                new Alert(Alert.AlertType.ERROR, "Reservation impossible : nombre de places insuffisant.")
                        .showAndWait();
                return;
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Reservation confirmee. Les billets et QR codes ont ete generes.")
                    .showAndWait();

            showReservationsList();
            notifyDataChanged();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Saisis un nombre de places valide.").showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Une erreur est survenue pendant la reservation.").showAndWait();
        }
    }

    private void showReservationsList() {
        listMode = true;
        mainView.getChildren().clear();

        VBox listCard = AppTheme.createCardBox();
        listCard.getChildren().addAll(
                AppTheme.pageTitle("Mes reservations"),
                AppTheme.mutedLabel("Retrouve tes billets et annule une reservation si besoin."));

        mainView.getChildren().add(listCard);
        loadReservations();
    }

    private void loadReservations() {
        if (mainView.getChildren().isEmpty()) {
            return;
        }

        VBox container = (VBox) mainView.getChildren().get(0);
        if (container.getChildren().size() > 2) {
            container.getChildren().remove(2, container.getChildren().size());
        }

        try {
            List<Reservation> reservations = reservationController.findByUsername(username);
            if (reservations.isEmpty()) {
                container.getChildren().add(AppTheme.mutedLabel("Vous n'avez aucune reservation pour le moment."));
                return;
            }

            VBox list = new VBox(14);
            for (Reservation reservation : reservations) {
                list.getChildren().add(createReservationCard(reservation));
            }
            container.getChildren().add(list);
        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur lors du chargement des reservations.");
            errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-family: '" + AppTheme.FONT_FAMILY + "';");
            container.getChildren().add(errorLabel);
        }
    }

    private HBox createReservationCard(Reservation reservation) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        AppTheme.styleSoftCard(card);

        VBox detailsBox = new VBox(6);

        Label spectacleName = new Label(reservation.getSpectacleName());
        spectacleName.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 18));
        spectacleName.setStyle(AppTheme.TITLE_TEXT_STYLE);

        Label dateLabel = AppTheme.mutedLabel("Date : " + reservation.getDate());
        Label placesLabel = AppTheme.mutedLabel("Nombre de places : " + reservation.getNombrePlaces());
        detailsBox.getChildren().addAll(spectacleName, dateLabel, placesLabel);

        Button ticketsButton = new Button("Voir billets");
        AppTheme.styleSuccessButton(ticketsButton);
        ticketsButton.setOnAction(event -> showBilletsDialog(reservation));

        Button cancelButton = new Button("Annuler");
        AppTheme.styleDangerButton(cancelButton);
        cancelButton.setOnAction(event -> cancelReservation(reservation.getId()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(detailsBox, spacer, ticketsButton, cancelButton);
        return card;
    }

    private void cancelReservation(int reservationId) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment annuler cette reservation ?",
                ButtonType.YES,
                ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean success = reservationController.annulerReservation(reservationId);
                    if (success) {
                        notifyDataChanged();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "La reservation n'a pas pu etre annulee.").showAndWait();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de l'annulation.").showAndWait();
                }
            }
        });
    }

    private void showBilletsDialog(Reservation reservation) {
        try {
            List<Billet> billets = reservationController.findBilletsByReservationId(reservation.getId());
            if (billets.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Aucun billet n'est disponible pour cette reservation.")
                        .showAndWait();
                return;
            }

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Billets de la reservation");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            AppTheme.styleDialog(dialog.getDialogPane());

            VBox content = new VBox(10);
            content.setPadding(new Insets(15));

            for (Billet billet : billets) {
                VBox billetBox = new VBox(4);
                billetBox.setPadding(new Insets(12));
                AppTheme.styleSoftCard(billetBox);

                Label idLabel = new Label("Billet #" + billet.getId());
                idLabel.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 14));
                idLabel.setStyle(AppTheme.TITLE_TEXT_STYLE);

                Label statutLabel = AppTheme.mutedLabel("Statut : " + billet.getStatut());
                Label qrLabel = AppTheme.mutedLabel("QR code : " + new File(billet.getQrCode()).getAbsolutePath());
                qrLabel.setWrapText(true);

                billetBox.getChildren().addAll(idLabel, statutLabel, qrLabel);
                content.getChildren().add(billetBox);
            }

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(560, 320);
            dialog.getDialogPane().setContent(scrollPane);
            dialog.showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des billets.").showAndWait();
        }
    }

    private void notifyDataChanged() {
        if (onDataChanged != null) {
            onDataChanged.run();
        } else {
            refreshData();
        }
    }

    public VBox getView() {
        return mainView;
    }
}
