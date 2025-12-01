package com.studyplanner.layanan;

import com.studyplanner.dao.*;
import com.studyplanner.model.JadwalUjian;
import com.studyplanner.model.MataKuliah;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk LayananJadwalUjian.
 */
@DisplayName("LayananJadwalUjian Tests")
class LayananJadwalUjianTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    
    private LayananJadwalUjian layananJadwalUjian;
    private DAOMataKuliah daoMataKuliah;
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
        layananJadwalUjian = new LayananJadwalUjian(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        
        // Setup mata kuliah test
        mataKuliahTest = new MataKuliah(0, "Test Course", "TEST", "Deskripsi");
        mataKuliahTest.setUserId(TEST_USER_ID);
        daoMataKuliah.simpan(mataKuliahTest);
    }

    private JadwalUjian buatJadwal(String judul, LocalDate tanggal) {
        JadwalUjian jadwal = new JadwalUjian();
        jadwal.setIdMataKuliah(mataKuliahTest.getId());
        jadwal.setJudul(judul);
        jadwal.setTipeUjian("UTS");
        jadwal.setTanggalUjian(tanggal);
        return jadwal;
    }

    @Nested
    @DisplayName("tambah() Tests")
    class TambahTests {
        
        @Test
        @DisplayName("tambah() berhasil menyimpan jadwal ujian baru")
        void tambah_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(7));
            
            int id = layananJadwalUjian.tambah(jadwal);
            
            assertTrue(id > 0);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanMataKuliah() Tests")
    class AmbilBerdasarkanMataKuliahTests {
        
        @Test
        @DisplayName("ambilBerdasarkanMataKuliah() mengembalikan jadwal untuk mata kuliah")
        void ambil_berhasil() throws SQLException {
            layananJadwalUjian.tambah(buatJadwal("UTS", LocalDate.now().plusDays(7)));
            layananJadwalUjian.tambah(buatJadwal("UAS", LocalDate.now().plusDays(30)));
            
            List<JadwalUjian> hasil = layananJadwalUjian.ambilBerdasarkanMataKuliah(mataKuliahTest.getId());
            
            assertEquals(2, hasil.size());
        }
    }

    @Nested
    @DisplayName("hapus() Tests")
    class HapusTests {
        
        @Test
        @DisplayName("hapus() berhasil menghapus jadwal")
        void hapus_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(7));
            layananJadwalUjian.tambah(jadwal);
            
            boolean hasil = layananJadwalUjian.hapus(jadwal.getId());
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanId() Tests")
    class AmbilBerdasarkanIdTests {
        
        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan jadwal")
        void ambil_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(7));
            layananJadwalUjian.tambah(jadwal);
            
            JadwalUjian hasil = layananJadwalUjian.ambilBerdasarkanId(jadwal.getId());
            
            assertNotNull(hasil);
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {
        
        @Test
        @DisplayName("perbarui() berhasil mengupdate jadwal")
        void perbarui_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(7));
            layananJadwalUjian.tambah(jadwal);
            
            jadwal.setJudul("UAS");
            boolean hasil = layananJadwalUjian.perbarui(jadwal);
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("ambilUjianMendatang() Tests")
    class AmbilUjianMendatangTests {
        
        @Test
        @DisplayName("ambilUjianMendatang() mengembalikan ujian masa depan")
        void ambil_berhasil() throws SQLException {
            layananJadwalUjian.tambah(buatJadwal("UTS", LocalDate.now().plusDays(7)));
            layananJadwalUjian.tambah(buatJadwal("UAS", LocalDate.now().plusDays(30)));
            
            List<JadwalUjian> hasil = layananJadwalUjian.ambilUjianMendatang();
            
            assertNotNull(hasil);
        }
    }

    @Nested
    @DisplayName("hitungHariTersisa() Tests")
    class HitungHariTersisaTests {
        
        @Test
        @DisplayName("hitungHariTersisa() menghitung dengan benar")
        void hitung_berhasil() {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(10));
            
            long hasil = layananJadwalUjian.hitungHariTersisa(jadwal);
            
            assertEquals(10, hasil);
        }
        
        @Test
        @DisplayName("hitungHariTersisa() dengan null")
        void hitung_null() {
            long hasil = layananJadwalUjian.hitungHariTersisa(null);
            assertEquals(0, hasil);
        }
    }

    @Nested
    @DisplayName("apakahMendesak() Tests")
    class ApakahMendesakTests {
        
        @Test
        @DisplayName("apakahMendesak() true jika <= 7 hari")
        void mendesak_true() {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(5));
            assertTrue(layananJadwalUjian.apakahMendesak(jadwal));
        }
        
        @Test
        @DisplayName("apakahMendesak() false jika > 7 hari")
        void mendesak_false() {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(10));
            assertFalse(layananJadwalUjian.apakahMendesak(jadwal));
        }
    }

    @Nested
    @DisplayName("apakahSangatMendesak() Tests")
    class ApakahSangatMendesakTests {
        
        @Test
        @DisplayName("apakahSangatMendesak() true jika <= 3 hari")
        void sangatMendesak_true() {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(2));
            assertTrue(layananJadwalUjian.apakahSangatMendesak(jadwal));
        }
    }

    @Nested
    @DisplayName("tandaiSelesai() Tests")
    class TandaiSelesaiTests {
        
        @Test
        @DisplayName("tandaiSelesai() berhasil")
        void tandai_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwal("UTS", LocalDate.now().plusDays(1));
            layananJadwalUjian.tambah(jadwal);
            
            boolean hasil = layananJadwalUjian.tandaiSelesai(jadwal.getId());
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("Validasi Tests")
    class ValidasiTests {
        
        @Test
        @DisplayName("tambah() dengan null melempar exception")
        void tambah_nullException() {
            assertThrows(IllegalArgumentException.class, 
                () -> layananJadwalUjian.tambah(null));
        }
        
        @Test
        @DisplayName("tambah() dengan judul kosong melempar exception")
        void tambah_judulKosongException() {
            JadwalUjian jadwal = new JadwalUjian();
            jadwal.setIdMataKuliah(mataKuliahTest.getId());
            jadwal.setJudul("");
            jadwal.setTanggalUjian(LocalDate.now().plusDays(7));
            
            assertThrows(IllegalArgumentException.class, 
                () -> layananJadwalUjian.tambah(jadwal));
        }
    }
}
