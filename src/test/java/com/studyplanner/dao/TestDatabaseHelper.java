package com.studyplanner.dao;

import java.sql.*;

/**
 * Helper class untuk menyediakan database in-memory untuk testing.
 * Menggunakan SQLite in-memory database yang terisolasi per test.
 */
public class TestDatabaseHelper {

    private static final String IN_MEMORY_URL = "jdbc:sqlite::memory:";
    private Connection koneksi;

    /**
     * Membuat koneksi ke database in-memory baru.
     */
    public void bukaKoneksi() throws SQLException {
        koneksi = DriverManager.getConnection(IN_MEMORY_URL);
        buatTabel();
    }

    /**
     * Menutup koneksi database.
     */
    public void tutupKoneksi() throws SQLException {
        if (koneksi != null && !koneksi.isClosed()) {
            koneksi.close();
        }
    }

    /**
     * Mengembalikan koneksi wrapper yang tidak menutup koneksi asli.
     * Ini diperlukan karena DAO menggunakan try-with-resources yang akan menutup koneksi,
     * tapi untuk in-memory SQLite, koneksi harus tetap terbuka.
     */
    public Connection getKoneksi() throws SQLException {
        if (koneksi == null || koneksi.isClosed()) {
            bukaKoneksi();
        }
        return new NonClosingConnectionWrapper(koneksi);
    }

    /**
     * Membersihkan semua data dari tabel.
     */
    public void bersihkanData() throws SQLException {
        try (Statement stmt = koneksi.createStatement()) {
            stmt.execute("DELETE FROM sesi_belajar");
            stmt.execute("DELETE FROM jadwal_ujian");
            stmt.execute("DELETE FROM topik");
            stmt.execute("DELETE FROM mata_kuliah");
            stmt.execute("DELETE FROM users");
        }
    }

    private void buatTabel() throws SQLException {
        String buatTabelUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT,
                email TEXT,
                google_id TEXT UNIQUE,
                nama TEXT NOT NULL,
                foto_profil TEXT,
                provider TEXT NOT NULL,
                dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String buatTabelMataKuliah = """
            CREATE TABLE IF NOT EXISTS mata_kuliah (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL DEFAULT 1,
                nama TEXT NOT NULL,
                kode TEXT NOT NULL,
                deskripsi TEXT,
                dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE(user_id, kode)
            )
        """;

        String buatTabelTopik = """
            CREATE TABLE IF NOT EXISTS topik (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_mata_kuliah INTEGER NOT NULL,
                nama TEXT NOT NULL,
                deskripsi TEXT,
                prioritas INTEGER DEFAULT 3,
                tingkat_kesulitan INTEGER DEFAULT 3,
                tanggal_belajar_pertama DATE,
                tanggal_ulasan_terakhir DATE,
                jumlah_ulasan INTEGER DEFAULT 0,
                faktor_kemudahan REAL DEFAULT 2.5,
                interval INTEGER DEFAULT 1,
                stabilitas_fsrs REAL DEFAULT 0,
                kesulitan_fsrs REAL DEFAULT 0,
                retensi_diinginkan REAL DEFAULT 0.9,
                peluruhan_fsrs REAL DEFAULT 0.1542,
                dikuasai BOOLEAN DEFAULT 0,
                dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_mata_kuliah) REFERENCES mata_kuliah(id) ON DELETE CASCADE
            )
        """;

        String buatTabelJadwalUjian = """
            CREATE TABLE IF NOT EXISTS jadwal_ujian (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_mata_kuliah INTEGER NOT NULL,
                tipe_ujian TEXT NOT NULL,
                judul TEXT NOT NULL,
                tanggal_ujian DATE NOT NULL,
                waktu_ujian TIME,
                lokasi TEXT,
                catatan TEXT,
                selesai BOOLEAN DEFAULT 0,
                dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_mata_kuliah) REFERENCES mata_kuliah(id) ON DELETE CASCADE
            )
        """;

        String buatTabelSesiBelajar = """
            CREATE TABLE IF NOT EXISTS sesi_belajar (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_topik INTEGER NOT NULL,
                id_mata_kuliah INTEGER NOT NULL,
                tanggal_jadwal DATE NOT NULL,
                tipe_sesi TEXT NOT NULL,
                selesai BOOLEAN DEFAULT 0,
                selesai_pada TIMESTAMP,
                rating_performa INTEGER,
                catatan TEXT,
                durasi_menit INTEGER DEFAULT 30,
                dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_topik) REFERENCES topik(id) ON DELETE CASCADE,
                FOREIGN KEY (id_mata_kuliah) REFERENCES mata_kuliah(id) ON DELETE CASCADE
            )
        """;

        try (Statement stmt = koneksi.createStatement()) {
            stmt.execute(buatTabelUsers);
            stmt.execute(buatTabelMataKuliah);
            stmt.execute(buatTabelTopik);
            stmt.execute(buatTabelJadwalUjian);
            stmt.execute(buatTabelSesiBelajar);
        }
    }
}
