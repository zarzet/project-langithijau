package com.studyplanner.controller;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.StudySession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk Schedule View
 */
public class ScheduleViewController implements Initializable {
    
    @FXML private DatePicker datePicker;
    @FXML private VBox scheduleContainer;
    @FXML private Label selectedDateLabel;
    @FXML private Label sessionCountLabel;
    
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
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        selectedDateLabel.setText(selectedDate.format(formatter));
        
        try {
            List<StudySession> sessions = dbManager.getSessionsByDate(selectedDate);
            sessionCountLabel.setText(sessions.size() + " sesi belajar");
            
            if (sessions.isEmpty()) {
                Label emptyLabel = new Label("Tidak ada jadwal untuk tanggal ini.");
                emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                scheduleContainer.getChildren().add(emptyLabel);
            } else {
                for (StudySession session : sessions) {
                    scheduleContainer.getChildren().add(createScheduleCard(session));
                }
            }
        } catch (SQLException e) {
            showError("Error loading schedule: " + e.getMessage());
        }
    }
    
    private VBox createScheduleCard(StudySession session) {
        VBox card = new VBox(8);
        card.getStyleClass().add("schedule-card");
        card.setStyle("-fx-padding: 15; -fx-background-color: white; " +
                     "-fx-border-color: #ddd; -fx-border-radius: 5; " +
                     "-fx-background-radius: 5; -fx-spacing: 8;");
        
        Label titleLabel = new Label(session.getTopicName());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label courseLabel = new Label(session.getCourseName());
        courseLabel.setStyle("-fx-text-fill: #666;");
        
        Label typeLabel = new Label(getSessionTypeLabel(session.getSessionType()));
        typeLabel.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                          "-fx-padding: 3 8; -fx-background-radius: 3;");
        
        Label durationLabel = new Label("⏱ " + session.getDurationMinutes() + " menit");
        
        Label statusLabel = new Label(session.isCompleted() ? "✓ Selesai" : "⏳ Belum selesai");
        statusLabel.setStyle(session.isCompleted() ? 
            "-fx-text-fill: green; -fx-font-weight: bold;" : 
            "-fx-text-fill: orange;");
        
        card.getChildren().addAll(titleLabel, courseLabel, typeLabel, durationLabel, statusLabel);
        
        return card;
    }
    
    private String getSessionTypeLabel(String type) {
        return switch (type) {
            case "INITIAL_STUDY" -> "📚 Belajar Pertama";
            case "REVIEW" -> "🔄 Review";
            case "PRACTICE" -> "✏️ Latihan";
            default -> type;
        };
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

