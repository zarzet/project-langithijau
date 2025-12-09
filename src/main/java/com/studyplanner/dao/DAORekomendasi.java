package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.Rekomendasi;
import com.studyplanner.model.Rekomendasi.StatusRekomendasi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data rekomendasi topik dari dosen ke mahasiswa.
 * Mendukung UC-22: Berikan Rekomendasi Topik.
 */
public class DAORekomendasi {

    private final ManajerBasisData manajerDB;

    public DAORekomendasi(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    /**
     * Menyimpan rekomendasi baru.
     *
     * @param rekomendasi Data rekomendasi
     * @return ID rekomendasi yang baru dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int simpan(Rekomendasi rekomendasi) throws SQLException {
        String sql = """
            INSERT INTO rekomendasi (dosen_id, mahasiswa_id, id_mata_kuliah, nama_topik,
                deskripsi, prioritas_saran, kesulitan_saran, url_sumber, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rekomendasi.getDosenId());
            pstmt.setInt(2, rekomendasi.getMahasiswaId());
            
            if (rekomendasi.getIdMataKuliah() != null) {
                pstmt.setInt(3, rekomendasi.getIdMataKuliah());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.setString(4, rekomendasi.getNamaTopik());
            pstmt.setString(5, rekomendasi.getDeskripsi());
            pstmt.setInt(6, rekomendasi.getPrioritasSaran());
            pstmt.setInt(7, rekomendasi.getKesulitanSaran());
            pstmt.setString(8, rekomendasi.getUrlSumber());
            pstmt.setString(9, rekomendasi.getStatus().getKode());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException("Gagal membuat rekomendasi, tidak ada ID yang dikembalikan");
        }
    }

    /**
     * Mengambil rekomendasi berdasarkan ID.
     *
     * @param id ID rekomendasi
     * @return Rekomendasi atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Rekomendasi ambilBerdasarkanId(int id) throws SQLException {
        String sql = """
            SELECT r.*,
                   ud.nama as nama_dosen,
                   um.nama as nama_mahasiswa,
                   mk.nama as nama_mata_kuliah
            FROM rekomendasi r
            JOIN dosen d ON r.dosen_id = d.id
            JOIN users ud ON d.user_id = ud.id
            JOIN mahasiswa m ON r.mahasiswa_id = m.id
            JOIN users um ON m.user_id = um.id
            LEFT JOIN mata_kuliah mk ON r.id_mata_kuliah = mk.id
            WHERE r.id = ?
            """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeRekomendasi(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil semua rekomendasi dari dosen tertentu.
     *
     * @param dosenId ID dosen
     * @return List rekomendasi
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Rekomendasi> ambilBerdasarkanDosenId(int dosenId) throws SQLException {
        String sql = """
            SELECT r.*,
                   ud.nama as nama_dosen,
                   um.nama as nama_mahasiswa,
                   mk.nama as nama_mata_kuliah
            FROM rekomendasi r
            JOIN dosen d ON r.dosen_id = d.id
            JOIN users ud ON d.user_id = ud.id
            JOIN mahasiswa m ON r.mahasiswa_id = m.id
            JOIN users um ON m.user_id = um.id
            LEFT JOIN mata_kuliah mk ON r.id_mata_kuliah = mk.id
            WHERE r.dosen_id = ?
            ORDER BY r.dibuat_pada DESC
            """;

        List<Rekomendasi> daftarRekomendasi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarRekomendasi.add(mapRowKeRekomendasi(rs));
            }
        }

        return daftarRekomendasi;
    }

    /**
     * Mengambil semua rekomendasi untuk mahasiswa tertentu.
     *
     * @param mahasiswaId ID mahasiswa
     * @return List rekomendasi
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Rekomendasi> ambilBerdasarkanMahasiswaId(int mahasiswaId) throws SQLException {
        String sql = """
            SELECT r.*,
                   ud.nama as nama_dosen,
                   um.nama as nama_mahasiswa,
                   mk.nama as nama_mata_kuliah
            FROM rekomendasi r
            JOIN dosen d ON r.dosen_id = d.id
            JOIN users ud ON d.user_id = ud.id
            JOIN mahasiswa m ON r.mahasiswa_id = m.id
            JOIN users um ON m.user_id = um.id
            LEFT JOIN mata_kuliah mk ON r.id_mata_kuliah = mk.id
            WHERE r.mahasiswa_id = ?
            ORDER BY r.dibuat_pada DESC
            """;

        List<Rekomendasi> daftarRekomendasi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mahasiswaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarRekomendasi.add(mapRowKeRekomendasi(rs));
            }
        }

        return daftarRekomendasi;
    }

    /**
     * Mengambil rekomendasi pending untuk mahasiswa tertentu.
     *
     * @param mahasiswaId ID mahasiswa
     * @return List rekomendasi pending
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Rekomendasi> ambilPendingBerdasarkanMahasiswaId(int mahasiswaId) throws SQLException {
        String sql = """
            SELECT r.*,
                   ud.nama as nama_dosen,
                   um.nama as nama_mahasiswa,
                   mk.nama as nama_mata_kuliah
            FROM rekomendasi r
            JOIN dosen d ON r.dosen_id = d.id
            JOIN users ud ON d.user_id = ud.id
            JOIN mahasiswa m ON r.mahasiswa_id = m.id
            JOIN users um ON m.user_id = um.id
            LEFT JOIN mata_kuliah mk ON r.id_mata_kuliah = mk.id
            WHERE r.mahasiswa_id = ? AND r.status = 'pending'
            ORDER BY r.dibuat_pada DESC
            """;

        List<Rekomendasi> daftarRekomendasi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mahasiswaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarRekomendasi.add(mapRowKeRekomendasi(rs));
            }
        }

        return daftarRekomendasi;
    }

    /**
     * Mengambil rekomendasi berdasarkan dosen dan status.
     *
     * @param dosenId ID dosen
     * @param status Status rekomendasi
     * @return List rekomendasi
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Rekomendasi> ambilBerdasarkanDosenDanStatus(int dosenId, StatusRekomendasi status) throws SQLException {
        String sql = """
            SELECT r.*,
                   ud.nama as nama_dosen,
                   um.nama as nama_mahasiswa,
                   mk.nama as nama_mata_kuliah
            FROM rekomendasi r
            JOIN dosen d ON r.dosen_id = d.id
            JOIN users ud ON d.user_id = ud.id
            JOIN mahasiswa m ON r.mahasiswa_id = m.id
            JOIN users um ON m.user_id = um.id
            LEFT JOIN mata_kuliah mk ON r.id_mata_kuliah = mk.id
            WHERE r.dosen_id = ? AND r.status = ?
            ORDER BY r.dibuat_pada DESC
            """;

        List<Rekomendasi> daftarRekomendasi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            pstmt.setString(2, status.getKode());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarRekomendasi.add(mapRowKeRekomendasi(rs));
            }
        }

        return daftarRekomendasi;
    }

    /**
     * Memperbarui status rekomendasi.
     *
     * @param id ID rekomendasi
     * @param status Status baru
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbaruiStatus(int id, StatusRekomendasi status) throws SQLException {
        String sql = "UPDATE rekomendasi SET status = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.getKode());
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Memperbarui data rekomendasi.
     *
     * @param rekomendasi Data rekomendasi yang diperbarui
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(Rekomendasi rekomendasi) throws SQLException {
        String sql = """
            UPDATE rekomendasi SET
                id_mata_kuliah = ?, nama_topik = ?, deskripsi = ?,
                prioritas_saran = ?, kesulitan_saran = ?, url_sumber = ?, status = ?
            WHERE id = ?
            """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (rekomendasi.getIdMataKuliah() != null) {
                pstmt.setInt(1, rekomendasi.getIdMataKuliah());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            
            pstmt.setString(2, rekomendasi.getNamaTopik());
            pstmt.setString(3, rekomendasi.getDeskripsi());
            pstmt.setInt(4, rekomendasi.getPrioritasSaran());
            pstmt.setInt(5, rekomendasi.getKesulitanSaran());
            pstmt.setString(6, rekomendasi.getUrlSumber());
            pstmt.setString(7, rekomendasi.getStatus().getKode());
            pstmt.setInt(8, rekomendasi.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus rekomendasi.
     *
     * @param id ID rekomendasi
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int id) throws SQLException {
        String sql = "DELETE FROM rekomendasi WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghitung total rekomendasi per dosen.
     *
     * @param dosenId ID dosen
     * @return Jumlah rekomendasi
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanDosen(int dosenId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rekomendasi WHERE dosen_id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Menghitung rekomendasi pending per mahasiswa.
     *
     * @param mahasiswaId ID mahasiswa
     * @return Jumlah rekomendasi pending
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungPendingBerdasarkanMahasiswa(int mahasiswaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rekomendasi WHERE mahasiswa_id = ? AND status = 'pending'";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mahasiswaId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Menghitung statistik rekomendasi per dosen.
     *
     * @param dosenId ID dosen
     * @return Array [total, pending, accepted, declined]
     * @throws SQLException jika terjadi kesalahan database
     */
    public int[] hitungStatistikBerdasarkanDosen(int dosenId) throws SQLException {
        String sql = """
            SELECT
                COUNT(*) as total,
                SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending,
                SUM(CASE WHEN status = 'accepted' THEN 1 ELSE 0 END) as accepted,
                SUM(CASE WHEN status = 'declined' THEN 1 ELSE 0 END) as declined
            FROM rekomendasi WHERE dosen_id = ?
            """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new int[]{
                    rs.getInt("total"),
                    rs.getInt("pending"),
                    rs.getInt("accepted"),
                    rs.getInt("declined")
                };
            }

            return new int[]{0, 0, 0, 0};
        }
    }

    /**
     * Helper untuk map ResultSet ke Rekomendasi.
     */
    private Rekomendasi mapRowKeRekomendasi(ResultSet rs) throws SQLException {
        Rekomendasi rekomendasi = new Rekomendasi();
        rekomendasi.setId(rs.getInt("id"));
        rekomendasi.setDosenId(rs.getInt("dosen_id"));
        rekomendasi.setMahasiswaId(rs.getInt("mahasiswa_id"));
        
        int idMataKuliah = rs.getInt("id_mata_kuliah");
        if (!rs.wasNull()) {
            rekomendasi.setIdMataKuliah(idMataKuliah);
        }
        
        rekomendasi.setNamaTopik(rs.getString("nama_topik"));
        rekomendasi.setDeskripsi(rs.getString("deskripsi"));
        rekomendasi.setPrioritasSaran(rs.getInt("prioritas_saran"));
        rekomendasi.setKesulitanSaran(rs.getInt("kesulitan_saran"));
        rekomendasi.setUrlSumber(rs.getString("url_sumber"));
        rekomendasi.setStatus(StatusRekomendasi.dariKode(rs.getString("status")));
        
        Timestamp dibuatPada = rs.getTimestamp("dibuat_pada");
        if (dibuatPada != null) {
            rekomendasi.setDibuatPada(dibuatPada.toLocalDateTime());
        }

        // Data dari join
        rekomendasi.setNamaDosen(rs.getString("nama_dosen"));
        rekomendasi.setNamaMahasiswa(rs.getString("nama_mahasiswa"));
        rekomendasi.setNamaMataKuliah(rs.getString("nama_mata_kuliah"));

        return rekomendasi;
    }
}
