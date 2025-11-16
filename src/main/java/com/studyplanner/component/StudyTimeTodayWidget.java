package com.studyplanner.component;

import com.studyplanner.database.DatabaseManager;
import java.sql.SQLException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
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
    private final DatabaseManager dbManager;
    private Timeline updateTimeline;

    public StudyTimeTodayWidget() {
        dbManager = new DatabaseManager();
        getStyleClass().add("study-time-widget");
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPrefSize(200, 200);
        setMinSize(200, 200);
        setMaxSize(200, 200);

        Label titleLabel = new Label("Study Time Today");
        titleLabel.getStyleClass().add("widget-title");

        timeLabel = new Label("0 min");
        timeLabel.getStyleClass().add("study-time-number");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(160);
        progressBar.getStyleClass().add("progress-bar");

        targetLabel = new Label("Target: " + DAILY_TARGET_MINUTES + " min");
        targetLabel.getStyleClass().add("study-time-target");

        comparisonLabel = new Label("");
        comparisonLabel.getStyleClass().add("study-time-comparison");

        getChildren().addAll(
            titleLabel,
            timeLabel,
            progressBar,
            targetLabel,
            comparisonLabel
        );

        updateTime();
        startAutoUpdate();
    }

    private void updateTime() {
        try {
            int todayMinutes = dbManager.getTodayStudyTime();
            int yesterdayMinutes = dbManager.getYesterdayStudyTime();

            timeLabel.setText(formatTime(todayMinutes));

            double progress = Math.min(
                (double) todayMinutes / DAILY_TARGET_MINUTES,
                1.0
            );
            progressBar.setProgress(progress);

            updateComparison(todayMinutes, yesterdayMinutes);
        } catch (SQLException e) {
            timeLabel.setText("0 min");
            progressBar.setProgress(0);
            comparisonLabel.setText("");
        }
    }

    private String formatTime(int minutes) {
        if (minutes < 60) {
            return minutes + " min";
        } else {
            int hours = minutes / 60;
            int mins = minutes % 60;
            return hours + "h " + mins + "m";
        }
    }

    private void updateComparison(int today, int yesterday) {
        if (yesterday == 0) {
            if (today > 0) {
                comparisonLabel.setText("Great start!");
            } else {
                comparisonLabel.setText("Let's begin!");
            }
        } else {
            int diff = today - yesterday;
            if (diff > 0) {
                comparisonLabel.setText("+" + diff + " min vs yesterday");
                comparisonLabel.setStyle("-fx-text-fill: #3b82f6;");
            } else if (diff < 0) {
                comparisonLabel.setText(diff + " min vs yesterday");
                comparisonLabel.setStyle("-fx-text-fill: #64748b;");
            } else {
                comparisonLabel.setText("Same as yesterday");
                comparisonLabel.setStyle("-fx-text-fill: #64748b;");
            }
        }
    }

    private void startAutoUpdate() {
        updateTimeline = new Timeline(
            new KeyFrame(Duration.minutes(1), _ -> updateTime())
        );
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
    }
}
