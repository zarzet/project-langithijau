package com.studyplanner.tampilan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Jam analog dengan animasi smooth 60 FPS.
 * Jarum detik bergerak fluid tanpa "melompat" setiap detik.
 */
public class JamAnalog extends Pane {

    private final Canvas canvas;
    private final double ukuran;
    private boolean modeGelap = false;
    private AnimationTimer animationTimer;
    private boolean smoothSecond = true; // Mode jarum detik smooth

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
        // Menggunakan AnimationTimer untuk smooth 60 FPS animation
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gambar();
            }
        };
        animationTimer.start();
        gambar();
    }

    /**
     * Set apakah jarum detik bergerak smooth atau melompat per detik.
     */
    public void setSmoothSecond(boolean smooth) {
        this.smoothSecond = smooth;
    }

    /**
     * Hentikan animasi jam (untuk cleanup).
     */
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
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

        LocalDateTime sekarangDateTime = LocalDateTime.now();
        LocalTime sekarang = sekarangDateTime.toLocalTime();
        int jam = sekarang.getHour() % 12;
        int menit = sekarang.getMinute();
        int detik = sekarang.getSecond();
        int nano = sekarang.getNano();

        // Smooth interpolation untuk semua jarum
        double fractionSecond = nano / 1_000_000_000.0;
        double smoothDetik = detik + fractionSecond;
        double smoothMenit = menit + smoothDetik / 60.0;
        double smoothJam = jam + smoothMenit / 60.0;

        double sudutJam = Math.toRadians((smoothJam * 30) - 90);
        double sudutMenit = Math.toRadians((smoothMenit * 6) - 90);
        double sudutDetik = smoothSecond
                ? Math.toRadians((smoothDetik * 6) - 90)
                : Math.toRadians((detik * 6) - 90);

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
