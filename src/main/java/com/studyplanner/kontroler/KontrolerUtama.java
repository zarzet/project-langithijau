package com.studyplanner.kontroler;

import com.studyplanner.utilitas.ManajerOtentikasi;
import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.algoritma.PembuatJadwal;
import com.studyplanner.algoritma.PengulanganBerjarak;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.tampilan.JamAnalog;
import com.studyplanner.tampilan.WidgetPelacakPencapaian;
import com.studyplanner.tampilan.WidgetRuntutanBelajar;
import com.studyplanner.tampilan.WidgetTugasMendatang;
import com.studyplanner.tampilan.WidgetUlasanBerikutnya;
import com.studyplanner.tampilan.WidgetWaktuBelajarHariIni;
import com.studyplanner.basisdata.ManajerBasisData;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    private Button manageCourseBtn;

    @FXML
    private Button viewScheduleBtn;

    @FXML
    private Button generateScheduleBtn;

    @FXML
    private Button themeToggleBtn;

    @FXML
    private MenuButton userMenuBtn;

    @FXML
    private MenuItem profileMenuItem;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private VBox streakContainer;

    @FXML
    private VBox studyTimeContainer;

    @FXML
    private VBox nextReviewContainer;

    @FXML
    private VBox clockContainer;

    @FXML
    private VBox achievementContainer;

    @FXML
    private VBox upcomingTasksWidgetContainer;

    private ManajerBasisData manajerBasisData;
    private boolean isDarkMode = false;
    private PembuatJadwal pembuatJadwal;
    private WidgetRuntutanBelajar streakWidget;
    private WidgetWaktuBelajarHariIni studyTimeWidget;
    private WidgetUlasanBerikutnya nextReviewWidget;
    private JamAnalog analogClock;
    private WidgetPelacakPencapaian achievementWidget;
    private WidgetTugasMendatang upcomingTasksWidget;
    private Timeline autoRefreshTimeline;
    private AchievementSnapshot latestAchievementData;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();
        pembuatJadwal = new PembuatJadwal(manajerBasisData);

        siapkanUI();
        loadDashboardData();
        siapkanPembaruanOtomatis();
    }

    private void siapkanUI() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy", Locale.of("id", "ID"));
        dateLabel.setText(LocalDate.now().format(formatter));

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

        if (achievementContainer != null) {
            achievementWidget = new WidgetPelacakPencapaian();
            terapkanUkuranWidgetKompak(achievementWidget);
            achievementWidget.setOnMouseClicked(_ -> showAchievementDetailDialog());
            achievementContainer.getChildren().setAll(achievementWidget);
        }

        if (upcomingTasksWidgetContainer != null) {
            upcomingTasksWidget = new WidgetTugasMendatang();
            terapkanUkuranWidgetKompak(upcomingTasksWidget);
            upcomingTasksWidget.setOnMouseClicked(_ -> showUpcomingTasksDetailDialog());
            upcomingTasksWidgetContainer
                    .getChildren()
                    .setAll(upcomingTasksWidget);
        }

        manageCourseBtn.setOnAction(_ -> bukaManajemenMataKuliah());
        viewScheduleBtn.setOnAction(_ -> bukaLihatJadwal());
        generateScheduleBtn.setOnAction(_ -> buatJadwalBaru());

        if (themeToggleBtn != null) {
            themeToggleBtn.setText(isDarkMode ? "☼" : "◐");
            themeToggleBtn.setOnAction(_ -> alihkanModaGelap());
        }
        
        if (ManajerOtentikasi.getInstance().isLoggedIn()) {
            Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
            welcomeLabel.setText("Selamat Datang, " + user.getGivenName() + "!");
            
            // Setup user menu
            if (userMenuBtn != null) {
                userMenuBtn.setText("👤 " + user.getGivenName());
            }
        }
        
        // Setup menu item handlers
        if (profileMenuItem != null) {
            profileMenuItem.setOnAction(_ -> tampilkanProfil());
        }
        if (settingsMenuItem != null) {
            settingsMenuItem.setOnAction(_ -> tampilkanPengaturan());
        }
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(_ -> keluar());
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
            themeToggleBtn.setText(isDarkMode ? "☼" : "◐");
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

            refreshAchievementWidget(progress);
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
            showError("Gagal memuat data dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshAchievementWidget(
            PembuatJadwal.KemajuanBelajar progress) throws SQLException {
        latestAchievementData = null;

        if (achievementWidget == null) {
            return;
        }

        List<SesiBelajar> todaySessions = manajerBasisData.ambilSesiHariIni();
        int reviewTotal = (int) todaySessions
                .stream()
                .filter(s -> "REVIEW".equalsIgnoreCase(s.getTipeSesi()))
                .count();
        int reviewCompleted = (int) todaySessions
                .stream()
                .filter(
                        s -> s.isSelesai() &&
                                "REVIEW".equalsIgnoreCase(s.getTipeSesi()))
                .count();

        int focusMinutes;
        if (studyTimeWidget != null) {
            focusMinutes = studyTimeWidget.ambilMenitHariIniSaatIni();
        } else {
            focusMinutes = manajerBasisData.ambilWaktuBelajarHariIni();
        }
        int streakDays = manajerBasisData.ambilRuntutanBelajar();

        achievementWidget.perbaruiData(
                progress.getSelesaiHariIni(),
                progress.getTotalHariIni(),
                reviewCompleted,
                reviewTotal,
                focusMinutes,
                streakDays);

        latestAchievementData = new AchievementSnapshot(
                progress.getSelesaiHariIni(),
                progress.getTotalHariIni(),
                reviewCompleted,
                reviewTotal,
                focusMinutes,
                streakDays);
    }

    private void refreshUpcomingTasksWidget() throws SQLException {
        if (upcomingTasksWidget == null) {
            return;
        }

        List<SesiBelajar> upcomingSessions = manajerBasisData.ambilSesiMendatang(4);
        upcomingTasksWidget.aturSesi(upcomingSessions);
    }

    private void showAchievementDetailDialog() {
        if (achievementWidget == null) {
            return;
        }
        if (latestAchievementData == null) {
            showInfo(
                    "Data achievement belum tersedia, coba lagi setelah data dimuat.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detail Achievement Tracker");
        dialog
                .getDialogPane()
                .getButtonTypes()
                .add(new ButtonType("Tutup", ButtonBar.ButtonData.OK_DONE));
        dialog
                .getDialogPane()
                .getStylesheets()
                .add(getClass().getResource("/css/style.css").toExternalForm());

        WidgetPelacakPencapaian detailWidget = new WidgetPelacakPencapaian();
        detailWidget.getStyleClass().removeAll("widget-interactive");
        detailWidget.setPrefWidth(420);
        detailWidget.setMaxWidth(Double.MAX_VALUE);
        detailWidget.perbaruiData(
                latestAchievementData.tasksCompleted,
                latestAchievementData.tasksTotal,
                latestAchievementData.reviewCompleted,
                latestAchievementData.reviewTotal,
                latestAchievementData.focusMinutes,
                latestAchievementData.streakDays);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        Label title = new Label("Ringkasan Pencapaian Hari Ini");
        title.getStyleClass().add("section-title");
        content.getChildren().addAll(title, detailWidget);

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void showUpcomingTasksDetailDialog() {
        if (upcomingTasksWidget == null) {
            return;
        }

        try {
            List<SesiBelajar> sessions = manajerBasisData.ambilSesiMendatang(12);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Detail Upcoming Tasks");
            dialog
                    .getDialogPane()
                    .getButtonTypes()
                    .add(new ButtonType("Tutup", ButtonBar.ButtonData.OK_DONE));
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
            showError("Gagal memuat detail upcoming tasks: " + e.getMessage());
        }
    }

    private void loadTodayTasks() throws SQLException {
        todayTasksContainer.getChildren().clear();
        List<SesiBelajar> sessions = manajerBasisData.ambilSesiHariIni();

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
                getSessionTypeLabel(session.getTipeSesi()));
        typeLabel.getStyleClass().add("task-type");
        typeLabel
                .getStyleClass()
                .add("badge-" + session.getTipeSesi().toLowerCase());

        Label durationLabel = new Label(
                session.getDurasiMenit() + " menit");
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

    private void markTaskComplete(SesiBelajar session, boolean completed) {
        try {
            session.setSelesai(completed);
            if (completed) {
                session.setSelesaiPada(java.time.LocalDateTime.now());
                showPerformanceRatingDialog(session);
            } else {
                session.setSelesaiPada(null);
                session.setRatingPerforma(0);
                manajerBasisData.perbaruiSesiBelajar(session);
            }

            loadDashboardData();
        } catch (SQLException e) {
            showError("Gagal memperbarui tugas: " + e.getMessage());
        }
    }

    private void showPerformanceRatingDialog(SesiBelajar session) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Rating Performa");
        dialog.setHeaderText("Bagaimana performa Anda untuk sesi ini?");

        ButtonType okButtonType = new ButtonType(
                "OK",
                ButtonBar.ButtonData.OK_DONE);
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
                        manajerBasisData.perbaruiSesiBelajar(session);

                        // Update topic based on spaced repetition algorithm
                        Topik topic = manajerBasisData.ambilTopikBerdasarkanId(session.getIdTopik());
                        if (topic != null) {
                            if (topic.getTanggalBelajarPertama() == null) {
                                topic.setTanggalBelajarPertama(LocalDate.now());
                            }

                            LocalDate nextReview = PengulanganBerjarak.hitungTanggalUlasanBerikutnya(
                                    topic,
                                    rating);

                            manajerBasisData.perbaruiTopik(topic);

                            showInfo(
                                    "Sesi berhasil diselesaikan!\nReview berikutnya: " +
                                            nextReview);
                        }
                    } catch (SQLException e) {
                        showError("Gagal menyimpan rating: " + e.getMessage());
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
        List<JadwalUjian> exams = manajerBasisData.ambilUjianMendatang();

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

        MataKuliah course = manajerBasisData.ambilMataKuliahBerdasarkanId(exam.getIdMataKuliah());
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
            showError("Gagal membuka manajemen mata kuliah: " + e.getMessage());
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
            showError("Gagal membuka tampilan jadwal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buatJadwalBaru() {
        try {
            pembuatJadwal.buatDanSimpanJadwal(7);
            showInfo(
                    "Jadwal belajar berhasil di-generate untuk 7 hari ke depan!");
            loadDashboardData();
        } catch (SQLException e) {
            showError("Gagal membuat jadwal: " + e.getMessage());
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

    private void tampilkanProfil() {
        if (!ManajerOtentikasi.getInstance().isLoggedIn()) {
            return;
        }
        
        Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Profil Pengguna");
        dialog.setHeaderText("Informasi Profil Anda");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-min-width: 400px;");
        
        // Info pengguna
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
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Pengaturan");
        dialog.setHeaderText("Pengaturan Aplikasi");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-min-width: 400px;");
        
        Label infoLabel = new Label("Fitur pengaturan akan segera hadir!");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        
        // Dark mode toggle
        HBox darkModeBox = new HBox(10);
        darkModeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label darkModeLabel = new Label("Mode Gelap:");
        darkModeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        CheckBox darkModeCheck = new CheckBox();
        darkModeCheck.setSelected(isDarkMode);
        darkModeCheck.setOnAction(_ -> alihkanModaGelap());
        darkModeBox.getChildren().addAll(darkModeLabel, darkModeCheck);
        
        content.getChildren().addAll(infoLabel, new Separator(), darkModeBox);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }

    private void keluar() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Konfirmasi Keluar");
        confirmation.setHeaderText("Apakah Anda yakin ingin keluar?");
        confirmation.setContentText("Anda akan logout dari aplikasi.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ManajerOtentikasi.getInstance().logout();
                    
                    // Kembali ke login screen
                    Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
                    Parent root = loader.load();
                    
                    Scene scene = new Scene(root, 1000, 700);
                    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                    
                    stage.setScene(scene);
                    DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", false);
                } catch (Exception e) {
                    showError("Gagal logout: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public ManajerBasisData getManajerBasisData() {
        return manajerBasisData;
    }

    private static class AchievementSnapshot {

        final int tasksCompleted;
        final int tasksTotal;
        final int reviewCompleted;
        final int reviewTotal;
        final int focusMinutes;
        final int streakDays;

        AchievementSnapshot(
                int tasksCompleted,
                int tasksTotal,
                int reviewCompleted,
                int reviewTotal,
                int focusMinutes,
                int streakDays) {
            this.tasksCompleted = tasksCompleted;
            this.tasksTotal = tasksTotal;
            this.reviewCompleted = reviewCompleted;
            this.reviewTotal = reviewTotal;
            this.focusMinutes = focusMinutes;
            this.streakDays = streakDays;
        }
    }
}
