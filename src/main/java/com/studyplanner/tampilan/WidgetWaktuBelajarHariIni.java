package com.studyplanner.tampilan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.utilitas.UtilUI;
import java.sql.SQLException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WidgetWaktuBelajarHariIni extends VBox {

    private static final int TARGET_HARIAN_MENIT = 120;

    private final Label labelWaktu;
    private final Label labelTarget;
    private final ProgressBar progressBar;
    private final Label labelPerbandingan;
    private final ManajerBasisData manajerBasisData;
    private Timeline timelinePembaruan;
    private final Button tombolMulaiJeda;
    private Timeline timelineTimer;
    private boolean timerBerjalan;
    private long detikSesiSaatIni;
    private int menitDasarHariIni;
    private int menitDasarKemarin;

    public WidgetWaktuBelajarHariIni() {
        manajerBasisData = new ManajerBasisData();
        getStyleClass().add("study-time-widget");
        setAlignment(Pos.CENTER);
        setSpacing(2);
        setPrefSize(180, 180);
        setMinSize(180, 180);
        setMaxSize(180, 180);

        Label labelJudul = new Label("Waktu Belajar Hari Ini");
        labelJudul.getStyleClass().add("widget-title");

        labelWaktu = new Label("0 menit");
        labelWaktu.getStyleClass().add("study-time-number");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(120);
        progressBar.getStyleClass().add("progress-bar");

        labelTarget = new Label("Target: " + TARGET_HARIAN_MENIT + " menit");
        labelTarget.getStyleClass().add("study-time-target");

        labelPerbandingan = new Label("");
        labelPerbandingan.getStyleClass().add("study-time-comparison");

        tombolMulaiJeda = new Button("Mulai");
        tombolMulaiJeda.getStyleClass().add("btn-secondary");
        tombolMulaiJeda.setOnAction(_ -> toggleTimer());

        getChildren().addAll(
                labelJudul,
                labelWaktu,
                progressBar,
                labelTarget,
                labelPerbandingan,
                tombolMulaiJeda);

        timerBerjalan = false;
        detikSesiSaatIni = 0;
        menitDasarHariIni = 0;
        menitDasarKemarin = 0;

        perbaruiWaktu();
        mulaiPembaruanOtomatis();
    }

    private void perbaruiWaktu() {
        try {
            menitDasarHariIni = manajerBasisData.ambilWaktuBelajarHariIni();
            menitDasarKemarin = manajerBasisData.ambilWaktuBelajarKemarin();

            perbaruiTampilan();
        } catch (SQLException e) {
            menitDasarHariIni = 0;
            menitDasarKemarin = 0;
            detikSesiSaatIni = 0;
            labelWaktu.setText("0 menit");
            progressBar.setProgress(0);
            labelPerbandingan.setText("");
        }
    }

    private void perbaruiTampilan() {
        int totalMenit = ambilMenitHariIniSaatIni();

        labelWaktu.setText(formatWaktu(totalMenit));

        progressBar.setProgress(UtilUI.hitungProgres(totalMenit, TARGET_HARIAN_MENIT));

        perbaruiPerbandingan(totalMenit, menitDasarKemarin);
    }

    private String formatWaktu(int menit) {
        if (menit < 60) {
            return menit + " menit";
        } else {
            int jam = menit / 60;
            int mnt = menit % 60;
            return jam + " jam " + mnt + " menit";
        }
    }

    private void perbaruiPerbandingan(int hariIni, int kemarin) {
        if (kemarin == 0) {
            if (hariIni > 0) {
                labelPerbandingan.setText("Awal yang bagus!");
            } else {
                labelPerbandingan.setText("Ayo mulai!");
            }
        } else {
            int selisih = hariIni - kemarin;
            if (selisih > 0) {
                labelPerbandingan.setText("+" + selisih + " menit vs kemarin");
                labelPerbandingan.setStyle("-fx-text-fill: #3b82f6;");
            } else if (selisih < 0) {
                labelPerbandingan.setText(selisih + " menit vs kemarin");
                labelPerbandingan.setStyle("-fx-text-fill: #64748b;");
            } else {
                labelPerbandingan.setText("Sama dengan kemarin");
                labelPerbandingan.setStyle("-fx-text-fill: #64748b;");
            }
        }
    }

    private void mulaiPembaruanOtomatis() {
        timelinePembaruan = new Timeline(
                new KeyFrame(Duration.minutes(1), _ -> perbaruiWaktu()));
        timelinePembaruan.setCycleCount(Animation.INDEFINITE);
        timelinePembaruan.play();
    }

    public void segarkan() {
        perbaruiWaktu();
    }

    public void hentikanPembaruan() {
        if (timelinePembaruan != null) {
            timelinePembaruan.stop();
        }
        if (timelineTimer != null) {
            timelineTimer.stop();
        }
    }

    public int ambilMenitHariIniSaatIni() {
        return menitDasarHariIni + (int) (detikSesiSaatIni / 60);
    }

    private void toggleTimer() {
        if (timerBerjalan) {
            jedaTimer();
        } else {
            mulaiTimer();
        }
    }

    private void mulaiTimer() {
        if (timelineTimer == null) {
            timelineTimer = new Timeline(
                    new KeyFrame(Duration.seconds(1), _ -> {
                        detikSesiSaatIni++;
                        perbaruiTampilan();
                    }));
            timelineTimer.setCycleCount(Animation.INDEFINITE);
        }

        timelineTimer.play();
        timerBerjalan = true;
        tombolMulaiJeda.setText("Jeda");
        labelPerbandingan.setText("Timer berjalan...");
        labelPerbandingan.setStyle("-fx-text-fill: #3b82f6;");
    }

    private void jedaTimer() {
        if (timelineTimer != null) {
            timelineTimer.stop();
        }
        timerBerjalan = false;
        tombolMulaiJeda.setText("Mulai");
        perbaruiTampilan();
    }
}
