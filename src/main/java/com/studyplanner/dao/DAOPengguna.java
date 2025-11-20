package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object untuk mengelola data pengguna (users).
 * Menyediakan operasi CRUD dan query khusus untuk entitas User.
 */
public class DAOPengguna implements DAOBase<Map<String, Object>, Integer> {

    private final ManajerBasisData manajerDB;

    public DAOPengguna(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    @Override
    public Integer simpan(Map<String, Object> pengguna) throws SQLException {
        String provider = (String) pengguna.get("provider");

        if ("google".equals(provider)) {
            return simpanPenggunaGoogle(
                    (String) pengguna.get("google_id"),
                    (String) pengguna.get("email"),
                    (String) pengguna.get("nama"),
                    (String) pengguna.get("foto_profil")
            );
        } else {
            return simpanPenggunaLokal(
                    (String) pengguna.get("username"),
                    (String) pengguna.get("password"),
                    (String) pengguna.get("email"),
                    (String) pengguna.get("nama")
            );
        }
    }

    /**
     * Menyimpan pengguna baru dengan autentikasi lokal (username & password).
     *
     * @param username Username unik
     * @param password Password yang sudah di-hash (SHA-256)
     * @param email Email pengguna (opsional)
     * @param nama Nama lengkap pengguna
     * @return ID pengguna yang baru dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int simpanPenggunaLokal(String username, String password, String email, String nama) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, nama, provider) VALUES (?, ?, ?, ?, 'local')";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, nama);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException("Gagal membuat pengguna, tidak ada ID yang dikembalikan");
        }
    }

    /**
     * Menyimpan pengguna baru dengan autentikasi Google OAuth.
     *
     * @param googleId ID unik dari Google
     * @param email Email dari Google account
     * @param nama Nama dari Google account
     * @param fotoProfil URL foto profil dari Google
     * @return ID pengguna yang baru dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int simpanPenggunaGoogle(String googleId, String email, String nama, String fotoProfil) throws SQLException {
        String sql = "INSERT INTO users (google_id, email, nama, foto_profil, provider) VALUES (?, ?, ?, ?, 'google')";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, googleId);
            pstmt.setString(2, email);
            pstmt.setString(3, nama);
            pstmt.setString(4, fotoProfil);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException("Gagal membuat pengguna Google, tidak ada ID yang dikembalikan");
        }
    }

    @Override
    public Map<String, Object> ambilBerdasarkanId(Integer id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMap(rs);
            }

            return null;
        }
    }

    /**
     * Mencari pengguna berdasarkan username.
     *
     * @param username Username yang dicari
     * @return Data pengguna atau null jika tidak ditemukan
     * @throws SQLException jika terjadi kesalahan database
     */
    public Map<String, Object> cariBerdasarkanUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMap(rs);
            }

            return null;
        }
    }

    /**
     * Mencari pengguna berdasarkan Google ID.
     *
     * @param googleId Google ID yang dicari
     * @return Data pengguna atau null jika tidak ditemukan
     * @throws SQLException jika terjadi kesalahan database
     */
    public Map<String, Object> cariBerdasarkanGoogleId(String googleId) throws SQLException {
        String sql = "SELECT * FROM users WHERE google_id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, googleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMap(rs);
            }

            return null;
        }
    }

    @Override
    public List<Map<String, Object>> ambilSemua() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY dibuat_pada DESC";
        List<Map<String, Object>> daftarPengguna = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarPengguna.add(mapRowKeMap(rs));
            }
        }

        return daftarPengguna;
    }

    @Override
    public boolean perbarui(Map<String, Object> pengguna) throws SQLException {
        String sql = "UPDATE users SET nama = ?, email = ?, foto_profil = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) pengguna.get("nama"));
            pstmt.setString(2, (String) pengguna.get("email"));
            pstmt.setString(3, (String) pengguna.get("foto_profil"));
            pstmt.setInt(4, (Integer) pengguna.get("id"));

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Memperbarui password pengguna lokal.
     *
     * @param userId ID pengguna
     * @param passwordBaru Password baru yang sudah di-hash
     * @return true jika berhasil diperbarui
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbaruiPassword(int userId, String passwordBaru) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ? AND provider = 'local'";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, passwordBaru);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(Integer id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Menghitung jumlah pengguna berdasarkan provider (google/local).
     *
     * @param provider Provider yang dihitung ("google" atau "local")
     * @return Jumlah pengguna
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanProvider(String provider) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE provider = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, provider);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Helper method untuk mengkonversi ResultSet row ke Map.
     */
    private Map<String, Object> mapRowKeMap(ResultSet rs) throws SQLException {
        Map<String, Object> pengguna = new HashMap<>();
        pengguna.put("id", rs.getInt("id"));
        pengguna.put("username", rs.getString("username"));
        pengguna.put("password", rs.getString("password"));
        pengguna.put("email", rs.getString("email"));
        pengguna.put("google_id", rs.getString("google_id"));
        pengguna.put("nama", rs.getString("nama"));
        pengguna.put("foto_profil", rs.getString("foto_profil"));
        pengguna.put("provider", rs.getString("provider"));
        pengguna.put("dibuat_pada", rs.getTimestamp("dibuat_pada"));
        return pengguna;
    }
}
