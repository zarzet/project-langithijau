package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
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
    private KontrolerUtama kontrolerUtama;
    private ObservableList<MataKuliah> daftarMataKuliah;
    private ObservableList<Topik> daftarTopik;
    private ObservableList<JadwalUjian> daftarUjian;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();

        setupTables();
        setupButtons();
        loadCourses();
    }

    public void aturKontrolerUtama(KontrolerUtama kontroler) {
        this.kontrolerUtama = kontroler;
        if (this.kontrolerUtama != null) {
            this.manajerBasisData = this.kontrolerUtama.getManajerBasisData();
        }
    }

    private void setupTables() {
        tabelMataKuliah.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        kolomKodeMataKuliah.setCellValueFactory(
                new PropertyValueFactory<>("kode"));
        kolomNamaMataKuliah.setCellValueFactory(
                new PropertyValueFactory<>("nama"));

        tabelTopik.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tabelMataKuliah
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, newSelection) -> {
                    if (newSelection != null) {
                        loadTopics(newSelection.getId());
                        loadExams(newSelection.getId());
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

    private void setupButtons() {
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
        addCourseBtn.setOnAction(_ -> addCourse());
        editCourseBtn.setOnAction(_ -> editCourse());
        deleteCourseBtn.setOnAction(_ -> deleteCourse());

        addTopicBtn.setOnAction(_ -> addTopic());
        editTopicBtn.setOnAction(_ -> editTopic());
        deleteTopicBtn.setOnAction(_ -> deleteTopic());

        addExamBtn.setOnAction(_ -> addExam());
        editExamBtn.setOnAction(_ -> editExam());
        deleteExamBtn.setOnAction(_ -> deleteExam());
    }

    private void loadCourses() {
        try {
            List<MataKuliah> courseList = manajerBasisData.ambilSemuaMataKuliah();
            daftarMataKuliah = FXCollections.observableArrayList(courseList);
            tabelMataKuliah.setItems(daftarMataKuliah);
        } catch (SQLException e) {
            showError("Gagal memuat daftar mata kuliah: " + e.getMessage());
        }
    }

    private void loadTopics(int courseId) {
        try {
            List<Topik> topicList = manajerBasisData.ambilTopikBerdasarkanMataKuliah(courseId);
            daftarTopik = FXCollections.observableArrayList(topicList);
            tabelTopik.setItems(daftarTopik);
        } catch (SQLException e) {
            showError("Gagal memuat topik: " + e.getMessage());
        }
    }

    private void loadExams(int courseId) {
        try {
            List<JadwalUjian> examList = manajerBasisData.ambilUjianBerdasarkanMataKuliah(courseId);
            daftarUjian = FXCollections.observableArrayList(examList);
            tabelUjian.setItems(daftarUjian);
        } catch (SQLException e) {
            showError("Gagal memuat ujian: " + e.getMessage());
        }
    }

    private void addCourse() {
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
        result.ifPresent(course -> {
            try {
                manajerBasisData.tambahMataKuliah(course);
                loadCourses();
                showInfo("Mata kuliah berhasil ditambahkan!");
            } catch (SQLException e) {
                showError("Gagal menambahkan mata kuliah: " + e.getMessage());
            }
        });
    }

    private void editCourse() {
        MataKuliah selected = tabelMataKuliah.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih mata kuliah yang akan diedit!");
            return;
        }

        Dialog<MataKuliah> dialog = PembuatDialogMD3.buatDialog("Edit Mata Kuliah", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField(selected.getKode());
        TextField nameField = new TextField(selected.getNama());
        TextArea descArea = new TextArea(selected.getDeskripsi());
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
                selected.setKode(codeField.getText());
                selected.setNama(nameField.getText());
                selected.setDeskripsi(descArea.getText());
                return selected;
            }
            return null;
        });

        Optional<MataKuliah> result = dialog.showAndWait();
        result.ifPresent(course -> {
            try {
                manajerBasisData.perbaruiMataKuliah(course);
                loadCourses();
                showInfo("Mata kuliah berhasil diupdate!");
            } catch (SQLException e) {
                showError("Gagal memperbarui mata kuliah: " + e.getMessage());
            }
        });
    }

    private void deleteCourse() {
        MataKuliah selected = tabelMataKuliah.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih mata kuliah yang akan dihapus!");
            return;
        }

        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus mata kuliah: " + selected.getNama() + "?",
                "Semua topik dan ujian terkait juga akan dihapus. Lanjutkan?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                manajerBasisData.hapusMataKuliah(selected.getId());
                loadCourses();
                tabelTopik.getItems().clear();
                tabelUjian.getItems().clear();
                showInfo("Mata kuliah berhasil dihapus!");
            } catch (SQLException e) {
                showError("Gagal menghapus mata kuliah: " + e.getMessage());
            }
        }
    }

    private void addTopic() {
        MataKuliah selectedCourse = tabelMataKuliah
                .getSelectionModel()
                .getSelectedItem();
        if (selectedCourse == null) {
            showWarning("Pilih mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<Topik> dialog = PembuatDialogMD3.buatDialog("Tambah Topik", "Tambah topik untuk: " + selectedCourse.getNama());

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
                Topik topic = new Topik();
                topic.setIdMataKuliah(selectedCourse.getId());
                topic.setNama(nameField.getText());
                topic.setDeskripsi(descArea.getText());
                topic.setPrioritas(prioritySpinner.getValue());
                topic.setTingkatKesulitan(difficultySpinner.getValue());
                return topic;
            }
            return null;
        });

        Optional<Topik> result = dialog.showAndWait();
        result.ifPresent(topic -> {
            try {
                manajerBasisData.tambahTopik(topic);
                loadTopics(selectedCourse.getId());
                showInfo("Topik berhasil ditambahkan!");
            } catch (SQLException e) {
                showError("Gagal menambahkan topik: " + e.getMessage());
            }
        });
    }

    private void editTopic() {
        Topik selected = tabelTopik.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih topik yang akan diedit!");
            return;
        }

        Dialog<Topik> dialog = PembuatDialogMD3.buatDialog("Edit Topik", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(selected.getNama());
        TextArea descArea = new TextArea(selected.getDeskripsi());
        descArea.setPrefRowCount(2);
        Spinner<Integer> prioritySpinner = new Spinner<>(
                1,
                5,
                selected.getPrioritas());
        Spinner<Integer> difficultySpinner = new Spinner<>(
                1,
                5,
                selected.getTingkatKesulitan());

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
                selected.setNama(nameField.getText());
                selected.setDeskripsi(descArea.getText());
                selected.setPrioritas(prioritySpinner.getValue());
                selected.setTingkatKesulitan(difficultySpinner.getValue());
                return selected;
            }
            return null;
        });

        Optional<Topik> result = dialog.showAndWait();
        result.ifPresent(topic -> {
            try {
                manajerBasisData.perbaruiTopik(topic);
                loadTopics(topic.getIdMataKuliah());
                showInfo("Topik berhasil diupdate!");
            } catch (SQLException e) {
                showError("Gagal memperbarui topik: " + e.getMessage());
            }
        });
    }

    private void deleteTopic() {
        Topik selected = tabelTopik.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih topik yang akan dihapus!");
            return;
        }

        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus topik: " + selected.getNama() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int courseId = selected.getIdMataKuliah();
                manajerBasisData.hapusTopik(selected.getId());
                loadTopics(courseId);
                showInfo("Topik berhasil dihapus!");
            } catch (SQLException e) {
                showError("Gagal menghapus topik: " + e.getMessage());
            }
        }
    }

    private void addExam() {
        MataKuliah selectedCourse = tabelMataKuliah
                .getSelectionModel()
                .getSelectedItem();
        if (selectedCourse == null) {
            showWarning("Pilih mata kuliah terlebih dahulu!");
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
                JadwalUjian exam = new JadwalUjian();
                exam.setIdMataKuliah(selectedCourse.getId());
                exam.setJudul(titleField.getText());
                exam.setTipeUjian(typeCombo.getValue());
                exam.setTanggalUjian(datePicker.getValue());
                return exam;
            }
            return null;
        });

        Optional<JadwalUjian> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            try {
                manajerBasisData.tambahJadwalUjian(exam);
                loadExams(selectedCourse.getId());
                showInfo("Jadwal ujian berhasil ditambahkan!");
            } catch (SQLException e) {
                showError("Gagal menambahkan ujian: " + e.getMessage());
            }
        });
    }

    private void editExam() {
        JadwalUjian selected = tabelUjian.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih ujian yang akan diedit!");
            return;
        }

        Dialog<JadwalUjian> dialog = PembuatDialogMD3.buatDialog("Edit Jadwal Ujian", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField(selected.getJudul());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("UTS", "UAS", "Kuis", "Tugas");
        typeCombo.setValue(selected.getTipeUjian());
        DatePicker datePicker = new DatePicker(selected.getTanggalUjian());

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
                selected.setJudul(titleField.getText());
                selected.setTipeUjian(typeCombo.getValue());
                selected.setTanggalUjian(datePicker.getValue());
                return selected;
            }
            return null;
        });

        Optional<JadwalUjian> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            try {
                manajerBasisData.perbaruiJadwalUjian(exam);
                loadExams(exam.getIdMataKuliah());
                showInfo("Jadwal ujian berhasil diupdate!");
            } catch (SQLException e) {
                showError("Gagal memperbarui ujian: " + e.getMessage());
            }
        });
    }

    private void deleteExam() {
        JadwalUjian selected = tabelUjian.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih ujian yang akan dihapus!");
            return;
        }

        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus jadwal ujian: " + selected.getJudul() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int courseId = selected.getIdMataKuliah();
                manajerBasisData.hapusJadwalUjian(selected.getId());
                loadExams(courseId);
                showInfo("Jadwal ujian berhasil dihapus!");
            } catch (SQLException e) {
                showError("Gagal menghapus ujian: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.ERROR, "Kesalahan", message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.WARNING, "Peringatan", message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = PembuatDialogMD3.buatAlert(Alert.AlertType.INFORMATION, "Informasi", message);
        alert.showAndWait();
    }
}
