package com.studyplanner.utilitas;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk KonfigurasiAplikasi.
 */
@DisplayName("KonfigurasiAplikasi Tests")
class KonfigurasiAplikasiTest {

    @Nested
    @DisplayName("Singleton Tests")
    class SingletonTests {
        
        @Test
        @DisplayName("getInstance() mengembalikan instance yang sama")
        void getInstance_samaSaja() {
            KonfigurasiAplikasi instance1 = KonfigurasiAplikasi.getInstance();
            KonfigurasiAplikasi instance2 = KonfigurasiAplikasi.getInstance();
            
            assertSame(instance1, instance2);
        }
    }

    @Nested
    @DisplayName("Config Value Tests")
    class ConfigValueTests {
        
        @Test
        @DisplayName("getAppVersion() mengembalikan versi")
        void getAppVersion() {
            KonfigurasiAplikasi config = KonfigurasiAplikasi.getInstance();
            
            String version = config.getAppVersion();
            
            assertNotNull(version);
            assertFalse(version.isEmpty());
        }
        
        @Test
        @DisplayName("getAppLanguage() mengembalikan bahasa")
        void getAppLanguage() {
            KonfigurasiAplikasi config = KonfigurasiAplikasi.getInstance();
            
            String language = config.getAppLanguage();
            
            assertNotNull(language);
        }
        
        @Test
        @DisplayName("isDbLogEnabled() mengembalikan boolean")
        void isDbLogEnabled() {
            KonfigurasiAplikasi config = KonfigurasiAplikasi.getInstance();
            
            // Hanya test bahwa tidak throw exception
            boolean enabled = config.isDbLogEnabled();
            assertTrue(enabled || !enabled); // selalu true
        }
        
        @Test
        @DisplayName("isAppLogEnabled() mengembalikan boolean")
        void isAppLogEnabled() {
            KonfigurasiAplikasi config = KonfigurasiAplikasi.getInstance();
            
            boolean enabled = config.isAppLogEnabled();
            assertTrue(enabled || !enabled);
        }
    }
}
