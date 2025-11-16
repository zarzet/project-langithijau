package com.studyplanner.controller;

import com.studyplanner.algorithm.ScheduleGenerator;
import com.studyplanner.component.AnalogClock;
import com.studyplanner.component.CustomWindowDecorator;
import com.studyplanner.component.NextReviewWidget;
import com.studyplanner.component.StudyStreakWidget;
import com.studyplanner.component.StudyTimeTodayWidget;
import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller untuk Main Dashboard
 */
public class MainController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label totalTopicsLabel;

    @FXML
    private Label masteredTopicsLabel;

    @FXML
    private Label todayTasksLabel;

    @FXML
    private Label completedTasksLabel;

    @FXML
    private ProgressBar overallProgressBar;

    @FXML
    private ProgressBar todayProgressBar;

    @FXML
    private Label overallProgressLabel;

    @FXML
    private Label todayProgressLabel;

    @FXML
    private VBox todayTasksContainer;

    @FXML
    private VBox upcomingExamsContainer;

    @FXML
    private Button manageCourseBtn;

    @FXML
    private Button viewScheduleBtn;

    @FXML
    private Button generateScheduleBtn;

    @FXML
    private Button themeToggleBtn;

    @FXML
    private VBox streakContainer;

    @FXML
    private VBox studyTimeContainer;

    @FXML
    private VBox nextReviewContainer;

    @FXML
    private VBox clockContainer;

    private DatabaseManager dbManager;
    private boolean isDarkMode = false;
    private ScheduleGenerator scheduleGenerator;
    private StudyStreakWidget streakWidget;
    private StudyTimeTodayWidget studyTimeWidget;
    private NextReviewWidget nextReviewWidget;
    private AnalogClock analogClock;
    private Timeline autoRefreshTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = new DatabaseManager();
        scheduleGenerator = new ScheduleGenerator(dbManager);

        setupUI();
        loadDashboardData();
        setupAutoRefresh();
    }

    private void setupUI() {
        // Set tanggal hari ini
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM yyyy"
        );
        dateLabel.setText(LocalDate.now().format(formatter));

        // Setup study streak widget
        if (streakContainer != null) {
            streakWidget = new StudyStreakWidget();
            streakContainer.getChildren().clear();
            streakContainer.getChildren().add(streakWidget);
        }

        // Setup study time today widget
        if (studyTimeContainer != null) {
            studyTimeWidget = new StudyTimeTodayWidget();
            studyTimeContainer.getChildren().clear();
            studyTimeContainer.getChildren().add(studyTimeWidget);
        }

        // Setup next review widget
        if (nextReviewContainer != null) {
            nextReviewWidget = new NextReviewWidget();
            nextReviewContainer.getChildren().clear();
            nextReviewContainer.getChildren().add(nextReviewWidget);
        }

        // Setup analog clock
        if (clockContainer != null) {
            analogClock = new AnalogClock(160);

            // Wrap clock in styled container
            VBox clockBox = new VBox();
            clockBox.setAlignment(javafx.geometry.Pos.CENTER);
            clockBox.getStyleClass().add("clock-container");
            clockBox.setPrefSize(200, 200);
            clockBox.setMinSize(200, 200);
            clockBox.setMaxSize(200, 200);
            clockBox.getChildren().add(analogClock);

            clockContainer.getChildren().clear();
            clockContainer.getChildren().add(clockBox);
        }

        // Setup button actions
        manageCourseBtn.setOnAction(_ -> openCourseManagement());
        viewScheduleBtn.setOnAction(_ -> openScheduleView());
        generateScheduleBtn.setOnAction(_ -> generateNewSchedule());

        if (themeToggleBtn != null) {
            themeToggleBtn.setText(isDarkMode ? "☼" : "◐");
            themeToggleBtn.setOnAction(_ -> toggleDarkMode());
        }
    }

    private void setupAutoRefresh() {
        // Auto-refresh every 30 seconds
        autoRefreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(30), _ -> loadDashboardData())
        );
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;

        if (themeToggleBtn != null) {
            themeToggleBtn.setText(isDarkMode ? "☼" : "◐");
        }

        if (analogClock != null) {
            analogClock.setDarkMode(isDarkMode);
        }

        if (manageCourseBtn.getScene() != null) {
            if (isDarkMode) {
                manageCourseBtn
                    .getScene()
                    .getRoot()
                    .getStyleClass()
                    .add("dark-mode");
            } else {
                manageCourseBtn
                    .getScene()
                    .getRoot()
                    .getStyleClass()
                    .remove("dark-mode");
            }
        }
    }

    private void loadDashboardData() {
        try {
            // Load progress statistics
            ScheduleGenerator.StudyProgress progress =
                scheduleGenerator.getStudyProgress();

            totalTopicsLabel.setText(String.valueOf(progress.getTotalTopics()));
            masteredTopicsLabel.setText(
                String.valueOf(progress.getMasteredTopics())
            );
            todayTasksLabel.setText(String.valueOf(progress.getTodayTotal()));
            completedTasksLabel.setText(
                String.valueOf(progress.getTodayCompleted())
            );

            // Update progress bars
            overallProgressBar.setProgress(
                progress.getOverallProgress() / 100.0
            );
            todayProgressBar.setProgress(progress.getTodayProgress() / 100.0);

            overallProgressLabel.setText(
                String.format("%.0f%%", progress.getOverallProgress())
            );
            todayProgressLabel.setText(
                String.format("%.0f%%", progress.getTodayProgress())
            );

            // Load today's tasks
            loadTodayTasks();

            // Load upcoming exams
            loadUpcomingExams();

            // Refresh widgets
            if (streakWidget != null) {
                streakWidget.refresh();
            }
            if (studyTimeWidget != null) {
                studyTimeWidget.refresh();
            }
            if (nextReviewWidget != null) {
                nextReviewWidget.refresh();
            }
        } catch (SQLException e) {
            showError("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTodayTasks() throws SQLException {
        todayTasksContainer.getChildren().clear();
        List<StudySession> sessions = dbManager.getTodaySessions();

        if (sessions.isEmpty()) {
            Label emptyLabel = new Label(
                "Belum ada tugas untuk hari ini. Klik 'Generate Schedule' untuk membuat jadwal otomatis."
            );
            emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
            todayTasksContainer.getChildren().add(emptyLabel);
        } else {
            for (StudySession session : sessions) {
                todayTasksContainer.getChildren().add(createTaskCard(session));
            }
        }
    }

    private VBox createTaskCard(StudySession session) {
        VBox card = new VBox(8);
        card.getStyleClass().add("task-card");

        // Header dengan checkbox
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(session.isCompleted());
        checkBox.setOnAction(_ ->
            markTaskComplete(session, checkBox.isSelected())
        );

        Label titleLabel = new Label(session.getTopicName());
        titleLabel.getStyleClass().add("task-title");
        if (session.isCompleted()) {
            titleLabel.setStyle(
                "-fx-text-fill: #888; -fx-strikethrough: true;"
            );
        }

        header.getChildren().addAll(checkBox, titleLabel);

        // Course info
        Label courseLabel = new Label(session.getCourseName());
        courseLabel.getStyleClass().add("task-course");

        // Session type badge
        Label typeLabel = new Label(
            getSessionTypeLabel(session.getSessionType())
        );
        typeLabel.getStyleClass().add("task-type");
        typeLabel
            .getStyleClass()
            .add("badge-" + session.getSessionType().toLowerCase());

        // Duration
        Label durationLabel = new Label(
            session.getDurationMinutes() + " menit"
        );
        durationLabel.getStyleClass().add("task-duration");

        HBox footer = new HBox(15);
        footer.getChildren().addAll(typeLabel, durationLabel);

        card.getChildren().addAll(header, courseLabel, footer);

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

    private void markTaskComplete(StudySession session, boolean completed) {
        try {
            session.setCompleted(completed);
            if (completed) {
                session.setCompletedAt(java.time.LocalDateTime.now());

                // Show performance rating dialog
                showPerformanceRatingDialog(session);
            } else {
                session.setCompletedAt(null);
                session.setPerformanceRating(0);
                dbManager.updateStudySession(session);
            }

            loadDashboardData();
        } catch (SQLException e) {
            showError("Error updating task: " + e.getMessage());
        }
    }

    private void showPerformanceRatingDialog(StudySession session) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Rating Performa");
        dialog.setHeaderText("Bagaimana performa Anda untuk sesi ini?");

        ButtonType okButtonType = new ButtonType(
            "OK",
            ButtonBar.ButtonData.OK_DONE
        );
        dialog
            .getDialogPane()
            .getButtonTypes()
            .addAll(okButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        Label label = new Label("Pilih rating (1-5):");

        ToggleGroup ratingGroup = new ToggleGroup();
        HBox ratingBox = new HBox(10);

        for (int i = 1; i <= 5; i++) {
            RadioButton rb = new RadioButton(i + " - " + getRatingLabel(i));
            rb.setToggleGroup(ratingGroup);
            rb.setUserData(i);
            if (i == 3) rb.setSelected(true);
            ratingBox.getChildren().add(rb);
        }

        content.getChildren().addAll(label, ratingBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selected =
                    (RadioButton) ratingGroup.getSelectedToggle();
                return (Integer) selected.getUserData();
            }
            return null;
        });

        dialog
            .showAndWait()
            .ifPresent(rating -> {
                try {
                    session.setPerformanceRating(rating);
                    dbManager.updateStudySession(session);

                    // Update topic based on spaced repetition algorithm
                    Topic topic = dbManager.getTopicById(session.getTopicId());
                    if (topic != null) {
                        if (topic.getFirstStudyDate() == null) {
                            topic.setFirstStudyDate(LocalDate.now());
                        }

                        LocalDate nextReview =
                            com.studyplanner.algorithm.SpacedRepetition.calculateNextReviewDate(
                                topic,
                                rating
                            );

                        dbManager.updateTopic(topic);

                        showInfo(
                            "Sesi berhasil diselesaikan!\nReview berikutnya: " +
                                nextReview
                        );
                    }
                } catch (SQLException e) {
                    showError("Error saving rating: " + e.getMessage());
                }
            });
    }

    private String getRatingLabel(int rating) {
        return switch (rating) {
            case 1 -> "Sangat Sulit";
            case 2 -> "Sulit";
            case 3 -> "Cukup";
            case 4 -> "Baik";
            case 5 -> "Sangat Mudah";
            default -> "";
        };
    }

    private void loadUpcomingExams() throws SQLException {
        upcomingExamsContainer.getChildren().clear();
        List<ExamSchedule> exams = dbManager.getUpcomingExams();

        if (exams.isEmpty()) {
            Label emptyLabel = new Label("Belum ada ujian yang dijadwalkan.");
            emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
            upcomingExamsContainer.getChildren().add(emptyLabel);
        } else {
            for (ExamSchedule exam : exams) {
                upcomingExamsContainer.getChildren().add(createExamCard(exam));
            }
        }
    }

    private VBox createExamCard(ExamSchedule exam) throws SQLException {
        VBox card = new VBox(5);
        card.getStyleClass().add("exam-card");

        Label titleLabel = new Label(exam.getTitle());
        titleLabel.getStyleClass().add("exam-title");

        Course course = dbManager.getCourseById(exam.getCourseId());
        Label courseLabel = new Label(course != null ? course.getCode() : "");
        courseLabel.getStyleClass().add("exam-course");

        Label dateLabel = new Label(exam.getExamDate().toString());
        dateLabel.getStyleClass().add("exam-date");

        int daysLeft = exam.getDaysUntilExam();
        Label daysLabel = new Label(daysLeft + " hari lagi");
        daysLabel.getStyleClass().add("exam-days");

        card
            .getChildren()
            .addAll(titleLabel, courseLabel, dateLabel, daysLabel);

        return card;
    }

    private void openCourseManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/CourseManagement.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Manajemen Mata Kuliah & Topik");
            Scene scene = new Scene(root, 1000, 700);
            scene
                .getStylesheets()
                .add(getClass().getResource("/css/style.css").toExternalForm());
            if (isDarkMode) {
                scene.getRoot().getStyleClass().add("dark-mode");
            }
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Apply custom window decoration
            CustomWindowDecorator.decorate(
                stage,
                "Manajemen Mata Kuliah & Topik",
                isDarkMode
            );

            CourseManagementController controller = loader.getController();
            controller.setMainController(this);

            stage.showAndWait();

            // Refresh after closing
            loadDashboardData();
        } catch (IOException e) {
            showError("Error opening course management: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openScheduleView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/ScheduleView.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root, 900, 700);
            scene
                .getStylesheets()
                .add(getClass().getResource("/css/style.css").toExternalForm());
            if (isDarkMode) {
                scene.getRoot().getStyleClass().add("dark-mode");
            }
            stage.setScene(scene);

            // Apply custom window decoration before setting modality
            CustomWindowDecorator.decorate(stage, "Jadwal Belajar", isDarkMode);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            showError("Error opening schedule view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateNewSchedule() {
        try {
            // Generate untuk 7 hari ke depan
            scheduleGenerator.generateAndSaveSchedule(7);
            showInfo(
                "Jadwal belajar berhasil di-generate untuk 7 hari ke depan!"
            );
            loadDashboardData();
        } catch (SQLException e) {
            showError("Error generating schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }
}
