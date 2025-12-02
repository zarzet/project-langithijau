package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.layanan.LayananMataKuliah;
import com.studyplanner.layanan.LayananTopik;
import com.studyplanner.layanan.LayananJadwalUjian;
import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.JadwalUjian;
import com.studyplanner.model.Topik;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatDialogMD3;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.UtilUI;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Kontroler untuk manajemen mata kuliah dengan UI card-based modern.
 */
public class KontrolerManajemenMataKuliah implements Initializable {

    // Tab Navigation
    @FXML private ToggleButton tabMataKuliah;
    @FXML private ToggleButton tabTopik;
    @FXML private ToggleButton tabUjian;
    @FXML private ToggleGroup tabGroup;

    // Panels
    @FXML private VBox panelMataKuliah;
    @FXML private VBox panelTopik;
    @FXML private VBox panelUjian;

    // Containers untuk cards
    @FXML private FlowPane containerMataKuliah;
    @FXML private FlowPane containerTopik;
    @FXML private FlowPane containerUjian;

    // Filter ComboBox
    @FXML private ComboBox<MataKuliah> filterMataKuliah;
    @FXML private ComboBox<MataKuliah> filterMataKuliahUjian;

    // Tombol Aksi
    @FXML private Button addCourseBtn;
    @FXML private Button addTopicBtn;
    @FXML private Button addExamBtn;

    private ManajerBasisData manajerBasisData;
    private LayananMataKuliah layananMataKuliah;
    private LayananTopik layananTopik;
    private LayananJadwalUjian layananJadwalUjian;
    private KontrolerUtama kontrolerUtama;
    
