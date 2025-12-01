package com.studyplanner.eksepsi;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Eksepsi yang dilempar ketika validasi data gagal.
 */
public class EksepsiValidasi extends RuntimeException {

    private final List<String> kesalahanValidasi;

    public EksepsiValidasi(String pesan) {
        super(pesan);
        this.kesalahanValidasi = Collections.singletonList(pesan);
    }

    public EksepsiValidasi(List<String> kesalahan) {
        super(String.join("; ", kesalahan));
        this.kesalahanValidasi = new ArrayList<>(kesalahan);
    }

    public EksepsiValidasi(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
        this.kesalahanValidasi = Collections.singletonList(pesan);
    }

    public List<String> getKesalahanValidasi() {
        return Collections.unmodifiableList(kesalahanValidasi);
    }

    public boolean punyaBanyakKesalahan() {
        return kesalahanValidasi.size() > 1;
    }
}
