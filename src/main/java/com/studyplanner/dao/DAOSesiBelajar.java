package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.SesiBelajar;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data sesi belajar (study sessions).
 * Menyediakan operasi CRUD dan query khusus untuk entitas SesiBelajar.
 */
public class DAOSesiBelajar implements DAOBase<SesiBelajar, Integer> {

    private final ManajerBasisData manajerDB;

    /** Query SELECT dasar dengan JOIN untuk mengambil nama topik dan mata kuliah.
     *  Menggunakan INNER JOIN untuk otomatis filter sesi yang topik/matakuliahnya sudah dihapus. */
    private static final String SELECT_SESI_DENGAN_JOIN = """
            SELECT s.*, t.nama AS nama_topik, mk.nama AS nama_mata_kuliah
            FROM sesi_belajar s
            INNER JOIN topik t ON s.id_topik = t.id
            INNER JOIN mata_kuliah mk ON s.id_mata_kuliah = mk.id
            """;

    public DAOSesiBelajar(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    @Override
    public Integer simpan(SesiBelajar sesi) throws SQLException {
        String sql = "INSERT INTO sesi_belajar (id_topik, id_mata_kuliah, tanggal_jadwal, tipe_sesi, " +
                     "selesai, selesai_pada, rating_performa, catatan, durasi_menit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, sesi.getIdTopik());
            pstmt.setInt(2, sesi.getIdMataKuliah());
            pstmt.setDate(3, Date.valueOf(sesi.getTanggalJadwal()));
            pstmt.setString(4, sesi.getTipeSesi());
            pstmt.setBoolean(5, sesi.isSelesai());
            pstmt.setTimestamp(6, sesi.getSelesaiPada() != null ? Timestamp.valueOf(sesi.getSelesaiPada()) : null);
            pstmt.setInt(7, sesi.getRatingPerforma());
            pstmt.setString(8, sesi.getCatatan());
            pstmt.setInt(9, sesi.getDurasiMenit());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    sesi.setId(id);
                    return id;
                }
            }

