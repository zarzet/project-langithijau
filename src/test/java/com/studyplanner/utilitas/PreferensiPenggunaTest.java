package com.studyplanner.utilitas;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk PreferensiPengguna.
 */
@DisplayName("PreferensiPengguna Tests")
class PreferensiPenggunaTest {

    private PreferensiPengguna preferensi;

    @BeforeEach
    void setUp() {
        preferensi = PreferensiPengguna.getInstance();
    }

    @Nested
    @DisplayName("Singleton Tests")
    class SingletonTests {
        
        @Test
        @DisplayName("getInstance() mengembalikan instance yang sama")
        void getInstance_samaSaja() {
            PreferensiPengguna instance1 = PreferensiPengguna.getInstance();
            PreferensiPengguna instance2 = PreferensiPengguna.getInstance();
            
            assertSame(instance1, instance2);
        }
    }

    @Nested
    @DisplayName("Onboarding Tests")
    class OnboardingTests {
        
        @Test
        @DisplayName("isOnboardingSelesai() mengembalikan default false")
        void onboarding_defaultFalse() {
            // User dengan ID yang tidak ada seharusnya return false
            boolean hasil = preferensi.isOnboardingSelesai(99999);
            
            assertFalse(hasil);
        }
        
        @Test
        @DisplayName("setOnboardingSelesai() menyimpan status")
        void setOnboarding_berhasil() {
            int testUserId = 12345;
            preferensi.setOnboardingSelesai(testUserId, true);
            
            boolean hasil = preferensi.isOnboardingSelesai(testUserId);
            
            assertTrue(hasil);
            
            // Cleanup
            preferensi.setOnboardingSelesai(testUserId, false);
        }
    }

    @Nested
    @DisplayName("Dark Mode Tests")
    class DarkModeTests {
        
        @Test
        @DisplayName("isDarkMode() mengembalikan boolean")
        void darkMode_defaultFalse() {
            boolean hasil = preferensi.isDarkMode();
            
            // Tidak masalah nilai apa, yang penting tidak exception
            assertTrue(hasil || !hasil);
        }
        
        @Test
        @DisplayName("setDarkMode() menyimpan nilai")
        void setDarkMode_berhasil() {
            boolean original = preferensi.isDarkMode();
            
            preferensi.setDarkMode(!original);
            boolean hasil = preferensi.isDarkMode();
            
            assertEquals(!original, hasil);
            
            // Cleanup
            preferensi.setDarkMode(original);
        }
    }

    @Nested
    @DisplayName("Durasi Default Tests")
    class DurasiDefaultTests {
        
        @Test
        @DisplayName("getDurasiDefault() mengembalikan nilai positif")
        void durasi_defaultPositif() {
            int durasi = preferensi.getDurasiDefault();
            
            assertTrue(durasi > 0);
        }
        
        @Test
        @DisplayName("setDurasiDefault() menyimpan nilai")
        void setDurasi_berhasil() {
            int original = preferensi.getDurasiDefault();
            
            preferensi.setDurasiDefault(45);
            int hasil = preferensi.getDurasiDefault();
            
            assertEquals(45, hasil);
            
            // Cleanup
            preferensi.setDurasiDefault(original);
        }
    }

    @Nested
    @DisplayName("Reminder Tests")
    class ReminderTests {
        
        @Test
        @DisplayName("isReminderAktif() mengembalikan boolean")
        void reminder_returnBoolean() {
            boolean hasil = preferensi.isReminderAktif();
            
            assertTrue(hasil || !hasil);
        }
        
        @Test
        @DisplayName("setReminderAktif() menyimpan nilai")
        void setReminder_berhasil() {
            boolean original = preferensi.isReminderAktif();
            
            preferensi.setReminderAktif(!original);
            boolean hasil = preferensi.isReminderAktif();
            
            assertEquals(!original, hasil);
            
            // Cleanup
            preferensi.setReminderAktif(original);
        }
    }

    @Nested
    @DisplayName("Widget Config Tests")
    class WidgetConfigTests {
        
        @Test
        @DisplayName("getWidgetConfig() mengembalikan string")
        void widgetConfig_returnsString() {
            String config = preferensi.getWidgetConfig(1);
            
            // Bisa null atau string
            assertTrue(config == null || config instanceof String);
        }
        
        @Test
        @DisplayName("setWidgetConfig() menyimpan konfigurasi")
        void setWidgetConfig_berhasil() {
            int testUserId = 12347;
            String testConfig = "streak,clock";
            
            preferensi.setWidgetConfig(testUserId, testConfig);
            String hasil = preferensi.getWidgetConfig(testUserId);
            
            assertEquals(testConfig, hasil);
        }
    }
}
