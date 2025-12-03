package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk data mahasiswa.
 */
public class Mahasiswa {
    private int id;
    private int userId;
    private String nim;
    private int semester;
    private Integer dosenId;
    private LocalDateTime dibuatPada;

    // Data dari join dengan users
    private String nama;
    private String email;
    private StatusPengguna status;
    private LocalDateTime loginTerakhir;

    // Data dari join dengan dosen
    private String namaDosen;

    // Statistik (untuk dashboard dosen)
    private int jumlahMataKuliah;
    private int jumlahTopik;
    private int topikDikuasai;
    private double progressKeseluruhan;
    private int runtutanBelajar;
    private int sesiMingguIni;
    private double rataRataPerforma;
    private LocalDate aktivitasTerakhir;

    public Mahasiswa() {
        this.semester = 1;
    }

    public Mahasiswa(int id, int userId, String nim, int semester) {
        this();
        this.id = id;
        this.userId = userId;
        this.nim = nim;
        this.semester = semester;
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

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Integer getDosenId() {
        return dosenId;
    }

    public void setDosenId(Integer dosenId) {
        this.dosenId = dosenId;
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

    public LocalDateTime getLoginTerakhir() {
        return loginTerakhir;
    }

    public void setLoginTerakhir(LocalDateTime loginTerakhir) {
        this.loginTerakhir = loginTerakhir;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public int getJumlahMataKuliah() {
        return jumlahMataKuliah;
    }

    public void setJumlahMataKuliah(int jumlahMataKuliah) {
        this.jumlahMataKuliah = jumlahMataKuliah;
    }

    public int getJumlahTopik() {
        return jumlahTopik;
    }

    public void setJumlahTopik(int jumlahTopik) {
        this.jumlahTopik = jumlahTopik;
    }

    public int getTopikDikuasai() {
        return topikDikuasai;
    }

    public void setTopikDikuasai(int topikDikuasai) {
        this.topikDikuasai = topikDikuasai;
    }

    public double getProgressKeseluruhan() {
        return progressKeseluruhan;
    }

    public void setProgressKeseluruhan(double progressKeseluruhan) {
        this.progressKeseluruhan = progressKeseluruhan;
    }

    public int getRuntutanBelajar() {
        return runtutanBelajar;
    }

    public void setRuntutanBelajar(int runtutanBelajar) {
        this.runtutanBelajar = runtutanBelajar;
    }

    public int getSesiMingguIni() {
        return sesiMingguIni;
    }

    public void setSesiMingguIni(int sesiMingguIni) {
        this.sesiMingguIni = sesiMingguIni;
    }

    public double getRataRataPerforma() {
        return rataRataPerforma;
    }

    public void setRataRataPerforma(double rataRataPerforma) {
        this.rataRataPerforma = rataRataPerforma;
    }

    public LocalDate getAktivitasTerakhir() {
        return aktivitasTerakhir;
    }

    public void setAktivitasTerakhir(LocalDate aktivitasTerakhir) {
        this.aktivitasTerakhir = aktivitasTerakhir;
    }

    /**
     * Cek apakah mahasiswa sudah di-assign ke dosen.
     */
    public boolean sudahAdaDosen() {
        return dosenId != null && dosenId > 0;
    }

    /**
     * Cek apakah mahasiswa aktif (login dalam 7 hari terakhir).
     */
    public boolean isAktifBelajar() {
        if (aktivitasTerakhir == null) return false;
        return aktivitasTerakhir.isAfter(LocalDate.now().minusDays(7));
    }

    @Override
    public String toString() {
        return nama + " (" + nim + ")";
    }
}
