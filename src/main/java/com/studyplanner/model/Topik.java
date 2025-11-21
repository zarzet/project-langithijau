package com.studyplanner.model;

import java.time.LocalDate;
import com.studyplanner.algoritma.AlgoritmaFSRS;

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
    private double stabilitasFsrs;
    private double kesulitanFsrs;
    private double retensiDiinginkan;
    private double peluruhanFsrs;

    public Topik() {
        this.prioritas = 3;
        this.tingkatKesulitan = 3;
        this.jumlahUlasan = 0;
        this.faktorKemudahan = 2.5;
        this.interval = 1;
        this.dikuasai = false;
        this.stabilitasFsrs = 0.0;
        this.kesulitanFsrs = 0.0;
        this.retensiDiinginkan = 0.9;
        this.peluruhanFsrs = AlgoritmaFSRS.PARAMETER_BAWAAN[20];
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

    public double getStabilitasFsrs() {
        return stabilitasFsrs;
    }

    public void setStabilitasFsrs(double stabilitasFsrs) {
        this.stabilitasFsrs = stabilitasFsrs;
    }

    public double getKesulitanFsrs() {
        return kesulitanFsrs;
    }

    public void setKesulitanFsrs(double kesulitanFsrs) {
        this.kesulitanFsrs = kesulitanFsrs;
    }

    public double getRetensiDiinginkan() {
        return retensiDiinginkan;
    }

    public void setRetensiDiinginkan(double retensiDiinginkan) {
        this.retensiDiinginkan = retensiDiinginkan;
    }

    public double getPeluruhanFsrs() {
        return peluruhanFsrs;
    }

    public void setPeluruhanFsrs(double peluruhanFsrs) {
        this.peluruhanFsrs = peluruhanFsrs;
    }

    @Override
    public String toString() {
        return nama;
    }
}
