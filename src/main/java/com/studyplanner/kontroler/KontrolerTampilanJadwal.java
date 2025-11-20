package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOSesiBelajar;
import com.studyplanner.model.SesiBelajar;
import com.studyplanner.utilitas.PembuatDialogMD3;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class KontrolerTampilanJadwal implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button prevWeekBtn;

    @FXML
    private Button nextWeekBtn;

    @FXML
    private Button todayBtn;

    @FXML
    private HBox weekCalendar;

    @FXML
    private VBox scheduleContainer;

    @FXML
    private VBox emptyState;

    @FXML
    private Label selectedDateLabel;

    @FXML
    private Label sessionCountLabel;

    private ManajerBasisData manajerBasisData;
    private DAOSesiBelajar daoSesiBelajar;
    private LocalDate awalMingguSaatIni;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();
        daoSesiBelajar = new DAOSesiBelajar(manajerBasisData);

        LocalDate hariIni = LocalDate.now();
        datePicker.setValue(hariIni);
        awalMingguSaatIni = hariIni.minusDays(hariIni.getDayOfWeek().getValue() - 1);

        // Setup event handlers
        datePicker.setOnAction(_ -> {
            muatJadwal();
            perbaruiKalenderMinggu();
        });

        if (prevWeekBtn != null) {
            prevWeekBtn.setOnAction(_ -> navigasiMinggu(-7));
        }

        if (nextWeekBtn != null) {
            nextWeekBtn.setOnAction(_ -> navigasiMinggu(7));
        }

        if (todayBtn != null) {
            todayBtn.setOnAction(_ -> {
                datePicker.setValue(LocalDate.now());
                awalMingguSaatIni = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
                perbaruiKalenderMinggu();
                muatJadwal();
            });
        }

        perbaruiKalenderMinggu();
        muatJadwal();
    }

    private void navigasiMinggu(int hari) {
        awalMingguSaatIni = awalMingguSaatIni.plusDays(hari);
        datePicker.setValue(awalMingguSaatIni);
        perbaruiKalenderMinggu();
        muatJadwal();
    }

    private void perbaruiKalenderMinggu() {
        if (weekCalendar == null) return;

        weekCalendar.getChildren().clear();
        LocalDate tanggalTerpilih = datePicker.getValue();

        for (int i = 0; i < 7; i++) {
            LocalDate tanggal = awalMingguSaatIni.plusDays(i);
            VBox kartuHari = buatKartuHari(tanggal, tanggal.equals(tanggalTerpilih));
            weekCalendar.getChildren().add(kartuHari);
        }
    }

    private VBox buatKartuHari(LocalDate tanggal, boolean dipilih) {
        VBox kartu = new VBox(8);
        kartu.setAlignment(Pos.CENTER);
        kartu.setPrefWidth(80);
        kartu.setPrefHeight(80);

        // Use CSS classes instead of inline styles
        kartu.getStyleClass().addAll("week-day-card");
        if (dipilih) {
            kartu.getStyleClass().add("selected");
        }

        String namaHari = tanggal.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.of("id", "ID"));
        Label labelHari = new Label(namaHari);
        labelHari.getStyleClass().add("day-name-label");

        Label labelTanggal = new Label(String.valueOf(tanggal.getDayOfMonth()));
        labelTanggal.getStyleClass().add("day-number-label");

        // Check if today
        if (tanggal.equals(LocalDate.now())) {
            Label titikHariIni = new Label("•");
            titikHariIni.getStyleClass().add("today-indicator");
            kartu.getChildren().add(titikHariIni);
        }

        kartu.getChildren().addAll(labelHari, labelTanggal);

        // Click handler
        kartu.setOnMouseClicked(_ -> {
            datePicker.setValue(tanggal);
            muatJadwal();
            perbaruiKalenderMinggu();
        });

        return kartu;
    }

    private void muatJadwal() {
        LocalDate tanggalTerpilih = datePicker.getValue();
        scheduleContainer.getChildren().clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy", Locale.of("id", "ID"));
        selectedDateLabel.setText(tanggalTerpilih.format(formatter));

        try {
            List<SesiBelajar> daftarSesi = daoSesiBelajar.ambilBerdasarkanTanggal(tanggalTerpilih);
            sessionCountLabel.setText(daftarSesi.size() + " sesi belajar");

            if (daftarSesi.isEmpty()) {
                // Show empty state
                if (emptyState != null) {
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                }
                scheduleContainer.setVisible(false);
                scheduleContainer.setManaged(false);
            } else {
                // Hide empty state
                if (emptyState != null) {
                    emptyState.setVisible(false);
                    emptyState.setManaged(false);
                }
                scheduleContainer.setVisible(true);
                scheduleContainer.setManaged(true);

                // Add schedule cards
                for (SesiBelajar sesi : daftarSesi) {
                    scheduleContainer
                            .getChildren()
                            .add(buatKartuJadwal(sesi));
                }
            }
        } catch (SQLException e) {
            tampilkanKesalahan("Kesalahan memuat jadwal: " + e.getMessage());
        }
    }

    private VBox buatKartuJadwal(SesiBelajar sesi) {
        VBox kartu = new VBox(12);
        kartu.getStyleClass().add("schedule-card");
        if (sesi.isSelesai()) {
            kartu.getStyleClass().add("completed");
        }

        // Header with title and status
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox kotakJudul = new VBox(4);
        HBox.setHgrow(kotakJudul, Priority.ALWAYS);

        Label labelJudul = new Label(sesi.getNamaTopik());
        labelJudul.getStyleClass().add("schedule-title");

        Label labelMataKuliah = new Label(sesi.getNamaMataKuliah());
        labelMataKuliah.getStyleClass().add("schedule-course");

        kotakJudul.getChildren().addAll(labelJudul, labelMataKuliah);

        // Status badge
        Label badgeStatus = new Label(sesi.isSelesai() ? "✓ Selesai" : "○ Belum");
        badgeStatus.getStyleClass().add("status-badge");
        if (sesi.isSelesai()) {
            badgeStatus.getStyleClass().add("completed");
        } else {
            badgeStatus.getStyleClass().add("pending");
        }

        header.getChildren().addAll(kotakJudul, badgeStatus);

        // Meta info row
        HBox barisMeta = new HBox(16);
        barisMeta.setAlignment(Pos.CENTER_LEFT);

        // Type badge
        Label labelTipe = new Label(dapatkanLabelTipeSesi(sesi.getTipeSesi()));
        labelTipe.getStyleClass().addAll("task-type", dapatkanKelasBadge(sesi.getTipeSesi()));

        // Duration
        Label labelDurasi = new Label("Durasi: " + sesi.getDurasiMenit() + " menit");
        labelDurasi.getStyleClass().add("schedule-duration");

        barisMeta.getChildren().addAll(labelTipe, labelDurasi);

        kartu.getChildren().addAll(header, barisMeta);

        return kartu;
    }

    private String dapatkanLabelTipeSesi(String tipe) {
        return switch (tipe) {
            case "INITIAL_STUDY" -> "Belajar Pertama";
            case "REVIEW" -> "Review";
            case "PRACTICE" -> "Latihan";
            default -> tipe;
        };
    }

    private String dapatkanKelasBadge(String tipe) {
        return switch (tipe) {
            case "INITIAL_STUDY" -> "badge-initial_study";
            case "REVIEW" -> "badge-review";
            case "PRACTICE" -> "badge-practice";
            default -> "badge-initial_study";
        };
    }

    private void tampilkanKesalahan(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.ERROR, "Kesalahan", pesan);
        alert.showAndWait();
    }
}
