package com.studyplanner.model;

/**
 * Enum untuk role pengguna dalam sistem.
 */
public enum RolePengguna {
    MAHASISWA("mahasiswa", "Mahasiswa"),
    DOSEN("dosen", "Dosen Pembimbing"),
    ADMIN("admin", "Administrator");

    private final String kode;
    private final String namaDisplay;

    RolePengguna(String kode, String namaDisplay) {
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
     * Mendapatkan RolePengguna dari kode string.
     */
    public static RolePengguna dariKode(String kode) {
        if (kode == null) return MAHASISWA;
        for (RolePengguna role : values()) {
            if (role.kode.equalsIgnoreCase(kode)) {
                return role;
            }
        }
        return MAHASISWA; // default
    }
}
