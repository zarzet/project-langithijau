package com.studyplanner.utilitas;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

/**
 * Utilitas untuk membuat dialog dengan gaya Material Design 3.
 * Style dialog didefinisikan di CSS (components/dialogs.css) untuk
 * memudahkan kustomisasi dan konsistensi dengan tema aplikasi.
 */
public class PembuatDialogMD3 {

    // CSS class names untuk dialog styling
    private static final String KELAS_DIALOG_PANE = "md3-dialog-pane";
    private static final String KELAS_DIALOG_HEADER = "md3-dialog-header";
    private static final String KELAS_KONTEN_LABEL = "md3-dialog-content-label";
    private static final String KELAS_INPUT = "md3-dialog-input";
    private static final String KELAS_TOMBOL_PRIMER = "md3-dialog-btn-primary";
    private static final String KELAS_TOMBOL_SEKUNDER = "md3-dialog-btn-secondary";
    private static final String KELAS_TOMBOL_BATAL = "md3-dialog-btn-cancel";

    /**
     * Terapkan gaya MD3 ke DialogPane menggunakan CSS classes.
     */
    public static void terapkanGayaMD3(DialogPane dialogPane) {
        // Load stylesheet ke dialog
        String stylesheet = PembuatDialogMD3.class.getResource("/css/style.css").toExternalForm();
        dialogPane.getStylesheets().add(stylesheet);
        
        // Tambah CSS class untuk styling dari dialogs.css
        dialogPane.getStyleClass().add(KELAS_DIALOG_PANE);
        
        // Apply dark mode jika aktif
        if (PreferensiPengguna.getInstance().isDarkMode()) {
            dialogPane.getStyleClass().add("dark-mode");
        }

        // Style header jika ada
        if (dialogPane.getHeader() != null) {
            dialogPane.getHeader().getStyleClass().add(KELAS_DIALOG_HEADER);
        }

        // Style content dengan CSS classes
        if (dialogPane.getContent() != null) {
            terapkanKelasCSSKonten(dialogPane.getContent());
        }

        // Style buttons dengan CSS classes
        dialogPane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (button != null) {
                terapkanKelasCSSButton(button, buttonType);
            }
        });

        // Listener untuk style buttons setelah mereka ditambahkan
        dialogPane.getButtonTypes().addListener((javafx.collections.ListChangeListener<ButtonType>) c -> {
            while (c.next()) {
                for (ButtonType bt : c.getAddedSubList()) {
                    Button button = (Button) dialogPane.lookupButton(bt);
                    if (button != null) {
                        terapkanKelasCSSButton(button, bt);
                    }
                }
            }
        });
    }

    /**
     * Terapkan CSS classes ke konten dialog secara rekursif.
     */
    private static void terapkanKelasCSSKonten(Node node) {
        if (node instanceof Label label) {
            label.getStyleClass().add(KELAS_KONTEN_LABEL);
        } else if (node instanceof javafx.scene.control.TextField tf) {
            tf.getStyleClass().add(KELAS_INPUT);
        } else if (node instanceof javafx.scene.control.TextArea ta) {
            ta.getStyleClass().add(KELAS_INPUT);
        } else if (node instanceof javafx.scene.control.Spinner<?> sp) {
            sp.getStyleClass().add(KELAS_INPUT);
        } else if (node instanceof javafx.scene.control.ComboBox<?> cb) {
            cb.getStyleClass().add(KELAS_INPUT);
        } else if (node instanceof javafx.scene.control.DatePicker dp) {
            dp.getStyleClass().add(KELAS_INPUT);
        } else if (node instanceof javafx.scene.layout.Pane pane) {
            for (Node child : pane.getChildren()) {
                terapkanKelasCSSKonten(child);
            }
        }
    }

    /**
     * Terapkan CSS class ke button berdasarkan tipe button.
     */
    private static void terapkanKelasCSSButton(Button button, ButtonType buttonType) {
        ButtonBar.ButtonData data = buttonType.getButtonData();
        
        // Hapus class button yang mungkin sudah ada
        button.getStyleClass().removeAll(
            KELAS_TOMBOL_PRIMER, 
            KELAS_TOMBOL_SEKUNDER, 
            KELAS_TOMBOL_BATAL
        );
        
        // Tentukan class berdasarkan tipe button
        switch (data) {
            case OK_DONE, YES, APPLY -> button.getStyleClass().add(KELAS_TOMBOL_PRIMER);
            case CANCEL_CLOSE, NO -> button.getStyleClass().add(KELAS_TOMBOL_BATAL);
            default -> button.getStyleClass().add(KELAS_TOMBOL_SEKUNDER);
        }
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
