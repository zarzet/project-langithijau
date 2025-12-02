package com.studyplanner.kontroler.pembantu;

import com.studyplanner.utilitas.PreferensiPengguna;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.Node;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests untuk PembantuTema.
 */
class PembantuTemaTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Reset preferensi ke file temp untuk isolasi test
        System.setProperty("user.home", tempDir.toString());
    }

    @Test
    void konstruktorMembacaPreferensiDarkMode() {
        // Given: preferensi dark mode sudah di-set
        PreferensiPengguna.getInstance().setDarkMode(true);

        // When: buat PembantuTema baru
        PembantuTema pembantu = new PembantuTema(null);

        // Then: isDarkMode harus true
        assertTrue(pembantu.isDarkMode());
    }

    @Test
    void isDarkModeDefaultFalse() {
        // Given: preferensi dark mode belum di-set (default false)
        PreferensiPengguna.getInstance().setDarkMode(false);

        // When: buat PembantuTema baru
        PembantuTema pembantu = new PembantuTema(null);

        // Then: isDarkMode harus false
        assertFalse(pembantu.isDarkMode());
    }

    @Test
    void callbackDipanggilSaatAlihkanModaGelap() {
        // Given
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        PembantuTema pembantu = new PembantuTema(() -> callbackCalled.set(true));

        // When: alihkan moda gelap (tanpa UI components)
        pembantu.alihkanModaGelap(null, (Node) null, (Stage) null);

        // Then: callback harus dipanggil
        assertTrue(callbackCalled.get());
    }

    @Test
    void alihkanModaGelapMengubahStatus() {
        // Given
        PreferensiPengguna.getInstance().setDarkMode(false);
        PembantuTema pembantu = new PembantuTema(null);
        assertFalse(pembantu.isDarkMode());

        // When: alihkan moda gelap
        pembantu.alihkanModaGelap(null, (Node) null, (Stage) null);

        // Then: status harus berubah
        assertTrue(pembantu.isDarkMode());

        // When: alihkan lagi
        pembantu.alihkanModaGelap(null, (Node) null, (Stage) null);

        // Then: status kembali ke false
        assertFalse(pembantu.isDarkMode());
    }

    @Test
    void alihkanModaGelapMenyimpanKePreferensi() {
        // Given
        PreferensiPengguna.getInstance().setDarkMode(false);
        PembantuTema pembantu = new PembantuTema(null);

        // When
        pembantu.alihkanModaGelap(null, (Node) null, (Stage) null);

        // Then: preferensi harus tersimpan
        assertTrue(PreferensiPengguna.getInstance().isDarkMode());
    }
}
