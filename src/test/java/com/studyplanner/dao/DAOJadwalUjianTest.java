package com.studyplanner.dao;

import com.studyplanner.model.JadwalUjian;
import com.studyplanner.model.MataKuliah;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk DAOJadwalUjian.
 */
@DisplayName("DAOJadwalUjian Tests")
class DAOJadwalUjianTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    
    private DAOJadwalUjian daoJadwalUjian;
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
        daoJadwalUjian = new DAOJadwalUjian(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        
        // Setup mata kuliah test
        mataKuliahTest = new MataKuliah(0, "Test Course", "TEST", "Deskripsi");
        mataKuliahTest.setUserId(TEST_USER_ID);
        daoMataKuliah.simpan(mataKuliahTest);
    }

    private JadwalUjian buatJadwalUjian(String nama, LocalDate tanggal) {
        JadwalUjian jadwal = new JadwalUjian();
        jadwal.setIdMataKuliah(mataKuliahTest.getId());
        jadwal.setJudul(nama);
        jadwal.setTipeUjian("UTS");
        jadwal.setTanggalUjian(tanggal);
        jadwal.setCatatan("Catatan " + nama);
        return jadwal;
    }

    @Nested
    @DisplayName("simpan() Tests")
    class SimpanTests {
        
        @Test
        @DisplayName("simpan() berhasil menyimpan jadwal ujian baru")
        void simpan_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwalUjian("UTS", LocalDate.now().plusDays(7));
            
            daoJadwalUjian.simpan(jadwal);
            
            assertTrue(jadwal.getId() > 0);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanMataKuliahId() Tests")
    class AmbilBerdasarkanMataKuliahTests {
        
        @Test
        @DisplayName("ambilBerdasarkanMataKuliahId() mengembalikan jadwal untuk mata kuliah")
        void ambil_berhasil() throws SQLException {
            daoJadwalUjian.simpan(buatJadwalUjian("UTS", LocalDate.now().plusDays(7)));
            daoJadwalUjian.simpan(buatJadwalUjian("UAS", LocalDate.now().plusDays(30)));
            
            List<JadwalUjian> hasil = daoJadwalUjian.ambilBerdasarkanMataKuliahId(mataKuliahTest.getId());
            
            assertEquals(2, hasil.size());
        }
        
        @Test
        @DisplayName("ambilBerdasarkanMataKuliahId() mengembalikan list kosong jika tidak ada")
        void ambil_tidakAda() throws SQLException {
            List<JadwalUjian> hasil = daoJadwalUjian.ambilBerdasarkanMataKuliahId(99999);
            
            assertTrue(hasil.isEmpty());
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {
        
        @Test
        @DisplayName("perbarui() berhasil mengupdate jadwal ujian")
        void perbarui_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwalUjian("UTS", LocalDate.now().plusDays(7));
            daoJadwalUjian.simpan(jadwal);
            
            jadwal.setJudul("UTS Revisi");
            boolean hasil = daoJadwalUjian.perbarui(jadwal);
            
            assertTrue(hasil);
        }
    }

    @Nested
    @DisplayName("hapus() Tests")
    class HapusTests {
        
        @Test
        @DisplayName("hapus() berhasil menghapus jadwal ujian")
        void hapus_berhasil() throws SQLException {
            JadwalUjian jadwal = buatJadwalUjian("UTS", LocalDate.now().plusDays(7));
            daoJadwalUjian.simpan(jadwal);
            
            boolean hasil = daoJadwalUjian.hapus(jadwal.getId());
            
            assertTrue(hasil);
            
            // Verifikasi sudah terhapus
            List<JadwalUjian> sisa = daoJadwalUjian.ambilBerdasarkanMataKuliahId(mataKuliahTest.getId());
            assertTrue(sisa.isEmpty());
        }
    }
}
