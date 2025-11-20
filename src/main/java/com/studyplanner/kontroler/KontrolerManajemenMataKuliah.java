package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOMataKuliah;
import com.studyplanner.dao.DAOTopik;
import com.studyplanner.dao.DAOJadwalUjian;
import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.JadwalUjian;
import com.studyplanner.model.Topik;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.PembuatIkon;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class KontrolerManajemenMataKuliah implements Initializable {

    @FXML
    private TableView<MataKuliah> tabelMataKuliah;

    @FXML
    private TableColumn<MataKuliah, String> kolomKodeMataKuliah;

    @FXML
    private TableColumn<MataKuliah, String> kolomNamaMataKuliah;

    @FXML
    private TableView<Topik> tabelTopik;

    @FXML
    private TableColumn<Topik, String> kolomNamaTopik;

    @FXML
    private TableColumn<Topik, Integer> kolomPrioritasTopik;

    @FXML
    private TableColumn<Topik, Integer> kolomKesulitanTopik;

    @FXML
    private TableColumn<Topik, Integer> kolomJumlahUlasanTopik;

    @FXML
    private TableView<JadwalUjian> tabelUjian;

    @FXML
    private TableColumn<JadwalUjian, String> kolomJudulUjian;

    @FXML
    private TableColumn<JadwalUjian, String> kolomTipeUjian;

    @FXML
    private TableColumn<JadwalUjian, LocalDate> kolomTanggalUjian;

    @FXML
    private Button addCourseBtn;

    @FXML
    private Button editCourseBtn;

    @FXML
    private Button deleteCourseBtn;

    @FXML
    private Button addTopicBtn;

    @FXML
    private Button editTopicBtn;

    @FXML
    private Button deleteTopicBtn;

    @FXML
    private Button addExamBtn;

    @FXML
    private Button editExamBtn;

    @FXML
    private Button deleteExamBtn;

    private ManajerBasisData manajerBasisData;
    private DAOMataKuliah daoMataKuliah;
    private DAOTopik daoTopik;
    private DAOJadwalUjian daoJadwalUjian;
    private KontrolerUtama kontrolerUtama;
    private ObservableList<MataKuliah> daftarMataKuliah;
    private ObservableList<Topik> daftarTopik;
    private ObservableList<JadwalUjian> daftarUjian;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();
        inisialisasiDAO();

        aturTabel();
        aturTombol();
        muatMataKuliah();
    }

    public void aturKontrolerUtama(KontrolerUtama kontroler) {
        this.kontrolerUtama = kontroler;
        if (this.kontrolerUtama != null) {
            this.manajerBasisData = this.kontrolerUtama.getManajerBasisData();
            inisialisasiDAO();
        }
    }

    private void inisialisasiDAO() {
        this.daoMataKuliah = new DAOMataKuliah(manajerBasisData);
        this.daoTopik = new DAOTopik(manajerBasisData);
        this.daoJadwalUjian = new DAOJadwalUjian(manajerBasisData);
    }

    private void aturTabel() {
        tabelMataKuliah.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        kolomKodeMataKuliah.setCellValueFactory(
                new PropertyValueFactory<>("kode"));
        kolomNamaMataKuliah.setCellValueFactory(
                new PropertyValueFactory<>("nama"));

        tabelTopik.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tabelMataKuliah
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, pilihan) -> {
                    if (pilihan != null) {
                        muatTopik(pilihan.getId());
                        muatUjian(pilihan.getId());
                    }
                });

        kolomNamaTopik.setCellValueFactory(new PropertyValueFactory<>("nama"));
        kolomPrioritasTopik.setCellValueFactory(
                new PropertyValueFactory<>("prioritas"));
        kolomKesulitanTopik.setCellValueFactory(
                new PropertyValueFactory<>("tingkatKesulitan"));
        kolomJumlahUlasanTopik.setCellValueFactory(
                new PropertyValueFactory<>("jumlahUlasan"));

        tabelUjian.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        kolomJudulUjian.setCellValueFactory(
                new PropertyValueFactory<>("judul"));
        kolomTipeUjian.setCellValueFactory(
                new PropertyValueFactory<>("tipeUjian"));
        kolomTanggalUjian.setCellValueFactory(
                new PropertyValueFactory<>("tanggalUjian"));
    }

    private void aturTombol() {
        // Setup ikon untuk buttons
        addCourseBtn.setGraphic(PembuatIkon.ikonTambah());
        editCourseBtn.setGraphic(PembuatIkon.ikonEdit());
        deleteCourseBtn.setGraphic(PembuatIkon.ikonHapus());

        addTopicBtn.setGraphic(PembuatIkon.ikonTambah());
        editTopicBtn.setGraphic(PembuatIkon.ikonEdit());
        deleteTopicBtn.setGraphic(PembuatIkon.ikonHapus());

        addExamBtn.setGraphic(PembuatIkon.ikonTambah());
        editExamBtn.setGraphic(PembuatIkon.ikonEdit());
        deleteExamBtn.setGraphic(PembuatIkon.ikonHapus());

        // Setup action handlers
        addCourseBtn.setOnAction(_ -> tambahMataKuliah());
        editCourseBtn.setOnAction(_ -> editMataKuliah());
        deleteCourseBtn.setOnAction(_ -> hapusMataKuliah());

        addTopicBtn.setOnAction(_ -> tambahTopik());
        editTopicBtn.setOnAction(_ -> editTopik());
        deleteTopicBtn.setOnAction(_ -> hapusTopik());

        addExamBtn.setOnAction(_ -> tambahUjian());
        editExamBtn.setOnAction(_ -> editUjian());
        deleteExamBtn.setOnAction(_ -> hapusUjian());
    }

    private void muatMataKuliah() {
        try {
            List<MataKuliah> daftarMK = daoMataKuliah.ambilSemua();
            daftarMataKuliah = FXCollections.observableArrayList(daftarMK);
            tabelMataKuliah.setItems(daftarMataKuliah);
        } catch (SQLException e) {
            tampilkanKesalahan("Gagal memuat daftar mata kuliah: " + e.getMessage());
        }
    }

    private void muatTopik(int idMataKuliah) {
        try {
            List<Topik> daftarTop = daoTopik.ambilBerdasarkanMataKuliahId(idMataKuliah);
            daftarTopik = FXCollections.observableArrayList(daftarTop);
            tabelTopik.setItems(daftarTopik);
        } catch (SQLException e) {
            tampilkanKesalahan("Gagal memuat topik: " + e.getMessage());
        }
    }

    private void muatUjian(int idMataKuliah) {
        try {
            List<JadwalUjian> daftarUj = daoJadwalUjian.ambilBerdasarkanMataKuliahId(idMataKuliah);
            daftarUjian = FXCollections.observableArrayList(daftarUj);
            tabelUjian.setItems(daftarUjian);
        } catch (SQLException e) {
            tampilkanKesalahan("Gagal memuat ujian: " + e.getMessage());
        }
    }

    private void tambahMataKuliah() {
        Dialog<MataKuliah> dialog = PembuatDialogMD3.buatDialog("Tambah Mata Kuliah", "Masukkan data mata kuliah baru");

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField();
        codeField.setPromptText("Kode MK (contoh: CS101)");
        TextField nameField = new TextField();
        nameField.setPromptText("Nama Mata Kuliah");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Deskripsi (opsional)");
        descArea.setPrefRowCount(3);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Kode:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Deskripsi:"), 0, 2);
        grid.add(descArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                MataKuliah course = new MataKuliah();
                course.setKode(codeField.getText());
                course.setNama(nameField.getText());
                course.setDeskripsi(descArea.getText());
                return course;
            }
            return null;
        });

        Optional<MataKuliah> result = dialog.showAndWait();
        result.ifPresent(mataKuliah -> {
            try {
                daoMataKuliah.simpan(mataKuliah);
                muatMataKuliah();
                tampilkanInfo("Mata kuliah berhasil ditambahkan!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal menambahkan mata kuliah: " + e.getMessage());
            }
        });
    }

    private void editMataKuliah() {
        MataKuliah terpilih = tabelMataKuliah.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanPeringatan("Pilih mata kuliah yang akan diedit!");
            return;
        }

        Dialog<MataKuliah> dialog = PembuatDialogMD3.buatDialog("Edit Mata Kuliah", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField(terpilih.getKode());
        TextField nameField = new TextField(terpilih.getNama());
        TextArea descArea = new TextArea(terpilih.getDeskripsi());
        descArea.setPrefRowCount(3);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Kode:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Deskripsi:"), 0, 2);
        grid.add(descArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                terpilih.setKode(codeField.getText());
                terpilih.setNama(nameField.getText());
                terpilih.setDeskripsi(descArea.getText());
                return terpilih;
            }
            return null;
        });

        Optional<MataKuliah> result = dialog.showAndWait();
        result.ifPresent(mataKuliah -> {
            try {
                daoMataKuliah.perbarui(mataKuliah);
                muatMataKuliah();
                tampilkanInfo("Mata kuliah berhasil diupdate!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal memperbarui mata kuliah: " + e.getMessage());
            }
        });
    }

    private void hapusMataKuliah() {
        MataKuliah terpilih = tabelMataKuliah.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanPeringatan("Pilih mata kuliah yang akan dihapus!");
            return;
        }

        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus mata kuliah: " + terpilih.getNama() + "?",
                "Semua topik dan ujian terkait juga akan dihapus. Lanjutkan?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                daoMataKuliah.hapus(terpilih.getId());
                muatMataKuliah();
                tabelTopik.getItems().clear();
                tabelUjian.getItems().clear();
                tampilkanInfo("Mata kuliah berhasil dihapus!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal menghapus mata kuliah: " + e.getMessage());
            }
        }
    }

    private void tambahTopik() {
        MataKuliah mkTerpilih = tabelMataKuliah
                .getSelectionModel()
                .getSelectedItem();
        if (mkTerpilih == null) {
            tampilkanPeringatan("Pilih mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<Topik> dialog = PembuatDialogMD3.buatDialog("Tambah Topik", "Tambah topik untuk: " + mkTerpilih.getNama());

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Nama Topik");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Deskripsi");
        descArea.setPrefRowCount(2);

        Spinner<Integer> prioritySpinner = new Spinner<>(1, 5, 3);
        Spinner<Integer> difficultySpinner = new Spinner<>(1, 5, 3);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nama:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Deskripsi:"), 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(new Label("Prioritas (1-5):"), 0, 2);
        grid.add(prioritySpinner, 1, 2);
        grid.add(new Label("Kesulitan (1-5):"), 0, 3);
        grid.add(difficultySpinner, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Topik topik = new Topik();
                topik.setIdMataKuliah(mkTerpilih.getId());
                topik.setNama(nameField.getText());
                topik.setDeskripsi(descArea.getText());
                topik.setPrioritas(prioritySpinner.getValue());
                topik.setTingkatKesulitan(difficultySpinner.getValue());
                return topik;
            }
            return null;
        });

        Optional<Topik> result = dialog.showAndWait();
        result.ifPresent(topik -> {
            try {
                daoTopik.simpan(topik);
                muatTopik(mkTerpilih.getId());
                tampilkanInfo("Topik berhasil ditambahkan!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal menambahkan topik: " + e.getMessage());
            }
        });
    }

    private void editTopik() {
        Topik terpilih = tabelTopik.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanPeringatan("Pilih topik yang akan diedit!");
            return;
        }

        Dialog<Topik> dialog = PembuatDialogMD3.buatDialog("Edit Topik", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(terpilih.getNama());
        TextArea descArea = new TextArea(terpilih.getDeskripsi());
        descArea.setPrefRowCount(2);
        Spinner<Integer> prioritySpinner = new Spinner<>(
                1,
                5,
                terpilih.getPrioritas());
        Spinner<Integer> difficultySpinner = new Spinner<>(
                1,
                5,
                terpilih.getTingkatKesulitan());

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nama:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Deskripsi:"), 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(new Label("Prioritas:"), 0, 2);
        grid.add(prioritySpinner, 1, 2);
        grid.add(new Label("Kesulitan:"), 0, 3);
        grid.add(difficultySpinner, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                terpilih.setNama(nameField.getText());
                terpilih.setDeskripsi(descArea.getText());
                terpilih.setPrioritas(prioritySpinner.getValue());
                terpilih.setTingkatKesulitan(difficultySpinner.getValue());
                return terpilih;
            }
            return null;
        });

        Optional<Topik> result = dialog.showAndWait();
        result.ifPresent(topik -> {
            try {
                daoTopik.perbarui(topik);
                muatTopik(topik.getIdMataKuliah());
                tampilkanInfo("Topik berhasil diupdate!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal memperbarui topik: " + e.getMessage());
            }
        });
    }

    private void hapusTopik() {
        Topik terpilih = tabelTopik.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanPeringatan("Pilih topik yang akan dihapus!");
            return;
        }

        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus topik: " + terpilih.getNama() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int idMataKuliah = terpilih.getIdMataKuliah();
                daoTopik.hapus(terpilih.getId());
                muatTopik(idMataKuliah);
                tampilkanInfo("Topik berhasil dihapus!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal menghapus topik: " + e.getMessage());
            }
        }
    }

    private void tambahUjian() {
        MataKuliah mkTerpilih = tabelMataKuliah
                .getSelectionModel()
                .getSelectedItem();
        if (mkTerpilih == null) {
            tampilkanPeringatan("Pilih mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<JadwalUjian> dialog = PembuatDialogMD3.buatDialog("Tambah Jadwal Ujian", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        titleField.setPromptText("Judul (contoh: UTS)");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("UTS", "UAS", "Kuis", "Tugas");
        typeCombo.setValue("UTS");

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Judul:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Tipe:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Tanggal:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                JadwalUjian ujian = new JadwalUjian();
                ujian.setIdMataKuliah(mkTerpilih.getId());
                ujian.setJudul(titleField.getText());
                ujian.setTipeUjian(typeCombo.getValue());
                ujian.setTanggalUjian(datePicker.getValue());
                return ujian;
            }
            return null;
        });

        Optional<JadwalUjian> result = dialog.showAndWait();
        result.ifPresent(ujian -> {
            try {
                daoJadwalUjian.simpan(ujian);
                muatUjian(mkTerpilih.getId());
                tampilkanInfo("Jadwal ujian berhasil ditambahkan!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal menambahkan ujian: " + e.getMessage());
            }
        });
    }

    private void editUjian() {
        JadwalUjian terpilih = tabelUjian.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanPeringatan("Pilih ujian yang akan diedit!");
            return;
        }

        Dialog<JadwalUjian> dialog = PembuatDialogMD3.buatDialog("Edit Jadwal Ujian", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField(terpilih.getJudul());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("UTS", "UAS", "Kuis", "Tugas");
        typeCombo.setValue(terpilih.getTipeUjian());
        DatePicker datePicker = new DatePicker(terpilih.getTanggalUjian());

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Judul:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Tipe:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Tanggal:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                terpilih.setJudul(titleField.getText());
                terpilih.setTipeUjian(typeCombo.getValue());
                terpilih.setTanggalUjian(datePicker.getValue());
                return terpilih;
            }
            return null;
        });

        Optional<JadwalUjian> result = dialog.showAndWait();
        result.ifPresent(ujian -> {
            try {
                daoJadwalUjian.perbarui(ujian);
                muatUjian(ujian.getIdMataKuliah());
                tampilkanInfo("Jadwal ujian berhasil diupdate!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal memperbarui ujian: " + e.getMessage());
            }
        });
    }

    private void hapusUjian() {
        JadwalUjian terpilih = tabelUjian.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            tampilkanPeringatan("Pilih ujian yang akan dihapus!");
            return;
        }

        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus jadwal ujian: " + terpilih.getJudul() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int idMataKuliah = terpilih.getIdMataKuliah();
                daoJadwalUjian.hapus(terpilih.getId());
                muatUjian(idMataKuliah);
                tampilkanInfo("Jadwal ujian berhasil dihapus!");
            } catch (SQLException e) {
                tampilkanKesalahan("Gagal menghapus ujian: " + e.getMessage());
            }
        }
    }

    private void tampilkanKesalahan(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.ERROR, "Kesalahan", pesan);
        alert.showAndWait();
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.WARNING, "Peringatan", pesan);
        alert.showAndWait();
    }

    private void tampilkanInfo(String pesan) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.INFORMATION, "Informasi", pesan);
        alert.showAndWait();
    }
}
