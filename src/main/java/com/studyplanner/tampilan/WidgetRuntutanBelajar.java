package com.studyplanner.tampilan;

import com.studyplanner.basisdata.ManajerBasisData;
import java.sql.SQLException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WidgetRuntutanBelajar extends VBox {

    private final Label labelAngkaRuntutan;
    private final Label labelTeksRuntutan;
    private final Label labelMotivasi;
    private final ManajerBasisData manajerBasisData;
    private Timeline timelinePembaruan;

    public WidgetRuntutanBelajar() {
        manajerBasisData = new ManajerBasisData();
        getStyleClass().add("streak-widget");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPrefSize(180, 180);
        setMinSize(180, 180);
        setMaxSize(180, 180);

        labelAngkaRuntutan = new Label("0");
        labelAngkaRuntutan.getStyleClass().add("streak-number");

        labelTeksRuntutan = new Label("Hari Beruntun");
        labelTeksRuntutan.getStyleClass().add("streak-label");

        labelMotivasi = new Label("Pertahankan!");
        labelMotivasi.getStyleClass().add("streak-motivation");

        getChildren().addAll(labelAngkaRuntutan, labelTeksRuntutan, labelMotivasi);

        perbaruiRuntutan();
        mulaiPembaruanOtomatis();
    }

    private void perbaruiRuntutan() {
        try {
            int runtutan = manajerBasisData.ambilRuntutanBelajar();
            labelAngkaRuntutan.setText(String.valueOf(runtutan));
            perbaruiMotivasi(runtutan);
        } catch (SQLException e) {
            labelAngkaRuntutan.setText("0");
            labelMotivasi.setText("Mulai perjalananmu!");
        }
    }

    private void perbaruiMotivasi(int runtutan) {
        if (runtutan == 0) {
            labelMotivasi.setText("Mulai hari ini!");
        } else if (runtutan == 1) {
            labelMotivasi.setText("Awal yang bagus!");
        } else if (runtutan < 7) {
            labelMotivasi.setText("Terus pertahankan!");
        } else if (runtutan < 30) {
            labelMotivasi.setText("Luar biasa!");
        } else if (runtutan < 100) {
            labelMotivasi.setText("Kamu hebat!");
        } else {
            labelMotivasi.setText("Legendaris!");
        }
    }

    private void mulaiPembaruanOtomatis() {
        timelinePembaruan = new Timeline(
                new KeyFrame(Duration.minutes(5), _ -> perbaruiRuntutan()));
        timelinePembaruan.setCycleCount(Animation.INDEFINITE);
        timelinePembaruan.play();
    }

    public void segarkan() {
        perbaruiRuntutan();
    }

    public void hentikanPembaruan() {
        if (timelinePembaruan != null) {
            timelinePembaruan.stop();
        }
    }
}
