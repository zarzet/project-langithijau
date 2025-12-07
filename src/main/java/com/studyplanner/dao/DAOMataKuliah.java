package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.MataKuliah;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data mata kuliah (courses).
 * Menyediakan operasi CRUD dan query khusus untuk entitas MataKuliah.
 */
public class DAOMataKuliah implements DAOBase<MataKuliah, Integer> {

    private final ManajerBasisData manajerDB;

    public DAOMataKuliah(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    @Override
    public Integer simpan(MataKuliah mataKuliah) throws SQLException {
        String sql = "INSERT INTO mata_kuliah (user_id, nama, kode, deskripsi) VALUES (?, ?, ?, ?)";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, mataKuliah.getUserId());
            pstmt.setString(2, mataKuliah.getNama());
            pstmt.setString(3, mataKuliah.getKode());
            pstmt.setString(4, mataKuliah.getDeskripsi());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    mataKuliah.setId(id);
                    return id;
                }
            }

            throw new SQLException("Gagal membuat mata kuliah, tidak ada ID yang dikembalikan");
        }
    }

    @Override
    public MataKuliah ambilBerdasarkanId(Integer id) throws SQLException {
        String sql = "SELECT * FROM mata_kuliah WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMataKuliah(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil mata kuliah berdasarkan kode untuk user tertentu.
     *
     * @param userId ID user
     * @param kode Kode mata kuliah
     * @return MataKuliah atau null jika tidak ditemukan
     * @throws SQLException jika terjadi kesalahan database
     */
    public MataKuliah ambilBerdasarkanKode(int userId, String kode) throws SQLException {
        String sql = "SELECT * FROM mata_kuliah WHERE user_id = ? AND kode = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, kode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMataKuliah(rs);
            }

            return null;
        }
    }

    @Override
    public List<MataKuliah> ambilSemua() throws SQLException {
        String sql = "SELECT * FROM mata_kuliah ORDER BY nama";
        List<MataKuliah> daftarMataKuliah = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarMataKuliah.add(mapRowKeMataKuliah(rs));
            }
        }

        return daftarMataKuliah;
    }

    /**
     * Mengambil semua mata kuliah untuk user tertentu.
     *
     * @param userId ID user
     * @return List mata kuliah milik user tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<MataKuliah> ambilSemuaByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM mata_kuliah WHERE user_id = ? ORDER BY nama";
        List<MataKuliah> daftarMataKuliah = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarMataKuliah.add(mapRowKeMataKuliah(rs));
            }
        }

        return daftarMataKuliah;
    }

    @Override
    public boolean perbarui(MataKuliah mataKuliah) throws SQLException {
        String sql = "UPDATE mata_kuliah SET nama = ?, kode = ?, deskripsi = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mataKuliah.getNama());
            pstmt.setString(2, mataKuliah.getKode());
            pstmt.setString(3, mataKuliah.getDeskripsi());
            pstmt.setInt(4, mataKuliah.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(Integer id) throws SQLException {
        String sql = "DELETE FROM mata_kuliah WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mata_kuliah";

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
     * Mencari mata kuliah berdasarkan nama untuk user tertentu (partial match).
     *
     * @param userId ID user
     * @param namaKataKunci Kata kunci pencarian
     * @return List mata kuliah yang cocok
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<MataKuliah> cariBerdasarkanNama(int userId, String namaKataKunci) throws SQLException {
        String sql = "SELECT * FROM mata_kuliah WHERE user_id = ? AND nama LIKE ? ORDER BY nama";
        List<MataKuliah> daftarMataKuliah = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + namaKataKunci + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarMataKuliah.add(mapRowKeMataKuliah(rs));
            }
        }

        return daftarMataKuliah;
    }

    /**
     * Mencari mata kuliah berdasarkan nama untuk user tertentu.
     *
     * @param nama Nama mata kuliah
     * @param userId ID user
     * @return MataKuliah atau null jika tidak ditemukan
     * @throws SQLException jika terjadi kesalahan database
     */
    public MataKuliah cariBerdasarkanNamaDanUser(String nama, int userId) throws SQLException {
        String sql = "SELECT * FROM mata_kuliah WHERE user_id = ? AND nama = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, nama);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMataKuliah(rs);
            }

            return null;
        }
    }

    /**
     * Menghitung total mata kuliah untuk user tertentu.
     *
     * @param userId ID user
     * @return Jumlah mata kuliah milik user
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungTotalByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM mata_kuliah WHERE user_id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    private MataKuliah mapRowKeMataKuliah(ResultSet rs) throws SQLException {
        MataKuliah mk = new MataKuliah();
        mk.setId(rs.getInt("id"));
        try {
            mk.setUserId(rs.getInt("user_id"));
        } catch (SQLException e) {
            mk.setUserId(1);
        }
        mk.setNama(rs.getString("nama"));
        mk.setKode(rs.getString("kode"));
        mk.setDeskripsi(rs.getString("deskripsi"));
        return mk;
    }
}
