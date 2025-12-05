package com.studyplanner.utilitas;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Manajer preferensi pengguna yang disimpan di file lokal.
 * Menyimpan pengaturan seperti status onboarding, dark mode, dll.
 */
public class PreferensiPengguna {

    private static PreferensiPengguna instance;
    private final Properties properties;
    private final Path filePath;

    // Keys
    private static final String KEY_ONBOARDING_SELESAI = "onboarding.selesai";
    private static final String KEY_DARK_MODE = "tampilan.dark_mode";
    private static final String KEY_DURASI_DEFAULT = "belajar.durasi_default";
    private static final String KEY_REMINDER_AKTIF = "notifikasi.reminder_aktif";
    private static final String KEY_WIDGET_CONFIG = "dashboard.widget_config";
    private static final String KEY_DARK_MODE_UNLOCKED = "tampilan.dark_mode_unlocked";
    private static final String KEY_SESSION_USER_ID = "session.user_id";
    private static final String KEY_SESSION_USERNAME = "session.username";
    private static final String KEY_SESSION_AKTIF = "session.aktif";

    private PreferensiPengguna() {
        properties = new Properties();
        filePath = Paths.get(System.getProperty("user.home"), ".studyplanner", "preferences.properties");
        System.out.println("[DEBUG] Preferences file path: " + filePath.toAbsolutePath());
        muatPreferensi();
        System.out.println("[DEBUG] Properties loaded: " + properties.size() + " items");
    }

    public static synchronized PreferensiPengguna getInstance() {
        if (instance == null) {
            instance = new PreferensiPengguna();
        }
        return instance;
    }

    private void muatPreferensi() {
        try {
            // Buat direktori jika belum ada
            Files.createDirectories(filePath.getParent());

            if (Files.exists(filePath)) {
                try (InputStream is = Files.newInputStream(filePath)) {
                    properties.load(is);
                }
            }
        } catch (IOException e) {
            PencatatLog.error("Gagal memuat preferensi: " + e.getMessage());
        }
    }

    private void simpanPreferensi() {
        try {
            Files.createDirectories(filePath.getParent());
            try (OutputStream os = Files.newOutputStream(filePath)) {
                properties.store(os, "Study Planner User Preferences");
            }
        } catch (IOException e) {
            PencatatLog.error("Gagal menyimpan preferensi: " + e.getMessage());
        }
    }

    // ===== ONBOARDING =====

    /**
     * Cek apakah onboarding sudah selesai untuk user ini.
     */
    public boolean isOnboardingSelesai(int userId) {
        String key = KEY_ONBOARDING_SELESAI + "." + userId;
        return Boolean.parseBoolean(properties.getProperty(key, "false"));
    }

    /**
     * Tandai onboarding sudah selesai untuk user ini.
     */
    public void setOnboardingSelesai(int userId, boolean selesai) {
        String key = KEY_ONBOARDING_SELESAI + "." + userId;
        properties.setProperty(key, String.valueOf(selesai));
        simpanPreferensi();
    }

    // ===== DARK MODE =====

    /**
     * Cek apakah dark mode aktif.
     */
    public boolean isDarkMode() {
        return Boolean.parseBoolean(properties.getProperty(KEY_DARK_MODE, "false"));
    }

    /**
     * Atur status dark mode.
     */
    public void setDarkMode(boolean darkMode) {
        properties.setProperty(KEY_DARK_MODE, String.valueOf(darkMode));
        simpanPreferensi();
    }

    /**
     * Cek apakah fitur dark mode sudah di-unlock (Easter egg).
     */
    public boolean isDarkModeUnlocked() {
        return Boolean.parseBoolean(properties.getProperty(KEY_DARK_MODE_UNLOCKED, "false"));
    }

    /**
     * Atur status unlock fitur dark mode.
     */
    public void setDarkModeUnlocked(boolean unlocked) {
        properties.setProperty(KEY_DARK_MODE_UNLOCKED, String.valueOf(unlocked));
        simpanPreferensi();
    }