    private List<MataKuliah> daftarMataKuliah;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manajerBasisData = new ManajerBasisData();
        inisialisasiLayanan();
        aturNavigasiTab();
        aturTombol();
        muatSemuaData();
    }

    public void aturKontrolerUtama(KontrolerUtama kontroler) {
        this.kontrolerUtama = kontroler;
        if (this.kontrolerUtama != null) {
            this.manajerBasisData = this.kontrolerUtama.getManajerBasisData();
            inisialisasiLayanan();
            muatSemuaData();
        }
    }

    private void inisialisasiLayanan() {
        this.layananMataKuliah = new LayananMataKuliah(manajerBasisData);
        this.layananTopik = new LayananTopik(manajerBasisData);
        this.layananJadwalUjian = new LayananJadwalUjian(manajerBasisData);
    }

    private void aturNavigasiTab() {
        tabGroup.selectedToggleProperty().addListener((_, _, newVal) -> {
            if (newVal == tabMataKuliah) {
                tampilkanPanel(panelMataKuliah);
            } else if (newVal == tabTopik) {
                tampilkanPanel(panelTopik);
                muatTopikCards();
            } else if (newVal == tabUjian) {
                tampilkanPanel(panelUjian);
                muatUjianCards();
            }
        });
    }

    private void tampilkanPanel(VBox panel) {
        panelMataKuliah.setVisible(panel == panelMataKuliah);
        panelMataKuliah.setManaged(panel == panelMataKuliah);
        panelTopik.setVisible(panel == panelTopik);
        panelTopik.setManaged(panel == panelTopik);
        panelUjian.setVisible(panel == panelUjian);
        panelUjian.setManaged(panel == panelUjian);
    }

    private void aturTombol() {
        addCourseBtn.setOnAction(_ -> tambahMataKuliah());
        addTopicBtn.setOnAction(_ -> tambahTopik());
        addExamBtn.setOnAction(_ -> tambahUjian());

        // Setup filter combo boxes
        if (filterMataKuliah != null) {
            filterMataKuliah.setOnAction(_ -> muatTopikCards());
        }
        if (filterMataKuliahUjian != null) {
            filterMataKuliahUjian.setOnAction(_ -> muatUjianCards());
        }
    }

    private void muatSemuaData() {
        muatMataKuliahCards();
        isiFilterComboBox();
    }

    // ==================== CARD BUILDERS ====================

    private void muatMataKuliahCards() {
        containerMataKuliah.getChildren().clear();
        try {
            int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
            daftarMataKuliah = layananMataKuliah.ambilSemuaByUserId(userId);
            
            if (daftarMataKuliah.isEmpty()) {
                containerMataKuliah.getChildren().add(buatEmptyState("Belum ada mata kuliah", "Klik tombol + untuk menambah"));
            } else {
                for (MataKuliah mk : daftarMataKuliah) {
                    containerMataKuliah.getChildren().add(buatCardMataKuliah(mk));
                }
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat mata kuliah: " + e.getMessage());
        }
    }

    private VBox buatCardMataKuliah(MataKuliah mk) {
        VBox card = new VBox(8);
        card.getStyleClass().add("course-card");
        card.setPadding(new Insets(16));
        card.setPrefWidth(280);
        card.setMinWidth(250);

        // Header dengan kode
        Label kodeLabel = new Label(mk.getKode());
        kodeLabel.getStyleClass().add("card-code");

        Label namaLabel = new Label(mk.getNama());
        namaLabel.getStyleClass().add("card-title");
        namaLabel.setWrapText(true);

        // Deskripsi (jika ada)
        if (mk.getDeskripsi() != null && !mk.getDeskripsi().isEmpty()) {
            Label descLabel = new Label(mk.getDeskripsi());
            descLabel.getStyleClass().add("card-desc");
            descLabel.setWrapText(true);
            card.getChildren().addAll(kodeLabel, namaLabel, descLabel);
        } else {
            card.getChildren().addAll(kodeLabel, namaLabel);
        }

        // Tombol aksi
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(8, 0, 0, 0));

        Button editBtn = new Button();
        editBtn.setGraphic(PembuatIkon.ikonEdit());
        editBtn.getStyleClass().add("btn-icon-small");
        editBtn.setOnAction(_ -> editMataKuliah(mk));

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(PembuatIkon.ikonHapus());
        deleteBtn.getStyleClass().addAll("btn-icon-small", "btn-danger-icon");
        deleteBtn.setOnAction(_ -> hapusMataKuliah(mk));

        actions.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().add(actions);

        return card;
    }

    private void muatTopikCards() {
        containerTopik.getChildren().clear();
        try {
            MataKuliah filter = filterMataKuliah != null ? filterMataKuliah.getValue() : null;
            List<Topik> daftarTopik;
            
            if (filter != null) {
                daftarTopik = layananTopik.ambilBerdasarkanMataKuliah(filter.getId());
            } else {
                int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
                daftarTopik = layananTopik.ambilSemuaByUserId(userId);
            }
            
            if (daftarTopik.isEmpty()) {
                containerTopik.getChildren().add(buatEmptyState("Belum ada topik", "Pilih mata kuliah dan tambah topik"));
            } else {
                for (Topik t : daftarTopik) {
                    containerTopik.getChildren().add(buatCardTopik(t));
                }
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat topik: " + e.getMessage());
        }
    }

    private VBox buatCardTopik(Topik topik) {
        VBox card = new VBox(8);
        card.getStyleClass().add("topic-card");
        card.setPadding(new Insets(16));
        card.setPrefWidth(260);
        card.setMinWidth(230);

        Label namaLabel = new Label(topik.getNama());
        namaLabel.getStyleClass().add("card-title");
        namaLabel.setWrapText(true);

        // Info badges
        HBox badges = new HBox(8);
        badges.setAlignment(Pos.CENTER_LEFT);
        
        Label prioritas = new Label("P: " + topik.getPrioritas());
        prioritas.getStyleClass().add("badge-priority");
        
        Label kesulitan = new Label("K: " + topik.getTingkatKesulitan());
        kesulitan.getStyleClass().add("badge-difficulty");
        
        Label review = new Label("Review: " + topik.getJumlahUlasan());
        review.getStyleClass().add("badge-review");

        badges.getChildren().addAll(prioritas, kesulitan, review);

        // Tombol aksi
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(8, 0, 0, 0));

        Button editBtn = new Button();
        editBtn.setGraphic(PembuatIkon.ikonEdit());
        editBtn.getStyleClass().add("btn-icon-small");
        editBtn.setOnAction(_ -> editTopik(topik));

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(PembuatIkon.ikonHapus());
        deleteBtn.getStyleClass().addAll("btn-icon-small", "btn-danger-icon");
        deleteBtn.setOnAction(_ -> hapusTopik(topik));

        actions.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(namaLabel, badges, actions);

        return card;
    }

    private void muatUjianCards() {
        containerUjian.getChildren().clear();
        try {
            MataKuliah filter = filterMataKuliahUjian != null ? filterMataKuliahUjian.getValue() : null;
            List<JadwalUjian> daftarUjian;
            
            if (filter != null) {
                daftarUjian = layananJadwalUjian.ambilBerdasarkanMataKuliah(filter.getId());
            } else {
                daftarUjian = layananJadwalUjian.ambilUjianMendatang();
            }
            
            if (daftarUjian.isEmpty()) {
                containerUjian.getChildren().add(buatEmptyState("Belum ada ujian", "Tambah jadwal ujian"));
            } else {
                for (JadwalUjian u : daftarUjian) {
                    containerUjian.getChildren().add(buatCardUjian(u));
                }
            }
        } catch (SQLException e) {
            UtilUI.tampilkanKesalahan("Gagal memuat ujian: " + e.getMessage());
        }
    }

    private VBox buatCardUjian(JadwalUjian ujian) {
        VBox card = new VBox(8);
        card.getStyleClass().add("exam-card-spa");
        card.setPadding(new Insets(16));
        card.setPrefWidth(260);
        card.setMinWidth(230);

        Label judulLabel = new Label(ujian.getJudul());
        judulLabel.getStyleClass().add("card-title");

        Label tipeLabel = new Label(ujian.getTipeUjian());
        tipeLabel.getStyleClass().add("badge-exam-type");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Label tanggalLabel = new Label(ujian.getTanggalUjian().format(fmt));
        tanggalLabel.getStyleClass().add("card-date");

        int hariLagi = ujian.getHariMenujuUjian();
        Label countdown = new Label(hariLagi + " hari lagi");
        countdown.getStyleClass().add(hariLagi <= 3 ? "countdown-urgent" : "countdown-normal");

        // Tombol aksi
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(8, 0, 0, 0));

        Button editBtn = new Button();
        editBtn.setGraphic(PembuatIkon.ikonEdit());
        editBtn.getStyleClass().add("btn-icon-small");
        editBtn.setOnAction(_ -> editUjian(ujian));

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(PembuatIkon.ikonHapus());
        deleteBtn.getStyleClass().addAll("btn-icon-small", "btn-danger-icon");
        deleteBtn.setOnAction(_ -> hapusUjian(ujian));

        actions.getChildren().addAll(editBtn, deleteBtn);
        card.getChildren().addAll(judulLabel, tipeLabel, tanggalLabel, countdown, actions);

        return card;
    }

    private VBox buatEmptyState(String judul, String deskripsi) {
        VBox empty = new VBox(8);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(40));
        empty.getStyleClass().add("empty-state");

        Label titleLabel = new Label(judul);
        titleLabel.getStyleClass().add("empty-title");

        Label descLabel = new Label(deskripsi);
        descLabel.getStyleClass().add("empty-desc");

        empty.getChildren().addAll(titleLabel, descLabel);
        return empty;
    }

    private void isiFilterComboBox() {
        if (filterMataKuliah != null && daftarMataKuliah != null) {
            filterMataKuliah.getItems().clear();
            filterMataKuliah.getItems().add(null); // "Semua"
            filterMataKuliah.getItems().addAll(daftarMataKuliah);
            filterMataKuliah.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(MataKuliah item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item == null ? "Semua Mata Kuliah" : item.getNama());
                }
            });
            filterMataKuliah.setCellFactory(_ -> new ListCell<>() {
                @Override
                protected void updateItem(MataKuliah item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item == null ? "Semua Mata Kuliah" : item.getNama());
                }
            });
        }
        if (filterMataKuliahUjian != null && daftarMataKuliah != null) {
            filterMataKuliahUjian.getItems().clear();
            filterMataKuliahUjian.getItems().add(null);
            filterMataKuliahUjian.getItems().addAll(daftarMataKuliah);
            filterMataKuliahUjian.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(MataKuliah item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item == null ? "Semua Mata Kuliah" : item.getNama());
                }
            });
            filterMataKuliahUjian.setCellFactory(_ -> new ListCell<>() {
                @Override
                protected void updateItem(MataKuliah item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item == null ? "Semua Mata Kuliah" : item.getNama());
                }
            });
        }
    }

    // ==================== CRUD MATA KULIAH ====================

    private void tambahMataKuliah() {
        Dialog<MataKuliah> dialog = PembuatDialogMD3.buatDialog("Tambah Mata Kuliah", "Masukkan data mata kuliah baru");

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField();
        codeField.setPromptText("Kode MK (contoh: CS101)");
        TextField nameField = new TextField();
        nameField.setPromptText("Nama Mata Kuliah");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Deskripsi (opsional)");
        descArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
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
                course.setUserId(ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1));
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
                layananMataKuliah.daftarkan(mataKuliah);
                muatSemuaData();
                UtilUI.tampilkanToast("Mata kuliah berhasil ditambahkan!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menambahkan mata kuliah: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                UtilUI.tampilkanKesalahan(e.getMessage());
            }
        });
    }

    private void editMataKuliah(MataKuliah terpilih) {
        Dialog<MataKuliah> dialog = PembuatDialogMD3.buatDialog("Edit Mata Kuliah", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField(terpilih.getKode());
        TextField nameField = new TextField(terpilih.getNama());
        TextArea descArea = new TextArea(terpilih.getDeskripsi());
        descArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
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
                layananMataKuliah.perbarui(mataKuliah);
                muatSemuaData();
                UtilUI.tampilkanToast("Mata kuliah berhasil diupdate!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal memperbarui mata kuliah: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                UtilUI.tampilkanKesalahan(e.getMessage());
            }
        });
    }

    private void hapusMataKuliah(MataKuliah terpilih) {
        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus mata kuliah: " + terpilih.getNama() + "?",
                "Semua topik dan ujian terkait juga akan dihapus. Lanjutkan?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                layananMataKuliah.hapus(terpilih.getId());
                muatSemuaData();
                UtilUI.tampilkanToast("Mata kuliah berhasil dihapus!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menghapus mata kuliah: " + e.getMessage());
            }
        }
    }

    // ==================== CRUD TOPIK ====================

    private void tambahTopik() {
        // Dialog untuk pilih mata kuliah dulu
        if (daftarMataKuliah == null || daftarMataKuliah.isEmpty()) {
            UtilUI.tampilkanPeringatan("Tambah mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<Topik> dialog = PembuatDialogMD3.buatDialog("Tambah Topik", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        ComboBox<MataKuliah> mkCombo = new ComboBox<>();
        mkCombo.getItems().addAll(daftarMataKuliah);
        mkCombo.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(MataKuliah item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNama());
            }
        });
        mkCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MataKuliah item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNama());
            }
        });

        TextField nameField = new TextField();
        nameField.setPromptText("Nama Topik");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Deskripsi");
        descArea.setPrefRowCount(2);
        Spinner<Integer> prioritySpinner = new Spinner<>(1, 5, 3);
        Spinner<Integer> difficultySpinner = new Spinner<>(1, 5, 3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Mata Kuliah:"), 0, 0);
        grid.add(mkCombo, 1, 0);
        grid.add(new Label("Nama:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Deskripsi:"), 0, 2);
        grid.add(descArea, 1, 2);
        grid.add(new Label("Prioritas (1-5):"), 0, 3);
        grid.add(prioritySpinner, 1, 3);
        grid.add(new Label("Kesulitan (1-5):"), 0, 4);
        grid.add(difficultySpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType && mkCombo.getValue() != null) {
                Topik topik = new Topik();
                topik.setIdMataKuliah(mkCombo.getValue().getId());
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
                layananTopik.tambah(topik);
                muatTopikCards();
                UtilUI.tampilkanToast("Topik berhasil ditambahkan!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menambahkan topik: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                UtilUI.tampilkanKesalahan(e.getMessage());
            }
        });
    }

    private void editTopik(Topik terpilih) {
        Dialog<Topik> dialog = PembuatDialogMD3.buatDialog("Edit Topik", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(terpilih.getNama());
        TextArea descArea = new TextArea(terpilih.getDeskripsi());
        descArea.setPrefRowCount(2);
        Spinner<Integer> prioritySpinner = new Spinner<>(1, 5, terpilih.getPrioritas());
        Spinner<Integer> difficultySpinner = new Spinner<>(1, 5, terpilih.getTingkatKesulitan());

        GridPane grid = new GridPane();
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
                layananTopik.perbarui(topik);
                muatTopikCards();
                UtilUI.tampilkanToast("Topik berhasil diupdate!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal memperbarui topik: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                UtilUI.tampilkanKesalahan(e.getMessage());
            }
        });
    }

    private void hapusTopik(Topik terpilih) {
        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus topik: " + terpilih.getNama() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                layananTopik.hapus(terpilih.getId());
                muatTopikCards();
                UtilUI.tampilkanToast("Topik berhasil dihapus!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menghapus topik: " + e.getMessage());
            }
        }
    }

    // ==================== CRUD UJIAN ====================

    private void tambahUjian() {
        if (daftarMataKuliah == null || daftarMataKuliah.isEmpty()) {
            UtilUI.tampilkanPeringatan("Tambah mata kuliah terlebih dahulu!");
            return;
        }

        Dialog<JadwalUjian> dialog = PembuatDialogMD3.buatDialog("Tambah Jadwal Ujian", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        ComboBox<MataKuliah> mkCombo = new ComboBox<>();
        mkCombo.getItems().addAll(daftarMataKuliah);
        mkCombo.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(MataKuliah item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNama());
            }
        });
        mkCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MataKuliah item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNama());
            }
        });

        TextField titleField = new TextField();
        titleField.setPromptText("Judul (contoh: UTS)");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("UTS", "UAS", "Kuis", "Tugas");
        typeCombo.setValue("UTS");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Mata Kuliah:"), 0, 0);
        grid.add(mkCombo, 1, 0);
        grid.add(new Label("Judul:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Tipe:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Tanggal:"), 0, 3);
        grid.add(datePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType && mkCombo.getValue() != null) {
                JadwalUjian ujian = new JadwalUjian();
                ujian.setIdMataKuliah(mkCombo.getValue().getId());
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
                layananJadwalUjian.tambah(ujian);
                muatUjianCards();
                UtilUI.tampilkanToast("Jadwal ujian berhasil ditambahkan!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menambahkan ujian: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                UtilUI.tampilkanKesalahan(e.getMessage());
            }
        });
    }

    private void editUjian(JadwalUjian terpilih) {
        Dialog<JadwalUjian> dialog = PembuatDialogMD3.buatDialog("Edit Jadwal Ujian", null);

        ButtonType saveButtonType = PembuatDialogMD3.buatTombolSimpan();
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField(terpilih.getJudul());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("UTS", "UAS", "Kuis", "Tugas");
        typeCombo.setValue(terpilih.getTipeUjian());
        DatePicker datePicker = new DatePicker(terpilih.getTanggalUjian());

        GridPane grid = new GridPane();
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
                layananJadwalUjian.perbarui(ujian);
                muatUjianCards();
                UtilUI.tampilkanToast("Jadwal ujian berhasil diupdate!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal memperbarui ujian: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                UtilUI.tampilkanKesalahan(e.getMessage());
            }
        });
    }

    private void hapusUjian(JadwalUjian terpilih) {
        Alert confirm = PembuatDialogMD3.buatAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus",
                "Hapus jadwal ujian: " + terpilih.getJudul() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                layananJadwalUjian.hapus(terpilih.getId());
                muatUjianCards();
                UtilUI.tampilkanToast("Jadwal ujian berhasil dihapus!");
            } catch (SQLException e) {
                UtilUI.tampilkanKesalahan("Gagal menghapus ujian: " + e.getMessage());
            }
        }
    }
}
