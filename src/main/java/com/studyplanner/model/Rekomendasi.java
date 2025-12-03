package com.studyplanner.model;

import java.time.LocalDateTime;

/**
 * Model untuk rekomendasi topik dari dosen ke mahasiswa.
 */
public class Rekomendasi {
    private int id;
    private int dosenId;
    private int mahasiswaId;
    private Integer idMataKuliah;
    private String namaTopik;
    private String deskripsi;
    private int prioritasSaran;
    private int kesulitanSaran;
    private String urlSumber;
    private StatusRekomendasi status;
    private LocalDateTime dibuatPada;

    // Data dari join
    private String namaDosen;
    private String namaMahasiswa;
    private String namaMataKuliah;

    public enum StatusRekomendasi {
        PENDING("pending", "Menunggu"),
        ACCEPTED("accepted", "Diterima"),
        DECLINED("declined", "Ditolak");

        private final String kode;
        private final String namaDisplay;

        StatusRekomendasi(String kode, String namaDisplay) {
            this.kode = kode;
            this.namaDisplay = namaDisplay;
        }

        public String getKode() {
            return kode;
        }

        public String getNamaDisplay() {
            return namaDisplay;
        }

        public static StatusRekomendasi dariKode(String kode) {
            if (kode == null) return PENDING;
            for (StatusRekomendasi s : values()) {
                if (s.kode.equalsIgnoreCase(kode)) {
                    return s;
                }
            }
            return PENDING;
        }
    }

    public Rekomendasi() {
        this.prioritasSaran = 3;
        this.kesulitanSaran = 3;
        this.status = StatusRekomendasi.PENDING;
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDosenId() {
        return dosenId;
    }

    public void setDosenId(int dosenId) {
        this.dosenId = dosenId;
    }

    public int getMahasiswaId() {
        return mahasiswaId;
    }

    public void setMahasiswaId(int mahasiswaId) {
        this.mahasiswaId = mahasiswaId;
    }

    public Integer getIdMataKuliah() {
        return idMataKuliah;
    }

    public void setIdMataKuliah(Integer idMataKuliah) {
        this.idMataKuliah = idMataKuliah;
    }

    public String getNamaTopik() {
        return namaTopik;
    }

    public void setNamaTopik(String namaTopik) {
        this.namaTopik = namaTopik;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getPrioritasSaran() {
        return prioritasSaran;
    }

    public void setPrioritasSaran(int prioritasSaran) {
        this.prioritasSaran = prioritasSaran;
    }

    public int getKesulitanSaran() {
        return kesulitanSaran;
    }

    public void setKesulitanSaran(int kesulitanSaran) {
        this.kesulitanSaran = kesulitanSaran;
    }

    public String getUrlSumber() {
        return urlSumber;
    }

    public void setUrlSumber(String urlSumber) {
        this.urlSumber = urlSumber;
    }

    public StatusRekomendasi getStatus() {
        return status;
    }

    public void setStatus(StatusRekomendasi status) {
        this.status = status;
    }

    public LocalDateTime getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(LocalDateTime dibuatPada) {
        this.dibuatPada = dibuatPada;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public String getNamaMataKuliah() {
        return namaMataKuliah;
    }

    public void setNamaMataKuliah(String namaMataKuliah) {
        this.namaMataKuliah = namaMataKuliah;
    }

    public boolean isPending() {
        return status == StatusRekomendasi.PENDING;
    }

    public boolean isAccepted() {
        return status == StatusRekomendasi.ACCEPTED;
    }

    @Override
    public String toString() {
        return namaTopik + " (" + status.getNamaDisplay() + ")";
    }
}
