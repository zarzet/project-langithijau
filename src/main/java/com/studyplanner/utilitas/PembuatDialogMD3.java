package com.studyplanner.utilitas;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

/**
 * Utilitas untuk membuat dialog dengan gaya Material Design 3
 */
public class PembuatDialogMD3 {

    private static final String STYLE_MD3_DIALOG = """
        .dialog-pane {
            -fx-background-color: #ffffff;
            -fx-background-radius: 28;
            -fx-padding: 24;
            -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.12), 8, 0, 0, 4);
        }

        .dialog-pane > .header-panel {
            -fx-background-color: transparent;
            -fx-padding: 0 0 16 0;
        }

        .dialog-pane > .header-panel > .label {
            -fx-font-size: 22px;
            -fx-font-weight: 500;
            -fx-text-fill: #191c20;
        }

        .dialog-pane > .content {
            -fx-padding: 16 0;
        }

        .dialog-pane > .content .label {
            -fx-font-size: 14px;
            -fx-text-fill: #42474e;
            -fx-wrap-text: true;
        }

        .dialog-pane > .button-bar > .container {
            -fx-padding: 16 0 0 0;
        }

        .dialog-pane .button {
            -fx-background-radius: 28;
            -fx-cursor: hand;
            -fx-font-size: 14px;
            -fx-font-weight: 500;
            -fx-padding: 10 24;
            -fx-border-width: 0;
        }

        .dialog-pane .button:default {
            -fx-background-color: #006495;
            -fx-text-fill: #ffffff;
            -fx-effect: dropshadow(gaussian, rgba(0, 100, 149, 0.2), 3, 0, 0, 1);
        }

        .dialog-pane .button:default:hover {
            -fx-background-color: #005580;
            -fx-effect: dropshadow(gaussian, rgba(0, 100, 149, 0.3), 4, 0, 0, 2);
        }

        .dialog-pane .button:default:pressed {
            -fx-background-color: #004a70;
        }

        .dialog-pane .button:cancel-button {
            -fx-background-color: #d2e5f5;
            -fx-text-fill: #0b1d29;
        }

        .dialog-pane .button:cancel-button:hover {
            -fx-background-color: #c0d8eb;
        }

        .dialog-pane .button:cancel-button:pressed {
            -fx-background-color: #aecce0;
        }

        .dialog-pane .text-field,
        .dialog-pane .text-area {
            -fx-background-color: #f2f3fa;
            -fx-background-radius: 12;
            -fx-border-color: #c2c7cf;
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-padding: 12 16;
            -fx-font-size: 14px;
            -fx-text-fill: #191c20;
        }

        .dialog-pane .text-field:focused,
        .dialog-pane .text-area:focused {
            -fx-border-color: #006495;
            -fx-border-width: 2;
            -fx-background-color: #ffffff;
        }

        .dialog-pane .spinner {
            -fx-background-color: #f2f3fa;
            -fx-background-radius: 12;
        }

        .dialog-pane .spinner .text-field {
            -fx-background-color: transparent;
            -fx-border-width: 0;
            -fx-background-radius: 12;
        }

        .dialog-pane .spinner .increment-arrow-button,
        .dialog-pane .spinner .decrement-arrow-button {
            -fx-background-color: transparent;
            -fx-background-radius: 12;
        }

        .dialog-pane .spinner .increment-arrow-button:hover,
        .dialog-pane .spinner .decrement-arrow-button:hover {
            -fx-background-color: #e7e9f0;
        }

        .dialog-pane .combo-box {
            -fx-background-color: #f2f3fa;
            -fx-background-radius: 12;
            -fx-border-color: #c2c7cf;
            -fx-border-width: 1;
            -fx-border-radius: 12;
        }

        .dialog-pane .combo-box:focused {
            -fx-border-color: #006495;
            -fx-border-width: 2;
        }

        .dialog-pane .combo-box .arrow-button {
            -fx-background-color: transparent;
            -fx-background-radius: 0 12 12 0;
        }

        .dialog-pane .date-picker {
            -fx-background-color: #f2f3fa;
            -fx-background-radius: 12;
        }

        .dialog-pane .date-picker .text-field {
            -fx-background-color: transparent;
            -fx-border-color: #c2c7cf;
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
        }

        .dialog-pane .date-picker .text-field:focused {
            -fx-border-color: #006495;
            -fx-border-width: 2;
        }

        .dialog-pane .date-picker .arrow-button {
            -fx-background-color: transparent;
            -fx-background-radius: 0 12 12 0;
        }

        .dialog-pane .date-picker .arrow-button:hover {
            -fx-background-color: #e7e9f0;
        }

        .dialog-pane .date-picker-popup {
            -fx-background-color: #ffffff;
            -fx-background-radius: 16;
            -fx-border-color: #c2c7cf;
            -fx-border-width: 1;
            -fx-border-radius: 16;
            -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 8, 0, 0, 4);
        }

        .dialog-pane .radio-button {
            -fx-text-fill: #191c20;
            -fx-font-size: 14px;
        }

        .dialog-pane .radio-button .radio {
            -fx-background-color: #f2f3fa;
            -fx-border-color: #006495;
            -fx-border-width: 2;
            -fx-border-radius: 10;
        }

        .dialog-pane .radio-button:selected .radio {
            -fx-background-color: #006495;
        }

        .dialog-pane .radio-button .dot {
            -fx-background-color: #ffffff;
            -fx-background-radius: 5;
        }
        """;

    /**
     * Terapkan gaya MD3 ke DialogPane
     */
    public static void terapkanGayaMD3(DialogPane dialogPane) {
        dialogPane.setStyle(STYLE_MD3_DIALOG);
    }

    /**
     * Terapkan gaya MD3 ke Dialog
     */
    public static <T> void terapkanGayaMD3(Dialog<T> dialog) {
        dialog.initStyle(StageStyle.TRANSPARENT);
        terapkanGayaMD3(dialog.getDialogPane());
    }

    /**
     * Terapkan gaya MD3 ke Alert
     */
    public static void terapkanGayaMD3(Alert alert) {
        alert.initStyle(StageStyle.TRANSPARENT);
        terapkanGayaMD3(alert.getDialogPane());
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
