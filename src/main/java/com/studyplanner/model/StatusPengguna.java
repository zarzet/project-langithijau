package com.studyplanner.model;

/**
 * Enum untuk status akun pengguna.
 */
public enum StatusPengguna {
    ACTIVE("active", "Aktif"),
    INACTIVE("inactive", "Tidak Aktif"),
    SUSPENDED("suspended", "Ditangguhkan");

    private final String kode;
    private final String namaDisplay;

    StatusPengguna(String kode, String namaDisplay) {
        this.kode = kode;
        this.namaDisplay = namaDisplay;
    }

    public String getKode() {
        return kode;
    }

    public String getNamaDisplay() {
        return namaDisplay;
    }

    /**
     * Mendapatkan StatusPengguna dari kode string.
     */
    public static StatusPengguna dariKode(String kode) {
        if (kode == null) return ACTIVE;
        for (StatusPengguna status : values()) {
            if (status.kode.equalsIgnoreCase(kode)) {
                return status;
            }
        }
        return ACTIVE; // default
    }
}
