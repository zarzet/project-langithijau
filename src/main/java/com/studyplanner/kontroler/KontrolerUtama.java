package com.studyplanner.kontroler;

import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.UtilUI;
import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.algoritma.PembuatJadwal;
import com.studyplanner.algoritma.PengulanganBerjarak;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.tampilan.JamAnalog;
import com.studyplanner.tampilan.WidgetRuntutanBelajar;
import com.studyplanner.tampilan.WidgetTugasMendatang;
import com.studyplanner.tampilan.WidgetUlasanBerikutnya;
import com.studyplanner.tampilan.WidgetWaktuBelajarHariIni;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.layanan.LayananMataKuliah;
import com.studyplanner.layanan.LayananTopik;
import com.studyplanner.layanan.LayananJadwalUjian;
import com.studyplanner.layanan.LayananSesiBelajar;
import com.studyplanner.model.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class KontrolerUtama implements Initializable {

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
    private VBox sidebar;

    @FXML
    private HBox welcomeSection;

    @FXML
    private GridPane statsGrid;

    @FXML
    private VBox activitySection;

    @FXML
    private GridPane mainContentGrid;

    @FXML
    private Button manageCourseBtn;

    @FXML
    private Button viewScheduleBtn;

    @FXML
    private Button generateScheduleBtn;

    @FXML
    private Button themeToggleBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button toggleSidebarBtn;

    @FXML
    private Button headerSettingsBtn;

    @FXML
    private Button headerProfileBtn;

    @FXML
    private VBox streakContainer;

    @FXML
    private VBox studyTimeContainer;

    @FXML
    private VBox nextReviewContainer;

    @FXML
    private VBox clockContainer;

    @FXML
    private VBox upcomingTasksWidgetContainer;

    private ManajerBasisData manajerBasisData;
    private LayananMataKuliah layananMataKuliah;
    private LayananTopik layananTopik;
    private LayananJadwalUjian layananJadwalUjian;
    private LayananSesiBelajar layananSesiBelajar;
    private boolean isDarkMode = false;
    private PembuatJadwal pembuatJadwal;
    private WidgetRuntutanBelajar streakWidget;
    private WidgetWaktuBelajarHariIni studyTimeWidget;
    private WidgetUlasanBerikutnya nextReviewWidget;
    private JamAnalog analogClock;
    private WidgetTugasMendatang upcomingTasksWidget;
    private Timeline autoRefreshTimeline;
    private boolean isSidebarVisible = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();

        layananMataKuliah = new LayananMataKuliah(manajerBasisData);
        layananTopik = new LayananTopik(manajerBasisData);
        layananJadwalUjian = new LayananJadwalUjian(manajerBasisData);
        layananSesiBelajar = new LayananSesiBelajar(manajerBasisData);

        pembuatJadwal = new PembuatJadwal(manajerBasisData);

        siapkanUI();
        loadDashboardData();
        siapkanPembaruanOtomatis();
    }

    private void siapkanUI() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy", Locale.of("id", "ID"));
        dateLabel.setText(LocalDate.now().format(formatter));

        terapkanAnimasiMasuk();

        if (streakContainer != null) {
            streakWidget = new WidgetRuntutanBelajar();
            streakContainer.getChildren().clear();
            streakContainer.getChildren().add(streakWidget);
        }

        if (studyTimeContainer != null) {
            studyTimeWidget = new WidgetWaktuBelajarHariIni();
            studyTimeContainer.getChildren().clear();
            studyTimeContainer.getChildren().add(studyTimeWidget);
        }

        if (nextReviewContainer != null) {
            nextReviewWidget = new WidgetUlasanBerikutnya();
            nextReviewContainer.getChildren().clear();
            nextReviewContainer.getChildren().add(nextReviewWidget);
        }

        if (clockContainer != null) {
            analogClock = new JamAnalog(140);

            VBox clockBox = new VBox();
            clockBox.setAlignment(javafx.geometry.Pos.CENTER);
            clockBox.getStyleClass().add("clock-container");
            clockBox.setPrefSize(180, 180);
            clockBox.setMinSize(180, 180);
            clockBox.setMaxSize(180, 180);
            clockBox.getChildren().add(analogClock);

            clockContainer.getChildren().clear();
            clockContainer.getChildren().add(clockBox);
        }

        if (upcomingTasksWidgetContainer != null) {
            upcomingTasksWidget = new WidgetTugasMendatang();
            terapkanUkuranWidgetKompak(upcomingTasksWidget);
            upcomingTasksWidget.setOnMouseClicked(_ -> showUpcomingTasksDetailDialog());
            upcomingTasksWidgetContainer
                    .getChildren()
                    .setAll(upcomingTasksWidget);
        }

        manageCourseBtn.setGraphic(PembuatIkon.ikonMataKuliah());
        manageCourseBtn.setGraphicTextGap(8);
        manageCourseBtn.setOnAction(_ -> bukaManajemenMataKuliah());

        viewScheduleBtn.setGraphic(PembuatIkon.ikonJadwal());
        viewScheduleBtn.setGraphicTextGap(8);
        viewScheduleBtn.setOnAction(_ -> bukaLihatJadwal());

        generateScheduleBtn.setGraphic(PembuatIkon.ikonBuatJadwal());
        generateScheduleBtn.setGraphicTextGap(8);
        generateScheduleBtn.setOnAction(_ -> buatJadwalBaru());

        if (themeToggleBtn != null) {
            themeToggleBtn.setGraphic(PembuatIkon.ikonModeGelap(isDarkMode));
            themeToggleBtn.setText(isDarkMode ? "Terang" : "Gelap");
            themeToggleBtn.setGraphicTextGap(6);
            themeToggleBtn.setOnAction(_ -> alihkanModaGelap());
        }
        
        if (ManajerOtentikasi.getInstance().isLoggedIn()) {
            Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
            welcomeLabel.setText("Selamat Datang, " + user.getGivenName() + "!");
        }
        
        if (logoutBtn != null) {
            logoutBtn.setGraphic(PembuatIkon.ikonKeluar());
            logoutBtn.setGraphicTextGap(8);
            logoutBtn.setOnAction(_ -> keluar());
        }

        if (toggleSidebarBtn != null) {
            toggleSidebarBtn.setGraphic(PembuatIkon.ikonMenu());
            toggleSidebarBtn.setText("");
            toggleSidebarBtn.setOnAction(_ -> toggleSidebar());
        }

        if (headerSettingsBtn != null) {
            headerSettingsBtn.setGraphic(PembuatIkon.ikonPengaturan());
            headerSettingsBtn.setText("");
            headerSettingsBtn.setOnAction(_ -> tampilkanPengaturan());
        }

        if (headerProfileBtn != null) {
            if (ManajerOtentikasi.getInstance().isLoggedIn()) {
                Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
                aturFotoProfilHeader(user);
            } else {
                headerProfileBtn.setGraphic(PembuatIkon.ikonProfil());
            }
            headerProfileBtn.setText("");
            headerProfileBtn.setOnAction(_ -> tampilkanProfil());
        }
    }

    private void terapkanUkuranWidgetKompak(Region region) {
        if (region != null) {
            region.setPrefSize(180, 180);
            region.setMinSize(180, 180);
            region.setMaxSize(180, 180);
        }
    }

    private void siapkanPembaruanOtomatis() {
        autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), _ -> loadDashboardData()));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void alihkanModaGelap() {
        isDarkMode = !isDarkMode;

        if (themeToggleBtn != null) {
            themeToggleBtn.setGraphic(PembuatIkon.ikonModeGelap(isDarkMode));
            themeToggleBtn.setText(isDarkMode ? "Terang" : "Gelap");
        }

        if (analogClock != null) {
            analogClock.aturModeGelap(isDarkMode);
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

            javafx.stage.Stage stage = (javafx.stage.Stage) manageCourseBtn.getScene().getWindow();
            if (stage != null) {
                DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
            }
        }
    }

    private void loadDashboardData() {
        try {
            PembuatJadwal.KemajuanBelajar progress = pembuatJadwal.ambilKemajuanBelajar();

            totalTopicsLabel.setText(String.valueOf(progress.getTotalTopik()));
            masteredTopicsLabel.setText(
                    String.valueOf(progress.getTopikDikuasai()));
            todayTasksLabel.setText(String.valueOf(progress.getTotalHariIni()));
            completedTasksLabel.setText(
                    String.valueOf(progress.getSelesaiHariIni()));

            overallProgressBar.setProgress(
                    progress.getKemajuanKeseluruhan() / 100.0);
            todayProgressBar.setProgress(progress.getKemajuanHariIni() / 100.0);

            overallProgressLabel.setText(
                    String.format("%.0f%%", progress.getKemajuanKeseluruhan()));
            todayProgressLabel.setText(
                    String.format("%.0f%%", progress.getKemajuanHariIni()));

            loadTodayTasks();
            loadUpcomingExams();

            refreshUpcomingTasksWidget();
            if (streakWidget != null) {
                streakWidget.segarkan();
            }
            if (studyTimeWidget != null) {
                studyTimeWidget.segarkan();
            }
            if (nextReviewWidget != null) {
                nextReviewWidget.segarkan();
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat data dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshUpcomingTasksWidget() throws SQLException {
        if (upcomingTasksWidget == null) {
            return;
        }

        List<SesiBelajar> upcomingSessions = layananSesiBelajar.ambilSesiMendatang(4);
        upcomingTasksWidget.aturSesi(upcomingSessions);
    }

    private void showUpcomingTasksDetailDialog() {
        if (upcomingTasksWidget == null) {
            return;
        }

        try {
            List<SesiBelajar> sessions = layananSesiBelajar.ambilSesiMendatang(12);

            Dialog<Void> dialog = PembuatDialogMD3.buatDialog("Detail Upcoming Tasks", null);
            dialog
                    .getDialogPane()
                    .getButtonTypes()
                    .add(PembuatDialogMD3.buatTombolTutup());
            dialog
                    .getDialogPane()
                    .getStylesheets()
                    .add(getClass().getResource("/css/style.css").toExternalForm());

            WidgetTugasMendatang detailWidget = new WidgetTugasMendatang();
            detailWidget.getStyleClass().removeAll("widget-interactive");
            detailWidget.setSpacing(12);
            detailWidget.setPrefWidth(420);
            detailWidget.setMaxWidth(Double.MAX_VALUE);
            detailWidget.aturSesi(sessions);

            ScrollPane scrollPane = new ScrollPane(detailWidget);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefViewportHeight(360);
            scrollPane.setStyle("-fx-background-color: transparent;");

            VBox content = new VBox(12);
            content.setPadding(new Insets(20));
            Label title = new Label("Prioritas 7 Hari Ke Depan");
            title.getStyleClass().add("section-title");
            content.getChildren().addAll(title, scrollPane);

            dialog.getDialogPane().setContent(content);
            dialog.showAndWait();
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat detail upcoming tasks: " + e.getMessage());
        }
    }

    private void loadTodayTasks() throws SQLException {
        todayTasksContainer.getChildren().clear();
        List<SesiBelajar> sessions = layananSesiBelajar.ambilSesiHariIni();

        if (sessions.isEmpty()) {
            Label emptyLabel = new Label(
                    "Belum ada tugas untuk hari ini. Klik 'Buat Jadwal' untuk membuat jadwal otomatis.");
            emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
            todayTasksContainer.getChildren().add(emptyLabel);
        } else {
            for (SesiBelajar session : sessions) {
                todayTasksContainer.getChildren().add(createTaskCard(session));
            }
        }
    }

    private VBox createTaskCard(SesiBelajar session) {
        VBox card = new VBox(8);
        card.getStyleClass().add("task-card");

        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(session.isSelesai());
        checkBox.setOnAction(_ -> markTaskComplete(session, checkBox.isSelected()));

        Label titleLabel = new Label(session.getNamaTopik());
        titleLabel.getStyleClass().add("task-title");
        if (session.isSelesai()) {
            titleLabel.setStyle(
                    "-fx-text-fill: #888; -fx-strikethrough: true;");
        }

        header.getChildren().addAll(checkBox, titleLabel);

        Label courseLabel = new Label(session.getNamaMataKuliah());
        courseLabel.getStyleClass().add("task-course");

        Label typeLabel = new Label(
                UtilUI.dapatkanLabelTipeSesi(session.getTipeSesi()));
        typeLabel.getStyleClass().add("task-type");
        typeLabel
                .getStyleClass()
                .add(UtilUI.dapatkanKelasBadge(session.getTipeSesi()));

        Label durationLabel = new Label(
                session.getDurasiMenit() + " menit");
        durationLabel.getStyleClass().add("task-duration");

        HBox footer = new HBox(15);
        footer.getChildren().addAll(typeLabel, durationLabel);

        card.getChildren().addAll(header, courseLabel, footer);

        return card;
    }

    private void markTaskComplete(SesiBelajar session, boolean completed) {
        try {
            session.setSelesai(completed);
            if (completed) {
                session.setSelesaiPada(java.time.LocalDateTime.now());
                showPerformanceRatingDialog(session);
            } else {
                session.setSelesaiPada(null);
                session.setRatingPerforma(0);
                layananSesiBelajar.perbarui(session);
            }

            loadDashboardData();
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memperbarui tugas: " + e.getMessage());
        }
    }

    private void showPerformanceRatingDialog(SesiBelajar session) {
        Dialog<Integer> dialog = PembuatDialogMD3.buatDialog("Rating Performa", "Bagaimana performa Anda untuk sesi ini?");

        ButtonType okButtonType = PembuatDialogMD3.buatTombolOK();
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
            if (i == 3)
                rb.setSelected(true);
            ratingBox.getChildren().add(rb);
        }

        content.getChildren().addAll(label, ratingBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selected = (RadioButton) ratingGroup.getSelectedToggle();
                return (Integer) selected.getUserData();
            }
            return null;
        });

        dialog
                .showAndWait()
                .ifPresent(rating -> {
                    try {
                        session.setRatingPerforma(rating);
                        layananSesiBelajar.perbarui(session);

                        Topik topic = layananTopik.ambilBerdasarkanId(session.getIdTopik());
                        if (topic != null) {
                            if (topic.getTanggalBelajarPertama() == null) {
                                topic.setTanggalBelajarPertama(LocalDate.now());
                            }

                            LocalDate nextReview = PengulanganBerjarak.hitungTanggalUlasanBerikutnya(
                                    topic,
                                    rating);

                            layananTopik.perbarui(topic);

                            UtilUI.tampilkanInfo(
                                    "Sesi berhasil diselesaikan!\nReview berikutnya: " +
                                            nextReview);
                        }
                    } catch (SQLException e) {
                        UtilUI.tampilkanKesalahan("Gagal menyimpan rating: " + e.getMessage());
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
        List<JadwalUjian> exams = layananJadwalUjian.ambilUjianMendatang();

        if (exams.isEmpty()) {
            Label emptyLabel = new Label("Belum ada ujian yang dijadwalkan.");
            emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
            upcomingExamsContainer.getChildren().add(emptyLabel);
        } else {
            for (JadwalUjian exam : exams) {
                upcomingExamsContainer.getChildren().add(createExamCard(exam));
            }
        }
    }

    private VBox createExamCard(JadwalUjian exam) throws SQLException {
        VBox card = new VBox(5);
        card.getStyleClass().add("exam-card");

        Label titleLabel = new Label(exam.getJudul());
        titleLabel.getStyleClass().add("exam-title");

        MataKuliah course = layananMataKuliah.ambilBerdasarkanId(exam.getIdMataKuliah());
        Label courseLabel = new Label(course != null ? course.getKode() : "");
        courseLabel.getStyleClass().add("exam-course");

        Label dateLabel = new Label(exam.getTanggalUjian().toString());
        dateLabel.getStyleClass().add("exam-date");

        int daysLeft = exam.getHariMenujuUjian();
        Label daysLabel = new Label(daysLeft + " hari lagi");
        daysLabel.getStyleClass().add("exam-days");

        card
                .getChildren()
                .addAll(titleLabel, courseLabel, dateLabel, daysLabel);

        return card;
    }

    private void bukaManajemenMataKuliah() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ManajemenMataKuliah.fxml"));
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

            DekoratorJendelaKustom.dekorasi(
                    stage,
                    "Manajemen Mata Kuliah & Topik",
                    isDarkMode);

            KontrolerManajemenMataKuliah controller = loader.getController();
            controller.aturKontrolerUtama(this);

            stage.showAndWait();

            loadDashboardData();
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka manajemen mata kuliah: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bukaLihatJadwal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ScheduleView.fxml"));
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

            DekoratorJendelaKustom.dekorasi(stage, "Jadwal Belajar", isDarkMode);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka tampilan jadwal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buatJadwalBaru() {
        try {
            pembuatJadwal.buatDanSimpanJadwal(7);
            UtilUI.tampilkanInfo(
                    "Jadwal belajar berhasil di-generate untuk 7 hari ke depan!");
            loadDashboardData();
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal membuat jadwal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tampilkanProfil() {
        if (!ManajerOtentikasi.getInstance().isLoggedIn()) {
            return;
        }
        
        Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();

        Dialog<Void> dialog = PembuatDialogMD3.buatDialog("Profil Pengguna", "Informasi Profil Anda");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-min-width: 400px;");
        
        Label nameLabel = new Label("Nama: " + user.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label emailLabel = new Label("Email: " + (user.getEmail() != null ? user.getEmail() : "Tidak tersedia"));
        emailLabel.setStyle("-fx-font-size: 13px;");
        
        Label idLabel = new Label("ID: " + user.getId());
        idLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        
        content.getChildren().addAll(nameLabel, emailLabel, idLabel);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }

    private void tampilkanPengaturan() {
        try {
            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            VBox root = new VBox();
            root.getStyleClass().add("settings-window");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            VBox settingsContent = new VBox(32);
            settingsContent.setPadding(new Insets(24));

        VBox appearanceSection = createSettingsSection("Tampilan", PembuatIkon.ikonTampilan());

        HBox darkModeRow = createSettingRow(
            "Mode Gelap",
            "Ubah tema aplikasi menjadi gelap untuk kenyamanan mata di malam hari"
        );
        CheckBox darkModeCheck = new CheckBox();
        darkModeCheck.setSelected(isDarkMode);
        darkModeCheck.setOnAction(_ -> {
            alihkanModaGelap();
            if (isDarkMode) {
                root.getStyleClass().add("dark-mode");
            } else {
                root.getStyleClass().remove("dark-mode");
            }
            DekoratorJendelaKustom.dekorasi(stage, "Pengaturan", isDarkMode);
        });
        darkModeRow.getChildren().add(darkModeCheck);

        appearanceSection.getChildren().add(darkModeRow);

        VBox studySection = createSettingsSection("Pembelajaran", PembuatIkon.ikonPembelajaran());

        HBox durationRow = createSettingRow(
            "Durasi Belajar Default",
            "Durasi standar untuk sesi belajar baru (dalam menit)"
        );
        ComboBox<String> durationCombo = new ComboBox<>();
        durationCombo.getItems().addAll("30 menit", "45 menit", "60 menit", "90 menit", "120 menit");
        durationCombo.setValue("60 menit");
        durationCombo.setStyle("-fx-pref-width: 140px;");
        durationRow.getChildren().add(durationCombo);

        HBox reminderRow = createSettingRow(
            "Pengingat Belajar",
            "Tampilkan notifikasi untuk mengingatkan jadwal belajar"
        );
        CheckBox reminderCheck = new CheckBox();
        reminderCheck.setSelected(true);
        reminderRow.getChildren().add(reminderCheck);

        studySection.getChildren().addAll(durationRow, reminderRow);

        VBox dataSection = createSettingsSection("Data & Backup", PembuatIkon.ikonBackup());

        HBox backupRow = createSettingRow(
            "Backup Otomatis",
            "Backup database secara otomatis setiap hari"
        );
        CheckBox backupCheck = new CheckBox();
        backupCheck.setSelected(false);
        backupRow.getChildren().add(backupCheck);

        HBox exportRow = createSettingRow(
            "Ekspor Data",
            "Ekspor semua data Anda ke file JSON"
        );
        Button exportBtn = new Button("Ekspor");
        exportBtn.getStyleClass().add("btn-secondary");
        exportBtn.setStyle("-fx-pref-width: 100px;");
        exportRow.getChildren().add(exportBtn);

        dataSection.getChildren().addAll(backupRow, exportRow);

        VBox aboutSection = createSettingsSection("Tentang", PembuatIkon.ikonTentang());

        VBox aboutContent = new VBox(8);
        aboutContent.getStyleClass().add("settings-about-box");

        Label appName = new Label("Perencana Belajar Adaptif");
        appName.getStyleClass().add("settings-about-title");

        Label version = new Label("Versi 1.0.0");
        version.getStyleClass().add("settings-about-version");

        Label description = new Label("Aplikasi manajemen pembelajaran dengan sistem spaced repetition");
        description.setWrapText(true);
        description.getStyleClass().add("settings-about-description");

        HBox copyrightBox = new HBox(6);
        copyrightBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        copyrightBox.getChildren().add(PembuatIkon.ikonCopyright());
        Label copyright = new Label("2025 - Dibuat dengan JavaFX 25");
        copyright.getStyleClass().add("settings-about-copyright");
        copyrightBox.getChildren().add(copyright);

        aboutContent.getChildren().addAll(appName, version, description, copyrightBox);
        aboutSection.getChildren().add(aboutContent);

        settingsContent.getChildren().addAll(
            appearanceSection,
            studySection,
            dataSection,
            aboutSection
        );

            scrollPane.setContent(settingsContent);
            root.getChildren().add(scrollPane);
            VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

            Scene scene = new Scene(root, 700, 600);
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
            );

            if (isDarkMode) {
                root.getStyleClass().add("dark-mode");
            }

            stage.setScene(scene);

            DekoratorJendelaKustom.dekorasi(stage, "Pengaturan", isDarkMode);

            stage.showAndWait();
        } catch (Exception e) {
            UtilUI.tampilkanKesalahan("Gagal membuka pengaturan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createSettingsSection(String title, Node icon) {
        VBox section = new VBox(16);

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 8, 0));

        if (icon != null) {
            titleBox.getChildren().add(icon);
        }

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("settings-section-title");

        titleBox.getChildren().add(titleLabel);
        section.getChildren().add(titleBox);
        return section;
    }

    private HBox createSettingRow(String title, String description) {
        HBox row = new HBox(16);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getStyleClass().add("settings-row");

        VBox textBox = new VBox(4);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("settings-row-title");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.getStyleClass().add("settings-row-description");

        textBox.getChildren().addAll(titleLabel, descLabel);
        row.getChildren().add(textBox);

        return row;
    }

    private void keluar() {
        Alert confirmation = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Keluar",
                "Apakah Anda yakin ingin keluar?",
                "Anda akan logout dari aplikasi.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ManajerOtentikasi.getInstance().logout();
                    
                    Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
                    Parent root = loader.load();
                    
                    Scene scene = new Scene(root, 1000, 700);
                    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                    
                    stage.setScene(scene);
                    DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", false);
                } catch (Exception e) {
                    UtilUI.tampilkanKesalahan("Gagal logout: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public ManajerBasisData getManajerBasisData() {
        return manajerBasisData;
    }

    private String ambilInisial(String namaLengkap) {
        if (namaLengkap == null || namaLengkap.isEmpty()) {
            return "?";
        }
        String[] parts = namaLengkap.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    private void aturFotoProfilHeader(Userinfo user) {
        try {
            String photoUrl = user.getPicture();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(44);
                imageView.setFitHeight(44);
                imageView.setPreserveRatio(true);

                Circle clip = new Circle(22, 22, 22);
                imageView.setClip(clip);

                Image image = new Image(photoUrl, true);
                imageView.setImage(image);

                headerProfileBtn.setGraphic(imageView);
            } else {
                Label inisial = new Label(ambilInisial(user.getName()));
                inisial.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 18px;"
                );
                headerProfileBtn.setGraphic(inisial);
            }
        } catch (Exception e) {
            headerProfileBtn.setGraphic(PembuatIkon.ikonProfil());
            e.printStackTrace();
        }
    }

    private void terapkanAnimasiMasuk() {
        if (sidebar != null) {
            sidebar.setOpacity(0);
            sidebar.setTranslateX(-50);

            FadeTransition fadeSidebar = new FadeTransition(Duration.millis(500), sidebar);
            fadeSidebar.setFromValue(0.0);
            fadeSidebar.setToValue(1.0);

            TranslateTransition slideSidebar = new TranslateTransition(Duration.millis(500), sidebar);
            slideSidebar.setFromX(-50);
            slideSidebar.setToX(0);

            ParallelTransition sidebarAnim = new ParallelTransition(fadeSidebar, slideSidebar);
            sidebarAnim.play();
        }

        if (welcomeLabel != null && welcomeLabel.getParent() != null) {
            FadeTransition fadeWelcome = new FadeTransition(Duration.millis(600), welcomeLabel.getParent());
            fadeWelcome.setFromValue(0.0);
            fadeWelcome.setToValue(1.0);
            fadeWelcome.setDelay(Duration.millis(100));
            fadeWelcome.play();
        }

        if (statsGrid != null) {
            statsGrid.setOpacity(0);
            statsGrid.setTranslateY(30);

            FadeTransition fadeStats = new FadeTransition(Duration.millis(600), statsGrid);
            fadeStats.setFromValue(0.0);
            fadeStats.setToValue(1.0);

            TranslateTransition slideStats = new TranslateTransition(Duration.millis(600), statsGrid);
            slideStats.setFromY(30);
            slideStats.setToY(0);

            ParallelTransition statsAnim = new ParallelTransition(fadeStats, slideStats);
            statsAnim.setDelay(Duration.millis(200));
            statsAnim.play();
        }

        if (activitySection != null) {
            activitySection.setOpacity(0);
            FadeTransition fadeActivity = new FadeTransition(Duration.millis(600), activitySection);
            fadeActivity.setFromValue(0.0);
            fadeActivity.setToValue(1.0);
            fadeActivity.setDelay(Duration.millis(400));
            fadeActivity.play();
        }

        if (mainContentGrid != null) {
            mainContentGrid.setOpacity(0);
            mainContentGrid.setTranslateY(30);

            FadeTransition fadeContent = new FadeTransition(Duration.millis(600), mainContentGrid);
            fadeContent.setFromValue(0.0);
            fadeContent.setToValue(1.0);

            TranslateTransition slideContent = new TranslateTransition(Duration.millis(600), mainContentGrid);
            slideContent.setFromY(30);
            slideContent.setToY(0);

            ParallelTransition contentAnim = new ParallelTransition(fadeContent, slideContent);
            contentAnim.setDelay(Duration.millis(500));
            contentAnim.play();
        }

        terapkanAnimasiHoverSidebarButton(manageCourseBtn);
        terapkanAnimasiHoverSidebarButton(viewScheduleBtn);
        terapkanAnimasiHoverSidebarButton(generateScheduleBtn);
    }

    private void terapkanAnimasiHoverSidebarButton(Button button) {
        if (button == null) return;

        button.setOnMouseEntered(event -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
            scaleUp.setToX(1.02);
            scaleUp.setToY(1.02);

            TranslateTransition slideRight = new TranslateTransition(Duration.millis(150), button);
            slideRight.setToX(4);

            ParallelTransition hoverIn = new ParallelTransition(scaleUp, slideRight);
            hoverIn.play();
        });

        button.setOnMouseExited(event -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            TranslateTransition slideBack = new TranslateTransition(Duration.millis(150), button);
            slideBack.setToX(0);

            ParallelTransition hoverOut = new ParallelTransition(scaleDown, slideBack);
            hoverOut.play();
        });
    }

    private void toggleSidebar() {
        if (sidebar == null) return;

        isSidebarVisible = !isSidebarVisible;

        if (isSidebarVisible) {
            sidebar.setManaged(true);
            sidebar.setVisible(true);
            sidebar.setTranslateX(-240);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), sidebar);
            slideIn.setFromX(-240);
            slideIn.setToX(0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), sidebar);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);
            showAnim.play();
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), sidebar);
            slideOut.setFromX(0);
            slideOut.setToX(-240);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), sidebar);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);
            hideAnim.setOnFinished(_ -> {
                sidebar.setManaged(false);
                sidebar.setVisible(false);
            });
            hideAnim.play();
        }
    }
}
