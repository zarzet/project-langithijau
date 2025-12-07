package com.studyplanner.kontroler;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAODosen;
import com.studyplanner.dao.DAOMahasiswa;
import com.studyplanner.layanan.LayananPengguna;
import com.studyplanner.model.Dosen;
import com.studyplanner.model.Mahasiswa;
import com.studyplanner.model.RolePengguna;
import com.studyplanner.model.StatusPengguna;
import com.studyplanner.utilitas.PencatatLog;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller untuk Panel Administrator.
 * Menangani UC-25 (Manage Users), UC-26 (Assign Dosen), UC-28 (View Statistics).
 */
public class KontrolerAdmin implements Initializable {

    @FXML private StackPane dialogOverlay;
    @FXML private VBox dialogContainer;

    @FXML private ToggleButton tabPengguna;
    @FXML private ToggleButton tabAssignment;
    @FXML private ToggleButton tabStatistik;
    @FXML private ToggleGroup tabGroup;

    @FXML private VBox panelPengguna;
    @FXML private ComboBox<String> filterRole;
    @FXML private ComboBox<String> filterStatus;
    @FXML private TextField searchField;
    @FXML private Button btnTambahPengguna;
    @FXML private TableView<Map<String, Object>> tabelPengguna;
    @FXML private TableColumn<Map<String, Object>, String> kolId;
    @FXML private TableColumn<Map<String, Object>, String> kolNama;
    @FXML private TableColumn<Map<String, Object>, String> kolEmail;
    @FXML private TableColumn<Map<String, Object>, String> kolRole;
    @FXML private TableColumn<Map<String, Object>, String> kolStatus;
    @FXML private TableColumn<Map<String, Object>, String> kolLoginTerakhir;
    @FXML private TableColumn<Map<String, Object>, Void> kolAksi;

    @FXML private VBox panelAssignment;
    @FXML private ListView<Dosen> listDosen;
    @FXML private ListView<MahasiswaSelectable> listMahasiswa;
    @FXML private Label labelInfoDosen;
    @FXML private Label labelTerpilih;
    @FXML private CheckBox checkSelectAll;
    @FXML private Button btnAssign;
    @FXML private Button btnBulkAssign;
    @FXML private ListView<MahasiswaSelectable> listMahasiswaAssigned;
    @FXML private Button btnUnassign;

    @FXML private VBox panelStatistik;
    @FXML private Label labelTotalPengguna;
    @FXML private Label labelTotalMahasiswa;
    @FXML private Label labelTotalDosen;
    @FXML private Label labelTanpaDosen;
    @FXML private TableView<Dosen> tabelDistribusi;
    @FXML private TableColumn<Dosen, String> kolDosenNama;
    @FXML private TableColumn<Dosen, String> kolDosenNip;
    @FXML private TableColumn<Dosen, String> kolJumlahMahasiswa;
    @FXML private TableColumn<Dosen, String> kolKuota;
    @FXML private TableColumn<Dosen, String> kolSisaKuota;

    private LayananPengguna layananPengguna;
    private DAODosen daoDosen;
    private DAOMahasiswa daoMahasiswa;
    private ObservableList<Map<String, Object>> dataPengguna;
    private Dosen dosenTerpilih;

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ManajerBasisData manajerDB = ManajerBasisData.dapatkanInstans();
        layananPengguna = new LayananPengguna(manajerDB);
        daoDosen = new DAODosen(manajerDB);
        daoMahasiswa = new DAOMahasiswa(manajerDB);

        setupTabNavigation();
        setupTabelPengguna();
        setupPanelAssignment();
        setupPanelStatistik();
        setupFilters();

