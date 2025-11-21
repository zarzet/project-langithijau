package com.studyplanner.algoritma.fsrs;

/**
 * Representasi satu review kartu untuk training FSRS.
 * rating disimpan pada skala 1-4 sesuai tombol Again/Hard/Good/Easy.
 */
public class FSRSReview {
    private int rating;
    private int deltaT;

    public FSRSReview(int rating, int deltaT) {
        this.rating = rating;
        this.deltaT = deltaT;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getDeltaT() {
        return deltaT;
    }

    public void setDeltaT(int deltaT) {
        this.deltaT = deltaT;
    }
}
