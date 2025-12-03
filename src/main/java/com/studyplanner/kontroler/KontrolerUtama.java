package com.studyplanner.kontroler;

import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.PreferensiPengguna;
import com.studyplanner.utilitas.UtilUI;
import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.algoritma.PembuatJadwal;
import com.studyplanner.tampilan.DekoratorJendelaKustom;
import com.studyplanner.tampilan.DialogPengenalan;
import com.studyplanner.tampilan.WidgetTugasMendatang;
import com.studyplanner.kontroler.pembantu.ManajerWidgetDashboard;
import com.studyplanner.kontroler.pembantu.PembantuTema;
import com.studyplanner.kontroler.pembantu.PembantuAnimasi;
import com.studyplanner.kontroler.pembantu.PembantuNavigasi;
import com.studyplanner.kontroler.pembantu.PembantuPengaturan;
import com.studyplanner.kontroler.pembantu.PembantuDialogOverlay;
import com.studyplanner.kontroler.pembantu.PembantuDashboard;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

    // SPA-style dialog overlay
    @FXML
    private StackPane dialogOverlay;

    @FXML
    private VBox dialogContainer;

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
    
    // Helper classes (SOLID - SRP)
    private PembantuTema pembantuTema;
    private PembantuAnimasi pembantuAnimasi;
    private PembantuNavigasi pembantuNavigasi;
    private PembantuDialogOverlay pembantuDialog;
    private PembantuDashboard pembantuDashboard;
    private PembantuPengaturan pembantuPengaturan;
    
    // State untuk SPA navigation (dikelola di sini, bukan di helper)
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

        // Inisialisasi helper classes (SOLID - SRP)
        inisialisasiHelpers();

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
     * Inisialisasi helper classes untuk Single Responsibility Principle.
     */
    private void inisialisasiHelpers() {
        // Tema helper
        pembantuTema = new PembantuTema(() -> {
            if (manajerWidget != null) {
                manajerWidget.aturModeGelap(pembantuTema.isDarkMode());
                manajerWidget.setDarkMode(pembantuTema.isDarkMode());
            }
        });

        // Animasi helper
        pembantuAnimasi = new PembantuAnimasi();

        // Navigasi helper
        pembantuNavigasi = new PembantuNavigasi(scrollPaneUtama, this::loadDashboardData);
        pembantuNavigasi.setTombolSidebar(tombolKelolaMataKuliah, tombolLihatJadwal, tombolPengaturan);

        // Dialog overlay helper
        pembantuDialog = new PembantuDialogOverlay(dialogOverlay, dialogContainer);

        // Dashboard helper
        pembantuDashboard = new PembantuDashboard(
            layananMataKuliah,
            layananTopik,
            layananJadwalUjian,
            layananSesiBelajar,
            pembuatJadwal,
            this::buatJadwalBaru,
            this::loadDashboardData
        );

        // Pengaturan helper
        pembantuPengaturan = new PembantuPengaturan(isDarkMode, this::alihkanModaGelap, this::buatJadwalBaru);
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
            String nama = ManajerOtentikasi.getInstance().getCurrentUserName();
            // Ambil nama depan saja
            String namaDepan = nama != null && nama.contains(" ") ? nama.split(" ")[0] : nama;
            labelSelamatDatang.setText("Selamat Datang, " + (namaDepan != null ? namaDepan : "User") + "!");
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

        // Tambah tombol berdasarkan role pengguna
        tambahTombolBerdasarkanRole();
        
        // Sembunyikan menu mahasiswa untuk admin dan dosen
        sembunyikanMenuBerdasarkanRole();
        
        // Auto-navigate ke panel sesuai role
        arahkanKeHalamanSesuaiRole();

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
                if (user != null) {
                    aturFotoProfilHeader(user);
                } else {
                    // Untuk local user, gunakan icon default
                    tombolProfilHeader.setGraphic(PembuatIkon.ikonProfil());
                }
            } else {
                tombolProfilHeader.setGraphic(PembuatIkon.ikonProfil());
            }
            tombolProfilHeader.setText("");
            tombolProfilHeader.setOnAction(_ -> tampilkanPengaturan()); // Langsung buka pengaturan
        }
    }

    /**
     * Tambah tombol sidebar berdasarkan role pengguna (Admin/Dosen).
     */
    private void tambahTombolBerdasarkanRole() {
        ManajerOtentikasi auth = ManajerOtentikasi.getInstance();
        
        // Debug: cek role
        System.out.println("[DEBUG] Role user: " + auth.getCurrentUserRole() + ", isAdmin: " + auth.isAdmin() + ", isDosen: " + auth.isDosen());
        
        // Cari VBox navigasi di sidebar (child kedua dari sidebar)
        VBox navContainer = null;
        for (Node child : sidebar.getChildren()) {
            if (child instanceof VBox vbox && vbox.getChildren().contains(tombolKelolaMataKuliah)) {
                navContainer = vbox;
                break;
            }
        }
        
        if (navContainer == null) return;

        // Tombol Admin
        if (auth.isAdmin()) {
            Button tombolAdmin = new Button("Panel Admin");
            tombolAdmin.setGraphic(PembuatIkon.ikonAdmin());
            tombolAdmin.setGraphicTextGap(8);
            tombolAdmin.getStyleClass().add("sidebar-btn");
            tombolAdmin.setMaxWidth(Double.MAX_VALUE);
            tombolAdmin.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            tombolAdmin.setStyle("-fx-padding: 12 16;");
            tombolAdmin.setOnAction(_ -> bukaPanelAdmin());
            navContainer.getChildren().add(0, tombolAdmin); // Di atas tombol lainnya
        }

        // Tombol Dosen
        if (auth.isDosen()) {
            Button tombolDosen = new Button("Dashboard Dosen");
            tombolDosen.setGraphic(PembuatIkon.ikonDosen());
            tombolDosen.setGraphicTextGap(8);
            tombolDosen.getStyleClass().add("sidebar-btn");
            tombolDosen.setMaxWidth(Double.MAX_VALUE);
            tombolDosen.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            tombolDosen.setStyle("-fx-padding: 12 16;");
            tombolDosen.setOnAction(_ -> bukaPanelDosen());
            navContainer.getChildren().add(0, tombolDosen);
        }
    }

    /**
     * Sembunyikan menu "Kelola Mata Kuliah" dan "Lihat Jadwal" untuk Admin dan Dosen.
     * Menu ini khusus untuk mahasiswa.
     */
    private void sembunyikanMenuBerdasarkanRole() {
        ManajerOtentikasi auth = ManajerOtentikasi.getInstance();
        
        // Jika user adalah Admin atau Dosen, sembunyikan menu mahasiswa
        if (auth.isAdmin() || auth.isDosen()) {
            if (tombolKelolaMataKuliah != null) {
                tombolKelolaMataKuliah.setVisible(false);
                tombolKelolaMataKuliah.setManaged(false);
            }
            if (tombolLihatJadwal != null) {
                tombolLihatJadwal.setVisible(false);
                tombolLihatJadwal.setManaged(false);
            }
        }
    }

    /**
     * Auto-navigate ke halaman sesuai role pengguna.
     * Admin langsung ke Panel Admin, Dosen langsung ke Dashboard Dosen.
     */
    private void arahkanKeHalamanSesuaiRole() {
        ManajerOtentikasi auth = ManajerOtentikasi.getInstance();
        
        // Gunakan Platform.runLater agar UI sudah siap
        javafx.application.Platform.runLater(() -> {
            if (auth.isAdmin()) {
                bukaPanelAdmin();
            } else if (auth.isDosen()) {
                bukaPanelDosen();
            }
            // Mahasiswa tetap di dashboard utama
        });
    }

    /**
     * Buka Panel Admin.
     */
    private void bukaPanelAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminView.fxml"));
            Parent konten = loader.load();
            
            VBox wrapper = pembantuNavigasi.buatWrapperDenganHeader("Panel Administrator", konten);
            pembantuNavigasi.navigasiKe(PembantuNavigasi.Halaman.PANEL_ADMIN, wrapper);
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka Panel Admin: " + e.getMessage());
        }
    }

    /**
     * Buka Panel Dosen.
     */
    private void bukaPanelDosen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DosenView.fxml"));
            Parent konten = loader.load();
            
            VBox wrapper = pembantuNavigasi.buatWrapperDenganHeader("Dashboard Dosen", konten);
            pembantuNavigasi.navigasiKe(PembantuNavigasi.Halaman.PANEL_DOSEN, wrapper);
        } catch (IOException e) {
            UtilUI.tampilkanKesalahan("Gagal membuka Dashboard Dosen: " + e.getMessage());
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

    // Delegasi ke PembantuTema
    private void terapkanModaGelapPadaStartup() {
        if (!isDarkMode) return;
        javafx.application.Platform.runLater(() -> {
            pembantuTema.terapkanModaGelap(labelSelamatDatang.getScene(), manajerWidget);
        });
    }

    private void alihkanModaGelap() {
        pembantuTema.alihkanModaGelap(tombolAlihTema, tombolKelolaMataKuliah.getScene(), manajerWidget);
        isDarkMode = pembantuTema.isDarkMode();
    }

    private void loadDashboardData() {
        try {
            // Delegasi ke PembantuDashboard
            pembantuDashboard.muatDataDashboard(
                labelTotalTopik, labelTopikDikuasai,
                labelTugasHariIni, labelTugasSelesai,
                progressKeseluruhan, progressHariIni,
                labelProgressKeseluruhan, labelProgressHariIni,
                wadahTugasHariIni, wadahUjianMendatang
            );
            
            // Refresh widget
            if (manajerWidget != null) {
                List<SesiBelajar> upcomingSessions = pembantuDashboard.ambilSesiMendatang(4);
                if (manajerWidget.getUpcomingTasksWidget() != null) {
                    manajerWidget.getUpcomingTasksWidget().aturSesi(upcomingSessions);
                }
                manajerWidget.segarkanSemua();
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat data dashboard: " + e.getMessage());
        }
    }

    private void showUpcomingTasksDetailDialog() {
        try {
            List<SesiBelajar> sessions = pembantuDashboard.ambilSesiMendatang(12);
            Dialog<Void> dialog = PembuatDialogMD3.buatDialog("Detail Upcoming Tasks", null);
            dialog.getDialogPane().getButtonTypes().add(PembuatDialogMD3.buatTombolTutup());
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

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
            content.getChildren().addAll(new Label("Prioritas 7 Hari Ke Depan"), scrollPane);
            dialog.getDialogPane().setContent(content);
            dialog.showAndWait();
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat detail: " + e.getMessage());
        }
    }

    // Delegasi ke PembantuNavigasi
    private void updateSidebarSelection(Button selected) {
        pembantuNavigasi.updateSidebarSelection(selected);
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
            int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
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
            UtilUI.tampilkanToast("Jadwal berhasil di-generate untuk 7 hari ke depan!");
            loadDashboardData();
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal membuat jadwal: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Tampilkan pengaturan (delegasi ke PembantuPengaturan).
     */
    private void tampilkanPengaturan() {
        if (halamanAktif != HalamanAktif.DASHBOARD && halamanAktif != HalamanAktif.PENGATURAN) {
            kembaliKeDashboard();
        }
        if (halamanAktif == HalamanAktif.PENGATURAN) return;
        
        if (kontenDashboardAsli == null) {
            kontenDashboardAsli = scrollPaneUtama.getContent();
        }
        halamanAktif = HalamanAktif.PENGATURAN;
        updateSidebarSelection(tombolPengaturan);

        // Delegasi ke PembantuPengaturan
        HBox header = buatHeaderDenganTombolKembali("Pengaturan");
        VBox settingsContent = pembantuPengaturan.bangunKontenPengaturan(header);
        
        scrollPaneUtama.setContent(settingsContent);
        scrollPaneUtama.setVvalue(0);
    }

    private void kembaliKeDashboard() {
        if (halamanAktif == HalamanAktif.DASHBOARD || kontenDashboardAsli == null) return;
        updateSidebarSelection(null);
        scrollPaneUtama.setContent(kontenDashboardAsli);
        scrollPaneUtama.setVvalue(0);
        halamanAktif = HalamanAktif.DASHBOARD;
        loadDashboardData();
    }

    private void keluar() {
        // Gunakan SPA-style dialog overlay
        tampilkanKonfirmasi(
            "Konfirmasi Keluar",
            "Apakah Anda yakin ingin keluar? Anda akan logout dari aplikasi.",
            () -> {
                try {
                    ManajerOtentikasi.getInstance().logout();
                    
                    Stage currentStage = getStage();
                    if (currentStage == null) return;
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
                    Parent root = loader.load();
                    
                    Scene scene = new Scene(root, 1000, 700);
                    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
                    
                    currentStage.setScene(scene);
                    DekoratorJendelaKustom.dekorasi(currentStage, "Perencana Belajar Adaptif", false);
                } catch (Exception e) {
                    UtilUI.tampilkanKesalahan("Gagal logout: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        );
    }
    
    /**
     * Helper method untuk mendapatkan Stage dari berbagai node yang mungkin tersedia.
     */
    private Stage getStage() {
        // Coba dari berbagai node yang mungkin masih terpasang ke scene
        if (labelSelamatDatang != null && labelSelamatDatang.getScene() != null) {
            return (Stage) labelSelamatDatang.getScene().getWindow();
        }
        if (sidebar != null && sidebar.getScene() != null) {
            return (Stage) sidebar.getScene().getWindow();
        }
        if (scrollPaneUtama != null && scrollPaneUtama.getScene() != null) {
            return (Stage) scrollPaneUtama.getScene().getWindow();
        }
        if (kontenDashboard != null && kontenDashboard.getScene() != null) {
            return (Stage) kontenDashboard.getScene().getWindow();
        }
        return null;
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
        // Delegasi ke PembantuAnimasi
        Node welcomeParent = (labelSelamatDatang != null) ? labelSelamatDatang.getParent() : null;
        pembantuAnimasi.terapkanAnimasiMasuk(sidebar, welcomeParent, statsGrid, activitySection, mainContentGrid);
        pembantuAnimasi.terapkanAnimasiHoverSidebarButton(tombolKelolaMataKuliah);
        pembantuAnimasi.terapkanAnimasiHoverSidebarButton(tombolLihatJadwal);
    }

    private void toggleSidebar() {
        isSidebarVisible = !isSidebarVisible;
        pembantuAnimasi.toggleSidebar(sidebar, isSidebarVisible);
    }

    // Delegasi ke PembantuDialogOverlay
    public void tampilkanDialogOverlay(Node konten) {
        pembantuDialog.setDarkMode(isDarkMode);
        pembantuDialog.tampilkan(konten);
    }

    public void tutupDialogOverlay() {
        pembantuDialog.tutup();
    }

    public void tampilkanKonfirmasi(String judul, String pesan, Runnable onKonfirmasi) {
        pembantuDialog.setDarkMode(isDarkMode);
        pembantuDialog.tampilkanKonfirmasi(judul, pesan, onKonfirmasi);
    }
}
