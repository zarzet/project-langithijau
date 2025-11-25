package com.studyplanner.utilitas;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

/**
 * Utilitas untuk membuat dialog dengan gaya Material Design 3
 */
public class PembuatDialogMD3 {

    private static final String PRIMARY_COLOR = "#006495";
    private static final String BG_COLOR = "#ffffff";
    private static final String TEXT_COLOR = "#191c20";
    private static final String TEXT_SECONDARY = "#42474e";
    private static final String INPUT_BG = "#f2f3fa";
    private static final String BORDER_COLOR = "#c2c7cf";
    private static final String BUTTON_SECONDARY = "#d2e5f5";

    /**
     * Terapkan gaya MD3 ke DialogPane
     */
    public static void terapkanGayaMD3(DialogPane dialogPane) {
        // Style untuk dialog pane utama
        dialogPane.setStyle(
            "-fx-background-color: " + BG_COLOR + ";" +
            "-fx-background-radius: 24;" +
            "-fx-border-radius: 24;" +
            "-fx-padding: 24;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1;"
        );

        // Tambah shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(16);
        shadow.setOffsetY(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        dialogPane.setEffect(shadow);

        // Style header jika ada
        if (dialogPane.getHeader() != null) {
            dialogPane.getHeader().setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: " + TEXT_COLOR + ";" +
                "-fx-padding: 0 0 8 0;"
            );
        }

        // Style content labels
        if (dialogPane.getContent() != null) {
            styleContentRecursive(dialogPane.getContent());
        }

        // Style buttons
        dialogPane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (button != null) {
                styleButton(button, buttonType);
            }
        });

        // Listener untuk style buttons setelah mereka ditambahkan
        dialogPane.getButtonTypes().addListener((javafx.collections.ListChangeListener<ButtonType>) c -> {
            while (c.next()) {
                for (ButtonType bt : c.getAddedSubList()) {
                    Button button = (Button) dialogPane.lookupButton(bt);
                    if (button != null) {
                        styleButton(button, bt);
                    }
                }
            }
        });
    }

    private static void styleContentRecursive(Node node) {
        if (node instanceof Label label) {
            label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-wrap-text: true;"
            );
        } else if (node instanceof javafx.scene.control.TextField tf) {
            tf.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;" +
                "-fx-padding: 10 14;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + TEXT_COLOR + ";"
            );
            tf.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused) {
                    tf.setStyle(
                        "-fx-background-color: " + BG_COLOR + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + PRIMARY_COLOR + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 9 13;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + TEXT_COLOR + ";"
                    );
                } else {
                    tf.setStyle(
                        "-fx-background-color: " + INPUT_BG + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 10 14;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + TEXT_COLOR + ";"
                    );
                }
            });
        } else if (node instanceof javafx.scene.control.TextArea ta) {
            ta.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;"
            );
        } else if (node instanceof javafx.scene.control.Spinner<?> sp) {
            sp.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;"
            );
        } else if (node instanceof javafx.scene.control.ComboBox<?> cb) {
            cb.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;" +
                "-fx-padding: 4 8;"
            );
        } else if (node instanceof javafx.scene.control.DatePicker dp) {
            dp.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;"
            );
        } else if (node instanceof javafx.scene.layout.Pane pane) {
            for (Node child : pane.getChildren()) {
                styleContentRecursive(child);
            }
        }
    }

    private static void styleButton(Button button, ButtonType buttonType) {
        boolean isPrimary = buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE;

        String baseStyle;
        if (isPrimary) {
            baseStyle = 
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 24;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 100, 149, 0.2), 3, 0, 0, 1);";
        } else {
            baseStyle = 
                "-fx-background-color: " + BUTTON_SECONDARY + ";" +
                "-fx-text-fill: #0b1d29;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 24;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-cursor: hand;";
        }

        button.setStyle(baseStyle);

        String hoverStyle = isPrimary ?
            baseStyle.replace(PRIMARY_COLOR, "#005580") :
            baseStyle.replace(BUTTON_SECONDARY, "#c0d8eb");

        String pressedStyle = isPrimary ?
            baseStyle.replace(PRIMARY_COLOR, "#004a70") :
            baseStyle.replace(BUTTON_SECONDARY, "#aecce0");

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnMousePressed(e -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(hoverStyle));
    }

    /**
     * Terapkan gaya MD3 ke Dialog
     */
    public static <T> void terapkanGayaMD3(Dialog<T> dialog) {
        try {
            dialog.initStyle(StageStyle.TRANSPARENT);
        } catch (IllegalStateException e) {
            // Dialog sudah ditampilkan
        }
        
        DialogPane dialogPane = dialog.getDialogPane();
        terapkanGayaMD3(dialogPane);
        
        // Set scene fill to transparent
        dialogPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setFill(Color.TRANSPARENT);
            }
        });
    }

    /**
     * Terapkan gaya MD3 ke Alert
     */
    public static void terapkanGayaMD3(Alert alert) {
        try {
            alert.initStyle(StageStyle.TRANSPARENT);
        } catch (IllegalStateException e) {
            // Alert sudah ditampilkan
        }
        
        DialogPane dialogPane = alert.getDialogPane();
        terapkanGayaMD3(dialogPane);
        
        // Set scene fill to transparent
        dialogPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setFill(Color.TRANSPARENT);
            }
        });

        // Ganti icon default dengan icon Material Design
        Node customIcon = buatIkonAlert(alert.getAlertType());
        dialogPane.setGraphic(customIcon);
    }

    /**
     * Buat ikon custom untuk Alert berdasarkan tipe
     */
    private static Node buatIkonAlert(Alert.AlertType tipe) {
        FontIcon icon;
        String bgColor;
        String iconColor;

        switch (tipe) {
            case INFORMATION -> {
                icon = new FontIcon(Material2OutlinedAL.INFO);
                bgColor = "#e3f2fd";
                iconColor = "#1976d2";
            }
            case WARNING -> {
                icon = new FontIcon(Material2OutlinedMZ.WARNING);
                bgColor = "#fff3e0";
                iconColor = "#f57c00";
            }
            case ERROR -> {
                icon = new FontIcon(Material2OutlinedAL.ERROR);
                bgColor = "#ffebee";
                iconColor = "#d32f2f";
            }
            case CONFIRMATION -> {
                icon = new FontIcon(Material2OutlinedAL.HELP);
                bgColor = "#e8f5e9";
                iconColor = "#388e3c";
            }
            default -> {
                icon = new FontIcon(Material2OutlinedAL.INFO);
                bgColor = "#e3f2fd";
                iconColor = "#1976d2";
            }
        }

        icon.setIconSize(28);
        icon.setIconColor(Color.web(iconColor));

        // Background circle
        Circle circle = new Circle(24);
        circle.setFill(Color.web(bgColor));

        StackPane iconContainer = new StackPane(circle, icon);
        iconContainer.setStyle("-fx-padding: 0 16 0 0;");

        return iconContainer;
    }

    /**
     * Buat Alert dengan gaya MD3
     */
    public static Alert buatAlert(Alert.AlertType tipe, String judul, String konten) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(konten);
        terapkanGayaMD3(alert);
        return alert;
    }

    /**
     * Buat Alert dengan header dan konten
     */
    public static Alert buatAlert(Alert.AlertType tipe, String judul, String header, String konten) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(header);
        alert.setContentText(konten);
        terapkanGayaMD3(alert);
        return alert;
    }

    /**
     * Buat Dialog generik dengan gaya MD3
     */
    public static <T> Dialog<T> buatDialog(String judul, String header) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(judul);
        if (header != null && !header.isEmpty()) {
            dialog.setHeaderText(header);
        }
        terapkanGayaMD3(dialog);
        return dialog;
    }

    /**
     * Buat ButtonType untuk dialog
     */
    public static ButtonType buatTombolSimpan() {
        return new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
    }

    public static ButtonType buatTombolBatal() {
        return new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
    }

    public static ButtonType buatTombolHapus() {
        return new ButtonType("Hapus", ButtonBar.ButtonData.OK_DONE);
    }

    public static ButtonType buatTombolTutup() {
        return new ButtonType("Tutup", ButtonBar.ButtonData.OK_DONE);
    }

    public static ButtonType buatTombolOK() {
        return new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    }
}
