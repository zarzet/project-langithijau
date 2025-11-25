package com.studyplanner.dao;

import com.studyplanner.model.MataKuliah;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk DAOMataKuliah.
 * Menggunakan in-memory SQLite database.
 */
@DisplayName("DAOMataKuliah Tests")
class DAOMataKuliahTest {

    private static final int TEST_USER_ID = 1;
    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    private DAOMataKuliah daoMataKuliah;

    // Helper untuk membuat MataKuliah dengan userId
    private MataKuliah buatMataKuliah(String nama, String kode, String deskripsi) {
        MataKuliah mk = new MataKuliah(0, nama, kode, deskripsi);
        mk.setUserId(TEST_USER_ID);
        return mk;
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
        daoMataKuliah = new DAOMataKuliah(manajerDB);
    }

    @Nested
    @DisplayName("simpan() Tests")
    class SimpanTests {

        @Test
        @DisplayName("simpan() berhasil menyimpan mata kuliah baru")
        void simpan_berhasilMenyimpan() throws SQLException {
            MataKuliah mk = buatMataKuliah("Struktur Data", "IF2040", "Mempelajari struktur data");

            Integer id = daoMataKuliah.simpan(mk);

            assertNotNull(id);
            assertTrue(id > 0);
            assertEquals(id, mk.getId());
        }

        @Test
        @DisplayName("simpan() dengan kode duplikat untuk user yang sama melempar exception")
        void simpan_kodeDuplikat_melemparException() throws SQLException {
            MataKuliah mk1 = buatMataKuliah("Struktur Data", "IF2040", "Deskripsi 1");
            MataKuliah mk2 = buatMataKuliah("Algoritma", "IF2040", "Deskripsi 2");

            daoMataKuliah.simpan(mk1);

            assertThrows(SQLException.class, () -> daoMataKuliah.simpan(mk2));
        }

