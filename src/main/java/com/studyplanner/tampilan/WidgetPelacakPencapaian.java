package com.studyplanner.tampilan;

import com.studyplanner.utilitas.UtilUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("this-escape")
public class WidgetPelacakPencapaian extends VBox {

    public static final int TARGET_FOKUS_MENIT = 120;

    private final Label lencanaRuntutan;
    private final ProgressBar progresTugas;
    private final Label labelNilaiTugas;
    private final Label labelDetailTugas;

    private final ProgressBar progresReview;
    private final Label labelNilaiReview;
    private final Label labelDetailReview;

    private final ProgressBar progresFokus;
    private final Label labelNilaiFokus;
    private final Label labelDetailFokus;

    public WidgetPelacakPencapaian() {
        getStyleClass().addAll("achievement-widget", "widget-interactive");
        setSpacing(8);
        setPadding(new Insets(0));
        setFillWidth(true);

        VBox konten = new VBox(8);
        konten.setFillWidth(true);

        Label judul = new Label("Pelacak Pencapaian");
        judul.getStyleClass().add("widget-title");
        judul.setWrapText(true);
        judul.setMaxWidth(170);

        lencanaRuntutan = new Label("0 hari beruntun");
        lencanaRuntutan.getStyleClass().add("achievement-badge");
        lencanaRuntutan.setWrapText(true);
        lencanaRuntutan.setMaxWidth(100);

        HBox header = new HBox(10, judul, lencanaRuntutan);
        header.setAlignment(Pos.CENTER_LEFT);

        progresTugas = new ProgressBar(0);
        progresTugas.setPrefWidth(150);
        progresTugas.setPrefHeight(5);
        progresTugas.getStyleClass().add("progress-bar");
        labelNilaiTugas = new Label("0/0 selesai");
        labelNilaiTugas.getStyleClass().add("achievement-value");
        labelNilaiTugas.setWrapText(true);
        labelNilaiTugas.setMaxWidth(170);

        labelDetailTugas = new Label("Belum ada tugas hari ini");
        labelDetailTugas.getStyleClass().add("achievement-detail");
        labelDetailTugas.setWrapText(true);
        labelDetailTugas.setMaxWidth(170);

        VBox kotakTugas = bangunItemPencapaian(
                "Target Harian",
                labelNilaiTugas,
                progresTugas,
                labelDetailTugas);

        progresReview = new ProgressBar(0);
        progresReview.setPrefWidth(150);
        progresReview.setPrefHeight(5);
        progresReview.getStyleClass().add("progress-bar");
        labelNilaiReview = new Label("0 review");
        labelNilaiReview.getStyleClass().add("achievement-value");
        labelNilaiReview.setWrapText(true);
        labelNilaiReview.setMaxWidth(170);

        labelDetailReview = new Label("Review menjaga retensi materi");
        labelDetailReview.getStyleClass().add("achievement-detail");
        labelDetailReview.setWrapText(true);
        labelDetailReview.setMaxWidth(170);

        VBox kotakReview = bangunItemPencapaian(
                "Sesi Review",
                labelNilaiReview,
                progresReview,
                labelDetailReview);

        progresFokus = new ProgressBar(0);
        progresFokus.setPrefWidth(150);
        progresFokus.setPrefHeight(5);
        progresFokus.getStyleClass().add("progress-bar");
        labelNilaiFokus = new Label("0/" + TARGET_FOKUS_MENIT + " menit");
        labelNilaiFokus.getStyleClass().add("achievement-value");
        labelNilaiFokus.setWrapText(true);
        labelNilaiFokus.setMaxWidth(170);

        labelDetailFokus = new Label("Mulai timer belajar");
        labelDetailFokus.getStyleClass().add("achievement-detail");
        labelDetailFokus.setWrapText(true);
        labelDetailFokus.setMaxWidth(170);

        VBox kotakFokus = bangunItemPencapaian(
                "Menit Fokus",
                labelNilaiFokus,
                progresFokus,
                labelDetailFokus);

        konten.getChildren().addAll(header, kotakTugas, kotakReview, kotakFokus);

        ScrollPane scrollPane = new ScrollPane(konten);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane-transparent");
        scrollPane.setPannable(true);

        getChildren().setAll(scrollPane);
    }

    private VBox bangunItemPencapaian(
            String judul,
            Label nilai,
            ProgressBar progressBar,
            Label detail) {
        Label labelJudulItem = new Label(judul);
        labelJudulItem.getStyleClass().add("achievement-title");
        labelJudulItem.setWrapText(true);
        labelJudulItem.setMaxWidth(170);

        VBox kotak = new VBox(3);
        kotak.setAlignment(Pos.CENTER_LEFT);
        kotak.getStyleClass().add("achievement-item");
        kotak.getChildren().addAll(labelJudulItem, nilai, progressBar, detail);

        return kotak;
    }

    public void perbaruiData(
            int tugasSelesai,
            int totalTugas,
            int reviewSelesai,
            int totalReview,
            int menitFokus,
            int hariRuntutan) {
        perbaruiBagianTugas(tugasSelesai, totalTugas);
        perbaruiBagianReview(reviewSelesai, totalReview);
        perbaruiBagianFokus(menitFokus);
        perbaruiLencanaRuntutan(hariRuntutan);
    }

    private void perbaruiBagianTugas(int selesai, int total) {
        if (total <= 0) {
            progresTugas.setProgress(0);
            labelNilaiTugas.setText("0/0 selesai");
            labelDetailTugas.setText("Belum ada tugas hari ini");
            return;
        }

        progresTugas.setProgress(UtilUI.hitungProgres(selesai, total));
        labelNilaiTugas.setText(selesai + "/" + total + " selesai");

        int sisa = Math.max(total - selesai, 0);
        if (sisa == 0) {
            labelDetailTugas.setText("Semua tugas hari ini selesai!");
        } else {
            labelDetailTugas.setText(sisa + " tugas lagi untuk tuntas");
        }
    }

    private void perbaruiBagianReview(int selesai, int total) {
        if (total <= 0) {
            progresReview.setProgress(0);
            labelNilaiReview.setText("0 review");
            labelDetailReview.setText("Belum ada sesi review terjadwal");
            return;
        }

        progresReview.setProgress(UtilUI.hitungProgres(selesai, total));
        labelNilaiReview.setText(selesai + "/" + total + " review selesai");

        int sisa = Math.max(total - selesai, 0);
        if (sisa == 0) {
            labelDetailReview.setText("Seluruh review terselesaikan!");
        } else {
            labelDetailReview.setText(sisa + " review lagi untuk stabil");
        }
    }

    private void perbaruiBagianFokus(int menit) {
        progresFokus.setProgress(UtilUI.hitungProgres(menit, TARGET_FOKUS_MENIT));
        labelNilaiFokus.setText(menit + "/" + TARGET_FOKUS_MENIT + " menit");

        int sisa = Math.max(TARGET_FOKUS_MENIT - menit, 0);
        if (sisa == 0) {
            labelDetailFokus.setText("Target fokus tercapai!");
        } else {
            labelDetailFokus.setText("Tambahkan " + sisa + " menit lagi");
        }
    }

    private void perbaruiLencanaRuntutan(int hariRuntutan) {
        if (hariRuntutan <= 0) {
            lencanaRuntutan.setText("Mulai streak baru");
        } else if (hariRuntutan == 1) {
            lencanaRuntutan.setText("1 hari beruntun");
        } else {
            lencanaRuntutan.setText(hariRuntutan + " hari beruntun");
        }
    }
}
