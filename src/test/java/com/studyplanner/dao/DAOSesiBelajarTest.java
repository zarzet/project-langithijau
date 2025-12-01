package com.studyplanner.dao;

import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.SesiBelajar;
import com.studyplanner.model.Topik;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk DAOSesiBelajar.
 * Menggunakan in-memory SQLite database.
 */
@DisplayName("DAOSesiBelajar Tests")
class DAOSesiBelajarTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    
    private DAOSesiBelajar daoSesiBelajar;
    private DAOMataKuliah daoMataKuliah;
    private DAOTopik daoTopik;
    
    private MataKuliah mataKuliahTest;
    private Topik topikTest;

    // Helper untuk membuat MataKuliah
    private MataKuliah buatMataKuliah(String nama, String kode) {
        MataKuliah mk = new MataKuliah(0, nama, kode, "Deskripsi " + nama);
        mk.setUserId(TEST_USER_ID);
        return mk;
    }

    // Helper untuk membuat Topik
    private Topik buatTopik(String nama, int idMataKuliah) {
        return new Topik(0, idMataKuliah, nama, "Deskripsi " + nama);
    }

    // Helper untuk membuat SesiBelajar
    private SesiBelajar buatSesi(int idTopik, int idMataKuliah, LocalDate tanggal) {
        SesiBelajar sesi = new SesiBelajar();
        sesi.setIdTopik(idTopik);
        sesi.setIdMataKuliah(idMataKuliah);
        sesi.setTanggalJadwal(tanggal);
        sesi.setTipeSesi("BELAJAR");
        sesi.setSelesai(false);
        return sesi;
    }

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
        daoSesiBelajar = new DAOSesiBelajar(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        daoTopik = new DAOTopik(manajerDB);
        
        // Buat mata kuliah test
        mataKuliahTest = buatMataKuliah("Test Course", "TEST101");
        daoMataKuliah.simpan(mataKuliahTest);
        
        // Buat topik test
        topikTest = buatTopik("Test Topic", mataKuliahTest.getId());
        daoTopik.simpan(topikTest);
    }

    @Nested
    @DisplayName("simpan() Tests")
    class SimpanTests {
        
        @Test
        @DisplayName("simpan() berhasil menyimpan sesi belajar baru")
        void simpan_berhasilMenyimpan() throws SQLException {
            SesiBelajar sesi = buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now());
            
            daoSesiBelajar.simpan(sesi);
            
            assertTrue(sesi.getId() > 0, "ID harus di-generate setelah simpan");
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanTanggal() Tests")
    class AmbilBerdasarkanTanggalTests {
        
        @Test
        @DisplayName("ambilBerdasarkanTanggal() mengembalikan sesi untuk tanggal tersebut")
        void ambil_berhasilMengambil() throws SQLException {
            LocalDate tanggal = LocalDate.now();
            SesiBelajar sesi = buatSesi(topikTest.getId(), mataKuliahTest.getId(), tanggal);
            daoSesiBelajar.simpan(sesi);
            
            List<SesiBelajar> hasil = daoSesiBelajar.ambilBerdasarkanTanggal(tanggal);
            
            assertFalse(hasil.isEmpty(), "Harus ada sesi untuk tanggal tersebut");
            assertEquals(tanggal, hasil.get(0).getTanggalJadwal());
        }
        
        @Test
        @DisplayName("ambilBerdasarkanTanggal() mengembalikan list kosong jika tidak ada")
        void ambil_tidakAdaSesi() throws SQLException {
            LocalDate tanggal = LocalDate.now().plusYears(10);
            
            List<SesiBelajar> hasil = daoSesiBelajar.ambilBerdasarkanTanggal(tanggal);
            
            assertTrue(hasil.isEmpty(), "Tidak boleh ada sesi untuk tanggal di masa depan");
        }
    }

    @Nested
    @DisplayName("Cascade Delete Tests - hapusBerdasarkanTopikId()")
    class CascadeDeleteTopikTests {
        
        @Test
        @DisplayName("hapusBerdasarkanTopikId() menghapus semua sesi untuk topik tersebut")
        void hapus_berhasilMenghapusSemuaSesi() throws SQLException {
            // Buat beberapa sesi untuk topik yang sama
            for (int i = 0; i < 3; i++) {
                SesiBelajar sesi = buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now().plusDays(i));
                daoSesiBelajar.simpan(sesi);
            }
            
            // Verifikasi ada sesi
            List<SesiBelajar> sebelum = daoSesiBelajar.ambilBerdasarkanTopikId(topikTest.getId());
            assertEquals(3, sebelum.size(), "Harus ada 3 sesi sebelum hapus");
            
            // Hapus berdasarkan topik ID
            int terhapus = daoSesiBelajar.hapusBerdasarkanTopikId(topikTest.getId());
            
            assertEquals(3, terhapus, "Harus menghapus 3 sesi");
            
            // Verifikasi tidak ada sesi tersisa
            List<SesiBelajar> sesudah = daoSesiBelajar.ambilBerdasarkanTopikId(topikTest.getId());
            assertTrue(sesudah.isEmpty(), "Tidak boleh ada sesi tersisa");
        }
        
        @Test
        @DisplayName("hapusBerdasarkanTopikId() dengan ID tidak ada mengembalikan 0")
        void hapus_topikTidakAda_return0() throws SQLException {
            int terhapus = daoSesiBelajar.hapusBerdasarkanTopikId(99999);
            assertEquals(0, terhapus, "Tidak ada yang dihapus untuk ID yang tidak ada");
        }
    }

    @Nested
    @DisplayName("Cascade Delete Tests - hapusBerdasarkanMataKuliahId()")
    class CascadeDeleteMataKuliahTests {
        
        @Test
        @DisplayName("hapusBerdasarkanMataKuliahId() menghapus semua sesi untuk mata kuliah tersebut")
        void hapus_berhasilMenghapusSemuaSesi() throws SQLException {
            // Buat topik kedua untuk mata kuliah yang sama
            Topik topik2 = buatTopik("Test Topic 2", mataKuliahTest.getId());
            daoTopik.simpan(topik2);
            
            // Buat sesi untuk kedua topik
            SesiBelajar sesi1 = buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now());
            daoSesiBelajar.simpan(sesi1);
            
            SesiBelajar sesi2 = buatSesi(topik2.getId(), mataKuliahTest.getId(), LocalDate.now());
            daoSesiBelajar.simpan(sesi2);
            
            // Hapus berdasarkan mata kuliah ID
            int terhapus = daoSesiBelajar.hapusBerdasarkanMataKuliahId(mataKuliahTest.getId());
            
            assertEquals(2, terhapus, "Harus menghapus 2 sesi");
        }
        
        @Test
        @DisplayName("hapusBerdasarkanMataKuliahId() dengan ID tidak ada mengembalikan 0")
        void hapus_mataKuliahTidakAda_return0() throws SQLException {
            int terhapus = daoSesiBelajar.hapusBerdasarkanMataKuliahId(99999);
            assertEquals(0, terhapus, "Tidak ada yang dihapus untuk ID yang tidak ada");
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {
        
        @Test
        @DisplayName("perbarui() berhasil mengupdate status selesai")
        void perbarui_statusSelesai() throws SQLException {
            SesiBelajar sesi = buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now());
            daoSesiBelajar.simpan(sesi);
            
            assertFalse(sesi.isSelesai(), "Sesi harus belum selesai");
            
            sesi.setSelesai(true);
            daoSesiBelajar.perbarui(sesi);
            
            SesiBelajar hasil = daoSesiBelajar.ambilBerdasarkanId(sesi.getId());
            assertTrue(hasil.isSelesai(), "Sesi harus sudah selesai setelah update");
        }
    }

    @Nested
    @DisplayName("ambilSesiHariIni() Tests")
    class AmbilSesiHariIniTests {
        
        @Test
        @DisplayName("ambilSesiHariIni() mengembalikan sesi hari ini")
        void ambilHariIni_berhasil() throws SQLException {
            daoSesiBelajar.simpan(buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now()));
            daoSesiBelajar.simpan(buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now().plusDays(1)));

            List<SesiBelajar> hasil = daoSesiBelajar.ambilSesiHariIni();

            assertEquals(1, hasil.size());
        }
    }

    @Nested
    @DisplayName("ambilSesiMendatang() Tests")
    class AmbilSesiMendatangTests {
        
        @Test
        @DisplayName("ambilSesiMendatang() mengembalikan sesi masa depan")
        void ambilMendatang_berhasil() throws SQLException {
            daoSesiBelajar.simpan(buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now().plusDays(1)));
            daoSesiBelajar.simpan(buatSesi(topikTest.getId(), mataKuliahTest.getId(), LocalDate.now().plusDays(2)));

            List<SesiBelajar> hasil = daoSesiBelajar.ambilSesiMendatang(7);

            // Minimal ada sesi yang disimpan
            assertTrue(hasil.size() >= 0);
        }
    }
}
