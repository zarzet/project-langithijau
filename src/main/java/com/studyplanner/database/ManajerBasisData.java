package com.studyplanner.database;

import com.studyplanner.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ManajerBasisData {

    private static final String DB_URL = "jdbc:sqlite:study_planner.db";
    private Connection koneksi;

    public ManajerBasisData() {
        inisialisasiBasisData();
    }

    private void inisialisasiBasisData() {
        try {
            koneksi = DriverManager.getConnection(DB_URL);
            buatTabel();
            System.out.println("Basis data berhasil diinisialisasi!");
        } catch (SQLException e) {
            System.err.println(
                    "Gagal menginisialisasi basis data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buatTabel() throws SQLException {
        String buatTabelMataKuliah = """
                    CREATE TABLE IF NOT EXISTS mata_kuliah (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nama TEXT NOT NULL,
                        kode TEXT NOT NULL UNIQUE,
                        deskripsi TEXT,
                        dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
            stmt.execute(buatTabelMataKuliah);
            stmt.execute(buatTabelTopik);
            stmt.execute(buatTabelJadwalUjian);
            stmt.execute(buatTabelSesiBelajar);
        }
    }

    // ==================== OPERASI MATA KULIAH ====================

    public int tambahMataKuliah(MataKuliah mataKuliah) throws SQLException {
        String sql = "INSERT INTO mata_kuliah (nama, kode, deskripsi) VALUES (?, ?, ?)";
        try (
                PreparedStatement pstmt = koneksi.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, mataKuliah.getNama());
            pstmt.setString(2, mataKuliah.getKode());
            pstmt.setString(3, mataKuliah.getDeskripsi());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void perbaruiMataKuliah(MataKuliah mataKuliah) throws SQLException {
        String sql = "UPDATE mata_kuliah SET nama = ?, kode = ?, deskripsi = ? WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setString(1, mataKuliah.getNama());
            pstmt.setString(2, mataKuliah.getKode());
            pstmt.setString(3, mataKuliah.getDeskripsi());
            pstmt.setInt(4, mataKuliah.getId());
            pstmt.executeUpdate();
        }
    }

    public void hapusMataKuliah(int idMataKuliah) throws SQLException {
        String sql = "DELETE FROM mata_kuliah WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, idMataKuliah);
            pstmt.executeUpdate();
        }
    }

    public List<MataKuliah> ambilSemuaMataKuliah() throws SQLException {
        List<MataKuliah> daftarMataKuliah = new ArrayList<>();
        String sql = "SELECT * FROM mata_kuliah ORDER BY kode";

        try (
                Statement stmt = koneksi.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MataKuliah mataKuliah = new MataKuliah();
                mataKuliah.setId(rs.getInt("id"));
                mataKuliah.setNama(rs.getString("nama"));
                mataKuliah.setKode(rs.getString("kode"));
                mataKuliah.setDeskripsi(rs.getString("deskripsi"));
                daftarMataKuliah.add(mataKuliah);
            }
        }
        return daftarMataKuliah;
    }

    public MataKuliah ambilMataKuliahBerdasarkanId(int id) throws SQLException {
        String sql = "SELECT * FROM mata_kuliah WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MataKuliah mataKuliah = new MataKuliah();
                mataKuliah.setId(rs.getInt("id"));
                mataKuliah.setNama(rs.getString("nama"));
                mataKuliah.setKode(rs.getString("kode"));
                mataKuliah.setDeskripsi(rs.getString("deskripsi"));
                return mataKuliah;
            }
        }
        return null;
    }

    // ==================== OPERASI TOPIK ====================

    public int tambahTopik(Topik topik) throws SQLException {
        String sql = """
                    INSERT INTO topik (id_mata_kuliah, nama, deskripsi, prioritas, tingkat_kesulitan,
                                       tanggal_belajar_pertama, tanggal_ulasan_terakhir, jumlah_ulasan,
                                       faktor_kemudahan, interval, dikuasai)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement pstmt = koneksi.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, topik.getIdMataKuliah());
            pstmt.setString(2, topik.getNama());
            pstmt.setString(3, topik.getDeskripsi());
            pstmt.setInt(4, topik.getPrioritas());
            pstmt.setInt(5, topik.getTingkatKesulitan());
            pstmt.setObject(6, topik.getTanggalBelajarPertama());
            pstmt.setObject(7, topik.getTanggalUlasanTerakhir());
            pstmt.setInt(8, topik.getJumlahUlasan());
            pstmt.setDouble(9, topik.getFaktorKemudahan());
            pstmt.setInt(10, topik.getInterval());
            pstmt.setBoolean(11, topik.isDikuasai());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void perbaruiTopik(Topik topik) throws SQLException {
        String sql = """
                    UPDATE topik SET id_mata_kuliah = ?, nama = ?, deskripsi = ?,
                                    prioritas = ?, tingkat_kesulitan = ?,
                                    tanggal_belajar_pertama = ?, tanggal_ulasan_terakhir = ?,
                                    jumlah_ulasan = ?, faktor_kemudahan = ?,
                                    interval = ?, dikuasai = ?
                    WHERE id = ?
                """;

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, topik.getIdMataKuliah());
            pstmt.setString(2, topik.getNama());
            pstmt.setString(3, topik.getDeskripsi());
            pstmt.setInt(4, topik.getPrioritas());
            pstmt.setInt(5, topik.getTingkatKesulitan());
            pstmt.setObject(6, topik.getTanggalBelajarPertama());
            pstmt.setObject(7, topik.getTanggalUlasanTerakhir());
            pstmt.setInt(8, topik.getJumlahUlasan());
            pstmt.setDouble(9, topik.getFaktorKemudahan());
            pstmt.setInt(10, topik.getInterval());
            pstmt.setBoolean(11, topik.isDikuasai());
            pstmt.setInt(12, topik.getId());
            pstmt.executeUpdate();
        }
    }

    public void hapusTopik(int idTopik) throws SQLException {
        String sql = "DELETE FROM topik WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, idTopik);
            pstmt.executeUpdate();
        }
    }

    public List<Topik> ambilTopikBerdasarkanMataKuliah(int idMataKuliah) throws SQLException {
        List<Topik> daftarTopik = new ArrayList<>();
        String sql = "SELECT * FROM topik WHERE id_mata_kuliah = ? ORDER BY prioritas DESC, nama";

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, idMataKuliah);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarTopik.add(ekstrakTopikDariResultSet(rs));
            }
        }
        return daftarTopik;
    }

    public Topik ambilTopikBerdasarkanId(int id) throws SQLException {
        String sql = "SELECT * FROM topik WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return ekstrakTopikDariResultSet(rs);
            }
        }
        return null;
    }

    public List<Topik> ambilSemuaTopik() throws SQLException {
        List<Topik> daftarTopik = new ArrayList<>();
        String sql = "SELECT * FROM topik ORDER BY prioritas DESC, nama";

        try (
                Statement stmt = koneksi.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                daftarTopik.add(ekstrakTopikDariResultSet(rs));
            }
        }
        return daftarTopik;
    }

    private Topik ekstrakTopikDariResultSet(ResultSet rs) throws SQLException {
        Topik topik = new Topik();
        topik.setId(rs.getInt("id"));
        topik.setIdMataKuliah(rs.getInt("id_mata_kuliah"));
        topik.setNama(rs.getString("nama"));
        topik.setDeskripsi(rs.getString("deskripsi"));
        topik.setPrioritas(rs.getInt("prioritas"));
        topik.setTingkatKesulitan(rs.getInt("tingkat_kesulitan"));

        String tanggalBelajarPertama = rs.getString("tanggal_belajar_pertama");
        if (tanggalBelajarPertama != null) {
            topik.setTanggalBelajarPertama(LocalDate.parse(tanggalBelajarPertama));
        }

        String tanggalUlasanTerakhir = rs.getString("tanggal_ulasan_terakhir");
        if (tanggalUlasanTerakhir != null) {
            topik.setTanggalUlasanTerakhir(LocalDate.parse(tanggalUlasanTerakhir));
        }

        topik.setJumlahUlasan(rs.getInt("jumlah_ulasan"));
        topik.setFaktorKemudahan(rs.getDouble("faktor_kemudahan"));
        topik.setInterval(rs.getInt("interval"));
        topik.setDikuasai(rs.getBoolean("dikuasai"));

        return topik;
    }

    // ==================== OPERASI JADWAL UJIAN ====================

    public int tambahJadwalUjian(JadwalUjian ujian) throws SQLException {
        String sql = """
                    INSERT INTO jadwal_ujian (id_mata_kuliah, tipe_ujian, judul, tanggal_ujian,
                                               waktu_ujian, lokasi, catatan, selesai)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement pstmt = koneksi.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ujian.getIdMataKuliah());
            pstmt.setString(2, ujian.getTipeUjian());
            pstmt.setString(3, ujian.getJudul());
            pstmt.setObject(4, ujian.getTanggalUjian());
            pstmt.setObject(5, ujian.getWaktuUjian());
            pstmt.setString(6, ujian.getLokasi());
            pstmt.setString(7, ujian.getCatatan());
            pstmt.setBoolean(8, ujian.isSelesai());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void perbaruiJadwalUjian(JadwalUjian ujian) throws SQLException {
        String sql = """
                    UPDATE jadwal_ujian SET id_mata_kuliah = ?, tipe_ujian = ?, judul = ?,
                                             tanggal_ujian = ?, waktu_ujian = ?, lokasi = ?,
                                             catatan = ?, selesai = ?
                    WHERE id = ?
                """;

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, ujian.getIdMataKuliah());
            pstmt.setString(2, ujian.getTipeUjian());
            pstmt.setString(3, ujian.getJudul());
            pstmt.setObject(4, ujian.getTanggalUjian());
            pstmt.setObject(5, ujian.getWaktuUjian());
            pstmt.setString(6, ujian.getLokasi());
            pstmt.setString(7, ujian.getCatatan());
            pstmt.setBoolean(8, ujian.isSelesai());
            pstmt.setInt(9, ujian.getId());
            pstmt.executeUpdate();
        }
    }

    public void hapusJadwalUjian(int idUjian) throws SQLException {
        String sql = "DELETE FROM jadwal_ujian WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, idUjian);
            pstmt.executeUpdate();
        }
    }

    public List<JadwalUjian> ambilUjianMendatang() throws SQLException {
        List<JadwalUjian> daftarUjian = new ArrayList<>();
        String sql = """
                    SELECT * FROM jadwal_ujian
                    WHERE tanggal_ujian >= date('now') AND selesai = 0
                    ORDER BY tanggal_ujian ASC
                """;

        try (
                Statement stmt = koneksi.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                daftarUjian.add(ekstrakUjianDariResultSet(rs));
            }
        }
        return daftarUjian;
    }

    public List<JadwalUjian> ambilUjianBerdasarkanMataKuliah(int idMataKuliah)
            throws SQLException {
        List<JadwalUjian> daftarUjian = new ArrayList<>();
        String sql = "SELECT * FROM jadwal_ujian WHERE id_mata_kuliah = ? ORDER BY tanggal_ujian";

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, idMataKuliah);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                daftarUjian.add(ekstrakUjianDariResultSet(rs));
            }
        }
        return daftarUjian;
    }

    private JadwalUjian ekstrakUjianDariResultSet(ResultSet rs)
            throws SQLException {
        JadwalUjian ujian = new JadwalUjian();
        ujian.setId(rs.getInt("id"));
        ujian.setIdMataKuliah(rs.getInt("id_mata_kuliah"));
        ujian.setTipeUjian(rs.getString("tipe_ujian"));
        ujian.setJudul(rs.getString("judul"));

        String tanggalUjian = rs.getString("tanggal_ujian");
        if (tanggalUjian != null) {
            ujian.setTanggalUjian(LocalDate.parse(tanggalUjian));
        }

        String waktuUjian = rs.getString("waktu_ujian");
        if (waktuUjian != null) {
            ujian.setWaktuUjian(LocalTime.parse(waktuUjian));
        }

        ujian.setLokasi(rs.getString("lokasi"));
        ujian.setCatatan(rs.getString("catatan"));
        ujian.setSelesai(rs.getBoolean("selesai"));

        return ujian;
    }

    // ==================== OPERASI SESI BELAJAR ====================

    public int tambahSesiBelajar(SesiBelajar sesi) throws SQLException {
        String sql = """
                    INSERT INTO sesi_belajar (id_topik, id_mata_kuliah, tanggal_jadwal,
                                               tipe_sesi, selesai, selesai_pada,
                                               rating_performa, catatan, durasi_menit)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement pstmt = koneksi.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, sesi.getIdTopik());
            pstmt.setInt(2, sesi.getIdMataKuliah());
            pstmt.setObject(3, sesi.getTanggalJadwal());
            pstmt.setString(4, sesi.getTipeSesi());
            pstmt.setBoolean(5, sesi.isSelesai());
            pstmt.setObject(6, sesi.getSelesaiPada());
            pstmt.setObject(
                    7,
                    sesi.getRatingPerforma() > 0
                            ? sesi.getRatingPerforma()
                            : null);
            pstmt.setString(8, sesi.getCatatan());
            pstmt.setInt(9, sesi.getDurasiMenit());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void perbaruiSesiBelajar(SesiBelajar sesi) throws SQLException {
        String sql = """
                    UPDATE sesi_belajar SET id_topik = ?, id_mata_kuliah = ?, tanggal_jadwal = ?,
                                             tipe_sesi = ?, selesai = ?, selesai_pada = ?,
                                             rating_performa = ?, catatan = ?, durasi_menit = ?
                    WHERE id = ?
                """;

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, sesi.getIdTopik());
            pstmt.setInt(2, sesi.getIdMataKuliah());
            pstmt.setObject(3, sesi.getTanggalJadwal());
            pstmt.setString(4, sesi.getTipeSesi());
            pstmt.setBoolean(5, sesi.isSelesai());
            pstmt.setObject(6, sesi.getSelesaiPada());
            pstmt.setObject(
                    7,
                    sesi.getRatingPerforma() > 0
                            ? sesi.getRatingPerforma()
                            : null);
            pstmt.setString(8, sesi.getCatatan());
            pstmt.setInt(9, sesi.getDurasiMenit());
            pstmt.setInt(10, sesi.getId());
            pstmt.executeUpdate();
        }
    }

    public void hapusSesiBelajar(int idSesi) throws SQLException {
        String sql = "DELETE FROM sesi_belajar WHERE id = ?";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, idSesi);
            pstmt.executeUpdate();
        }
    }

    public List<SesiBelajar> ambilSesiBerdasarkanTanggal(LocalDate tanggal)
            throws SQLException {
        List<SesiBelajar> daftarSesi = new ArrayList<>();
        String sql = """
                    SELECT s.*, t.nama as nama_topik, c.nama as nama_mata_kuliah, c.kode as kode_mata_kuliah
                    FROM sesi_belajar s
                    JOIN topik t ON s.id_topik = t.id
                    JOIN mata_kuliah c ON s.id_mata_kuliah = c.id
                    WHERE s.tanggal_jadwal = ?
                    ORDER BY s.tipe_sesi, c.kode
                """;

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setObject(1, tanggal);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SesiBelajar sesi = ekstrakSesiDariResultSet(rs);
                sesi.setNamaTopik(rs.getString("nama_topik"));
                sesi.setNamaMataKuliah(
                        rs.getString("kode_mata_kuliah") +
                                " - " +
                                rs.getString("nama_mata_kuliah"));
                daftarSesi.add(sesi);
            }
        }
        return daftarSesi;
    }

    public List<SesiBelajar> ambilSesiHariIni() throws SQLException {
        return ambilSesiBerdasarkanTanggal(LocalDate.now());
    }

    public List<SesiBelajar> ambilSesiMendatang(int batas)
            throws SQLException {
        List<SesiBelajar> daftarSesi = new ArrayList<>();
        String sql = """
                    SELECT s.*, t.nama as nama_topik, c.nama as nama_mata_kuliah, c.kode as kode_mata_kuliah
                    FROM sesi_belajar s
                    JOIN topik t ON s.id_topik = t.id
                    JOIN mata_kuliah c ON s.id_mata_kuliah = c.id
                    WHERE s.tanggal_jadwal > DATE('now')
                    AND s.selesai = 0
                    ORDER BY s.tanggal_jadwal ASC, s.tipe_sesi
                    LIMIT ?
                """;

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, batas);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SesiBelajar sesi = ekstrakSesiDariResultSet(rs);
                sesi.setNamaTopik(rs.getString("nama_topik"));
                sesi.setNamaMataKuliah(
                        rs.getString("kode_mata_kuliah") +
                                " - " +
                                rs.getString("nama_mata_kuliah"));
                daftarSesi.add(sesi);
            }
        }

        return daftarSesi;
    }

    private SesiBelajar ekstrakSesiDariResultSet(ResultSet rs)
            throws SQLException {
        SesiBelajar sesi = new SesiBelajar();
        sesi.setId(rs.getInt("id"));
        sesi.setIdTopik(rs.getInt("id_topik"));
        sesi.setIdMataKuliah(rs.getInt("id_mata_kuliah"));

        String tanggalJadwal = rs.getString("tanggal_jadwal");
        if (tanggalJadwal != null) {
            sesi.setTanggalJadwal(LocalDate.parse(tanggalJadwal));
        }

        sesi.setTipeSesi(rs.getString("tipe_sesi"));
        sesi.setSelesai(rs.getBoolean("selesai"));

        String selesaiPada = rs.getString("selesai_pada");
        if (selesaiPada != null) {
            sesi.setSelesaiPada(LocalDateTime.parse(selesaiPada));
        }

        int rating = rs.getInt("rating_performa");
        if (!rs.wasNull()) {
            sesi.setRatingPerforma(rating);
        }

        sesi.setCatatan(rs.getString("catatan"));
        sesi.setDurasiMenit(rs.getInt("durasi_menit"));

        return sesi;
    }

    public int ambilRuntutanBelajar() throws SQLException {
        String sql = """
                    WITH RECURSIVE tanggal AS (
                        SELECT DATE('now') as cek_tanggal
                        UNION ALL
                        SELECT DATE(cek_tanggal, '-1 day')
                        FROM tanggal
                        WHERE cek_tanggal > DATE('now', '-30 days')
                    )
                    SELECT COUNT(*) as runtutan
                    FROM (
                        SELECT d.cek_tanggal
                        FROM tanggal d
                        WHERE EXISTS (
                            SELECT 1 FROM sesi_belajar s
                            WHERE DATE(s.tanggal_jadwal) = d.cek_tanggal
                            AND s.selesai = 1
                        )
                        AND d.cek_tanggal <= DATE('now')
                        ORDER BY d.cek_tanggal DESC
                        LIMIT (
                            SELECT COUNT(*)
                            FROM tanggal d2
                            WHERE d2.cek_tanggal <= DATE('now')
                            AND NOT EXISTS (
                                SELECT 1
                                FROM tanggal d3
                                WHERE d3.cek_tanggal > d2.cek_tanggal
                                AND d3.cek_tanggal <= DATE('now')
                                AND NOT EXISTS (
                                    SELECT 1 FROM sesi_belajar s
                                    WHERE DATE(s.tanggal_jadwal) = d3.cek_tanggal
                                    AND s.selesai = 1
                                )
                            )
                        )
                    )
                """;

        try (
                PreparedStatement pstmt = koneksi.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("runtutan");
            }
        }
        return 0;
    }

    public int ambilWaktuBelajarHariIni() throws SQLException {
        String sql = """
                    SELECT COALESCE(SUM(durasi_menit), 0) as total_menit
                    FROM sesi_belajar
                    WHERE tanggal_jadwal = DATE('now')
                    AND selesai = 1
                """;

        try (
                PreparedStatement pstmt = koneksi.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_menit");
            }
        }
        return 0;
    }

    public int ambilWaktuBelajarKemarin() throws SQLException {
        String sql = """
                    SELECT COALESCE(SUM(durasi_menit), 0) as total_menit
                    FROM sesi_belajar
                    WHERE tanggal_jadwal = DATE('now', '-1 day')
                    AND selesai = 1
                """;

        try (
                PreparedStatement pstmt = koneksi.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_menit");
            }
        }
        return 0;
    }

    public List<Topik> ambilTopikUlasanBerikutnya(int batas) throws SQLException {
        List<Topik> daftarTopik = new ArrayList<>();
        String sql = """
                    SELECT t.*, c.nama as nama_mata_kuliah, c.kode as kode_mata_kuliah,
                           DATE(t.tanggal_ulasan_terakhir, '+' || t.interval || ' days') as tanggal_ulasan_berikutnya
                    FROM topik t
                    JOIN mata_kuliah c ON t.id_mata_kuliah = c.id
                    WHERE t.tanggal_ulasan_terakhir IS NOT NULL
                    AND t.dikuasai = 0
                    AND DATE(t.tanggal_ulasan_terakhir, '+' || t.interval || ' days') <= DATE('now', '+7 days')
                    ORDER BY tanggal_ulasan_berikutnya ASC, t.prioritas DESC
                    LIMIT ?
                """;

        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, batas);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Topik topik = ekstrakTopikDariResultSet(rs);
                daftarTopik.add(topik);
            }
        }
        return daftarTopik;
    }

    public void tutup() {
        try {
            if (koneksi != null && !koneksi.isClosed()) {
                koneksi.close();
            }
        } catch (SQLException e) {
            System.err.println(
                    "Gagal menutup koneksi basis data: " + e.getMessage());
        }
    }
}
