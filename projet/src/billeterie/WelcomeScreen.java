package billeterie;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

public class WelcomeScreen {

    private StackPane root;
    private App app;

    public WelcomeScreen(App app) {
        this.app = app;

        // Texte principal
        Label title = new Label("🎟️ BILLETTERIE");
        title.setStyle("""
            -fx-font-size: 48px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
        """);

        Label subtitle = new Label("Réservez vos spectacles en toute simplicité");
        subtitle.setStyle("""
            -fx-font-size: 18px;
            -fx-text-fill: rgba(255,255,255,0.85);
        """);

        // Boutons
        Button login = new Button("Se connecter");
        Button register = new Button("Créer un compte");

        stylePrimary(login);
        styleSecondary(register);

        addHoverEffect(login);
        addHoverEffect(register);

        login.setOnAction(e -> app.showLoginScreen());
        register.setOnAction(e -> app.showRegisterScreen());

        VBox content = new VBox(20, title, subtitle, login, register);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        // Fond
        root = new StackPane(content);
        root.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);
        """);
    }

    /* ================= STYLES ================= */

    private void stylePrimary(Button b) {
        b.setStyle("""
            -fx-background-color: #007aff;
            -fx-text-fill: white;
            -fx-font-size: 15px;
            -fx-font-weight: bold;
            -fx-padding: 12 28;
            -fx-background-radius: 30;
        """);
    }

    private void styleSecondary(Button b) {
        b.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: white;
            -fx-border-width: 2;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10 26;
            -fx-background-radius: 30;
            -fx-border-radius: 30;
        """);
    }

    /* ================= HOVER EFFECT ================= */

    private void addHoverEffect(Button button) {
        String baseStyle = button.getStyle();

        button.setOnMouseEntered(e ->
            button.setStyle(baseStyle + """
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 20, 0, 0, 6);
                -fx-translate-y: -3;
                -fx-cursor: hand;
            """)
        );

        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        button.setOnMousePressed(e ->
            button.setStyle(baseStyle + "-fx-translate-y: 1;")
        );
    }

    public StackPane getView() {
        return root;
    }
}
