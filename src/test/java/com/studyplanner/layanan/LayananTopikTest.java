package com.studyplanner.layanan;

import com.studyplanner.dao.*;
import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.SesiBelajar;
import com.studyplanner.model.Topik;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk LayananTopik.
 */
@DisplayName("LayananTopik Tests")
class LayananTopikTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    
    private LayananTopik layananTopik;
    private DAOMataKuliah daoMataKuliah;
    private DAOSesiBelajar daoSesiBelajar;
    private MataKuliah mataKuliahTest;

    @BeforeAll
    static void setUpClass() throws SQLException {
        dbHelper = new TestDatabaseHelper();
        dbHelper.bukaKoneksi();
        manajerDB = new ManajerBasisDataTest(dbHelper);
    }

    @AfterAll
    static void tearDownClass() throws SQLException {
        dbHelper.tutupKoneksi();
    }

    @BeforeEach
    void setUp() throws SQLException {
        dbHelper.bersihkanData();
        layananTopik = new LayananTopik(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        daoSesiBelajar = new DAOSesiBelajar(manajerDB);
        
        // Setup mata kuliah test
        mataKuliahTest = new MataKuliah(0, "Test Course", "TEST", "Deskripsi");
        mataKuliahTest.setUserId(TEST_USER_ID);
        daoMataKuliah.simpan(mataKuliahTest);
    }

    private Topik buatTopik(String nama) {
        return new Topik(0, mataKuliahTest.getId(), nama, "Deskripsi " + nama);
    }

    private SesiBelajar buatSesi(int idTopik) {
        SesiBelajar sesi = new SesiBelajar();
        sesi.setIdTopik(idTopik);
        sesi.setIdMataKuliah(mataKuliahTest.getId());
        sesi.setTanggalJadwal(LocalDate.now());
        sesi.setTipeSesi("BELAJAR");
        return sesi;
    }

    @Nested
    @DisplayName("tambah() Tests")
    class TambahTests {
        
        @Test
        @DisplayName("tambah() berhasil menyimpan topik baru")
        void tambah_berhasil() throws SQLException {
            Topik topik = buatTopik("Topik Baru");
            
            int id = layananTopik.tambah(topik);
            
            assertTrue(id > 0);
        }
        
        @Test
        @DisplayName("tambah() dengan nama null melempar exception")
        void tambah_namaNullThrowsException() {
            Topik topik = new Topik(0, mataKuliahTest.getId(), null, "Deskripsi");
            
            assertThrows(IllegalArgumentException.class, () -> layananTopik.tambah(topik));
        }
        
        @Test
        @DisplayName("tambah() dengan nama kosong melempar exception")
        void tambah_namaKosongThrowsException() {
            Topik topik = new Topik(0, mataKuliahTest.getId(), "   ", "Deskripsi");
            
            assertThrows(IllegalArgumentException.class, () -> layananTopik.tambah(topik));
        }
    }

    @Nested
    @DisplayName("hapus() Tests - Cascade Delete")
    class HapusCascadeTests {
        
        @Test
        @DisplayName("hapus() juga menghapus semua sesi belajar terkait")
        void hapus_cascadeDeleteSesiBelajar() throws SQLException {
            Topik topik = buatTopik("Test Topic");
            layananTopik.tambah(topik);
            
            // Buat sesi belajar untuk topik
            SesiBelajar sesi1 = buatSesi(topik.getId());
            SesiBelajar sesi2 = buatSesi(topik.getId());
            daoSesiBelajar.simpan(sesi1);
            daoSesiBelajar.simpan(sesi2);
            
            // Verifikasi sesi ada
            List<SesiBelajar> sebelum = daoSesiBelajar.ambilBerdasarkanTopikId(topik.getId());
            assertEquals(2, sebelum.size());
            
            // Hapus topik
            boolean hasil = layananTopik.hapus(topik.getId());
            
            assertTrue(hasil);
            
            // Verifikasi sesi sudah terhapus
            List<SesiBelajar> sesudah = daoSesiBelajar.ambilBerdasarkanTopikId(topik.getId());
            assertTrue(sesudah.isEmpty());
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanMataKuliah() Tests")
    class AmbilBerdasarkanMataKuliahTests {
        
        @Test
        @DisplayName("ambilBerdasarkanMataKuliah() mengembalikan topik untuk mata kuliah")
        void ambil_berhasil() throws SQLException {
            layananTopik.tambah(buatTopik("Topik 1"));
            layananTopik.tambah(buatTopik("Topik 2"));
            
            List<Topik> hasil = layananTopik.ambilBerdasarkanMataKuliah(mataKuliahTest.getId());
            
            assertEquals(2, hasil.size());
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {
        
        @Test
        @DisplayName("perbarui() berhasil mengupdate topik")
        void perbarui_berhasil() throws SQLException {
            Topik topik = buatTopik("Nama Lama");
            layananTopik.tambah(topik);
            
            topik.setNama("Nama Baru");
            boolean hasil = layananTopik.perbarui(topik);
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanId() Tests")
    class AmbilBerdasarkanIdTests {
        
        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan topik yang ada")
        void ambil_berhasil() throws SQLException {
            Topik topik = buatTopik("Test Topic");
            layananTopik.tambah(topik);
            
            Topik hasil = layananTopik.ambilBerdasarkanId(topik.getId());
            
            assertNotNull(hasil);
            assertEquals("Test Topic", hasil.getNama());
        }
        
        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan null jika tidak ada")
        void ambil_tidakAda() throws SQLException {
            Topik hasil = layananTopik.ambilBerdasarkanId(99999);
            
            assertNull(hasil);
        }
    }

    @Nested
    @DisplayName("prosesHasilReview() Tests")
    class ProsesHasilReviewTests {
        
        @Test
        @DisplayName("prosesHasilReview() dengan nilai valid")
        void proses_berhasil() throws SQLException {
            Topik topik = buatTopik("Test Topic");
            layananTopik.tambah(topik);
            
            LocalDate hasil = layananTopik.prosesHasilReview(topik.getId(), 4);
            
            assertNotNull(hasil);
            assertTrue(hasil.isAfter(LocalDate.now()) || hasil.isEqual(LocalDate.now()));
        }
        
        @Test
        @DisplayName("prosesHasilReview() dengan nilai < 1 melempar exception")
        void proses_nilaiTerlaluKecil() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananTopik.prosesHasilReview(1, 0));
        }
        
        @Test
        @DisplayName("prosesHasilReview() dengan nilai > 5 melempar exception")
        void proses_nilaiTerlaluBesar() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananTopik.prosesHasilReview(1, 6));
        }
        
        @Test
        @DisplayName("prosesHasilReview() dengan topik tidak ada melempar exception")
        void proses_topikTidakAda() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananTopik.prosesHasilReview(99999, 4));
        }
    }

    @Nested
    @DisplayName("ambilSemua() Tests")
    class AmbilSemuaTests {
        
        @Test
        @DisplayName("ambilSemua() mengembalikan semua topik")
        void ambilSemua_berhasil() throws SQLException {
            layananTopik.tambah(buatTopik("Topik 1"));
            layananTopik.tambah(buatTopik("Topik 2"));
            
            List<Topik> hasil = layananTopik.ambilSemua();
            
            assertEquals(2, hasil.size());
        }
    }

    @Nested
    @DisplayName("ambilTopikUntukDiulang() Tests")
    class AmbilTopikUntukDiulangTests {
        
        @Test
        @DisplayName("ambilTopikUntukDiulang() mengembalikan list topik")
        void ambil_berhasil() throws SQLException {
            Topik topik = buatTopik("Test");
            topik.setTanggalBelajarPertama(LocalDate.now().minusDays(2));
            layananTopik.tambah(topik);
            
            List<Topik> hasil = layananTopik.ambilTopikUntukDiulang(mataKuliahTest.getId());
            
            assertNotNull(hasil);
        }
    }

    @Nested
    @DisplayName("tandaiDikuasai() Tests")
    class TandaiDikuasaiTests {
        
        @Test
        @DisplayName("tandaiDikuasai() berhasil")
        void tandai_berhasil() throws SQLException {
            Topik topik = buatTopik("Test");
            layananTopik.tambah(topik);
            
            boolean hasil = layananTopik.tandaiDikuasai(topik.getId());
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("hitungBerdasarkanMataKuliah() Tests")
    class HitungBerdasarkanMataKuliahTests {
        
        @Test
        @DisplayName("hitungBerdasarkanMataKuliah() mengembalikan jumlah")
        void hitung_berhasil() throws SQLException {
            layananTopik.tambah(buatTopik("Topik 1"));
            layananTopik.tambah(buatTopik("Topik 2"));
            
            int hasil = layananTopik.hitungBerdasarkanMataKuliah(mataKuliahTest.getId());
            
            assertEquals(2, hasil);
        }
    }

    @Nested
    @DisplayName("Validasi Tests")
    class ValidasiTests {
        
        @Test
        @DisplayName("tambah() dengan null melempar exception")
        void tambah_nullException() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananTopik.tambah(null));
        }
        
        @Test
        @DisplayName("tambah() dengan nama kosong melempar exception")
        void tambah_namaKosongException() {
            Topik topik = new Topik();
            topik.setIdMataKuliah(mataKuliahTest.getId());
            topik.setNama("");
            
            assertThrows(IllegalArgumentException.class, 
                () -> layananTopik.tambah(topik));
        }
    }
}
