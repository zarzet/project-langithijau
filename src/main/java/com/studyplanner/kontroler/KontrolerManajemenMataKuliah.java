package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.JadwalUjian;
import com.studyplanner.model.Topik;
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
    private TableView<MataKuliah> courseTable;

    @FXML
    private TableColumn<MataKuliah, String> courseCodeColumn;

    @FXML
    private TableColumn<MataKuliah, String> courseNameColumn;

    @FXML
    private TableView<Topik> topicTable;

    @FXML
    private TableColumn<Topik, String> topicNameColumn;

    @FXML
    private TableColumn<Topik, Integer> topicPriorityColumn;

    @FXML
    private TableColumn<Topik, Integer> topicDifficultyColumn;

    @FXML
    private TableColumn<Topik, Integer> topicReviewCountColumn;

    @FXML
    private TableView<JadwalUjian> examTable;

    @FXML
    private TableColumn<JadwalUjian, String> examTitleColumn;

    @FXML
    private TableColumn<JadwalUjian, String> examTypeColumn;

    @FXML
    private TableColumn<JadwalUjian, LocalDate> examDateColumn;

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
    private ObservableList<MataKuliah> courses;
    private ObservableList<Topik> topics;
    private ObservableList<JadwalUjian> exams;

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
        courseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        courseCodeColumn.setCellValueFactory(
                new PropertyValueFactory<>("kode"));
        courseNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("nama"));

        topicTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        courseTable
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, newSelection) -> {
                    if (newSelection != null) {
                        loadTopics(newSelection.getId());
                        loadExams(newSelection.getId());
                    }
                });

        topicNameColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        topicPriorityColumn.setCellValueFactory(
                new PropertyValueFactory<>("prioritas"));
        topicDifficultyColumn.setCellValueFactory(
                new PropertyValueFactory<>("tingkatKesulitan"));
        topicReviewCountColumn.setCellValueFactory(
                new PropertyValueFactory<>("jumlahUlasan"));

        examTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        examTitleColumn.setCellValueFactory(
                new PropertyValueFactory<>("judul"));
        examTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("tipeUjian"));
        examDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("tanggalUjian"));
    }

    private void setupButtons() {
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
            courses = FXCollections.observableArrayList(courseList);
            courseTable.setItems(courses);
        } catch (SQLException e) {
            showError("Gagal memuat daftar mata kuliah: " + e.getMessage());
        }
    }

    private void loadTopics(int courseId) {
        try {
            List<Topik> topicList = manajerBasisData.ambilTopikBerdasarkanMataKuliah(courseId);
            topics = FXCollections.observableArrayList(topicList);
            topicTable.setItems(topics);
        } catch (SQLException e) {
            showError("Gagal memuat topik: " + e.getMessage());
        }
    }

    private void loadExams(int courseId) {
        try {
            List<JadwalUjian> examList = manajerBasisData.ambilUjianBerdasarkanMataKuliah(courseId);
            exams = FXCollections.observableArrayList(examList);
            examTable.setItems(exams);
        } catch (SQLException e) {
            showError("Gagal memuat ujian: " + e.getMessage());
        }
    }

    private void addCourse() {
        Dialog<MataKuliah> dialog = new Dialog<>();
        dialog.setTitle("Tambah Mata Kuliah");
        dialog.setHeaderText("Masukkan data mata kuliah baru");

        ButtonType saveButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE);
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
        MataKuliah selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih mata kuliah yang akan diedit!");
            return;
        }

        Dialog<MataKuliah> dialog = new Dialog<>();
        dialog.setTitle("Edit Mata Kuliah");

        ButtonType saveButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE);
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
        MataKuliah selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih mata kuliah yang akan dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus mata kuliah: " + selected.getNama() + "?");
        confirm.setContentText(
                "Semua topik dan ujian terkait juga akan dihapus. Lanjutkan?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                manajerBasisData.hapusMataKuliah(selected.getId());
                loadCourses();
                topicTable.getItems().clear();
                examTable.getItems().clear();
                showInfo("Mata kuliah berhasil dihapus!");
            } catch (SQLException e) {
                showError("Gagal menghapus mata kuliah: " + e.getMessage());
            }
        }
    }

    private void addTopic() {
        MataKuliah selectedCourse = courseTable
                .getSelectionModel()
                .getSelectedItem();
        if (selectedCourse == null) {
            showWarning("Pilih mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<Topik> dialog = new Dialog<>();
        dialog.setTitle("Tambah Topik");
        dialog.setHeaderText("Tambah topik untuk: " + selectedCourse.getNama());

        ButtonType saveButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE);
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
        Topik selected = topicTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih topik yang akan diedit!");
            return;
        }

        Dialog<Topik> dialog = new Dialog<>();
        dialog.setTitle("Edit Topik");

        ButtonType saveButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE);
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
        Topik selected = topicTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih topik yang akan dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setContentText("Hapus topik: " + selected.getNama() + "?");

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
        MataKuliah selectedCourse = courseTable
                .getSelectionModel()
                .getSelectedItem();
        if (selectedCourse == null) {
            showWarning("Pilih mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<JadwalUjian> dialog = new Dialog<>();
        dialog.setTitle("Tambah Jadwal Ujian");

        ButtonType saveButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE);
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
        JadwalUjian selected = examTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih ujian yang akan diedit!");
            return;
        }

        Dialog<JadwalUjian> dialog = new Dialog<>();
        dialog.setTitle("Edit Jadwal Ujian");

        ButtonType saveButtonType = new ButtonType(
                "Simpan",
                ButtonBar.ButtonData.OK_DONE);
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
        JadwalUjian selected = examTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih ujian yang akan dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText(
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
