package com.studyplanner.kontroler.pembantu;

import com.studyplanner.algoritma.PembuatJadwal;
import com.studyplanner.algoritma.PengulanganBerjarak;
import com.studyplanner.layanan.LayananJadwalUjian;
import com.studyplanner.layanan.LayananMataKuliah;
import com.studyplanner.layanan.LayananSesiBelajar;
import com.studyplanner.layanan.LayananTopik;
import com.studyplanner.model.JadwalUjian;
import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.SesiBelajar;
import com.studyplanner.model.Topik;
import com.studyplanner.tampilan.TampilanKosong;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.UtilUI;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Pembantu untuk mengelola data dan UI dashboard.
 * Memisahkan logika dashboard dari KontrolerUtama untuk SRP.
 */
public class PembantuDashboard {

    private final LayananMataKuliah layananMataKuliah;
    private final LayananTopik layananTopik;
    private final LayananJadwalUjian layananJadwalUjian;
    private final LayananSesiBelajar layananSesiBelajar;
    private final PembuatJadwal pembuatJadwal;

    // Callback
    private final Runnable onBuatJadwalBaru;
    private final Runnable onDataChanged;

    /**
     * Konstruktor PembantuDashboard.
     */
    public PembantuDashboard(
            LayananMataKuliah layananMataKuliah,
            LayananTopik layananTopik,
            LayananJadwalUjian layananJadwalUjian,
            LayananSesiBelajar layananSesiBelajar,
            PembuatJadwal pembuatJadwal,
            Runnable onBuatJadwalBaru,
            Runnable onDataChanged) {
        this.layananMataKuliah = layananMataKuliah;
        this.layananTopik = layananTopik;
        this.layananJadwalUjian = layananJadwalUjian;
        this.layananSesiBelajar = layananSesiBelajar;
        this.pembuatJadwal = pembuatJadwal;
        this.onBuatJadwalBaru = onBuatJadwalBaru;
        this.onDataChanged = onDataChanged;
    }

    /**
     * Muat data dashboard dan update labels.
     *
     * @param labelTotalTopik label total topik
     * @param labelTopikDikuasai label topik dikuasai
     * @param labelTugasHariIni label tugas hari ini
     * @param labelTugasSelesai label tugas selesai
     * @param progressKeseluruhan progress bar keseluruhan
     * @param progressHariIni progress bar hari ini
     * @param labelProgressKeseluruhan label persentase keseluruhan
     * @param labelProgressHariIni label persentase hari ini
     * @param wadahTugasHariIni container tugas hari ini
     * @param wadahUjianMendatang container ujian mendatang
     */
    public void muatDataDashboard(
            Label labelTotalTopik,
            Label labelTopikDikuasai,
            Label labelTugasHariIni,
            Label labelTugasSelesai,
            ProgressBar progressKeseluruhan,
            ProgressBar progressHariIni,
            Label labelProgressKeseluruhan,
            Label labelProgressHariIni,
            VBox wadahTugasHariIni,
            VBox wadahUjianMendatang) throws SQLException {

        PembuatJadwal.KemajuanBelajar progress = pembuatJadwal.ambilKemajuanBelajar();

        labelTotalTopik.setText(String.valueOf(progress.getTotalTopik()));
        labelTopikDikuasai.setText(String.valueOf(progress.getTopikDikuasai()));
        labelTugasHariIni.setText(String.valueOf(progress.getTotalHariIni()));
        labelTugasSelesai.setText(String.valueOf(progress.getSelesaiHariIni()));

        progressKeseluruhan.setProgress(progress.getKemajuanKeseluruhan() / 100.0);
        progressHariIni.setProgress(progress.getKemajuanHariIni() / 100.0);

        labelProgressKeseluruhan.setText(String.format("%.0f%%", progress.getKemajuanKeseluruhan()));
        labelProgressHariIni.setText(String.format("%.0f%%", progress.getKemajuanHariIni()));

        muatTugasHariIni(wadahTugasHariIni);
        muatUjianMendatang(wadahUjianMendatang);
    }

    /**
     * Muat tugas hari ini.
     */
    public void muatTugasHariIni(VBox wadahTugasHariIni) throws SQLException {
        wadahTugasHariIni.getChildren().clear();
        List<SesiBelajar> sessions = layananSesiBelajar.ambilSesiHariIni();

        if (sessions.isEmpty()) {
            TampilanKosong tampilanKosong = TampilanKosong.untukTugasKosong(onBuatJadwalBaru);
            wadahTugasHariIni.getChildren().add(tampilanKosong);
        } else {
            for (SesiBelajar session : sessions) {
                wadahTugasHariIni.getChildren().add(buatKartuTugas(session));
            }
        }
    }

    /**
     * Muat ujian mendatang.
     */
    public void muatUjianMendatang(VBox wadahUjianMendatang) throws SQLException {
        wadahUjianMendatang.getChildren().clear();
        int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
        List<JadwalUjian> exams = layananJadwalUjian.ambilUjianMendatang(userId);

        if (exams.isEmpty()) {
            TampilanKosong tampilanKosong = TampilanKosong.untukUjianKosong(null);
            wadahUjianMendatang.getChildren().add(tampilanKosong);
        } else {
            for (JadwalUjian exam : exams) {
                wadahUjianMendatang.getChildren().add(buatKartuUjian(exam));
            }
        }
    }

