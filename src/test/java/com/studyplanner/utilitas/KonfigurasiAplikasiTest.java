package com.studyplanner.utilitas;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk KonfigurasiAplikasi.
 * Memverifikasi singleton pattern dan pembacaan konfigurasi dari config.json.
 */
@DisplayName("KonfigurasiAplikasi Tests")
class KonfigurasiAplikasiTest {

    private KonfigurasiAplikasi config;

    @BeforeEach
    void setUp() {
        config = KonfigurasiAplikasi.getInstance();
    }

    @Nested
    @DisplayName("Singleton Pattern Tests")
    class SingletonPatternTests {

        @Test
        @DisplayName("getInstance() mengembalikan instance yang sama")
        void getInstance_mengembalikanInstanceYangSama() {
            KonfigurasiAplikasi instance1 = KonfigurasiAplikasi.getInstance();
            KonfigurasiAplikasi instance2 = KonfigurasiAplikasi.getInstance();

            assertSame(instance1, instance2, "Singleton harus mengembalikan instance yang sama");
        }

        @Test
        @DisplayName("getInstance() tidak pernah mengembalikan null")
        void getInstance_tidakNull() {
            assertNotNull(KonfigurasiAplikasi.getInstance());
        }

        @RepeatedTest(5)
        @DisplayName("getInstance() konsisten dalam multiple calls")
        void getInstance_konsistenDalamMultipleCalls() {
            assertSame(config, KonfigurasiAplikasi.getInstance());
        }
    }

    @Nested
    @DisplayName("App Version Tests")
    class AppVersionTests {

        @Test
        @DisplayName("getAppVersion() tidak null dan tidak kosong")
        void getAppVersion_tidakNullDanTidakKosong() {
            String version = config.getAppVersion();

            assertNotNull(version, "Version tidak boleh null");
            assertFalse(version.isBlank(), "Version tidak boleh kosong");
        }

        @Test
        @DisplayName("getAppVersion() mengikuti format semantic versioning")
        void getAppVersion_formatSemanticVersioning() {
            String version = config.getAppVersion();

            // Format: X.Y.Z atau X.Y.Z-suffix
            assertTrue(version.matches("\\d+\\.\\d+\\.\\d+(-.*)?|\\d+\\.\\d+"),
                    "Version harus mengikuti format semantic versioning: " + version);
        }
    }

    @Nested
    @DisplayName("App Language Tests")
    class AppLanguageTests {

        @Test
        @DisplayName("getAppLanguage() tidak null")
        void getAppLanguage_tidakNull() {
            String language = config.getAppLanguage();

            assertNotNull(language, "Language tidak boleh null");
        }

        @Test
        @DisplayName("getAppLanguage() menggunakan kode bahasa valid")
        void getAppLanguage_kodeValid() {
            String language = config.getAppLanguage();

            // Kode bahasa standar: 2 huruf lowercase (ISO 639-1)
            assertTrue(language.matches("[a-z]{2}(-[A-Z]{2})?"),
                    "Language harus berupa kode ISO 639-1 (contoh: id, en): " + language);
        }
    }

    @Nested
    @DisplayName("Logging Config Tests")
    class LoggingConfigTests {

        @Test
        @DisplayName("isDbLogEnabled() mengembalikan nilai yang dapat digunakan")
        void isDbLogEnabled_dapatDigunakan() {
            // Test bahwa method berjalan tanpa exception dan mengembalikan boolean
            boolean enabled = config.isDbLogEnabled();

            // Verifikasi konsistensi: panggilan berulang harus sama
            assertEquals(enabled, config.isDbLogEnabled(),
                    "isDbLogEnabled() harus konsisten dalam multiple calls");
        }

        @Test
        @DisplayName("isAppLogEnabled() mengembalikan nilai yang dapat digunakan")
        void isAppLogEnabled_dapatDigunakan() {
            boolean enabled = config.isAppLogEnabled();

            // Verifikasi konsistensi
            assertEquals(enabled, config.isAppLogEnabled(),
                    "isAppLogEnabled() harus konsisten dalam multiple calls");
        }

        @Test
        @DisplayName("Logging config konsisten antara panggilan")
        void loggingConfig_konsisten() {
            boolean dbLog1 = config.isDbLogEnabled();
            boolean appLog1 = config.isAppLogEnabled();

            // Panggil beberapa kali
            for (int i = 0; i < 10; i++) {
                assertEquals(dbLog1, config.isDbLogEnabled());
                assertEquals(appLog1, config.isAppLogEnabled());
            }
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Semua config memiliki nilai default yang valid")
        void semuaConfigMemilikiNilaiDefault() {
            // Pastikan tidak ada null pointer exception
            assertDoesNotThrow(() -> {
                config.getAppVersion();
                config.getAppLanguage();
                config.isDbLogEnabled();
                config.isAppLogEnabled();
            }, "Semua getter harus berjalan tanpa exception");
        }
    }
}
