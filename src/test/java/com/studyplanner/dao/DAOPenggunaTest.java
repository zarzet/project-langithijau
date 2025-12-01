package com.studyplanner.dao;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test untuk DAOPengguna.
 */
@DisplayName("DAOPengguna Tests")
class DAOPenggunaTest {

    private static TestDatabaseHelper dbHelper;
    private static ManajerBasisDataTest manajerDB;
    private DAOPengguna daoPengguna;

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
        daoPengguna = new DAOPengguna(manajerDB);
    }

    @Nested
    @DisplayName("simpanPenggunaLokal() Tests")
    class SimpanPenggunaLokalTests {
        
        @Test
        @DisplayName("simpanPenggunaLokal() berhasil menyimpan pengguna baru")
        void simpan_berhasil() throws SQLException {
            int id = daoPengguna.simpanPenggunaLokal("testuser", "password123", "test@test.com", "Test User");
            
            assertTrue(id > 0);
        }
        
        @Test
        @DisplayName("simpanPenggunaLokal() dengan username duplikat gagal")
        void simpan_usernameDuplikat() throws SQLException {
            daoPengguna.simpanPenggunaLokal("testuser", "password1", "test1@test.com", "User 1");
            
            assertThrows(SQLException.class, () -> 
                daoPengguna.simpanPenggunaLokal("testuser", "password2", "test2@test.com", "User 2"));
        }
    }

    @Nested
    @DisplayName("simpanPenggunaGoogle() Tests")
    class SimpanPenggunaGoogleTests {
        
        @Test
        @DisplayName("simpanPenggunaGoogle() berhasil menyimpan pengguna Google")
        void simpan_berhasil() throws SQLException {
            int id = daoPengguna.simpanPenggunaGoogle("google123", "test@gmail.com", "Test User", "http://photo.url");
            
            assertTrue(id > 0);
        }
    }

    @Nested
    @DisplayName("cariBerdasarkanUsername() Tests")
    class CariBerdasarkanUsernameTests {
        
        @Test
        @DisplayName("cariBerdasarkanUsername() menemukan pengguna yang ada")
        void cari_ditemukan() throws SQLException {
            daoPengguna.simpanPenggunaLokal("testuser", "password123", "test@test.com", "Test User");
            
            Map<String, Object> hasil = daoPengguna.cariBerdasarkanUsername("testuser");
            
            assertNotNull(hasil);
            assertEquals("testuser", hasil.get("username"));
        }
        
        @Test
        @DisplayName("cariBerdasarkanUsername() mengembalikan null jika tidak ada")
        void cari_tidakDitemukan() throws SQLException {
            Map<String, Object> hasil = daoPengguna.cariBerdasarkanUsername("nonexistent");
            
            assertNull(hasil);
        }
    }

    @Nested
    @DisplayName("cariBerdasarkanGoogleId() Tests")
    class CariBerdasarkanGoogleIdTests {
        
        @Test
        @DisplayName("cariBerdasarkanGoogleId() menemukan pengguna yang ada")
        void cari_ditemukan() throws SQLException {
            daoPengguna.simpanPenggunaGoogle("google123", "test@gmail.com", "Test User", "http://photo.url");
            
            Map<String, Object> hasil = daoPengguna.cariBerdasarkanGoogleId("google123");
            
            assertNotNull(hasil);
            assertEquals("google123", hasil.get("google_id"));
        }
    }

    @Nested
    @DisplayName("ambilBerdasarkanId() Tests")
    class AmbilBerdasarkanIdTests {
        
        @Test
        @DisplayName("ambilBerdasarkanId() menemukan pengguna yang ada")
        void ambil_ditemukan() throws SQLException {
            int id = daoPengguna.simpanPenggunaLokal("testuser", "password123", "test@test.com", "Test User");
            
            Map<String, Object> hasil = daoPengguna.ambilBerdasarkanId(id);
            
            assertNotNull(hasil);
            assertEquals(id, hasil.get("id"));
        }
    }
}
