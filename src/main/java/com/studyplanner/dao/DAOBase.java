package com.studyplanner.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface dasar untuk semua Data Access Object (DAO).
 * Menyediakan operasi CRUD standar untuk entitas.
 *
 * @param <T> Tipe entitas yang dikelola oleh DAO ini
 * @param <ID> Tipe primary key dari entitas
 */
public interface DAOBase<T, ID> {

    /**
     * Menyimpan entitas baru ke database.
     *
     * @param entitas Entitas yang akan disimpan
     * @return ID dari entitas yang baru disimpan
     * @throws SQLException jika terjadi kesalahan database
     */
    ID simpan(T entitas) throws SQLException;

    /**
     * Mengambil entitas berdasarkan ID.
     *
     * @param id Primary key dari entitas
     * @return Entitas yang ditemukan, atau null jika tidak ada
     * @throws SQLException jika terjadi kesalahan database
     */
    T ambilBerdasarkanId(ID id) throws SQLException;

    /**
     * Mengambil semua entitas dari database.
     *
     * @return List berisi semua entitas
     * @throws SQLException jika terjadi kesalahan database
     */
    List<T> ambilSemua() throws SQLException;

    /**
     * Memperbarui entitas yang sudah ada.
     *
     * @param entitas Entitas dengan data yang diperbarui
     * @return true jika berhasil diperbarui, false jika tidak
     * @throws SQLException jika terjadi kesalahan database
     */
    boolean perbarui(T entitas) throws SQLException;

    /**
     * Menghapus entitas berdasarkan ID.
     *
     * @param id Primary key dari entitas yang akan dihapus
     * @return true jika berhasil dihapus, false jika tidak
     * @throws SQLException jika terjadi kesalahan database
     */
    boolean hapus(ID id) throws SQLException;

    /**
     * Menghitung jumlah total entitas di database.
     *
     * @return Jumlah entitas
     * @throws SQLException jika terjadi kesalahan database
     */
    int hitungTotal() throws SQLException;
}
