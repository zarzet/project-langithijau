package com.studyplanner.model;

import java.time.LocalDate;

public class Topik {
    private int id;
    private int idMataKuliah;
    private String nama;
    private String deskripsi;
    private int prioritas;
    private int tingkatKesulitan;
    private LocalDate tanggalBelajarPertama;
    private LocalDate tanggalUlasanTerakhir;
    private int jumlahUlasan;
    private double faktorKemudahan;
    private int interval;
    private boolean dikuasai;

    public Topik() {
        this.prioritas = 3;
        this.tingkatKesulitan = 3;
        this.jumlahUlasan = 0;
        this.faktorKemudahan = 2.5;
        this.interval = 1;
        this.dikuasai = false;
    }

    public Topik(int id, int idMataKuliah, String nama, String deskripsi) {
        this();
        this.id = id;
        this.idMataKuliah = idMataKuliah;
        this.nama = nama;
        this.deskripsi = deskripsi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMataKuliah() {
        return idMataKuliah;
    }

    public void setIdMataKuliah(int idMataKuliah) {
        this.idMataKuliah = idMataKuliah;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(int prioritas) {
        this.prioritas = prioritas;
    }

    public int getTingkatKesulitan() {
        return tingkatKesulitan;
    }

    public void setTingkatKesulitan(int tingkatKesulitan) {
        this.tingkatKesulitan = tingkatKesulitan;
    }

    public LocalDate getTanggalBelajarPertama() {
        return tanggalBelajarPertama;
    }

    public void setTanggalBelajarPertama(LocalDate tanggalBelajarPertama) {
        this.tanggalBelajarPertama = tanggalBelajarPertama;
    }

    public LocalDate getTanggalUlasanTerakhir() {
        return tanggalUlasanTerakhir;
    }

    public void setTanggalUlasanTerakhir(LocalDate tanggalUlasanTerakhir) {
        this.tanggalUlasanTerakhir = tanggalUlasanTerakhir;
    }

    public int getJumlahUlasan() {
        return jumlahUlasan;
    }

    public void setJumlahUlasan(int jumlahUlasan) {
        this.jumlahUlasan = jumlahUlasan;
    }

    public double getFaktorKemudahan() {
        return faktorKemudahan;
    }

    public void setFaktorKemudahan(double faktorKemudahan) {
        this.faktorKemudahan = faktorKemudahan;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isDikuasai() {
        return dikuasai;
    }

    public void setDikuasai(boolean dikuasai) {
        this.dikuasai = dikuasai;
    }

    @Override
    public String toString() {
        return nama;
    }
}