            throw new SQLException("Gagal membuat sesi belajar, tidak ada ID yang dikembalikan");
        }
    }

    @Override
    public SesiBelajar ambilBerdasarkanId(Integer id) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + "WHERE s.id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeSesiBelajar(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil sesi belajar berdasarkan tanggal.
     *
     * @param tanggal Tanggal jadwal
     * @return List sesi belajar pada tanggal tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanTanggal(LocalDate tanggal) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + "WHERE s.tanggal_jadwal = ? ORDER BY s.tipe_sesi, mk.kode";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(tanggal));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar hari ini.
     *
     * @return List sesi belajar hari ini
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiHariIni() throws SQLException {
        return ambilBerdasarkanTanggal(LocalDate.now());
    }

    /**
     * Mengambil sesi belajar hari ini untuk user tertentu.
     *
     * @param userId ID user
     * @return List sesi belajar hari ini milik user
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiHariIniByUserId(int userId) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + 
                "WHERE s.tanggal_jadwal = ? AND mk.user_id = ? ORDER BY s.tipe_sesi, mk.kode";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar mendatang yang belum selesai.
     *
     * @return List sesi belajar mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiMendatang() throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN +
                "WHERE s.tanggal_jadwal > DATE('now') AND s.selesai = 0 " +
                "ORDER BY s.tanggal_jadwal ASC, s.tipe_sesi";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar mendatang dengan batas tertentu.
     *
     * @param batas Jumlah maksimal sesi yang akan diambil
     * @return List sesi belajar mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiMendatang(int batas) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN +
                "WHERE s.tanggal_jadwal > DATE('now') AND s.selesai = 0 " +
                "ORDER BY s.tanggal_jadwal ASC, s.tipe_sesi LIMIT ?";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, batas);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar dalam rentang tanggal tertentu.
     *
     * @param tanggalMulai Tanggal mulai
     * @param tanggalAkhir Tanggal akhir
     * @return List sesi belajar dalam rentang tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanRentangTanggal(LocalDate tanggalMulai, LocalDate tanggalAkhir) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN +
                "WHERE s.tanggal_jadwal BETWEEN ? AND ? ORDER BY s.tanggal_jadwal, s.tipe_sesi";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(tanggalMulai));
            pstmt.setDate(2, Date.valueOf(tanggalAkhir));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar berdasarkan topik ID.
     *
     * @param topikId ID topik
     * @return List sesi belajar untuk topik tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanTopikId(int topikId) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + "WHERE s.id_topik = ? ORDER BY s.tanggal_jadwal DESC";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, topikId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar berdasarkan mata kuliah ID.
     *
     * @param mataKuliahId ID mata kuliah
     * @return List sesi belajar untuk mata kuliah tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + "WHERE s.id_mata_kuliah = ? ORDER BY s.tanggal_jadwal DESC";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mataKuliahId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    /**
     * Mengambil sesi belajar berdasarkan status selesai.
     *
     * @param selesai Status selesai (true/false)
     * @return List sesi belajar
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanStatus(boolean selesai) throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + "WHERE s.selesai = ? ORDER BY s.tanggal_jadwal DESC";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, selesai);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    @Override
    public List<SesiBelajar> ambilSemua() throws SQLException {
        String sql = SELECT_SESI_DENGAN_JOIN + "ORDER BY s.tanggal_jadwal DESC";
        List<SesiBelajar> daftarSesi = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarSesi.add(mapRowKeSesiBelajar(rs));
            }
        }

        return daftarSesi;
    }

    @Override
    public boolean perbarui(SesiBelajar sesi) throws SQLException {
        String sql = "UPDATE sesi_belajar SET id_topik = ?, id_mata_kuliah = ?, tanggal_jadwal = ?, " +
                     "tipe_sesi = ?, selesai = ?, selesai_pada = ?, rating_performa = ?, " +
                     "catatan = ?, durasi_menit = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sesi.getIdTopik());
            pstmt.setInt(2, sesi.getIdMataKuliah());
            pstmt.setDate(3, Date.valueOf(sesi.getTanggalJadwal()));
            pstmt.setString(4, sesi.getTipeSesi());
            pstmt.setBoolean(5, sesi.isSelesai());
            pstmt.setTimestamp(6, sesi.getSelesaiPada() != null ? Timestamp.valueOf(sesi.getSelesaiPada()) : null);
            pstmt.setInt(7, sesi.getRatingPerforma());
            pstmt.setString(8, sesi.getCatatan());
            pstmt.setInt(9, sesi.getDurasiMenit());
            pstmt.setInt(10, sesi.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menandai sesi sebagai selesai dengan rating dan catatan.
     *
     * @param sesiId ID sesi
     * @param ratingPerforma Rating performa (1-5)
     * @param catatan Catatan sesi
     * @return true jika berhasil diperbarui
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean tandaiSelesai(int sesiId, int ratingPerforma, String catatan) throws SQLException {
        String sql = "UPDATE sesi_belajar SET selesai = 1, selesai_pada = ?, " +
                     "rating_performa = ?, catatan = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, ratingPerforma);
            pstmt.setString(3, catatan);
            pstmt.setInt(4, sesiId);

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(Integer id) throws SQLException {
        String sql = "DELETE FROM sesi_belajar WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus semua sesi belajar berdasarkan ID topik.
     * Digunakan saat topik dihapus untuk cascade delete sesi terkait.
     *
     * @param topikId ID topik
     * @return jumlah sesi yang dihapus
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hapusBerdasarkanTopikId(int topikId) throws SQLException {
        String sql = "DELETE FROM sesi_belajar WHERE id_topik = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, topikId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * Menghapus semua sesi belajar berdasarkan ID mata kuliah.
     * Digunakan saat mata kuliah dihapus untuk cascade delete sesi terkait.
     *
     * @param mataKuliahId ID mata kuliah
     * @return jumlah sesi yang dihapus
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hapusBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = "DELETE FROM sesi_belajar WHERE id_mata_kuliah = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mataKuliahId);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sesi_belajar";

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
     * Menghitung sesi yang selesai hari ini.
     *
     * @return Jumlah sesi selesai hari ini
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungSesiSelesaiHariIni() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sesi_belajar " +
                     "WHERE tanggal_jadwal = DATE('now') AND selesai = 1";

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
     * Menghitung sesi berdasarkan topik ID.
     *
     * @param topikId ID topik
     * @return Jumlah sesi
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanTopikId(int topikId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sesi_belajar WHERE id_topik = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, topikId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Menghitung sesi berdasarkan mata kuliah ID.
     *
     * @param mataKuliahId ID mata kuliah
     * @return Jumlah sesi
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sesi_belajar WHERE id_mata_kuliah = ?";

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

    private SesiBelajar mapRowKeSesiBelajar(ResultSet rs) throws SQLException {
        SesiBelajar sesi = new SesiBelajar();
        sesi.setId(rs.getInt("id"));
        sesi.setIdTopik(rs.getInt("id_topik"));
        sesi.setIdMataKuliah(rs.getInt("id_mata_kuliah"));

        Date tanggalJadwal = rs.getDate("tanggal_jadwal");
        if (tanggalJadwal != null) {
            sesi.setTanggalJadwal(tanggalJadwal.toLocalDate());
        }

        sesi.setTipeSesi(rs.getString("tipe_sesi"));
        sesi.setSelesai(rs.getBoolean("selesai"));

        Timestamp selesaiPada = rs.getTimestamp("selesai_pada");
        if (selesaiPada != null) {
            sesi.setSelesaiPada(selesaiPada.toLocalDateTime());
        }

        sesi.setRatingPerforma(rs.getInt("rating_performa"));
        sesi.setCatatan(rs.getString("catatan"));
        sesi.setDurasiMenit(rs.getInt("durasi_menit"));

        try {
            String namaTopik = rs.getString("nama_topik");
            if (namaTopik != null) {
                sesi.setNamaTopik(namaTopik);
            }
        } catch (SQLException ignored) {
        }

        try {
            String namaMataKuliah = rs.getString("nama_mata_kuliah");
            if (namaMataKuliah != null) {
                sesi.setNamaMataKuliah(namaMataKuliah);
            }
        } catch (SQLException ignored) {
        }

        return sesi;
    }
}
