package com.studyplanner.kontroler.pembantu;

import com.google.api.services.oauth2.model.Userinfo;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.utilitas.PembuatIkon;
import com.studyplanner.utilitas.PreferensiPengguna;
import com.studyplanner.utilitas.UtilUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Window;

/**
 * Pembantu untuk membangun UI pengaturan.
 * Memisahkan logika pembuatan UI pengaturan dari KontrolerUtama.
 */
public class PembantuPengaturan {

    // Easter egg counter
    private int hitungKlikProfil = 0;
    private boolean darkModeUnlocked;
    
    // References untuk Easter egg
    private HBox darkModeRowRef;
    private VBox appearanceSectionRef;
    
    // Callbacks
    private final Runnable onDarkModeToggle;
    private final Runnable onBuatJadwal;
    private final boolean isDarkMode;

    /**
     * Konstruktor PembantuPengaturan.
     *
     * @param isDarkMode status dark mode saat ini
     * @param onDarkModeToggle callback saat dark mode di-toggle
     * @param onBuatJadwal callback saat generate jadwal
     */
    public PembantuPengaturan(boolean isDarkMode, Runnable onDarkModeToggle, Runnable onBuatJadwal) {
        this.isDarkMode = isDarkMode;
        this.darkModeUnlocked = PreferensiPengguna.getInstance().isDarkModeUnlocked();
        this.onDarkModeToggle = onDarkModeToggle;
        this.onBuatJadwal = onBuatJadwal;
    }

    /**
     * Bangun seluruh konten pengaturan.
     *
     * @param headerNode header dengan tombol kembali
     * @return VBox berisi seluruh konten pengaturan
     */
    public VBox bangunKontenPengaturan(Node headerNode) {
        VBox settingsContent = new VBox(24);
        settingsContent.setPadding(new Insets(24));
        settingsContent.getStyleClass().add("settings-window");

        // Section Profil
        VBox profileSection = buatSectionProfil();

        // Section Tampilan (Easter egg)
        appearanceSectionRef = buatSectionTampilan();

        // Section Pembelajaran
        VBox studySection = buatSectionPembelajaran();

        // Section Data & Backup
        VBox dataSection = buatSectionData();

        // Section Tentang
        VBox aboutSection = buatSectionTentang();

        settingsContent.getChildren().addAll(
            headerNode,
            profileSection,
            appearanceSectionRef,
            studySection,
            dataSection,
            aboutSection
        );

        return settingsContent;
    }

    /**
     * Buat section profil dengan Easter egg.
     */
    private VBox buatSectionProfil() {
        VBox section = new VBox(16);
        section.getStyleClass().add("settings-profile-section");

        HBox profileCard = new HBox(16);
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.getStyleClass().add("settings-profile-card");
        profileCard.setPadding(new Insets(16));

        if (ManajerOtentikasi.getInstance().isLoggedIn()) {
            Userinfo user = ManajerOtentikasi.getInstance().getCurrentUser();

            // Avatar dengan click counter untuk Easter egg
            StackPane avatarContainer = new StackPane();
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
            avatarContainer.getChildren().add(avatarView);
            avatarContainer.setCursor(javafx.scene.Cursor.HAND);

            // Easter egg: klik 10x untuk unlock dark mode
            avatarContainer.setOnMouseClicked(event -> {
                hitungKlikProfil++;

                Window window = avatarContainer.getScene() != null
                    ? avatarContainer.getScene().getWindow() : null;

                if (hitungKlikProfil >= 7 && hitungKlikProfil < 10) {
                    int sisa = 10 - hitungKlikProfil;
                    UtilUI.tampilkanToast(window, "* " + sisa + " klik lagi...");
                } else if (hitungKlikProfil == 10 && !darkModeUnlocked) {
                    darkModeUnlocked = true;
                    PreferensiPengguna.getInstance().setDarkModeUnlocked(true);

                    if (appearanceSectionRef != null) {
                        appearanceSectionRef.setVisible(true);
                        appearanceSectionRef.setManaged(true);
                    }
                    if (darkModeRowRef != null) {
                        darkModeRowRef.setVisible(true);
                        darkModeRowRef.setManaged(true);
                    }

                    UtilUI.tampilkanToast(window, "Mode Gelap telah dibuka!");
                    hitungKlikProfil = 0;
                }
            });

            // Info
            VBox infoBox = new VBox(2);
            HBox.setHgrow(infoBox, Priority.ALWAYS);

            Label nameLabel = new Label(user.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            infoBox.getChildren().add(nameLabel);

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                Label emailLabel = new Label(user.getEmail());
                emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
                infoBox.getChildren().add(emailLabel);
            }

            Label providerLabel = new Label("Masuk dengan Google");
            providerLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
            infoBox.getChildren().add(providerLabel);

            profileCard.getChildren().addAll(avatarContainer, infoBox);
        } else {
            Label notLoggedIn = new Label("Belum masuk");
            notLoggedIn.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
            profileCard.getChildren().add(notLoggedIn);
        }

        section.getChildren().add(profileCard);
        return section;
    }

