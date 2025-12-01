package com.studyplanner.eksepsi;

/**
 * Eksepsi yang dilempar ketika mencoba menyimpan data yang sudah ada (duplikat).
 */
public class EksepsiDuplikat extends EksepsiAksesBasisData {

    private final String namaField;
    private final Object nilaiField;

    public EksepsiDuplikat(String namaField, Object nilaiField) {
        super(String.format("Data dengan %s '%s' sudah ada", namaField, nilaiField), "DUPLICATE_ENTRY");
        this.namaField = namaField;
        this.nilaiField = nilaiField;
    }

    public EksepsiDuplikat(String pesan) {
        super(pesan, "DUPLICATE_ENTRY");
        this.namaField = null;
        this.nilaiField = null;
    }

    public String getNamaField() {
        return namaField;
    }

    public Object getNilaiField() {
        return nilaiField;
    }
}
