package com.studyplanner.dao;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.model.Mahasiswa;
import com.studyplanner.model.StatusPengguna;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk mengelola data mahasiswa.
 */
public class DAOMahasiswa {

    private final ManajerBasisData manajerDB;

    public DAOMahasiswa(ManajerBasisData manajerDB) {
        this.manajerDB = manajerDB;
    }

    /**
     * Menyimpan data mahasiswa baru.
     *
     * @param mahasiswa Data mahasiswa
     * @return ID mahasiswa yang baru dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int simpan(Mahasiswa mahasiswa) throws SQLException {
        String sql = "INSERT INTO mahasiswa (user_id, nim, semester, dosen_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, mahasiswa.getUserId());
            pstmt.setString(2, mahasiswa.getNim());
            pstmt.setInt(3, mahasiswa.getSemester());
            
            if (mahasiswa.getDosenId() != null) {
                pstmt.setInt(4, mahasiswa.getDosenId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException("Gagal membuat mahasiswa, tidak ada ID yang dikembalikan");
        }
    }

    /**
     * Mengambil mahasiswa berdasarkan ID.
     *
     * @param id ID mahasiswa
     * @return Mahasiswa atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Mahasiswa ambilBerdasarkanId(int id) throws SQLException {
        String sql = """
            SELECT m.*, u.nama, u.email, u.status, u.login_terakhir,
                   d_user.nama as nama_dosen
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            LEFT JOIN dosen d ON m.dosen_id = d.id
            LEFT JOIN users d_user ON d.user_id = d_user.id
            WHERE m.id = ?
        """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMahasiswa(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil mahasiswa berdasarkan user ID.
     *
     * @param userId ID user
     * @return Mahasiswa atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Mahasiswa ambilBerdasarkanUserId(int userId) throws SQLException {
        String sql = """
            SELECT m.*, u.nama, u.email, u.status, u.login_terakhir,
                   d_user.nama as nama_dosen
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            LEFT JOIN dosen d ON m.dosen_id = d.id
            LEFT JOIN users d_user ON d.user_id = d_user.id
            WHERE m.user_id = ?
        """;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowKeMahasiswa(rs);
            }

            return null;
        }
    }

    /**
     * Mengambil semua mahasiswa.
     *
     * @return List semua mahasiswa
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Mahasiswa> ambilSemua() throws SQLException {
        String sql = """
            SELECT m.*, u.nama, u.email, u.status, u.login_terakhir,
                   d_user.nama as nama_dosen
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            LEFT JOIN dosen d ON m.dosen_id = d.id
            LEFT JOIN users d_user ON d.user_id = d_user.id
            ORDER BY u.nama ASC
        """;

        List<Mahasiswa> daftarMahasiswa = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarMahasiswa.add(mapRowKeMahasiswa(rs));
            }
        }

        return daftarMahasiswa;
    }

    /**
     * Mengambil mahasiswa berdasarkan dosen pembimbing.
     *
     * @param dosenId ID dosen
     * @return List mahasiswa bimbingan
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Mahasiswa> ambilBerdasarkanDosenId(int dosenId) throws SQLException {
        String sql = """
            SELECT m.*, u.nama, u.email, u.status, u.login_terakhir,
                   d_user.nama as nama_dosen
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            LEFT JOIN dosen d ON m.dosen_id = d.id
            LEFT JOIN users d_user ON d.user_id = d_user.id
            WHERE m.dosen_id = ?
            ORDER BY u.nama ASC
        """;

        List<Mahasiswa> daftarMahasiswa = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarMahasiswa.add(mapRowKeMahasiswa(rs));
            }
        }

        return daftarMahasiswa;
    }

    /**
     * Mengambil mahasiswa yang belum di-assign ke dosen.
     * Termasuk user dengan role mahasiswa yang belum ada entry di tabel mahasiswa.
     *
     * @return List mahasiswa tanpa dosen
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Mahasiswa> ambilTanpaDosen() throws SQLException {
        // Query gabungan:
        // 1. Mahasiswa yang ada di tabel mahasiswa tapi dosen_id IS NULL
        // 2. User dengan role mahasiswa yang belum ada entry di tabel mahasiswa
        String sql = """
            SELECT m.id, m.user_id, m.nim, m.semester, m.dosen_id,
                   u.nama, u.email, u.status, u.login_terakhir,
                   NULL as nama_dosen
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            WHERE m.dosen_id IS NULL

            UNION ALL

            SELECT NULL as id, u.id as user_id, NULL as nim, 1 as semester, NULL as dosen_id,
                   u.nama, u.email, u.status, u.login_terakhir,
                   NULL as nama_dosen
            FROM users u
            WHERE u.role = 'mahasiswa'
            AND u.status = 'active'
            AND NOT EXISTS (SELECT 1 FROM mahasiswa m WHERE m.user_id = u.id)

            ORDER BY nama ASC
        """;

        List<Mahasiswa> daftarMahasiswa = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                daftarMahasiswa.add(mapRowKeMahasiswaTanpaDosen(rs));
            }
        }

        return daftarMahasiswa;
    }

    /**
     * Map ResultSet ke objek Mahasiswa untuk query tanpa dosen.
     * Menangani kasus dimana mahasiswa belum ada di tabel mahasiswa.
     */
    private Mahasiswa mapRowKeMahasiswaTanpaDosen(ResultSet rs) throws SQLException {
        Mahasiswa mhs = new Mahasiswa();
        
        // id bisa null jika user belum ada di tabel mahasiswa
        int id = rs.getInt("id");
        if (!rs.wasNull()) {
            mhs.setId(id);
        }
        
        mhs.setUserId(rs.getInt("user_id"));
        mhs.setNim(rs.getString("nim"));
        mhs.setSemester(rs.getInt("semester"));
        
        Integer dosenId = rs.getInt("dosen_id");
        if (rs.wasNull()) {
            dosenId = null;
        }
        mhs.setDosenId(dosenId);
        
        // Data dari users
        mhs.setNama(rs.getString("nama"));
        mhs.setEmail(rs.getString("email"));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            mhs.setStatus(StatusPengguna.dariKode(statusStr));
        }
        
