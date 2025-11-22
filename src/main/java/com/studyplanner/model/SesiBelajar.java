package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SesiBelajar {
    private int id;
    private int idTopik;
    private int idMataKuliah;
    private LocalDate tanggalJadwal;
    private String tipeSesi;
    private boolean selesai;
    private LocalDateTime selesaiPada;
    private int ratingPerforma;
    private String catatan;
    private int durasiMenit;

    private String namaTopik;
    private String namaMataKuliah;

    public SesiBelajar() {
        this.selesai = false;
        this.durasiMenit = 30;
    }

    public SesiBelajar(int id, int idTopik, int idMataKuliah, LocalDate tanggalJadwal, String tipeSesi) {
        this();
        this.id = id;
        this.idTopik = idTopik;
        this.idMataKuliah = idMataKuliah;
        this.tanggalJadwal = tanggalJadwal;
        this.tipeSesi = tipeSesi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTopik() {
        return idTopik;
    }

    public void setIdTopik(int idTopik) {
        this.idTopik = idTopik;
    }

    public int getIdMataKuliah() {
        return idMataKuliah;
    }

    public void setIdMataKuliah(int idMataKuliah) {
        this.idMataKuliah = idMataKuliah;
    }

    public LocalDate getTanggalJadwal() {
        return tanggalJadwal;
    }

    public void setTanggalJadwal(LocalDate tanggalJadwal) {
        this.tanggalJadwal = tanggalJadwal;
    }

    public String getTipeSesi() {
        return tipeSesi;
    }

    public void setTipeSesi(String tipeSesi) {
        this.tipeSesi = tipeSesi;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public void setSelesai(boolean selesai) {
        this.selesai = selesai;
    }

    public LocalDateTime getSelesaiPada() {
        return selesaiPada;
    }

    public void setSelesaiPada(LocalDateTime selesaiPada) {
        this.selesaiPada = selesaiPada;
    }

    public int getRatingPerforma() {
        return ratingPerforma;
    }

    public void setRatingPerforma(int ratingPerforma) {
        this.ratingPerforma = ratingPerforma;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public int getDurasiMenit() {
        return durasiMenit;
    }

    public void setDurasiMenit(int durasiMenit) {
        this.durasiMenit = durasiMenit;
    }

    public String getNamaTopik() {
        return namaTopik;
    }

    public void setNamaTopik(String namaTopik) {
        this.namaTopik = namaTopik;
    }

    public String getNamaMataKuliah() {
        return namaMataKuliah;
    }

    public void setNamaMataKuliah(String namaMataKuliah) {
        this.namaMataKuliah = namaMataKuliah;
    }

    @Override
    public String toString() {
        return tipeSesi + ": " + namaTopik + " (" + namaMataKuliah + ")";
    }
}
