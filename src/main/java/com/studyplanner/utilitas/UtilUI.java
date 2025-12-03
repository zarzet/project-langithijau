package com.studyplanner.utilitas;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Utility class untuk fungsi UI yang digunakan bersama di berbagai kontroler.
 * Menghindari duplikasi kode untuk session type labels, badge styling, dan dialog alerts.
 */
public final class UtilUI {

    /** Locale Indonesia untuk formatting tanggal */
    public static final Locale LOCALE_ID = Locale.of("id", "ID");

    /** Formatter tanggal standar: "Senin, 22 Nov" */
    public static final DateTimeFormatter FORMAT_TANGGAL_PENDEK =
            DateTimeFormatter.ofPattern("EEEE, dd MMM", LOCALE_ID);

    /** Formatter tanggal lengkap: "Senin, 22 November 2024" */
    public static final DateTimeFormatter FORMAT_TANGGAL_LENGKAP =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", LOCALE_ID);

    private UtilUI() {
        // Utility class, tidak perlu diinstansiasi
    }

    /**
     * Menghitung progress dengan nilai maksimum 1.0.
     *
     * @param nilai nilai saat ini
     * @param total nilai maksimum/target
     * @return progress antara 0.0 dan 1.0
     */
    public static double hitungProgres(int nilai, int total) {
        if (total <= 0) {
            return 0.0;
        }
        return Math.min((double) nilai / total, 1.0);
    }

    /**
     * Mengkonfigurasi label dengan setting standar untuk widget.
     *
     * @param label label yang akan dikonfigurasi
     * @param maxWidth lebar maksimum
     * @param styleClass CSS class yang akan ditambahkan
     */
    public static void konfigurasLabel(Label label, int maxWidth, String styleClass) {
        label.setWrapText(true);
        label.setMaxWidth(maxWidth);
        label.getStyleClass().add(styleClass);
    }

    /**
     * Menghitung jumlah hari dari hari ini menuju tanggal target.
     *
     * @param tanggalTarget tanggal yang dituju
     * @return jumlah hari (positif jika di masa depan, negatif jika sudah lewat)
     */
    public static long hitungHariMenuju(LocalDate tanggalTarget) {
        return ChronoUnit.DAYS.between(LocalDate.now(), tanggalTarget);
    }

    /**
     * Mendapatkan label tipe sesi belajar dalam bahasa Indonesia.
     *
     * @param tipe kode tipe sesi (INITIAL_STUDY, REVIEW, PRACTICE)
     * @return label yang sudah diterjemahkan
     */
    public static String dapatkanLabelTipeSesi(String tipe) {
        return switch (tipe) {
            case "INITIAL_STUDY" -> "Belajar Pertama";
            case "REVIEW" -> "Review";
            case "PRACTICE" -> "Latihan";
            default -> tipe;
        };
    }

    /**
     * Mendapatkan kelas CSS badge berdasarkan tipe sesi.
     *
     * @param tipe kode tipe sesi
     * @return nama kelas CSS untuk badge
     */
    public static String dapatkanKelasBadge(String tipe) {
        return switch (tipe) {
            case "INITIAL_STUDY" -> "badge-initial_study";
            case "REVIEW" -> "badge-review";
            case "PRACTICE" -> "badge-practice";
            default -> "badge-initial_study";
        };
    }

    /**
     * Menampilkan dialog kesalahan.
     *
     * @param pesan pesan kesalahan yang akan ditampilkan
     */
    public static void tampilkanKesalahan(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.ERROR, "Kesalahan", pesan);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog peringatan.
     *
     * @param pesan pesan peringatan yang akan ditampilkan
     */
    public static void tampilkanPeringatan(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.WARNING, "Peringatan", pesan);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog informasi.
     *
     * @param pesan pesan informasi yang akan ditampilkan
     */
    public static void tampilkanInfo(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.INFORMATION, "Informasi", pesan);
        alert.showAndWait();
    }

    /**
     * Menampilkan toast notification tanpa perlu referensi window.
     * Mencari window yang aktif secara otomatis.
     *
     * @param pesan pesan yang akan ditampilkan
     */
    public static void tampilkanToast(String pesan) {
        // Cari window yang sedang fokus
        Window owner = Stage.getWindows().stream()
            .filter(Window::isFocused)
            .findFirst()
            .orElse(Stage.getWindows().isEmpty() ? null : Stage.getWindows().get(0));
        
        tampilkanToast(owner, pesan);
    }

    /**
     * Menampilkan toast notification yang muncul sebentar lalu menghilang.
     * Tidak memblokir interaksi pengguna. Otomatis menyesuaikan tema.
     *
     * @param owner window tempat toast ditampilkan
     * @param pesan pesan yang akan ditampilkan
     */
    public static void tampilkanToast(Window owner, String pesan) {
        if (owner == null) return;
        
        // Cek apakah dark mode aktif
        boolean isDarkMode = PreferensiPengguna.getInstance().isDarkMode();
        
        Popup popup = new Popup();
        
        Label label = new Label(pesan);
        
        // Style sesuai tema
        if (isDarkMode) {
            label.setStyle(
                "-fx-background-color: rgba(40, 45, 51, 0.95);" +
                "-fx-text-fill: #e1e2e9;" +
                "-fx-padding: 12 20;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 14px;" +
                "-fx-border-color: #3a4048;" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 1;"
            );
        } else {
            label.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.98);" +
                "-fx-text-fill: #191c20;" +
                "-fx-padding: 12 20;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 14px;" +
                "-fx-border-color: #c2c7cf;" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
            );
        }
        
        StackPane container = new StackPane(label);
        container.setAlignment(Pos.CENTER);
        
        popup.getContent().add(container);
        popup.setAutoHide(true);
        
        // Posisi di tengah bawah window
        popup.setOnShown(e -> {
            popup.setX(owner.getX() + (owner.getWidth() - container.getWidth()) / 2);
            popup.setY(owner.getY() + owner.getHeight() - 100);
        });
        
        popup.show(owner);
        
        // Auto-hide setelah 2 detik dengan fade out
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(300), container);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(ev -> popup.hide());
            fade.play();
        });
        delay.play();
    }
}
