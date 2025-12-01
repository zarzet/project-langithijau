package com.studyplanner.kontroler.pembantu;

import com.studyplanner.model.KonfigurasiWidget;
import com.studyplanner.tampilan.DialogPemilihWidget;
import com.studyplanner.tampilan.JamAnalog;
import com.studyplanner.tampilan.WadahWidgetDraggable;
import com.studyplanner.tampilan.WidgetRuntutanBelajar;
import com.studyplanner.tampilan.WidgetTugasMendatang;
import com.studyplanner.tampilan.WidgetUlasanBerikutnya;
import com.studyplanner.tampilan.WidgetWaktuBelajarHariIni;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PreferensiPengguna;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Mengelola sistem widget di dashboard.
 * Termasuk pembuatan, konfigurasi, dan persistensi widget.
 */
public class ManajerWidgetDashboard {

    private final HBox wadahWidgetKontainer;
    private boolean isDarkMode;
    private final Runnable onUpcomingTasksClicked;
    
    private WadahWidgetDraggable wadahWidget;
    private KonfigurasiWidget konfigurasiWidget;
    
    // Widget instances
    private WidgetRuntutanBelajar streakWidget;
    private WidgetWaktuBelajarHariIni studyTimeWidget;
    private WidgetUlasanBerikutnya nextReviewWidget;
    private WidgetTugasMendatang upcomingTasksWidget;
    private JamAnalog analogClock;

    public ManajerWidgetDashboard(
            HBox wadahWidgetKontainer, 
            boolean isDarkMode,
            Runnable onUpcomingTasksClicked) {
        this.wadahWidgetKontainer = wadahWidgetKontainer;
        this.isDarkMode = isDarkMode;
        this.onUpcomingTasksClicked = onUpcomingTasksClicked;
    }

    /**
     * Inisialisasi sistem widget.
     */
    public void inisialisasi() {
        if (wadahWidgetKontainer == null) return;

        int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
        
        // Muat konfigurasi widget dari preferensi
        String configStr = PreferensiPengguna.getInstance().getWidgetConfig(userId);
        if (configStr.isEmpty()) {
            konfigurasiWidget = new KonfigurasiWidget();
        } else {
            konfigurasiWidget = KonfigurasiWidget.dariString(configStr);
        }

        // Buat wadah widget dengan drag & drop
        wadahWidget = new WadahWidgetDraggable(
            konfigurasiWidget,
            this::buatWidgetDariJenis,
            this::simpanKonfigurasi
        );

        // Handler untuk buka dialog pemilih widget
        wadahWidget.setOnTambahWidgetClicked(_ -> tampilkanDialogPemilihWidget());

        wadahWidgetKontainer.getChildren().clear();
        wadahWidgetKontainer.getChildren().add(wadahWidget);
    }

    /**
     * Factory method untuk membuat widget berdasarkan jenisnya.
     */
    private Node buatWidgetDariJenis(KonfigurasiWidget.JenisWidget jenis) {
        return switch (jenis) {
            case RUNTUTAN_BELAJAR -> {
                streakWidget = new WidgetRuntutanBelajar();
                yield streakWidget;
            }
            case JAM_ANALOG -> {
                analogClock = new JamAnalog(140);
                VBox clockBox = new VBox();
                clockBox.setAlignment(Pos.CENTER);
                clockBox.getStyleClass().add("clock-container");
                clockBox.setPrefSize(180, 180);
                clockBox.setMinSize(180, 180);
                clockBox.setMaxSize(180, 180);
                clockBox.getChildren().add(analogClock);
                if (isDarkMode && analogClock != null) {
                    analogClock.aturModeGelap(true);
                }
                yield clockBox;
            }
            case WAKTU_BELAJAR -> {
                studyTimeWidget = new WidgetWaktuBelajarHariIni();
                yield studyTimeWidget;
            }
            case ULASAN_BERIKUTNYA -> {
                nextReviewWidget = new WidgetUlasanBerikutnya();
                yield nextReviewWidget;
            }
            case TUGAS_MENDATANG -> {
                upcomingTasksWidget = new WidgetTugasMendatang();
                terapkanUkuranKompak(upcomingTasksWidget);
                upcomingTasksWidget.setOnMouseClicked(_ -> {
                    if (onUpcomingTasksClicked != null) {
                        onUpcomingTasksClicked.run();
                    }
                });
                yield upcomingTasksWidget;
            }
        };
    }

    /**
     * Terapkan ukuran kompak untuk widget.
     */
    private void terapkanUkuranKompak(Node widget) {
        if (widget instanceof VBox vbox) {
            vbox.setPrefWidth(200);
            vbox.setMinWidth(180);
            vbox.setMaxWidth(220);
            vbox.setPrefHeight(180);
            vbox.setMinHeight(160);
            vbox.setMaxHeight(200);
        }
    }

    /**
     * Simpan konfigurasi widget ke preferensi.
     */
    private void simpanKonfigurasi(KonfigurasiWidget config) {
        int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
        PreferensiPengguna.getInstance().setWidgetConfig(userId, config.keString());
        konfigurasiWidget = config;
    }

    /**
     * Tampilkan dialog untuk memilih widget.
     */
    private void tampilkanDialogPemilihWidget() {
        Stage stage = (Stage) wadahWidgetKontainer.getScene().getWindow();
        DialogPemilihWidget dialog = new DialogPemilihWidget(
            stage,
            konfigurasiWidget,
            isDarkMode,
            configBaru -> {
                simpanKonfigurasi(configBaru);
                wadahWidget.refreshWidgets();
            }
        );
        dialog.tampilkan();
    }

    /**
     * Segarkan semua widget.
     */
    public void segarkanSemua() {
        if (streakWidget != null) {
            streakWidget.segarkan();
        }
        if (studyTimeWidget != null) {
            studyTimeWidget.segarkan();
        }
        if (nextReviewWidget != null) {
            nextReviewWidget.segarkan();
        }
        // WidgetTugasMendatang diatur via aturSesi() dari luar
    }

    /**
     * Atur mode gelap untuk widget yang mendukung.
     */
    public void aturModeGelap(boolean gelap) {
        if (analogClock != null) {
            analogClock.aturModeGelap(gelap);
        }
    }

    /**
     * Update dark mode status.
     */
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    // Getters untuk widget instances
    public WidgetRuntutanBelajar getStreakWidget() { return streakWidget; }
    public WidgetWaktuBelajarHariIni getStudyTimeWidget() { return studyTimeWidget; }
    public WidgetUlasanBerikutnya getNextReviewWidget() { return nextReviewWidget; }
    public WidgetTugasMendatang getUpcomingTasksWidget() { return upcomingTasksWidget; }
    public JamAnalog getAnalogClock() { return analogClock; }
}
