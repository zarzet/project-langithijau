package com.studyplanner.layanan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOJadwalUjian;
import com.studyplanner.model.JadwalUjian;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Layanan untuk mengelola business logic terkait Jadwal Ujian.
 */
public class LayananJadwalUjian {

    private final DAOJadwalUjian daoJadwalUjian;

    public LayananJadwalUjian(ManajerBasisData manajerDB) {
        this.daoJadwalUjian = new DAOJadwalUjian(manajerDB);
    }

    /**
     * Menambahkan jadwal ujian baru dengan validasi.
     *
     * @param ujian Data jadwal ujian
     * @return ID ujian yang baru dibuat
     * @throws IllegalArgumentException jika data tidak valid
     * @throws SQLException jika terjadi kesalahan database
     */
    public int tambah(JadwalUjian ujian) throws SQLException {
        validasiJadwalUjian(ujian);
        return daoJadwalUjian.simpan(ujian);
    }

    /**
     * Memperbarui jadwal ujian.
     *
     * @param ujian Data jadwal ujian yang diperbarui
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(JadwalUjian ujian) throws SQLException {
        validasiJadwalUjian(ujian);
        return daoJadwalUjian.perbarui(ujian);
    }

    /**
     * Menghapus jadwal ujian.
     *
     * @param idUjian ID ujian
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int idUjian) throws SQLException {
        return daoJadwalUjian.hapus(idUjian);
    }

    /**
     * Mengambil semua ujian berdasarkan mata kuliah.
     *
     * @param idMataKuliah ID mata kuliah
     * @return List jadwal ujian
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilBerdasarkanMataKuliah(int idMataKuliah) throws SQLException {
        return daoJadwalUjian.ambilBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Mengambil ujian berdasarkan ID.
     *
     * @param id ID ujian
     * @return JadwalUjian atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public JadwalUjian ambilBerdasarkanId(int id) throws SQLException {
        return daoJadwalUjian.ambilBerdasarkanId(id);
    }

    /**
     * Mengambil semua ujian mendatang.
     *
     * @return List ujian mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilUjianMendatang() throws SQLException {
        return daoJadwalUjian.ambilUjianMendatang();
    }

    /**
     * Mengambil ujian dalam rentang tanggal.
     *
     * @param tanggalMulai Tanggal mulai
     * @param tanggalAkhir Tanggal akhir
     * @return List ujian dalam rentang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<JadwalUjian> ambilBerdasarkanRentangTanggal(LocalDate tanggalMulai,
                                                            LocalDate tanggalAkhir) throws SQLException {
        return daoJadwalUjian.ambilBerdasarkanRentangTanggal(tanggalMulai, tanggalAkhir);
    }

    /**
     * Menandai ujian sebagai selesai.
     *
     * @param idUjian ID ujian
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean tandaiSelesai(int idUjian) throws SQLException {
        return daoJadwalUjian.tandaiSelesai(idUjian);
    }

    /**
     * Menghitung hari tersisa sampai ujian.
     *
     * @param ujian Jadwal ujian
     * @return Jumlah hari tersisa (negatif jika sudah lewat)
     */
    public long hitungHariTersisa(JadwalUjian ujian) {
        if (ujian == null || ujian.getTanggalUjian() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), ujian.getTanggalUjian());
    }

    /**
     * Memeriksa apakah ujian mendesak (kurang dari 7 hari).
     *
     * @param ujian Jadwal ujian
     * @return true jika mendesak
     */
    public boolean apakahMendesak(JadwalUjian ujian) {
        long hariTersisa = hitungHariTersisa(ujian);
        return hariTersisa >= 0 && hariTersisa <= 7;
    }

    /**
     * Memeriksa apakah ujian sangat mendesak (kurang dari 3 hari).
     *
     * @param ujian Jadwal ujian
     * @return true jika sangat mendesak
     */
    public boolean apakahSangatMendesak(JadwalUjian ujian) {
        long hariTersisa = hitungHariTersisa(ujian);
        return hariTersisa >= 0 && hariTersisa <= 3;
    }

    /**
     * Menghitung total ujian untuk mata kuliah.
     *
     * @param idMataKuliah ID mata kuliah
     * @return Jumlah ujian
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanMataKuliah(int idMataKuliah) throws SQLException {
        return daoJadwalUjian.hitungBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Validasi data jadwal ujian.
     */
    private void validasiJadwalUjian(JadwalUjian ujian) {
        if (ujian == null) {
            throw new IllegalArgumentException("Data jadwal ujian tidak boleh null");
        }
        if (ujian.getJudul() == null || ujian.getJudul().trim().isEmpty()) {
            throw new IllegalArgumentException("Judul ujian tidak boleh kosong");
        }
        if (ujian.getTanggalUjian() == null) {
            throw new IllegalArgumentException("Tanggal ujian tidak boleh kosong");
        }
        if (ujian.getIdMataKuliah() <= 0) {
            throw new IllegalArgumentException("ID mata kuliah tidak valid");
        }
    }
}
