package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.Dosen;
import com.studyplanner.model.StatusPengguna;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data dosen.
 */
public class DAODosen {

    private final ManajerBasisData manajerDB;

    public DAODosen(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    /**
     * Menyimpan data dosen baru.
     *
     * @param dosen Data dosen
     * @return ID dosen yang baru dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int simpan(Dosen dosen) throws SQLException {
        String sql = "INSERT INTO dosen (user_id, nip, max_mahasiswa) VALUES (?, ?, ?)";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, dosen.getUserId());
            pstmt.setString(2, dosen.getNip());
            pstmt.setInt(3, dosen.getMaxMahasiswa());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException("Gagal membuat dosen, tidak ada ID yang dikembalikan");
        }
    }

    /**
     * Mengambil dosen berdasarkan ID.
     *
     * @param id ID dosen
     * @return Dosen atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Dosen ambilBerdasarkanId(int id) throws SQLException {
        String sql = """
            SELECT d.*, u.nama, u.email, u.status,
                   (SELECT COUNT(*) FROM mahasiswa m WHERE m.dosen_id = d.id) as jumlah_mahasiswa
            FROM dosen d
            JOIN users u ON d.user_id = u.id
            WHERE d.id = ?
        """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeDosen(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil dosen berdasarkan user ID.
     *
     * @param userId ID user
     * @return Dosen atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Dosen ambilBerdasarkanUserId(int userId) throws SQLException {
        String sql = """
            SELECT d.*, u.nama, u.email, u.status,
                   (SELECT COUNT(*) FROM mahasiswa m WHERE m.dosen_id = d.id) as jumlah_mahasiswa
            FROM dosen d
            JOIN users u ON d.user_id = u.id
            WHERE d.user_id = ?
        """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeDosen(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil semua dosen.
     *
     * @return List semua dosen
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Dosen> ambilSemua() throws SQLException {
        String sql = """
            SELECT d.*, u.nama, u.email, u.status,
                   (SELECT COUNT(*) FROM mahasiswa m WHERE m.dosen_id = d.id) as jumlah_mahasiswa
            FROM dosen d
            JOIN users u ON d.user_id = u.id
            ORDER BY u.nama ASC
        """;

        List<Dosen> daftarDosen = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarDosen.add(mapRowKeDosen(rs));
            }
        }

        return daftarDosen;
    }

    /**
     * Mengambil dosen yang masih bisa menerima mahasiswa baru.
     *
     * @return List dosen dengan kuota tersedia
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Dosen> ambilDosenDenganKuota() throws SQLException {
        String sql = """
            SELECT d.*, u.nama, u.email, u.status,
                   (SELECT COUNT(*) FROM mahasiswa m WHERE m.dosen_id = d.id) as jumlah_mahasiswa
            FROM dosen d
            JOIN users u ON d.user_id = u.id
            WHERE u.status = 'active'
            HAVING jumlah_mahasiswa < d.max_mahasiswa
            ORDER BY jumlah_mahasiswa ASC, u.nama ASC
        """;

        List<Dosen> daftarDosen = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarDosen.add(mapRowKeDosen(rs));
            }
        }

        return daftarDosen;
    }

    /**
     * Memperbarui data dosen.
     *
     * @param dosen Data dosen yang diperbarui
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(Dosen dosen) throws SQLException {
        String sql = "UPDATE dosen SET nip = ?, max_mahasiswa = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dosen.getNip());
            pstmt.setInt(2, dosen.getMaxMahasiswa());
            pstmt.setInt(3, dosen.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus dosen.
     *
     * @param id ID dosen
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int id) throws SQLException {
        String sql = "DELETE FROM dosen WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus dosen berdasarkan user ID.
     *
     * @param userId ID user
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapusBerdasarkanUserId(int userId) throws SQLException {
        String sql = "DELETE FROM dosen WHERE user_id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghitung total dosen.
     *
     * @return Jumlah dosen
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM dosen";

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
     * Cek apakah NIP sudah digunakan.
     *
     * @param nip NIP yang dicek
     * @param excludeId ID dosen yang dikecualikan (untuk edit)
     * @return true jika NIP sudah ada
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean nipSudahAda(String nip, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dosen WHERE nip = ? AND id != ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nip);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        }
    }

    /**
     * Helper untuk map ResultSet ke Dosen.
     */
    private Dosen mapRowKeDosen(ResultSet rs) throws SQLException {
        Dosen dosen = new Dosen();
        dosen.setId(rs.getInt("id"));
        dosen.setUserId(rs.getInt("user_id"));
        dosen.setNip(rs.getString("nip"));
        dosen.setMaxMahasiswa(rs.getInt("max_mahasiswa"));
        
        Timestamp dibuatPada = rs.getTimestamp("dibuat_pada");
        if (dibuatPada != null) {
            dosen.setDibuatPada(dibuatPada.toLocalDateTime());
        }

        // Data dari join dengan users
        dosen.setNama(rs.getString("nama"));
        dosen.setEmail(rs.getString("email"));
        dosen.setStatus(StatusPengguna.dariKode(rs.getString("status")));
        dosen.setJumlahMahasiswa(rs.getInt("jumlah_mahasiswa"));

        return dosen;
    }
}
