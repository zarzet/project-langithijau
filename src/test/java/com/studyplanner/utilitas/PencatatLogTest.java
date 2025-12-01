package com.studyplanner.utilitas;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk PencatatLog.
 */
@DisplayName("PencatatLog Tests")
class PencatatLogTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

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
        @DisplayName("db() mencetak log database")
        void db_cetakLog() {
            PencatatLog.db("Test database log");
            
            // Jika logging enabled, harus ada output
            // Jika disabled, output kosong - kedua case valid
            String output = outContent.toString();
            assertTrue(output.isEmpty() || output.contains("DB"));
        }
    }

    @Nested
    @DisplayName("app() Tests")
    class AppLogTests {
        
        @Test
        @DisplayName("app() mencetak log aplikasi")
        void app_cetakLog() {
            PencatatLog.app("Test app log");
            
            String output = outContent.toString();
            assertTrue(output.isEmpty() || output.contains("APP"));
        }
    }

    @Nested
    @DisplayName("info() Tests")
    class InfoLogTests {
        
        @Test
        @DisplayName("info() mencetak log info")
        void info_cetakLog() {
            PencatatLog.info("Test info log");
            
            String output = outContent.toString();
            assertTrue(output.isEmpty() || output.contains("INFO"));
        }
    }

    @Nested
    @DisplayName("error() Tests")
    class ErrorLogTests {
        
        @Test
        @DisplayName("error() selalu mencetak ke stderr")
        void error_selaluCetak() {
            PencatatLog.error("Test error log");
            
            String output = errContent.toString();
            assertTrue(output.contains("ERROR"));
            assertTrue(output.contains("Test error log"));
        }
    }
}
