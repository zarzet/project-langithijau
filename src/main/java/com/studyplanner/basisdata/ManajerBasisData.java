package com.studyplanner.basisdata;

import com.studyplanner.model.*;
import java.sql.*;
import java.time.LocalDate;
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

        String buatTabelFsrsModel = """
                    CREATE TABLE IF NOT EXISTS fsrs_model (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nama_model TEXT DEFAULT 'default',
                        bobot TEXT NOT NULL,
                        loss REAL DEFAULT 0,
                        akurasi REAL DEFAULT 0,
                        catatan TEXT,
                        dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """;

        try (Statement stmt = koneksi.createStatement()) {
            stmt.execute(buatTabelUsers);
            stmt.execute(buatTabelMataKuliah);
            stmt.execute(buatTabelTopik);
            stmt.execute(buatTabelJadwalUjian);
            stmt.execute(buatTabelSesiBelajar);
            stmt.execute(buatTabelFsrsModel);
        }

        tambahKolomJikaBelumAda("topik", "stabilitas_fsrs", "REAL DEFAULT 0");
        tambahKolomJikaBelumAda("topik", "kesulitan_fsrs", "REAL DEFAULT 0");
        tambahKolomJikaBelumAda("topik", "retensi_diinginkan", "REAL DEFAULT 0.9");
        tambahKolomJikaBelumAda("topik", "peluruhan_fsrs", "REAL DEFAULT 0.1542");
    }

    private void catatKueri(String sql) {
        PencatatQuery.getInstance().catat(sql);
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
        catatKueri(sql);

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
        catatKueri(sql);

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
        catatKueri(sql);

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
        catatKueri(sql);

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
        try {
            topik.setStabilitasFsrs(rs.getDouble("stabilitas_fsrs"));
            topik.setKesulitanFsrs(rs.getDouble("kesulitan_fsrs"));
            topik.setRetensiDiinginkan(rs.getDouble("retensi_diinginkan"));
            topik.setPeluruhanFsrs(rs.getDouble("peluruhan_fsrs"));
        } catch (SQLException e) {
            // Kolom baru FSRS mungkin belum ada pada basis data lama; diabaikan untuk kompatibilitas.
        }

        return topik;
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

    public int tambahUser(String username, String password, String email, String nama, String provider) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, nama, provider) VALUES (?, ?, ?, ?, ?)";
        catatKueri(sql);
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, nama);
            pstmt.setString(5, provider);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public int tambahUserGoogle(String googleId, String email, String nama, String fotoProfil) throws SQLException {
        String sql = "INSERT INTO users (google_id, email, nama, foto_profil, provider) VALUES (?, ?, ?, ?, 'google')";
        catatKueri(sql);
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, googleId);
            pstmt.setString(2, email);
            pstmt.setString(3, nama);
            pstmt.setString(4, fotoProfil);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public java.util.Map<String, Object> cariUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        catatKueri(sql);
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                java.util.Map<String, Object> user = new java.util.HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("username", rs.getString("username"));
                user.put("password", rs.getString("password"));
                user.put("email", rs.getString("email"));
                user.put("nama", rs.getString("nama"));
                user.put("provider", rs.getString("provider"));
                return user;
            }
        }
        return null;
    }

    public java.util.Map<String, Object> cariUserByGoogleId(String googleId) throws SQLException {
        String sql = "SELECT * FROM users WHERE google_id = ?";
        catatKueri(sql);
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setString(1, googleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                java.util.Map<String, Object> user = new java.util.HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("google_id", rs.getString("google_id"));
                user.put("email", rs.getString("email"));
                user.put("nama", rs.getString("nama"));
                user.put("foto_profil", rs.getString("foto_profil"));
                user.put("provider", rs.getString("provider"));
                return user;
            }
        }
        return null;
    }

    public List<String> ambilDaftarTabel() throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData md = koneksi.getMetaData();
        try (ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!tableName.startsWith("sqlite_")) {
                    tables.add(tableName);
                }
            }
        }
        return tables;
    }

    public List<java.util.Map<String, Object>> jalankanQuerySelect(String sql) throws SQLException {
        catatKueri("[MANUAL] " + sql);
        List<java.util.Map<String, Object>> results = new ArrayList<>();
        
        try (Statement stmt = koneksi.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }
    
    public void jalankanQueryUpdate(String sql) throws SQLException {
        catatKueri("[MANUAL] " + sql);
        try (Statement stmt = koneksi.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private void tambahKolomJikaBelumAda(String namaTabel, String namaKolom, String definisi)
            throws SQLException {
        String sqlCek = "PRAGMA table_info(" + namaTabel + ")";
        boolean sudahAda = false;
        try (Statement stmt = koneksi.createStatement(); ResultSet rs = stmt.executeQuery(sqlCek)) {
            while (rs.next()) {
                if (namaKolom.equalsIgnoreCase(rs.getString("name"))) {
                    sudahAda = true;
                    break;
                }
            }
        }

        if (!sudahAda) {
            try (Statement stmt = koneksi.createStatement()) {
                stmt.execute("ALTER TABLE " + namaTabel + " ADD COLUMN " + namaKolom + " " + definisi);
            }
        }
    }

    public void simpanModelFSRS(double[] bobot, double loss, double akurasi, String catatan) throws SQLException {
        String sql = "INSERT INTO fsrs_model (nama_model, bobot, loss, akurasi, catatan) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setString(1, "default");
            pstmt.setString(2, serializeBobot(bobot));
            pstmt.setDouble(3, loss);
            pstmt.setDouble(4, akurasi);
            pstmt.setString(5, catatan);
            pstmt.executeUpdate();
        }
    }

    public com.studyplanner.model.ModelFSRS ambilModelFSRSTerbaru() throws SQLException {
        String sql = "SELECT * FROM fsrs_model ORDER BY dibuat_pada DESC LIMIT 1";
        try (PreparedStatement pstmt = koneksi.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                com.studyplanner.model.ModelFSRS model = new com.studyplanner.model.ModelFSRS();
                model.setNama(rs.getString("nama_model"));
                model.setBobot(deserializeBobot(rs.getString("bobot")));
                model.setLoss(rs.getDouble("loss"));
                model.setAkurasi(rs.getDouble("akurasi"));
                model.setCatatan(rs.getString("catatan"));
                model.setDibuatPada(rs.getString("dibuat_pada"));
                return model;
            }
        }
        return null;
    }

    private String serializeBobot(double[] bobot) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bobot.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(bobot[i]);
        }
        return sb.toString();
    }

    private double[] deserializeBobot(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] parts = data.split(",");
        double[] arr = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Double.parseDouble(parts[i]);
        }
        return arr;
    }

    /**
     * Memberikan akses ke Connection untuk DAO classes.
     * Method ini membuat koneksi baru setiap kali dipanggil
     * sehingga aman untuk digunakan dengan try-with-resources.
     */
    public Connection bukaKoneksi() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
