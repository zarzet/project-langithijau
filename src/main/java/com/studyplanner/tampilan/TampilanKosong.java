package com.studyplanner.tampilan;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Komponen untuk menampilkan empty state dengan ikon dan aksi.
 * Digunakan ketika tidak ada data untuk ditampilkan.
 */
public class TampilanKosong extends VBox {

    private final Label labelJudul;
    private final Label labelDeskripsi;
    private final VBox wadahIkon;
    private Button tombolAksi;

    /**
     * Buat EmptyStateView sederhana dengan ikon dan teks.
     */
    public TampilanKosong(FontIcon ikon, String judul) {
        this(ikon, judul, null, null, null);
    }

    /**
     * Buat EmptyStateView dengan ikon, judul, dan deskripsi.
     */
    public TampilanKosong(FontIcon ikon, String judul, String deskripsi) {
        this(ikon, judul, deskripsi, null, null);
    }

    /**
     * Buat EmptyStateView lengkap dengan ikon, judul, deskripsi, dan tombol aksi.
     */
    public TampilanKosong(FontIcon ikon, String judul, String deskripsi, String tekstombol, Runnable aksi) {
        setAlignment(Pos.CENTER);
        setSpacing(12);
        setPadding(new Insets(24));
        getStyleClass().add("empty-state-view");

        // Wadah ikon
        wadahIkon = new VBox();
        wadahIkon.setAlignment(Pos.CENTER);
        wadahIkon.getStyleClass().add("empty-state-icon");
        if (ikon != null) {
            wadahIkon.getChildren().add(ikon);
        }

        // Label judul
        labelJudul = new Label(judul);
        labelJudul.getStyleClass().add("empty-state-title");
        labelJudul.setWrapText(true);

        // Label deskripsi
        labelDeskripsi = new Label();
        labelDeskripsi.getStyleClass().add("empty-state-description");
        labelDeskripsi.setWrapText(true);
        labelDeskripsi.setVisible(deskripsi != null && !deskripsi.isEmpty());
        labelDeskripsi.setManaged(deskripsi != null && !deskripsi.isEmpty());
        if (deskripsi != null) {
            labelDeskripsi.setText(deskripsi);
        }

        getChildren().addAll(wadahIkon, labelJudul, labelDeskripsi);

        // Tombol aksi (opsional)
        if (tekstombol != null && aksi != null) {
            tombolAksi = new Button(tekstombol);
            tombolAksi.getStyleClass().addAll("btn-primary", "empty-state-button");
            tombolAksi.setOnAction(_ -> aksi.run());
            getChildren().add(tombolAksi);
        }
    }

    /**
     * Atur ikon baru.
     */
    public void setIkon(FontIcon ikon) {
        wadahIkon.getChildren().clear();
        if (ikon != null) {
            wadahIkon.getChildren().add(ikon);
        }
    }

    /**
     * Atur judul baru.
     */
    public void setJudul(String judul) {
        labelJudul.setText(judul);
    }

    /**
     * Atur deskripsi baru.
     */
    public void setDeskripsi(String deskripsi) {
        labelDeskripsi.setText(deskripsi);
        labelDeskripsi.setVisible(deskripsi != null && !deskripsi.isEmpty());
        labelDeskripsi.setManaged(deskripsi != null && !deskripsi.isEmpty());
    }

    // ===== FACTORY METHODS UNTUK EMPTY STATES UMUM =====

    /**
     * Tampilan kosong untuk tidak ada tugas hari ini.
     */
    public static TampilanKosong untukTugasKosong(Runnable aksiBuatJadwal) {
        return new TampilanKosong(
            com.studyplanner.utilitas.PembuatIkon.ikonKosongTugas(),
            "Belum ada tugas",
            "Klik 'Buat Jadwal' untuk membuat jadwal belajar otomatis.",
            "Buat Jadwal",
            aksiBuatJadwal
        );
    }

    /**
     * Tampilan kosong untuk tidak ada ujian.
     */
    public static TampilanKosong untukUjianKosong(Runnable aksiTambahUjian) {
        return new TampilanKosong(
            com.studyplanner.utilitas.PembuatIkon.ikonKosongUjian(),
            "Belum ada ujian",
            "Tambahkan jadwal ujian untuk melacak persiapan.",
            aksiTambahUjian != null ? "Tambah Ujian" : null,
            aksiTambahUjian
        );
    }

    /**
     * Tampilan kosong untuk tidak ada ulasan.
     */
    public static TampilanKosong untukUlasanKosong() {
        return new TampilanKosong(
            com.studyplanner.utilitas.PembuatIkon.ikonSemuaSelesai(),
            "Semua selesai!",
            "Tidak ada topik yang perlu diulang saat ini."
        );
    }

    /**
     * Tampilan kosong untuk tidak ada mata kuliah.
     */
    public static TampilanKosong untukMataKuliahKosong(Runnable aksiTambah) {
        return new TampilanKosong(
            com.studyplanner.utilitas.PembuatIkon.ikonKosongMataKuliah(),
            "Belum ada mata kuliah",
            "Mulai dengan menambahkan mata kuliah pertama Anda.",
            "Tambah Mata Kuliah",
            aksiTambah
        );
    }

    /**
     * Tampilan kosong untuk tidak ada topik.
     */
    public static TampilanKosong untukTopikKosong(Runnable aksiTambah) {
        return new TampilanKosong(
            com.studyplanner.utilitas.PembuatIkon.ikonKosongTopik(),
            "Belum ada topik",
            "Tambahkan topik untuk mulai belajar.",
            aksiTambah != null ? "Tambah Topik" : null,
            aksiTambah
        );
    }

    /**
     * Tampilan kosong untuk tugas mendatang kosong.
     */
    public static TampilanKosong untukTugasMendatangKosong() {
        return new TampilanKosong(
            com.studyplanner.utilitas.PembuatIkon.ikonKosongJadwal(),
            "Tidak ada tugas mendatang",
            "Jadwal belajar Anda kosong untuk beberapa hari ke depan."
        );
    }
}
