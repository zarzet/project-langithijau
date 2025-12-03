package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.JadwalUjian;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data jadwal ujian (exam schedules).
 * Menyediakan operasi CRUD dan query khusus untuk entitas JadwalUjian.
 */
public class DAOJadwalUjian implements DAOBase<JadwalUjian, Integer> {

    private final ManajerBasisData manajerDB;

    public DAOJadwalUjian(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    @Override
    public Integer simpan(JadwalUjian ujian) throws SQLException {
        String sql = "INSERT INTO jadwal_ujian (id_mata_kuliah, tipe_ujian, judul, tanggal_ujian, waktu_ujian, lokasi, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, ujian.getIdMataKuliah());
            pstmt.setString(2, ujian.getTipeUjian());
            pstmt.setString(3, ujian.getJudul());
            pstmt.setDate(4, Date.valueOf(ujian.getTanggalUjian()));
            pstmt.setTime(5, ujian.getWaktuUjian() != null ? Time.valueOf(ujian.getWaktuUjian()) : null);
            pstmt.setString(6, ujian.getLokasi());
            pstmt.setString(7, ujian.getCatatan());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    ujian.setId(id);
                    return id;
                }
            }

            throw new SQLException("Gagal membuat jadwal ujian, tidak ada ID yang dikembalikan");
        }
    }

    @Override
    public JadwalUjian ambilBerdasarkanId(Integer id) throws SQLException {
        String sql = "SELECT * FROM jadwal_ujian WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeJadwalUjian(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil ujian berdasarkan mata kuliah ID.
     *
     * @param mataKuliahId ID mata kuliah
     * @return List jadwal ujian dari mata kuliah tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = "SELECT * FROM jadwal_ujian WHERE id_mata_kuliah = ? ORDER BY tanggal_ujian";
        List<JadwalUjian> daftarUjian = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mataKuliahId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarUjian.add(mapRowKeJadwalUjian(rs));
            }
        }

        return daftarUjian;
    }

    /**
     * Mengambil ujian mendatang (yang belum lewat dari hari ini) untuk user tertentu.
     *
     * @param userId ID pengguna
     * @return List jadwal ujian mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilUjianMendatang(int userId) throws SQLException {
        String sql = """
            SELECT ju.* FROM jadwal_ujian ju
            JOIN mata_kuliah mk ON ju.id_mata_kuliah = mk.id
            WHERE mk.user_id = ?
            ORDER BY ju.tanggal_ujian
            """;
        List<JadwalUjian> daftarUjian = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarUjian.add(mapRowKeJadwalUjian(rs));
            }
        }

        return daftarUjian;
    }

    /**
     * Mengambil ujian mendatang (deprecated - gunakan versi dengan userId).
     */
    @Deprecated
    public List<JadwalUjian> ambilUjianMendatang() throws SQLException {
        return ambilUjianMendatang(-1); // Fallback
    }

    /**
     * Mengambil ujian dalam rentang tanggal tertentu.
     *
     * @param tanggalMulai Tanggal mulai
     * @param tanggalAkhir Tanggal akhir
     * @return List jadwal ujian dalam rentang tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilBerdasarkanRentangTanggal(LocalDate tanggalMulai, LocalDate tanggalAkhir) throws SQLException {
        String sql = "SELECT * FROM jadwal_ujian WHERE tanggal_ujian BETWEEN ? AND ? ORDER BY tanggal_ujian";
        List<JadwalUjian> daftarUjian = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(tanggalMulai));
            pstmt.setDate(2, Date.valueOf(tanggalAkhir));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarUjian.add(mapRowKeJadwalUjian(rs));
            }
        }

        return daftarUjian;
    }

    /**
     * Mengambil ujian berdasarkan status selesai.
     *
     * @param selesai Status selesai (true/false)
     * @return List jadwal ujian
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilBerdasarkanStatus(boolean selesai) throws SQLException {
        String sql = "SELECT * FROM jadwal_ujian WHERE selesai = ? ORDER BY tanggal_ujian";
        List<JadwalUjian> daftarUjian = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, selesai);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarUjian.add(mapRowKeJadwalUjian(rs));
            }
        }

        return daftarUjian;
    }

    @Override
    public List<JadwalUjian> ambilSemua() throws SQLException {
        String sql = "SELECT * FROM jadwal_ujian ORDER BY tanggal_ujian DESC";
        List<JadwalUjian> daftarUjian = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarUjian.add(mapRowKeJadwalUjian(rs));
            }
        }

        return daftarUjian;
    }

    @Override
    public boolean perbarui(JadwalUjian ujian) throws SQLException {
        String sql = "UPDATE jadwal_ujian SET id_mata_kuliah = ?, tipe_ujian = ?, judul = ?, " +
                     "tanggal_ujian = ?, waktu_ujian = ?, lokasi = ?, catatan = ?, selesai = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ujian.getIdMataKuliah());
            pstmt.setString(2, ujian.getTipeUjian());
            pstmt.setString(3, ujian.getJudul());
            pstmt.setDate(4, Date.valueOf(ujian.getTanggalUjian()));
            pstmt.setTime(5, ujian.getWaktuUjian() != null ? Time.valueOf(ujian.getWaktuUjian()) : null);
            pstmt.setString(6, ujian.getLokasi());
            pstmt.setString(7, ujian.getCatatan());
            pstmt.setBoolean(8, ujian.isSelesai());
            pstmt.setInt(9, ujian.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menandai ujian sebagai selesai.
     *
     * @param ujianId ID ujian
     * @return true jika berhasil diperbarui
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean tandaiSelesai(int ujianId) throws SQLException {
        String sql = "UPDATE jadwal_ujian SET selesai = 1 WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ujianId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(Integer id) throws SQLException {
        String sql = "DELETE FROM jadwal_ujian WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM jadwal_ujian";

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
     * Menghitung ujian berdasarkan mata kuliah.
     *
     * @param mataKuliahId ID mata kuliah
     * @return Jumlah ujian
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jadwal_ujian WHERE id_mata_kuliah = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mataKuliahId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    private JadwalUjian mapRowKeJadwalUjian(ResultSet rs) throws SQLException {
        JadwalUjian ujian = new JadwalUjian();
        ujian.setId(rs.getInt("id"));
        ujian.setIdMataKuliah(rs.getInt("id_mata_kuliah"));
        ujian.setTipeUjian(rs.getString("tipe_ujian"));
        ujian.setJudul(rs.getString("judul"));

        Date tanggalUjian = rs.getDate("tanggal_ujian");
        if (tanggalUjian != null) {
            ujian.setTanggalUjian(tanggalUjian.toLocalDate());
        }

        Time waktuUjian = rs.getTime("waktu_ujian");
        if (waktuUjian != null) {
            ujian.setWaktuUjian(waktuUjian.toLocalTime());
        }

        ujian.setLokasi(rs.getString("lokasi"));
        ujian.setCatatan(rs.getString("catatan"));
        ujian.setSelesai(rs.getBoolean("selesai"));

        return ujian;
    }
}
