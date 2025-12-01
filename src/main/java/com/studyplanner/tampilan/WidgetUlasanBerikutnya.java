package com.studyplanner.tampilan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.Topik;
import com.studyplanner.utilitas.UtilUI;
import com.studyplanner.eksepsi.EksepsiAksesBasisData;
import java.time.LocalDate;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WidgetUlasanBerikutnya extends VBox {

    private final Label labelTopik;
    private final Label labelMataKuliah;
    private final Label labelJatuhTempo;
    private final Label labelStatus;
    private final ManajerBasisData manajerBasisData;
    private Timeline timelinePembaruan;

    public WidgetUlasanBerikutnya() {
        manajerBasisData = new ManajerBasisData();
        getStyleClass().add("next-review-widget");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPrefSize(180, 180);
        setMinSize(180, 180);
        setMaxSize(180, 180);

        Label labelJudul = new Label("Review Berikutnya");
        labelJudul.getStyleClass().add("widget-title");

        labelTopik = new Label("Tidak ada review");
        labelTopik.getStyleClass().add("next-review-topic");
        labelTopik.setWrapText(true);
        labelTopik.setMaxWidth(170);
        labelTopik.setAlignment(Pos.CENTER);

        labelMataKuliah = new Label("");
        labelMataKuliah.getStyleClass().add("next-review-course");
        labelMataKuliah.setWrapText(true);
        labelMataKuliah.setMaxWidth(170);

        labelJatuhTempo = new Label("");
        labelJatuhTempo.getStyleClass().add("next-review-due");

        labelStatus = new Label("Semua selesai!");
        labelStatus.getStyleClass().add("next-review-status");
        labelStatus.setWrapText(true);
        labelStatus.setMaxWidth(140);
        labelStatus.setAlignment(Pos.CENTER);

        getChildren().addAll(
                labelJudul,
                labelTopik,
                labelMataKuliah,
                labelJatuhTempo,
                labelStatus);

        perbaruiUlasanBerikutnya();
        mulaiPembaruanOtomatis();
    }

    private void perbaruiUlasanBerikutnya() {
        try {
            var topics = manajerBasisData.ambilTopikUlasanBerikutnya(1);

            if (topics.isEmpty()) {
                labelTopik.setText("Tidak ada review");
                labelMataKuliah.setText("");
                labelJatuhTempo.setText("");
                labelStatus.setText("Semua selesai!");
                labelStatus.setVisible(true);
            } else {
                Topik topik = topics.get(0);
                labelTopik.setText(topik.getNama());

                labelMataKuliah.setText("");
                labelMataKuliah.setVisible(false);

                LocalDate tanggalUlasanBerikutnya = topik
                        .getTanggalUlasanTerakhir()
                        .plusDays(topik.getInterval());
                long hariMenuju = UtilUI.hitungHariMenuju(tanggalUlasanBerikutnya);

                if (hariMenuju < 0) {
                    labelJatuhTempo.setText(
                            "Terlambat " + Math.abs(hariMenuju) + " hari");
                    labelJatuhTempo.setStyle(
                            "-fx-text-fill: #ef4444; -fx-font-weight: 600;");
                } else if (hariMenuju == 0) {
                    labelJatuhTempo.setText("Jatuh tempo hari ini");
                    labelJatuhTempo.setStyle(
                            "-fx-text-fill: #3b82f6; -fx-font-weight: 600;");
                } else if (hariMenuju == 1) {
                    labelJatuhTempo.setText("Jatuh tempo besok");
                    labelJatuhTempo.setStyle("-fx-text-fill: #3b82f6;");
                } else {
                    labelJatuhTempo.setText("Jatuh tempo " + hariMenuju + " hari lagi");
                    labelJatuhTempo.setStyle("-fx-text-fill: #3b82f6;");
                }

                labelStatus.setText("Prioritas: " + topik.getPrioritas() + "/5");
                labelStatus.setStyle("-fx-text-fill: #64748b;");
                labelStatus.setVisible(true);
            }
        } catch (EksepsiAksesBasisData e) {
            labelTopik.setText("Gagal memuat");
            labelMataKuliah.setText("");
            labelJatuhTempo.setText("");
            labelStatus.setText("");
            labelStatus.setVisible(false);
        }
    }

    private void mulaiPembaruanOtomatis() {
        timelinePembaruan = new Timeline(
                new KeyFrame(Duration.minutes(5), _ -> perbaruiUlasanBerikutnya()));
        timelinePembaruan.setCycleCount(Animation.INDEFINITE);
        timelinePembaruan.play();
    }

    public void segarkan() {
        perbaruiUlasanBerikutnya();
    }

    public void hentikanPembaruan() {
        if (timelinePembaruan != null) {
            timelinePembaruan.stop();
        }
    }
}
