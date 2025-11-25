package com.studyplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk model JadwalUjian.
 */
@DisplayName("JadwalUjian Model Tests")
class JadwalUjianTest {

    private JadwalUjian jadwal;

    @BeforeEach
    void setUp() {
        jadwal = new JadwalUjian();
    }

    @Nested
    @DisplayName("Konstruktor Tests")
    class KonstruktorTests {

        @Test
        @DisplayName("Konstruktor default menginisialisasi selesai = false")
        void konstruktorDefault_selesaiFalse() {
            JadwalUjian jadwalBaru = new JadwalUjian();
            assertFalse(jadwalBaru.isSelesai());
        }

        @Test
        @DisplayName("Konstruktor dengan parameter menginisialisasi dengan benar")
        void konstruktorDenganParameter_inisialisasiBenar() {
            LocalDate tanggal = LocalDate.of(2025, 12, 15);
            JadwalUjian jadwalParam = new JadwalUjian(1, 100, "UTS", "Ujian Tengah Semester", tanggal);

            assertEquals(1, jadwalParam.getId());
            assertEquals(100, jadwalParam.getIdMataKuliah());
            assertEquals("UTS", jadwalParam.getTipeUjian());
            assertEquals("Ujian Tengah Semester", jadwalParam.getJudul());
            assertEquals(tanggal, jadwalParam.getTanggalUjian());
            assertFalse(jadwalParam.isSelesai());
        }
    }

    @Nested
    @DisplayName("Getter dan Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setId dan getId bekerja dengan benar")
        void setIdDanGetId() {
            jadwal.setId(5);
            assertEquals(5, jadwal.getId());
        }

        @Test
        @DisplayName("setIdMataKuliah dan getIdMataKuliah bekerja dengan benar")
        void setIdMataKuliahDanGetIdMataKuliah() {
            jadwal.setIdMataKuliah(200);
            assertEquals(200, jadwal.getIdMataKuliah());
        }

        @Test
        @DisplayName("setTipeUjian dan getTipeUjian bekerja dengan benar")
        void setTipeUjianDanGetTipeUjian() {
            jadwal.setTipeUjian("UAS");
            assertEquals("UAS", jadwal.getTipeUjian());
        }

        @Test
        @DisplayName("setJudul dan getJudul bekerja dengan benar")
        void setJudulDanGetJudul() {
            jadwal.setJudul("Ujian Akhir Semester");
            assertEquals("Ujian Akhir Semester", jadwal.getJudul());
        }

        @Test
        @DisplayName("setTanggalUjian dan getTanggalUjian bekerja dengan benar")
        void setTanggalUjianDanGetTanggalUjian() {
            LocalDate tanggal = LocalDate.of(2025, 12, 20);
            jadwal.setTanggalUjian(tanggal);
            assertEquals(tanggal, jadwal.getTanggalUjian());
        }

        @Test
        @DisplayName("setWaktuUjian dan getWaktuUjian bekerja dengan benar")
        void setWaktuUjianDanGetWaktuUjian() {
            LocalTime waktu = LocalTime.of(9, 0);
            jadwal.setWaktuUjian(waktu);
            assertEquals(waktu, jadwal.getWaktuUjian());
        }

        @Test
        @DisplayName("setLokasi dan getLokasi bekerja dengan benar")
        void setLokasiDanGetLokasi() {
            jadwal.setLokasi("Gedung A, Ruang 301");
            assertEquals("Gedung A, Ruang 301", jadwal.getLokasi());
        }

        @Test
        @DisplayName("setCatatan dan getCatatan bekerja dengan benar")
        void setCatatanDanGetCatatan() {
            jadwal.setCatatan("Bawa kalkulator scientific");
            assertEquals("Bawa kalkulator scientific", jadwal.getCatatan());
        }

        @Test
        @DisplayName("setSelesai dan isSelesai bekerja dengan benar")
        void setSelesaiDanIsSelesai() {
            jadwal.setSelesai(true);
            assertTrue(jadwal.isSelesai());
        }
    }

    @Nested
    @DisplayName("getHariMenujuUjian Tests")
    class HariMenujuUjianTests {

        @Test
        @DisplayName("Ujian 7 hari ke depan mengembalikan 7")
        void ujian7HariKeDepan() {
            jadwal.setTanggalUjian(LocalDate.now().plusDays(7));
            assertEquals(7, jadwal.getHariMenujuUjian());
        }

        @Test
        @DisplayName("Ujian hari ini mengembalikan 0")
        void ujianHariIni() {
            jadwal.setTanggalUjian(LocalDate.now());
            assertEquals(0, jadwal.getHariMenujuUjian());
        }

        @Test
        @DisplayName("Ujian sudah lewat mengembalikan nilai negatif")
        void ujianSudahLewat() {
            jadwal.setTanggalUjian(LocalDate.now().minusDays(3));
            assertEquals(-3, jadwal.getHariMenujuUjian());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString mengembalikan format yang benar")
        void toStringFormatBenar() {
            jadwal.setJudul("UTS Struktur Data");
            jadwal.setTanggalUjian(LocalDate.of(2025, 12, 15));
            
            assertEquals("UTS Struktur Data (2025-12-15)", jadwal.toString());
        }
    }
}
