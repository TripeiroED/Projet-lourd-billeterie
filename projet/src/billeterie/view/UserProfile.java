package billeterie.view;

import billeterie.controller.UserController;
import billeterie.model.UserProfileData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserProfile {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final VBox mainView = new VBox(22);
    private final UserController userController;
    private final String username;
    private final Runnable onBack;

    private final VBox contentCard = AppTheme.createCardBox();
    private final ImageView profileImageView = new ImageView();

    private final Label lblFullname = new Label();
    private final Label lblUsername = new Label();
    private final Label lblEmail = new Label();
    private final Label lblPhone = new Label();
    private final Label lblBirthdate = new Label();
    private final Label lblAddress = new Label();

    private final TextField tfFullname = new TextField();
    private final TextField tfEmail = new TextField();
    private final TextField tfPhone = new TextField();
    private final DatePicker dpBirthdate = new DatePicker();
    private final TextField tfAddress = new TextField();

    private final HBox buttonsBar = new HBox(12);

    private Button btnChangeImage;
    private Button btnEdit;
    private Button btnBack;
    private Button btnSave;
    private Button btnCancel;

    private String currentProfileImagePath;
    private boolean editMode;

    public UserProfile(App app, String username, Runnable onBack) {
        this.userController = app.getUserController();
        this.username = username;
        this.onBack = onBack;

        mainView.setPadding(new Insets(30));
        mainView.setMaxWidth(980);
        AppTheme.stylePage(mainView);

        configureImageView();
        configureInputs();
        configureButtons();

        mainView.getChildren().addAll(createHeroCard(), contentCard, buttonsBar);
        loadUserInfo();
    }

    private VBox createHeroCard() {
        VBox hero = AppTheme.createCardBox();
        hero.setSpacing(8);

        Label title = AppTheme.pageTitle("Mon profil");
        Label subtitle = AppTheme.mutedLabel(
                "Retrouve tes informations personnelles et mets-les a jour facilement.");

        hero.getChildren().addAll(title, subtitle);
        return hero;
    }

    private void configureImageView() {
        profileImageView.setFitWidth(156);
        profileImageView.setFitHeight(156);
        profileImageView.setPreserveRatio(false);
        profileImageView.setSmooth(true);
        profileImageView.setClip(new Circle(78, 78, 78));
        profileImageView.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.18), 14, 0, 0, 4);" +
                "-fx-background-color: white;");
    }

    private void configureInputs() {
        AppTheme.styleField(tfFullname);
        AppTheme.styleField(tfEmail);
        AppTheme.styleField(tfPhone);
        AppTheme.styleField(tfAddress);

        tfFullname.setPromptText("Nom complet");
        tfEmail.setPromptText("Email");
        tfPhone.setPromptText("Telephone");
        tfAddress.setPromptText("Adresse");
        dpBirthdate.setPromptText("Date de naissance");
        dpBirthdate.setStyle(AppTheme.FIELD_STYLE);
        dpBirthdate.setMaxWidth(Double.MAX_VALUE);
    }

    private void configureButtons() {
        btnChangeImage = new Button("Changer la photo");
        AppTheme.styleSecondaryButton(btnChangeImage);
        btnChangeImage.setOnAction(event -> changeProfileImage());

        btnEdit = new Button("Modifier");
        AppTheme.stylePrimaryButton(btnEdit);
        btnEdit.setOnAction(event -> switchToEditMode());

        btnBack = new Button("Retour");
        AppTheme.styleSecondaryButton(btnBack);
        btnBack.setOnAction(event -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        btnSave = new Button("Enregistrer");
        AppTheme.stylePrimaryButton(btnSave);
        btnSave.setOnAction(event -> saveChanges());

        btnCancel = new Button("Annuler");
        AppTheme.styleSecondaryButton(btnCancel);
        btnCancel.setOnAction(event -> cancelEdit());

        buttonsBar.setAlignment(Pos.CENTER_RIGHT);
    }

    private void loadUserInfo() {
        contentCard.getChildren().clear();

        try {
            UserProfileData profile = userController.findProfileByUsername(username);

            if (profile == null) {
                lblFullname.setText("Utilisateur introuvable");
                setDefaultProfileImage(profileImageView);
            } else {
                lblFullname.setText(displayValue(profile.getFullname(), "Nom non renseigne"));
                lblUsername.setText(username);
                lblEmail.setText(displayValue(profile.getEmail(), "Non renseigne"));
                lblPhone.setText(displayValue(profile.getPhone(), "Non renseigne"));
                lblBirthdate.setText(formatBirthdate(profile.getBirthdate()));
                lblAddress.setText(displayValue(profile.getAddress(), "Non renseignee"));
                loadProfileImage(profile.getProfileImagePath());
            }
        } catch (SQLException e) {
            lblFullname.setText("Erreur de chargement");
            lblUsername.setText(username);
            lblEmail.setText("Impossible de charger l'email");
            lblPhone.setText("Impossible de charger le telephone");
            lblBirthdate.setText("Impossible de charger la date");
            lblAddress.setText("Impossible de charger l'adresse");
            setDefaultProfileImage(profileImageView);
        }

        HBox layout = new HBox(28, createIdentityPanel(), createDisplayInfoPanel());
        layout.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(layout.getChildren().get(1), Priority.ALWAYS);

        contentCard.getChildren().add(layout);
        buttonsBar.getChildren().setAll(btnBack, btnEdit);
        editMode = false;
    }

    private VBox createIdentityPanel() {
        VBox panel = new VBox(14);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(260);
        AppTheme.styleSoftCard(panel);

        Label badge = new Label("Compte utilisateur");
        badge.setStyle(
                "-fx-background-color: #dbeafe;" +
                "-fx-text-fill: #1d4ed8;" +
                "-fx-background-radius: 999;" +
                "-fx-padding: 6 12;" +
                "-fx-font-family: '" + AppTheme.FONT_FAMILY + "';" +
                "-fx-font-weight: bold;");

        Label fullname = new Label(lblFullname.getText());
        fullname.setWrapText(true);
        fullname.setTextFill(Color.web("#0f172a"));
        fullname.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 24));
        fullname.setAlignment(Pos.CENTER);

        Label usernameValue = AppTheme.mutedLabel("@" + lblUsername.getText());
        usernameValue.setFont(Font.font(AppTheme.FONT_FAMILY, 14));

        panel.getChildren().addAll(profileImageView, badge, fullname, usernameValue, btnChangeImage);
        return panel;
    }

    private VBox createDisplayInfoPanel() {
        VBox panel = new VBox(16);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(18));
        AppTheme.styleSoftCard(panel);
        HBox.setHgrow(panel, Priority.ALWAYS);

        Label sectionTitle = AppTheme.sectionTitle("Informations personnelles");
        Label helper = AppTheme.mutedLabel("Les informations ci-dessous sont visibles uniquement dans ton espace.");

        panel.getChildren().addAll(
                sectionTitle,
                helper,
                createInfoRow("Nom complet", lblFullname.getText()),
                createInfoRow("Nom d'utilisateur", lblUsername.getText()),
                createInfoRow("Email", lblEmail.getText()),
                createInfoRow("Telephone", lblPhone.getText()),
                createInfoRow("Date de naissance", lblBirthdate.getText()),
                createInfoRow("Adresse", lblAddress.getText()));

        return panel;
    }

    private VBox createInfoRow(String labelText, String valueText) {
        VBox row = new VBox(4);
        row.setPadding(new Insets(10, 0, 10, 0));
        row.setStyle("-fx-border-color: transparent transparent #e2e8f0 transparent;");

        Label label = new Label(labelText);
        label.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 13));
        label.setStyle("-fx-text-fill: #334155;");

        Label value = new Label(valueText);
        value.setWrapText(true);
        value.setFont(Font.font(AppTheme.FONT_FAMILY, 15));
        value.setStyle("-fx-text-fill: #0f172a;");

        row.getChildren().addAll(label, value);
        return row;
    }

    private void switchToEditMode() {
        editMode = true;
        contentCard.getChildren().clear();

        tfFullname.setText(lblFullname.getText().equals("Nom non renseigne") ? "" : lblFullname.getText());
        tfEmail.setText(lblEmail.getText().equals("Non renseigne") ? "" : lblEmail.getText());
        tfPhone.setText(lblPhone.getText().equals("Non renseigne") ? "" : lblPhone.getText());
        tfAddress.setText(lblAddress.getText().equals("Non renseignee") ? "" : lblAddress.getText());

        try {
            dpBirthdate.setValue(lblBirthdate.getText().equals("Non renseignee")
                    ? null
                    : LocalDate.parse(lblBirthdate.getText(), DATE_FORMAT));
        } catch (Exception e) {
            dpBirthdate.setValue(null);
        }

        VBox identityPanel = createIdentityPanel();
        VBox formPanel = createEditPanel();

        HBox layout = new HBox(28, identityPanel, formPanel);
        layout.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        contentCard.getChildren().add(layout);
        buttonsBar.getChildren().setAll(btnCancel, btnSave);
    }

    private VBox createEditPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(18));
        AppTheme.styleSoftCard(panel);
        HBox.setHgrow(panel, Priority.ALWAYS);

        Label title = AppTheme.sectionTitle("Modifier mes informations");
        Label helper = AppTheme.mutedLabel("Mets a jour les champs ci-dessous puis enregistre.");

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(14);

        form.add(createFormLabel("Nom complet"), 0, 0);
        form.add(tfFullname, 1, 0);
        form.add(createFormLabel("Email"), 0, 1);
        form.add(tfEmail, 1, 1);
        form.add(createFormLabel("Telephone"), 0, 2);
        form.add(tfPhone, 1, 2);
        form.add(createFormLabel("Date de naissance"), 0, 3);
        form.add(dpBirthdate, 1, 3);
        form.add(createFormLabel("Adresse"), 0, 4);
        form.add(tfAddress, 1, 4);

        ColumnConstraintsHelper.setGrow(form);

        panel.getChildren().addAll(title, helper, form);
        return panel;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(AppTheme.FONT_FAMILY, FontWeight.BOLD, 13));
        label.setStyle("-fx-text-fill: #334155;");
        return label;
    }

    private void saveChanges() {
        String newFullname = normalizeEmpty(tfFullname.getText());
        String newEmail = normalizeEmpty(tfEmail.getText());
        String newPhone = normalizeEmpty(tfPhone.getText());
        LocalDate newBirthdate = dpBirthdate.getValue();
        String newAddress = normalizeEmpty(tfAddress.getText());

        try {
            boolean success = userController.updateProfile(
                    username,
                    newFullname,
                    newEmail,
                    newPhone,
                    newBirthdate,
                    newAddress);

            if (success) {
                if (currentProfileImagePath != null) {
                    userController.updateProfileImage(username, currentProfileImagePath);
                }
                new Alert(Alert.AlertType.INFORMATION, "Profil mis a jour avec succes.").showAndWait();
                loadUserInfo();
            } else {
                new Alert(Alert.AlertType.ERROR, "La mise a jour a echoue.").showAndWait();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise a jour : " + e.getMessage()).showAndWait();
        }
    }

    private void cancelEdit() {
        loadUserInfo();
    }

    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(mainView.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        try {
            Image newImage = new Image(new FileInputStream(selectedFile));
            profileImageView.setImage(newImage);
            currentProfileImagePath = selectedFile.toURI().toString();

            if (!editMode) {
                userController.updateProfileImage(username, currentProfileImagePath);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'image : " + e.getMessage()).showAndWait();
        }
    }

    private void loadProfileImage(String profileImagePath) {
        if (profileImagePath != null && !profileImagePath.isBlank()) {
            try {
                profileImageView.setImage(new Image(profileImagePath, true));
                currentProfileImagePath = profileImagePath;
                return;
            } catch (Exception e) {
                System.err.println("Erreur chargement image profil : " + e.getMessage());
            }
        }

        currentProfileImagePath = null;
        setDefaultProfileImage(profileImageView);
    }

    private void setDefaultProfileImage(ImageView imageView) {
        InputStream stream = AppResources.openStream("default_images.jpg");
        if (stream == null) {
            imageView.setImage(null);
            return;
        }

        imageView.setImage(new Image(stream));
    }

    private String formatBirthdate(String birthdate) {
        if (birthdate == null || birthdate.isBlank()) {
            return "Non renseignee";
        }

        try {
            return LocalDate.parse(birthdate).format(DATE_FORMAT);
        } catch (Exception e) {
            return birthdate;
        }
    }

    private String displayValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String normalizeEmpty(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public VBox getView() {
        return mainView;
    }

    private static final class ColumnConstraintsHelper {
        private ColumnConstraintsHelper() {
        }

        private static void setGrow(GridPane form) {
            javafx.scene.layout.ColumnConstraints left = new javafx.scene.layout.ColumnConstraints();
            left.setMinWidth(150);

            javafx.scene.layout.ColumnConstraints right = new javafx.scene.layout.ColumnConstraints();
            right.setHgrow(Priority.ALWAYS);

            form.getColumnConstraints().setAll(left, right);
        }
    }
}
