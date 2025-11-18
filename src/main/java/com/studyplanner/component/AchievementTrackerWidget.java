package com.studyplanner.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AchievementTrackerWidget extends VBox {

    public static final int FOCUS_TARGET_MINUTES = 120;

    private final Label streakBadge;
    private final ProgressBar taskProgress;
    private final Label taskValueLabel;
    private final Label taskDetailLabel;

    private final ProgressBar reviewProgress;
    private final Label reviewValueLabel;
    private final Label reviewDetailLabel;

    private final ProgressBar focusProgress;
    private final Label focusValueLabel;
    private final Label focusDetailLabel;

    public AchievementTrackerWidget() {
        getStyleClass().addAll("achievement-widget", "widget-interactive");
        setSpacing(8);
        setPadding(new Insets(0));
        setFillWidth(true);

        VBox content = new VBox(8);
        content.setFillWidth(true);

        Label title = new Label("Pelacak Pencapaian");
        title.getStyleClass().add("widget-title");
        title.setWrapText(true);
        title.setMaxWidth(170);

        streakBadge = new Label("0-day streak");
        streakBadge.getStyleClass().add("achievement-badge");
        streakBadge.setWrapText(true);
        streakBadge.setMaxWidth(100);

        HBox header = new HBox(10, title, streakBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        taskProgress = new ProgressBar(0);
        taskProgress.setPrefWidth(150);
        taskProgress.setPrefHeight(5);
        taskProgress.getStyleClass().add("progress-bar");
        taskValueLabel = new Label("0/0 selesai");
        taskValueLabel.getStyleClass().add("achievement-value");
        taskValueLabel.setWrapText(true);
        taskValueLabel.setMaxWidth(170);
        
        taskDetailLabel = new Label("Belum ada tugas hari ini");
        taskDetailLabel.getStyleClass().add("achievement-detail");
        taskDetailLabel.setWrapText(true);
        taskDetailLabel.setMaxWidth(170);

        VBox taskBox = buildAchievementItem(
            "Target Harian",
            taskValueLabel,
            taskProgress,
            taskDetailLabel
        );

        reviewProgress = new ProgressBar(0);
        reviewProgress.setPrefWidth(150);
        reviewProgress.setPrefHeight(5);
        reviewProgress.getStyleClass().add("progress-bar");
        reviewValueLabel = new Label("0 review");
        reviewValueLabel.getStyleClass().add("achievement-value");
        reviewValueLabel.setWrapText(true);
        reviewValueLabel.setMaxWidth(170);
        
        reviewDetailLabel = new Label("Review menjaga retensi materi");
        reviewDetailLabel.getStyleClass().add("achievement-detail");
        reviewDetailLabel.setWrapText(true);
        reviewDetailLabel.setMaxWidth(170);

        VBox reviewBox = buildAchievementItem(
            "Sesi Review",
            reviewValueLabel,
            reviewProgress,
            reviewDetailLabel
        );

        focusProgress = new ProgressBar(0);
        focusProgress.setPrefWidth(150);
        focusProgress.setPrefHeight(5);
        focusProgress.getStyleClass().add("progress-bar");
        focusValueLabel = new Label("0/" + FOCUS_TARGET_MINUTES + " menit");
        focusValueLabel.getStyleClass().add("achievement-value");
        focusValueLabel.setWrapText(true);
        focusValueLabel.setMaxWidth(170);
        
        focusDetailLabel = new Label("Mulai timer belajar");
        focusDetailLabel.getStyleClass().add("achievement-detail");
        focusDetailLabel.setWrapText(true);
        focusDetailLabel.setMaxWidth(170);

        VBox focusBox = buildAchievementItem(
            "Menit Fokus",
            focusValueLabel,
            focusProgress,
            focusDetailLabel
        );

        content.getChildren().addAll(header, taskBox, reviewBox, focusBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane-transparent");
        scrollPane.setPannable(true);

        getChildren().setAll(scrollPane);
    }

    private VBox buildAchievementItem(
        String title,
        Label value,
        ProgressBar progressBar,
        Label detail
    ) {
        Label itemTitle = new Label(title);
        itemTitle.getStyleClass().add("achievement-title");
        itemTitle.setWrapText(true);
        itemTitle.setMaxWidth(170);

        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("achievement-item");
        box.getChildren().addAll(itemTitle, value, progressBar, detail);

        return box;
    }

    public void updateData(
        int tasksCompleted,
        int tasksTotal,
        int reviewCompleted,
        int reviewTotal,
        int focusMinutes,
        int streakDays
    ) {
        updateTasksSection(tasksCompleted, tasksTotal);
        updateReviewSection(reviewCompleted, reviewTotal);
        updateFocusSection(focusMinutes);
        updateStreakBadge(streakDays);
    }

    private void updateTasksSection(int completed, int total) {
        if (total <= 0) {
            taskProgress.setProgress(0);
            taskValueLabel.setText("0/0 selesai");
            taskDetailLabel.setText("Belum ada tugas hari ini");
            return;
        }

        double progress = Math.min((double) completed / total, 1.0);
        taskProgress.setProgress(progress);
        taskValueLabel.setText(completed + "/" + total + " selesai");

        int remaining = Math.max(total - completed, 0);
        if (remaining == 0) {
            taskDetailLabel.setText("Semua tugas hari ini selesai!");
        } else {
            taskDetailLabel.setText(remaining + " tugas lagi untuk tuntas");
        }
    }

    private void updateReviewSection(int completed, int total) {
        if (total <= 0) {
            reviewProgress.setProgress(0);
            reviewValueLabel.setText("0 review");
            reviewDetailLabel.setText("Belum ada sesi review terjadwal");
            return;
        }

        double progress = Math.min((double) completed / total, 1.0);
        reviewProgress.setProgress(progress);
        reviewValueLabel.setText(completed + "/" + total + " review selesai");

        int remaining = Math.max(total - completed, 0);
        if (remaining == 0) {
            reviewDetailLabel.setText("Seluruh review terselesaikan!");
        } else {
            reviewDetailLabel.setText(remaining + " review lagi untuk stabil");
        }
    }

    private void updateFocusSection(int minutes) {
        double progress = Math.min(
            (double) minutes / FOCUS_TARGET_MINUTES,
            1.0
        );
        focusProgress.setProgress(progress);
        focusValueLabel.setText(minutes + "/" + FOCUS_TARGET_MINUTES + " menit");

        int remaining = Math.max(FOCUS_TARGET_MINUTES - minutes, 0);
        if (remaining == 0) {
            focusDetailLabel.setText("Target fokus tercapai!");
        } else {
            focusDetailLabel.setText("Tambahkan " + remaining + " menit lagi");
        }
    }

    private void updateStreakBadge(int streakDays) {
        if (streakDays <= 0) {
            streakBadge.setText("Mulai streak baru");
        } else if (streakDays == 1) {
            streakBadge.setText("1 hari beruntun");
        } else {
            streakBadge.setText(streakDays + " hari beruntun");
        }
    }
}
