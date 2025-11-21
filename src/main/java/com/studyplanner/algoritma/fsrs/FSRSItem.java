package com.studyplanner.algoritma.fsrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Satu kartu/topik dengan runtutan review historis berurutan.
 */
public class FSRSItem {
    private final List<FSRSReview> reviewList;

    public FSRSItem(List<FSRSReview> reviewList) {
        this.reviewList = new ArrayList<>(reviewList);
    }

    public List<FSRSReview> getReviewList() {
        return Collections.unmodifiableList(reviewList);
    }
}
