package com.studyplanner.basisdata;

import com.studyplanner.eksepsi.EksepsiAksesBasisData;
import com.studyplanner.eksepsi.EksepsiKoneksiBasisData;
import com.studyplanner.model.*;
import com.studyplanner.utilitas.PencatatLog;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manajer basis data dengan connection pooling menggunakan HikariCP.
 * Mengelola koneksi ke SQLite dan menyediakan operasi basis data.
 */
public class ManajerBasisData {

    private static final String URL_BASIS_DATA = "jdbc:sqlite:study_planner.db";
    
    // Konstanta konfigurasi
    private static final int UKURAN_POOL_MAKSIMUM = 10;
    private static final int UKURAN_POOL_MINIMUM = 2;
    private static final long WAKTU_IDLE_MAKSIMUM_MS = 300000; // 5 menit
    private static final long WAKTU_KONEKSI_MAKSIMUM_MS = 30000; // 30 detik
    
    private final HikariDataSource sumberData;
    private static ManajerBasisData instansTunggal;

    public ManajerBasisData() {
        this.sumberData = buatSumberData();
        inisialisasiBasisData();
    }
    
    public static synchronized ManajerBasisData dapatkanInstans() {
        if (instansTunggal == null) {
            instansTunggal = new ManajerBasisData();
        }
        return instansTunggal;
    }
    
    private HikariDataSource buatSumberData() {
        HikariConfig konfigurasi = new HikariConfig();
        konfigurasi.setJdbcUrl(URL_BASIS_DATA);
        konfigurasi.setMaximumPoolSize(UKURAN_POOL_MAKSIMUM);
        konfigurasi.setMinimumIdle(UKURAN_POOL_MINIMUM);
        konfigurasi.setIdleTimeout(WAKTU_IDLE_MAKSIMUM_MS);
        konfigurasi.setConnectionTimeout(WAKTU_KONEKSI_MAKSIMUM_MS);
        konfigurasi.setPoolName("StudyPlannerPool");
        
        konfigurasi.addDataSourceProperty("cachePrepStmts", "true");
        konfigurasi.addDataSourceProperty("prepStmtCacheSize", "250");
        
        return new HikariDataSource(konfigurasi);
    }

    private void inisialisasiBasisData() {
        try (Connection koneksi = sumberData.getConnection()) {
            buatTabel(koneksi);
            PencatatLog.info("Basis data berhasil diinisialisasi dengan connection pool!");
        } catch (SQLException e) {
            PencatatLog.error("Gagal menginisialisasi basis data: " + e.getMessage());
            throw new EksepsiKoneksiBasisData("Gagal menginisialisasi basis data", e);
        }
    }

    private void buatTabel(Connection koneksi) throws SQLException {
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
                        user_id INTEGER NOT NULL DEFAULT 1,
                        nama TEXT NOT NULL,
                        kode TEXT NOT NULL,
                        deskripsi TEXT,
                        dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                        UNIQUE(user_id, kode)
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

        // Tabel Dosen
        String buatTabelDosen = """
                    CREATE TABLE IF NOT EXISTS dosen (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL UNIQUE,
                        nip TEXT UNIQUE,
                        max_mahasiswa INTEGER DEFAULT 30,
                        dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    )
                """;

        // Tabel Mahasiswa
        String buatTabelMahasiswa = """
                    CREATE TABLE IF NOT EXISTS mahasiswa (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL UNIQUE,
                        nim TEXT UNIQUE,
                        semester INTEGER DEFAULT 1,
                        dosen_id INTEGER,
                        dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (dosen_id) REFERENCES dosen(id) ON DELETE SET NULL
                    )
                """;

        // Tabel Rekomendasi
        String buatTabelRekomendasi = """
                    CREATE TABLE IF NOT EXISTS rekomendasi (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        dosen_id INTEGER NOT NULL,
                        mahasiswa_id INTEGER NOT NULL,
                        id_mata_kuliah INTEGER,
                        nama_topik TEXT NOT NULL,
                        deskripsi TEXT,
                        prioritas_saran INTEGER DEFAULT 3,
                        kesulitan_saran INTEGER DEFAULT 3,
                        url_sumber TEXT,
                        status TEXT DEFAULT 'pending',
                        dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (dosen_id) REFERENCES dosen(id) ON DELETE CASCADE,
                        FOREIGN KEY (mahasiswa_id) REFERENCES mahasiswa(id) ON DELETE CASCADE,
                        FOREIGN KEY (id_mata_kuliah) REFERENCES mata_kuliah(id) ON DELETE SET NULL
                    )
                """;

        try (Statement stmt = koneksi.createStatement()) {
            stmt.execute(buatTabelUsers);
            stmt.execute(buatTabelDosen);
            stmt.execute(buatTabelMahasiswa);
            stmt.execute(buatTabelMataKuliah);
            stmt.execute(buatTabelTopik);
            stmt.execute(buatTabelJadwalUjian);
            stmt.execute(buatTabelSesiBelajar);
            stmt.execute(buatTabelRekomendasi);
        }

        tambahKolomJikaBelumAda(koneksi, "topik", "stabilitas_fsrs", "REAL DEFAULT 0");
        tambahKolomJikaBelumAda(koneksi, "topik", "kesulitan_fsrs", "REAL DEFAULT 0");
        tambahKolomJikaBelumAda(koneksi, "topik", "retensi_diinginkan", "REAL DEFAULT 0.9");
        tambahKolomJikaBelumAda(koneksi, "topik", "peluruhan_fsrs", "REAL DEFAULT 0.1542");

        tambahKolomJikaBelumAda(koneksi, "mata_kuliah", "user_id", "INTEGER NOT NULL DEFAULT 1");

        tambahKolomJikaBelumAda(koneksi, "users", "role", "TEXT DEFAULT 'mahasiswa'");
        tambahKolomJikaBelumAda(koneksi, "users", "status", "TEXT DEFAULT 'active'");
        tambahKolomJikaBelumAda(koneksi, "users", "login_terakhir", "TIMESTAMP");
    }

