package com.studyplanner.eksepsi;

/**
 * Eksepsi dasar untuk semua kesalahan akses basis data.
 * Menggantikan SQLException mentah yang dilempar ke layer atas.
 */
public class EksepsiAksesBasisData extends RuntimeException {

    private final String kodeKesalahan;

    public EksepsiAksesBasisData(String pesan) {
        super(pesan);
        this.kodeKesalahan = "DB_ERROR";
    }

    public EksepsiAksesBasisData(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
        this.kodeKesalahan = "DB_ERROR";
    }

    public EksepsiAksesBasisData(String pesan, String kodeKesalahan) {
        super(pesan);
        this.kodeKesalahan = kodeKesalahan;
    }

    public EksepsiAksesBasisData(String pesan, String kodeKesalahan, Throwable penyebab) {
        super(pesan, penyebab);
        this.kodeKesalahan = kodeKesalahan;
    }

    public String getKodeKesalahan() {
        return kodeKesalahan;
    }
}
