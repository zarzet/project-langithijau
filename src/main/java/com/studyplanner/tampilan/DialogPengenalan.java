package com.studyplanner.tampilan;

import com.studyplanner.utilitas.AnimasiUtil;
import com.studyplanner.utilitas.PembuatIkon;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Dialog onboarding untuk memperkenalkan aplikasi kepada user baru.
 */
public class DialogPengenalan {

    private final Stage stage;
    private final VBox kontenUtama;
    private int halamanSaatIni = 0;
    private final PengenalanSelesaiCallback callback;

    // Data halaman onboarding
    private final HalamanPengenalan[] halamanList = {
        new HalamanPengenalan(
            "Selamat Datang!",
            "Terima kasih telah menggunakan Perencana Belajar Adaptif.\n\n" +
            "Aplikasi ini akan membantu Anda mengatur jadwal belajar dengan sistem " +
            "pengulangan berjarak untuk hasil yang lebih optimal.",
            () -> PembuatIkon.ikonOnboardingSelamatDatang()
        ),
        new HalamanPengenalan(
            "Langkah 1: Tambah Mata Kuliah",
            "Mulailah dengan menambahkan mata kuliah yang sedang Anda pelajari.\n\n" +
            "Klik menu 'Kelola Mata Kuliah' di sidebar, lalu tambahkan mata kuliah " +
            "beserta topik-topik yang ingin dipelajari.",
            () -> PembuatIkon.ikonOnboardingMataKuliah()
        ),
        new HalamanPengenalan(
            "Langkah 2: Buat Jadwal Otomatis",
            "Setelah menambahkan topik, gunakan fitur 'Buat Jadwal' untuk " +
            "membuat jadwal belajar otomatis.\n\n" +
            "Sistem akan memprioritaskan topik berdasarkan ujian mendatang dan " +
            "tingkat kesulitan.",
            () -> PembuatIkon.ikonOnboardingJadwal()
        ),
        new HalamanPengenalan(
            "Siap Memulai!",
            "Selamat! Anda sudah siap menggunakan aplikasi ini.\n\n" +
            "Tips: Selesaikan tugas harian Anda dan beri penilaian performa untuk " +
            "mengaktifkan sistem pengulangan berjarak yang akan mengoptimalkan " +
            "jadwal ulasan Anda.",
            () -> PembuatIkon.ikonOnboardingSelesai()
        )
    };

    public interface PengenalanSelesaiCallback {
        void onSelesai();
    }

    public DialogPengenalan(Stage parent, PengenalanSelesaiCallback callback) {
        this.callback = callback;
        this.stage = new Stage();
        stage.initOwner(parent);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        kontenUtama = new VBox(20);
        kontenUtama.setPadding(new Insets(40));
        kontenUtama.setAlignment(Pos.CENTER);
        kontenUtama.getStyleClass().add("onboarding-dialog");
        kontenUtama.setMinWidth(500);
        kontenUtama.setMaxWidth(500);

        tampilkanHalaman(0);

        Scene scene = new Scene(kontenUtama);
        scene.setFill(null);
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm()
        );

        stage.setScene(scene);
    }

    private void tampilkanHalaman(int index) {
        if (index < 0 || index >= halamanList.length) return;

        halamanSaatIni = index;
        HalamanPengenalan halaman = halamanList[index];

        kontenUtama.getChildren().clear();

        // Ikon
        VBox wadahIkon = new VBox();
        wadahIkon.setAlignment(Pos.CENTER);
        wadahIkon.setPadding(new Insets(0, 0, 10, 0));
        wadahIkon.getChildren().add(halaman.ikonProvider.get());

        // Judul
        Label labelJudul = new Label(halaman.judul);
        labelJudul.getStyleClass().add("onboarding-title");

        // Deskripsi
        Label labelDeskripsi = new Label(halaman.deskripsi);
        labelDeskripsi.getStyleClass().add("onboarding-description");
        labelDeskripsi.setWrapText(true);
        labelDeskripsi.setMaxWidth(420);

        // Indikator halaman
        HBox indikator = buatIndikatorHalaman();

        // Tombol navigasi
        HBox tombolBox = buatTombolNavigasi();

        kontenUtama.getChildren().addAll(
            wadahIkon,
            labelJudul,
            labelDeskripsi,
            indikator,
            tombolBox
        );

        // Animasi masuk dengan spring physics
        kontenUtama.setTranslateY(20);
        kontenUtama.setScaleX(0.95);
        kontenUtama.setScaleY(0.95);
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), kontenUtama);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
        
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), kontenUtama);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setInterpolator(AnimasiUtil.SPRING_DEFAULT);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(350), kontenUtama);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(AnimasiUtil.EASE_OUT_BACK);
        
        ParallelTransition entrance = new ParallelTransition(fade, slide, scale);
        entrance.play();
    }

    private HBox buatIndikatorHalaman() {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 0, 10, 0));

        for (int i = 0; i < halamanList.length; i++) {
            Region dot = new Region();
            dot.getStyleClass().add("onboarding-dot");
            if (i == halamanSaatIni) {
                dot.getStyleClass().add("onboarding-dot-active");
            }
            dot.setPrefSize(10, 10);
            dot.setMinSize(10, 10);
            dot.setMaxSize(10, 10);
            box.getChildren().add(dot);
        }

        return box;
    }

    private HBox buatTombolNavigasi() {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 0, 0, 0));

        boolean isFirst = halamanSaatIni == 0;
        boolean isLast = halamanSaatIni == halamanList.length - 1;

        // Tombol Skip
        if (!isLast) {
            Button btnSkip = new Button("Lewati");
            btnSkip.getStyleClass().add("btn-text");
            btnSkip.setOnAction(_ -> selesai());
            box.getChildren().add(btnSkip);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);

        // Tombol Back
        if (!isFirst) {
            Button btnBack = new Button("Kembali");
            btnBack.getStyleClass().add("btn-secondary");
            btnBack.setGraphic(PembuatIkon.ikonBack());
            btnBack.setOnAction(_ -> tampilkanHalaman(halamanSaatIni - 1));
            box.getChildren().add(btnBack);
        }

        // Tombol Next / Mulai
        Button btnNext = new Button(isLast ? "Mulai Sekarang" : "Lanjut");
        btnNext.getStyleClass().add("btn-primary");
        if (!isLast) {
            btnNext.setGraphic(PembuatIkon.ikonNext());
        }
        btnNext.setOnAction(_ -> {
            if (isLast) {
                selesai();
            } else {
                tampilkanHalaman(halamanSaatIni + 1);
            }
        });
        box.getChildren().add(btnNext);

        return box;
    }

    private void selesai() {
        stage.close();
        if (callback != null) {
            callback.onSelesai();
        }
    }

    public void tampilkan() {
        stage.showAndWait();
    }

    // Inner class untuk data halaman
    private static class HalamanPengenalan {
        String judul;
        String deskripsi;
        IkonProvider ikonProvider;

        HalamanPengenalan(String judul, String deskripsi, IkonProvider ikonProvider) {
            this.judul = judul;
            this.deskripsi = deskripsi;
            this.ikonProvider = ikonProvider;
        }
    }

    @FunctionalInterface
    private interface IkonProvider {
        FontIcon get();
    }
}
