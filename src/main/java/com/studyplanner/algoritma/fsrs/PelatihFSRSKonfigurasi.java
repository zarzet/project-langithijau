package com.studyplanner.algoritma.fsrs;

/**
 * Konfigurasi pelatihan: iterasi optimasi, sampling noise, dan retensi target.
 */
public class PelatihFSRSKonfigurasi {
    private final int iterasiMaks;
    private final double langkahAwal;
    private final long seed;
    private final double retensiDiinginkan;
    private final double rasioTrain;

    public PelatihFSRSKonfigurasi(int iterasiMaks,
                                  double langkahAwal,
                                  long seed,
                                  double retensiDiinginkan,
                                  double rasioTrain) {
        this.iterasiMaks = iterasiMaks;
        this.langkahAwal = langkahAwal;
        this.seed = seed;
        this.retensiDiinginkan = retensiDiinginkan;
        this.rasioTrain = rasioTrain;
    }

    public static PelatihFSRSKonfigurasi bawaan() {
        return new PelatihFSRSKonfigurasi(
                120,
                0.12,
                42L,
                0.9,
                0.85
        );
    }

    public int getIterasiMaks() {
        return iterasiMaks;
    }

    public double getLangkahAwal() {
        return langkahAwal;
    }

    public long getSeed() {
        return seed;
    }

    public double getRetensiDiinginkan() {
        return retensiDiinginkan;
    }

    public double getRasioTrain() {
        return rasioTrain;
    }
}
