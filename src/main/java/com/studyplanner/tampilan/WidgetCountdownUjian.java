package com.studyplanner.tampilan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.layanan.LayananJadwalUjian;
import com.studyplanner.model.JadwalUjian;
import com.studyplanner.utilitas.PembuatIkon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Widget untuk menampilkan countdown ke ujian terdekat.
 */
public class WidgetCountdownUjian extends VBox {

    private final Label labelNamaUjian;
    private final Label labelCountdown;
    private final Label labelTanggal;
    private final VBox wadahKonten;
    private final VBox wadahKosong;
    private final LayananJadwalUjian layananJadwalUjian;

    public WidgetCountdownUjian() {
        getStyleClass().addAll("countdown-widget", "widget-interactive");
        setSpacing(8);
        setPadding(new Insets(16));
        setAlignment(Pos.CENTER);
        setPrefWidth(200);
        setMinWidth(180);
        setMaxWidth(220);

        this.layananJadwalUjian = new LayananJadwalUjian(ManajerBasisData.dapatkanInstans());

        // Header
        Label judul = new Label("Ujian Terdekat");
        judul.getStyleClass().add("widget-title");

        // Konten countdown
        wadahKonten = new VBox(8);
        wadahKonten.setAlignment(Pos.CENTER);

        labelNamaUjian = new Label();
        labelNamaUjian.getStyleClass().add("countdown-exam-name");
        labelNamaUjian.setWrapText(true);

        labelCountdown = new Label();
        labelCountdown.getStyleClass().add("countdown-number");

        labelTanggal = new Label();
        labelTanggal.getStyleClass().add("countdown-date");

        wadahKonten.getChildren().addAll(labelNamaUjian, labelCountdown, labelTanggal);

        // Tampilan kosong
        wadahKosong = buatTampilanKosong();

        getChildren().addAll(judul, wadahKonten, wadahKosong);

        // Muat data awal
        segarkan();
    }

    private VBox buatTampilanKosong() {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER);
        container.setVisible(false);
        container.setManaged(false);

        var ikon = PembuatIkon.ikonKosongUjian();

        Label label = new Label("Tidak ada ujian terjadwal");
        label.getStyleClass().add("achievement-detail");
        label.setWrapText(true);

        container.getChildren().addAll(ikon, label);
        return container;
    }

    /**
     * Segarkan data widget.
     */
    public void segarkan() {
        try {
            List<JadwalUjian> ujianMendatang = layananJadwalUjian.ambilUjianMendatang();

            if (ujianMendatang.isEmpty()) {
                tampilkanKosong();
            } else {
                // Ambil ujian terdekat
                JadwalUjian ujianTerdekat = ujianMendatang.get(0);
                tampilkanCountdown(ujianTerdekat);
            }
        } catch (Exception e) {
            tampilkanKosong();
        }
    }

    private void tampilkanCountdown(JadwalUjian ujian) {
        wadahKonten.setVisible(true);
        wadahKonten.setManaged(true);
        wadahKosong.setVisible(false);
        wadahKosong.setManaged(false);

        labelNamaUjian.setText(ujian.getJudul());

        long hariTersisa = ChronoUnit.DAYS.between(LocalDate.now(), ujian.getTanggalUjian());

        if (hariTersisa < 0) {
            labelCountdown.setText("Lewat");
            labelCountdown.getStyleClass().removeAll("countdown-urgent", "countdown-warning", "countdown-safe");
            labelCountdown.getStyleClass().add("countdown-urgent");
        } else if (hariTersisa == 0) {
            labelCountdown.setText("HARI INI!");
            labelCountdown.getStyleClass().removeAll("countdown-urgent", "countdown-warning", "countdown-safe");
            labelCountdown.getStyleClass().add("countdown-urgent");
        } else if (hariTersisa == 1) {
            labelCountdown.setText("BESOK!");
            labelCountdown.getStyleClass().removeAll("countdown-urgent", "countdown-warning", "countdown-safe");
            labelCountdown.getStyleClass().add("countdown-urgent");
        } else {
            labelCountdown.setText(hariTersisa + " hari");

            // Styling berdasarkan urgensi
            labelCountdown.getStyleClass().removeAll("countdown-urgent", "countdown-warning", "countdown-safe");
            if (hariTersisa <= 3) {
                labelCountdown.getStyleClass().add("countdown-urgent");
            } else if (hariTersisa <= 7) {
                labelCountdown.getStyleClass().add("countdown-warning");
            } else {
                labelCountdown.getStyleClass().add("countdown-safe");
            }
        }

        // Format tanggal ujian
        labelTanggal.setText(formatTanggal(ujian.getTanggalUjian()));
    }

    private void tampilkanKosong() {
        wadahKonten.setVisible(false);
        wadahKonten.setManaged(false);
        wadahKosong.setVisible(true);
        wadahKosong.setManaged(true);
    }

    private String formatTanggal(LocalDate tanggal) {
        if (tanggal == null) return "";

        String[] namaBulan = {
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
        };

        return tanggal.getDayOfMonth() + " " + 
               namaBulan[tanggal.getMonthValue() - 1] + " " + 
               tanggal.getYear();
    }
}
