package com.studyplanner.kontroler.pembantu;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Pembantu untuk mengelola dialog overlay SPA-style.
 * Menampilkan dialog sebagai overlay di dalam window yang sama (bukan window terpisah).
 */
public class PembantuDialogOverlay {

    private final StackPane dialogOverlay;
    private final VBox dialogContainer;
    private boolean isDarkMode;

    /**
     * Konstruktor PembantuDialogOverlay.
     *
     * @param dialogOverlay StackPane overlay
     * @param dialogContainer VBox container untuk konten dialog
     */
    public PembantuDialogOverlay(StackPane dialogOverlay, VBox dialogContainer) {
        this.dialogOverlay = dialogOverlay;
        this.dialogContainer = dialogContainer;
        this.isDarkMode = false;
    }

    /**
     * Set status dark mode.
     */
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    /**
     * Tampilkan dialog overlay dengan konten yang diberikan.
     *
     * @param konten Node yang akan ditampilkan di dalam dialog
     */
    public void tampilkan(Node konten) {
        if (dialogOverlay == null || dialogContainer == null) return;

        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().add(konten);

        // Terapkan dark mode jika aktif
        if (isDarkMode) {
            if (!dialogOverlay.getStyleClass().contains("dark-mode")) {
                dialogOverlay.getStyleClass().add("dark-mode");
            }
        } else {
            dialogOverlay.getStyleClass().remove("dark-mode");
        }

        // Tampilkan dengan animasi fade in
        dialogOverlay.setVisible(true);
        dialogOverlay.setManaged(true);
        dialogOverlay.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Klik di luar dialog untuk menutup
        dialogOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == dialogOverlay) {
                tutup();
            }
        });
    }

    /**
     * Tutup dialog overlay dengan animasi fade out.
     */
    public void tutup() {
        if (dialogOverlay == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), dialogOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> {
            dialogOverlay.setVisible(false);
            dialogOverlay.setManaged(false);
            if (dialogContainer != null) {
                dialogContainer.getChildren().clear();
            }
        });
        fadeOut.play();
    }

    /**
     * Tampilkan dialog konfirmasi.
     *
     * @param judul judul dialog
     * @param pesan pesan dialog
     * @param onKonfirmasi callback saat user mengkonfirmasi
     */
    public void tampilkanKonfirmasi(String judul, String pesan, Runnable onKonfirmasi) {
        VBox konten = new VBox(16);
        konten.setAlignment(Pos.CENTER);
        konten.setStyle("-fx-padding: 8;");

        Label labelJudul = new Label(judul);
        labelJudul.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label labelPesan = new Label(pesan);
        labelPesan.setWrapText(true);
        labelPesan.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        HBox tombolBox = new HBox(12);
        tombolBox.setAlignment(Pos.CENTER_RIGHT);
        tombolBox.setStyle("-fx-padding: 16 0 0 0;");

        Button tombolBatal = new Button("Batal");
        tombolBatal.getStyleClass().add("btn-secondary");
        tombolBatal.setOnAction(_ -> tutup());

        Button tombolOk = new Button("OK");
        tombolOk.getStyleClass().add("btn-primary");
        tombolOk.setOnAction(_ -> {
            tutup();
            if (onKonfirmasi != null) {
                onKonfirmasi.run();
            }
        });

        tombolBox.getChildren().addAll(tombolBatal, tombolOk);
        konten.getChildren().addAll(labelJudul, labelPesan, tombolBox);

        tampilkan(konten);
    }

    /**
     * Tampilkan dialog informasi sederhana.
     *
     * @param judul judul dialog
     * @param pesan pesan dialog
     */
    public void tampilkanInfo(String judul, String pesan) {
        VBox konten = new VBox(16);
        konten.setAlignment(Pos.CENTER);
        konten.setStyle("-fx-padding: 8;");

        Label labelJudul = new Label(judul);
        labelJudul.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label labelPesan = new Label(pesan);
        labelPesan.setWrapText(true);
        labelPesan.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        Button tombolOk = new Button("OK");
        tombolOk.getStyleClass().add("btn-primary");
        tombolOk.setOnAction(_ -> tutup());

        konten.getChildren().addAll(labelJudul, labelPesan, tombolOk);

        tampilkan(konten);
    }
}
