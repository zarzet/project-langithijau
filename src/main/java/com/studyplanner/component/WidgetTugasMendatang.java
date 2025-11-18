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

public class WidgetTugasMendatang extends VBox {

    private static final Locale ID_LOCALE = new Locale("id", "ID");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "EEEE, dd MMM",
            ID_LOCALE);

    private final VBox wadahDaftar;
    private final Label labelKosong;
    private final ScrollPane scrollPane;

    public WidgetTugasMendatang() {
        getStyleClass().addAll("upcoming-widget", "widget-interactive");
        setSpacing(6);
        setPadding(new Insets(0));

        Label judul = new Label("Tugas Mendatang");
        judul.getStyleClass().add("widget-title");

        Label subJudul = new Label("Prioritaskan tugas terdekat");
        subJudul.getStyleClass().add("achievement-detail");

        wadahDaftar = new VBox(5);
        wadahDaftar.setFillWidth(true);
        wadahDaftar.setPadding(new Insets(2));

        labelKosong = new Label("Tidak ada tugas dalam beberapa hari ke depan.");
        labelKosong.getStyleClass().add("achievement-detail");
        labelKosong.setWrapText(true);

        // Wrap listContainer in ScrollPane
        scrollPane = new ScrollPane(wadahDaftar);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(110);
        scrollPane.setMaxHeight(120);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("scroll-pane-transparent");

        getChildren().addAll(judul, subJudul, scrollPane);
        setAlignment(Pos.TOP_LEFT);
    }

    public void aturSesi(List<SesiBelajar> sesiList) {
        wadahDaftar.getChildren().clear();

        if (sesiList == null || sesiList.isEmpty()) {
            wadahDaftar.getChildren().add(labelKosong);
            return;
        }

        for (SesiBelajar sesi : sesiList) {
            wadahDaftar.getChildren().add(buatItemTugas(sesi));
        }
    }

    private VBox buatItemTugas(SesiBelajar sesi) {
        VBox kartu = new VBox(3);
        kartu.getStyleClass().add("upcoming-task");

        Label labelJudul = new Label(sesi.getNamaTopik());
        labelJudul.getStyleClass().add("upcoming-task-title");

        Label labelMataKuliah = new Label(sesi.getNamaMataKuliah());
        labelMataKuliah.getStyleClass().add("upcoming-task-course");

        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label labelTipe = new Label(
                dapatkanLabelTipeSesi(sesi.getTipeSesi()));
        labelTipe
                .getStyleClass()
                .addAll(
                        "task-type",
                        "badge-" + sesi.getTipeSesi().toLowerCase());

        Label labelTanggal = new Label(formatTanggalRelatif(sesi.getTanggalJadwal()));
        labelTanggal.getStyleClass().add("upcoming-task-date");

        footer.getChildren().addAll(labelTipe, labelTanggal);

        kartu.getChildren().addAll(labelJudul, labelMataKuliah, footer);
        return kartu;
    }

    private String formatTanggalRelatif(LocalDate tanggal) {
        if (tanggal == null) {
            return "Jadwal tidak diketahui";
        }

        LocalDate hariIni = LocalDate.now();
        if (tanggal.equals(hariIni)) {
            return "Hari ini";
        }
        if (tanggal.equals(hariIni.plusDays(1))) {
            return "Besok";
        }
        if (tanggal.isBefore(hariIni.plusDays(7))) {
            return tanggal
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, ID_LOCALE);
        }
        return DATE_FORMATTER.format(tanggal);
    }

    private String dapatkanLabelTipeSesi(String tipe) {
        if (tipe == null)
            return "-";

        return switch (tipe) {
            case "INITIAL_STUDY" -> "Belajar Baru";
            case "REVIEW" -> "Review";
            case "PRACTICE" -> "Latihan";
            default -> tipe;
        };
    }
}
