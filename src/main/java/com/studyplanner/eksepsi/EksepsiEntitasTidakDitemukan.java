package com.studyplanner.eksepsi;

/**
 * Eksepsi yang dilempar ketika entitas yang dicari tidak ditemukan di basis data.
 */
public class EksepsiEntitasTidakDitemukan extends EksepsiAksesBasisData {

    private final String namaEntitas;
    private final Object idEntitas;

    public EksepsiEntitasTidakDitemukan(String namaEntitas, Object idEntitas) {
        super(String.format("%s dengan ID '%s' tidak ditemukan", namaEntitas, idEntitas), "ENTITY_NOT_FOUND");
        this.namaEntitas = namaEntitas;
        this.idEntitas = idEntitas;
    }

    public EksepsiEntitasTidakDitemukan(String pesan) {
        super(pesan, "ENTITY_NOT_FOUND");
        this.namaEntitas = null;
        this.idEntitas = null;
    }

    public String getNamaEntitas() {
        return namaEntitas;
    }

    public Object getIdEntitas() {
        return idEntitas;
    }
}
