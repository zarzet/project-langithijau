package com.studyplanner.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk data dosen pembimbing.
 */
public class Dosen {
    private int id;
    private int userId;
    private String nip;
    private int maxMahasiswa;
    private LocalDateTime dibuatPada;

    // Data dari join dengan users
    private String nama;
    private String email;
    private StatusPengguna status;

    // Relasi
    private List<Mahasiswa> daftarMahasiswa;
    private int jumlahMahasiswa;

    public Dosen() {
        this.maxMahasiswa = 30;
        this.daftarMahasiswa = new ArrayList<>();
    }

    public Dosen(int id, int userId, String nip) {
        this();
        this.id = id;
        this.userId = userId;
        this.nip = nip;
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public int getMaxMahasiswa() {
        return maxMahasiswa;
    }

    public void setMaxMahasiswa(int maxMahasiswa) {
        this.maxMahasiswa = maxMahasiswa;
    }

    public LocalDateTime getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(LocalDateTime dibuatPada) {
        this.dibuatPada = dibuatPada;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StatusPengguna getStatus() {
        return status;
    }

    public void setStatus(StatusPengguna status) {
        this.status = status;
    }

    public List<Mahasiswa> getDaftarMahasiswa() {
        return daftarMahasiswa;
    }

    public void setDaftarMahasiswa(List<Mahasiswa> daftarMahasiswa) {
        this.daftarMahasiswa = daftarMahasiswa;
    }

    public int getJumlahMahasiswa() {
        return jumlahMahasiswa;
    }

    public void setJumlahMahasiswa(int jumlahMahasiswa) {
        this.jumlahMahasiswa = jumlahMahasiswa;
    }

    /**
     * Cek apakah dosen masih bisa menerima mahasiswa baru.
     */
    public boolean bisaMenerimaMahasiswa() {
        return jumlahMahasiswa < maxMahasiswa;
    }

    /**
     * Hitung sisa kuota mahasiswa.
     */
    public int sisaKuota() {
        return Math.max(0, maxMahasiswa - jumlahMahasiswa);
    }

    @Override
    public String toString() {
        return nama + " (" + nip + ")";
    }
}
