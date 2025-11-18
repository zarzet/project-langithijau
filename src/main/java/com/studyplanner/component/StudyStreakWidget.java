package com.studyplanner.component;

import com.studyplanner.database.ManajerBasisData;
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
    private final ManajerBasisData manajerBasisData;
    private Timeline updateTimeline;

    public StudyStreakWidget() {
        manajerBasisData = new ManajerBasisData();
        getStyleClass().add("streak-widget");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPrefSize(180, 180);
        setMinSize(180, 180);
        setMaxSize(180, 180);

        streakNumber = new Label("0");
        streakNumber.getStyleClass().add("streak-number");

        streakLabel = new Label("Hari Beruntun");
        streakLabel.getStyleClass().add("streak-label");

        motivationLabel = new Label("Pertahankan!");
        motivationLabel.getStyleClass().add("streak-motivation");

        getChildren().addAll(streakNumber, streakLabel, motivationLabel);

        updateStreak();
        startAutoUpdate();
    }

    private void updateStreak() {
        try {
            int streak = manajerBasisData.ambilRuntutanBelajar();
            streakNumber.setText(String.valueOf(streak));
            updateMotivation(streak);
        } catch (SQLException e) {
            streakNumber.setText("0");
            motivationLabel.setText("Mulai perjalananmu!");
        }
    }

    private void updateMotivation(int streak) {
        if (streak == 0) {
            motivationLabel.setText("Mulai hari ini!");
        } else if (streak == 1) {
            motivationLabel.setText("Awal yang bagus!");
        } else if (streak < 7) {
            motivationLabel.setText("Terus pertahankan!");
        } else if (streak < 30) {
            motivationLabel.setText("Luar biasa!");
        } else if (streak < 100) {
            motivationLabel.setText("Kamu hebat!");
        } else {
            motivationLabel.setText("Legendaris!");
        }
    }

    private void startAutoUpdate() {
        updateTimeline = new Timeline(
                new KeyFrame(Duration.minutes(5), _ -> updateStreak()));
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