    /**
     * Buat section tampilan (dark mode).
     */
    private VBox buatSectionTampilan() {
        VBox section = buatSection("Tampilan", PembuatIkon.ikonTampilan());

        darkModeRowRef = buatSettingRow(
            "Mode Gelap",
            "Ubah tema aplikasi menjadi gelap untuk kenyamanan mata di malam hari"
        );
        CheckBox darkModeCheck = new CheckBox();
        darkModeCheck.setSelected(isDarkMode);
        darkModeCheck.setOnAction(_ -> {
            if (onDarkModeToggle != null) onDarkModeToggle.run();
        });
        darkModeRowRef.getChildren().add(darkModeCheck);

        // Sembunyikan jika belum di-unlock
        if (!darkModeUnlocked) {
            darkModeRowRef.setVisible(false);
            darkModeRowRef.setManaged(false);
            section.setVisible(false);
            section.setManaged(false);
        }

        section.getChildren().add(darkModeRowRef);
        return section;
    }

    /**
     * Buat section pembelajaran.
     */
    private VBox buatSectionPembelajaran() {
        VBox section = buatSection("Pembelajaran", PembuatIkon.ikonPembelajaran());

        HBox durationRow = buatSettingRow(
            "Durasi Belajar Default",
            "Durasi standar untuk sesi belajar baru (dalam menit)"
        );
        ComboBox<String> durationCombo = new ComboBox<>();
        durationCombo.getItems().addAll("30 menit", "45 menit", "60 menit", "90 menit", "120 menit");
        durationCombo.setValue("60 menit");
        durationCombo.setStyle("-fx-pref-width: 140px;");
        durationRow.getChildren().add(durationCombo);

        HBox reminderRow = buatSettingRow(
            "Pengingat Belajar",
            "Tampilkan notifikasi untuk mengingatkan jadwal belajar"
        );
        CheckBox reminderCheck = new CheckBox();
        reminderCheck.setSelected(true);
        reminderRow.getChildren().add(reminderCheck);

        HBox generateRow = buatSettingRow(
            "Generate Jadwal Manual",
            "Buat ulang jadwal belajar untuk 7 hari ke depan (override)"
        );
        Button generateBtn = new Button("Buat Jadwal");
        generateBtn.setGraphic(PembuatIkon.ikonBuatJadwal());
        generateBtn.getStyleClass().add("btn-secondary");
        generateBtn.setStyle("-fx-pref-width: 140px;");
        generateBtn.setOnAction(_ -> {
            if (onBuatJadwal != null) onBuatJadwal.run();
        });
        generateRow.getChildren().add(generateBtn);

        section.getChildren().addAll(durationRow, reminderRow, generateRow);
        return section;
    }

    /**
     * Buat section data & backup.
     */
    private VBox buatSectionData() {
        VBox section = buatSection("Data & Backup", PembuatIkon.ikonBackup());

        HBox backupRow = buatSettingRow(
            "Backup Otomatis",
            "Backup database secara otomatis setiap hari"
        );
        CheckBox backupCheck = new CheckBox();
        backupCheck.setSelected(false);
        backupRow.getChildren().add(backupCheck);

        HBox exportRow = buatSettingRow(
            "Ekspor Data",
            "Ekspor semua data Anda ke file JSON"
        );
        Button exportBtn = new Button("Ekspor");
        exportBtn.getStyleClass().add("btn-secondary");
        exportBtn.setStyle("-fx-pref-width: 100px;");
        exportRow.getChildren().add(exportBtn);

        section.getChildren().addAll(backupRow, exportRow);
        return section;
    }

    /**
     * Buat section tentang.
     */
    private VBox buatSectionTentang() {
        VBox section = buatSection("Tentang", PembuatIkon.ikonTentang());

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
        copyrightBox.setAlignment(Pos.CENTER_LEFT);
        copyrightBox.getChildren().add(PembuatIkon.ikonCopyright());
        Label copyright = new Label("2025 - Dibuat dengan JavaFX 25");
        copyright.getStyleClass().add("settings-about-copyright");
        copyrightBox.getChildren().add(copyright);

        aboutContent.getChildren().addAll(appName, version, description, copyrightBox);
        section.getChildren().add(aboutContent);
        return section;
    }

    /**
     * Buat section dengan judul dan ikon.
     */
    private VBox buatSection(String title, Node icon) {
        VBox section = new VBox(16);

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
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

    /**
     * Buat row pengaturan.
     */
    private HBox buatSettingRow(String title, String description) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("settings-row");

        VBox textBox = new VBox(4);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("settings-row-title");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.getStyleClass().add("settings-row-description");

        textBox.getChildren().addAll(titleLabel, descLabel);
        row.getChildren().add(textBox);

        return row;
    }
}
