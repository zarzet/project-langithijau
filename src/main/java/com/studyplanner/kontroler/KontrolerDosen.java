package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAODosen;
import com.studyplanner.dao.DAOMahasiswa;
import com.studyplanner.dao.DAOMataKuliah;
import com.studyplanner.dao.DAORekomendasi;
import com.studyplanner.dao.DAOTopik;
import com.studyplanner.model.Dosen;
import com.studyplanner.model.Mahasiswa;
import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.Rekomendasi;
import com.studyplanner.model.Rekomendasi.StatusRekomendasi;
import com.studyplanner.model.Topik;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.PencatatLog;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk Dashboard Dosen Pembimbing.
 * Menangani UC-19 (Lihat Daftar Mahasiswa Bimbingan), UC-20 (Lihat Progress Mahasiswa),
 * UC-22 (Berikan Rekomendasi Topik), dan UC-23 (Export Laporan Progress).
 */
public class KontrolerDosen implements Initializable {

    // Header
    @FXML private Label labelNamaDosen;

    // Statistik Cards
    @FXML private Label labelTotalMahasiswa;
    @FXML private Label labelAktifMingguIni;
    @FXML private Label labelRataProgress;
    @FXML private Label labelPerluPerhatian;
    @FXML private Label labelTotalRekomendasi;

    // Tab Navigation
    @FXML private ToggleButton tabMahasiswa;
    @FXML private ToggleButton tabRekomendasi;
    @FXML private ToggleGroup tabGroup;

    // Panel Mahasiswa
    @FXML private VBox panelMahasiswa;
    @FXML private ComboBox<String> filterStatus;
    @FXML private TextField searchField;
    @FXML private Button btnRefresh;

    @FXML private TableView<Mahasiswa> tabelMahasiswa;
    @FXML private TableColumn<Mahasiswa, String> kolNama;
    @FXML private TableColumn<Mahasiswa, String> kolNim;
    @FXML private TableColumn<Mahasiswa, String> kolSemester;
    @FXML private TableColumn<Mahasiswa, String> kolMataKuliah;
    @FXML private TableColumn<Mahasiswa, String> kolProgress;
    @FXML private TableColumn<Mahasiswa, String> kolAktivitas;
    @FXML private TableColumn<Mahasiswa, String> kolPerforma;
    @FXML private TableColumn<Mahasiswa, String> kolStatusMhs;
    @FXML private TableColumn<Mahasiswa, Void> kolAksi;

    // Panel Rekomendasi
    @FXML private VBox panelRekomendasi;
    @FXML private ComboBox<String> filterStatusRekomendasi;
    @FXML private Button btnTambahRekomendasi;

    @FXML private TableView<Rekomendasi> tabelRekomendasi;
    @FXML private TableColumn<Rekomendasi, String> kolRekMahasiswa;
    @FXML private TableColumn<Rekomendasi, String> kolRekTopik;
    @FXML private TableColumn<Rekomendasi, String> kolRekMataKuliah;
    @FXML private TableColumn<Rekomendasi, String> kolRekPrioritas;
    @FXML private TableColumn<Rekomendasi, String> kolRekKesulitan;
    @FXML private TableColumn<Rekomendasi, String> kolRekStatus;
    @FXML private TableColumn<Rekomendasi, String> kolRekTanggal;
    @FXML private TableColumn<Rekomendasi, Void> kolRekAksi;

    // SPA Dialog Overlay
    @FXML private StackPane dialogOverlay;
    @FXML private VBox dialogContainer;

    // DAO
    private DAODosen daoDosen;
    private DAOMahasiswa daoMahasiswa;
    private DAORekomendasi daoRekomendasi;
    private DAOMataKuliah daoMataKuliah;
    private DAOTopik daoTopik;
    
    private Dosen dosenLogin;
    private ObservableList<Mahasiswa> dataMahasiswa;
    private ObservableList<Rekomendasi> dataRekomendasi;

    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ManajerBasisData manajerDB = ManajerBasisData.dapatkanInstans();
        daoDosen = new DAODosen(manajerDB);
        daoMahasiswa = new DAOMahasiswa(manajerDB);
        daoRekomendasi = new DAORekomendasi(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        daoTopik = new DAOTopik(manajerDB);

        setupTabNavigation();
        setupFilters();
        setupTabelMahasiswa();
        setupTabelRekomendasi();
        setupTombolAksi();
        
        muatDataDosen();
        muatDataMahasiswa();
        muatDataRekomendasi();
    }

