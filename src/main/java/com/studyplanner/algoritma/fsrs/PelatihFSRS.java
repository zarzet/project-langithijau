package com.studyplanner.algoritma.fsrs;

import com.studyplanner.algoritma.AlgoritmaFSRS;
import com.studyplanner.algoritma.AlgoritmaFSRS.KondisiMemori;
import com.studyplanner.algoritma.AlgoritmaFSRS.OpsiInterval;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Pelatih FSRS tanpa simulator/benchmark.
 * Menggunakan Nelder-Mead untuk meminimalkan log-loss berbobot (tanpa simulator/benchmark).
 */
public class PelatihFSRS {

    public HasilPelatihanFSRS latih(List<FSRSItem> data) {
        return latih(data, PelatihFSRSKonfigurasi.bawaan());
    }

    public HasilPelatihanFSRS latih(List<FSRSItem> data, PelatihFSRSKonfigurasi konfigurasi) {
        Objects.requireNonNull(data, "Dataset tidak boleh null");
        if (data.isEmpty()) {
            return new HasilPelatihanFSRS(AlgoritmaFSRS.PARAMETER_BAWAAN.clone(), 0.0, 0.0);
        }

        UtilitasFSRS.DatasetTerbagi terbagi = UtilitasFSRS.bagiTrainTest(data, konfigurasi.getRasioTrain());
        List<WeightedFSRSItem> train = UtilitasFSRS.bobotRecency(terbagi.train());
        List<WeightedFSRSItem> test = UtilitasFSRS.bobotKonstan(terbagi.test());

        Random rng = new Random(konfigurasi.getSeed());
        double[] bobotAwal = jitter(AlgoritmaFSRS.PARAMETER_BAWAAN, konfigurasi.getLangkahAwal(), rng);
        OptimasiNelderMead optimizer = new OptimasiNelderMead();
        double[] bobotTerbaik = optimizer.optimasi(
                bobotAwal,
                konfigurasi.getLangkahAwal(),
                konfigurasi.getIterasiMaks(),
                w -> evaluasiLoss(new AlgoritmaFSRS(w), train, konfigurasi.getRetensiDiinginkan()));

        AlgoritmaFSRS modelTerbaik = new AlgoritmaFSRS(bobotTerbaik);
        double lossTest = test.isEmpty()
                ? hitungLossRata(modelTerbaik, train, konfigurasi.getRetensiDiinginkan())
                : evaluasiLoss(modelTerbaik, test, konfigurasi.getRetensiDiinginkan());
        double akurasi = test.isEmpty()
                ? hitungAkurasi(modelTerbaik, train, konfigurasi.getRetensiDiinginkan())
                : hitungAkurasi(modelTerbaik, test, konfigurasi.getRetensiDiinginkan());

        return new HasilPelatihanFSRS(
                bobotTerbaik.clone(),
                lossTest,
                akurasi
        );
    }

    private double evaluasiLoss(AlgoritmaFSRS model, List<WeightedFSRSItem> data, double retensi) {
        double totalLoss = 0.0;
        double totalBobot = 0.0;

        for (WeightedFSRSItem wItem : data) {
            totalLoss += hitungLossItem(model, wItem.getItem(), retensi) * wItem.getBobot();
            totalBobot += wItem.getBobot() * wItem.getItem().getReviewList().size();
        }

        return totalBobot == 0.0 ? 0.0 : totalLoss / totalBobot;
    }

    private double hitungLossRata(AlgoritmaFSRS model, List<WeightedFSRSItem> data, double retensi) {
        return evaluasiLoss(model, data, retensi);
    }

    private double hitungLossItem(AlgoritmaFSRS model, FSRSItem item, double retensi) {
        KondisiMemori kondisi = null;
        double loss = 0.0;

        for (FSRSReview rv : item.getReviewList()) {
            int rating = clamp(rv.getRating(), 1, 4);
            int deltaT = Math.max(0, rv.getDeltaT());

            double probBenar = clamp(model.hitungRetrievability(kondisi, deltaT), 1e-5, 1.0 - 1e-5);
            int label = rating >= 3 ? 1 : 0;
            loss += -label * Math.log(probBenar) - (1 - label) * Math.log(1.0 - probBenar);

            OpsiInterval opsi = model.hitungKeadaanBerikutnya(kondisi, retensi, deltaT);
            kondisi = opsi.pilih(rating).getKondisiMemori();
        }
        return loss;
    }

    private double hitungAkurasi(AlgoritmaFSRS model, List<WeightedFSRSItem> data, double retensi) {
        double benar = 0.0;
        double total = 0.0;
        for (WeightedFSRSItem wItem : data) {
            KondisiMemori kondisi = null;
            for (FSRSReview rv : wItem.getItem().getReviewList()) {
                int rating = clamp(rv.getRating(), 1, 4);
                int delta = Math.max(0, rv.getDeltaT());
                double probBenar = model.hitungRetrievability(kondisi, delta);
                int pred = probBenar >= 0.5 ? 1 : 0;
                int label = rating >= 3 ? 1 : 0;
                if (pred == label) {
                    benar += wItem.getBobot();
                }
                total += wItem.getBobot();

                OpsiInterval opsi = model.hitungKeadaanBerikutnya(kondisi, retensi, delta);
                kondisi = opsi.pilih(rating).getKondisiMemori();
            }
        }
        return total == 0.0 ? 0.0 : benar / total;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double[] jitter(double[] sumber, double skala, Random rng) {
        double[] hasil = new double[sumber.length];
        for (int i = 0; i < sumber.length; i++) {
            hasil[i] = sumber[i] + (rng.nextDouble() * 2 - 1) * skala;
        }
        return hasil;
    }
}
