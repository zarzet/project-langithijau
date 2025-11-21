package com.studyplanner.algoritma.fsrs;

/**
 * Item dengan bobot untuk pelatihan (weighting recency/konstan).
 */
public class WeightedFSRSItem {
    private final FSRSItem item;
    private final double bobot;

    public WeightedFSRSItem(FSRSItem item, double bobot) {
        this.item = item;
        this.bobot = bobot;
    }

    public FSRSItem getItem() {
        return item;
    }

    public double getBobot() {
        return bobot;
    }
}
