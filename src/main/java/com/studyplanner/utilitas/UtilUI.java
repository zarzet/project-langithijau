package com.studyplanner.utilitas;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
}
