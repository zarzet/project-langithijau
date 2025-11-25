package com.studyplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk model SesiBelajar.
 */
@DisplayName("SesiBelajar Model Tests")
class SesiBelajarTest {

    private SesiBelajar sesi;

    @BeforeEach
    void setUp() {
        sesi = new SesiBelajar();
    }

    @Nested
    @DisplayName("Konstruktor Tests")
    class KonstruktorTests {

        @Test
        @DisplayName("Konstruktor default menginisialisasi nilai bawaan")
        void konstruktorDefault_inisialisasiNilaiBawaan() {
            SesiBelajar sesiBaru = new SesiBelajar();

            assertFalse(sesiBaru.isSelesai(), "Selesai default harus false");
            assertEquals(30, sesiBaru.getDurasiMenit(), "Durasi default harus 30 menit");
        }

        @Test
        @DisplayName("Konstruktor dengan parameter menginisialisasi dengan benar")
        void konstruktorDenganParameter_inisialisasiBenar() {
            LocalDate tanggal = LocalDate.of(2025, 11, 25);
            SesiBelajar sesiParam = new SesiBelajar(1, 10, 100, tanggal, "ULASAN");

            assertEquals(1, sesiParam.getId());
            assertEquals(10, sesiParam.getIdTopik());
            assertEquals(100, sesiParam.getIdMataKuliah());
            assertEquals(tanggal, sesiParam.getTanggalJadwal());
            assertEquals("ULASAN", sesiParam.getTipeSesi());
            assertFalse(sesiParam.isSelesai());
            assertEquals(30, sesiParam.getDurasiMenit());
        }
    }

    @Nested
    @DisplayName("Getter dan Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setId dan getId bekerja dengan benar")
        void setIdDanGetId() {
            sesi.setId(42);
            assertEquals(42, sesi.getId());
        }

        @Test
        @DisplayName("setIdTopik dan getIdTopik bekerja dengan benar")
        void setIdTopikDanGetIdTopik() {
            sesi.setIdTopik(15);
            assertEquals(15, sesi.getIdTopik());
        }

        @Test
        @DisplayName("setIdMataKuliah dan getIdMataKuliah bekerja dengan benar")
        void setIdMataKuliahDanGetIdMataKuliah() {
            sesi.setIdMataKuliah(200);
            assertEquals(200, sesi.getIdMataKuliah());
        }

        @Test
        @DisplayName("setTanggalJadwal dan getTanggalJadwal bekerja dengan benar")
        void setTanggalJadwalDanGetTanggalJadwal() {
            LocalDate tanggal = LocalDate.of(2025, 12, 1);
            sesi.setTanggalJadwal(tanggal);
            assertEquals(tanggal, sesi.getTanggalJadwal());
        }

        @Test
        @DisplayName("setTipeSesi dan getTipeSesi bekerja dengan benar")
        void setTipeSesiDanGetTipeSesi() {
            sesi.setTipeSesi("BELAJAR_BARU");
            assertEquals("BELAJAR_BARU", sesi.getTipeSesi());
        }

        @Test
        @DisplayName("setSelesai dan isSelesai bekerja dengan benar")
        void setSelesaiDanIsSelesai() {
            sesi.setSelesai(true);
            assertTrue(sesi.isSelesai());
        }

        @Test
        @DisplayName("setSelesaiPada dan getSelesaiPada bekerja dengan benar")
        void setSelesaiPadaDanGetSelesaiPada() {
            LocalDateTime waktu = LocalDateTime.of(2025, 11, 25, 14, 30);
            sesi.setSelesaiPada(waktu);
            assertEquals(waktu, sesi.getSelesaiPada());
        }

        @Test
        @DisplayName("setRatingPerforma dan getRatingPerforma bekerja dengan benar")
        void setRatingPerformaDanGetRatingPerforma() {
            sesi.setRatingPerforma(4);
            assertEquals(4, sesi.getRatingPerforma());
        }

        @Test
        @DisplayName("setCatatan dan getCatatan bekerja dengan benar")
        void setCatatanDanGetCatatan() {
            sesi.setCatatan("Sesi belajar berjalan lancar");
            assertEquals("Sesi belajar berjalan lancar", sesi.getCatatan());
        }

        @Test
        @DisplayName("setDurasiMenit dan getDurasiMenit bekerja dengan benar")
        void setDurasiMenitDanGetDurasiMenit() {
            sesi.setDurasiMenit(45);
            assertEquals(45, sesi.getDurasiMenit());
        }

        @Test
        @DisplayName("setNamaTopik dan getNamaTopik bekerja dengan benar")
        void setNamaTopikDanGetNamaTopik() {
            sesi.setNamaTopik("Binary Tree");
            assertEquals("Binary Tree", sesi.getNamaTopik());
        }

        @Test
        @DisplayName("setNamaMataKuliah dan getNamaMataKuliah bekerja dengan benar")
        void setNamaMataKuliahDanGetNamaMataKuliah() {
            sesi.setNamaMataKuliah("Struktur Data");
            assertEquals("Struktur Data", sesi.getNamaMataKuliah());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString mengembalikan format yang benar")
        void toStringMengembalikanFormatBenar() {
            sesi.setTipeSesi("ULASAN");
            sesi.setNamaTopik("Sorting Algorithm");
            sesi.setNamaMataKuliah("Algoritma");

            String expected = "ULASAN: Sorting Algorithm (Algoritma)";
            assertEquals(expected, sesi.toString());
        }

        @Test
        @DisplayName("toString dengan nilai null")
        void toStringDenganNilaiNull() {
            String result = sesi.toString();
            assertEquals("null: null (null)", result);
        }
    }

    @Nested
    @DisplayName("Skenario Penggunaan Tests")
    class SkenarioPenggunaanTests {

        @Test
        @DisplayName("Simulasi menyelesaikan sesi belajar")
        void simulasiMenyelesaikanSesi() {
            // Setup sesi baru
            LocalDate tanggalJadwal = LocalDate.now();
            sesi.setId(1);
            sesi.setIdTopik(10);
            sesi.setIdMataKuliah(100);
            sesi.setTanggalJadwal(tanggalJadwal);
            sesi.setTipeSesi("ULASAN");
            sesi.setDurasiMenit(30);
            sesi.setNamaTopik("Hash Table");
            sesi.setNamaMataKuliah("Struktur Data");

            // Verifikasi status awal
            assertFalse(sesi.isSelesai());
            assertNull(sesi.getSelesaiPada());
            assertEquals(0, sesi.getRatingPerforma());

            // Simulasi menyelesaikan sesi
            LocalDateTime waktuSelesai = LocalDateTime.now();
            sesi.setSelesai(true);
            sesi.setSelesaiPada(waktuSelesai);
            sesi.setRatingPerforma(4);
            sesi.setCatatan("Berhasil memahami konsep dasar hash table");

            // Verifikasi setelah selesai
            assertTrue(sesi.isSelesai());
            assertEquals(waktuSelesai, sesi.getSelesaiPada());
            assertEquals(4, sesi.getRatingPerforma());
            assertNotNull(sesi.getCatatan());
        }
    }
}