        return mhs;
    }

    /**
     * Mengambil mahasiswa bimbingan beserta statistik belajar.
     *
     * @param dosenId ID dosen
     * @return List mahasiswa dengan statistik
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Mahasiswa> ambilBimbinganDenganStatistik(int dosenId) throws SQLException {
        String sql = """
            SELECT m.*, u.nama, u.email, u.status, u.login_terakhir,
                   d_user.nama as nama_dosen,
                   (SELECT COUNT(*) FROM mata_kuliah mk WHERE mk.user_id = m.user_id) as jumlah_mata_kuliah,
                   (SELECT COUNT(*) FROM topik t
                    JOIN mata_kuliah mk ON t.id_mata_kuliah = mk.id
                    WHERE mk.user_id = m.user_id) as jumlah_topik,
                   (SELECT COUNT(*) FROM topik t
                    JOIN mata_kuliah mk ON t.id_mata_kuliah = mk.id
                    WHERE mk.user_id = m.user_id AND t.dikuasai = 1) as topik_dikuasai,
                   (SELECT MAX(DATE(tanggal_jadwal)) FROM sesi_belajar sb
                    JOIN mata_kuliah mk ON sb.id_mata_kuliah = mk.id
                    WHERE mk.user_id = m.user_id AND sb.selesai = 1) as aktivitas_terakhir,
                   (SELECT AVG(rating_performa) FROM sesi_belajar sb
                    JOIN mata_kuliah mk ON sb.id_mata_kuliah = mk.id
                    WHERE mk.user_id = m.user_id AND sb.rating_performa IS NOT NULL) as rata_rata_performa
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            LEFT JOIN dosen d ON m.dosen_id = d.id
            LEFT JOIN users d_user ON d.user_id = d_user.id
            WHERE m.dosen_id = ?
            ORDER BY u.nama ASC
        """;

        List<Mahasiswa> daftarMahasiswa = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Mahasiswa mhs = mapRowKeMahasiswa(rs);
                
                mhs.setJumlahMataKuliah(rs.getInt("jumlah_mata_kuliah"));
                mhs.setJumlahTopik(rs.getInt("jumlah_topik"));
                mhs.setTopikDikuasai(rs.getInt("topik_dikuasai"));
                
                int totalTopik = mhs.getJumlahTopik();
                if (totalTopik > 0) {
                    mhs.setProgressKeseluruhan((mhs.getTopikDikuasai() * 100.0) / totalTopik);
                }
                
                String aktivitasTerakhirStr = rs.getString("aktivitas_terakhir");
                if (aktivitasTerakhirStr != null) {
                    mhs.setAktivitasTerakhir(LocalDate.parse(aktivitasTerakhirStr));
                }
                
                mhs.setRataRataPerforma(rs.getDouble("rata_rata_performa"));
                
                daftarMahasiswa.add(mhs);
            }
        }

        return daftarMahasiswa;
    }

    /**
     * Memperbarui data mahasiswa.
     *
     * @param mahasiswa Data mahasiswa yang diperbarui
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(Mahasiswa mahasiswa) throws SQLException {
        String sql = "UPDATE mahasiswa SET nim = ?, semester = ?, dosen_id = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mahasiswa.getNim());
            pstmt.setInt(2, mahasiswa.getSemester());
            
            if (mahasiswa.getDosenId() != null) {
                pstmt.setInt(3, mahasiswa.getDosenId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.setInt(4, mahasiswa.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Assign mahasiswa ke dosen.
     *
     * @param mahasiswaId ID mahasiswa
     * @param dosenId ID dosen (null untuk unassign)
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean assignKeDosen(int mahasiswaId, Integer dosenId) throws SQLException {
        String sql = "UPDATE mahasiswa SET dosen_id = ? WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (dosenId != null) {
                pstmt.setInt(1, dosenId);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setInt(2, mahasiswaId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Bulk assign mahasiswa ke dosen.
     *
     * @param mahasiswaIds List ID mahasiswa
     * @param dosenId ID dosen
     * @return Jumlah mahasiswa yang berhasil di-assign
     * @throws SQLException jika terjadi kesalahan database
     */
    public int bulkAssignKeDosen(List<Integer> mahasiswaIds, int dosenId) throws SQLException {
        String sql = "UPDATE mahasiswa SET dosen_id = ? WHERE id = ?";
        int count = 0;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Integer mahasiswaId : mahasiswaIds) {
                pstmt.setInt(1, dosenId);
                pstmt.setInt(2, mahasiswaId);
                count += pstmt.executeUpdate();
            }
        }

        return count;
    }

    /**
     * Bulk unassign mahasiswa dari dosen.
     *
     * @param mahasiswaIds List ID mahasiswa
     * @return Jumlah mahasiswa yang berhasil di-unassign
     * @throws SQLException jika terjadi kesalahan database
     */
    public int bulkUnassign(List<Integer> mahasiswaIds) throws SQLException {
        String sql = "UPDATE mahasiswa SET dosen_id = NULL WHERE id = ?";
        int count = 0;

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Integer mahasiswaId : mahasiswaIds) {
                pstmt.setInt(1, mahasiswaId);
                count += pstmt.executeUpdate();
            }
        }

        return count;
    }

    /**
     * Mengambil mahasiswa berdasarkan dosen pembimbing.
     *
     * @param dosenId ID dosen
     * @return List mahasiswa yang dibimbing
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Mahasiswa> ambilBerdasarkanDosen(int dosenId) throws SQLException {
        String sql = """
            SELECT m.*, u.nama, u.email, u.status, u.login_terakhir,
                   d_user.nama as nama_dosen
            FROM mahasiswa m
            JOIN users u ON m.user_id = u.id
            LEFT JOIN dosen d ON m.dosen_id = d.id
            LEFT JOIN users d_user ON d.user_id = d_user.id
            WHERE m.dosen_id = ?
        """;
        List<Mahasiswa> daftarMahasiswa = new ArrayList<>();

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dosenId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarMahasiswa.add(mapRowKeMahasiswa(rs));
            }
        }

        return daftarMahasiswa;
    }

    /**
     * Menghapus mahasiswa.
     *
     * @param id ID mahasiswa
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int id) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus mahasiswa berdasarkan user ID.
     *
     * @param userId ID user
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapusBerdasarkanUserId(int userId) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE user_id = ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghitung total mahasiswa.
     *
     * @return Jumlah mahasiswa
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mahasiswa";

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
     * Menghitung mahasiswa bimbingan dosen.
     *
     * @param dosenId ID dosen
     * @return Jumlah mahasiswa bimbingan
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanDosen(int dosenId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE dosen_id = ?";

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
     * Cek apakah NIM sudah digunakan.
     *
     * @param nim NIM yang dicek
     * @param excludeId ID mahasiswa yang dikecualikan (untuk edit)
     * @return true jika NIM sudah ada
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean nimSudahAda(String nim, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE nim = ? AND id != ?";

        try (Connection conn = manajerDB.bukaKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nim);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        }
    }

    private Mahasiswa mapRowKeMahasiswa(ResultSet rs) throws SQLException {
        Mahasiswa mhs = new Mahasiswa();
        mhs.setId(rs.getInt("id"));
        mhs.setUserId(rs.getInt("user_id"));
        mhs.setNim(rs.getString("nim"));
        mhs.setSemester(rs.getInt("semester"));
        
        int dosenId = rs.getInt("dosen_id");
        if (!rs.wasNull()) {
            mhs.setDosenId(dosenId);
        }
        
        Timestamp dibuatPada = rs.getTimestamp("dibuat_pada");
        if (dibuatPada != null) {
            mhs.setDibuatPada(dibuatPada.toLocalDateTime());
        }

        mhs.setNama(rs.getString("nama"));
        mhs.setEmail(rs.getString("email"));
        mhs.setStatus(StatusPengguna.dariKode(rs.getString("status")));
        
        Timestamp loginTerakhir = rs.getTimestamp("login_terakhir");
        if (loginTerakhir != null) {
            mhs.setLoginTerakhir(loginTerakhir.toLocalDateTime());
        }

        mhs.setNamaDosen(rs.getString("nama_dosen"));

        return mhs;
    }
}
