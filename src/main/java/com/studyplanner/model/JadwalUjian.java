package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class JadwalUjian {
    private int id;
    private int idMataKuliah;
    private String tipeUjian;
    private String judul;
    private LocalDate tanggalUjian;
    private LocalTime waktuUjian;
    private String lokasi;
    private String catatan;
    private boolean selesai;

    public JadwalUjian() {
        this.selesai = false;
    }

    public JadwalUjian(int id, int idMataKuliah, String tipeUjian, String judul, LocalDate tanggalUjian) {
        this();
        this.id = id;
        this.idMataKuliah = idMataKuliah;
        this.tipeUjian = tipeUjian;
        this.judul = judul;
        this.tanggalUjian = tanggalUjian;
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

    public String getTipeUjian() {
        return tipeUjian;
    }

    public void setTipeUjian(String tipeUjian) {
        this.tipeUjian = tipeUjian;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public LocalDate getTanggalUjian() {
        return tanggalUjian;
    }

    public void setTanggalUjian(LocalDate tanggalUjian) {
        this.tanggalUjian = tanggalUjian;
    }

    public LocalTime getWaktuUjian() {
        return waktuUjian;
    }

    public void setWaktuUjian(LocalTime waktuUjian) {
        this.waktuUjian = waktuUjian;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public void setSelesai(boolean selesai) {
        this.selesai = selesai;
    }

    public int getHariMenujuUjian() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), tanggalUjian);
    }

    @Override
    public String toString() {
        return judul + " (" + tanggalUjian + ")";
    }
}
