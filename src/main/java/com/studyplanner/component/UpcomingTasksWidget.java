package com.studyplanner.component;

import com.studyplanner.model.SesiBelajar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UpcomingTasksWidget extends VBox {

    private static final Locale ID_LOCALE = new Locale("id", "ID");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "EEEE, dd MMM",
            ID_LOCALE);

    private final VBox listContainer;
    private final Label emptyLabel;
    private final ScrollPane scrollPane;

    public UpcomingTasksWidget() {
        getStyleClass().addAll("upcoming-widget", "widget-interactive");
        setSpacing(6);
        setPadding(new Insets(0));

        Label title = new Label("Tugas Mendatang");
        title.getStyleClass().add("widget-title");

        Label subtitle = new Label("Prioritaskan tugas terdekat");
        subtitle.getStyleClass().add("achievement-detail");

        listContainer = new VBox(5);
        listContainer.setFillWidth(true);
        listContainer.setPadding(new Insets(2));

        emptyLabel = new Label("Tidak ada tugas dalam beberapa hari ke depan.");
        emptyLabel.getStyleClass().add("achievement-detail");
        emptyLabel.setWrapText(true);

        // Wrap listContainer in ScrollPane
        scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(110);
        scrollPane.setMaxHeight(120);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("scroll-pane-transparent");

        getChildren().addAll(title, subtitle, scrollPane);
        setAlignment(Pos.TOP_LEFT);
    }

    public void setSessions(List<SesiBelajar> sessions) {
        listContainer.getChildren().clear();

        if (sessions == null || sessions.isEmpty()) {
            listContainer.getChildren().add(emptyLabel);
            return;
        }

        for (SesiBelajar session : sessions) {
            listContainer.getChildren().add(createTaskItem(session));
        }
    }

    private VBox createTaskItem(SesiBelajar session) {
        VBox card = new VBox(3);
        card.getStyleClass().add("upcoming-task");

        Label titleLabel = new Label(session.getNamaTopik());
        titleLabel.getStyleClass().add("upcoming-task-title");

        Label courseLabel = new Label(session.getNamaMataKuliah());
        courseLabel.getStyleClass().add("upcoming-task-course");

        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(
                getSessionTypeLabel(session.getTipeSesi()));
        typeLabel
                .getStyleClass()
                .addAll(
                        "task-type",
                        "badge-" + session.getTipeSesi().toLowerCase());

        Label dateLabel = new Label(formatRelativeDate(session.getTanggalJadwal()));
        dateLabel.getStyleClass().add("upcoming-task-date");

        footer.getChildren().addAll(typeLabel, dateLabel);

        card.getChildren().addAll(titleLabel, courseLabel, footer);
        return card;
    }

    private String formatRelativeDate(LocalDate date) {
        if (date == null) {
            return "Jadwal tidak diketahui";
        }

        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return "Hari ini";
        }
        if (date.equals(today.plusDays(1))) {
            return "Besok";
        }
        if (date.isBefore(today.plusDays(7))) {
            return date
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, ID_LOCALE);
        }
        return DATE_FORMATTER.format(date);
    }

    private String getSessionTypeLabel(String type) {
        if (type == null)
            return "-";

        return switch (type) {
            case "INITIAL_STUDY" -> "Belajar Baru";
            case "REVIEW" -> "Review";
            case "PRACTICE" -> "Latihan";
            default -> type;
        };
    }
}