        muatDataPengguna();
        perbaruiStatistik();
    }

    private void setupTabNavigation() {
        tabGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == tabPengguna) {
                tampilkanPanel(panelPengguna);
                muatDataPengguna();
            } else if (newVal == tabAssignment) {
                tampilkanPanel(panelAssignment);
                muatDataAssignment();
            } else if (newVal == tabStatistik) {
                tampilkanPanel(panelStatistik);
                muatDataStatistik();
            }
        });
    }

    private void tampilkanPanel(VBox panel) {
        panelPengguna.setVisible(panel == panelPengguna);
        panelPengguna.setManaged(panel == panelPengguna);
        panelAssignment.setVisible(panel == panelAssignment);
        panelAssignment.setManaged(panel == panelAssignment);
        panelStatistik.setVisible(panel == panelStatistik);
        panelStatistik.setManaged(panel == panelStatistik);
    }

    private void setupFilters() {
        filterRole.setItems(FXCollections.observableArrayList(
                "Semua Role", "Mahasiswa", "Dosen", "Admin"
        ));
        filterRole.setValue("Semua Role");
        filterRole.setOnAction(e -> filterData());

        filterStatus.setItems(FXCollections.observableArrayList(
                "Semua Status", "Aktif", "Tidak Aktif", "Ditangguhkan"
        ));
        filterStatus.setValue("Semua Status");
        filterStatus.setOnAction(e -> filterData());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());
    }

    private void setupTabelPengguna() {
        tabelPengguna.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        kolId.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().get("id"))));
        kolNama.setCellValueFactory(data -> 
            new SimpleStringProperty((String) data.getValue().get("nama")));
        kolEmail.setCellValueFactory(data -> 
            new SimpleStringProperty((String) data.getValue().get("email")));
        kolRole.setCellValueFactory(data -> {
            String role = (String) data.getValue().get("role");
            return new SimpleStringProperty(RolePengguna.dariKode(role).getNamaDisplay());
        });
        kolStatus.setCellValueFactory(data -> {
            String status = (String) data.getValue().get("status");
            return new SimpleStringProperty(StatusPengguna.dariKode(status).getNamaDisplay());
        });
        kolLoginTerakhir.setCellValueFactory(data -> {
            Timestamp ts = (Timestamp) data.getValue().get("login_terakhir");
            if (ts != null) {
                return new SimpleStringProperty(ts.toLocalDateTime().format(FORMAT_WAKTU));
            }
            return new SimpleStringProperty("-");
        });

        setupKolomAksi();
        dataPengguna = FXCollections.observableArrayList();
        tabelPengguna.setItems(dataPengguna);

        btnTambahPengguna.setOnAction(e -> tampilkanDialogTambahPengguna());
    }

    private void setupKolomAksi() {
        kolAksi.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnStatus = new Button("Status");
            private final HBox container = new HBox(4, btnEdit, btnStatus);

            {
                btnEdit.getStyleClass().add("btn-small");
                btnStatus.getStyleClass().add("btn-small-secondary");

                btnEdit.setOnAction(e -> {
                    Map<String, Object> user = getTableView().getItems().get(getIndex());
                    tampilkanDialogEditPengguna(user);
                });

                btnStatus.setOnAction(e -> {
                    Map<String, Object> user = getTableView().getItems().get(getIndex());
                    tampilkanMenuStatus(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void muatDataPengguna() {
        try {
            List<Map<String, Object>> users = layananPengguna.ambilSemuaPengguna();
            dataPengguna.setAll(users);
            perbaruiStatistik();
        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat data pengguna: " + e.getMessage());
            tampilkanError("Gagal memuat data pengguna: " + e.getMessage());
        }
    }

    private void filterData() {
        try {
            List<Map<String, Object>> users = layananPengguna.ambilSemuaPengguna();
            String roleFilter = filterRole.getValue();
            String statusFilter = filterStatus.getValue();
            String search = searchField.getText().toLowerCase();

            List<Map<String, Object>> filtered = users.stream()
                .filter(u -> {
                    if (!"Semua Role".equals(roleFilter)) {
                        String role = (String) u.get("role");
                        String roleDisplay = RolePengguna.dariKode(role).getNamaDisplay();
                        if (!roleDisplay.equals(roleFilter)) return false;
                    }
                    return true;
                })
                .filter(u -> {
                    if (!"Semua Status".equals(statusFilter)) {
                        String status = (String) u.get("status");
                        String statusDisplay = StatusPengguna.dariKode(status).getNamaDisplay();
                        if (!statusDisplay.equals(statusFilter)) return false;
                    }
                    return true;
                })
                .filter(u -> {
                    if (!search.isEmpty()) {
                        String nama = ((String) u.get("nama")).toLowerCase();
                        String email = u.get("email") != null ? ((String) u.get("email")).toLowerCase() : "";
                        return nama.contains(search) || email.contains(search);
                    }
                    return true;
                })
                .toList();

            dataPengguna.setAll(filtered);
        } catch (SQLException e) {
            PencatatLog.error("Gagal filter data: " + e.getMessage());
        }
    }

    private void tampilkanDialogTambahPengguna() {
        VBox konten = new VBox(16);
        konten.getStyleClass().add("spa-dialog-content");

        // Form fields
        TextField nama = new TextField();
        nama.setPromptText("Nama Lengkap");
        nama.getStyleClass().add("spa-input");

        TextField email = new TextField();
        email.setPromptText("Email");
        email.getStyleClass().add("spa-input");

        TextField username = new TextField();
        username.setPromptText("Username");
        username.getStyleClass().add("spa-input");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("spa-input");

        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Mahasiswa", "Dosen", "Admin"));
        role.setValue("Mahasiswa");
        role.getStyleClass().add("spa-combo");
        role.setMaxWidth(Double.MAX_VALUE);

        TextField nimNip = new TextField();
        nimNip.setPromptText("NIM/NIP (opsional)");
        nimNip.getStyleClass().add("spa-input");

        konten.getChildren().addAll(
            buatFieldGroup("Nama", nama),
            buatFieldGroup("Email", email),
            buatFieldGroup("Username", username),
            buatFieldGroup("Password", password),
            buatFieldGroup("Role", role),
            buatFieldGroup("NIM/NIP", nimNip)
        );

        HBox tombolContainer = new HBox(12);
        tombolContainer.setAlignment(Pos.CENTER_RIGHT);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().addAll("spa-btn", "spa-btn-secondary");
        btnBatal.setOnAction(e -> tutupDialogSPA());

        Button btnSimpan = new Button("Simpan");
        btnSimpan.getStyleClass().addAll("spa-btn", "spa-btn-primary");
        btnSimpan.setOnAction(e -> {
            try {
                String roleStr = role.getValue();
                RolePengguna rolePengguna = switch (roleStr) {
                    case "Dosen" -> RolePengguna.DOSEN;
                    case "Admin" -> RolePengguna.ADMIN;
                    default -> RolePengguna.MAHASISWA;
                };

                String hashedPassword = hashPassword(password.getText());

                if (rolePengguna == RolePengguna.DOSEN) {
                    layananPengguna.buatDosen(nama.getText(), email.getText(), username.getText(),
                        hashedPassword, nimNip.getText(), 30);
                } else if (rolePengguna == RolePengguna.MAHASISWA) {
                    layananPengguna.buatMahasiswa(nama.getText(), email.getText(), username.getText(),
                        hashedPassword, nimNip.getText(), 1);
                } else {
                    layananPengguna.buatPengguna(nama.getText(), email.getText(), username.getText(),
                        hashedPassword, rolePengguna);
                }

                tutupDialogSPA();
                tampilkanInfo("Pengguna berhasil ditambahkan!");
                muatDataPengguna();
            } catch (Exception ex) {
                tampilkanError("Gagal menambah pengguna: " + ex.getMessage());
            }
        });

        tombolContainer.getChildren().addAll(btnBatal, btnSimpan);
        konten.getChildren().add(tombolContainer);

        tampilkanDialogSPA("Tambah Pengguna Baru", konten);
    }

    private VBox buatFieldGroup(String label, javafx.scene.Node field) {
        VBox group = new VBox(6);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("spa-label");
        group.getChildren().addAll(lbl, field);
        return group;
    }

    private void tampilkanDialogEditPengguna(Map<String, Object> user) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Edit Pengguna");
        dialog.setHeaderText("Edit data pengguna: " + user.get("nama"));

        ButtonType simpanBtn = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nama = new TextField((String) user.get("nama"));
        TextField email = new TextField((String) user.get("email"));
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Mahasiswa", "Dosen", "Admin"));
        String currentRole = (String) user.get("role");
        role.setValue(RolePengguna.dariKode(currentRole).getNamaDisplay());
        
        // Field NIP/NIM
        TextField nimNip = new TextField();
        Label labelNimNip = new Label("NIM/NIP:");
        
        // Load existing NIM/NIP berdasarkan role
        int userId = (Integer) user.get("id");
        try {
            if ("dosen".equals(currentRole)) {
                Dosen dosen = daoDosen.ambilBerdasarkanUserId(userId);
                if (dosen != null && dosen.getNip() != null) {
                    nimNip.setText(dosen.getNip());
                }
                labelNimNip.setText("NIP:");
            } else if ("mahasiswa".equals(currentRole)) {
                Mahasiswa mhs = daoMahasiswa.ambilBerdasarkanUserId(userId);
                if (mhs != null && mhs.getNim() != null) {
                    nimNip.setText(mhs.getNim());
                }
                labelNimNip.setText("NIM:");
            }
        } catch (SQLException e) {
            PencatatLog.error("Gagal load NIM/NIP: " + e.getMessage());
        }
        
        // Update label saat role berubah
        role.setOnAction(e -> {
            String selectedRole = role.getValue();
            if ("Dosen".equals(selectedRole) || "Dosen Pembimbing".equals(selectedRole)) {
                labelNimNip.setText("NIP:");
            } else if ("Mahasiswa".equals(selectedRole)) {
                labelNimNip.setText("NIM:");
            } else {
                labelNimNip.setText("NIM/NIP:");
            }
        });

        grid.add(new Label("Nama:"), 0, 0);
        grid.add(nama, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(role, 1, 2);
        grid.add(labelNimNip, 0, 3);
        grid.add(nimNip, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == simpanBtn) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", user.get("id"));
                result.put("nama", nama.getText());
                result.put("email", email.getText());
                result.put("role", role.getValue());
                result.put("nimNip", nimNip.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(data -> {
            try {
                int uid = (Integer) data.get("id");
                String roleStr = (String) data.get("role");
                String nimNipValue = (String) data.get("nimNip");
                
                RolePengguna roleBaru = switch (roleStr) {
                    case "Dosen", "Dosen Pembimbing" -> RolePengguna.DOSEN;
                    case "Admin", "Administrator" -> RolePengguna.ADMIN;
                    default -> RolePengguna.MAHASISWA;
                };

                layananPengguna.ubahRole(uid, roleBaru);
                
                // Update NIM/NIP
                if (nimNipValue != null && !nimNipValue.trim().isEmpty()) {
                    if (roleBaru == RolePengguna.DOSEN) {
                        daoMahasiswa.hapusBerdasarkanUserId(uid); // Hapus dari mahasiswa jika ada
                        Dosen dosen = daoDosen.ambilBerdasarkanUserId(uid);
                        if (dosen == null) {
                            // Buat dosen baru
                            Dosen dosenBaru = new Dosen();
                            dosenBaru.setUserId(uid);
                            dosenBaru.setNip(nimNipValue.trim());
                            dosenBaru.setMaxMahasiswa(30);
                            daoDosen.simpan(dosenBaru);
                        } else {
                            dosen.setNip(nimNipValue.trim());
                            daoDosen.perbarui(dosen);
                        }
                    } else if (roleBaru == RolePengguna.MAHASISWA) {
                        daoDosen.hapusBerdasarkanUserId(uid); // Hapus dari dosen jika ada
                        Mahasiswa mhs = daoMahasiswa.ambilBerdasarkanUserId(uid);
                        if (mhs == null) {
                            // Buat mahasiswa baru
                            Mahasiswa mhsBaru = new Mahasiswa();
                            mhsBaru.setUserId(uid);
                            mhsBaru.setNim(nimNipValue.trim());
                            mhsBaru.setSemester(1);
                            daoMahasiswa.simpan(mhsBaru);
                        } else {
                            mhs.setNim(nimNipValue.trim());
                            daoMahasiswa.perbarui(mhs);
                        }
                    }
                }
                
                tampilkanInfo("Pengguna berhasil diperbarui!");
                muatDataPengguna();
            } catch (Exception e) {
                tampilkanError("Gagal memperbarui pengguna: " + e.getMessage());
            }
        });
    }

    private void tampilkanMenuStatus(Map<String, Object> user) {
        ContextMenu menu = new ContextMenu();
        
        MenuItem aktivasi = new MenuItem("Aktifkan");
        aktivasi.setOnAction(e -> ubahStatusPengguna(user, StatusPengguna.ACTIVE));
        
        MenuItem nonaktif = new MenuItem("Nonaktifkan");
        nonaktif.setOnAction(e -> ubahStatusPengguna(user, StatusPengguna.INACTIVE));
        
        MenuItem suspend = new MenuItem("Tangguhkan");
        suspend.setOnAction(e -> ubahStatusPengguna(user, StatusPengguna.SUSPENDED));

        menu.getItems().addAll(aktivasi, nonaktif, suspend);
        menu.show(tabelPengguna.getScene().getWindow());
    }

    private void ubahStatusPengguna(Map<String, Object> user, StatusPengguna status) {
        try {
            int userId = (Integer) user.get("id");
            layananPengguna.ubahStatus(userId, status);
            tampilkanInfo("Status berhasil diubah!");
            muatDataPengguna();
        } catch (SQLException e) {
            tampilkanError("Gagal mengubah status: " + e.getMessage());
        }
    }

    // ==================== UC-26: Assign Dosen ====================

    private void setupPanelAssignment() {
        listDosen.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Dosen dosen, boolean empty) {
                super.updateItem(dosen, empty);
                if (empty || dosen == null) {
                    setText(null);
                } else {
                    setText(dosen.getNama() + " (" + dosen.getJumlahMahasiswa() + "/" + dosen.getMaxMahasiswa() + ")");
                }
            }
        });

        listDosen.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            dosenTerpilih = newVal;
            if (newVal != null) {
                labelInfoDosen.setText("Dosen: " + newVal.getNama() + 
                    "\nNIP: " + (newVal.getNip() != null ? newVal.getNip() : "-") +
                    "\nKuota: " + newVal.getJumlahMahasiswa() + "/" + newVal.getMaxMahasiswa() +
                    "\nSisa: " + newVal.sisaKuota());
                btnAssign.setDisable(false);
                muatMahasiswaAssigned(newVal.getId());
            } else {
                labelInfoDosen.setText("Pilih dosen dari daftar di atas");
                btnAssign.setDisable(true);
                listMahasiswaAssigned.getItems().clear();
                btnUnassign.setDisable(true);
            }
        });

        checkSelectAll.setOnAction(e -> {
            boolean selected = checkSelectAll.isSelected();
            listMahasiswa.getItems().forEach(m -> m.setSelected(selected));
            listMahasiswa.refresh();
            updateSelectedCount();
        });

        btnAssign.setOnAction(e -> assignMahasiswaKeDosen());
        
        // Setup list mahasiswa assigned
        listMahasiswaAssigned.setCellFactory(CheckBoxListCell.forListView(MahasiswaSelectable::selectedProperty));
        
        btnUnassign.setOnAction(e -> unassignMahasiswaDariDosen());
    }

    private void muatDataAssignment() {
        try {
            // Muat daftar dosen
            List<Dosen> daftarDosen = daoDosen.ambilSemua();
            listDosen.setItems(FXCollections.observableArrayList(daftarDosen));

            // Muat mahasiswa tanpa dosen
            List<Mahasiswa> mahasiswaTanpaDosen = daoMahasiswa.ambilTanpaDosen();
            List<MahasiswaSelectable> selectableList = mahasiswaTanpaDosen.stream()
                .map(MahasiswaSelectable::new)
                .toList();
            listMahasiswa.setItems(FXCollections.observableArrayList(selectableList));
            
            listMahasiswa.setCellFactory(CheckBoxListCell.forListView(MahasiswaSelectable::selectedProperty));

            dosenTerpilih = null;
            labelInfoDosen.setText("Pilih dosen dari daftar di atas");
            btnAssign.setDisable(true);
            updateSelectedCount();
        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat data assignment: " + e.getMessage());
        }
    }

    private void updateSelectedCount() {
        long count = listMahasiswa.getItems().stream().filter(MahasiswaSelectable::isSelected).count();
        labelTerpilih.setText(count + " mahasiswa terpilih");
    }

    private void assignMahasiswaKeDosen() {
        if (dosenTerpilih == null) {
            tampilkanError("Pilih dosen terlebih dahulu!");
            return;
        }

        List<MahasiswaSelectable> selectedMahasiswa = listMahasiswa.getItems().stream()
            .filter(MahasiswaSelectable::isSelected)
            .toList();

        if (selectedMahasiswa.isEmpty()) {
            tampilkanError("Pilih minimal satu mahasiswa!");
            return;
        }

        if (selectedMahasiswa.size() > dosenTerpilih.sisaKuota()) {
            tampilkanError("Kuota dosen tidak mencukupi! Sisa kuota: " + dosenTerpilih.sisaKuota());
            return;
        }

        try {
            int berhasil = 0;
            
            for (MahasiswaSelectable ms : selectedMahasiswa) {
                Mahasiswa mhs = ms.getMahasiswa();
                
                // Jika mahasiswa belum ada di tabel mahasiswa (id == 0), buat dulu
                if (mhs.getId() == 0) {
                    Mahasiswa mhsBaru = new Mahasiswa();
                    mhsBaru.setUserId(mhs.getUserId());
                    mhsBaru.setNim(mhs.getNim());
                    mhsBaru.setSemester(mhs.getSemester() > 0 ? mhs.getSemester() : 1);
                    mhsBaru.setDosenId(dosenTerpilih.getId());
                    daoMahasiswa.simpan(mhsBaru);
                    berhasil++;
                } else {
                    // Mahasiswa sudah ada, update dosen_id
                    mhs.setDosenId(dosenTerpilih.getId());
                    daoMahasiswa.perbarui(mhs);
                    berhasil++;
                }
            }
            
            tampilkanInfo(berhasil + " mahasiswa berhasil di-assign ke " + dosenTerpilih.getNama());
            muatDataAssignment();
            perbaruiStatistik();
        } catch (SQLException e) {
            tampilkanError("Gagal assign mahasiswa: " + e.getMessage());
        }
    }

    private void muatMahasiswaAssigned(int dosenId) {
        try {
            List<Mahasiswa> mahasiswaAssigned = daoMahasiswa.ambilBerdasarkanDosen(dosenId);
            List<MahasiswaSelectable> selectableList = mahasiswaAssigned.stream()
                .map(MahasiswaSelectable::new)
                .toList();
            listMahasiswaAssigned.setItems(FXCollections.observableArrayList(selectableList));
            btnUnassign.setDisable(mahasiswaAssigned.isEmpty());
        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat mahasiswa assigned: " + e.getMessage());
        }
    }

    private void unassignMahasiswaDariDosen() {
        List<Integer> selectedIds = listMahasiswaAssigned.getItems().stream()
            .filter(MahasiswaSelectable::isSelected)
            .map(m -> m.getMahasiswa().getId())
            .toList();

        if (selectedIds.isEmpty()) {
            tampilkanError("Pilih minimal satu mahasiswa untuk di-unassign!");
            return;
        }

        try {
            int berhasil = daoMahasiswa.bulkUnassign(selectedIds);
            tampilkanInfo(berhasil + " mahasiswa berhasil di-unassign");
            muatDataAssignment();
            if (dosenTerpilih != null) {
                muatMahasiswaAssigned(dosenTerpilih.getId());
            }
            perbaruiStatistik();
        } catch (SQLException e) {
            tampilkanError("Gagal unassign mahasiswa: " + e.getMessage());
        }
    }

    // ==================== UC-28: Statistik ====================

    private void setupPanelStatistik() {
        // Set resize policy agar kolom mengisi seluruh lebar
        tabelDistribusi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        kolDosenNama.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNama()));
        kolDosenNip.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getNip() != null ? data.getValue().getNip() : "-"));
        kolJumlahMahasiswa.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getJumlahMahasiswa())));
        kolKuota.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getMaxMahasiswa())));
        kolSisaKuota.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().sisaKuota())));
    }

    private void muatDataStatistik() {
        try {
            // Statistik umum
            Map<String, Integer> stats = layananPengguna.hitungStatistikPengguna();
            labelTotalPengguna.setText(String.valueOf(stats.get("total")));
            labelTotalMahasiswa.setText(String.valueOf(stats.get("mahasiswa")));
            labelTotalDosen.setText(String.valueOf(stats.get("dosen")));

            // Mahasiswa tanpa dosen
            List<Mahasiswa> tanpaDosen = daoMahasiswa.ambilTanpaDosen();
            labelTanpaDosen.setText(String.valueOf(tanpaDosen.size()));

            // Distribusi per dosen
            List<Dosen> daftarDosen = daoDosen.ambilSemua();
            tabelDistribusi.setItems(FXCollections.observableArrayList(daftarDosen));
        } catch (SQLException e) {
            PencatatLog.error("Gagal memuat statistik: " + e.getMessage());
        }
    }

    private void perbaruiStatistik() {
        // Label statistik dihapus dari header, tidak perlu update
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }

    // ==================== Helper Methods untuk Dialog ====================

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

    // ==================== SPA Dialog Methods ====================

    private void tampilkanDialogSPA(String judul, VBox konten) {
        if (dialogOverlay == null || dialogContainer == null) return;

        // Header
        Label labelJudul = new Label(judul);
        labelJudul.getStyleClass().add("spa-dialog-title");

        // Langsung masukkan ke dialogContainer (sudah ada style spa-dialog-card)
        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().addAll(labelJudul, konten);

        // Tampilkan dengan animasi
        dialogOverlay.setVisible(true);
        dialogOverlay.setManaged(true);
        dialogOverlay.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Klik di luar untuk tutup
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

    // Helper class untuk checkbox list
    public static class MahasiswaSelectable {
        private final Mahasiswa mahasiswa;
        private final javafx.beans.property.BooleanProperty selected;

        public MahasiswaSelectable(Mahasiswa mahasiswa) {
            this.mahasiswa = mahasiswa;
            this.selected = new javafx.beans.property.SimpleBooleanProperty(false);
        }

        public Mahasiswa getMahasiswa() {
            return mahasiswa;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean value) {
            selected.set(value);
        }

        public javafx.beans.property.BooleanProperty selectedProperty() {
            return selected;
        }

        @Override
        public String toString() {
            return mahasiswa.getNama() + " (" + (mahasiswa.getNim() != null ? mahasiswa.getNim() : "-") + ")";
        }
    }
}
