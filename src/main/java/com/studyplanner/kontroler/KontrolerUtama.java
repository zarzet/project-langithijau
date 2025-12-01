package com.studyplanner.kontroler;

import com.studyplanner.utilitas.AnimasiUtil;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.PreferensiPengguna;
import com.studyplanner.utilitas.UtilUI;
import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.algoritma.PembuatJadwal;
import com.studyplanner.algoritma.PengulanganBerjarak;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.tampilan.DialogPengenalan;
import com.studyplanner.tampilan.TampilanKosong;
import com.studyplanner.tampilan.WidgetTugasMendatang;
import com.studyplanner.kontroler.pembantu.ManajerWidgetDashboard;
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
import javafx.stage.Stage;
import javafx.util.Duration;

public class KontrolerUtama implements Initializable {

    @FXML
    private Label labelSelamatDatang;

    @FXML
    private Label labelTanggal;

    @FXML
    private Label labelTotalTopik;

    @FXML
    private Label labelTopikDikuasai;

    @FXML
    private Label labelTugasHariIni;

    @FXML
    private Label labelTugasSelesai;

    @FXML
    private ProgressBar progressKeseluruhan;

    @FXML
    private ProgressBar progressHariIni;

    @FXML
    private Label labelProgressKeseluruhan;

    @FXML
    private Label labelProgressHariIni;

    @FXML
    private VBox wadahTugasHariIni;

    @FXML
    private VBox wadahUjianMendatang;

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
    private Button tombolKelolaMataKuliah;

    @FXML
    private Button tombolLihatJadwal;

    @FXML
    private Button tombolAlihTema;

    @FXML
    private Button tombolPengaturan;

    @FXML
    private Button tombolKeluar;

    @FXML
    private Button tombolAlihSidebar;

    @FXML
    private Button tombolPengaturanHeader;

    @FXML
    private Button tombolProfilHeader;

    @FXML
    private HBox wadahWidgetKontainer;

    @FXML
    private ScrollPane scrollPaneUtama;

    @FXML
    private VBox kontenDashboard;

    private ManajerBasisData manajerBasisData;
    private Node kontenDashboardAsli; // Simpan konten dashboard untuk swap
    private LayananMataKuliah layananMataKuliah;
    private LayananTopik layananTopik;
    private LayananJadwalUjian layananJadwalUjian;
    private LayananSesiBelajar layananSesiBelajar;
    private boolean isDarkMode = false;
    private PembuatJadwal pembuatJadwal;
    private Timeline autoRefreshTimeline;
    private boolean isSidebarVisible = true;
    private ManajerWidgetDashboard manajerWidget;
    
    // State untuk SPA navigation
    private enum HalamanAktif { DASHBOARD, PENGATURAN, MANAJEMEN_MATKUL, LIHAT_JADWAL }
    private HalamanAktif halamanAktif = HalamanAktif.DASHBOARD;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();

        layananMataKuliah = new LayananMataKuliah(manajerBasisData);
        layananTopik = new LayananTopik(manajerBasisData);
        layananJadwalUjian = new LayananJadwalUjian(manajerBasisData);
        layananSesiBelajar = new LayananSesiBelajar(manajerBasisData);

        pembuatJadwal = new PembuatJadwal(manajerBasisData);


        isDarkMode = PreferensiPengguna.getInstance().isDarkMode();

        siapkanUI();
        loadDashboardData();
        siapkanPembaruanOtomatis();

        // Terapkan dark mode jika tersimpan di preferensi
        terapkanModaGelapPadaStartup();