        @Test
        @DisplayName("simpan() mengisi ID pada objek mata kuliah")
        void simpan_mengisiIdPadaObjek() throws SQLException {
            MataKuliah mk = buatMataKuliah("Basis Data", "IF3040", "Database");
            assertEquals(0, mk.getId());

            daoMataKuliah.simpan(mk);

            assertTrue(mk.getId() > 0);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanId() Tests")
    class AmbilBerdasarkanIdTests {

        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan mata kuliah yang benar")
        void ambilBerdasarkanId_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Pemrograman Web", "IF3110", "Web dev");
            Integer id = daoMataKuliah.simpan(mk);

            MataKuliah hasil = daoMataKuliah.ambilBerdasarkanId(id);

            assertNotNull(hasil);
            assertEquals(id, hasil.getId());
            assertEquals("Pemrograman Web", hasil.getNama());
            assertEquals("IF3110", hasil.getKode());
            assertEquals("Web dev", hasil.getDeskripsi());
        }

        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan null jika tidak ditemukan")
        void ambilBerdasarkanId_tidakDitemukan() throws SQLException {
            MataKuliah hasil = daoMataKuliah.ambilBerdasarkanId(999);
            assertNull(hasil);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanKode() Tests")
    class AmbilBerdasarkanKodeTests {

        @Test
        @DisplayName("ambilBerdasarkanKode() mengembalikan mata kuliah yang benar")
        void ambilBerdasarkanKode_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Kecerdasan Buatan", "IF3170", "AI");
            daoMataKuliah.simpan(mk);

            MataKuliah hasil = daoMataKuliah.ambilBerdasarkanKode(TEST_USER_ID, "IF3170");

            assertNotNull(hasil);
            assertEquals("Kecerdasan Buatan", hasil.getNama());
        }

        @Test
        @DisplayName("ambilBerdasarkanKode() mengembalikan null jika tidak ditemukan")
        void ambilBerdasarkanKode_tidakDitemukan() throws SQLException {
            MataKuliah hasil = daoMataKuliah.ambilBerdasarkanKode(TEST_USER_ID, "XX9999");
            assertNull(hasil);
        }
    }

    @Nested
    @DisplayName("ambilSemua() Tests")
    class AmbilSemuaTests {

        @Test
        @DisplayName("ambilSemua() mengembalikan list kosong jika tidak ada data")
        void ambilSemua_kosong() throws SQLException {
            List<MataKuliah> hasil = daoMataKuliah.ambilSemua();

            assertNotNull(hasil);
            assertTrue(hasil.isEmpty());
        }

        @Test
        @DisplayName("ambilSemua() mengembalikan semua mata kuliah")
        void ambilSemua_berhasil() throws SQLException {
            daoMataKuliah.simpan(buatMataKuliah("Struktur Data", "IF2040", "SD"));
            daoMataKuliah.simpan(buatMataKuliah("Basis Data", "IF3040", "BD"));
            daoMataKuliah.simpan(buatMataKuliah("Algoritma", "IF2010", "Algo"));

            List<MataKuliah> hasil = daoMataKuliah.ambilSemua();

            assertEquals(3, hasil.size());
        }

        @Test
        @DisplayName("ambilSemua() mengurutkan berdasarkan nama")
        void ambilSemua_terurut() throws SQLException {
            daoMataKuliah.simpan(buatMataKuliah("Struktur Data", "IF2040", "SD"));
            daoMataKuliah.simpan(buatMataKuliah("Algoritma", "IF2010", "Algo"));
            daoMataKuliah.simpan(buatMataKuliah("Basis Data", "IF3040", "BD"));

            List<MataKuliah> hasil = daoMataKuliah.ambilSemua();

            assertEquals("Algoritma", hasil.get(0).getNama());
            assertEquals("Basis Data", hasil.get(1).getNama());
            assertEquals("Struktur Data", hasil.get(2).getNama());
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {

        @Test
        @DisplayName("perbarui() berhasil memperbarui data")
        void perbarui_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Struktur Data", "IF2040", "Deskripsi lama");
            Integer id = daoMataKuliah.simpan(mk);

            mk.setNama("Struktur Data Lanjut");
            mk.setDeskripsi("Deskripsi baru");
            boolean hasil = daoMataKuliah.perbarui(mk);

            assertTrue(hasil);

            MataKuliah mkUpdated = daoMataKuliah.ambilBerdasarkanId(id);
            assertEquals("Struktur Data Lanjut", mkUpdated.getNama());
            assertEquals("Deskripsi baru", mkUpdated.getDeskripsi());
        }

        @Test
        @DisplayName("perbarui() mengembalikan false jika ID tidak ditemukan")
        void perbarui_idTidakDitemukan() throws SQLException {
            MataKuliah mk = new MataKuliah(999, "Test", "XX0000", "Test");
            boolean hasil = daoMataKuliah.perbarui(mk);
            assertFalse(hasil);
        }
    }

    @Nested
    @DisplayName("hapus() Tests")
    class HapusTests {

        @Test
        @DisplayName("hapus() berhasil menghapus data")
        void hapus_berhasil() throws SQLException {
            MataKuliah mk = buatMataKuliah("Untuk Dihapus", "XX1111", "Test");
            Integer id = daoMataKuliah.simpan(mk);

            boolean hasil = daoMataKuliah.hapus(id);

            assertTrue(hasil);
            assertNull(daoMataKuliah.ambilBerdasarkanId(id));
        }

        @Test
        @DisplayName("hapus() mengembalikan false jika ID tidak ditemukan")
        void hapus_idTidakDitemukan() throws SQLException {
            boolean hasil = daoMataKuliah.hapus(999);
            assertFalse(hasil);
        }
    }

    @Nested
    @DisplayName("hitungTotal() Tests")
    class HitungTotalTests {

        @Test
        @DisplayName("hitungTotal() mengembalikan 0 jika tidak ada data")
        void hitungTotal_kosong() throws SQLException {
            assertEquals(0, daoMataKuliah.hitungTotal());
        }

        @Test
        @DisplayName("hitungTotal() mengembalikan jumlah yang benar")
        void hitungTotal_berhasil() throws SQLException {
            daoMataKuliah.simpan(buatMataKuliah("MK 1", "MK001", ""));
            daoMataKuliah.simpan(buatMataKuliah("MK 2", "MK002", ""));

            assertEquals(2, daoMataKuliah.hitungTotal());
        }
    }

    @Nested
    @DisplayName("cariBerdasarkanNama() Tests")
    class CariBerdasarkanNamaTests {

        @Test
        @DisplayName("cariBerdasarkanNama() menemukan dengan partial match")
        void cariBerdasarkanNama_partialMatch() throws SQLException {
            daoMataKuliah.simpan(buatMataKuliah("Struktur Data", "IF2040", ""));
            daoMataKuliah.simpan(buatMataKuliah("Basis Data", "IF3040", ""));
            daoMataKuliah.simpan(buatMataKuliah("Algoritma", "IF2010", ""));

            List<MataKuliah> hasil = daoMataKuliah.cariBerdasarkanNama(TEST_USER_ID, "Data");

            assertEquals(2, hasil.size());
        }

        @Test
        @DisplayName("cariBerdasarkanNama() case insensitive")
        void cariBerdasarkanNama_caseInsensitive() throws SQLException {
            daoMataKuliah.simpan(buatMataKuliah("Struktur Data", "IF2040", ""));

            List<MataKuliah> hasil = daoMataKuliah.cariBerdasarkanNama(TEST_USER_ID, "data");

            // SQLite LIKE is case-insensitive for ASCII by default
            assertEquals(1, hasil.size());
        }

        @Test
        @DisplayName("cariBerdasarkanNama() mengembalikan kosong jika tidak cocok")
        void cariBerdasarkanNama_tidakCocok() throws SQLException {
            daoMataKuliah.simpan(buatMataKuliah("Struktur Data", "IF2040", ""));

            List<MataKuliah> hasil = daoMataKuliah.cariBerdasarkanNama(TEST_USER_ID, "Jaringan");

            assertTrue(hasil.isEmpty());
        }
    }
}
