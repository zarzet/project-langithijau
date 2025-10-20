package com.studyplanner.controller;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.Course;
import com.studyplanner.model.Topic;
import com.studyplanner.model.ExamSchedule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller untuk Course Management
 */
public class CourseManagementController implements Initializable {
    
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    
    @FXML private TableView<Topic> topicTable;
    @FXML private TableColumn<Topic, String> topicNameColumn;
    @FXML private TableColumn<Topic, Integer> topicPriorityColumn;
    @FXML private TableColumn<Topic, Integer> topicDifficultyColumn;
    @FXML private TableColumn<Topic, Integer> topicReviewCountColumn;
    
    @FXML private TableView<ExamSchedule> examTable;
    @FXML private TableColumn<ExamSchedule, String> examTitleColumn;
    @FXML private TableColumn<ExamSchedule, String> examTypeColumn;
    @FXML private TableColumn<ExamSchedule, LocalDate> examDateColumn;
    
    @FXML private Button addCourseBtn;
    @FXML private Button editCourseBtn;
    @FXML private Button deleteCourseBtn;
    
    @FXML private Button addTopicBtn;
    @FXML private Button editTopicBtn;
    @FXML private Button deleteTopicBtn;
    
    @FXML private Button addExamBtn;
    @FXML private Button editExamBtn;
    @FXML private Button deleteExamBtn;
    
    private DatabaseManager dbManager;
    private MainController mainController;
    private ObservableList<Course> courses;
    private ObservableList<Topic> topics;
    private ObservableList<ExamSchedule> exams;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = new DatabaseManager();
        
