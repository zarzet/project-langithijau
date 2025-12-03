package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAODosen;
import com.studyplanner.dao.DAOMahasiswa;
import com.studyplanner.model.Dosen;
import com.studyplanner.model.Mahasiswa;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PencatatLog;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk Dashboard Dosen Pembimbing.
 * Menangani UC-19 (Lihat Daftar Mahasiswa Bimbingan) dan UC-20 (Lihat Progress Mahasiswa).
 */
public class KontrolerDosen implements Initializable {

    // Header
    @FXML private Label labelNamaDosen;

    // Statistik
    @FXML private Label labelTotalMahasiswa;
    @FXML private Label labelAktifMingguIni;
    @FXML private Label labelRataProgress;
    @FXML private Label labelPerluPerhatian;

    // Filters
    @FXML private ComboBox<String> filterStatus;
    @FXML private TextField searchField;
    @FXML private Button btnRefresh;

    // Tabel Mahasiswa
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

    private DAODosen daoDosen;
    private DAOMahasiswa daoMahasiswa;
    private Dosen dosenLogin;
    private ObservableList<Mahasiswa> dataMahasiswa;

    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ManajerBasisData manajerDB = ManajerBasisData.dapatkanInstans();
        daoDosen = new DAODosen(manajerDB);
        daoMahasiswa = new DAOMahasiswa(manajerDB);

        setupFilters();
        setupTabelMahasiswa();
        
        muatDataDosen();
        muatDataMahasiswa();
    }

    private void setupFilters() {
        filterStatus.setItems(FXCollections.observableArrayList(
            "Semua", "Aktif (7 hari)", "Tidak Aktif", "Progress Rendah"
        ));
        filterStatus.setValue("Semua");
        filterStatus.setOnAction(e -> filterData());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());
        btnRefresh.setOnAction(e -> {
            muatDataDosen();
            muatDataMahasiswa();
        });
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
            private final Button btnDetail = new Button("Detail");
            private final HBox container = new HBox(4, btnDetail);

            {
                btnDetail.getStyleClass().add("btn-small");
                btnDetail.setOnAction(e -> {
                    Mahasiswa mhs = getTableView().getItems().get(getIndex());
                    tampilkanDetailMahasiswa(mhs);
                });
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

    private void tampilkanDetailMahasiswa(Mahasiswa mhs) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detail Mahasiswa");
        alert.setHeaderText(mhs.getNama());
        
        StringBuilder content = new StringBuilder();
        content.append("NIM: ").append(mhs.getNim() != null ? mhs.getNim() : "-").append("\n");
        content.append("Semester: ").append(mhs.getSemester()).append("\n");
        content.append("Email: ").append(mhs.getEmail() != null ? mhs.getEmail() : "-").append("\n\n");
        
        content.append("=== Statistik Belajar ===\n");
        content.append("Mata Kuliah: ").append(mhs.getJumlahMataKuliah()).append("\n");
        content.append("Total Topik: ").append(mhs.getJumlahTopik()).append("\n");
        content.append("Topik Dikuasai: ").append(mhs.getTopikDikuasai()).append("\n");
        content.append("Progress: ").append(String.format("%.1f%%", mhs.getProgressKeseluruhan())).append("\n");
        content.append("Rata-rata Performa: ").append(String.format("%.1f/5.0", mhs.getRataRataPerforma())).append("\n\n");
        
        content.append("=== Aktivitas ===\n");
        content.append("Terakhir Aktif: ");
        if (mhs.getAktivitasTerakhir() != null) {
            content.append(mhs.getAktivitasTerakhir().format(FORMAT_TANGGAL));
        } else {
            content.append("Belum ada aktivitas");
        }
        content.append("\n");
        content.append("Status: ").append(mhs.isAktifBelajar() ? "Aktif" : "Tidak Aktif");
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
}
