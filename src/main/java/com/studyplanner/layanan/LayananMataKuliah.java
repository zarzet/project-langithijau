package com.studyplanner.layanan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOMataKuliah;
import com.studyplanner.dao.DAOSesiBelajar;
import com.studyplanner.dao.DAOTopik;
import com.studyplanner.dao.DAOJadwalUjian;
import com.studyplanner.model.MataKuliah;
import java.sql.SQLException;
import java.util.List;

/**
 * Layanan untuk mengelola business logic terkait Mata Kuliah.
 * Menyediakan operasi tingkat tinggi yang menggunakan DAO.
 */
public class LayananMataKuliah {

    private final DAOMataKuliah daoMataKuliah;
    private final DAOTopik daoTopik;
    private final DAOJadwalUjian daoJadwalUjian;
    private final DAOSesiBelajar daoSesiBelajar;

    public LayananMataKuliah(ManajerBasisData manajerDB) {
        this.daoMataKuliah = new DAOMataKuliah(manajerDB);
        this.daoTopik = new DAOTopik(manajerDB);
        this.daoJadwalUjian = new DAOJadwalUjian(manajerDB);
        this.daoSesiBelajar = new DAOSesiBelajar(manajerDB);
    }

    /**
     * Mendaftarkan mata kuliah baru dengan validasi.
     *
     * @param mataKuliah Data mata kuliah (harus memiliki userId yang valid)
     * @return ID mata kuliah yang baru dibuat
     * @throws IllegalArgumentException jika data tidak valid
     * @throws IllegalStateException jika kode sudah digunakan oleh user yang sama
     * @throws SQLException jika terjadi kesalahan database
     */
    public int daftarkan(MataKuliah mataKuliah) throws SQLException {
        validasiMataKuliah(mataKuliah);

        if (mataKuliah.getKode() != null && !mataKuliah.getKode().trim().isEmpty()) {
            MataKuliah existing = daoMataKuliah.ambilBerdasarkanKode(mataKuliah.getUserId(), mataKuliah.getKode());
            if (existing != null) {
                throw new IllegalStateException("Kode mata kuliah '" + mataKuliah.getKode() + "' sudah digunakan");
            }
        }

        return daoMataKuliah.simpan(mataKuliah);
    }

    /**
     * Memperbarui data mata kuliah.
     *
     * @param mataKuliah Data mata kuliah yang diperbarui
     * @return true jika berhasil
     * @throws IllegalArgumentException jika data tidak valid
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(MataKuliah mataKuliah) throws SQLException {
        validasiMataKuliah(mataKuliah);

        if (mataKuliah.getKode() != null && !mataKuliah.getKode().trim().isEmpty()) {
            MataKuliah existing = daoMataKuliah.ambilBerdasarkanKode(mataKuliah.getUserId(), mataKuliah.getKode());
            if (existing != null && existing.getId() != mataKuliah.getId()) {
                throw new IllegalStateException("Kode mata kuliah '" + mataKuliah.getKode() + "' sudah digunakan");
            }
        }

        return daoMataKuliah.perbarui(mataKuliah);
    }

    /**
     * Menghapus mata kuliah beserta semua topik, ujian, dan sesi belajar terkait.
     *
     * @param idMataKuliah ID mata kuliah
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int idMataKuliah) throws SQLException {
        daoSesiBelajar.hapusBerdasarkanMataKuliahId(idMataKuliah);
        
        return daoMataKuliah.hapus(idMataKuliah);
    }

    /**
     * Mengambil semua mata kuliah (deprecated - gunakan ambilSemuaByUserId).
     *
     * @return List semua mata kuliah
     * @throws SQLException jika terjadi kesalahan database
     * @deprecated Gunakan {@link #ambilSemuaByUserId(int)} untuk multi-user support
     */
    @Deprecated
    public List<MataKuliah> ambilSemua() throws SQLException {
        return daoMataKuliah.ambilSemua();
    }

    /**
     * Mengambil semua mata kuliah untuk user tertentu.
     *
     * @param userId ID user
     * @return List mata kuliah milik user tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<MataKuliah> ambilSemuaByUserId(int userId) throws SQLException {
        return daoMataKuliah.ambilSemuaByUserId(userId);
    }

    /**
     * Mengambil mata kuliah berdasarkan ID.
     *
     * @param id ID mata kuliah
     * @return MataKuliah atau null jika tidak ditemukan
     * @throws SQLException jika terjadi kesalahan database
     */
    public MataKuliah ambilBerdasarkanId(int id) throws SQLException {
        return daoMataKuliah.ambilBerdasarkanId(id);
    }

    /**
     * Mengambil mata kuliah berdasarkan kode untuk user tertentu.
     *
     * @param userId ID user
     * @param kode Kode mata kuliah
     * @return MataKuliah atau null jika tidak ditemukan
     * @throws SQLException jika terjadi kesalahan database
     */
    public MataKuliah ambilBerdasarkanKode(int userId, String kode) throws SQLException {
        return daoMataKuliah.ambilBerdasarkanKode(userId, kode);
    }

    /**
     * Menghitung total mata kuliah (semua user).
     *
     * @return Jumlah mata kuliah
     * @throws SQLException jika terjadi kesalahan database
     * @deprecated Gunakan {@link #hitungTotalByUserId(int)} untuk multi-user support
     */
    @Deprecated
    public int hitungTotal() throws SQLException {
        return daoMataKuliah.hitungTotal();
    }

    /**
     * Menghitung total mata kuliah untuk user tertentu.
     *
     * @param userId ID user
     * @return Jumlah mata kuliah milik user
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungTotalByUserId(int userId) throws SQLException {
        return daoMataKuliah.hitungTotalByUserId(userId);
    }

    /**
     * Menghitung total topik untuk mata kuliah tertentu.
     *
     * @param idMataKuliah ID mata kuliah
     * @return Jumlah topik
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungTotalTopik(int idMataKuliah) throws SQLException {
        return daoTopik.hitungBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Menghitung total ujian untuk mata kuliah tertentu.
     *
     * @param idMataKuliah ID mata kuliah
     * @return Jumlah ujian
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungTotalUjian(int idMataKuliah) throws SQLException {
        return daoJadwalUjian.hitungBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Memeriksa apakah mata kuliah memiliki ujian mendatang.
     *
     * @param idMataKuliah ID mata kuliah
     * @return true jika ada ujian mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean punyaUjianMendatang(int idMataKuliah) throws SQLException {
        return !daoJadwalUjian.ambilBerdasarkanMataKuliahId(idMataKuliah).isEmpty();
    }

    private void validasiMataKuliah(MataKuliah mk) {
        if (mk == null) {
            throw new IllegalArgumentException("Data mata kuliah tidak boleh null");
        }
        if (mk.getNama() == null || mk.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama mata kuliah tidak boleh kosong");
        }
    }
}
