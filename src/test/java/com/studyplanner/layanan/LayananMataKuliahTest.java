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
 * Integration test untuk LayananMataKuliah.
 * Menggunakan in-memory SQLite database.
 */
@DisplayName("LayananMataKuliah Tests")
class LayananMataKuliahTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    
    private LayananMataKuliah layananMataKuliah;
    private DAOTopik daoTopik;
    private DAOSesiBelajar daoSesiBelajar;

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
        layananMataKuliah = new LayananMataKuliah(manajerDB);
        daoTopik = new DAOTopik(manajerDB);
        daoSesiBelajar = new DAOSesiBelajar(manajerDB);
    }

    // Helper methods
    private MataKuliah buatMataKuliah(String nama, String kode) {
        MataKuliah mk = new MataKuliah(0, nama, kode, "Deskripsi " + nama);
        mk.setUserId(TEST_USER_ID);
        return mk;
    }

    private Topik buatTopik(String nama, int idMataKuliah) {
        return new Topik(0, idMataKuliah, nama, "Deskripsi " + nama);
    }

    private SesiBelajar buatSesi(int idTopik, int idMataKuliah, LocalDate tanggal) {
        SesiBelajar sesi = new SesiBelajar();
        sesi.setIdTopik(idTopik);
        sesi.setIdMataKuliah(idMataKuliah);
        sesi.setTanggalJadwal(tanggal);
        sesi.setTipeSesi("BELAJAR");
        return sesi;
    }

    @Nested
    @DisplayName("simpan() Tests")
    class SimpanTests {
        
        @Test
        @DisplayName("simpan() berhasil menyimpan mata kuliah baru")
        void simpan_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Struktur Data", "IF2040");
            
            layananMataKuliah.daftarkan(mk);
            
            assertTrue(mk.getId() > 0);
        }
    }

    @Nested
    @DisplayName("ambilSemuaByUserId() Tests")
    class AmbilSemuaTests {
        
        @Test
        @DisplayName("ambilSemuaByUserId() mengembalikan daftar mata kuliah untuk user")
        void ambilSemua_berhasil() throws SQLException {
            layananMataKuliah.daftarkan(buatMataKuliah("Mata Kuliah 1", "MK1"));
            layananMataKuliah.daftarkan(buatMataKuliah("Mata Kuliah 2", "MK2"));
            
            List<MataKuliah> hasil = layananMataKuliah.ambilSemuaByUserId(TEST_USER_ID);
            
            assertEquals(2, hasil.size());
        }
    }

    @Nested
    @DisplayName("hapus() Tests - Cascade Delete")
    class HapusCascadeTests {
        
        @Test
        @DisplayName("hapus() juga menghapus semua sesi belajar terkait")
        void hapus_cascadeDeleteSesiBelajar() throws SQLException {
            // Setup: Mata Kuliah -> Topik -> Sesi Belajar
            MataKuliah mk = buatMataKuliah("Test Course", "TEST");
            layananMataKuliah.daftarkan(mk);
            
            Topik topik = buatTopik("Test Topic", mk.getId());
            daoTopik.simpan(topik);
            
            SesiBelajar sesi = buatSesi(topik.getId(), mk.getId(), LocalDate.now());
            daoSesiBelajar.simpan(sesi);
            
            // Verifikasi sesi ada
            List<SesiBelajar> sebelum = daoSesiBelajar.ambilBerdasarkanTopikId(topik.getId());
            assertEquals(1, sebelum.size(), "Harus ada 1 sesi sebelum hapus");
            
            // Hapus mata kuliah
            layananMataKuliah.hapus(mk.getId());
            
            // Verifikasi sesi sudah terhapus (cascade)
            List<SesiBelajar> sesudah = daoSesiBelajar.ambilBerdasarkanTopikId(topik.getId());
            assertTrue(sesudah.isEmpty(), "Sesi harus terhapus setelah hapus mata kuliah");
        }
        
        @Test
        @DisplayName("hapus() dengan multiple topik dan sesi menghapus semua")
        void hapus_cascadeDeleteMultiple() throws SQLException {
            // Setup: Mata Kuliah -> 2 Topik -> 4 Sesi Belajar
            MataKuliah mk = buatMataKuliah("Test Course", "TEST");
            layananMataKuliah.daftarkan(mk);
            
            Topik topik1 = buatTopik("Topic 1", mk.getId());
            Topik topik2 = buatTopik("Topic 2", mk.getId());
            daoTopik.simpan(topik1);
            daoTopik.simpan(topik2);
            
            // 2 sesi per topik
            daoSesiBelajar.simpan(buatSesi(topik1.getId(), mk.getId(), LocalDate.now()));
            daoSesiBelajar.simpan(buatSesi(topik1.getId(), mk.getId(), LocalDate.now().plusDays(1)));
            daoSesiBelajar.simpan(buatSesi(topik2.getId(), mk.getId(), LocalDate.now()));
            daoSesiBelajar.simpan(buatSesi(topik2.getId(), mk.getId(), LocalDate.now().plusDays(1)));
            
            // Hapus mata kuliah
            layananMataKuliah.hapus(mk.getId());
            
            // Verifikasi semua sesi terhapus
            List<SesiBelajar> sesiTopik1 = daoSesiBelajar.ambilBerdasarkanTopikId(topik1.getId());
            List<SesiBelajar> sesiTopik2 = daoSesiBelajar.ambilBerdasarkanTopikId(topik2.getId());
            
            assertTrue(sesiTopik1.isEmpty(), "Sesi topik 1 harus terhapus");
            assertTrue(sesiTopik2.isEmpty(), "Sesi topik 2 harus terhapus");
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {
        
        @Test
        @DisplayName("perbarui() berhasil mengupdate data")
        void perbarui_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Nama Lama", "OLD001");
            layananMataKuliah.daftarkan(mk);
            
            mk.setNama("Nama Baru");
            boolean hasil = layananMataKuliah.perbarui(mk);
            
            assertTrue(hasil);
        }
        
        @Test
        @DisplayName("perbarui() dengan kode duplikat melempar exception")
        void perbarui_kodeDuplikat() throws SQLException {
            MataKuliah mk1 = buatMataKuliah("Course 1", "CODE1");
            MataKuliah mk2 = buatMataKuliah("Course 2", "CODE2");
            layananMataKuliah.daftarkan(mk1);
            layananMataKuliah.daftarkan(mk2);
            
            // Coba ubah kode mk2 ke kode mk1
            mk2.setKode("CODE1");
            
            assertThrows(IllegalStateException.class, () -> layananMataKuliah.perbarui(mk2));
        }
    }

    @Nested
    @DisplayName("ambilSemuaByUserId() Tests")
    class AmbilSemuaByUserIdTests {
        
        @Test
        @DisplayName("ambilSemuaByUserId() mengembalikan mata kuliah user")
        void ambilSemua_berhasil() throws SQLException {
            layananMataKuliah.daftarkan(buatMataKuliah("Course 1", "C1"));
            layananMataKuliah.daftarkan(buatMataKuliah("Course 2", "C2"));
            
            List<MataKuliah> hasil = layananMataKuliah.ambilSemuaByUserId(TEST_USER_ID);
            
            assertEquals(2, hasil.size());
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanId() Tests")
    class AmbilBerdasarkanIdTests {
        
        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan mata kuliah")
        void ambil_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Test", "TEST");
            layananMataKuliah.daftarkan(mk);
            
            MataKuliah hasil = layananMataKuliah.ambilBerdasarkanId(mk.getId());
            
            assertNotNull(hasil);
            assertEquals("Test", hasil.getNama());
        }
        
        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan null jika tidak ada")
        void ambil_tidakAda() throws SQLException {
            MataKuliah hasil = layananMataKuliah.ambilBerdasarkanId(99999);
            
            assertNull(hasil);
        }
    }

    @Nested
    @DisplayName("hitungTotal() Tests")
    class HitungTotalTests {
        
        @Test
        @DisplayName("hitungTotal() mengembalikan jumlah yang benar")
        void hitung_berhasil() throws SQLException {
            layananMataKuliah.daftarkan(buatMataKuliah("Course 1", "C1"));
            layananMataKuliah.daftarkan(buatMataKuliah("Course 2", "C2"));
            layananMataKuliah.daftarkan(buatMataKuliah("Course 3", "C3"));
            
            int total = layananMataKuliah.hitungTotal();
            
            assertEquals(3, total);
        }
    }

    @Nested
    @DisplayName("Validasi Tests")
    class ValidasiTests {
        
        @Test
        @DisplayName("daftarkan() dengan nama null melempar exception")
        void daftarkan_namaNullException() {
            MataKuliah mk = new MataKuliah(0, null, "CODE", "Desc");
            mk.setUserId(TEST_USER_ID);
            
            assertThrows(IllegalArgumentException.class, 
                () -> layananMataKuliah.daftarkan(mk));
        }
        
        @Test
        @DisplayName("daftarkan() dengan nama kosong melempar exception")
        void daftarkan_namaKosongException() {
            MataKuliah mk = new MataKuliah(0, "", "CODE", "Desc");
            mk.setUserId(TEST_USER_ID);
            
            assertThrows(IllegalArgumentException.class, 
                () -> layananMataKuliah.daftarkan(mk));
        }
        
        @Test
        @DisplayName("daftarkan() dengan kode duplikat melempar exception")
        void daftarkan_kodeDuplikatException() throws SQLException {
            layananMataKuliah.daftarkan(buatMataKuliah("Course 1", "DUPCODE"));
            
            MataKuliah mk2 = buatMataKuliah("Course 2", "DUPCODE");
            
            assertThrows(IllegalStateException.class, 
                () -> layananMataKuliah.daftarkan(mk2));
        }
    }

}
