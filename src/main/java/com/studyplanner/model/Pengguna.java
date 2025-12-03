package com.studyplanner.model;

import java.time.LocalDateTime;

/**
 * Model untuk data pengguna sistem.
 */
public class Pengguna {
    private int id;
    private String username;
    private String password;
    private String email;
    private String googleId;
    private String nama;
    private String fotoProfil;
    private String provider;
    private RolePengguna role;
    private StatusPengguna status;
    private LocalDateTime dibuatPada;
    private LocalDateTime loginTerakhir;

    public Pengguna() {
        this.role = RolePengguna.MAHASISWA;
        this.status = StatusPengguna.ACTIVE;
    }

    public Pengguna(int id, String nama, String email, RolePengguna role) {
        this();
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.role = role;
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFotoProfil() {
        return fotoProfil;
    }

    public void setFotoProfil(String fotoProfil) {
        this.fotoProfil = fotoProfil;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public RolePengguna getRole() {
        return role;
    }

    public void setRole(RolePengguna role) {
        this.role = role;
    }

    public StatusPengguna getStatus() {
        return status;
    }

    public void setStatus(StatusPengguna status) {
        this.status = status;
    }

    public LocalDateTime getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(LocalDateTime dibuatPada) {
        this.dibuatPada = dibuatPada;
    }

    public LocalDateTime getLoginTerakhir() {
        return loginTerakhir;
    }

    public void setLoginTerakhir(LocalDateTime loginTerakhir) {
        this.loginTerakhir = loginTerakhir;
    }

    public boolean isMahasiswa() {
        return role == RolePengguna.MAHASISWA;
    }

    public boolean isDosen() {
        return role == RolePengguna.DOSEN;
    }

    public boolean isAdmin() {
        return role == RolePengguna.ADMIN;
    }

    public boolean isAktif() {
        return status == StatusPengguna.ACTIVE;
    }

    @Override
    public String toString() {
        return nama + " (" + role.getNamaDisplay() + ")";
    }
}
