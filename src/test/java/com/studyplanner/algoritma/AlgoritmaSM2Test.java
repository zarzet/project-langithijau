package com.studyplanner.algoritma;

import com.studyplanner.algoritma.AlgoritmaSM2.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk AlgoritmaSM2 - implementasi spaced repetition berbasis FSRS.
 */
@DisplayName("AlgoritmaSM2 Tests")
class AlgoritmaSM2Test {

    private AlgoritmaSM2 algoritma;

    @BeforeEach
    void setUp() {
        algoritma = new AlgoritmaSM2();
    }

    // ==================== Test Konstruktor ====================

    @Nested
    @DisplayName("Konstruktor Tests")
    class KonstruktorTests {

        @Test
        @DisplayName("Konstruktor default menggunakan parameter bawaan")
        void konstruktorDefault_menggunakanParameterBawaan() {
            AlgoritmaSM2 alg = new AlgoritmaSM2();
            assertNotNull(alg);
        }

        @Test
        @DisplayName("Konstruktor dengan bobot kustom berhasil")
        void konstruktorDenganBobotKustom_berhasil() {
            double[] bobotKustom = AlgoritmaSM2.PARAMETER_BAWAAN.clone();
            bobotKustom[0] = 0.5; // modifikasi satu parameter
            
            AlgoritmaSM2 alg = new AlgoritmaSM2(bobotKustom);
            assertNotNull(alg);
        }

        @Test
        @DisplayName("Konstruktor dengan bobot null melempar exception")
        void konstruktorDenganBobotNull_melemparException() {
            assertThrows(IllegalArgumentException.class, 
                () -> new AlgoritmaSM2(null));
        }

        @Test
        @DisplayName("Konstruktor dengan bobot kurang melempar exception")
        void konstruktorDenganBobotKurang_melemparException() {
            double[] bobotKurang = new double[5]; // kurang dari 21 parameter
            assertThrows(IllegalArgumentException.class, 
                () -> new AlgoritmaSM2(bobotKurang));
        }
    }

    // ==================== Test Pemetaan Rating ====================

    @Nested
    @DisplayName("Pemetaan Rating Tests")
    class PemetaanRatingTests {

        @ParameterizedTest(name = "Rating UI {0} -> FSRS {1}")
        @CsvSource({
            "1, 1",  // again
            "2, 2",  // hard
            "3, 3",  // good
            "4, 3",  // good (3 dan 4 sama-sama good)
            "5, 4"   // easy
        })
        @DisplayName("petaRatingFsrs memetakan rating UI ke FSRS dengan benar")
        void petaRatingFsrs_memetakanDenganBenar(int ratingUi, int expectedFsrs) {
            assertEquals(expectedFsrs, AlgoritmaSM2.petaRatingFsrs(ratingUi));
        }

        @Test
        @DisplayName("petaRatingFsrs membatasi rating di bawah 1")
        void petaRatingFsrs_membatasiDiBawah1() {
            assertEquals(1, AlgoritmaSM2.petaRatingFsrs(0));
            assertEquals(1, AlgoritmaSM2.petaRatingFsrs(-5));
        }

        @Test
        @DisplayName("petaRatingFsrs membatasi rating di atas 5")
        void petaRatingFsrs_membatasiDiAtas5() {
            assertEquals(4, AlgoritmaSM2.petaRatingFsrs(6));
            assertEquals(4, AlgoritmaSM2.petaRatingFsrs(100));
        }
    }

    // ==================== Test KondisiMemori ====================

    @Nested
    @DisplayName("KondisiMemori Tests")
    class KondisiMemoriTests {

        @Test
        @DisplayName("KondisiMemori.kosong() membuat kondisi dengan nilai 0")
        void kondisiKosong_membuatKondisiDenganNilai0() {
            KondisiMemori kosong = KondisiMemori.kosong();
            
            assertEquals(0.0, kosong.stabilitas());
            assertEquals(0.0, kosong.kesulitan());
            assertTrue(kosong.adalahKosong());
        }

