package com.studyplanner.utilitas;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

/**
 * Utilitas untuk membuat ikon Material Design dengan Ikonli
 */
public class PembuatIkon {

    /**
     * Buat ikon dengan ukuran dan warna default
     */
    public static FontIcon buat(Ikon kodeIkon) {
        return buat(kodeIkon, 20);
    }

    /**
     * Buat ikon dengan ukuran custom
     */
    public static FontIcon buat(Ikon kodeIkon, int ukuran) {
        FontIcon icon = new FontIcon(kodeIkon);
        icon.setIconSize(ukuran);
        return icon;
    }

    /**
     * Buat ikon dengan ukuran dan warna custom
     */
    public static FontIcon buat(Ikon kodeIkon, int ukuran, String warna) {
        FontIcon icon = new FontIcon(kodeIkon);
        icon.setIconSize(ukuran);
        icon.setIconColor(Color.web(warna));
        return icon;
    }

    /**
     * Buat ikon dengan style CSS
     */
    public static FontIcon buatDenganStyle(Ikon kodeIkon, int ukuran, String style) {
        FontIcon icon = new FontIcon(kodeIkon);
        icon.setIconSize(ukuran);
        icon.setStyle(style);
        return icon;
    }

    /**
     * Ikon untuk "Kelola Mata Kuliah"
     */
    public static Node ikonMataKuliah() {
        return buat(Material2OutlinedAL.BOOK, 20);
    }

    /**
     * Ikon untuk "Lihat Jadwal"
     */
    public static Node ikonJadwal() {
        return buat(Material2OutlinedAL.EVENT, 20);
    }

    /**
     * Ikon untuk "Buat Jadwal"
     */
    public static Node ikonBuatJadwal() {
        return buat(Material2OutlinedMZ.SCHEDULE, 20);
    }

    /**
     * Ikon untuk profil pengguna
     */
    public static Node ikonProfil() {
        return buat(Material2OutlinedMZ.PERSON, 20);
    }

    /**
     * Ikon untuk pengaturan
     */
    public static Node ikonPengaturan() {
        return buat(Material2OutlinedMZ.SETTINGS, 20);
    }

    /**
     * Ikon untuk keluar/logout
     */
    public static Node ikonKeluar() {
        return buat(Material2OutlinedAL.EXIT_TO_APP, 20);
    }

    /**
     * Ikon untuk mode terang/gelap
     */
    public static Node ikonModeGelap(boolean modeGelap) {
        if (modeGelap) {
            return buat(Material2OutlinedAL.BRIGHTNESS_7, 20);
        } else {
            return buat(Material2OutlinedAL.BRIGHTNESS_4, 20);
        }
    }

    /**
     * Ikon untuk menu toggle
     */
    public static Node ikonMenu() {
        return buat(Material2OutlinedMZ.MENU, 24);
    }

    /**
     * Ikon untuk tombol tambah
     */
    public static Node ikonTambah() {
        return buat(Material2OutlinedAL.ADD, 18);
    }

    /**
     * Ikon untuk tombol edit
     */
    public static Node ikonEdit() {
        return buat(Material2OutlinedAL.EDIT, 18);
    }

    /**
     * Ikon untuk tombol hapus
     */
    public static Node ikonHapus() {
        return buat(Material2OutlinedAL.DELETE, 18);
    }

    /**
     * Ikon untuk tombol close
     */
    public static Node ikonTutup() {
        return buat(Material2OutlinedAL.CLOSE, 18);
    }

    /**
     * Ikon untuk check/selesai
     */
    public static Node ikonSelesai() {
        return buat(Material2OutlinedAL.CHECK_CIRCLE, 18);
    }

    /**
     * Ikon untuk warning
     */
    public static Node ikonPeringatan() {
        return buat(Material2OutlinedMZ.WARNING, 18);
    }

    /**
     * Ikon untuk error
     */
    public static Node ikonError() {
        return buat(Material2OutlinedAL.ERROR, 18);
    }

    /**
     * Ikon untuk info
     */
    public static Node ikonInfo() {
        return buat(Material2OutlinedAL.INFO, 18);
    }

    /**
     * Ikon untuk school/belajar
     */
    public static Node ikonBelajar() {
        return buat(Material2OutlinedMZ.SCHOOL, 32);
    }

