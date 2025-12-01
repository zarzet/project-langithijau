package com.studyplanner.model;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk KonfigurasiWidget.
 */
@DisplayName("KonfigurasiWidget Tests")
class KonfigurasiWidgetTest {

    @Nested
    @DisplayName("Constructor Tests")
    class KonstruktorTests {
        
        @Test
        @DisplayName("Constructor default membuat list widget kosong")
        void konstruktorDefault() {
            KonfigurasiWidget config = new KonfigurasiWidget();
            
            assertNotNull(config.getWidgetAktif());
        }
    }

    @Nested
    @DisplayName("JenisWidget Enum Tests")
    class JenisWidgetTests {
        
        @Test
        @DisplayName("JenisWidget memiliki semua nilai yang diharapkan")
        void jenisWidgetValues() {
            KonfigurasiWidget.JenisWidget[] values = KonfigurasiWidget.JenisWidget.values();
            
            assertTrue(values.length >= 4);
        }
        
        @Test
        @DisplayName("JenisWidget valueOf bekerja dengan benar")
        void jenisWidgetValueOf() {
            KonfigurasiWidget.JenisWidget widget = KonfigurasiWidget.JenisWidget.valueOf("RUNTUTAN_BELAJAR");
            
            assertEquals(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR, widget);
        }
    }

    @Nested
    @DisplayName("Serialisasi Tests")
    class SerialisasiTests {
        
        @Test
        @DisplayName("keString() menghasilkan string dengan kode widget")
        void keString() {
            KonfigurasiWidget config = new KonfigurasiWidget();
            config.tambahWidget(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR);
            config.tambahWidget(KonfigurasiWidget.JenisWidget.WAKTU_BELAJAR);
            
            String hasil = config.keString();
            
            assertNotNull(hasil);
            // Format: kode1,kode2 (menggunakan kode, bukan enum name)
            assertTrue(hasil.contains("streak")); // kode untuk RUNTUTAN_BELAJAR
            assertTrue(hasil.contains("study_time")); // kode untuk WAKTU_BELAJAR
        }
        
        @Test
        @DisplayName("dariString() membaca string kode dengan benar")
        void dariString() {
            // Menggunakan format kode, bukan enum name
            String input = "streak,study_time";
            
            KonfigurasiWidget config = KonfigurasiWidget.dariString(input);
            
            assertNotNull(config);
            List<KonfigurasiWidget.JenisWidget> widgets = config.getWidgetAktif();
            assertEquals(2, widgets.size());
            assertTrue(widgets.contains(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR));
            assertTrue(widgets.contains(KonfigurasiWidget.JenisWidget.WAKTU_BELAJAR));
        }
        
        @Test
        @DisplayName("dariString() dengan string kosong mengembalikan config kosong")
        void dariStringKosong() {
            KonfigurasiWidget config = KonfigurasiWidget.dariString("");
            
            assertNotNull(config);
            assertTrue(config.getWidgetAktif().isEmpty());
        }
        
        @Test
        @DisplayName("dariString() dengan null mengembalikan config kosong")
        void dariStringNull() {
            KonfigurasiWidget config = KonfigurasiWidget.dariString(null);
            
            assertNotNull(config);
            assertTrue(config.getWidgetAktif().isEmpty());
        }
        
        @Test
        @DisplayName("keString() dan dariString() roundtrip benar")
        void roundtrip() {
            KonfigurasiWidget original = new KonfigurasiWidget();
            original.tambahWidget(KonfigurasiWidget.JenisWidget.JAM_ANALOG);
            original.tambahWidget(KonfigurasiWidget.JenisWidget.ULASAN_BERIKUTNYA);
            
            String str = original.keString();
            KonfigurasiWidget restored = KonfigurasiWidget.dariString(str);
            
            assertEquals(original.getWidgetAktif().size(), restored.getWidgetAktif().size());
        }
    }

    @Nested
    @DisplayName("Widget Management Tests")
    class WidgetManagementTests {
        
        @Test
        @DisplayName("tambahWidget() menambah widget ke list")
        void tambahWidget() {
            KonfigurasiWidget config = new KonfigurasiWidget();
            
            config.tambahWidget(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR);
            
            assertTrue(config.getWidgetAktif().contains(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR));
        }
        
        @Test
        @DisplayName("hapusWidget() menghapus widget dari list")
        void hapusWidget() {
            KonfigurasiWidget config = new KonfigurasiWidget();
            config.tambahWidget(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR);
            
            config.hapusWidget(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR);
            
            assertFalse(config.getWidgetAktif().contains(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR));
        }
        
        @Test
        @DisplayName("pindahWidget() memindah widget ke posisi baru")
        void pindahWidget() {
            KonfigurasiWidget config = new KonfigurasiWidget();
            config.tambahWidget(KonfigurasiWidget.JenisWidget.RUNTUTAN_BELAJAR);
            config.tambahWidget(KonfigurasiWidget.JenisWidget.WAKTU_BELAJAR);
            config.tambahWidget(KonfigurasiWidget.JenisWidget.ULASAN_BERIKUTNYA);
            
            config.pindahWidget(0, 2);
            
            List<KonfigurasiWidget.JenisWidget> widgets = config.getWidgetAktif();
            assertEquals(KonfigurasiWidget.JenisWidget.WAKTU_BELAJAR, widgets.get(0));
        }
    }
}
