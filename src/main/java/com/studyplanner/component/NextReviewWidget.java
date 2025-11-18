package com.studyplanner.component;

import com.studyplanner.database.ManajerBasisData;
import com.studyplanner.model.Topik;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class NextReviewWidget extends VBox {

    private final Label topicLabel;
    private final Label courseLabel;
    private final Label dueLabel;
    private final Label statusLabel;
    private final ManajerBasisData manajerBasisData;
    private Timeline updateTimeline;

    public NextReviewWidget() {
        manajerBasisData = new ManajerBasisData();
        getStyleClass().add("next-review-widget");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPrefSize(180, 180);
        setMinSize(180, 180);
        setMaxSize(180, 180);

        Label titleLabel = new Label("Review Berikutnya");
        titleLabel.getStyleClass().add("widget-title");

        topicLabel = new Label("Tidak ada review");
        topicLabel.getStyleClass().add("next-review-topic");
        topicLabel.setWrapText(true);
        topicLabel.setMaxWidth(170);
        topicLabel.setAlignment(Pos.CENTER);

        courseLabel = new Label("");
        courseLabel.getStyleClass().add("next-review-course");
        courseLabel.setWrapText(true);
        courseLabel.setMaxWidth(170);

        dueLabel = new Label("");
        dueLabel.getStyleClass().add("next-review-due");

        statusLabel = new Label("Semua selesai!");
        statusLabel.getStyleClass().add("next-review-status");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(140);
        statusLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(
                titleLabel,
                topicLabel,
                courseLabel,
                dueLabel,
                statusLabel);

        updateNextReview();
        startAutoUpdate();
    }

    private void updateNextReview() {
        try {
            var topics = manajerBasisData.ambilTopikUlasanBerikutnya(1);

            if (topics.isEmpty()) {
                topicLabel.setText("Tidak ada review");
                courseLabel.setText("");
                dueLabel.setText("");
                statusLabel.setText("Semua selesai!");
                statusLabel.setVisible(true);
            } else {
                Topik topik = topics.get(0);
                topicLabel.setText(topik.getNama());

                courseLabel.setText("");
                courseLabel.setVisible(false);

                LocalDate nextReviewDate = topik
                        .getTanggalUlasanTerakhir()
                        .plusDays(topik.getInterval());
                long daysUntil = ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        nextReviewDate);

                if (daysUntil < 0) {
                    dueLabel.setText(
                            "Terlambat " + Math.abs(daysUntil) + " hari");
                    dueLabel.setStyle(
                            "-fx-text-fill: #ef4444; -fx-font-weight: 600;");
                } else if (daysUntil == 0) {
                    dueLabel.setText("Jatuh tempo hari ini");
                    dueLabel.setStyle(
                            "-fx-text-fill: #3b82f6; -fx-font-weight: 600;");
                } else if (daysUntil == 1) {
                    dueLabel.setText("Jatuh tempo besok");
                    dueLabel.setStyle("-fx-text-fill: #3b82f6;");
                } else {
                    dueLabel.setText("Jatuh tempo " + daysUntil + " hari lagi");
                    dueLabel.setStyle("-fx-text-fill: #3b82f6;");
                }

                statusLabel.setText("Prioritas: " + topik.getPrioritas() + "/5");
                statusLabel.setStyle("-fx-text-fill: #64748b;");
                statusLabel.setVisible(true);
            }
        } catch (SQLException e) {
            topicLabel.setText("Gagal memuat");
            courseLabel.setText("");
            dueLabel.setText("");
            statusLabel.setText("");
            statusLabel.setVisible(false);
        }
    }

    private void startAutoUpdate() {
        updateTimeline = new Timeline(
                new KeyFrame(Duration.minutes(5), _ -> updateNextReview()));
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }

    public void refresh() {
        updateNextReview();
    }

    public void stopUpdates() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
}
