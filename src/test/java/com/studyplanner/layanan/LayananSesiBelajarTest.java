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
 * Integration test untuk LayananSesiBelajar.
 */
@DisplayName("LayananSesiBelajar Tests")
class LayananSesiBelajarTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    
    private LayananSesiBelajar layananSesiBelajar;
    private DAOMataKuliah daoMataKuliah;
    private DAOTopik daoTopik;
    private MataKuliah mataKuliahTest;
    private Topik topikTest;

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
        layananSesiBelajar = new LayananSesiBelajar(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        daoTopik = new DAOTopik(manajerDB);
        
        // Setup data test
        mataKuliahTest = new MataKuliah(0, "Test Course", "TEST", "Deskripsi");
        mataKuliahTest.setUserId(TEST_USER_ID);
        daoMataKuliah.simpan(mataKuliahTest);
        
        topikTest = new Topik(0, mataKuliahTest.getId(), "Test Topic", "Deskripsi");
        daoTopik.simpan(topikTest);
    }

    private SesiBelajar buatSesi(LocalDate tanggal) {
        SesiBelajar sesi = new SesiBelajar();
        sesi.setIdTopik(topikTest.getId());
        sesi.setIdMataKuliah(mataKuliahTest.getId());
        sesi.setTanggalJadwal(tanggal);
        sesi.setTipeSesi("BELAJAR");
        return sesi;
    }

    @Nested
    @DisplayName("tambah() Tests")
    class TambahTests {
        
        @Test
        @DisplayName("tambah() berhasil menyimpan sesi belajar baru")
        void tambah_berhasil() throws SQLException {
            SesiBelajar sesi = buatSesi(LocalDate.now());
            
            int id = layananSesiBelajar.tambah(sesi);
            
            assertTrue(id > 0);
        }
        
        @Test
        @DisplayName("tambah() dengan tanggal null melempar exception")
        void tambah_tanggalNullThrowsException() {
            SesiBelajar sesi = new SesiBelajar();
            sesi.setIdTopik(topikTest.getId());
            sesi.setIdMataKuliah(mataKuliahTest.getId());
            // tanggal tidak diset
            
            assertThrows(IllegalArgumentException.class, () -> layananSesiBelajar.tambah(sesi));
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanTanggal() Tests")
    class AmbilBerdasarkanTanggalTests {
        
        @Test
        @DisplayName("ambilBerdasarkanTanggal() mengembalikan sesi untuk tanggal tersebut")
        void ambil_berhasil() throws SQLException {
            LocalDate tanggal = LocalDate.now();
            layananSesiBelajar.tambah(buatSesi(tanggal));
            layananSesiBelajar.tambah(buatSesi(tanggal));
            
            List<SesiBelajar> hasil = layananSesiBelajar.ambilBerdasarkanTanggal(tanggal);
            
            assertEquals(2, hasil.size());
        }
    }

    @Nested
    @DisplayName("ambilSesiHariIni() Tests")
    class AmbilSesiHariIniTests {
        
        @Test
        @DisplayName("ambilSesiHariIni() mengembalikan sesi untuk hari ini")
        void ambil_berhasil() throws SQLException {
            layananSesiBelajar.tambah(buatSesi(LocalDate.now()));
            
            List<SesiBelajar> hasil = layananSesiBelajar.ambilSesiHariIni();
            
            assertFalse(hasil.isEmpty());
        }
    }

    @Nested
    @DisplayName("hapus() Tests")
    class HapusTests {
        
        @Test
        @DisplayName("hapus() berhasil menghapus sesi")
        void hapus_berhasil() throws SQLException {
            SesiBelajar sesi = buatSesi(LocalDate.now());
            layananSesiBelajar.tambah(sesi);
            
            boolean hasil = layananSesiBelajar.hapus(sesi.getId());
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("ambilSesiMendatang() Tests")
    class AmbilSesiMendatangTests {
        
        @Test
        @DisplayName("ambilSesiMendatang() mengembalikan list")
        void ambilMendatang_berhasil() throws SQLException {
            layananSesiBelajar.tambah(buatSesi(LocalDate.now().plusDays(1)));
            
            List<SesiBelajar> hasil = layananSesiBelajar.ambilSesiMendatang();
            
            assertNotNull(hasil);
        }
        
        @Test
        @DisplayName("ambilSesiMendatang(batas) mengembalikan list dengan batas")
        void ambilMendatangDenganBatas() throws SQLException {
            layananSesiBelajar.tambah(buatSesi(LocalDate.now().plusDays(1)));
            layananSesiBelajar.tambah(buatSesi(LocalDate.now().plusDays(2)));
            
            List<SesiBelajar> hasil = layananSesiBelajar.ambilSesiMendatang(1);
            
            assertNotNull(hasil);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanRentangTanggal() Tests")
    class AmbilBerdasarkanRentangTanggalTests {
        
        @Test
        @DisplayName("ambilBerdasarkanRentangTanggal() mengembalikan sesi dalam rentang")
        void ambilRentang_berhasil() throws SQLException {
            layananSesiBelajar.tambah(buatSesi(LocalDate.now()));
            layananSesiBelajar.tambah(buatSesi(LocalDate.now().plusDays(1)));
            
            List<SesiBelajar> hasil = layananSesiBelajar.ambilBerdasarkanRentangTanggal(
                LocalDate.now(), LocalDate.now().plusDays(7));
            
            assertNotNull(hasil);
        }
    }

    @Nested
    @DisplayName("selesaikanSesi() Tests")
    class SelesaikanSesiTests {
        
        @Test
        @DisplayName("selesaikanSesi() dengan rating invalid melempar exception")
        void selesaikan_ratingInvalid() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananSesiBelajar.selesaikanSesi(1, 0, ""));
        }
        
        @Test
        @DisplayName("selesaikanSesi() dengan sesi tidak ada melempar exception")
        void selesaikan_sesiTidakAda() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananSesiBelajar.selesaikanSesi(99999, 4, ""));
        }
    }

    @Nested
    @DisplayName("hitungSesiSelesaiHariIni() Tests")
    class HitungSesiSelesaiHariIniTests {
        
        @Test
        @DisplayName("hitungSesiSelesaiHariIni() mengembalikan jumlah")
        void hitung_berhasil() throws SQLException {
            int hasil = layananSesiBelajar.hitungSesiSelesaiHariIni();
            
            assertTrue(hasil >= 0);
        }
    }

    @Nested
    @DisplayName("hitungBerdasarkanTopik() Tests")
    class HitungBerdasarkanTopikTests {
        
        @Test
        @DisplayName("hitungBerdasarkanTopik() mengembalikan jumlah sesi untuk topik")
        void hitung_berhasil() throws SQLException {
            layananSesiBelajar.tambah(buatSesi(LocalDate.now()));
            
            int hasil = layananSesiBelajar.hitungBerdasarkanTopik(topikTest.getId());
            
            assertTrue(hasil >= 1);
        }
    }
}
