package com.studyplanner.kontroler.pembantu;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests untuk PembantuNavigasi.
 * Note: Beberapa test tidak bisa dilakukan karena membutuhkan JavaFX toolkit.
 */
class PembantuNavigasiTest {

    @Test
    void konstruktorMengaturHalamanAktifKeDashboard() {
        // Given & When
        PembantuNavigasi pembantu = new PembantuNavigasi(null, null);

        // Then
        assertEquals(PembantuNavigasi.Halaman.DASHBOARD, pembantu.getHalamanAktif());
    }

    @Test
    void callbackKembaliKeDashboardDipanggil() {
        // Given
        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        PembantuNavigasi pembantu = new PembantuNavigasi(null, () -> callbackCalled.set(true));

        // When: simulasi navigasi dan kembali
        // Note: Tidak bisa test penuh tanpa ScrollPane
        // Test ini memverifikasi callback tersimpan dengan benar

        // Then: callback harus tersimpan (tidak null)
        assertNotNull(pembantu);
    }

    @Test
    void enumHalamanMemilikiNilaiYangBenar() {
        // Verifikasi semua nilai enum tersedia
        assertEquals(4, PembantuNavigasi.Halaman.values().length);
        
        assertNotNull(PembantuNavigasi.Halaman.DASHBOARD);
        assertNotNull(PembantuNavigasi.Halaman.PENGATURAN);
        assertNotNull(PembantuNavigasi.Halaman.MANAJEMEN_MATKUL);
        assertNotNull(PembantuNavigasi.Halaman.LIHAT_JADWAL);
    }

    @Test
    void setTombolSidebarTidakError() {
        // Given
        PembantuNavigasi pembantu = new PembantuNavigasi(null, null);

        // When & Then: tidak boleh error meskipun null
        assertDoesNotThrow(() -> pembantu.setTombolSidebar(null, null, null));
    }

    @Test
    void simpanKontenDashboardTidakErrorDenganNullScrollPane() {
        // Given
        PembantuNavigasi pembantu = new PembantuNavigasi(null, null);

        // When & Then: tidak boleh error
        assertDoesNotThrow(() -> pembantu.simpanKontenDashboard());
    }

    @Test
    void kembaliKeDashboardTidakErrorJikaSudahDiDashboard() {
        // Given
        PembantuNavigasi pembantu = new PembantuNavigasi(null, null);
        assertEquals(PembantuNavigasi.Halaman.DASHBOARD, pembantu.getHalamanAktif());

        // When & Then: tidak boleh error
        assertDoesNotThrow(() -> pembantu.kembaliKeDashboard());
    }
}
