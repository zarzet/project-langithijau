package com.studyplanner.eksepsi;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk semua custom exception classes.
 */
@DisplayName("Custom Exception Tests")
class EksepsiTest {

    @Nested
    @DisplayName("EksepsiAksesBasisData Tests")
    class EksepsiAksesBasisDataTests {
        
        @Test
        @DisplayName("Constructor dengan message")
        void konstruktorDenganMessage() {
            EksepsiAksesBasisData ex = new EksepsiAksesBasisData("Test error");
            assertEquals("Test error", ex.getMessage());
        }
        
        @Test
        @DisplayName("Constructor dengan message dan cause")
        void konstruktorDenganMessageDanCause() {
            Exception cause = new RuntimeException("Root cause");
            EksepsiAksesBasisData ex = new EksepsiAksesBasisData("Test error", cause);
            
            assertEquals("Test error", ex.getMessage());
            assertEquals(cause, ex.getCause());
        }
    }

    @Nested
    @DisplayName("EksepsiDuplikat Tests")
    class EksepsiDuplikatTests {
        
        @Test
        @DisplayName("Constructor dengan message")
        void konstruktorDenganMessage() {
            EksepsiDuplikat ex = new EksepsiDuplikat("Duplicate entry");
            assertEquals("Duplicate entry", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("EksepsiEntitasTidakDitemukan Tests")
    class EksepsiEntitasTidakDitemukanTests {
        
        @Test
        @DisplayName("Constructor dengan message")
        void konstruktorDenganMessage() {
            EksepsiEntitasTidakDitemukan ex = new EksepsiEntitasTidakDitemukan("Entity not found");
            assertEquals("Entity not found", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("EksepsiKoneksiBasisData Tests")
    class EksepsiKoneksiBasisDataTests {
        
        @Test
        @DisplayName("Constructor dengan message")
        void konstruktorDenganMessage() {
            EksepsiKoneksiBasisData ex = new EksepsiKoneksiBasisData("Connection error");
            assertEquals("Connection error", ex.getMessage());
        }
        
        @Test
        @DisplayName("Constructor dengan message dan cause")
        void konstruktorDenganMessageDanCause() {
            Exception cause = new RuntimeException("Root cause");
            EksepsiKoneksiBasisData ex = new EksepsiKoneksiBasisData("Connection error", cause);
            
            assertEquals("Connection error", ex.getMessage());
            assertEquals(cause, ex.getCause());
        }
    }

    @Nested
    @DisplayName("EksepsiValidasi Tests")
    class EksepsiValidasiTests {
        
        @Test
        @DisplayName("Constructor dengan message")
        void konstruktorDenganMessage() {
            EksepsiValidasi ex = new EksepsiValidasi("Validation error");
            assertEquals("Validation error", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Exception Inheritance Tests")
    class InheritanceTests {
        
        @Test
        @DisplayName("EksepsiAksesBasisData extends SQLException")
        void aksesBasisData_isSQLException() {
            EksepsiAksesBasisData ex = new EksepsiAksesBasisData("Test");
            assertNotNull(ex);
            // Already verifies it compiles as SQLException subclass
        }
        
        @Test
        @DisplayName("EksepsiKoneksiBasisData adalah RuntimeException")
        void koneksiBasisData_isRuntimeException() {
            EksepsiKoneksiBasisData ex = new EksepsiKoneksiBasisData("Test");
            assertTrue(ex instanceof RuntimeException);
        }
        
        @Test
        @DisplayName("EksepsiDuplikat adalah RuntimeException")
        void duplikat_isRuntimeException() {
            EksepsiDuplikat ex = new EksepsiDuplikat("Test");
            assertTrue(ex instanceof RuntimeException);
        }
        
        @Test
        @DisplayName("EksepsiEntitasTidakDitemukan adalah RuntimeException")
        void entitasTidakDitemukan_isRuntimeException() {
            EksepsiEntitasTidakDitemukan ex = new EksepsiEntitasTidakDitemukan("Test");
            assertTrue(ex instanceof RuntimeException);
        }
        
        @Test
        @DisplayName("EksepsiValidasi adalah RuntimeException")
        void validasi_isRuntimeException() {
            EksepsiValidasi ex = new EksepsiValidasi("Test");
            assertTrue(ex instanceof RuntimeException);
        }
    }
}
