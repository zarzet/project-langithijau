package com.studyplanner.utilitas;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk PencatatLog.
 * Memverifikasi bahwa log dicetak dengan format yang benar.
 */
@DisplayName("PencatatLog Tests")
class PencatatLogTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    // Regex untuk format log: [HH:mm:ss.SSS] [TYPE] message
    private static final String LOG_FORMAT_REGEX = "\\[\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\] \\[%s\\] .+";

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Nested
    @DisplayName("db() Tests")
    class DbLogTests {

        @Test
        @DisplayName("db() mencetak log dengan format yang benar jika enabled")
        void db_formatBenar() {
            PencatatLog.db("Test database log");

            String output = outContent.toString().trim();
            
            if (KonfigurasiAplikasi.getInstance().isDbLogEnabled()) {
                // Verifikasi format: [HH:mm:ss.SSS] [DB] message
                assertTrue(output.matches(String.format(LOG_FORMAT_REGEX, "DB")),
                        "Format log harus [waktu] [DB] pesan. Actual: " + output);
                assertTrue(output.contains("Test database log"),
                        "Log harus mengandung pesan yang dikirim");
            } else {
                assertTrue(output.isEmpty(),
                        "Tidak boleh ada output jika DB log disabled");
            }
        }

        @Test
        @DisplayName("db() tidak mencetak ke stderr")
        void db_tidakKeSterr() {
            PencatatLog.db("Test database log");

            assertTrue(errContent.toString().isEmpty(),
                    "db() tidak boleh mencetak ke stderr");
        }
    }

    @Nested
    @DisplayName("app() Tests")
    class AppLogTests {

        @Test
        @DisplayName("app() mencetak log dengan format yang benar jika enabled")
        void app_formatBenar() {
            PencatatLog.app("Test app log");

            String output = outContent.toString().trim();
            
            if (KonfigurasiAplikasi.getInstance().isAppLogEnabled()) {
                assertTrue(output.matches(String.format(LOG_FORMAT_REGEX, "APP")),
                        "Format log harus [waktu] [APP] pesan. Actual: " + output);
                assertTrue(output.contains("Test app log"),
                        "Log harus mengandung pesan yang dikirim");
            } else {
                assertTrue(output.isEmpty(),
                        "Tidak boleh ada output jika APP log disabled");
            }
        }

        @Test
        @DisplayName("app() tidak mencetak ke stderr")
        void app_tidakKeSterr() {
            PencatatLog.app("Test app log");

            assertTrue(errContent.toString().isEmpty(),
                    "app() tidak boleh mencetak ke stderr");
        }
    }

    @Nested
    @DisplayName("info() Tests")
    class InfoLogTests {

        @Test
        @DisplayName("info() mencetak log dengan format yang benar jika enabled")
        void info_formatBenar() {
            PencatatLog.info("Test info log");

            String output = outContent.toString().trim();
            
            if (KonfigurasiAplikasi.getInstance().isAppLogEnabled()) {
                assertTrue(output.matches(String.format(LOG_FORMAT_REGEX, "INFO")),
                        "Format log harus [waktu] [INFO] pesan. Actual: " + output);
                assertTrue(output.contains("Test info log"),
                        "Log harus mengandung pesan yang dikirim");
            } else {
                assertTrue(output.isEmpty(),
                        "Tidak boleh ada output jika APP log disabled");
            }
        }

        @Test
        @DisplayName("info() tidak mencetak ke stderr")
        void info_tidakKeSterr() {
            PencatatLog.info("Test info log");

            assertTrue(errContent.toString().isEmpty(),
                    "info() tidak boleh mencetak ke stderr");
        }
    }

    @Nested
    @DisplayName("error() Tests")
    class ErrorLogTests {

        @Test
        @DisplayName("error() selalu mencetak ke stderr terlepas dari config")
        void error_selaluCetakKeStderr() {
            PencatatLog.error("Test error log");

            String output = errContent.toString().trim();
            
            // error() selalu mencetak, tidak terpengaruh config
            assertFalse(output.isEmpty(), "error() harus selalu mencetak");
            assertTrue(output.matches(String.format(LOG_FORMAT_REGEX, "ERROR")),
                    "Format log harus [waktu] [ERROR] pesan. Actual: " + output);
            assertTrue(output.contains("Test error log"),
                    "Log harus mengandung pesan yang dikirim");
        }

        @Test
        @DisplayName("error() tidak mencetak ke stdout")
        void error_tidakKeStdout() {
            PencatatLog.error("Test error log");

            assertTrue(outContent.toString().isEmpty(),
                    "error() tidak boleh mencetak ke stdout");
        }

        @Test
        @DisplayName("error() dapat mencetak pesan panjang")
        void error_pesanPanjang() {
            String pesanPanjang = "Error: " + "x".repeat(500);
            PencatatLog.error(pesanPanjang);

            String output = errContent.toString();
            assertTrue(output.contains(pesanPanjang),
                    "Pesan panjang harus tercatat lengkap");
        }

        @Test
        @DisplayName("error() dapat mencetak karakter khusus")
        void error_karakterKhusus() {
            String pesanKhusus = "Error: Exception at line 42 - NullPointerException: obj.method()";
            PencatatLog.error(pesanKhusus);

            String output = errContent.toString();
            assertTrue(output.contains(pesanKhusus),
                    "Karakter khusus harus tercatat dengan benar");
        }
    }

    @Nested
    @DisplayName("Format Timestamp Tests")
    class FormatTimestampTests {

        @Test
        @DisplayName("Timestamp menggunakan format HH:mm:ss.SSS")
        void timestamp_formatBenar() {
            // error() selalu mencetak, jadi gunakan ini untuk test
            PencatatLog.error("Test");

            String output = errContent.toString().trim();
            // Format: [HH:mm:ss.SSS] - gunakan Pattern dengan DOTALL untuk menangani newline
            assertTrue(output.contains("[") && output.contains("]"),
                    "Output harus mengandung timestamp dalam bracket");
            
            // Verifikasi format waktu dengan regex yang lebih toleran
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "\\[\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\]", 
                    java.util.regex.Pattern.DOTALL);
            assertTrue(pattern.matcher(output).find(),
                    "Timestamp harus dalam format HH:mm:ss.SSS. Actual: " + output);
        }
    }
}