    /**
     * Buat kartu tugas.
     */
    private VBox buatKartuTugas(SesiBelajar session) {
        VBox card = new VBox(8);
        card.getStyleClass().add("task-card");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(session.isSelesai());
        checkBox.setOnAction(_ -> tandaiTugasSelesai(session, checkBox.isSelected()));

        Label titleLabel = new Label(session.getNamaTopik());
        titleLabel.getStyleClass().add("task-title");
        if (session.isSelesai()) {
            titleLabel.setStyle("-fx-text-fill: #888; -fx-strikethrough: true;");
        }

        header.getChildren().addAll(checkBox, titleLabel);

        Label courseLabel = new Label(session.getNamaMataKuliah());
        courseLabel.getStyleClass().add("task-course");

        Label typeLabel = new Label(UtilUI.dapatkanLabelTipeSesi(session.getTipeSesi()));
        typeLabel.getStyleClass().add("task-type");
        typeLabel.getStyleClass().add(UtilUI.dapatkanKelasBadge(session.getTipeSesi()));

        Label durationLabel = new Label(session.getDurasiMenit() + " menit");
        durationLabel.getStyleClass().add("task-duration");

        HBox footer = new HBox(15);
        footer.getChildren().addAll(typeLabel, durationLabel);

        card.getChildren().addAll(header, courseLabel, footer);
        return card;
    }

    /**
     * Buat kartu ujian.
     */
    private VBox buatKartuUjian(JadwalUjian exam) throws SQLException {
        VBox card = new VBox(5);
        card.getStyleClass().add("exam-card");

        Label titleLabel = new Label(exam.getJudul());
        titleLabel.getStyleClass().add("exam-title");

        MataKuliah course = layananMataKuliah.ambilBerdasarkanId(exam.getIdMataKuliah());
        Label courseLabel = new Label(course != null ? course.getKode() : "");
        courseLabel.getStyleClass().add("exam-course");

        Label dateLabel = new Label(exam.getTanggalUjian().toString());
        dateLabel.getStyleClass().add("exam-date");

        int daysLeft = exam.getHariMenujuUjian();
        Label daysLabel = new Label(daysLeft + " hari lagi");
        daysLabel.getStyleClass().add("exam-days");

        card.getChildren().addAll(titleLabel, courseLabel, dateLabel, daysLabel);
        return card;
    }

    /**
     * Tandai tugas sebagai selesai/belum selesai.
     */
    private void tandaiTugasSelesai(SesiBelajar session, boolean completed) {
        try {
            session.setSelesai(completed);
            if (completed) {
                session.setSelesaiPada(java.time.LocalDateTime.now());
                tampilkanDialogRatingPerforma(session);
            } else {
                session.setSelesaiPada(null);
                session.setRatingPerforma(0);
                layananSesiBelajar.perbarui(session);
            }

            if (onDataChanged != null) {
                onDataChanged.run();
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memperbarui tugas: " + e.getMessage());
        }
    }

    /**
     * Tampilkan dialog rating performa.
     */
    private void tampilkanDialogRatingPerforma(SesiBelajar session) {
        Dialog<Integer> dialog = PembuatDialogMD3.buatDialog("Rating Performa", "Bagaimana performa Anda untuk sesi ini?");

        ButtonType okButtonType = PembuatDialogMD3.buatTombolOK();
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        Label label = new Label("Pilih rating (1-5):");

        ToggleGroup ratingGroup = new ToggleGroup();
        HBox ratingBox = new HBox(10);

        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton(i + " - " + getLabelRating(i));
            rb.setToggleGroup(ratingGroup);
            rb.setUserData(i);
            if (i == 3) rb.setSelected(true);
            ratingBox.getChildren().add(rb);
        }

        content.getChildren().addAll(label, ratingBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selected = (RadioButton) ratingGroup.getSelectedToggle();
                return (Integer) selected.getUserData();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(rating -> {
            try {
                session.setRatingPerforma(rating);
                layananSesiBelajar.perbarui(session);

                Topik topic = layananTopik.ambilBerdasarkanId(session.getIdTopik());
                if (topic != null) {
                    if (topic.getTanggalBelajarPertama() == null) {
                        topic.setTanggalBelajarPertama(LocalDate.now());
                    }

                    LocalDate nextReview = PengulanganBerjarak.hitungTanggalUlasanBerikutnya(topic, rating);
                    layananTopik.perbarui(topic);

                    UtilUI.tampilkanToast("Sesi selesai! Review: " + nextReview);
                }
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menyimpan rating: " + e.getMessage());
            }
        });
    }

    /**
     * Dapatkan label untuk rating.
     */
    private String getLabelRating(int rating) {
        return switch (rating) {
            case 1 -> "Sangat Sulit";
            case 2 -> "Sulit";
            case 3 -> "Cukup";
            case 4 -> "Baik";
            case 5 -> "Sangat Mudah";
            default -> "";
        };
    }

    /**
     * Ambil sesi mendatang untuk widget.
     */
    public List<SesiBelajar> ambilSesiMendatang(int limit) throws SQLException {
        return layananSesiBelajar.ambilSesiMendatang(limit);
    }
}
