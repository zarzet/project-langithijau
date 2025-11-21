package com.studyplanner.algoritma.fsrs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Fungsi utilitas untuk konversi dataset dan pembobotan.
 */
public final class UtilitasFSRS {
    private UtilitasFSRS() {}

    /**
     * Membuat bobot konstan (1.0) untuk seluruh item.
     */
    public static List<WeightedFSRSItem> bobotKonstan(List<FSRSItem> data) {
        return data.stream().map(item -> new WeightedFSRSItem(item, 1.0)).collect(Collectors.toList());
    }

    /**
     * Membuat bobot turun berdasarkan posisi (recency). Item lebih baru mendapat bobot lebih tinggi.
     */
    public static List<WeightedFSRSItem> bobotRecency(List<FSRSItem> data) {
        int n = data.size();
        if (n == 0) {
            return List.of();
        }
        double total = IntStream.range(1, n + 1).sum();
        List<WeightedFSRSItem> hasil = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double bobot = (n - i) / total;
            hasil.add(new WeightedFSRSItem(data.get(i), bobot));
        }
        return hasil;
    }

    /**
     * Membagi dataset terurut waktu menjadi train/test dengan rasio.
     */
    public static DatasetTerbagi bagiTrainTest(List<FSRSItem> data, double rasioTrain) {
        if (data.isEmpty()) {
            return new DatasetTerbagi(List.of(), List.of());
        }
        int batas = Math.max(1, (int) Math.round(data.size() * rasioTrain));
        List<FSRSItem> train = data.subList(0, Math.min(batas, data.size()));
        List<FSRSItem> test = data.subList(Math.min(batas, data.size()), data.size());
        return new DatasetTerbagi(train, test);
    }

    public record DatasetTerbagi(List<FSRSItem> train, List<FSRSItem> test) {}
}
