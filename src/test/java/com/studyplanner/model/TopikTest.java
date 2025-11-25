package com.studyplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk model Topik.
 */
@DisplayName("Topik Model Tests")
class TopikTest {

    private Topik topik;

    @BeforeEach
    void setUp() {
        topik = new Topik();
    }

    @Nested
    @DisplayName("Konstruktor Tests")
    class KonstruktorTests {

        @Test
        @DisplayName("Konstruktor default menginisialisasi nilai bawaan dengan benar")
        void konstruktorDefault_inisialisasiNilaiBawaan() {
            Topik topikBaru = new Topik();

            assertEquals(3, topikBaru.getPrioritas(), "Prioritas default harus 3");
            assertEquals(3, topikBaru.getTingkatKesulitan(), "Tingkat kesulitan default harus 3");
            assertEquals(0, topikBaru.getJumlahUlasan(), "Jumlah ulasan default harus 0");
            assertEquals(2.5, topikBaru.getFaktorKemudahan(), "Faktor kemudahan default harus 2.5");
            assertEquals(1, topikBaru.getInterval(), "Interval default harus 1");
            assertFalse(topikBaru.isDikuasai(), "Dikuasai default harus false");
            assertEquals(0.0, topikBaru.getStabilitasFsrs(), "Stabilitas FSRS default harus 0.0");
            assertEquals(0.0, topikBaru.getKesulitanFsrs(), "Kesulitan FSRS default harus 0.0");
            assertEquals(0.9, topikBaru.getRetensiDiinginkan(), "Retensi diinginkan default harus 0.9");
            assertEquals(0.1542, topikBaru.getPeluruhanFsrs(), 0.0001, "Peluruhan FSRS default");
        }

        @Test
        @DisplayName("Konstruktor dengan parameter menginisialisasi dengan benar")
        void konstruktorDenganParameter_inisialisasiBenar() {
            Topik topikParam = new Topik(1, 100, "Algoritma Sorting", "Bubble sort, Quick sort");

            assertEquals(1, topikParam.getId());
            assertEquals(100, topikParam.getIdMataKuliah());
            assertEquals("Algoritma Sorting", topikParam.getNama());
            assertEquals("Bubble sort, Quick sort", topikParam.getDeskripsi());
            // Nilai default tetap diinisialisasi
            assertEquals(3, topikParam.getPrioritas());
        }
    }

    @Nested
    @DisplayName("Getter dan Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setId dan getId bekerja dengan benar")
        void setIdDanGetId() {
            topik.setId(42);
            assertEquals(42, topik.getId());
        }

        @Test
        @DisplayName("setIdMataKuliah dan getIdMataKuliah bekerja dengan benar")
        void setIdMataKuliahDanGetIdMataKuliah() {
            topik.setIdMataKuliah(99);
            assertEquals(99, topik.getIdMataKuliah());
        }

        @Test
        @DisplayName("setNama dan getNama bekerja dengan benar")
        void setNamaDanGetNama() {
            topik.setNama("Pemrograman Dinamis");
            assertEquals("Pemrograman Dinamis", topik.getNama());
        }

        @Test
        @DisplayName("setDeskripsi dan getDeskripsi bekerja dengan benar")
        void setDeskripsiDanGetDeskripsi() {
            topik.setDeskripsi("Teknik optimasi dengan memoization");
            assertEquals("Teknik optimasi dengan memoization", topik.getDeskripsi());
        }

        @Test
        @DisplayName("setPrioritas dan getPrioritas bekerja dengan benar")
        void setPrioritasDanGetPrioritas() {
            topik.setPrioritas(5);
            assertEquals(5, topik.getPrioritas());
        }

        @Test
        @DisplayName("setTingkatKesulitan dan getTingkatKesulitan bekerja dengan benar")
        void setTingkatKesulitanDanGetTingkatKesulitan() {
            topik.setTingkatKesulitan(4);
            assertEquals(4, topik.getTingkatKesulitan());
        }

        @Test
        @DisplayName("setTanggalBelajarPertama dan getTanggalBelajarPertama bekerja")
        void setTanggalBelajarPertamaDanGetTanggalBelajarPertama() {
            LocalDate tanggal = LocalDate.of(2025, 1, 15);
            topik.setTanggalBelajarPertama(tanggal);
            assertEquals(tanggal, topik.getTanggalBelajarPertama());
        }

        @Test
        @DisplayName("setTanggalUlasanTerakhir dan getTanggalUlasanTerakhir bekerja")
        void setTanggalUlasanTerakhirDanGetTanggalUlasanTerakhir() {
            LocalDate tanggal = LocalDate.of(2025, 11, 20);
            topik.setTanggalUlasanTerakhir(tanggal);
            assertEquals(tanggal, topik.getTanggalUlasanTerakhir());
        }

        @Test
        @DisplayName("setJumlahUlasan dan getJumlahUlasan bekerja dengan benar")
        void setJumlahUlasanDanGetJumlahUlasan() {
            topik.setJumlahUlasan(10);
            assertEquals(10, topik.getJumlahUlasan());
        }

        @Test
        @DisplayName("setFaktorKemudahan dan getFaktorKemudahan bekerja dengan benar")
        void setFaktorKemudahanDanGetFaktorKemudahan() {
            topik.setFaktorKemudahan(3.0);
            assertEquals(3.0, topik.getFaktorKemudahan());
        }

        @Test
        @DisplayName("setInterval dan getInterval bekerja dengan benar")
        void setIntervalDanGetInterval() {
            topik.setInterval(7);
            assertEquals(7, topik.getInterval());
        }

        @Test
        @DisplayName("setDikuasai dan isDikuasai bekerja dengan benar")
        void setDikuasaiDanIsDikuasai() {
            topik.setDikuasai(true);
            assertTrue(topik.isDikuasai());
        }
    }

    @Nested
    @DisplayName("FSRS Parameter Tests")
    class FsrsParameterTests {

        @Test
        @DisplayName("setStabilitasFsrs dan getStabilitasFsrs bekerja dengan benar")
        void setStabilitasFsrsDanGetStabilitasFsrs() {
            topik.setStabilitasFsrs(15.5);
            assertEquals(15.5, topik.getStabilitasFsrs());
        }

        @Test
        @DisplayName("setKesulitanFsrs dan getKesulitanFsrs bekerja dengan benar")
        void setKesulitanFsrsDanGetKesulitanFsrs() {
            topik.setKesulitanFsrs(5.2);
            assertEquals(5.2, topik.getKesulitanFsrs());
        }

        @Test
        @DisplayName("setRetensiDiinginkan dan getRetensiDiinginkan bekerja dengan benar")
        void setRetensiDiinginkanDanGetRetensiDiinginkan() {
            topik.setRetensiDiinginkan(0.85);
            assertEquals(0.85, topik.getRetensiDiinginkan());
        }

        @Test
        @DisplayName("setPeluruhanFsrs dan getPeluruhanFsrs bekerja dengan benar")
        void setPeluruhanFsrsDanGetPeluruhanFsrs() {
            topik.setPeluruhanFsrs(0.2);
            assertEquals(0.2, topik.getPeluruhanFsrs());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString mengembalikan nama topik")
        void toStringMengembalikanNama() {
            topik.setNama("Binary Search Tree");
            assertEquals("Binary Search Tree", topik.toString());
        }

        @Test
        @DisplayName("toString mengembalikan null jika nama belum diset")
        void toStringMengembalikanNullJikaNamaBelumDiSet() {
            assertNull(topik.toString());
        }
    }
}
