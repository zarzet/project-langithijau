package com.studyplanner.component;

import com.studyplanner.database.ManajerBasisData;
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

public class StudyTimeTodayWidget extends VBox {

    private static final int DAILY_TARGET_MINUTES = 120;

    private final Label timeLabel;
    private final Label targetLabel;
    private final ProgressBar progressBar;
    private final Label comparisonLabel;
    private final ManajerBasisData manajerBasisData;
    private Timeline updateTimeline;
    private final Button startPauseButton;
    private Timeline timerTimeline;
    private boolean timerRunning;
    private long currentSessionSeconds;
    private int baseTodayMinutes;
    private int baseYesterdayMinutes;

    public StudyTimeTodayWidget() {
        manajerBasisData = new ManajerBasisData();
        getStyleClass().add("study-time-widget");
        setAlignment(Pos.CENTER);
        setSpacing(2);
        setPrefSize(180, 180);
        setMinSize(180, 180);
        setMaxSize(180, 180);

        Label titleLabel = new Label("Waktu Belajar Hari Ini");
        titleLabel.getStyleClass().add("widget-title");

        timeLabel = new Label("0 menit");
        timeLabel.getStyleClass().add("study-time-number");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(120);
        progressBar.getStyleClass().add("progress-bar");

        targetLabel = new Label("Target: " + DAILY_TARGET_MINUTES + " menit");
        targetLabel.getStyleClass().add("study-time-target");

        comparisonLabel = new Label("");
        comparisonLabel.getStyleClass().add("study-time-comparison");

        startPauseButton = new Button("Mulai");
        startPauseButton.getStyleClass().add("btn-secondary");
        startPauseButton.setOnAction(_ -> toggleTimer());

        getChildren().addAll(
                titleLabel,
                timeLabel,
                progressBar,
                targetLabel,
                comparisonLabel,
                startPauseButton);

        timerRunning = false;
        currentSessionSeconds = 0;
        baseTodayMinutes = 0;
        baseYesterdayMinutes = 0;

        updateTime();
        startAutoUpdate();
    }

    private void updateTime() {
        try {
            baseTodayMinutes = manajerBasisData.ambilWaktuBelajarHariIni();
            baseYesterdayMinutes = manajerBasisData.ambilWaktuBelajarKemarin();

            updateDisplay();
        } catch (SQLException e) {
            baseTodayMinutes = 0;
            baseYesterdayMinutes = 0;
            currentSessionSeconds = 0;
            timeLabel.setText("0 menit");
            progressBar.setProgress(0);
            comparisonLabel.setText("");
        }
    }

    private void updateDisplay() {
        int totalMinutes = getCurrentTodayMinutes();

        timeLabel.setText(formatTime(totalMinutes));

        double progress = Math.min(
                (double) totalMinutes / DAILY_TARGET_MINUTES,
                1.0);
        progressBar.setProgress(progress);

        updateComparison(totalMinutes, baseYesterdayMinutes);
    }

    private String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + " menit";
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            return hours + " jam " + mins + " menit";
        }
    }

    private void updateComparison(int today, int yesterday) {
        if (yesterday == 0) {
            if (today > 0) {
                comparisonLabel.setText("Awal yang bagus!");
            } else {
                comparisonLabel.setText("Ayo mulai!");
            }
        } else {
            int diff = today - yesterday;
            if (diff > 0) {
                comparisonLabel.setText("+" + diff + " menit vs kemarin");
                comparisonLabel.setStyle("-fx-text-fill: #3b82f6;");
            } else if (diff < 0) {
                comparisonLabel.setText(diff + " menit vs kemarin");
                comparisonLabel.setStyle("-fx-text-fill: #64748b;");
            } else {
                comparisonLabel.setText("Sama dengan kemarin");
                comparisonLabel.setStyle("-fx-text-fill: #64748b;");
            }
        }
    }

    private void startAutoUpdate() {
        updateTimeline = new Timeline(
                new KeyFrame(Duration.minutes(1), _ -> updateTime()));
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }

    public void refresh() {
        updateTime();
    }

    public void stopUpdates() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }

    public int getCurrentTodayMinutes() {
        return baseTodayMinutes + (int) (currentSessionSeconds / 60);
    }

    private void toggleTimer() {
        if (timerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (timerTimeline == null) {
            timerTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), _ -> {
                        currentSessionSeconds++;
                        updateDisplay();
                    }));
            timerTimeline.setCycleCount(Animation.INDEFINITE);
        }

        timerTimeline.play();
        timerRunning = true;
        startPauseButton.setText("Jeda");
        comparisonLabel.setText("Timer berjalan...");
        comparisonLabel.setStyle("-fx-text-fill: #3b82f6;");
    }

    private void pauseTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
        timerRunning = false;
        startPauseButton.setText("Mulai");
        updateDisplay();
    }
}
