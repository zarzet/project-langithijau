package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.Topik;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data topik (topics).
 * Menyediakan operasi CRUD dan query khusus untuk entitas Topik.
 */
public class DAOTopik implements DAOBase<Topik, Integer> {

    private final ManajerBasisData manajerDB;

    public DAOTopik(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    @Override
    public Integer simpan(Topik topik) throws SQLException {
        String sql = "INSERT INTO topik (id_mata_kuliah, nama, deskripsi, prioritas, tingkat_kesulitan) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, topik.getIdMataKuliah());
            pstmt.setString(2, topik.getNama());
            pstmt.setString(3, topik.getDeskripsi());
            pstmt.setInt(4, topik.getPrioritas());
            pstmt.setInt(5, topik.getTingkatKesulitan());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    topik.setId(id);
                    return id;
                }
            }

            throw new SQLException("Gagal membuat topik, tidak ada ID yang dikembalikan");
        }
    }

    @Override
    public Topik ambilBerdasarkanId(Integer id) throws SQLException {
        String sql = "SELECT * FROM topik WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeTopik(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil semua topik berdasarkan mata kuliah ID.
     *
     * @param mataKuliahId ID mata kuliah
     * @return List topik dari mata kuliah tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = "SELECT * FROM topik WHERE id_mata_kuliah = ? ORDER BY nama";
        List<Topik> daftarTopik = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mataKuliahId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarTopik.add(mapRowKeTopik(rs));
            }
        }

        return daftarTopik;
    }

    @Override
    public List<Topik> ambilSemua() throws SQLException {
        String sql = "SELECT * FROM topik ORDER BY id_mata_kuliah, nama";
        List<Topik> daftarTopik = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarTopik.add(mapRowKeTopik(rs));
            }
        }

        return daftarTopik;
    }

    /**
     * Mengambil topik berdasarkan tingkat kesulitan.
     *
     * @param tingkatKesulitan Tingkat kesulitan (1-5, dimana 1=sangat mudah, 5=sangat sulit)
     * @param mataKuliahId ID mata kuliah (opsional, -1 untuk semua)
     * @return List topik dengan tingkat kesulitan tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilBerdasarkanTingkatKesulitan(int tingkatKesulitan, int mataKuliahId) throws SQLException {
        String sql;
        if (mataKuliahId > 0) {
            sql = "SELECT * FROM topik WHERE tingkat_kesulitan = ? AND id_mata_kuliah = ? ORDER BY nama";
        } else {
            sql = "SELECT * FROM topik WHERE tingkat_kesulitan = ? ORDER BY nama";
        }

        List<Topik> daftarTopik = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tingkatKesulitan);
            if (mataKuliahId > 0) {
                pstmt.setInt(2, mataKuliahId);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarTopik.add(mapRowKeTopik(rs));
            }
        }

        return daftarTopik;
    }

    @Override
    public boolean perbarui(Topik topik) throws SQLException {
        String sql = "UPDATE topik SET nama = ?, deskripsi = ?, prioritas = ?, tingkat_kesulitan = ?, " +
                     "tanggal_belajar_pertama = ?, tanggal_ulasan_terakhir = ?, jumlah_ulasan = ?, " +
                     "faktor_kemudahan = ?, interval = ?, dikuasai = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, topik.getNama());
            pstmt.setString(2, topik.getDeskripsi());
            pstmt.setInt(3, topik.getPrioritas());
            pstmt.setInt(4, topik.getTingkatKesulitan());
            pstmt.setDate(5, topik.getTanggalBelajarPertama() != null ? Date.valueOf(topik.getTanggalBelajarPertama()) : null);
            pstmt.setDate(6, topik.getTanggalUlasanTerakhir() != null ? Date.valueOf(topik.getTanggalUlasanTerakhir()) : null);
            pstmt.setInt(7, topik.getJumlahUlasan());
            pstmt.setDouble(8, topik.getFaktorKemudahan());
            pstmt.setInt(9, topik.getInterval());
            pstmt.setBoolean(10, topik.isDikuasai());
            pstmt.setInt(11, topik.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Memperbarui data spaced repetition untuk topik setelah sesi belajar.
     *
     * @param topikId ID topik
     * @param intervalBaru Interval baru dalam hari
     * @param tanggalUlasanTerakhir Tanggal ulasan terakhir
     * @param faktorKemudahanBaru Faktor kemudahan baru
     * @param jumlahUlasanBaru Jumlah ulasan baru
     * @return true jika berhasil diperbarui
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbaruiDataSpacedRepetition(int topikId, int intervalBaru, LocalDate tanggalUlasanTerakhir,
                                                 double faktorKemudahanBaru, int jumlahUlasanBaru) throws SQLException {
        String sql = "UPDATE topik SET interval = ?, tanggal_ulasan_terakhir = ?, " +
                     "faktor_kemudahan = ?, jumlah_ulasan = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, intervalBaru);
            pstmt.setDate(2, Date.valueOf(tanggalUlasanTerakhir));
            pstmt.setDouble(3, faktorKemudahanBaru);
            pstmt.setInt(4, jumlahUlasanBaru);
            pstmt.setInt(5, topikId);

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean hapus(Integer id) throws SQLException {
        String sql = "DELETE FROM topik WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM topik";

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
     * Menghitung jumlah topik berdasarkan mata kuliah.
     *
     * @param mataKuliahId ID mata kuliah
     * @return Jumlah topik
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanMataKuliahId(int mataKuliahId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topik WHERE id_mata_kuliah = ?";

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

    /**
     * Helper method untuk mengkonversi ResultSet row ke Topik.
     */
    private Topik mapRowKeTopik(ResultSet rs) throws SQLException {
        Topik topik = new Topik();
        topik.setId(rs.getInt("id"));
        topik.setIdMataKuliah(rs.getInt("id_mata_kuliah"));
        topik.setNama(rs.getString("nama"));
        topik.setDeskripsi(rs.getString("deskripsi"));
        topik.setPrioritas(rs.getInt("prioritas"));
        topik.setTingkatKesulitan(rs.getInt("tingkat_kesulitan"));

        Date tanggalBelajarPertama = rs.getDate("tanggal_belajar_pertama");
        if (tanggalBelajarPertama != null) {
            topik.setTanggalBelajarPertama(tanggalBelajarPertama.toLocalDate());
        }

        Date tanggalUlasanTerakhir = rs.getDate("tanggal_ulasan_terakhir");
        if (tanggalUlasanTerakhir != null) {
            topik.setTanggalUlasanTerakhir(tanggalUlasanTerakhir.toLocalDate());
        }

        topik.setJumlahUlasan(rs.getInt("jumlah_ulasan"));
        topik.setFaktorKemudahan(rs.getDouble("faktor_kemudahan"));
        topik.setInterval(rs.getInt("interval"));
        topik.setDikuasai(rs.getBoolean("dikuasai"));

        return topik;
    }
}