    // ==================== Tab Navigation ====================

    private void setupTabNavigation() {
        tabGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == tabMahasiswa) {
                tampilkanPanel(panelMahasiswa);
            } else if (newVal == tabRekomendasi) {
                tampilkanPanel(panelRekomendasi);
                muatDataRekomendasi();
            }
        });
    }

    private void tampilkanPanel(VBox panel) {
        panelMahasiswa.setVisible(panel == panelMahasiswa);
        panelMahasiswa.setManaged(panel == panelMahasiswa);
        panelRekomendasi.setVisible(panel == panelRekomendasi);
        panelRekomendasi.setManaged(panel == panelRekomendasi);
    }

    private void setupTombolAksi() {
        // Tombol refresh
        FontIcon ikonRefresh = PembuatIkon.buat(Material2OutlinedMZ.REFRESH, 16);
        btnRefresh.setGraphic(ikonRefresh);
        
        // Tombol tambah rekomendasi
        FontIcon ikonTambah = PembuatIkon.buat(Material2OutlinedAL.ADD, 16);
        btnTambahRekomendasi.setGraphic(ikonTambah);
        btnTambahRekomendasi.setOnAction(e -> tampilkanDialogTambahRekomendasi(null));
    }

    private void setupFilters() {
        // Filter mahasiswa
        filterStatus.setItems(FXCollections.observableArrayList(
            "Semua", "Aktif (7 hari)", "Tidak Aktif", "Progress Rendah"
        ));
        filterStatus.setValue("Semua");
        filterStatus.setOnAction(e -> filterData());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());
        btnRefresh.setOnAction(e -> {
            muatDataDosen();
            muatDataMahasiswa();
            muatDataRekomendasi();
        });

        // Filter rekomendasi
        filterStatusRekomendasi.setItems(FXCollections.observableArrayList(
            "Semua Status", "Menunggu", "Diterima", "Ditolak"
        ));
        filterStatusRekomendasi.setValue("Semua Status");
        filterStatusRekomendasi.setOnAction(e -> filterDataRekomendasi());
    }

    private void setupTabelMahasiswa() {
        kolNama.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNama()));
        
        kolNim.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNim() != null ? data.getValue().getNim() : "-"));
        
        kolSemester.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getSemester())));
        
        kolMataKuliah.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getJumlahMataKuliah())));
        
        kolProgress.setCellValueFactory(data -> {
            double progress = data.getValue().getProgressKeseluruhan();
            return new SimpleStringProperty(String.format("%.1f%%", progress));
        });
        
        kolAktivitas.setCellValueFactory(data -> {
            LocalDate aktivitas = data.getValue().getAktivitasTerakhir();
            if (aktivitas != null) {
                return new SimpleStringProperty(aktivitas.format(FORMAT_TANGGAL));
            }
            return new SimpleStringProperty("-");
        });
        
        kolPerforma.setCellValueFactory(data -> {
            double performa = data.getValue().getRataRataPerforma();
            if (performa > 0) {
                return new SimpleStringProperty(String.format("%.1f/5.0", performa));
            }
            return new SimpleStringProperty("-");
        });
        
        kolStatusMhs.setCellValueFactory(data -> {
            Mahasiswa mhs = data.getValue();
            if (mhs.isAktifBelajar()) {
                return new SimpleStringProperty("Aktif");
            } else if (mhs.getAktivitasTerakhir() == null) {
                return new SimpleStringProperty("Belum Mulai");
            } else {
                return new SimpleStringProperty("Tidak Aktif");
            }
        });

        // Style status column
        kolStatusMhs.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Aktif".equals(item)) {
                        setStyle("-fx-text-fill: #22c55e;");
                    } else if ("Tidak Aktif".equals(item)) {
                        setStyle("-fx-text-fill: #ef4444;");
                    } else {
                        setStyle("-fx-text-fill: #f59e0b;");
                    }
                }
            }
        });

        setupKolomAksi();
        dataMahasiswa = FXCollections.observableArrayList();
        tabelMahasiswa.setItems(dataMahasiswa);
    }

    private void setupKolomAksi() {
        kolAksi.setCellFactory(col -> new TableCell<>() {
            private final Button btnDetail = new Button();
            private final Button btnRekomendasi = new Button();
            private final Button btnExport = new Button();
            private final HBox container = new HBox(4, btnDetail, btnRekomendasi, btnExport);

            {
                // Tombol Detail
                btnDetail.setGraphic(PembuatIkon.buat(Material2OutlinedAL.INFO, 14));
                btnDetail.setTooltip(new Tooltip("Lihat Detail"));
                btnDetail.getStyleClass().add("btn-icon");
                btnDetail.setOnAction(e -> {
                    Mahasiswa mhs = getTableView().getItems().get(getIndex());
                    tampilkanDetailMahasiswa(mhs);
                });

                // Tombol Rekomendasi
                btnRekomendasi.setGraphic(PembuatIkon.buat(Material2OutlinedAL.LIGHTBULB, 14));
                btnRekomendasi.setTooltip(new Tooltip("Beri Rekomendasi"));
                btnRekomendasi.getStyleClass().add("btn-icon");
                btnRekomendasi.setOnAction(e -> {
                    Mahasiswa mhs = getTableView().getItems().get(getIndex());
                    KontrolerDosen.this.tampilkanDialogTambahRekomendasi(mhs);
                });

                // Tombol Export
                btnExport.setGraphic(PembuatIkon.buat(Material2OutlinedAL.GET_APP, 14));
                btnExport.setTooltip(new Tooltip("Export Laporan"));
                btnExport.getStyleClass().add("btn-icon");
                btnExport.setOnAction(e -> {
                    Mahasiswa mhs = getTableView().getItems().get(getIndex());
                    KontrolerDosen.this.eksporLaporanMahasiswa(mhs);
                });

                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void muatDataDosen() {
        try {
            int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
            if (userId > 0) {
                dosenLogin = daoDosen.ambilBerdasarkanUserId(userId);
                if (dosenLogin != null) {
                    labelNamaDosen.setText(dosenLogin.getNama() + " (NIP: " + 
                        (dosenLogin.getNip() != null ? dosenLogin.getNip() : "-") + ")");
                }
            }
        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat data dosen: " + e.getMessage());
        }
    }

    private void muatDataMahasiswa() {
        if (dosenLogin == null) {
            labelTotalMahasiswa.setText("0");
            labelAktifMingguIni.setText("0");
            labelRataProgress.setText("0%");
            labelPerluPerhatian.setText("0");
            return;
        }

        try {
            List<Mahasiswa> mahasiswaBimbingan = daoMahasiswa.ambilBimbinganDenganStatistik(dosenLogin.getId());
            dataMahasiswa.setAll(mahasiswaBimbingan);
            
            // Hitung statistik
            int total = mahasiswaBimbingan.size();
            long aktif = mahasiswaBimbingan.stream().filter(Mahasiswa::isAktifBelajar).count();
            double rataProgress = mahasiswaBimbingan.stream()
                .mapToDouble(Mahasiswa::getProgressKeseluruhan)
                .average()
                .orElse(0);
            long perluPerhatian = mahasiswaBimbingan.stream()
                .filter(m -> !m.isAktifBelajar() || m.getProgressKeseluruhan() < 30)
                .count();

            labelTotalMahasiswa.setText(String.valueOf(total));
            labelAktifMingguIni.setText(String.valueOf(aktif));
            labelRataProgress.setText(String.format("%.1f%%", rataProgress));
            labelPerluPerhatian.setText(String.valueOf(perluPerhatian));

        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat data mahasiswa: " + e.getMessage());
        }
    }

    private void filterData() {
        if (dosenLogin == null) return;

        try {
            List<Mahasiswa> mahasiswaBimbingan = daoMahasiswa.ambilBimbinganDenganStatistik(dosenLogin.getId());
            String filterValue = filterStatus.getValue();
            String search = searchField.getText().toLowerCase();

            List<Mahasiswa> filtered = mahasiswaBimbingan.stream()
                .filter(m -> {
                    if ("Aktif (7 hari)".equals(filterValue)) {
                        return m.isAktifBelajar();
                    } else if ("Tidak Aktif".equals(filterValue)) {
                        return !m.isAktifBelajar();
                    } else if ("Progress Rendah".equals(filterValue)) {
                        return m.getProgressKeseluruhan() < 30;
                    }
                    return true;
                })
                .filter(m -> {
                    if (!search.isEmpty()) {
                        String nama = m.getNama() != null ? m.getNama().toLowerCase() : "";
                        String nim = m.getNim() != null ? m.getNim().toLowerCase() : "";
                        return nama.contains(search) || nim.contains(search);
                    }
                    return true;
                })
                .toList();

            dataMahasiswa.setAll(filtered);
        } catch (SQLException e) {
            PencatatLog.error("Gagal filter data: " + e.getMessage());
        }
    }

    // ==================== UC-20: Dashboard Progress Mahasiswa ====================

    private void tampilkanDetailMahasiswa(Mahasiswa mhs) {
        VBox konten = new VBox(16);
        konten.getStyleClass().add("spa-dialog-content");
        konten.setPadding(new Insets(20));

        // Header dengan info mahasiswa
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Avatar
        StackPane avatar = new StackPane();
        Circle circle = new Circle(30);
        circle.setFill(Color.web("#6366f1"));
        Label inisial = new Label(mhs.getNama().substring(0, 1).toUpperCase());
        inisial.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(circle, inisial);

        VBox infoMhs = new VBox(4);
        Label lblNama = new Label(mhs.getNama());
        lblNama.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblNim = new Label("NIM: " + (mhs.getNim() != null ? mhs.getNim() : "-") + " | Semester " + mhs.getSemester());
        lblNim.setStyle("-fx-text-fill: #64748b;");
        infoMhs.getChildren().addAll(lblNama, lblNim);
        header.getChildren().addAll(avatar, infoMhs);

        // Statistik Cards
        HBox statCards = new HBox(12);
        statCards.getChildren().addAll(
            buatStatCardKecil("Mata Kuliah", String.valueOf(mhs.getJumlahMataKuliah()), Material2OutlinedAL.BOOK, "#3b82f6"),
            buatStatCardKecil("Total Topik", String.valueOf(mhs.getJumlahTopik()), Material2OutlinedAL.LIBRARY_BOOKS, "#8b5cf6"),
            buatStatCardKecil("Dikuasai", String.valueOf(mhs.getTopikDikuasai()), Material2OutlinedAL.CHECK_CIRCLE, "#22c55e"),
            buatStatCardKecil("Progress", String.format("%.0f%%", mhs.getProgressKeseluruhan()), Material2OutlinedMZ.TRENDING_UP, "#f59e0b")
        );

        // Progress Bar
        VBox progressSection = new VBox(8);
        Label lblProgress = new Label("Progress Keseluruhan");
        lblProgress.setStyle("-fx-font-weight: bold;");
        ProgressBar progressBar = new ProgressBar(mhs.getProgressKeseluruhan() / 100.0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #22c55e;");
        progressSection.getChildren().addAll(lblProgress, progressBar);

        // Info Performa
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(16);
        infoGrid.setVgap(8);
        infoGrid.addRow(0, new Label("Rata-rata Performa:"), new Label(String.format("%.1f / 5.0", mhs.getRataRataPerforma())));
        infoGrid.addRow(1, new Label("Aktivitas Terakhir:"), new Label(mhs.getAktivitasTerakhir() != null ? mhs.getAktivitasTerakhir().format(FORMAT_TANGGAL) : "Belum ada aktivitas"));
        infoGrid.addRow(2, new Label("Status:"), buatLabelStatus(mhs.isAktifBelajar() ? "Aktif" : "Tidak Aktif"));

        // Tombol aksi
        HBox tombolContainer = new HBox(12);
        tombolContainer.setAlignment(Pos.CENTER_RIGHT);

        Button btnRekomendasi = new Button("Beri Rekomendasi");
        btnRekomendasi.setGraphic(PembuatIkon.buat(Material2OutlinedAL.LIGHTBULB, 16));
        btnRekomendasi.getStyleClass().addAll("spa-btn", "spa-btn-secondary");
        btnRekomendasi.setOnAction(e -> {
            tutupDialogSPA();
            tampilkanDialogTambahRekomendasi(mhs);
        });

        Button btnExport = new Button("Export Laporan");
        btnExport.setGraphic(PembuatIkon.buat(Material2OutlinedAL.GET_APP, 16));
        btnExport.getStyleClass().addAll("spa-btn", "spa-btn-primary");
        btnExport.setOnAction(e -> {
            tutupDialogSPA();
            eksporLaporanMahasiswa(mhs);
        });

        Button btnTutup = new Button("Tutup");
        btnTutup.getStyleClass().addAll("spa-btn", "spa-btn-secondary");
        btnTutup.setOnAction(e -> tutupDialogSPA());

        tombolContainer.getChildren().addAll(btnRekomendasi, btnExport, btnTutup);

        konten.getChildren().addAll(header, new Separator(), statCards, progressSection, infoGrid, tombolContainer);

        tampilkanDialogSPA("Detail Progress Mahasiswa", konten);
    }

    private VBox buatStatCardKecil(String judul, String nilai, org.kordamp.ikonli.Ikon ikon, String warna) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8; " +
                      "-fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(100);

        FontIcon icon = PembuatIkon.buat(ikon, 24, warna);
        Label lblNilai = new Label(nilai);
        lblNilai.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + warna + ";");
        Label lblJudul = new Label(judul);
        lblJudul.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(icon, lblNilai, lblJudul);
        return card;
    }

    private Label buatLabelStatus(String status) {
        Label label = new Label(status);
        if ("Aktif".equals(status)) {
            label.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 4 8; -fx-background-radius: 4;");
        } else {
            label.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 4 8; -fx-background-radius: 4;");
        }
        return label;
    }

    // ==================== UC-22: Rekomendasi Topik ====================

    private void setupTabelRekomendasi() {
        kolRekMahasiswa.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNamaMahasiswa()));
        
        kolRekTopik.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNamaTopik()));
        
        kolRekMataKuliah.setCellValueFactory(data -> {
            String mk = data.getValue().getNamaMataKuliah();
            return new SimpleStringProperty(mk != null ? mk : "-");
        });
        
        kolRekPrioritas.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getPrioritasSaran())));
        
        kolRekKesulitan.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getKesulitanSaran())));
        
        kolRekStatus.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().getNamaDisplay()));
        
        kolRekTanggal.setCellValueFactory(data -> {
            LocalDateTime dibuat = data.getValue().getDibuatPada();
            return new SimpleStringProperty(dibuat != null ? dibuat.format(FORMAT_WAKTU) : "-");
        });

        // Style status column
        kolRekStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Menunggu" -> setStyle("-fx-text-fill: #f59e0b;");
                        case "Diterima" -> setStyle("-fx-text-fill: #22c55e;");
                        case "Ditolak" -> setStyle("-fx-text-fill: #ef4444;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // Aksi kolom
        kolRekAksi.setCellFactory(col -> new TableCell<>() {
            private final Button btnHapus = new Button();

            {
                btnHapus.setGraphic(PembuatIkon.buat(Material2OutlinedAL.DELETE, 14));
                btnHapus.setTooltip(new Tooltip("Hapus"));
                btnHapus.getStyleClass().add("btn-icon-danger");
                btnHapus.setOnAction(e -> {
                    Rekomendasi rek = getTableView().getItems().get(getIndex());
                    hapusRekomendasi(rek);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnHapus);
            }
        });

        dataRekomendasi = FXCollections.observableArrayList();
        tabelRekomendasi.setItems(dataRekomendasi);
    }

    private void muatDataRekomendasi() {
        if (dosenLogin == null) {
            labelTotalRekomendasi.setText("0");
            return;
        }

        try {
            List<Rekomendasi> daftarRekomendasi = daoRekomendasi.ambilBerdasarkanDosenId(dosenLogin.getId());
            dataRekomendasi.setAll(daftarRekomendasi);
            labelTotalRekomendasi.setText(String.valueOf(daftarRekomendasi.size()));
        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat data rekomendasi: " + e.getMessage());
        }
    }

    private void filterDataRekomendasi() {
        if (dosenLogin == null) return;

        try {
            List<Rekomendasi> daftarRekomendasi = daoRekomendasi.ambilBerdasarkanDosenId(dosenLogin.getId());
            String filterValue = filterStatusRekomendasi.getValue();

            List<Rekomendasi> filtered = daftarRekomendasi.stream()
                .filter(r -> {
                    if ("Menunggu".equals(filterValue)) {
                        return r.getStatus() == StatusRekomendasi.PENDING;
                    } else if ("Diterima".equals(filterValue)) {
                        return r.getStatus() == StatusRekomendasi.ACCEPTED;
                    } else if ("Ditolak".equals(filterValue)) {
                        return r.getStatus() == StatusRekomendasi.DECLINED;
                    }
                    return true;
                })
                .toList();

            dataRekomendasi.setAll(filtered);
        } catch (SQLException e) {
            PencatatLog.error("Gagal filter rekomendasi: " + e.getMessage());
        }
    }

    private void tampilkanDialogTambahRekomendasi(Mahasiswa mahasiswaTerpilih) {
        VBox konten = new VBox(16);
        konten.getStyleClass().add("spa-dialog-content");

        // Pilih Mahasiswa
        ComboBox<Mahasiswa> comboMahasiswa = new ComboBox<>();
        comboMahasiswa.setPromptText("Pilih Mahasiswa");
        comboMahasiswa.setMaxWidth(Double.MAX_VALUE);
        comboMahasiswa.getStyleClass().add("spa-combo");
        comboMahasiswa.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Mahasiswa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNama() + " (" + item.getNim() + ")");
            }
        });
        comboMahasiswa.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Mahasiswa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNama() + " (" + item.getNim() + ")");
            }
        });
        comboMahasiswa.setItems(dataMahasiswa);
        if (mahasiswaTerpilih != null) {
            comboMahasiswa.setValue(mahasiswaTerpilih);
        }

        // Pilih Mata Kuliah (opsional)
        ComboBox<MataKuliah> comboMataKuliah = new ComboBox<>();
        comboMataKuliah.setPromptText("Pilih Mata Kuliah (Opsional)");
        comboMataKuliah.setMaxWidth(Double.MAX_VALUE);
        comboMataKuliah.getStyleClass().add("spa-combo");
        comboMataKuliah.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MataKuliah item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNama());
            }
        });
        comboMataKuliah.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MataKuliah item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNama());
            }
        });

        // Update mata kuliah saat mahasiswa dipilih
        comboMahasiswa.setOnAction(e -> {
            Mahasiswa mhs = comboMahasiswa.getValue();
            if (mhs != null) {
                try {
                    List<MataKuliah> mataKuliahList = daoMataKuliah.ambilSemuaByUserId(mhs.getUserId());
                    comboMataKuliah.setItems(FXCollections.observableArrayList(mataKuliahList));
                } catch (SQLException ex) {
                    PencatatLog.error("Gagal memuat mata kuliah: " + ex.getMessage());
                }
            }
        });

        // Trigger update jika sudah ada mahasiswa terpilih
        if (mahasiswaTerpilih != null) {
            try {
                List<MataKuliah> mataKuliahList = daoMataKuliah.ambilSemuaByUserId(mahasiswaTerpilih.getUserId());
                comboMataKuliah.setItems(FXCollections.observableArrayList(mataKuliahList));
            } catch (SQLException ex) {
                PencatatLog.error("Gagal memuat mata kuliah: " + ex.getMessage());
            }
        }

        // Nama Topik
        TextField inputNamaTopik = new TextField();
        inputNamaTopik.setPromptText("Nama Topik yang Direkomendasikan");
        inputNamaTopik.getStyleClass().add("spa-input");

        // Deskripsi
        TextArea inputDeskripsi = new TextArea();
        inputDeskripsi.setPromptText("Deskripsi/Catatan untuk mahasiswa...");
        inputDeskripsi.getStyleClass().add("spa-input");
        inputDeskripsi.setPrefRowCount(3);

        // Prioritas dan Kesulitan
        HBox sliderRow = new HBox(24);
        VBox prioritasBox = new VBox(4);
        Label lblPrioritas = new Label("Prioritas Saran: 3");
        Slider sliderPrioritas = new Slider(1, 5, 3);
        sliderPrioritas.setMajorTickUnit(1);
        sliderPrioritas.setMinorTickCount(0);
        sliderPrioritas.setSnapToTicks(true);
        sliderPrioritas.setShowTickLabels(true);
        sliderPrioritas.valueProperty().addListener((obs, oldVal, newVal) -> 
            lblPrioritas.setText("Prioritas Saran: " + newVal.intValue()));
        prioritasBox.getChildren().addAll(lblPrioritas, sliderPrioritas);
        HBox.setHgrow(prioritasBox, Priority.ALWAYS);

        VBox kesulitanBox = new VBox(4);
        Label lblKesulitan = new Label("Kesulitan Saran: 3");
        Slider sliderKesulitan = new Slider(1, 5, 3);
        sliderKesulitan.setMajorTickUnit(1);
        sliderKesulitan.setMinorTickCount(0);
        sliderKesulitan.setSnapToTicks(true);
        sliderKesulitan.setShowTickLabels(true);
        sliderKesulitan.valueProperty().addListener((obs, oldVal, newVal) -> 
            lblKesulitan.setText("Kesulitan Saran: " + newVal.intValue()));
        kesulitanBox.getChildren().addAll(lblKesulitan, sliderKesulitan);
        HBox.setHgrow(kesulitanBox, Priority.ALWAYS);

        sliderRow.getChildren().addAll(prioritasBox, kesulitanBox);

        // URL Sumber (opsional)
        TextField inputUrl = new TextField();
        inputUrl.setPromptText("URL Sumber Belajar (opsional)");
        inputUrl.getStyleClass().add("spa-input");

        konten.getChildren().addAll(
            buatFieldGroup("Mahasiswa", comboMahasiswa),
            buatFieldGroup("Mata Kuliah", comboMataKuliah),
            buatFieldGroup("Nama Topik", inputNamaTopik),
            buatFieldGroup("Deskripsi", inputDeskripsi),
            sliderRow,
            buatFieldGroup("URL Sumber", inputUrl)
        );

        // Tombol
        HBox tombolContainer = new HBox(12);
        tombolContainer.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().addAll("spa-btn", "spa-btn-secondary");
        btnBatal.setOnAction(e -> tutupDialogSPA());

        Button btnSimpan = new Button("Kirim Rekomendasi");
        btnSimpan.setGraphic(PembuatIkon.buat(Material2OutlinedMZ.SEND, 16));
        btnSimpan.getStyleClass().addAll("spa-btn", "spa-btn-primary");
        btnSimpan.setOnAction(e -> {
            if (comboMahasiswa.getValue() == null) {
                tampilkanError("Pilih mahasiswa terlebih dahulu!");
                return;
            }
            if (inputNamaTopik.getText().trim().isEmpty()) {
                tampilkanError("Nama topik tidak boleh kosong!");
                return;
            }

            try {
                Rekomendasi rek = new Rekomendasi();
                rek.setDosenId(dosenLogin.getId());
                rek.setMahasiswaId(comboMahasiswa.getValue().getId());
                if (comboMataKuliah.getValue() != null) {
                    rek.setIdMataKuliah(comboMataKuliah.getValue().getId());
                }
                rek.setNamaTopik(inputNamaTopik.getText().trim());
                rek.setDeskripsi(inputDeskripsi.getText().trim());
                rek.setPrioritasSaran((int) sliderPrioritas.getValue());
                rek.setKesulitanSaran((int) sliderKesulitan.getValue());
                rek.setUrlSumber(inputUrl.getText().trim());
                rek.setStatus(StatusRekomendasi.PENDING);

                daoRekomendasi.simpan(rek);
                tutupDialogSPA();
                tampilkanInfo("Rekomendasi berhasil dikirim ke " + comboMahasiswa.getValue().getNama());
                muatDataRekomendasi();
            } catch (SQLException ex) {
                tampilkanError("Gagal menyimpan rekomendasi: " + ex.getMessage());
            }
        });

        tombolContainer.getChildren().addAll(btnBatal, btnSimpan);
        konten.getChildren().add(tombolContainer);

        tampilkanDialogSPA("Tambah Rekomendasi Topik", konten);
    }

    private void hapusRekomendasi(Rekomendasi rek) {
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Hapus Rekomendasi");
        konfirmasi.setHeaderText("Hapus rekomendasi ini?");
        konfirmasi.setContentText("Rekomendasi: " + rek.getNamaTopik());

        konfirmasi.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    daoRekomendasi.hapus(rek.getId());
                    tampilkanInfo("Rekomendasi berhasil dihapus!");
                    muatDataRekomendasi();
                } catch (SQLException e) {
                    tampilkanError("Gagal menghapus rekomendasi: " + e.getMessage());
                }
            }
        });
    }

    private VBox buatFieldGroup(String label, javafx.scene.Node field) {
        VBox group = new VBox(6);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("spa-label");
        group.getChildren().addAll(lbl, field);
        return group;
    }

    // ==================== UC-23: Export Laporan Progress ====================

    private void eksporLaporanMahasiswa(Mahasiswa mhs) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Progress");
        fileChooser.setInitialFileName("Laporan_" + mhs.getNama().replaceAll("\\s+", "_") + "_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showSaveDialog(dialogOverlay.getScene().getWindow());
        if (file != null) {
            try {
                generasiLaporan(mhs, file);
                tampilkanInfo("Laporan berhasil disimpan ke:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                tampilkanError("Gagal menyimpan laporan: " + e.getMessage());
            }
        }
    }

    private void generasiLaporan(Mahasiswa mhs, File file) throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println("               LAPORAN PROGRESS PEMBELAJARAN MAHASISWA          ");
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println();
            writer.println("Tanggal Generate: " + LocalDateTime.now().format(FORMAT_WAKTU));
            writer.println("Dosen Pembimbing: " + dosenLogin.getNama());
            writer.println();
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("                      DATA MAHASISWA                            ");
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("Nama          : " + mhs.getNama());
            writer.println("NIM           : " + (mhs.getNim() != null ? mhs.getNim() : "-"));
            writer.println("Semester      : " + mhs.getSemester());
            writer.println("Email         : " + (mhs.getEmail() != null ? mhs.getEmail() : "-"));
            writer.println();
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("                   STATISTIK PEMBELAJARAN                       ");
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("Jumlah Mata Kuliah   : " + mhs.getJumlahMataKuliah());
            writer.println("Total Topik          : " + mhs.getJumlahTopik());
            writer.println("Topik Dikuasai       : " + mhs.getTopikDikuasai());
            writer.println("Progress Keseluruhan : " + String.format("%.1f%%", mhs.getProgressKeseluruhan()));
            writer.println("Rata-rata Performa   : " + String.format("%.1f / 5.0", mhs.getRataRataPerforma()));
            writer.println();
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("                       STATUS AKTIVITAS                         ");
            writer.println("───────────────────────────────────────────────────────────────");
            writer.println("Aktivitas Terakhir : " + (mhs.getAktivitasTerakhir() != null ? 
                mhs.getAktivitasTerakhir().format(FORMAT_TANGGAL) : "Belum ada aktivitas"));
            writer.println("Status             : " + (mhs.isAktifBelajar() ? "AKTIF" : "TIDAK AKTIF"));
            writer.println();

            // Detail Mata Kuliah jika ada
            try {
                List<MataKuliah> mataKuliahList = daoMataKuliah.ambilSemuaByUserId(mhs.getUserId());
                if (!mataKuliahList.isEmpty()) {
                    writer.println("───────────────────────────────────────────────────────────────");
                    writer.println("                    DETAIL MATA KULIAH                          ");
                    writer.println("───────────────────────────────────────────────────────────────");
                    for (MataKuliah mk : mataKuliahList) {
                        writer.println();
                        writer.println("  [" + mk.getKode() + "] " + mk.getNama());
                        List<Topik> topikList = daoTopik.ambilBerdasarkanMataKuliahId(mk.getId());
                        int totalTopik = topikList.size();
                        long dikuasai = topikList.stream().filter(Topik::isDikuasai).count();
                        writer.println("    - Total Topik: " + totalTopik + ", Dikuasai: " + dikuasai);
                    }
                }
            } catch (SQLException e) {
                writer.println("  (Gagal memuat detail mata kuliah)");
            }

            // Riwayat Rekomendasi
            try {
                List<Rekomendasi> rekomendasiList = daoRekomendasi.ambilBerdasarkanMahasiswaId(mhs.getId());
                if (!rekomendasiList.isEmpty()) {
                    writer.println();
                    writer.println("───────────────────────────────────────────────────────────────");
                    writer.println("                    RIWAYAT REKOMENDASI                         ");
                    writer.println("───────────────────────────────────────────────────────────────");
                    for (Rekomendasi rek : rekomendasiList) {
                        writer.println();
                        writer.println("  Topik     : " + rek.getNamaTopik());
                        writer.println("  Status    : " + rek.getStatus().getNamaDisplay());
                        writer.println("  Prioritas : " + rek.getPrioritasSaran() + "/5");
                        writer.println("  Tanggal   : " + (rek.getDibuatPada() != null ? 
                            rek.getDibuatPada().format(FORMAT_WAKTU) : "-"));
                    }
                }
            } catch (SQLException e) {
                writer.println("  (Gagal memuat riwayat rekomendasi)");
            }

            writer.println();
            writer.println("═══════════════════════════════════════════════════════════════");
            writer.println("                      AKHIR LAPORAN                             ");
            writer.println("═══════════════════════════════════════════════════════════════");
        }
    }

    // ==================== SPA Dialog Methods ====================

    private void tampilkanDialogSPA(String judul, VBox konten) {
        if (dialogOverlay == null || dialogContainer == null) return;

        Label labelJudul = new Label(judul);
        labelJudul.getStyleClass().add("spa-dialog-title");

        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().addAll(labelJudul, konten);

        dialogOverlay.setVisible(true);
        dialogOverlay.setManaged(true);
        dialogOverlay.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        dialogOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == dialogOverlay) {
                tutupDialogSPA();
            }
        });
    }

    private void tutupDialogSPA() {
        if (dialogOverlay == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), dialogOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            dialogOverlay.setVisible(false);
            dialogOverlay.setManaged(false);
        });
        fadeOut.play();
    }

    // ==================== Helper Methods ====================

    private void tampilkanInfo(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanError(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