    // ===== DURASI BELAJAR =====

    /**
     * Dapatkan durasi belajar default (menit).
     */
    public int getDurasiDefault() {
        return Integer.parseInt(properties.getProperty(KEY_DURASI_DEFAULT, "60"));
    }

    /**
     * Atur durasi belajar default (menit).
     */
    public void setDurasiDefault(int menit) {
        properties.setProperty(KEY_DURASI_DEFAULT, String.valueOf(menit));
        simpanPreferensi();
    }

    // ===== REMINDER =====

    /**
     * Cek apakah reminder aktif.
     */
    public boolean isReminderAktif() {
        return Boolean.parseBoolean(properties.getProperty(KEY_REMINDER_AKTIF, "true"));
    }

    /**
     * Atur status reminder.
     */
    public void setReminderAktif(boolean aktif) {
        properties.setProperty(KEY_REMINDER_AKTIF, String.valueOf(aktif));
        simpanPreferensi();
    }

    /**
     * Reset semua preferensi untuk user tertentu.
     */
    public void resetPreferensiUser(int userId) {
        properties.remove(KEY_ONBOARDING_SELESAI + "." + userId);
        properties.remove(KEY_WIDGET_CONFIG + "." + userId);
        simpanPreferensi();
    }

    // ===== KONFIGURASI WIDGET =====

    /**
     * Dapatkan konfigurasi widget untuk user.
     * Mengembalikan string kosong jika belum ada konfigurasi (user baru).
     */
    public String getWidgetConfig(int userId) {
        String key = KEY_WIDGET_CONFIG + "." + userId;
        return properties.getProperty(key, "");
    }

    /**
     * Simpan konfigurasi widget untuk user.
     * @param config String format: "kode1,kode2,kode3"
     */
    public void setWidgetConfig(int userId, String config) {
        String key = KEY_WIDGET_CONFIG + "." + userId;
        properties.setProperty(key, config);
        simpanPreferensi();
    }

    /**
     * Cek apakah user sudah pernah mengatur widget.
     */
    public boolean sudahAturWidget(int userId) {
        String key = KEY_WIDGET_CONFIG + "." + userId;
        return properties.containsKey(key);
    }

    // ===== SESSION LOGIN LOKAL =====

    /**
     * Simpan session login lokal.
     * @param userId ID user dari database
     * @param username Username pengguna
     */
    public void simpanSessionLokal(int userId, String username) {
        properties.setProperty(KEY_SESSION_USER_ID, String.valueOf(userId));
        properties.setProperty(KEY_SESSION_USERNAME, username);
        properties.setProperty(KEY_SESSION_AKTIF, "true");
        simpanPreferensi();
        PencatatLog.info("Session lokal disimpan untuk user: " + username);
    }

    /**
     * Hapus session login lokal (logout).
     */
    public void hapusSessionLokal() {
        properties.remove(KEY_SESSION_USER_ID);
        properties.remove(KEY_SESSION_USERNAME);
        properties.setProperty(KEY_SESSION_AKTIF, "false");
        simpanPreferensi();
        PencatatLog.info("Session lokal dihapus");
    }

    /**
     * Cek apakah ada session login lokal yang aktif.
     */
    public boolean adaSessionLokalAktif() {
        String value = properties.getProperty(KEY_SESSION_AKTIF, "false");
        boolean aktif = Boolean.parseBoolean(value);
        System.out.println("[DEBUG] Session aktif check: " + value + " -> " + aktif);
        System.out.println("[DEBUG] Session user ID: " + properties.getProperty(KEY_SESSION_USER_ID, "tidak ada"));
        return aktif;
    }

    /**
     * Dapatkan user ID dari session yang tersimpan.
     * @return User ID atau -1 jika tidak ada session
     */
    public int getSessionUserId() {
        String idStr = properties.getProperty(KEY_SESSION_USER_ID, "-1");
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Dapatkan username dari session yang tersimpan.
     */
    public String getSessionUsername() {
        return properties.getProperty(KEY_SESSION_USERNAME, "");
    }
}