        @Test
        @DisplayName("KondisiMemori dengan nilai tidak kosong")
        void kondisiDenganNilai_tidakKosong() {
            KondisiMemori kondisi = new KondisiMemori(5.0, 3.0);
            
            assertEquals(5.0, kondisi.stabilitas());
            assertEquals(3.0, kondisi.kesulitan());
            assertFalse(kondisi.adalahKosong());
        }

        @Test
        @DisplayName("KondisiMemori dengan stabilitas 0 dan kesulitan > 0 bukan kosong")
        void kondisiDenganStabilitas0DanKesulitanPositif_bukanKosong() {
            KondisiMemori kondisi = new KondisiMemori(0.0, 5.0);
            assertFalse(kondisi.adalahKosong());
        }
    }

    // ==================== Test Retrievability ====================

    @Nested
    @DisplayName("Retrievability Tests")
    class RetrievabilityTests {

        @Test
        @DisplayName("hitungRetrievability mengembalikan 0 untuk kondisi kosong")
        void hitungRetrievability_kondisiKosong_mengembalikan0() {
            double hasil = algoritma.hitungRetrievability(KondisiMemori.kosong(), 5);
            assertEquals(0.0, hasil);
        }

        @Test
        @DisplayName("hitungRetrievability mengembalikan 0 untuk kondisi null")
        void hitungRetrievability_kondisiNull_mengembalikan0() {
            double hasil = algoritma.hitungRetrievability(null, 5);
            assertEquals(0.0, hasil);
        }

        @Test
        @DisplayName("hitungRetrievability mengembalikan 1 untuk hari 0")
        void hitungRetrievability_hari0_mengembalikan1() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            double hasil = algoritma.hitungRetrievability(kondisi, 0);
            assertEquals(1.0, hasil);
        }

        @Test
        @DisplayName("hitungRetrievability menurun seiring waktu")
        void hitungRetrievability_menurunSeiringWaktu() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            
            double hari1 = algoritma.hitungRetrievability(kondisi, 1);
            double hari5 = algoritma.hitungRetrievability(kondisi, 5);
            double hari10 = algoritma.hitungRetrievability(kondisi, 10);
            
