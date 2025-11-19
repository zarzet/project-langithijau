package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.SesiBelajar;
import com.studyplanner.utilitas.PembuatDialogMD3;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class KontrolerTampilanJadwal implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button prevWeekBtn;

    @FXML
    private Button nextWeekBtn;

    @FXML
    private Button todayBtn;

    @FXML
    private HBox weekCalendar;

    @FXML
    private VBox scheduleContainer;

    @FXML
    private VBox emptyState;

    @FXML
    private Label selectedDateLabel;

    @FXML
    private Label sessionCountLabel;

    private ManajerBasisData manajerBasisData;
    private LocalDate currentWeekStart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();

        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        currentWeekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);

        // Setup event handlers
        datePicker.setOnAction(_ -> {
            loadSchedule();
            updateWeekCalendar();
        });

        if (prevWeekBtn != null) {
            prevWeekBtn.setOnAction(_ -> navigateWeek(-7));
        }

        if (nextWeekBtn != null) {
            nextWeekBtn.setOnAction(_ -> navigateWeek(7));
        }

        if (todayBtn != null) {
            todayBtn.setOnAction(_ -> {
                datePicker.setValue(LocalDate.now());
                currentWeekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
                updateWeekCalendar();
                loadSchedule();
            });
        }

        updateWeekCalendar();
        loadSchedule();
    }

    private void navigateWeek(int days) {
        currentWeekStart = currentWeekStart.plusDays(days);
        datePicker.setValue(currentWeekStart);
        updateWeekCalendar();
        loadSchedule();
    }

    private void updateWeekCalendar() {
        if (weekCalendar == null) return;

        weekCalendar.getChildren().clear();
        LocalDate selectedDate = datePicker.getValue();

        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            VBox dayCard = createDayCard(date, date.equals(selectedDate));
            weekCalendar.getChildren().add(dayCard);
        }
    }

    private VBox createDayCard(LocalDate date, boolean isSelected) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(80);
        card.setPrefHeight(80);

        // Use CSS classes instead of inline styles
        card.getStyleClass().addAll("week-day-card");
        if (isSelected) {
            card.getStyleClass().add("selected");
        }

        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.of("id", "ID"));
        Label dayLabel = new Label(dayName);
        dayLabel.getStyleClass().add("day-name-label");

        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.getStyleClass().add("day-number-label");

        // Check if today
        if (date.equals(LocalDate.now())) {
            Label todayDot = new Label("•");
            todayDot.getStyleClass().add("today-indicator");
            card.getChildren().add(todayDot);
        }

        card.getChildren().addAll(dayLabel, dateLabel);

        // Click handler
        card.setOnMouseClicked(_ -> {
            datePicker.setValue(date);
            loadSchedule();
            updateWeekCalendar();
        });

        return card;
    }

    private void loadSchedule() {
        LocalDate selectedDate = datePicker.getValue();
        scheduleContainer.getChildren().clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy", Locale.of("id", "ID"));
        selectedDateLabel.setText(selectedDate.format(formatter));

        try {
            List<SesiBelajar> sessions = manajerBasisData.ambilSesiBerdasarkanTanggal(
                    selectedDate);
            sessionCountLabel.setText(sessions.size() + " sesi belajar");

            if (sessions.isEmpty()) {
                // Show empty state
                if (emptyState != null) {
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                }
                scheduleContainer.setVisible(false);
                scheduleContainer.setManaged(false);
            } else {
                // Hide empty state
                if (emptyState != null) {
                    emptyState.setVisible(false);
                    emptyState.setManaged(false);
                }
                scheduleContainer.setVisible(true);
                scheduleContainer.setManaged(true);

                // Add schedule cards
                for (SesiBelajar session : sessions) {
                    scheduleContainer
                            .getChildren()
                            .add(createScheduleCard(session));
                }
            }
        } catch (SQLException e) {
            showError("Kesalahan memuat jadwal: " + e.getMessage());
        }
    }

    private VBox createScheduleCard(SesiBelajar session) {
        VBox card = new VBox(12);
        card.getStyleClass().add("schedule-card");
        if (session.isSelesai()) {
            card.getStyleClass().add("completed");
        }

        // Header with title and status
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label titleLabel = new Label(session.getNamaTopik());
        titleLabel.getStyleClass().add("schedule-title");

        Label courseLabel = new Label(session.getNamaMataKuliah());
        courseLabel.getStyleClass().add("schedule-course");

        titleBox.getChildren().addAll(titleLabel, courseLabel);

        // Status badge
        Label statusBadge = new Label(session.isSelesai() ? "✓ Selesai" : "○ Belum");
        statusBadge.getStyleClass().add("status-badge");
        if (session.isSelesai()) {
            statusBadge.getStyleClass().add("completed");
        } else {
            statusBadge.getStyleClass().add("pending");
        }

        header.getChildren().addAll(titleBox, statusBadge);

        // Meta info row
        HBox metaRow = new HBox(16);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        // Type badge
        Label typeLabel = new Label(getSessionTypeLabel(session.getTipeSesi()));
        typeLabel.getStyleClass().addAll("task-type", getBadgeClass(session.getTipeSesi()));

        // Duration
        Label durationLabel = new Label("Durasi: " + session.getDurasiMenit() + " menit");
        durationLabel.getStyleClass().add("schedule-duration");

        metaRow.getChildren().addAll(typeLabel, durationLabel);

        card.getChildren().addAll(header, metaRow);

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
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.ERROR, "Kesalahan", message);
        alert.showAndWait();
    }
}
