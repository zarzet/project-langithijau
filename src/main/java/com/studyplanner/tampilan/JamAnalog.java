package com.studyplanner.tampilan;

import java.time.LocalTime;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class JamAnalog extends Pane {

    private final Canvas canvas;
    private final double ukuran;
    private boolean modeGelap = false;

    public JamAnalog(double ukuran) {
        this.ukuran = ukuran;
        this.canvas = new Canvas(ukuran, ukuran);
        getChildren().add(canvas);

        setPrefSize(ukuran, ukuran);
        setMinSize(ukuran, ukuran);
        setMaxSize(ukuran, ukuran);

        mulaiJam();
    }

    private void mulaiJam() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), _ -> gambar()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        gambar();
    }

    private void gambar() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, ukuran, ukuran);

        double pusatX = ukuran / 2;
        double pusatY = ukuran / 2;
        double radius = ukuran / 2 - 10;

        Color warnaLatar = modeGelap ? Color.web("#0f172a") : Color.WHITE;
        Color warnaDepan = modeGelap ? Color.web("#f8fafc") : Color.web("#0f172a");
        Color warnaAksen = modeGelap ? Color.web("#cbd5e1") : Color.web("#64748b");

        gc.setFill(warnaLatar);
        gc.fillOval(5, 5, ukuran - 10, ukuran - 10);

        gc.setStroke(modeGelap ? Color.web("#1e293b") : Color.web("#e2e8f0"));
        gc.setLineWidth(2);
        gc.strokeOval(5, 5, ukuran - 10, ukuran - 10);

        for (int i = 0; i < 12; i++) {
            double sudut = Math.toRadians(i * 30 - 90);
            double x1 = pusatX + Math.cos(sudut) * (radius - 15);
            double y1 = pusatY + Math.sin(sudut) * (radius - 15);
            double x2 = pusatX + Math.cos(sudut) * (radius - 5);
            double y2 = pusatY + Math.sin(sudut) * (radius - 5);

            gc.setStroke(warnaAksen);
            gc.setLineWidth(2);
            gc.strokeLine(x1, y1, x2, y2);
        }

        LocalTime sekarang = LocalTime.now();
        int jam = sekarang.getHour() % 12;
        int menit = sekarang.getMinute();
        int detik = sekarang.getSecond();

        double sudutJam = Math.toRadians((jam * 30 + menit * 0.5) - 90);
        double sudutMenit = Math.toRadians((menit * 6) - 90);
        double sudutDetik = Math.toRadians((detik * 6) - 90);

        gc.setStroke(warnaDepan);
        gc.setLineWidth(4);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        double jamX = pusatX + Math.cos(sudutJam) * (radius * 0.5);
        double jamY = pusatY + Math.sin(sudutJam) * (radius * 0.5);
        gc.strokeLine(pusatX, pusatY, jamX, jamY);

        gc.setLineWidth(3);
        double menitX = pusatX + Math.cos(sudutMenit) * (radius * 0.7);
        double menitY = pusatY + Math.sin(sudutMenit) * (radius * 0.7);
        gc.strokeLine(pusatX, pusatY, menitX, menitY);

        gc.setStroke(warnaAksen);
        gc.setLineWidth(1.5);
        double detikX = pusatX + Math.cos(sudutDetik) * (radius * 0.8);
        double detikY = pusatY + Math.sin(sudutDetik) * (radius * 0.8);
        gc.strokeLine(pusatX, pusatY, detikX, detikY);

        gc.setFill(warnaDepan);
        gc.fillOval(pusatX - 5, pusatY - 5, 10, 10);
    }

    public void aturModeGelap(boolean modeGelap) {
        this.modeGelap = modeGelap;
        gambar();
    }
}