    /**
     * Ikon untuk statistik
     */
    public static Node ikonStatistik() {
        return buat(Material2OutlinedAL.BAR_CHART, 20);
    }

    /**
     * Ikon panah kiri untuk navigasi
     */
    public static Node ikonPanahKiri() {
        return buat(Material2OutlinedAL.CHEVRON_LEFT, 24);
    }

    /**
     * Ikon panah kiri dengan ukuran custom
     */
    public static Node ikonPanahKiri(int ukuran) {
        return buat(Material2OutlinedAL.CHEVRON_LEFT, ukuran);
    }

    /**
     * Ikon panah kanan untuk navigasi
     */
    public static Node ikonPanahKanan() {
        return buat(Material2OutlinedAL.CHEVRON_RIGHT, 24);
    }

    /**
     * Ikon panah kanan dengan ukuran custom
     */
    public static Node ikonPanahKanan(int ukuran) {
        return buat(Material2OutlinedAL.CHEVRON_RIGHT, ukuran);
    }

    /**
     * Ikon untuk timer/stopwatch
     */
    public static Node ikonTimer() {
        return buat(Material2OutlinedMZ.TIMER, 20);
    }

    /**
     * Ikon untuk streak/runtutan
     */
    public static Node ikonStreak() {
        return buat(Material2OutlinedAL.LOCAL_FIRE_DEPARTMENT, 20);
    }

    /**
     * Ikon untuk review
     */
    public static Node ikonReview() {
        return buat(Material2OutlinedMZ.RATE_REVIEW, 20);
    }

    /**
     * Ikon untuk task/tugas
     */
    public static Node ikonTugas() {
        return buat(Material2OutlinedAL.ASSIGNMENT, 20);
    }

    /**
     * Ikon untuk tampilan/appearance
     */
    public static Node ikonTampilan() {
        return buat(Material2OutlinedMZ.PALETTE, 20);
    }

    /**
     * Ikon untuk pembelajaran
     */
    public static Node ikonPembelajaran() {
        return buat(Material2OutlinedMZ.SCHOOL, 20);
    }

    /**
     * Ikon untuk backup/storage
     */
    public static Node ikonBackup() {
        return buat(Material2OutlinedMZ.STORAGE, 20);
    }

    /**
     * Ikon untuk tentang
     */
    public static Node ikonTentang() {
        return buat(Material2OutlinedAL.INFO, 20);
    }

    /**
     * Ikon untuk notifikasi
     */
    public static Node ikonNotifikasi() {
        return buat(Material2OutlinedMZ.NOTIFICATIONS, 20);
    }

    /**
     * Ikon untuk durasi/waktu
     */
    public static Node ikonDurasi() {
        return buat(Material2OutlinedMZ.SCHEDULE, 20);
    }

    /**
     * Ikon untuk ekspor
     */
    public static Node ikonEkspor() {
        return buat(Material2OutlinedAL.GET_APP, 20);
    }

    /**
     * Ikon untuk copyright
     */
    public static Node ikonCopyright() {
        return buat(Material2OutlinedAL.COPYRIGHT, 16);
    }

    /**
     * Ikon kembali/back arrow
     */
    public static Node ikonKembali() {
        return buat(Material2OutlinedAL.ARROW_BACK, 20);
    }

    /**
     * Ikon untuk empty state - tidak ada tugas
     */
    public static FontIcon ikonKosongTugas() {
        return buat(Material2OutlinedAL.ASSIGNMENT, 48, "#94a3b8");
    }

    /**
     * Ikon untuk empty state - tidak ada ujian
     */
    public static FontIcon ikonKosongUjian() {
        return buat(Material2OutlinedAL.EVENT_NOTE, 48, "#94a3b8");
    }

    /**
     * Ikon untuk empty state - tidak ada review
     */
    public static FontIcon ikonKosongReview() {
        return buat(Material2OutlinedMZ.RATE_REVIEW, 48, "#94a3b8");
    }

    /**
     * Ikon untuk empty state - tidak ada mata kuliah
     */
    public static FontIcon ikonKosongMataKuliah() {
        return buat(Material2OutlinedAL.BOOK, 48, "#94a3b8");
    }

