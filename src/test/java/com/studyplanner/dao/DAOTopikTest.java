package com.studyplanner.dao;

import com.studyplanner.model.MataKuliah;
import com.studyplanner.model.Topik;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk DAOTopik.
 * Menggunakan in-memory SQLite database.
 */
@DisplayName("DAOTopik Tests")
class DAOTopikTest {

    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    private DAOTopik daoTopik;
    private DAOMataKuliah daoMataKuliah;
    private int mataKuliahId;

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
        daoTopik = new DAOTopik(manajerDB);
        daoMataKuliah = new DAOMataKuliah(manajerDB);
        
        // Buat mata kuliah untuk foreign key
        MataKuliah mk = new MataKuliah(0, "Struktur Data", "IF2040", "Test");
        mataKuliahId = daoMataKuliah.simpan(mk);
    }

    @Nested
    @DisplayName("simpan() Tests")
    class SimpanTests {

        @Test
        @DisplayName("simpan() berhasil menyimpan topik baru")
        void simpan_berhasilMenyimpan() throws SQLException {
            Topik topik = buatTopikBaru("Array", "Struktur data array");

            Integer id = daoTopik.simpan(topik);

            assertNotNull(id);
            assertTrue(id > 0);
            assertEquals(id, topik.getId());
        }

        @Test
        @DisplayName("simpan() menyimpan parameter FSRS dengan benar")
        void simpan_parameterFsrs() throws SQLException {
            Topik topik = buatTopikBaru("Linked List", "Struktur data linked list");
            topik.setStabilitasFsrs(10.5);
            topik.setKesulitanFsrs(5.0);
            topik.setRetensiDiinginkan(0.85);

            Integer id = daoTopik.simpan(topik);
            Topik hasil = daoTopik.ambilBerdasarkanId(id);

            assertEquals(10.5, hasil.getStabilitasFsrs(), 0.001);
            assertEquals(5.0, hasil.getKesulitanFsrs(), 0.001);
            assertEquals(0.85, hasil.getRetensiDiinginkan(), 0.001);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanId() Tests")
    class AmbilBerdasarkanIdTests {

        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan topik yang benar")
        void ambilBerdasarkanId_berhasil() throws SQLException {
            Topik topik = buatTopikBaru("Stack", "LIFO structure");
            topik.setPrioritas(5);
            topik.setTingkatKesulitan(4);
            Integer id = daoTopik.simpan(topik);

            Topik hasil = daoTopik.ambilBerdasarkanId(id);

            assertNotNull(hasil);
            assertEquals("Stack", hasil.getNama());
            assertEquals("LIFO structure", hasil.getDeskripsi());
            assertEquals(5, hasil.getPrioritas());
            assertEquals(4, hasil.getTingkatKesulitan());
        }

        @Test
        @DisplayName("ambilBerdasarkanId() mengembalikan null jika tidak ditemukan")
        void ambilBerdasarkanId_tidakDitemukan() throws SQLException {
            Topik hasil = daoTopik.ambilBerdasarkanId(999);
            assertNull(hasil);
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanMataKuliahId() Tests")
    class AmbilBerdasarkanMataKuliahIdTests {

        @Test
        @DisplayName("ambilBerdasarkanMataKuliahId() mengembalikan semua topik")
        void ambilBerdasarkanMataKuliahId_berhasil() throws SQLException {
            daoTopik.simpan(buatTopikBaru("Array", ""));
            daoTopik.simpan(buatTopikBaru("Linked List", ""));
            daoTopik.simpan(buatTopikBaru("Stack", ""));

            List<Topik> hasil = daoTopik.ambilBerdasarkanMataKuliahId(mataKuliahId);

            assertEquals(3, hasil.size());
        }

        @Test
        @DisplayName("ambilBerdasarkanMataKuliahId() kosong untuk MK tanpa topik")
        void ambilBerdasarkanMataKuliahId_kosong() throws SQLException {
            MataKuliah mkBaru = new MataKuliah(0, "MK Baru", "XX0000", "");
            int mkBaruId = daoMataKuliah.simpan(mkBaru);

            List<Topik> hasil = daoTopik.ambilBerdasarkanMataKuliahId(mkBaruId);

            assertTrue(hasil.isEmpty());
        }
    }

    @Nested
    @DisplayName("ambilSemua() Tests")
    class AmbilSemuaTests {

        @Test
        @DisplayName("ambilSemua() mengembalikan semua topik dari semua MK")
        void ambilSemua_berhasil() throws SQLException {
            // Topik dari MK pertama
            daoTopik.simpan(buatTopikBaru("Array", ""));
            daoTopik.simpan(buatTopikBaru("Stack", ""));

            // Topik dari MK kedua
            MataKuliah mk2 = new MataKuliah(0, "Algoritma", "IF2010", "");
            int mk2Id = daoMataKuliah.simpan(mk2);
            Topik topikMk2 = new Topik(0, mk2Id, "Sorting", "");
            daoTopik.simpan(topikMk2);

            List<Topik> hasil = daoTopik.ambilSemua();

            assertEquals(3, hasil.size());
        }
    }

    @Nested
    @DisplayName("perbarui() Tests")
    class PerbaruiTests {

        @Test
        @DisplayName("perbarui() berhasil memperbarui data")
        void perbarui_berhasil() throws SQLException {
            Topik topik = buatTopikBaru("Array", "Deskripsi lama");
            Integer id = daoTopik.simpan(topik);

            topik.setNama("Array Dinamis");
            topik.setDeskripsi("Deskripsi baru");
            topik.setPrioritas(5);
            boolean hasil = daoTopik.perbarui(topik);

            assertTrue(hasil);

            Topik updated = daoTopik.ambilBerdasarkanId(id);
            assertEquals("Array Dinamis", updated.getNama());
            assertEquals("Deskripsi baru", updated.getDeskripsi());
            assertEquals(5, updated.getPrioritas());
        }
    }

    @Nested
    @DisplayName("hapus() Tests")
    class HapusTests {

        @Test
        @DisplayName("hapus() berhasil menghapus topik")
        void hapus_berhasil() throws SQLException {
            Topik topik = buatTopikBaru("Untuk Dihapus", "");
            Integer id = daoTopik.simpan(topik);

            boolean hasil = daoTopik.hapus(id);

            assertTrue(hasil);
            assertNull(daoTopik.ambilBerdasarkanId(id));
        }
    }

    @Nested
    @DisplayName("hitungTotal() Tests")
    class HitungTotalTests {

        @Test
        @DisplayName("hitungTotal() menghitung dengan benar")
        void hitungTotal_berhasil() throws SQLException {
            daoTopik.simpan(buatTopikBaru("Topik 1", ""));
            daoTopik.simpan(buatTopikBaru("Topik 2", ""));
            daoTopik.simpan(buatTopikBaru("Topik 3", ""));

            assertEquals(3, daoTopik.hitungTotal());
        }
    }

    // Helper method
    private Topik buatTopikBaru(String nama, String deskripsi) {
        Topik topik = new Topik();
        topik.setIdMataKuliah(mataKuliahId);
        topik.setNama(nama);
        topik.setDeskripsi(deskripsi);
        return topik;
    }
}