            assertTrue(hari1 > hari5, "Retrievability hari 1 harus > hari 5");
            assertTrue(hari5 > hari10, "Retrievability hari 5 harus > hari 10");
        }

        @Test
        @DisplayName("hitungRetrievability selalu antara 0 dan 1")
        void hitungRetrievability_selaluAntara0Dan1() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            
            for (int hari = 0; hari <= 365; hari += 30) {
                double hasil = algoritma.hitungRetrievability(kondisi, hari);
                assertTrue(hasil >= 0.0 && hasil <= 1.0, 
                    "Retrievability harus antara 0-1 untuk hari " + hari);
            }
        }
    }

    // ==================== Test Kalkulasi Interval ====================

    @Nested
    @DisplayName("Kalkulasi Interval Tests")
    class KalkulasiIntervalTests {

        @Test
        @DisplayName("hitungIntervalDariStabilitas menghasilkan interval positif")
        void hitungIntervalDariStabilitas_menghasilkanIntervalPositif() {
            double interval = algoritma.hitungIntervalDariStabilitas(10.0, 0.9);
            assertTrue(interval > 0, "Interval harus positif");
        }

        @Test
        @DisplayName("Stabilitas lebih tinggi menghasilkan interval lebih panjang")
        void stabilitasLebihTinggi_intervalLebihPanjang() {
            double intervalRendah = algoritma.hitungIntervalDariStabilitas(5.0, 0.9);
            double intervalTinggi = algoritma.hitungIntervalDariStabilitas(20.0, 0.9);
            
            assertTrue(intervalTinggi > intervalRendah, 
                "Stabilitas tinggi harus menghasilkan interval lebih panjang");
        }

        @Test
        @DisplayName("Retensi lebih tinggi menghasilkan interval lebih pendek")
        void retensiLebihTinggi_intervalLebihPendek() {
            double interval85 = algoritma.hitungIntervalDariStabilitas(10.0, 0.85);
            double interval95 = algoritma.hitungIntervalDariStabilitas(10.0, 0.95);
            
            assertTrue(interval85 > interval95, 
                "Retensi 85% harus menghasilkan interval lebih panjang dari 95%");
        }
    }

    // ==================== Test Keadaan Berikutnya ====================

    @Nested
    @DisplayName("hitungKeadaanBerikutnya Tests")
    class KeadaanBerikutnyaTests {

        @Test
        @DisplayName("hitungKeadaanBerikutnya dengan kondisi null/kosong berhasil")
        void hitungKeadaanBerikutnya_kondisiKosong_berhasil() {
            OpsiInterval opsi = algoritma.hitungKeadaanBerikutnya(
                null, 0.9, 0);
            
            assertNotNull(opsi);
            assertNotNull(opsi.ulang());
            assertNotNull(opsi.sulit());
            assertNotNull(opsi.baik());
            assertNotNull(opsi.mudah());
        }

        @Test
        @DisplayName("hitungKeadaanBerikutnya dengan kondisi valid berhasil")
        void hitungKeadaanBerikutnya_kondisiValid_berhasil() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            OpsiInterval opsi = algoritma.hitungKeadaanBerikutnya(kondisi, 0.9, 5);
            
            assertNotNull(opsi);
            assertTrue(opsi.baik().interval() > 0);
        }

        @Test
        @DisplayName("hitungKeadaanBerikutnya dengan retensi invalid melempar exception")
        void hitungKeadaanBerikutnya_retensiInvalid_melemparException() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            
            assertThrows(IllegalArgumentException.class, 
                () -> algoritma.hitungKeadaanBerikutnya(kondisi, 0, 5));
            assertThrows(IllegalArgumentException.class, 
                () -> algoritma.hitungKeadaanBerikutnya(kondisi, 1.5, 5));
            assertThrows(IllegalArgumentException.class, 
                () -> algoritma.hitungKeadaanBerikutnya(kondisi, -0.5, 5));
        }

        @Test
        @DisplayName("hitungKeadaanBerikutnya dengan hari negatif melempar exception")
        void hitungKeadaanBerikutnya_hariNegatif_melemparException() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            
            assertThrows(IllegalArgumentException.class, 
                () -> algoritma.hitungKeadaanBerikutnya(kondisi, 0.9, -1));
        }

        @Test
        @DisplayName("Rating 'mudah' menghasilkan interval terpanjang")
        void ratingMudah_intervalTerpanjang() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            OpsiInterval opsi = algoritma.hitungKeadaanBerikutnya(kondisi, 0.9, 5);
            
            assertTrue(opsi.mudah().interval() > opsi.baik().interval(),
                "Rating mudah harus menghasilkan interval lebih panjang dari baik");
            assertTrue(opsi.baik().interval() > opsi.sulit().interval(),
                "Rating baik harus menghasilkan interval lebih panjang dari sulit");
        }

        @Test
        @DisplayName("Rating 'ulang' menghasilkan interval terpendek")
        void ratingUlang_intervalTerpendek() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            OpsiInterval opsi = algoritma.hitungKeadaanBerikutnya(kondisi, 0.9, 5);
            
            assertTrue(opsi.ulang().interval() <= opsi.sulit().interval(),
                "Rating ulang harus menghasilkan interval terpendek atau sama dengan sulit");
        }
    }

    // ==================== Test OpsiInterval ====================

    @Nested
    @DisplayName("OpsiInterval Tests")
    class OpsiIntervalTests {

        @Test
        @DisplayName("OpsiInterval.pilih() mengembalikan opsi yang benar")
        void pilih_mengembalikanOpsiYangBenar() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            OpsiInterval opsi = algoritma.hitungKeadaanBerikutnya(kondisi, 0.9, 5);
            
            assertEquals(opsi.ulang(), opsi.pilih(1));
            assertEquals(opsi.sulit(), opsi.pilih(2));
            assertEquals(opsi.baik(), opsi.pilih(3));
            assertEquals(opsi.mudah(), opsi.pilih(4));
        }

        @Test
        @DisplayName("OpsiInterval.pilih() dengan rating default mengembalikan baik")
        void pilih_ratingDefault_mengembalikanBaik() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            OpsiInterval opsi = algoritma.hitungKeadaanBerikutnya(kondisi, 0.9, 5);
            
            // Rating selain 1,2,4 mengembalikan baik
            assertEquals(opsi.baik(), opsi.pilih(0));
            assertEquals(opsi.baik(), opsi.pilih(5));
            assertEquals(opsi.baik(), opsi.pilih(99));
        }
    }

    // ==================== Test RangeInterval ====================

    @Nested
    @DisplayName("RangeInterval Tests")
    class RangeIntervalTests {

        @Test
        @DisplayName("hitungRangeInterval menghasilkan range valid")
        void hitungRangeInterval_menghasilkanRangeValid() {
            RangeInterval range = algoritma.hitungRangeInterval(10.0, 0.9);
            
            assertTrue(range.minimum() > 0, "Minimum harus positif");
            assertTrue(range.ideal() >= range.minimum(), "Ideal >= minimum");
            assertTrue(range.maksimum() >= range.ideal(), "Maksimum >= ideal");
        }

        @Test
        @DisplayName("RangeInterval.dalamRange() bekerja dengan benar")
        void dalamRange_bekerja() {
            RangeInterval range = algoritma.hitungRangeInterval(10.0, 0.9);
            
            assertTrue(range.dalamRange(range.ideal()));
            assertTrue(range.dalamRange(range.minimum()));
            assertTrue(range.dalamRange(range.maksimum()));
            assertFalse(range.dalamRange(range.minimum() - 1));
            assertFalse(range.dalamRange(range.maksimum() + 1));
        }

        @Test
        @DisplayName("RangeInterval.toString() menghasilkan format yang benar")
        void toString_formatBenar() {
            RangeInterval range = algoritma.hitungRangeInterval(10.0, 0.9);
            String str = range.toString();
            
            assertNotNull(str);
            assertTrue(str.contains("hari"));
        }
    }

    // ==================== Test Perlu Review ====================

    @Nested
    @DisplayName("apakahPerluReview Tests")
    class PerluReviewTests {

        @Test
        @DisplayName("apakahPerluReview dengan kondisi null dan hari >= 1 mengembalikan true")
        void apakahPerluReview_kondisiNull_perluReview() {
            assertTrue(algoritma.apakahPerluReview(null, 1, 0.9));
            assertTrue(algoritma.apakahPerluReview(KondisiMemori.kosong(), 1, 0.9));
        }

        @Test
        @DisplayName("apakahPerluReview dengan kondisi null dan hari < 1 mengembalikan false")
        void apakahPerluReview_kondisiNullHari0_tidakPerluReview() {
            assertFalse(algoritma.apakahPerluReview(null, 0, 0.9));
        }

        @Test
        @DisplayName("apakahPerluReview berdasarkan retrievability vs target retensi")
        void apakahPerluReview_berdasarkanRetrievability() {
            KondisiMemori kondisi = new KondisiMemori(5.0, 5.0);
            
            // Baru saja review (hari 0) - tidak perlu
            assertFalse(algoritma.apakahPerluReview(kondisi, 0, 0.9));
            
            // Setelah waktu lama - perlu review (retrievability turun)
            assertTrue(algoritma.apakahPerluReview(kondisi, 30, 0.9));
        }
    }

    // ==================== Test Batch Processing ====================

    @Nested
    @DisplayName("Batch Processing Tests")
    class BatchProcessingTests {

        @Test
        @DisplayName("hitungKeadaanBerikutnyaBatch dengan daftar valid berhasil")
        void hitungBatch_daftarValid_berhasil() {
            java.util.List<KondisiMemori> daftarKondisi = java.util.List.of(
                new KondisiMemori(5.0, 3.0),
                new KondisiMemori(10.0, 5.0),
                new KondisiMemori(15.0, 4.0)
            );
            java.util.List<Long> daftarHari = java.util.List.of(3L, 5L, 7L);
            
            java.util.List<OpsiInterval> hasil = algoritma.hitungKeadaanBerikutnyaBatch(
                daftarKondisi, 0.9, daftarHari);
            
            assertEquals(3, hasil.size());
            for (OpsiInterval opsi : hasil) {
                assertNotNull(opsi);
            }
        }

        @Test
        @DisplayName("hitungKeadaanBerikutnyaBatch dengan ukuran berbeda melempar exception")
        void hitungBatch_ukuranBerbeda_melemparException() {
            java.util.List<KondisiMemori> daftarKondisi = java.util.List.of(
                new KondisiMemori(5.0, 3.0),
                new KondisiMemori(10.0, 5.0)
            );
            java.util.List<Long> daftarHari = java.util.List.of(3L);
            
            assertThrows(IllegalArgumentException.class, 
                () -> algoritma.hitungKeadaanBerikutnyaBatch(
                    daftarKondisi, 0.9, daftarHari));
        }
    }

    // ==================== Test Uncertainty ====================

    @Nested
    @DisplayName("Uncertainty Tests")
    class UncertaintyTests {

        @Test
        @DisplayName("hitungUncertainty dengan kondisi null/kosong mengembalikan 1.0")
        void hitungUncertainty_kondisiKosong_mengembalikan1() {
            assertEquals(1.0, algoritma.hitungUncertainty(null, 5));
            assertEquals(1.0, algoritma.hitungUncertainty(KondisiMemori.kosong(), 5));
        }

        @Test
        @DisplayName("hitungUncertainty selalu antara 0 dan 1")
        void hitungUncertainty_selaluAntara0Dan1() {
            KondisiMemori kondisi = new KondisiMemori(10.0, 5.0);
            
            for (int hari = 0; hari <= 100; hari += 10) {
                double uncertainty = algoritma.hitungUncertainty(kondisi, hari);
                assertTrue(uncertainty >= 0.0 && uncertainty <= 1.0,
                    "Uncertainty harus antara 0-1 untuk hari " + hari);
            }
        }
    }

    // ==================== Test Konversi dari SM-2 ====================

    @Nested
    @DisplayName("Konversi SM-2 Tests")
    class KonversiSM2Tests {

        @Test
        @DisplayName("kondisiAwalDariSm2 dengan parameter valid berhasil")
        void kondisiAwalDariSm2_parameterValid_berhasil() {
            // Parameter SM-2 tipikal
            double faktorKemudahan = 2.5;
            double interval = 10.0;
            double retensiSm2 = 0.9;
            
            KondisiMemori kondisi = algoritma.kondisiAwalDariSm2(
                faktorKemudahan, interval, retensiSm2);
            
            assertNotNull(kondisi);
            assertTrue(kondisi.stabilitas() > 0);
            assertTrue(kondisi.kesulitan() >= 1.0 && kondisi.kesulitan() <= 10.0);
        }

        @ParameterizedTest(name = "Faktor kemudahan {0}")
        @ValueSource(doubles = {1.3, 2.0, 2.5, 3.0})
        @DisplayName("kondisiAwalDariSm2 dengan berbagai faktor kemudahan")
        void kondisiAwalDariSm2_berbagaiFaktorKemudahan(double faktorKemudahan) {
            KondisiMemori kondisi = algoritma.kondisiAwalDariSm2(
                faktorKemudahan, 10.0, 0.9);
            
            assertNotNull(kondisi);
            assertFalse(kondisi.adalahKosong());
        }
    }
}
