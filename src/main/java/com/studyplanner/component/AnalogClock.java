package com.studyplanner.component;

import java.time.LocalTime;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnalogClock extends Pane {

    private final Canvas canvas;
    private final double size;
    private boolean isDarkMode = false;

    public AnalogClock(double size) {
        this.size = size;
        this.canvas = new Canvas(size, size);
        getChildren().add(canvas);

        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);

        startClock();
    }

    private void startClock() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), _ -> draw())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, size, size);

        double centerX = size / 2;
        double centerY = size / 2;
        double radius = size / 2 - 10;

        Color bgColor = isDarkMode ? Color.web("#0f172a") : Color.WHITE;
        Color fgColor = isDarkMode ? Color.web("#f8fafc") : Color.web("#0f172a");
        Color accentColor = isDarkMode ? Color.web("#cbd5e1") : Color.web("#64748b");

        gc.setFill(bgColor);
        gc.fillOval(5, 5, size - 10, size - 10);

        gc.setStroke(isDarkMode ? Color.web("#1e293b") : Color.web("#e2e8f0"));
        gc.setLineWidth(2);
        gc.strokeOval(5, 5, size - 10, size - 10);

        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            double x1 = centerX + Math.cos(angle) * (radius - 15);
            double y1 = centerY + Math.sin(angle) * (radius - 15);
            double x2 = centerX + Math.cos(angle) * (radius - 5);
            double y2 = centerY + Math.sin(angle) * (radius - 5);

            gc.setStroke(accentColor);
            gc.setLineWidth(2);
            gc.strokeLine(x1, y1, x2, y2);
        }

        LocalTime now = LocalTime.now();
        int hours = now.getHour() % 12;
        int minutes = now.getMinute();
        int seconds = now.getSecond();

        double hourAngle = Math.toRadians((hours * 30 + minutes * 0.5) - 90);
        double minuteAngle = Math.toRadians((minutes * 6) - 90);
        double secondAngle = Math.toRadians((seconds * 6) - 90);

        gc.setStroke(fgColor);
        gc.setLineWidth(4);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        double hourX = centerX + Math.cos(hourAngle) * (radius * 0.5);
        double hourY = centerY + Math.sin(hourAngle) * (radius * 0.5);
        gc.strokeLine(centerX, centerY, hourX, hourY);

        gc.setLineWidth(3);
        double minuteX = centerX + Math.cos(minuteAngle) * (radius * 0.7);
        double minuteY = centerY + Math.sin(minuteAngle) * (radius * 0.7);
        gc.strokeLine(centerX, centerY, minuteX, minuteY);

        gc.setStroke(accentColor);
        gc.setLineWidth(1.5);
        double secondX = centerX + Math.cos(secondAngle) * (radius * 0.8);
        double secondY = centerY + Math.sin(secondAngle) * (radius * 0.8);
        gc.strokeLine(centerX, centerY, secondX, secondY);

        gc.setFill(fgColor);
        gc.fillOval(centerX - 5, centerY - 5, 10, 10);
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
        draw();
    }
}
