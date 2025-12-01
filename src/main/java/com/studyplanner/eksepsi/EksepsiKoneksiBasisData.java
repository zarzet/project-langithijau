package com.studyplanner.eksepsi;

/**
 * Eksepsi yang dilempar ketika gagal membuat koneksi ke basis data.
 */
public class EksepsiKoneksiBasisData extends EksepsiAksesBasisData {

    public EksepsiKoneksiBasisData(String pesan) {
        super(pesan, "CONNECTION_ERROR");
    }

    public EksepsiKoneksiBasisData(String pesan, Throwable penyebab) {
        super(pesan, "CONNECTION_ERROR", penyebab);
    }
}
