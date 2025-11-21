package com.studyplanner.algoritma.fsrs;

/**
 * Hasil pelatihan FSRS tanpa simulator/benchmark, berisi bobot dan metrik sederhana.
 */
public class HasilPelatihanFSRS {
    private final double[] bobotTerbaik;
    private final double lossRataRata;
    private final double accuracy;

    public HasilPelatihanFSRS(double[] bobotTerbaik, double lossRataRata, double accuracy) {
        this.bobotTerbaik = bobotTerbaik;
        this.lossRataRata = lossRataRata;
        this.accuracy = accuracy;
    }

    public double[] getBobotTerbaik() {
        return bobotTerbaik;
    }

    public double getLossRataRata() {
        return lossRataRata;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