        setupTables();
        setupButtons();
        loadCourses();
    }
    
    public void setMainController(MainController controller) {
        this.mainController = controller;
        if (this.mainController != null) {
            this.dbManager = this.mainController.getDbManager();
        }
    }
    
    private void setupTables() {
        // Course Table
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        courseTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                loadTopics(newSelection.getId());
                loadExams(newSelection.getId());
            }
        });
        
        // Topic Table
        topicNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        topicPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        topicDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficultyLevel"));
        topicReviewCountColumn.setCellValueFactory(new PropertyValueFactory<>("reviewCount"));
        
        // Exam Table
        examTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("examType"));
        examDateColumn.setCellValueFactory(new PropertyValueFactory<>("examDate"));
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
            List<Course> courseList = dbManager.getAllCourses();
            courses = FXCollections.observableArrayList(courseList);
            courseTable.setItems(courses);
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }
    
    private void loadTopics(int courseId) {
        try {
            List<Topic> topicList = dbManager.getTopicsByCourse(courseId);
            topics = FXCollections.observableArrayList(topicList);
            topicTable.setItems(topics);
        } catch (SQLException e) {
            showError("Error loading topics: " + e.getMessage());
        }
    }
    
    private void loadExams(int courseId) {
        try {
            List<ExamSchedule> examList = dbManager.getExamsByCourse(courseId);
            exams = FXCollections.observableArrayList(examList);
            examTable.setItems(exams);
        } catch (SQLException e) {
            showError("Error loading exams: " + e.getMessage());
        }
    }
    
    // ========== COURSE OPERATIONS ==========
    
    private void addCourse() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Tambah Mata Kuliah");
        dialog.setHeaderText("Masukkan data mata kuliah baru");
        
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
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
                Course course = new Course();
                course.setCode(codeField.getText());
                course.setName(nameField.getText());
                course.setDescription(descArea.getText());
                return course;
            }
            return null;
        });
        
        Optional<Course> result = dialog.showAndWait();
        result.ifPresent(course -> {
            try {
                dbManager.addCourse(course);
                loadCourses();
                showInfo("Mata kuliah berhasil ditambahkan!");
            } catch (SQLException e) {
                showError("Error adding course: " + e.getMessage());
            }
        });
    }
    
    private void editCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih mata kuliah yang akan diedit!");
            return;
        }
        
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Mata Kuliah");
        
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        TextField codeField = new TextField(selected.getCode());
        TextField nameField = new TextField(selected.getName());
        TextArea descArea = new TextArea(selected.getDescription());
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
                selected.setCode(codeField.getText());
                selected.setName(nameField.getText());
                selected.setDescription(descArea.getText());
                return selected;
            }
            return null;
        });
        
        Optional<Course> result = dialog.showAndWait();
        result.ifPresent(course -> {
            try {
                dbManager.updateCourse(course);
                loadCourses();
                showInfo("Mata kuliah berhasil diupdate!");
            } catch (SQLException e) {
                showError("Error updating course: " + e.getMessage());
            }
        });
    }
    
    private void deleteCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih mata kuliah yang akan dihapus!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus mata kuliah: " + selected.getName() + "?");
        confirm.setContentText("Semua topik dan ujian terkait juga akan dihapus. Lanjutkan?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                dbManager.deleteCourse(selected.getId());
                loadCourses();
                topicTable.getItems().clear();
                examTable.getItems().clear();
                showInfo("Mata kuliah berhasil dihapus!");
            } catch (SQLException e) {
                showError("Error deleting course: " + e.getMessage());
            }
        }
    }
    
    // ========== TOPIC OPERATIONS ==========
    
    private void addTopic() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showWarning("Pilih mata kuliah terlebih dahulu!");
            return;
        }
        
        Dialog<Topic> dialog = new Dialog<>();
        dialog.setTitle("Tambah Topik");
        dialog.setHeaderText("Tambah topik untuk: " + selectedCourse.getName());
        
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
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
                Topic topic = new Topic();
                topic.setCourseId(selectedCourse.getId());
                topic.setName(nameField.getText());
                topic.setDescription(descArea.getText());
                topic.setPriority(prioritySpinner.getValue());
                topic.setDifficultyLevel(difficultySpinner.getValue());
                return topic;
            }
            return null;
        });
        
        Optional<Topic> result = dialog.showAndWait();
        result.ifPresent(topic -> {
            try {
                dbManager.addTopic(topic);
                loadTopics(selectedCourse.getId());
                showInfo("Topik berhasil ditambahkan!");
            } catch (SQLException e) {
                showError("Error adding topic: " + e.getMessage());
            }
        });
    }
    
    private void editTopic() {
        Topic selected = topicTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih topik yang akan diedit!");
            return;
        }
        
        Dialog<Topic> dialog = new Dialog<>();
        dialog.setTitle("Edit Topik");
        
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        TextField nameField = new TextField(selected.getName());
        TextArea descArea = new TextArea(selected.getDescription());
        descArea.setPrefRowCount(2);
        Spinner<Integer> prioritySpinner = new Spinner<>(1, 5, selected.getPriority());
        Spinner<Integer> difficultySpinner = new Spinner<>(1, 5, selected.getDifficultyLevel());
        
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
                selected.setName(nameField.getText());
                selected.setDescription(descArea.getText());
                selected.setPriority(prioritySpinner.getValue());
                selected.setDifficultyLevel(difficultySpinner.getValue());
                return selected;
            }
            return null;
        });
        
        Optional<Topic> result = dialog.showAndWait();
        result.ifPresent(topic -> {
            try {
                dbManager.updateTopic(topic);
                loadTopics(topic.getCourseId());
                showInfo("Topik berhasil diupdate!");
            } catch (SQLException e) {
                showError("Error updating topic: " + e.getMessage());
            }
        });
    }
    
    private void deleteTopic() {
        Topic selected = topicTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih topik yang akan dihapus!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setContentText("Hapus topik: " + selected.getName() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int courseId = selected.getCourseId();
                dbManager.deleteTopic(selected.getId());
                loadTopics(courseId);
                showInfo("Topik berhasil dihapus!");
            } catch (SQLException e) {
                showError("Error deleting topic: " + e.getMessage());
            }
        }
    }
    
    // ========== EXAM OPERATIONS ==========
    
    private void addExam() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showWarning("Pilih mata kuliah terlebih dahulu!");
            return;
        }
        
        Dialog<ExamSchedule> dialog = new Dialog<>();
        dialog.setTitle("Tambah Jadwal Ujian");
        
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        TextField titleField = new TextField();
        titleField.setPromptText("Judul (contoh: UTS)");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("MIDTERM", "FINAL", "QUIZ", "ASSIGNMENT");
        typeCombo.setValue("MIDTERM");
        
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
                ExamSchedule exam = new ExamSchedule();
                exam.setCourseId(selectedCourse.getId());
                exam.setTitle(titleField.getText());
                exam.setExamType(typeCombo.getValue());
                exam.setExamDate(datePicker.getValue());
                return exam;
            }
            return null;
        });
        
        Optional<ExamSchedule> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            try {
                dbManager.addExamSchedule(exam);
                loadExams(selectedCourse.getId());
                showInfo("Jadwal ujian berhasil ditambahkan!");
            } catch (SQLException e) {
                showError("Error adding exam: " + e.getMessage());
            }
        });
    }
    
    private void editExam() {
        ExamSchedule selected = examTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih ujian yang akan diedit!");
            return;
        }
        
        Dialog<ExamSchedule> dialog = new Dialog<>();
        dialog.setTitle("Edit Jadwal Ujian");
        
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        TextField titleField = new TextField(selected.getTitle());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("MIDTERM", "FINAL", "QUIZ", "ASSIGNMENT");
        typeCombo.setValue(selected.getExamType());
        DatePicker datePicker = new DatePicker(selected.getExamDate());
        
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
                selected.setTitle(titleField.getText());
                selected.setExamType(typeCombo.getValue());
                selected.setExamDate(datePicker.getValue());
                return selected;
            }
            return null;
        });
        
        Optional<ExamSchedule> result = dialog.showAndWait();
        result.ifPresent(exam -> {
            try {
                dbManager.updateExamSchedule(exam);
                loadExams(exam.getCourseId());
                showInfo("Jadwal ujian berhasil diupdate!");
            } catch (SQLException e) {
                showError("Error updating exam: " + e.getMessage());
            }
        });
    }
    
    private void deleteExam() {
        ExamSchedule selected = examTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih ujian yang akan dihapus!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Hapus jadwal ujian: " + selected.getTitle() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int courseId = selected.getCourseId();
                dbManager.deleteExamSchedule(selected.getId());
                loadExams(courseId);
                showInfo("Jadwal ujian berhasil dihapus!");
            } catch (SQLException e) {
                showError("Error deleting exam: " + e.getMessage());
            }
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
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

