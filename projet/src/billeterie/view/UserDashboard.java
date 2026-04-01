package billeterie.view;

import billeterie.controller.ReservationController;
import billeterie.controller.SpectacleController;
import billeterie.model.Spectacle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserDashboard {
    private static final int ITEMS_PER_PAGE = 8;

    private final SpectacleController spectacleController;
    private final ReservationController reservationController;
    private final String username;
    private final Runnable onDataChanged;

    private final ObservableList<Spectacle> spectacles = FXCollections.observableArrayList();
    private final VBox mainView = new VBox(20);
    private final FlowPane cardsPane = new FlowPane();
    private final TextArea detailArea = new TextArea();
    private final TextField searchField = new TextField();
    private final Label resultLabel = AppTheme.mutedLabel("Chargement...");
    private final Button btnPrev = new Button("Precedent");
    private final Button btnNext = new Button("Suivant");

    private int currentPage = 0;

    public UserDashboard(App app, String username) {
        this(app, username, null);
    }

    public UserDashboard(App app, String username, Runnable onDataChanged) {
        this.spectacleController = app.getSpectacleController();
        this.reservationController = app.getReservationController();
        this.username = username;
        this.onDataChanged = onDataChanged;

        mainView.setPadding(new Insets(30));
        AppTheme.stylePage(mainView);

        setupSearchField();
        setupCardsPane();
        setupDetailArea();
        setupPaginationButtons();

        VBox catalogCard = AppTheme.createCardBox();
        catalogCard.getChildren().addAll(
                AppTheme.sectionTitle("Catalogue"),
                createToolbar(),
                cardsPane,
                createPaginationBox());

        VBox detailCard = AppTheme.createCardBox();
        detailCard.getChildren().addAll(AppTheme.sectionTitle("Details du spectacle"), detailArea);

        mainView.getChildren().addAll(
                createHeroSection(),
                catalogCard,
                detailCard);

        refreshData();
    }

    public void refreshData() {
        loadSpectaclesFromDB();
        updateCards();
    }

    private VBox createHeroSection() {
        VBox hero = AppTheme.createCardBox();
        hero.setSpacing(8);
        hero.getChildren().addAll(
                AppTheme.pageTitle("Spectacles disponibles"),
                AppTheme.mutedLabel("Recherche, consulte les details et reserve sans quitter ton espace."));
        return hero;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(14);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = AppTheme.mutedLabel("Recherche");
        VBox searchBox = new VBox(6, searchLabel, searchField);
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        VBox countBox = new VBox(6, AppTheme.mutedLabel("Resultats"), resultLabel);
        toolbar.getChildren().addAll(searchBox, countBox);
        return toolbar;
    }

    private HBox createPaginationBox() {
        HBox paginationBox = new HBox(12, btnPrev, btnNext);
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.setPadding(new Insets(4, 0, 0, 0));
        return paginationBox;
    }

    private void setupSearchField() {
        searchField.setPromptText("Rechercher un spectacle par nom");
        AppTheme.styleField(searchField);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 0;
            updateCards();
        });
    }

    private void setupCardsPane() {
        cardsPane.setHgap(18);
        cardsPane.setVgap(18);
        cardsPane.setPrefWrapLength(940);
    }

    private void setupDetailArea() {
        detailArea.setEditable(false);
        detailArea.setPrefRowCount(6);
        AppTheme.styleTextArea(detailArea);
        detailArea.setText("Selectionne un spectacle pour voir ses details.");
    }

    private void setupPaginationButtons() {
        AppTheme.styleSecondaryButton(btnPrev);
        AppTheme.stylePrimaryButton(btnNext);

        btnPrev.setOnAction(event -> {
            if (currentPage > 0) {
                currentPage--;
                updateCards();
            }
        });

        btnNext.setOnAction(event -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < getFilteredSpectacles().size()) {
                currentPage++;
                updateCards();
            }
        });
    }

    private void loadSpectaclesFromDB() {
        try {
            spectacles.setAll(spectacleController.findAll());
        } catch (SQLException e) {
            spectacles.clear();
            detailArea.setText("Impossible de charger les spectacles.");
        }
    }

    private List<Spectacle> getFilteredSpectacles() {
        String filter = searchField.getText();
        if (filter == null || filter.isBlank()) {
            return spectacles;
        }

        String normalizedFilter = filter.toLowerCase();
        return spectacles.stream()
                .filter(spectacle -> spectacle.getNom().toLowerCase().contains(normalizedFilter))
                .collect(Collectors.toList());
    }

    private void updateCards() {
        cardsPane.getChildren().clear();

        List<Spectacle> filtered = getFilteredSpectacles();
        resultLabel.setText(filtered.size() + " spectacle(s)");

        if (filtered.isEmpty()) {
            VBox emptyCard = AppTheme.createCardBox();
            emptyCard.getChildren().addAll(
                    AppTheme.sectionTitle("Aucun resultat"),
                    AppTheme.mutedLabel("Essaie un autre nom ou reinitialise la recherche."));
            emptyCard.setPrefWidth(420);
            cardsPane.getChildren().add(emptyCard);
            btnPrev.setDisable(true);
            btnNext.setDisable(true);
            return;
        }

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filtered.size());

        if (start >= filtered.size()) {
            currentPage = 0;
            start = 0;
            end = Math.min(ITEMS_PER_PAGE, filtered.size());
        }

        for (int i = start; i < end; i++) {
            cardsPane.getChildren().add(createCard(filtered.get(i)));
        }

        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(end >= filtered.size());
    }

    private VBox createCard(Spectacle spectacle) {
        VBox card = AppTheme.createCardBox();
        card.setPrefWidth(280);
        card.setSpacing(10);

        Label title = new Label(spectacle.getNom());
        title.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 18));
        title.setStyle(AppTheme.TITLE_TEXT_STYLE);

        Label date = AppTheme.mutedLabel("Date : " + spectacle.getDate());
        Label location = AppTheme.mutedLabel("Lieu : " + spectacle.getLieu());

        Label price = new Label(AppTheme.formatPrice(spectacle.getPrix()));
        price.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 15));
        price.setStyle("-fx-text-fill: #0f766e; -fx-font-family: '" + AppTheme.FONT_FAMILY + "';");

        Label seats = new Label("Places disponibles : " + spectacle.getPlacesDisponibles());
        seats.setStyle(
                "-fx-background-color: #dbeafe;" +
                "-fx-background-radius: 999;" +
                "-fx-padding: 6 10;" +
                "-fx-text-fill: #1d4ed8;" +
                "-fx-font-family: '" + AppTheme.FONT_FAMILY + "';" +
                "-fx-font-weight: bold;");

        Button reserveButton = new Button(spectacle.getPlacesDisponibles() > 0 ? "Reserver" : "Complet");
        if (spectacle.getPlacesDisponibles() > 0) {
            AppTheme.stylePrimaryButton(reserveButton);
        } else {
            AppTheme.styleSecondaryButton(reserveButton);
            reserveButton.setDisable(true);
        }

        reserveButton.setOnAction(event -> openReservationDialog(spectacle));

        card.getChildren().addAll(title, date, location, price, seats, reserveButton);
        card.setOnMouseClicked(event -> showSpectacleDetails(spectacle));
        return card;
    }

    private void showSpectacleDetails(Spectacle spectacle) {
        detailArea.setText(
                spectacle.getNom() + "\n\n" +
                "Date : " + spectacle.getDate() + "\n" +
                "Lieu : " + spectacle.getLieu() + "\n" +
                "Prix : " + AppTheme.formatPrice(spectacle.getPrix()) + "\n" +
                "Places disponibles : " + spectacle.getPlacesDisponibles() + "\n\n" +
                "La disponibilite et les compteurs sont mis a jour juste apres une reservation ou une annulation.");
    }

    private void openReservationDialog(Spectacle spectacle) {
        ReservationDialog dialog = new ReservationDialog(reservationController, username, spectacle);
        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                detailArea.setText(
                        "Reservation confirmee pour " + spectacle.getNom() + ".\n" +
                        "Les billets ont ete generes et les compteurs ont ete rafraichis.");
                notifyDataChanged();
            }
        });
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
