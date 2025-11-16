package com.studyplanner.controller;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.StudySession;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ScheduleViewController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private VBox scheduleContainer;

    @FXML
    private Label selectedDateLabel;

    @FXML
    private Label sessionCountLabel;

    private DatabaseManager dbManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = new DatabaseManager();
        datePicker.setValue(LocalDate.now());
        datePicker.setOnAction(_ -> loadSchedule());
        loadSchedule();
    }

    private void loadSchedule() {
        LocalDate selectedDate = datePicker.getValue();
        scheduleContainer.getChildren().clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM yyyy"
        );
        selectedDateLabel.setText(selectedDate.format(formatter));

        try {
            List<StudySession> sessions = dbManager.getSessionsByDate(
                selectedDate
            );
            sessionCountLabel.setText(sessions.size() + " sesi belajar");

            if (sessions.isEmpty()) {
                Label emptyLabel = new Label(
                    "Tidak ada jadwal untuk tanggal ini."
                );
                emptyLabel.getStyleClass().add("text-muted");
                emptyLabel.setStyle("-fx-padding: 20;");
                scheduleContainer.getChildren().add(emptyLabel);
            } else {
                for (StudySession session : sessions) {
                    scheduleContainer
                        .getChildren()
                        .add(createScheduleCard(session));
                }
            }
        } catch (SQLException e) {
            showError("Error loading schedule: " + e.getMessage());
        }
    }

    private VBox createScheduleCard(StudySession session) {
        VBox card = new VBox(10);
        card.getStyleClass().add("schedule-card");

        Label titleLabel = new Label(session.getTopicName());
        titleLabel.getStyleClass().add("task-title");

        Label courseLabel = new Label(session.getCourseName());
        courseLabel.getStyleClass().add("task-course");

        HBox typeRow = new HBox(10);
        typeRow.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(
            getSessionTypeLabel(session.getSessionType())
        );
        typeLabel
            .getStyleClass()
            .addAll("task-type", getBadgeClass(session.getSessionType()));

        Label durationLabel = new Label(
            session.getDurationMinutes() + " menit"
        );
        durationLabel.getStyleClass().add("task-duration");

        typeRow.getChildren().addAll(typeLabel, durationLabel);

        Label statusLabel = new Label(
            session.isCompleted() ? "Selesai" : "Belum selesai"
        );
        statusLabel.setStyle(
            session.isCompleted()
                ? "-fx-text-fill: #10b981; -fx-font-weight: 600;"
                : "-fx-text-fill: #f59e0b;"
        );

        card
            .getChildren()
            .addAll(titleLabel, courseLabel, typeRow, statusLabel);

        return card;
    }

    private String getSessionTypeLabel(String type) {
        return switch (type) {
            case "INITIAL_STUDY" -> "Belajar Pertama";
            case "REVIEW" -> "Review";
            case "PRACTICE" -> "Latihan";
            default -> type;
        };
    }

    private String getBadgeClass(String type) {
        return switch (type) {
            case "INITIAL_STUDY" -> "badge-initial_study";
            case "REVIEW" -> "badge-review";
            case "PRACTICE" -> "badge-practice";
            default -> "badge-initial_study";
        };
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
