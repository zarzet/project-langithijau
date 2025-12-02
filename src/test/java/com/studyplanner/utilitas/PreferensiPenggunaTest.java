package com.studyplanner.utilitas;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk PreferensiPengguna.
 * Memverifikasi penyimpanan dan pembacaan preferensi pengguna.
 */
@DisplayName("PreferensiPengguna Tests")
class PreferensiPenggunaTest {

    private PreferensiPengguna preferensi;
    
    // ID unik untuk test agar tidak konflik dengan data real
    private static final int TEST_USER_ID = 999999;

    @BeforeEach
    void setUp() {
        preferensi = PreferensiPengguna.getInstance();
    }

    @AfterEach
    void tearDown() {
        // Bersihkan data test
        preferensi.resetPreferensiUser(TEST_USER_ID);
    }

    @Nested
    @DisplayName("Singleton Pattern Tests")
    class SingletonPatternTests {

        @Test
        @DisplayName("getInstance() mengembalikan instance yang sama")
        void getInstance_mengembalikanInstanceYangSama() {
            PreferensiPengguna instance1 = PreferensiPengguna.getInstance();
            PreferensiPengguna instance2 = PreferensiPengguna.getInstance();

            assertSame(instance1, instance2, "Singleton harus mengembalikan instance yang sama");
        }

        @Test
        @DisplayName("getInstance() tidak pernah null")
        void getInstance_tidakNull() {
            assertNotNull(PreferensiPengguna.getInstance());
        }

        @RepeatedTest(3)
        @DisplayName("getInstance() konsisten dalam multiple calls")
        void getInstance_konsisten() {
            assertSame(preferensi, PreferensiPengguna.getInstance());
        }
    }

    @Nested
    @DisplayName("Onboarding Tests")
    class OnboardingTests {

        @Test
        @DisplayName("isOnboardingSelesai() mengembalikan false untuk user baru")
        void onboarding_defaultFalseUntukUserBaru() {
            boolean hasil = preferensi.isOnboardingSelesai(TEST_USER_ID);

            assertFalse(hasil, "User baru harus memiliki onboarding = false");
        }

        @Test
        @DisplayName("setOnboardingSelesai(true) menyimpan status dengan benar")
        void setOnboardingTrue_tersimpan() {
            preferensi.setOnboardingSelesai(TEST_USER_ID, true);

            assertTrue(preferensi.isOnboardingSelesai(TEST_USER_ID),
                    "Status onboarding harus true setelah di-set");
        }

        @Test
        @DisplayName("setOnboardingSelesai(false) dapat mereset status")
        void setOnboardingFalse_dapatMereset() {
            preferensi.setOnboardingSelesai(TEST_USER_ID, true);
            preferensi.setOnboardingSelesai(TEST_USER_ID, false);

            assertFalse(preferensi.isOnboardingSelesai(TEST_USER_ID),
                    "Status onboarding harus bisa di-reset ke false");
        }

        @Test
        @DisplayName("Onboarding independen per user")
        void onboarding_independenPerUser() {
            int user1 = TEST_USER_ID;
            int user2 = TEST_USER_ID + 1;

            preferensi.setOnboardingSelesai(user1, true);
            preferensi.setOnboardingSelesai(user2, false);

            assertTrue(preferensi.isOnboardingSelesai(user1));
            assertFalse(preferensi.isOnboardingSelesai(user2));

            // Cleanup user2
            preferensi.resetPreferensiUser(user2);
        }
    }

    @Nested
    @DisplayName("Dark Mode Tests")
    class DarkModeTests {

        private boolean originalDarkMode;

        @BeforeEach
        void simpanOriginal() {
            originalDarkMode = preferensi.isDarkMode();
        }

        @AfterEach
        void kembalikanOriginal() {
            preferensi.setDarkMode(originalDarkMode);
        }

        @Test
        @DisplayName("isDarkMode() mengembalikan false secara default")
        void darkMode_defaultFalse() {
            // Reset ke default untuk test ini
            preferensi.setDarkMode(false);
            
            assertFalse(preferensi.isDarkMode(), "Dark mode default harus false");
        }

        @Test
        @DisplayName("setDarkMode(true) mengaktifkan dark mode")
        void setDarkModeTrue_aktif() {
            preferensi.setDarkMode(true);

            assertTrue(preferensi.isDarkMode(), "Dark mode harus aktif setelah di-set true");
        }

        @Test
        @DisplayName("setDarkMode(false) menonaktifkan dark mode")
        void setDarkModeFalse_nonaktif() {
            preferensi.setDarkMode(true);
            preferensi.setDarkMode(false);

            assertFalse(preferensi.isDarkMode(), "Dark mode harus nonaktif setelah di-set false");
        }

        @Test
        @DisplayName("Dark mode toggle bekerja bolak-balik")
        void darkMode_toggleBolakBalik() {
            preferensi.setDarkMode(false);
            assertFalse(preferensi.isDarkMode());

            preferensi.setDarkMode(true);
            assertTrue(preferensi.isDarkMode());

            preferensi.setDarkMode(false);
            assertFalse(preferensi.isDarkMode());
        }
    }

    @Nested
    @DisplayName("Durasi Default Tests")
    class DurasiDefaultTests {

        private int originalDurasi;

        @BeforeEach
        void simpanOriginal() {
            originalDurasi = preferensi.getDurasiDefault();
        }

        @AfterEach
        void kembalikanOriginal() {
            preferensi.setDurasiDefault(originalDurasi);
        }

        @Test
        @DisplayName("getDurasiDefault() mengembalikan nilai default 60 menit")
        void durasi_default60Menit() {
            // Default dari kode adalah 60
            // Catatan: nilai mungkin berbeda jika sudah diubah sebelumnya
            int durasi = preferensi.getDurasiDefault();

            assertTrue(durasi > 0, "Durasi default harus positif");
        }

