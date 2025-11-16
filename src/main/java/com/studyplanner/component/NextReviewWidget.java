package com.studyplanner.component;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.Topic;
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
    private final DatabaseManager dbManager;
    private Timeline updateTimeline;

    public NextReviewWidget() {
        dbManager = new DatabaseManager();
        getStyleClass().add("next-review-widget");
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPrefSize(200, 200);
        setMinSize(200, 200);
        setMaxSize(200, 200);

        Label titleLabel = new Label("Next Review");
        titleLabel.getStyleClass().add("widget-title");

        topicLabel = new Label("No reviews");
        topicLabel.getStyleClass().add("next-review-topic");
        topicLabel.setWrapText(true);
        topicLabel.setMaxWidth(180);
        topicLabel.setAlignment(Pos.CENTER);

        courseLabel = new Label("");
        courseLabel.getStyleClass().add("next-review-course");
        courseLabel.setWrapText(true);
        courseLabel.setMaxWidth(180);

        dueLabel = new Label("");
        dueLabel.getStyleClass().add("next-review-due");

        statusLabel = new Label("All caught up!");
        statusLabel.getStyleClass().add("next-review-status");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(180);
        statusLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(
            titleLabel,
            topicLabel,
            courseLabel,
            dueLabel,
            statusLabel
        );

        updateNextReview();
        startAutoUpdate();
    }

    private void updateNextReview() {
        try {
            var topics = dbManager.getNextReviewTopics(1);

            if (topics.isEmpty()) {
                topicLabel.setText("No reviews");
                courseLabel.setText("");
                dueLabel.setText("");
                statusLabel.setText("All caught up!");
                statusLabel.setVisible(true);
            } else {
                Topic topic = topics.get(0);
                topicLabel.setText(topic.getName());

                courseLabel.setText("");
                courseLabel.setVisible(false);

                LocalDate nextReviewDate = topic
                    .getLastReviewDate()
                    .plusDays(topic.getInterval());
                long daysUntil = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    nextReviewDate
                );

                if (daysUntil < 0) {
                    dueLabel.setText(
                        "Overdue by " + Math.abs(daysUntil) + " days"
                    );
                    dueLabel.setStyle(
                        "-fx-text-fill: #ef4444; -fx-font-weight: 600;"
                    );
                } else if (daysUntil == 0) {
                    dueLabel.setText("Due today");
                    dueLabel.setStyle(
                        "-fx-text-fill: #3b82f6; -fx-font-weight: 600;"
                    );
                } else if (daysUntil == 1) {
                    dueLabel.setText("Due tomorrow");
                    dueLabel.setStyle("-fx-text-fill: #3b82f6;");
                } else {
                    dueLabel.setText("Due in " + daysUntil + " days");
                    dueLabel.setStyle("-fx-text-fill: #3b82f6;");
                }

                statusLabel.setText("Priority: " + topic.getPriority() + "/5");
                statusLabel.setStyle("-fx-text-fill: #64748b;");
                statusLabel.setVisible(true);
            }
        } catch (SQLException e) {
            topicLabel.setText("Error loading");
            courseLabel.setText("");
            dueLabel.setText("");
            statusLabel.setText("");
            statusLabel.setVisible(false);
        }
    }

    private void startAutoUpdate() {
        updateTimeline = new Timeline(
            new KeyFrame(Duration.minutes(5), _ -> updateNextReview())
        );
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