        // Cek dan tampilkan onboarding untuk user baru
        periksaDanTampilkanOnboarding();
    }

    /**
     * Cek apakah perlu menampilkan onboarding untuk user baru.
     */
    private void periksaDanTampilkanOnboarding() {
        int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
        if (userId > 0 && !PreferensiPengguna.getInstance().isOnboardingSelesai(userId)) {
            // Tampilkan onboarding setelah scene ditampilkan
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(500); // Tunggu sebentar agar UI siap
                    Stage stage = (Stage) labelSelamatDatang.getScene().getWindow();
                    DialogPengenalan pengenalan = new DialogPengenalan(stage, () -> {
                        PreferensiPengguna.getInstance().setOnboardingSelesai(userId, true);
                    });
                    pengenalan.tampilkan();
                } catch (Exception e) {
                    // Abaikan error onboarding
                }
            });
        }
    }

    private void siapkanUI() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy", Locale.of("id", "ID"));
        labelTanggal.setText(LocalDate.now().format(formatter));

        terapkanAnimasiMasuk();

        // Setup widget system
        siapkanSistemWidget();

        tombolKelolaMataKuliah.setGraphic(PembuatIkon.ikonMataKuliah());
        tombolKelolaMataKuliah.setGraphicTextGap(8);
        tombolKelolaMataKuliah.setOnAction(_ -> bukaManajemenMataKuliah());

        tombolLihatJadwal.setGraphic(PembuatIkon.ikonJadwal());
        tombolLihatJadwal.setGraphicTextGap(8);
        tombolLihatJadwal.setOnAction(_ -> bukaLihatJadwal());

        if (tombolAlihTema != null) {
            tombolAlihTema.setGraphic(PembuatIkon.ikonModeGelap(isDarkMode));
            tombolAlihTema.setText(isDarkMode ? "Terang" : "Gelap");
            tombolAlihTema.setGraphicTextGap(6);
            tombolAlihTema.setOnAction(_ -> alihkanModaGelap());
        }
        
        if (ManajerOtentikasi.getInstance().isLoggedIn()) {
            Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
            labelSelamatDatang.setText("Selamat Datang, " + user.getGivenName() + "!");
        }
        
        if (tombolPengaturan != null) {
            tombolPengaturan.setGraphic(PembuatIkon.ikonPengaturan());
            tombolPengaturan.setGraphicTextGap(8);
            tombolPengaturan.setOnAction(_ -> tampilkanPengaturan());
        }

        if (tombolKeluar != null) {
            tombolKeluar.setGraphic(PembuatIkon.ikonKeluar());
            tombolKeluar.setGraphicTextGap(8);
            tombolKeluar.setOnAction(_ -> keluar());
        }

        if (tombolAlihSidebar != null) {
            tombolAlihSidebar.setGraphic(PembuatIkon.ikonMenu());
            tombolAlihSidebar.setText("");
            tombolAlihSidebar.setOnAction(_ -> toggleSidebar());
        }

        // Tombol settings dihapus dari header - dipindah ke dialog profil
        if (tombolPengaturanHeader != null) {
            tombolPengaturanHeader.setVisible(false);
            tombolPengaturanHeader.setManaged(false);
        }

        if (tombolProfilHeader != null) {
            if (ManajerOtentikasi.getInstance().isLoggedIn()) {
                Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
                aturFotoProfilHeader(user);
            } else {
                tombolProfilHeader.setGraphic(PembuatIkon.ikonProfil());
            }
            tombolProfilHeader.setText("");
            tombolProfilHeader.setOnAction(_ -> tampilkanPengaturan()); // Langsung buka pengaturan
        }
    }

    /**
     * Setup sistem widget yang dapat dikustomisasi.
     * Menggunakan ManajerWidgetDashboard untuk mengelola widget.
     */
    private void siapkanSistemWidget() {
        if (wadahWidgetKontainer == null) return;
        
        manajerWidget = new ManajerWidgetDashboard(
            wadahWidgetKontainer,
            isDarkMode,
            this::showUpcomingTasksDetailDialog
        );
        manajerWidget.inisialisasi();
    }

    private void siapkanPembaruanOtomatis() {
        autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), _ -> loadDashboardData()));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }

    /**
     * Terapkan dark mode pada startup jika user sebelumnya menggunakan dark mode.
     */
    private void terapkanModaGelapPadaStartup() {
        if (!isDarkMode) return;
        
        // Tunggu scene tersedia lalu terapkan dark mode
        javafx.application.Platform.runLater(() -> {
            if (labelSelamatDatang.getScene() != null) {
                labelSelamatDatang.getScene().getRoot().getStyleClass().add("dark-mode");
                
                // Update window decoration
                Stage stage = (Stage) labelSelamatDatang.getScene().getWindow();
                if (stage != null) {
                    DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
                }
                
                // Update widget dark mode
                if (manajerWidget != null) {
                    manajerWidget.aturModeGelap(isDarkMode);
                }
            }
        });
    }

    private void alihkanModaGelap() {
        isDarkMode = !isDarkMode;

        // Simpan preferensi
        PreferensiPengguna.getInstance().setDarkMode(isDarkMode);

        if (tombolAlihTema != null) {
            tombolAlihTema.setGraphic(PembuatIkon.ikonModeGelap(isDarkMode));
            tombolAlihTema.setText(isDarkMode ? "Terang" : "Gelap");
        }

        if (manajerWidget != null) {
            manajerWidget.aturModeGelap(isDarkMode);
            manajerWidget.setDarkMode(isDarkMode);
        }

        if (tombolKelolaMataKuliah.getScene() != null) {
            if (isDarkMode) {
                tombolKelolaMataKuliah
                        .getScene()
                        .getRoot()
                        .getStyleClass()
                        .add("dark-mode");
            } else {
                tombolKelolaMataKuliah
                        .getScene()
                        .getRoot()
                        .getStyleClass()
                        .remove("dark-mode");
            }

            javafx.stage.Stage stage = (javafx.stage.Stage) tombolKelolaMataKuliah.getScene().getWindow();
            if (stage != null) {
                DekoratorJendelaKustom.dekorasi(stage, "Perencana Belajar Adaptif", isDarkMode);
            }
        }
    }

    private void loadDashboardData() {
        try {
            PembuatJadwal.KemajuanBelajar progress = pembuatJadwal.ambilKemajuanBelajar();

            labelTotalTopik.setText(String.valueOf(progress.getTotalTopik()));
            labelTopikDikuasai.setText(
                    String.valueOf(progress.getTopikDikuasai()));
            labelTugasHariIni.setText(String.valueOf(progress.getTotalHariIni()));
            labelTugasSelesai.setText(
                    String.valueOf(progress.getSelesaiHariIni()));

            progressKeseluruhan.setProgress(
                    progress.getKemajuanKeseluruhan() / 100.0);
            progressHariIni.setProgress(progress.getKemajuanHariIni() / 100.0);

            labelProgressKeseluruhan.setText(
                    String.format("%.0f%%", progress.getKemajuanKeseluruhan()));
            labelProgressHariIni.setText(
                    String.format("%.0f%%", progress.getKemajuanHariIni()));

            loadTodayTasks();
            loadUpcomingExams();

            refreshUpcomingTasksWidget();
            if (manajerWidget != null) {
                manajerWidget.segarkanSemua();
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat data dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshUpcomingTasksWidget() throws SQLException {
        if (manajerWidget == null || manajerWidget.getUpcomingTasksWidget() == null) {
            return;
        }

        List<SesiBelajar> upcomingSessions = layananSesiBelajar.ambilSesiMendatang(4);
        manajerWidget.getUpcomingTasksWidget().aturSesi(upcomingSessions);
    }

    private void showUpcomingTasksDetailDialog() {
        if (manajerWidget == null || manajerWidget.getUpcomingTasksWidget() == null) {
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
        wadahTugasHariIni.getChildren().clear();
        List<SesiBelajar> sessions = layananSesiBelajar.ambilSesiHariIni();

        if (sessions.isEmpty()) {
            TampilanKosong tampilanKosong = TampilanKosong.untukTugasKosong(this::buatJadwalBaru);
            wadahTugasHariIni.getChildren().add(tampilanKosong);
        } else {
            for (SesiBelajar session : sessions) {
                wadahTugasHariIni.getChildren().add(createTaskCard(session));
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
        wadahUjianMendatang.getChildren().clear();
        List<JadwalUjian> exams = layananJadwalUjian.ambilUjianMendatang();

        if (exams.isEmpty()) {
            TampilanKosong tampilanKosong = TampilanKosong.untukUjianKosong(null);
            wadahUjianMendatang.getChildren().add(tampilanKosong);
        } else {
            for (JadwalUjian exam : exams) {
                wadahUjianMendatang.getChildren().add(createExamCard(exam));
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

        Label labelTanggal = new Label(exam.getTanggalUjian().toString());
        labelTanggal.getStyleClass().add("exam-date");

        int daysLeft = exam.getHariMenujuUjian();
        Label daysLabel = new Label(daysLeft + " hari lagi");
        daysLabel.getStyleClass().add("exam-days");

        card
                .getChildren()
                .addAll(titleLabel, courseLabel, labelTanggal, daysLabel);

        return card;
    }

    /**
     * Update state selected pada tombol sidebar.
     */
    private void updateSidebarSelection(Button selected) {
        // Reset semua tombol
        resetSidebarButton(tombolKelolaMataKuliah);
        resetSidebarButton(tombolLihatJadwal);
        resetSidebarButton(tombolPengaturan);
        
        // Tambah selected ke tombol yang aktif
        if (selected != null) {
            selected.getStyleClass().add("sidebar-btn-selected");
            // Set icon warna putih
            if (selected.getGraphic() instanceof org.kordamp.ikonli.javafx.FontIcon icon) {
                icon.setIconColor(javafx.scene.paint.Color.WHITE);
            }
        }
    }
    
    /**
     * Reset sidebar button ke state normal.
     */
    private void resetSidebarButton(Button button) {
        button.getStyleClass().remove("sidebar-btn-selected");
        // Reset icon ke warna default
        if (button.getGraphic() instanceof org.kordamp.ikonli.javafx.FontIcon icon) {
            icon.setIconColor(javafx.scene.paint.Color.web("#42474e"));
        }
    }

    /**
     * Buka manajemen mata kuliah dengan SPA-style.
     */
    private void bukaManajemenMataKuliah() {
        // Jika bukan di dashboard, kembali dulu ke dashboard
        if (halamanAktif != HalamanAktif.DASHBOARD) {
            kembaliKeDashboard();
        }
        
        try {
            // Update sidebar selection
            updateSidebarSelection(tombolKelolaMataKuliah);
            
            // Simpan konten dashboard asli
            if (kontenDashboardAsli == null) {
                kontenDashboardAsli = scrollPaneUtama.getContent();
            }
            halamanAktif = HalamanAktif.MANAJEMEN_MATKUL;

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ManajemenMataKuliah.fxml"));
            Parent fxmlContent = loader.load();

            // Setup controller dulu sebelum ditampilkan
            KontrolerManajemenMataKuliah controller = loader.getController();
            controller.aturKontrolerUtama(this);

            // Wrapper dengan header kembali
            VBox wrapper = new VBox(16);
            wrapper.setPadding(new Insets(24));
            
            // Header dengan tombol kembali
            HBox header = buatHeaderDenganTombolKembali("Kelola Mata Kuliah");
            
            // Tambahkan FXML content
            VBox.setVgrow(fxmlContent, javafx.scene.layout.Priority.ALWAYS);
            wrapper.getChildren().addAll(header, fxmlContent);

            // Swap konten
            scrollPaneUtama.setContent(wrapper);
            scrollPaneUtama.setVvalue(0);
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka manajemen mata kuliah: " + e.getMessage());
            e.printStackTrace();
            halamanAktif = HalamanAktif.DASHBOARD;
        }
    }

    /**
     * Buka lihat jadwal dengan SPA-style.
     */
    private void bukaLihatJadwal() {
        // Jika bukan di dashboard, kembali dulu ke dashboard
        if (halamanAktif != HalamanAktif.DASHBOARD) {
            kembaliKeDashboard();
        }
        
        try {
            // Update sidebar selection
            updateSidebarSelection(tombolLihatJadwal);
            
            // Simpan konten dashboard asli
            if (kontenDashboardAsli == null) {
                kontenDashboardAsli = scrollPaneUtama.getContent();
            }
            halamanAktif = HalamanAktif.LIHAT_JADWAL;

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ScheduleView.fxml"));
            Parent fxmlContent = loader.load();

            // Wrapper dengan header kembali dan judul
            VBox wrapper = new VBox(16);
            wrapper.setPadding(new Insets(24));
            
            // Header dengan tombol kembali dan judul (konsisten dengan halaman lain)
            HBox header = buatHeaderDenganTombolKembali("Jadwal Belajar");
            
            // Tambahkan FXML content
            VBox.setVgrow(fxmlContent, javafx.scene.layout.Priority.ALWAYS);
            wrapper.getChildren().addAll(header, fxmlContent);

            // Swap konten
            scrollPaneUtama.setContent(wrapper);
            scrollPaneUtama.setVvalue(0);
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka tampilan jadwal: " + e.getMessage());
            e.printStackTrace();
            halamanAktif = HalamanAktif.DASHBOARD;
        }
    }

    /**
     * Helper untuk membuat header dengan tombol kembali (reusable).
     */
    private HBox buatHeaderDenganTombolKembali(String judul) {
        HBox header = new HBox(16);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button tombolKembali = new Button();
        tombolKembali.setGraphic(PembuatIkon.ikonKembali());
        tombolKembali.getStyleClass().add("btn-icon");
        tombolKembali.setOnAction(_ -> kembaliKeDashboard());
        
        Label labelJudul = new Label(judul);
        labelJudul.getStyleClass().add("section-title");
        labelJudul.setStyle("-fx-font-size: 24px;");
        
        header.getChildren().addAll(tombolKembali, labelJudul);
        return header;
    }

    private void buatJadwalBaru() {
        try {
            // Validasi: cek apakah ada mata kuliah
            int userId = ManajerOtentikasi.getInstance().getCurrentUserId();
            var daftarMataKuliah = layananMataKuliah.ambilSemuaByUserId(userId);
            
            if (daftarMataKuliah.isEmpty()) {
                UtilUI.tampilkanPeringatan(
                    "Belum ada mata kuliah!\n\n" +
                    "Silakan tambahkan mata kuliah terlebih dahulu melalui menu 'Kelola Mata Kuliah'.");
                return;
            }
            
            // Validasi: cek apakah ada topik
            var daftarTopik = layananTopik.ambilSemuaByUserId(userId);
            
            if (daftarTopik.isEmpty()) {
                UtilUI.tampilkanPeringatan(
                    "Belum ada topik materi!\n\n" +
                    "Silakan tambahkan topik materi pada mata kuliah melalui menu 'Kelola Mata Kuliah'.");
                return;
            }
            
            pembuatJadwal.buatDanSimpanJadwal(7);
            UtilUI.tampilkanInfo(
                    "Jadwal belajar berhasil di-generate untuk 7 hari ke depan!");
            loadDashboardData();
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal membuat jadwal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Buat section profil untuk ditampilkan di pengaturan.
     */
    private VBox buatSectionProfil() {
        VBox section = new VBox(16);
        section.getStyleClass().add("settings-profile-section");
        
        HBox profileCard = new HBox(16);
        profileCard.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        profileCard.getStyleClass().add("settings-profile-card");
        profileCard.setPadding(new Insets(16));

        if (ManajerOtentikasi.getInstance().isLoggedIn()) {
            Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();
            
            // Avatar
            ImageView avatarView = new ImageView();
            if (user.getPicture() != null) {
                try {
                    avatarView.setImage(new Image(user.getPicture(), 56, 56, true, true));
                } catch (Exception e) {
                    avatarView.setImage(null);
                }
            }
            avatarView.setFitWidth(56);
            avatarView.setFitHeight(56);
            Circle clip = new Circle(28, 28, 28);
            avatarView.setClip(clip);
            
            // Info
            VBox infoBox = new VBox(2);
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);
            
            Label nameLabel = new Label(user.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            infoBox.getChildren().add(nameLabel);
            
            // Hanya tampilkan email jika ada
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                Label emailLabel = new Label(user.getEmail());
                emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
                infoBox.getChildren().add(emailLabel);
            }
            
            Label providerLabel = new Label("Masuk dengan Google");
            providerLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
            infoBox.getChildren().add(providerLabel);
            
            profileCard.getChildren().addAll(avatarView, infoBox);
        } else {
            Label notLoggedIn = new Label("Belum masuk");
            notLoggedIn.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
            profileCard.getChildren().add(notLoggedIn);
        }
        
        section.getChildren().add(profileCard);
        return section;
    }

    /**
     * Tampilkan pengaturan dengan swap konten (SPA-style).
     * Tidak membuka window baru, tapi mengganti konten di window yang sama.
     */
    private void tampilkanPengaturan() {
        // Jika bukan di dashboard, kembali dulu
        if (halamanAktif != HalamanAktif.DASHBOARD && halamanAktif != HalamanAktif.PENGATURAN) {
            kembaliKeDashboard();
        }
        
        // Jika sudah di pengaturan, tidak perlu buka lagi
        if (halamanAktif == HalamanAktif.PENGATURAN) return;
        
        // Simpan konten dashboard asli
        if (kontenDashboardAsli == null) {
            kontenDashboardAsli = scrollPaneUtama.getContent();
        }
        halamanAktif = HalamanAktif.PENGATURAN;
        
        // Update sidebar selection
        updateSidebarSelection(tombolPengaturan);

        // Buat konten pengaturan
        VBox settingsContent = new VBox(24);
        settingsContent.setPadding(new Insets(24));
        settingsContent.getStyleClass().add("settings-window");
        
        // === HEADER dengan tombol kembali ===
        HBox header = new HBox(16);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button tombolKembali = new Button();
        tombolKembali.setGraphic(PembuatIkon.ikonKembali());
        tombolKembali.getStyleClass().add("btn-icon");
        tombolKembali.setOnAction(_ -> kembaliKeDashboard());
        
        Label judulPengaturan = new Label("Pengaturan");
        judulPengaturan.getStyleClass().add("section-title");
        judulPengaturan.setStyle("-fx-font-size: 24px;");
        
        header.getChildren().addAll(tombolKembali, judulPengaturan);

        // === SECTION PROFIL ===
        VBox profileSection = buatSectionProfil();

        VBox appearanceSection = createSettingsSection("Tampilan", PembuatIkon.ikonTampilan());

        HBox darkModeRow = createSettingRow(
            "Mode Gelap",
            "Ubah tema aplikasi menjadi gelap untuk kenyamanan mata di malam hari"
        );
        CheckBox darkModeCheck = new CheckBox();
        darkModeCheck.setSelected(isDarkMode);
        darkModeCheck.setOnAction(_ -> alihkanModaGelap());
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

        HBox generateScheduleRow = createSettingRow(
            "Generate Jadwal Manual",
            "Buat ulang jadwal belajar untuk 7 hari ke depan (override)"
        );
        Button generateScheduleBtn = new Button("Buat Jadwal");
        generateScheduleBtn.setGraphic(PembuatIkon.ikonBuatJadwal());
        generateScheduleBtn.getStyleClass().add("btn-secondary");
        generateScheduleBtn.setStyle("-fx-pref-width: 140px;");
        generateScheduleBtn.setOnAction(_ -> buatJadwalBaru());
        generateScheduleRow.getChildren().add(generateScheduleBtn);

        studySection.getChildren().addAll(durationRow, reminderRow, generateScheduleRow);

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
            header,
            profileSection,
            appearanceSection,
            studySection,
            dataSection,
            aboutSection
        );

        // Swap konten - seperti React Router!
        scrollPaneUtama.setContent(settingsContent);
        scrollPaneUtama.setVvalue(0); // Scroll ke atas
    }

    /**
     * Kembali ke dashboard dari halaman manapun (seperti router.back() di web).
     */
    private void kembaliKeDashboard() {
        if (halamanAktif == HalamanAktif.DASHBOARD || kontenDashboardAsli == null) return;
        
        // Clear sidebar selection saat kembali ke dashboard
        updateSidebarSelection(null);
        
        scrollPaneUtama.setContent(kontenDashboardAsli);
        scrollPaneUtama.setVvalue(0);
        halamanAktif = HalamanAktif.DASHBOARD;
        
        // Refresh data dashboard
        loadDashboardData();
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
                    
                    Stage stage = (Stage) labelSelamatDatang.getScene().getWindow();
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

                tombolProfilHeader.setGraphic(imageView);
            } else {
                Label inisial = new Label(ambilInisial(user.getName()));
                inisial.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 18px;"
                );
                tombolProfilHeader.setGraphic(inisial);
            }
        } catch (Exception e) {
            tombolProfilHeader.setGraphic(PembuatIkon.ikonProfil());
            e.printStackTrace();
        }
    }

    private void terapkanAnimasiMasuk() {
        // Sidebar: slide in dari kiri dengan spring physics
        if (sidebar != null) {
            sidebar.setOpacity(0);
            sidebar.setTranslateX(-50);

            FadeTransition fadeSidebar = new FadeTransition(Duration.millis(400), sidebar);
            fadeSidebar.setFromValue(0.0);
            fadeSidebar.setToValue(1.0);
            fadeSidebar.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideSidebar = new TranslateTransition(Duration.millis(500), sidebar);
            slideSidebar.setFromX(-50);
            slideSidebar.setToX(0);
            slideSidebar.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            ParallelTransition sidebarAnim = new ParallelTransition(fadeSidebar, slideSidebar);
            sidebarAnim.play();
        }

        // Welcome section: fade in dengan easing
        if (labelSelamatDatang != null && labelSelamatDatang.getParent() != null) {
            labelSelamatDatang.getParent().setOpacity(0);
            FadeTransition fadeWelcome = new FadeTransition(Duration.millis(500), labelSelamatDatang.getParent());
            fadeWelcome.setFromValue(0.0);
            fadeWelcome.setToValue(1.0);
            fadeWelcome.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
            fadeWelcome.setDelay(Duration.millis(100));
            fadeWelcome.play();
        }

        // Stats grid: slide up dengan spring
        if (statsGrid != null) {
            statsGrid.setOpacity(0);
            statsGrid.setTranslateY(40);

            FadeTransition fadeStats = new FadeTransition(Duration.millis(400), statsGrid);
            fadeStats.setFromValue(0.0);
            fadeStats.setToValue(1.0);
            fadeStats.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideStats = new TranslateTransition(Duration.millis(600), statsGrid);
            slideStats.setFromY(40);
            slideStats.setToY(0);
            slideStats.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            ParallelTransition statsAnim = new ParallelTransition(fadeStats, slideStats);
            statsAnim.setDelay(Duration.millis(150));
            statsAnim.play();
        }

        // Activity section: fade in
        if (activitySection != null) {
            activitySection.setOpacity(0);
            FadeTransition fadeActivity = new FadeTransition(Duration.millis(500), activitySection);
            fadeActivity.setFromValue(0.0);
            fadeActivity.setToValue(1.0);
            fadeActivity.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
            fadeActivity.setDelay(Duration.millis(300));
            fadeActivity.play();
        }

        // Main content: slide up dengan spring
        if (mainContentGrid != null) {
            mainContentGrid.setOpacity(0);
            mainContentGrid.setTranslateY(40);

            FadeTransition fadeContent = new FadeTransition(Duration.millis(400), mainContentGrid);
            fadeContent.setFromValue(0.0);
            fadeContent.setToValue(1.0);
            fadeContent.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideContent = new TranslateTransition(Duration.millis(600), mainContentGrid);
            slideContent.setFromY(40);
            slideContent.setToY(0);
            slideContent.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            ParallelTransition contentAnim = new ParallelTransition(fadeContent, slideContent);
            contentAnim.setDelay(Duration.millis(400));
            contentAnim.play();
        }

        terapkanAnimasiHoverSidebarButton(tombolKelolaMataKuliah);
        terapkanAnimasiHoverSidebarButton(tombolLihatJadwal);
    }

    private void terapkanAnimasiHoverSidebarButton(Button button) {
        if (button == null) return;

        button.setOnMouseEntered(_ -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.03);
            scaleUp.setToY(1.03);
            scaleUp.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            TranslateTransition slideRight = new TranslateTransition(Duration.millis(200), button);
            slideRight.setToX(6);
            slideRight.setInterpolator(AnimasiUtil.EASE_OUT_BACK);

            ParallelTransition hoverIn = new ParallelTransition(scaleUp, slideRight);
            hoverIn.play();
        });

        button.setOnMouseExited(_ -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(250), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.setInterpolator(AnimasiUtil.SPRING_SNAPPY);

            TranslateTransition slideBack = new TranslateTransition(Duration.millis(250), button);
            slideBack.setToX(0);
            slideBack.setInterpolator(AnimasiUtil.SPRING_SNAPPY);

            ParallelTransition hoverOut = new ParallelTransition(scaleDown, slideBack);
            hoverOut.play();
        });

        // Press animation untuk feedback responsif
        button.setOnMousePressed(_ -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(80), button);
            press.setToX(0.97);
            press.setToY(0.97);
            press.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);
            press.play();
        });

        button.setOnMouseReleased(_ -> {
            ScaleTransition release = new ScaleTransition(Duration.millis(200), button);
            release.setToX(1.03);
            release.setToY(1.03);
            release.setInterpolator(AnimasiUtil.SPRING_BOUNCY);
            release.play();
        });
    }

    private void toggleSidebar() {
        if (sidebar == null) return;

        isSidebarVisible = !isSidebarVisible;

        if (isSidebarVisible) {
            sidebar.setManaged(true);
            sidebar.setVisible(true);
            sidebar.setTranslateX(-240);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), sidebar);
            slideIn.setFromX(-240);
            slideIn.setToX(0);
            slideIn.setInterpolator(AnimasiUtil.SPRING_DEFAULT);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), sidebar);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);
            showAnim.play();
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), sidebar);
            slideOut.setFromX(0);
            slideOut.setToX(-240);
            slideOut.setInterpolator(AnimasiUtil.EASE_IN_OUT_CUBIC);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), sidebar);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(AnimasiUtil.EASE_OUT_CUBIC);

            ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);
            hideAnim.setOnFinished(_ -> {
                sidebar.setManaged(false);
                sidebar.setVisible(false);
            });
            hideAnim.play();
        }
    }
}