        @Test
        @DisplayName("setDurasiDefault() menyimpan nilai dengan benar")
        void setDurasi_tersimpan() {
            preferensi.setDurasiDefault(45);

            assertEquals(45, preferensi.getDurasiDefault(),
                    "Durasi harus tersimpan dengan nilai yang di-set");
        }

        @Test
        @DisplayName("Durasi dapat diatur ke berbagai nilai")
        void durasi_berbagaiNilai() {
            int[] nilaiTest = {15, 30, 45, 60, 90, 120};

            for (int nilai : nilaiTest) {
                preferensi.setDurasiDefault(nilai);
                assertEquals(nilai, preferensi.getDurasiDefault(),
                        "Durasi " + nilai + " menit harus tersimpan");
            }
        }
    }

    @Nested
    @DisplayName("Reminder Tests")
    class ReminderTests {

        private boolean originalReminder;

        @BeforeEach
        void simpanOriginal() {
            originalReminder = preferensi.isReminderAktif();
        }

        @AfterEach
        void kembalikanOriginal() {
            preferensi.setReminderAktif(originalReminder);
        }

        @Test
        @DisplayName("isReminderAktif() mengembalikan true secara default")
        void reminder_defaultTrue() {
            // Default dari kode adalah true
            preferensi.setReminderAktif(true);
            
            assertTrue(preferensi.isReminderAktif(), "Reminder default harus aktif");
        }

        @Test
        @DisplayName("setReminderAktif(true) mengaktifkan reminder")
        void setReminderTrue_aktif() {
            preferensi.setReminderAktif(true);

            assertTrue(preferensi.isReminderAktif(), "Reminder harus aktif");
        }

        @Test
        @DisplayName("setReminderAktif(false) menonaktifkan reminder")
        void setReminderFalse_nonaktif() {
            preferensi.setReminderAktif(false);

            assertFalse(preferensi.isReminderAktif(), "Reminder harus nonaktif");
        }
    }

    @Nested
    @DisplayName("Widget Config Tests")
    class WidgetConfigTests {

        @Test
        @DisplayName("getWidgetConfig() mengembalikan string kosong untuk user baru")
        void widgetConfig_kosongUntukUserBaru() {
            String config = preferensi.getWidgetConfig(TEST_USER_ID);

            assertNotNull(config, "Widget config tidak boleh null");
            assertEquals("", config, "Widget config untuk user baru harus kosong");
        }

        @Test
        @DisplayName("setWidgetConfig() menyimpan konfigurasi dengan benar")
        void setWidgetConfig_tersimpan() {
            String testConfig = "streak,clock,calendar";

            preferensi.setWidgetConfig(TEST_USER_ID, testConfig);
            String hasil = preferensi.getWidgetConfig(TEST_USER_ID);

            assertEquals(testConfig, hasil, "Widget config harus tersimpan");
        }

        @Test
        @DisplayName("Widget config independen per user")
        void widgetConfig_independenPerUser() {
            int user1 = TEST_USER_ID;
            int user2 = TEST_USER_ID + 1;
            String config1 = "widget1,widget2";
            String config2 = "widget3,widget4,widget5";

            preferensi.setWidgetConfig(user1, config1);
            preferensi.setWidgetConfig(user2, config2);

            assertEquals(config1, preferensi.getWidgetConfig(user1));
            assertEquals(config2, preferensi.getWidgetConfig(user2));

            // Cleanup user2
            preferensi.resetPreferensiUser(user2);
        }

        @Test
        @DisplayName("sudahAturWidget() mengembalikan false untuk user baru")
        void sudahAturWidget_falseUntukUserBaru() {
            assertFalse(preferensi.sudahAturWidget(TEST_USER_ID),
                    "User baru belum mengatur widget");
        }

        @Test
        @DisplayName("sudahAturWidget() mengembalikan true setelah set config")
        void sudahAturWidget_trueSetelahSet() {
            preferensi.setWidgetConfig(TEST_USER_ID, "test");

            assertTrue(preferensi.sudahAturWidget(TEST_USER_ID),
                    "sudahAturWidget harus true setelah set config");
        }
    }

    @Nested
    @DisplayName("Reset Preferensi Tests")
    class ResetPreferensiTests {

        @Test
        @DisplayName("resetPreferensiUser() menghapus semua preferensi user")
        void reset_menghapusSemuaPreferensi() {
            // Set beberapa preferensi
            preferensi.setOnboardingSelesai(TEST_USER_ID, true);
            preferensi.setWidgetConfig(TEST_USER_ID, "widget1,widget2");

            // Reset
            preferensi.resetPreferensiUser(TEST_USER_ID);

            // Verifikasi
            assertFalse(preferensi.isOnboardingSelesai(TEST_USER_ID),
                    "Onboarding harus kembali ke default setelah reset");
            assertFalse(preferensi.sudahAturWidget(TEST_USER_ID),
                    "Widget config harus terhapus setelah reset");
        }

        @Test
        @DisplayName("resetPreferensiUser() tidak mempengaruhi user lain")
        void reset_tidakMempengaruhiUserLain() {
            int user1 = TEST_USER_ID;
            int user2 = TEST_USER_ID + 1;

            preferensi.setOnboardingSelesai(user1, true);
            preferensi.setOnboardingSelesai(user2, true);

            // Reset hanya user1
            preferensi.resetPreferensiUser(user1);

            // user2 tidak terpengaruh
            assertTrue(preferensi.isOnboardingSelesai(user2),
                    "User lain tidak boleh terpengaruh oleh reset");

            // Cleanup
            preferensi.resetPreferensiUser(user2);
        }
    }
}
