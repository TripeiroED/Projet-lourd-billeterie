package billeterie;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

public class UserProfile {

    private VBox mainView = new VBox(30);
    private Connection conn;
    private String username;
    private Runnable onBack;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String currentProfileImagePath = null;

    // Labels affichage
    private Label lblFullname = new Label();
    private Label lblUsername = new Label();
    private Label lblEmail = new Label();
    private Label lblPhone = new Label();
    private Label lblBirthdate = new Label();
    private Label lblAddress = new Label();
    private ImageView profileImageView = new ImageView();

    // Pour mode édition
    private TextField tfFullname = new TextField();
    private TextField tfEmail = new TextField();
    private TextField tfPhone = new TextField();
    private DatePicker dpBirthdate = new DatePicker();
    private TextField tfAddress = new TextField();

    private VBox infoBox = new VBox(18);
    private HBox buttons = new HBox(25);

    private Button btnChangeImage;
    private Button btnEdit;
    private Button btnBack;
    private Button btnSave;
    private Button btnCancel;

    private boolean editMode = false;

    public UserProfile(Connection conn, String username, Runnable onBack) {
        this.conn = conn;
        this.username = username;
        this.onBack = onBack;

        mainView.setPadding(new Insets(40));
        mainView.setStyle("-fx-background-color: #f0f2f5;");
        mainView.setMaxWidth(700);
        mainView.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Mon Profil");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#34495e"));
        mainView.getChildren().add(title);

        setupUI();
        loadUserInfo();
    }

    private void setupUI() {
        infoBox.setPadding(new Insets(25));
        infoBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-radius: 14; " +
            "-fx-background-radius: 14; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 4);"
        );
        infoBox.setMaxWidth(650);

        profileImageView.setFitWidth(140);
        profileImageView.setFitHeight(140);
        profileImageView.setPreserveRatio(false);
        profileImageView.setSmooth(true);
        profileImageView.setClip(new javafx.scene.shape.Circle(70, 70, 70));
        profileImageView.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 1);"
        );

        // Labels style
        lblFullname.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblFullname.setTextFill(Color.web("#2c3e50"));

        Label[] labels = {lblUsername, lblEmail, lblPhone, lblBirthdate, lblAddress};
        for (Label lbl : labels) {
            lbl.setFont(Font.font("Segoe UI", 16));
            lbl.setTextFill(Color.web("#555"));
        }

        btnChangeImage = new Button("\uD83D\uDDBC  Changer l'image");
        btnChangeImage.setStyle(buttonStyle());
        btnChangeImage.setOnMouseEntered(e -> btnChangeImage.setStyle(buttonHoverStyle()));
        btnChangeImage.setOnMouseExited(e -> btnChangeImage.setStyle(buttonStyle()));
        btnChangeImage.setOnAction(e -> changeProfileImage());

        // Boutons bas
        btnEdit = new Button("Modifier mes infos");
        btnEdit.setStyle(buttonStyle());
        btnEdit.setOnMouseEntered(e -> btnEdit.setStyle(buttonHoverStyle()));
        btnEdit.setOnMouseExited(e -> btnEdit.setStyle(buttonStyle()));
        btnEdit.setOnAction(e -> switchToEditMode());

        btnBack = new Button("Retour à l'accueil");
        btnBack.setStyle(buttonStyle());
        btnBack.setOnMouseEntered(e -> btnBack.setStyle(buttonHoverStyle()));
        btnBack.setOnMouseExited(e -> btnBack.setStyle(buttonStyle()));
        btnBack.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        btnSave = new Button("Enregistrer");
        btnSave.setStyle(buttonStyle());
        btnSave.setOnMouseEntered(e -> btnSave.setStyle(buttonHoverStyle()));
        btnSave.setOnMouseExited(e -> btnSave.setStyle(buttonStyle()));
        btnSave.setOnAction(e -> saveChanges());

        btnCancel = new Button("Annuler");
        btnCancel.setStyle(buttonStyle());
        btnCancel.setOnMouseEntered(e -> btnCancel.setStyle(buttonHoverStyle()));
        btnCancel.setOnMouseExited(e -> btnCancel.setStyle(buttonStyle()));
        btnCancel.setOnAction(e -> cancelEdit());

        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20, 0, 0, 0));
        buttons.getChildren().addAll(btnEdit, btnBack);

        mainView.getChildren().addAll(infoBox, buttons);
    }

    private void loadUserInfo() {
        infoBox.getChildren().clear();

        try {
            String sql = "SELECT fullname, username, email, phone, birthdate, address, profile_image_path FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                java.sql.Date birthdateSql = rs.getDate("birthdate");
                String address = rs.getString("address");
                String profileImagePath = rs.getString("profile_image_path");

                lblFullname.setText(fullname != null ? fullname : "Nom non renseigné");
                lblUsername.setText("Nom d'utilisateur : " + username);
                lblEmail.setText("Email : " + (email != null ? email : "non renseigné"));
                lblPhone.setText("Téléphone : " + (phone != null ? phone : "non renseigné"));
                lblBirthdate.setText("Date de naissance : " + (birthdateSql != null ? birthdateSql.toLocalDate().format(DATE_FORMAT) : "non renseignée"));
                lblAddress.setText("Adresse : " + (address != null ? address : "non renseignée"));

                if (profileImagePath != null && !profileImagePath.trim().isEmpty()) {
                    try {
                        Image profileImage = new Image(profileImagePath, true);
                        profileImageView.setImage(profileImage);
                        currentProfileImagePath = profileImagePath;
                    } catch (Exception ex) {
                        System.err.println("Erreur chargement image profil : " + ex.getMessage());
                        setDefaultProfileImage(profileImageView);
                    }
                } else {
                    setDefaultProfileImage(profileImageView);
                }
            } else {
                lblFullname.setText("Utilisateur non trouvé");
                setDefaultProfileImage(profileImageView);
            }
        } catch (SQLException e) {
            lblFullname.setText("Erreur lors du chargement des données");
            e.printStackTrace();
            setDefaultProfileImage(profileImageView);
        }

        HBox profileBox = new HBox(30);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        VBox infoTexts = new VBox(12);
        infoTexts.getChildren().addAll(lblFullname, lblUsername, lblEmail, lblPhone, lblBirthdate, lblAddress, btnChangeImage);

        profileBox.getChildren().addAll(profileImageView, infoTexts);

        infoBox.getChildren().add(profileBox);

        // Mode affichage => boutons Edit + Back
        buttons.getChildren().clear();
        buttons.getChildren().addAll(btnEdit, btnBack);

        editMode = false;
    }

    private void switchToEditMode() {
        editMode = true;
        infoBox.getChildren().clear();

        // Remplir champs avec valeurs actuelles
        tfFullname.setText(lblFullname.getText().equals("Nom non renseigné") ? "" : lblFullname.getText());
        tfEmail.setText(lblEmail.getText().replace("Email : ", "").equals("non renseigné") ? "" : lblEmail.getText().replace("Email : ", ""));
        tfPhone.setText(lblPhone.getText().replace("Téléphone : ", "").equals("non renseigné") ? "" : lblPhone.getText().replace("Téléphone : ", ""));
        tfAddress.setText(lblAddress.getText().replace("Adresse : ", "").equals("non renseignée") ? "" : lblAddress.getText().replace("Adresse : ", ""));
        try {
            String bdStr = lblBirthdate.getText().replace("Date de naissance : ", "");
            LocalDate bd = bdStr.equals("non renseignée") ? null : LocalDate.parse(bdStr, DATE_FORMAT);
            dpBirthdate.setValue(bd);
        } catch (Exception e) {
            dpBirthdate.setValue(null);
        }

        // Création layout formulaire
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(0, 0, 20, 0));

        form.add(new Label("Nom complet :"), 0, 0);
        form.add(tfFullname, 1, 0);

        form.add(new Label("Email :"), 0, 1);
        form.add(tfEmail, 1, 1);

        form.add(new Label("Téléphone :"), 0, 2);
        form.add(tfPhone, 1, 2);

        form.add(new Label("Date de naissance :"), 0, 3);
        form.add(dpBirthdate, 1, 3);

        form.add(new Label("Adresse :"), 0, 4);
        form.add(tfAddress, 1, 4);

        profileImageView.setFitWidth(140);
        profileImageView.setFitHeight(140);
        profileImageView.setPreserveRatio(false);

        HBox profileBox = new HBox(30);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        VBox leftBox = new VBox(12);
        leftBox.getChildren().add(profileImageView);
        leftBox.setAlignment(Pos.TOP_CENTER);

        VBox rightBox = new VBox(12);
        rightBox.getChildren().addAll(form, btnChangeImage);

        profileBox.getChildren().addAll(leftBox, rightBox);

        infoBox.getChildren().add(profileBox);

        buttons.getChildren().clear();
        buttons.getChildren().addAll(btnSave, btnCancel);
    }

    private void saveChanges() {
        // Validation minimale
        String newFullname = tfFullname.getText().trim();
        String newEmail = tfEmail.getText().trim();
        String newPhone = tfPhone.getText().trim();
        LocalDate newBirthdate = dpBirthdate.getValue();
        String newAddress = tfAddress.getText().trim();

        try {
            String sql = "UPDATE users SET fullname=?, email=?, phone=?, birthdate=?, address=?, profile_image_path=? WHERE username=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newFullname.isEmpty() ? null : newFullname);
            stmt.setString(2, newEmail.isEmpty() ? null : newEmail);
            stmt.setString(3, newPhone.isEmpty() ? null : newPhone);
            if (newBirthdate != null) {
                stmt.setDate(4, java.sql.Date.valueOf(newBirthdate));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setString(5, newAddress.isEmpty() ? null : newAddress);
            stmt.setString(6, currentProfileImagePath);
            stmt.setString(7, username);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profil mis à jour avec succès !");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour.");
                alert.showAndWait();
            }

            // Recharge affichage normal
            loadUserInfo();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void cancelEdit() {
        loadUserInfo();
    }

    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(mainView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image newImage = new Image(new FileInputStream(selectedFile));
                profileImageView.setImage(newImage);
                currentProfileImagePath = selectedFile.toURI().toString();

                if (!editMode) {
                    saveProfileImagePath(currentProfileImagePath);
                }

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'image : " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void saveProfileImagePath(String imagePath) {
        try {
            String sqlUpdate = "UPDATE users SET profile_image_path = ? WHERE username = ?";
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, imagePath);
            stmtUpdate.setString(2, username);
            stmtUpdate.executeUpdate();
            System.out.println("Image de profil mise à jour en base.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'image de profil en base : " + e.getMessage());
        }
    }

    private void setDefaultProfileImage(ImageView imageView) {
        InputStream is = getClass().getResourceAsStream("/resources/default_images.jpg");
        if (is != null) {
            Image defaultImage = new Image(is);
            imageView.setImage(defaultImage);
        } else {
            System.err.println("Image par défaut non trouvée : vérifie le chemin !");
            imageView.setImage(null);
        }
    }

    private String buttonStyle() {
        return "-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; " +
               "-fx-font-size: 15px; -fx-font-weight: bold;";
    }

    private String buttonHoverStyle() {
        return "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; " +
               "-fx-font-size: 15px; -fx-font-weight: bold;";
    }

    public VBox getView() {
        return mainView;
    }
}
