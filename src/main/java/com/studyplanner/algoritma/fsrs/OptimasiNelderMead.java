package com.studyplanner.algoritma.fsrs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Optimasi Nelder-Mead sederhana untuk meminimalkan fungsi biaya tanpa turunan.
 */
public class OptimasiNelderMead {

    private final double alpha; // refleksi
    private final double gamma; // ekspansi
    private final double rho;   // kontraksi
    private final double sigma; // pengecilan

    public OptimasiNelderMead() {
        this(1.0, 2.0, 0.5, 0.5);
    }

    public OptimasiNelderMead(double alpha, double gamma, double rho, double sigma) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.rho = rho;
        this.sigma = sigma;
    }

    public double[] optimasi(double[] titikAwal, double langkahAwal, int iterMaks, FungsiBiaya biaya) {
        int n = titikAwal.length;
        double[][] simplex = new double[n + 1][n];
        double[] biayaSimplex = new double[n + 1];

        // inisialisasi simplex
        simplex[0] = Arrays.copyOf(titikAwal, n);
        biayaSimplex[0] = biaya.hitung(simplex[0]);
        for (int i = 1; i <= n; i++) {
            double[] titik = Arrays.copyOf(titikAwal, n);
            titik[i - 1] += langkahAwal;
            simplex[i] = titik;
            biayaSimplex[i] = biaya.hitung(titik);
        }

        for (int iter = 0; iter < iterMaks; iter++) {
            urutkanBerdasarkanBiaya(simplex, biayaSimplex);

            double[] terbaik = simplex[0];
            double[] terburuk = simplex[n];
            double[] centroid = centroid(simplex, n);

            // refleksi
            double[] refleksi = transform(centroid, terburuk, alpha, true);
            double biayaRefleksi = biaya.hitung(refleksi);

            if (biayaRefleksi < biayaSimplex[0]) {
                // ekspansi
                double[] ekspansi = transform(centroid, terburuk, gamma, true);
                double biayaEkspansi = biaya.hitung(ekspansi);
                if (biayaEkspansi < biayaRefleksi) {
                    simplex[n] = ekspansi;
                    biayaSimplex[n] = biayaEkspansi;
                } else {
                    simplex[n] = refleksi;
                    biayaSimplex[n] = biayaRefleksi;
                }
            } else if (biayaRefleksi < biayaSimplex[n - 1]) {
                simplex[n] = refleksi;
                biayaSimplex[n] = biayaRefleksi;
            } else {
                // kontraksi
                boolean kontraksiLuar = biayaRefleksi < biayaSimplex[n];
                double[] kontraksi = transform(
                        centroid,
                        kontraksiLuar ? refleksi : terburuk,
                        rho,
                        kontraksiLuar);
                double biayaKontraksi = biaya.hitung(kontraksi);
                if (biayaKontraksi < (kontraksiLuar ? biayaRefleksi : biayaSimplex[n])) {
                    simplex[n] = kontraksi;
                    biayaSimplex[n] = biayaKontraksi;
                } else {
                    // pengecilan
                    for (int i = 1; i <= n; i++) {
                        simplex[i] = gabung(terbaik, simplex[i], sigma);
                        biayaSimplex[i] = biaya.hitung(simplex[i]);
                    }
                }
            }
        }

        urutkanBerdasarkanBiaya(simplex, biayaSimplex);
        return simplex[0];
    }

    private void urutkanBerdasarkanBiaya(double[][] simplex, double[] biaya) {
        List<Integer> urutan = IntStream.range(0, simplex.length)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> biaya[i]))
                .collect(Collectors.toList());

        double[][] simplexBaru = new double[simplex.length][];
        double[] biayaBaru = new double[biaya.length];
        for (int i = 0; i < urutan.size(); i++) {
            simplexBaru[i] = simplex[urutan.get(i)];
            biayaBaru[i] = biaya[urutan.get(i)];
        }
        System.arraycopy(simplexBaru, 0, simplex, 0, simplex.length);
        System.arraycopy(biayaBaru, 0, biaya, 0, biaya.length);
    }

    private double[] centroid(double[][] simplex, int n) {
        double[] c = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[j] += simplex[i][j];
            }
        }
        for (int j = 0; j < n; j++) {
            c[j] /= n;
        }
        return c;
    }

    private double[] transform(double[] centroid, double[] titik, double faktor, boolean reflektif) {
        double[] hasil = new double[centroid.length];
        for (int i = 0; i < centroid.length; i++) {
            double diff = reflektif ? (centroid[i] - titik[i]) : (titik[i] - centroid[i]);
            hasil[i] = centroid[i] + faktor * diff;
        }
        return hasil;
    }

    private double[] gabung(double[] terbaik, double[] titik, double faktor) {
        double[] hasil = new double[terbaik.length];
        for (int i = 0; i < terbaik.length; i++) {
            hasil[i] = terbaik[i] + faktor * (titik[i] - terbaik[i]);
        }
        return hasil;
    }

    @FunctionalInterface
    public interface FungsiBiaya {
        double hitung(double[] bobot);
    }
}
