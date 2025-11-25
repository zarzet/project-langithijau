package com.studyplanner.model;

import java.util.ArrayList;
import java.util.List;

public class MataKuliah {
    private int id;
    private int userId;
    private String nama;
    private String kode;
    private String deskripsi;
    private List<Topik> daftarTopik;

    public MataKuliah() {
        this.daftarTopik = new ArrayList<>();
    }

    public MataKuliah(int id, String nama, String kode, String deskripsi) {
        this.id = id;
        this.nama = nama;
        this.kode = kode;
        this.deskripsi = deskripsi;
        this.daftarTopik = new ArrayList<>();
    }

    public MataKuliah(int id, int userId, String nama, String kode, String deskripsi) {
        this.id = id;
        this.userId = userId;
        this.nama = nama;
        this.kode = kode;
        this.deskripsi = deskripsi;
        this.daftarTopik = new ArrayList<>();
    }

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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public List<Topik> getDaftarTopik() {
        return daftarTopik;
    }

    public void setDaftarTopik(List<Topik> daftarTopik) {
        this.daftarTopik = daftarTopik;
    }

    public void tambahTopik(Topik topik) {
        this.daftarTopik.add(topik);
    }

    @Override
    public String toString() {
        return kode + " - " + nama;
    }
}
