package com.studyplanner.model;

public class ModelFSRS {
    private String nama;
    private double[] bobot;
    private double loss;
    private double akurasi;
    private String catatan;
    private String dibuatPada;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double[] getBobot() {
        return bobot;
    }

    public void setBobot(double[] bobot) {
        this.bobot = bobot;
    }

    public double getLoss() {
        return loss;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public double getAkurasi() {
        return akurasi;
    }

    public void setAkurasi(double akurasi) {
        this.akurasi = akurasi;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(String dibuatPada) {
        this.dibuatPada = dibuatPada;
    }
}