    /**
     * Ikon untuk empty state - tidak ada topik
     */
    public static FontIcon ikonKosongTopik() {
        return buat(Material2OutlinedAL.LIBRARY_BOOKS, 48, "#94a3b8");
    }

    /**
     * Ikon untuk empty state - tidak ada jadwal
     */
    public static FontIcon ikonKosongJadwal() {
        return buat(Material2OutlinedMZ.SCHEDULE, 48, "#94a3b8");
    }

    /**
     * Ikon untuk empty state - semua selesai / celebrasi
     */
    public static FontIcon ikonSemuaSelesai() {
        return buat(Material2OutlinedAL.CHECK_CIRCLE, 48, "#22c55e");
    }

    /**
     * Ikon untuk onboarding - selamat datang
     */
    public static FontIcon ikonOnboardingSelamatDatang() {
        return buat(Material2OutlinedAL.EMOJI_PEOPLE, 64, "#6366f1");
    }

    /**
     * Ikon untuk onboarding - tambah mata kuliah
     */
    public static FontIcon ikonOnboardingMataKuliah() {
        return buat(Material2OutlinedAL.ADD_BOX, 64, "#6366f1");
    }

    /**
     * Ikon untuk onboarding - buat jadwal
     */
    public static FontIcon ikonOnboardingJadwal() {
        return buat(Material2OutlinedAL.CALENDAR_TODAY, 64, "#6366f1");
    }

    /**
     * Ikon untuk onboarding - selesai
     */
    public static FontIcon ikonOnboardingSelesai() {
        return buat(Material2OutlinedMZ.STAR, 64, "#22c55e");
    }

    /**
     * Ikon untuk navigasi next
     */
    public static FontIcon ikonNext() {
        return buat(Material2OutlinedAL.ARROW_FORWARD, 20);
    }

    /**
     * Ikon untuk navigasi back
     */
    public static FontIcon ikonBack() {
        return buat(Material2OutlinedAL.ARROW_BACK, 20);
    }

    // ===== IKON UNTUK WIDGET PICKER =====

    /**
     * Ikon untuk simpan
     */
    public static FontIcon ikonSimpan() {
        return buat(Material2OutlinedMZ.SAVE, 18);
    }

    /**
     * Ikon untuk widget runtutan belajar
     */
    public static FontIcon ikonRuntutan() {
        return buat(Material2OutlinedAL.LOCAL_FIRE_DEPARTMENT, 24, "#f97316");
    }

    /**
     * Ikon untuk widget jam
     */
    public static FontIcon ikonJam() {
        return buat(Material2OutlinedMZ.SCHEDULE, 24, "#6366f1");
    }

    /**
     * Ikon untuk widget waktu belajar
     */
    public static FontIcon ikonWaktuBelajar() {
        return buat(Material2OutlinedMZ.TIMER, 24, "#22c55e");
    }

    /**
     * Ikon untuk widget ulasan berikutnya
     */
    public static FontIcon ikonUlasan() {
        return buat(Material2OutlinedMZ.RATE_REVIEW, 24, "#eab308");
    }

    /**
     * Ikon untuk widget tugas mendatang
     */
    public static FontIcon ikonTugasMendatang() {
        return buat(Material2OutlinedAL.ASSIGNMENT, 24, "#3b82f6");
    }

    /**
     * Ikon untuk widget countdown ujian
     */
    public static FontIcon ikonCountdownUjian() {
        return buat(Material2OutlinedAL.ALARM, 24, "#ef4444");
    }

    /**
     * Ikon untuk tambah widget
     */
    public static FontIcon ikonTambahWidget() {
        return buat(Material2OutlinedAL.ADD_CIRCLE_OUTLINE, 32, "#94a3b8");
    }

    /**
     * Ikon untuk drag handle
     */
    public static FontIcon ikonDragHandle() {
        return buat(Material2OutlinedAL.DRAG_INDICATOR, 20, "#94a3b8");
    }

    /**
     * Ikon untuk panel admin
     */
    public static FontIcon ikonAdmin() {
        return buat(Material2OutlinedAL.ADMIN_PANEL_SETTINGS, 20, "#6366f1");
    }

    /**
     * Ikon untuk panel dosen
     */
    public static FontIcon ikonDosen() {
        return buat(Material2OutlinedMZ.SCHOOL, 20, "#0ea5e9");
    }
}