    private void catatKueri(String sql) {
        PencatatQuery.getInstance().catat(sql);
    }

    public int ambilRuntutanBelajar() {
        String sql = """
                    WITH RECURSIVE tanggal AS (
                        SELECT DATE('now', 'localtime') as cek_tanggal
                        UNION ALL
                        SELECT DATE(cek_tanggal, '-1 day')
                        FROM tanggal
                        WHERE cek_tanggal > DATE('now', 'localtime', '-30 days')
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
                        AND d.cek_tanggal <= DATE('now', 'localtime')
                        ORDER BY d.cek_tanggal DESC
                        LIMIT (
                            SELECT COUNT(*)
                            FROM tanggal d2
                            WHERE d2.cek_tanggal <= DATE('now', 'localtime')
                            AND NOT EXISTS (
                                SELECT 1
                                FROM tanggal d3
                                WHERE d3.cek_tanggal > d2.cek_tanggal
                                AND d3.cek_tanggal <= DATE('now', 'localtime')
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

        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("runtutan");
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mengambil runtutan belajar", e);
        }
        return 0;
    }

    public int ambilWaktuBelajarHariIni() {
        String sql = """
                    SELECT COALESCE(SUM(durasi_menit), 0) as total_menit
                    FROM sesi_belajar
                    WHERE tanggal_jadwal = ?
                    AND selesai = 1
                """;
        catatKueri(sql);

        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_menit");
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mengambil waktu belajar hari ini", e);
        }
        return 0;
    }

    public int ambilWaktuBelajarKemarin() {
        String sql = """
                    SELECT COALESCE(SUM(durasi_menit), 0) as total_menit
                    FROM sesi_belajar
                    WHERE tanggal_jadwal = ?
                    AND selesai = 1
                """;
        catatKueri(sql);

        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now().minusDays(1)));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_menit");
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mengambil waktu belajar kemarin", e);
        }
        return 0;
    }

    public List<Topik> ambilTopikUlasanBerikutnya(int batas) {
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

        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, batas);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Topik topik = ekstrakTopikDariResultSet(rs);
                daftarTopik.add(topik);
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mengambil topik ulasan berikutnya", e);
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
        } catch (SQLException ignored) {
        }

        return topik;
    }

    public void tutup() {
        if (sumberData != null && !sumberData.isClosed()) {
            sumberData.close();
            PencatatLog.info("Connection pool berhasil ditutup.");
        }
    }
    
    public boolean apakahAktif() {
        return sumberData != null && !sumberData.isClosed();
    }

    public int tambahUser(String username, String password, String email, String nama, String provider) {
        String sql = "INSERT INTO users (username, password, email, nama, provider) VALUES (?, ?, ?, ?, ?)";
        catatKueri(sql);
        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal menambah user", e);
        }
        return -1;
    }

    public int tambahUserGoogle(String googleId, String email, String nama, String fotoProfil) {
        String sql = "INSERT INTO users (google_id, email, nama, foto_profil, provider) VALUES (?, ?, ?, ?, 'google')";
        catatKueri(sql);
        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, googleId);
            pstmt.setString(2, email);
            pstmt.setString(3, nama);
            pstmt.setString(4, fotoProfil);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal menambah user Google", e);
        }
        return -1;
    }

    public java.util.Map<String, Object> cariUserBerdasarkanUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        catatKueri(sql);
        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mencari user berdasarkan username", e);
        }
        return null;
    }

    public java.util.Map<String, Object> cariUserBerdasarkanGoogleId(String googleId) {
        String sql = "SELECT * FROM users WHERE google_id = ?";
        catatKueri(sql);
        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
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
                user.put("role", rs.getString("role"));
                user.put("status", rs.getString("status"));
                return user;
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mencari user berdasarkan Google ID", e);
        }
        return null;
    }

    public java.util.Map<String, Object> cariUserBerdasarkanId(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        catatKueri(sql);
        try (Connection koneksi = bukaKoneksi();
             PreparedStatement pstmt = koneksi.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                java.util.Map<String, Object> user = new java.util.HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("google_id", rs.getString("google_id"));
                user.put("email", rs.getString("email"));
                user.put("nama", rs.getString("nama"));
                user.put("username", rs.getString("username"));
                user.put("foto_profil", rs.getString("foto_profil"));
                user.put("provider", rs.getString("provider"));
                user.put("role", rs.getString("role"));
                user.put("status", rs.getString("status"));
                return user;
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mencari user berdasarkan ID", e);
        }
        return null;
    }

    public List<String> ambilDaftarTabel() {
        List<String> daftarTabel = new ArrayList<>();
        try (Connection koneksi = bukaKoneksi()) {
            DatabaseMetaData md = koneksi.getMetaData();
            try (ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" })) {
                while (rs.next()) {
                    String namaTabel = rs.getString("TABLE_NAME");
                    if (!namaTabel.startsWith("sqlite_")) {
                        daftarTabel.add(namaTabel);
                    }
                }
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal mengambil daftar tabel", e);
        }
        return daftarTabel;
    }

    public List<java.util.Map<String, Object>> jalankanKueriSelect(String sql) {
        catatKueri("[MANUAL] " + sql);
        List<java.util.Map<String, Object>> hasil = new ArrayList<>();
        
        try (Connection koneksi = bukaKoneksi();
             Statement stmt = koneksi.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int jumlahKolom = metaData.getColumnCount();
            
            while (rs.next()) {
                java.util.Map<String, Object> baris = new java.util.HashMap<>();
                for (int i = 1; i <= jumlahKolom; i++) {
                    baris.put(metaData.getColumnName(i), rs.getObject(i));
                }
                hasil.add(baris);
            }
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal menjalankan kueri SELECT", e);
        }
        return hasil;
    }
    
    public void jalankanKueriUpdate(String sql) {
        catatKueri("[MANUAL] " + sql);
        try (Connection koneksi = bukaKoneksi();
             Statement stmt = koneksi.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new EksepsiAksesBasisData("Gagal menjalankan kueri UPDATE", e);
        }
    }

    private void tambahKolomJikaBelumAda(Connection koneksi, String namaTabel, String namaKolom, String definisi)
            throws SQLException {
        String sqlCek = "PRAGMA table_info(" + namaTabel + ")";
        boolean sudahAda = false;
        try (Statement stmt = koneksi.createStatement(); 
             ResultSet rs = stmt.executeQuery(sqlCek)) {
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

    public Connection bukaKoneksi() {
        try {
            return sumberData.getConnection();
        } catch (SQLException e) {
            throw new EksepsiKoneksiBasisData("Gagal mendapatkan koneksi dari pool", e);
        }
    }
    
    public String dapatkanStatistikPool() {
        if (sumberData == null) {
            return "Pool tidak aktif";
        }
        return String.format(
            "Aktif: %d, Idle: %d, Menunggu: %d, Total: %d",
            sumberData.getHikariPoolMXBean().getActiveConnections(),
            sumberData.getHikariPoolMXBean().getIdleConnections(),
            sumberData.getHikariPoolMXBean().getThreadsAwaitingConnection(),
            sumberData.getHikariPoolMXBean().getTotalConnections()
        );
    }
}
