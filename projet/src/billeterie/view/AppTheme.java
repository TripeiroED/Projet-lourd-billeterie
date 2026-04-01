package billeterie.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Locale;

public final class AppTheme {
    public static final String FONT_FAMILY = "Segoe UI";

    public static final String PAGE_BACKGROUND =
            "-fx-background-color: linear-gradient(to bottom, #f4f7fb 0%, #eef3f9 100%);";
    public static final String SHELL_BACKGROUND =
            "-fx-background-color: #f8fafc;";
    public static final String CARD_STYLE =
            "-fx-background-color: white;" +
            "-fx-background-radius: 18;" +
            "-fx-border-radius: 18;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 20, 0, 0, 8);";
    public static final String SOFT_CARD_STYLE =
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-color: #e2e8f0;";
    public static final String PRIMARY_BUTTON_STYLE =
            "-fx-background-color: #1d4ed8;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 18;" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;";
    public static final String SECONDARY_BUTTON_STYLE =
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 18;" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;";
    public static final String SUCCESS_BUTTON_STYLE =
            "-fx-background-color: #0f766e;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 18;" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;";
    public static final String DANGER_BUTTON_STYLE =
            "-fx-background-color: #dc2626;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 18;" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;";
    public static final String FIELD_STYLE =
            "-fx-background-color: white;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 12;" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-text-fill: #0f172a;";
    public static final String TEXT_AREA_STYLE =
            FIELD_STYLE +
            "-fx-highlight-fill: #bfdbfe;" +
            "-fx-highlight-text-fill: #0f172a;";
    public static final String TITLE_TEXT_STYLE =
            "-fx-text-fill: #0f172a;" +
            "-fx-font-family: '" + FONT_FAMILY + "';" +
            "-fx-font-weight: bold;";
    public static final String MUTED_TEXT_STYLE =
            "-fx-text-fill: #64748b;" +
            "-fx-font-family: '" + FONT_FAMILY + "';";
    public static final String NAV_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #0f172a 0%, #172554 100%);" +
            "-fx-border-color: #1e293b;" +
            "-fx-border-width: 0 1 0 0;";

    private AppTheme() {
    }

    public static void stylePage(Region region) {
        region.setStyle(PAGE_BACKGROUND);
    }

    public static void styleShell(Region region) {
        region.setStyle(SHELL_BACKGROUND);
    }

    public static void styleCard(Region region) {
        region.setStyle(CARD_STYLE);
    }

    public static void styleSoftCard(Region region) {
        region.setStyle(SOFT_CARD_STYLE);
    }

    public static void stylePrimaryButton(Button button) {
        button.setStyle(PRIMARY_BUTTON_STYLE);
    }

    public static void styleSecondaryButton(Button button) {
        button.setStyle(SECONDARY_BUTTON_STYLE);
    }

    public static void styleSuccessButton(Button button) {
        button.setStyle(SUCCESS_BUTTON_STYLE);
    }

    public static void styleDangerButton(Button button) {
        button.setStyle(DANGER_BUTTON_STYLE);
    }

    public static void styleNavButton(Button button, boolean selected) {
        if (selected) {
            button.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.14);" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 12 18;" +
                    "-fx-font-family: '" + FONT_FAMILY + "';" +
                    "-fx-font-weight: bold;" +
                    "-fx-alignment: center-left;" +
                    "-fx-cursor: hand;");
            return;
        }

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: rgba(255,255,255,0.92);" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 12 18;" +
                "-fx-font-family: '" + FONT_FAMILY + "';" +
                "-fx-font-weight: bold;" +
                "-fx-alignment: center-left;" +
                "-fx-cursor: hand;");
    }

    public static void styleField(TextInputControl control) {
        control.setStyle(FIELD_STYLE);
    }

    public static void styleTextArea(TextArea area) {
        area.setStyle(TEXT_AREA_STYLE);
        area.setWrapText(true);
    }

    public static void styleDialog(DialogPane dialogPane) {
        dialogPane.setStyle(PAGE_BACKGROUND);
    }

    public static Label pageTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 30));
        label.setStyle(TITLE_TEXT_STYLE);
        return label;
    }

    public static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 18));
        label.setStyle(TITLE_TEXT_STYLE);
        return label;
    }

    public static Label mutedLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT_FAMILY, 13));
        label.setStyle(MUTED_TEXT_STYLE);
        return label;
    }

    public static VBox createCardBox() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(22));
        styleCard(box);
        return box;
    }

    public static String formatPrice(double value) {
        return String.format(Locale.US, "%.2f EUR", value);
    }
}
