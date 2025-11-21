package com.studyplanner.algoritma;

import java.util.Arrays;

/**
 * Implementasi Java untuk algoritma Free Spaced Repetition Scheduler (FSRS).
 * Porting ini mengikuti referensi Rust yang ada di folder {@code referensi}.
 * Semua nama variabel dipertahankan dalam bahasa Indonesia untuk konsistensi kodebasis.
 */
public class AlgoritmaFSRS {

    public static final double[] PARAMETER_BAWAAN = {
            0.212,
            1.2931,
            2.3065,
            8.2956,
            6.4133,
            0.8334,
            3.0194,
            0.001,
            1.8722,
            0.1666,
            0.796,
            1.4835,
            0.0614,
            0.2629,
            1.6483,
            0.6014,
            1.8729,
            0.5425,
            0.0912,
            0.0658,
            0.1542, // FSRS6_DEFAULT_DECAY
    };

    private static final double S_MIN = 0.001;
    private static final double S_MAX = 36500.0;
    private static final double D_MIN = 1.0;
    private static final double D_MAX = 10.0;

    private final double[] bobot;

    public AlgoritmaFSRS() {
        this(PARAMETER_BAWAAN);
    }

    public AlgoritmaFSRS(double[] bobot) {
        if (bobot == null || bobot.length < 21) {
            throw new IllegalArgumentException("Parameter FSRS harus berisi minimal 21 nilai bobot.");
        }
        this.bobot = Arrays.copyOf(bobot, 21);
    }

    public OpsiInterval hitungKeadaanBerikutnya(KondisiMemori kondisiSaatIni,
                                                double retensiDiinginkan,
                                                long hariSejakUlasan) {
        KondisiMemori kondisi = kondisiSaatIni != null ? kondisiSaatIni : KondisiMemori.kosong();
        int urutanKe = kondisiSaatIni == null ? 0 : 1;

        KeadaanKartu ulang = hitungLangkah(kondisi, retensiDiinginkan, hariSejakUlasan, 1, urutanKe);
        KeadaanKartu sulit = hitungLangkah(kondisi, retensiDiinginkan, hariSejakUlasan, 2, urutanKe);
        KeadaanKartu baik = hitungLangkah(kondisi, retensiDiinginkan, hariSejakUlasan, 3, urutanKe);
        KeadaanKartu mudah = hitungLangkah(kondisi, retensiDiinginkan, hariSejakUlasan, 4, urutanKe);

        return new OpsiInterval(ulang, sulit, baik, mudah);
    }

    public double hitungRetrievability(KondisiMemori kondisiSaatIni, double hariSejakUlasan) {
        if (kondisiSaatIni == null) {
            return 0.0;
        }
        double stabilitas = clamp(kondisiSaatIni.getStabilitas(), S_MIN, S_MAX);
        double decay = -bobot[20];
        double faktorPelupaan = Math.exp(Math.log(0.9) / decay) - 1.0;
        return Math.pow((hariSejakUlasan / stabilitas) * faktorPelupaan + 1.0, decay);
    }

    public KondisiMemori kondisiAwalDariSm2(double faktorKemudahan, double interval, double retensiSm2) {
        double decay = -bobot[20];
        double faktor = Math.pow(0.9, 1.0 / decay) - 1.0;
        double stabilitas = Math.max(interval, S_MIN) * faktor / (Math.pow(retensiSm2, 1.0 / decay) - 1.0);

        double w8 = bobot[8];
        double w9 = bobot[9];
        double w10 = bobot[10];
        double penyebut = Math.exp(w8) * Math.pow(stabilitas, -w9) * Math.expm1((1.0 - retensiSm2) * w10);
        double kesulitan = 11.0 - (faktorKemudahan - 1.0) / penyebut;

        if (!Double.isFinite(stabilitas) || !Double.isFinite(kesulitan)) {
            throw new IllegalArgumentException("Input SM-2 tidak valid untuk konversi FSRS.");
        }

        return new KondisiMemori(clamp(stabilitas, S_MIN, S_MAX), clamp(kesulitan, D_MIN, D_MAX));
    }

    public double hitungIntervalDariStabilitas(double stabilitas, double retensiDiinginkan) {
        double decay = -bobot[20];
        double faktor = Math.exp(Math.log(0.9) / decay) - 1.0;
        return stabilitas / faktor * (Math.pow(retensiDiinginkan, 1.0 / decay) - 1.0);
    }

    public static int petaRatingPenggunaKeFsrs(int ratingPerforma) {
        int rating = Math.min(5, Math.max(1, ratingPerforma));
        if (rating == 1) {
            return 1; // Again
        } else if (rating == 2) {
            return 2; // Hard
        } else if (rating == 5) {
            return 4; // Easy
        }
        return 3; // Good untuk 3-4
    }

