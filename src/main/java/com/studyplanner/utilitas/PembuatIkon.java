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
}
