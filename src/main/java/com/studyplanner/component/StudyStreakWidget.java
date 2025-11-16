package com.studyplanner.component;

import com.studyplanner.database.DatabaseManager;
import java.sql.SQLException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StudyStreakWidget extends VBox {

    private final Label streakNumber;
    private final Label streakLabel;
    private final Label motivationLabel;
    private final DatabaseManager dbManager;
    private Timeline updateTimeline;

    public StudyStreakWidget() {
        dbManager = new DatabaseManager();
        getStyleClass().add("streak-widget");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPrefSize(200, 200);
        setMinSize(200, 200);
        setMaxSize(200, 200);

        streakNumber = new Label("0");
        streakNumber.getStyleClass().add("streak-number");

        streakLabel = new Label("Day Streak");
        streakLabel.getStyleClass().add("streak-label");

        motivationLabel = new Label("Keep it up!");
        motivationLabel.getStyleClass().add("streak-motivation");

        getChildren().addAll(streakNumber, streakLabel, motivationLabel);

        updateStreak();
        startAutoUpdate();
    }

    private void updateStreak() {
        try {
            int streak = dbManager.getStudyStreak();
            streakNumber.setText(String.valueOf(streak));
            updateMotivation(streak);
        } catch (SQLException e) {
            streakNumber.setText("0");
            motivationLabel.setText("Start your journey!");
        }
    }

    private void updateMotivation(int streak) {
        if (streak == 0) {
            motivationLabel.setText("Start today!");
        } else if (streak == 1) {
            motivationLabel.setText("Great start!");
        } else if (streak < 7) {
            motivationLabel.setText("Keep going!");
        } else if (streak < 30) {
            motivationLabel.setText("Amazing streak!");
        } else if (streak < 100) {
            motivationLabel.setText("You're on fire!");
        } else {
            motivationLabel.setText("Legendary!");
        }
    }

    private void startAutoUpdate() {
        updateTimeline = new Timeline(
            new KeyFrame(Duration.minutes(5), _ -> updateStreak())
        );
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }

    public void refresh() {
        updateStreak();
    }

    public void stopUpdates() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
}