    private KeadaanKartu hitungLangkah(KondisiMemori kondisiSaatIni,
                                       double retensiDiinginkan,
                                       long hariSejakUlasan,
                                       int ratingFsrs,
                                       int urutanKe) {
        double lastS = clamp(kondisiSaatIni.getStabilitas(), S_MIN, S_MAX);
        double lastD = clamp(kondisiSaatIni.getKesulitan(), D_MIN, D_MAX);

        double retrievability = hitungRetrievability(
                new KondisiMemori(lastS, lastD),
                Math.max(0, hariSejakUlasan));

        double stabilitasSukses = stabilitasSetelahSukses(lastS, lastD, retrievability, ratingFsrs);
        double stabilitasGagal = stabilitasSetelahGagal(lastS, lastD, retrievability);
        double stabilitasPendek = stabilitasJangkaPendek(lastS, ratingFsrs);

        double stabilitasBaru = ratingFsrs == 1 ? stabilitasGagal : stabilitasSukses;
        if (hariSejakUlasan == 0) {
            stabilitasBaru = stabilitasPendek;
        }

        double kesulitanBaru = kesulitanBerikutnya(lastD, ratingFsrs);
        kesulitanBaru = meanReversion(kesulitanBaru);
        kesulitanBaru = clamp(kesulitanBaru, D_MIN, D_MAX);

        if (urutanKe == 0 && kondisiSaatIni.adalahKosong()) {
            double ratingTerbatas = clamp(ratingFsrs, 1, 4);
            stabilitasBaru = stabilitasAwal(ratingTerbatas);
            kesulitanBaru = kesulitanAwal(ratingTerbatas);
        }

        stabilitasBaru = clamp(stabilitasBaru, S_MIN, S_MAX);
        double interval = hitungIntervalDariStabilitas(stabilitasBaru, retensiDiinginkan);

        KondisiMemori kondisiMemori = new KondisiMemori(stabilitasBaru, kesulitanBaru);
        return new KeadaanKartu(kondisiMemori, interval);
    }

    private double stabilitasSetelahSukses(double stabilitasLama,
                                           double kesulitanLama,
                                           double retrievability,
                                           int ratingFsrs) {
        double penaltiSulit = ratingFsrs == 2 ? bobot[15] : 1.0;
        double bonusMudah = ratingFsrs == 4 ? bobot[16] : 1.0;

        double faktorInti = Math.exp(bobot[8]) *
                (-kesulitanLama + 11.0) *
                Math.pow(stabilitasLama, -bobot[9]) *
                (Math.exp((1.0 - retrievability) * bobot[10]) - 1.0);

        return stabilitasLama * (faktorInti * penaltiSulit * bonusMudah + 1.0);
    }

    private double stabilitasSetelahGagal(double stabilitasLama,
                                          double kesulitanLama,
                                          double retrievability) {
        double baru = bobot[11] *
                Math.pow(kesulitanLama, -bobot[12]) *
                (Math.pow(stabilitasLama + 1.0, bobot[13]) - 1.0) *
                Math.exp((1.0 - retrievability) * bobot[14]);
        double batasBawah = stabilitasLama / Math.exp(bobot[17] * bobot[18]);
        return Math.max(baru, batasBawah);
    }

    private double stabilitasJangkaPendek(double stabilitasLama, int ratingFsrs) {
        double sinc = Math.exp(bobot[17] * (ratingFsrs - 3.0 + bobot[18])) *
                Math.pow(stabilitasLama, -bobot[19]);
        if (ratingFsrs >= 2) {
            sinc = Math.max(sinc, 1.0);
        }
        return stabilitasLama * sinc;
    }

    private double kesulitanAwal(double ratingFsrs) {
        return bobot[4] - Math.exp(bobot[5] * (ratingFsrs - 1.0)) + 1.0;
    }

    private double stabilitasAwal(double ratingFsrs) {
        int indeks = (int) ratingFsrs - 1;
        return bobot[Math.max(0, Math.min(indeks, 3))];
    }

    private double linearDamping(double deltaKesulitan, double kesulitanLama) {
        return (10.0 - kesulitanLama) * deltaKesulitan / 9.0;
    }

    private double kesulitanBerikutnya(double kesulitanLama, double ratingFsrs) {
        double delta = -bobot[6] * (ratingFsrs - 3.0);
        return kesulitanLama + linearDamping(delta, kesulitanLama);
    }

    private double meanReversion(double kesulitanBaru) {
        double basis = kesulitanAwal(4.0);
        return bobot[7] * (basis - kesulitanBaru) + kesulitanBaru;
    }

    private double clamp(double nilai, double min, double max) {
        return Math.max(min, Math.min(max, nilai));
    }

    public record KondisiMemori(double stabilitas, double kesulitan) {
        public static KondisiMemori kosong() {
            return new KondisiMemori(0.0, 0.0);
        }

        boolean adalahKosong() {
            return stabilitas == 0.0 && kesulitan == 0.0;
        }

        public double getStabilitas() {
            return stabilitas;
        }

        public double getKesulitan() {
            return kesulitan;
        }
    }

    public record KeadaanKartu(KondisiMemori kondisiMemori, double interval) {
        public KondisiMemori getKondisiMemori() {
            return kondisiMemori;
        }

        public double getInterval() {
            return interval;
        }
    }

    public static class OpsiInterval {
        private final KeadaanKartu ulang;
        private final KeadaanKartu sulit;
        private final KeadaanKartu baik;
        private final KeadaanKartu mudah;

        public OpsiInterval(KeadaanKartu ulang, KeadaanKartu sulit, KeadaanKartu baik, KeadaanKartu mudah) {
            this.ulang = ulang;
            this.sulit = sulit;
            this.baik = baik;
            this.mudah = mudah;
        }

        public KeadaanKartu pilih(int ratingFsrs) {
            return switch (ratingFsrs) {
                case 1 -> ulang;
                case 2 -> sulit;
                case 4 -> mudah;
                default -> baik;
            };
        }

        public KeadaanKartu getUlang() {
            return ulang;
        }

        public KeadaanKartu getSulit() {
            return sulit;
        }

        public KeadaanKartu getBaik() {
            return baik;
        }

        public KeadaanKartu getMudah() {
            return mudah;
        }
    }
}
