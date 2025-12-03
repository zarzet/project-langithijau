package com.studyplanner.layanan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOSesiBelajar;
import com.studyplanner.model.SesiBelajar;
import com.studyplanner.model.Topik;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Layanan untuk mengelola business logic terkait Sesi Belajar.
 * Termasuk integrasi dengan spaced repetition.
 */
public class LayananSesiBelajar {

    private final DAOSesiBelajar daoSesiBelajar;
    private final LayananTopik layananTopik;

    public LayananSesiBelajar(ManajerBasisData manajerDB) {
        this.daoSesiBelajar = new DAOSesiBelajar(manajerDB);
        this.layananTopik = new LayananTopik(manajerDB);
    }

    /**
     * Menambahkan sesi belajar baru.
     *
     * @param sesi Data sesi belajar
     * @return ID sesi yang baru dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int tambah(SesiBelajar sesi) throws SQLException {
        validasiSesiBelajar(sesi);
        return daoSesiBelajar.simpan(sesi);
    }

    /**
     * Memperbarui sesi belajar.
     *
     * @param sesi Data sesi yang diperbarui
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(SesiBelajar sesi) throws SQLException {
        validasiSesiBelajar(sesi);
        return daoSesiBelajar.perbarui(sesi);
    }

    /**
     * Menghapus sesi belajar.
     *
     * @param idSesi ID sesi
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int idSesi) throws SQLException {
        return daoSesiBelajar.hapus(idSesi);
    }

    /**
     * Mengambil sesi belajar berdasarkan tanggal.
     *
     * @param tanggal Tanggal jadwal
     * @return List sesi belajar
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanTanggal(LocalDate tanggal) throws SQLException {
        return daoSesiBelajar.ambilBerdasarkanTanggal(tanggal);
    }

    /**
     * Mengambil sesi belajar hari ini.
     *
     * @return List sesi hari ini
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiHariIni() throws SQLException {
        return daoSesiBelajar.ambilSesiHariIni();
    }

    /**
     * Mengambil sesi belajar hari ini untuk user tertentu.
     *
     * @param userId ID user
     * @return List sesi hari ini milik user
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiHariIniByUserId(int userId) throws SQLException {
        return daoSesiBelajar.ambilSesiHariIniByUserId(userId);
    }

    /**
     * Mengambil sesi belajar mendatang yang belum selesai.
     *
     * @return List sesi mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiMendatang() throws SQLException {
        return daoSesiBelajar.ambilSesiMendatang();
    }

    /**
     * Mengambil sesi belajar mendatang dengan batas tertentu.
     *
     * @param batas Jumlah maksimal sesi yang akan diambil
     * @return List sesi mendatang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilSesiMendatang(int batas) throws SQLException {
        return daoSesiBelajar.ambilSesiMendatang(batas);
    }

    /**
     * Mengambil sesi dalam rentang tanggal.
     *
     * @param tanggalMulai Tanggal mulai
     * @param tanggalAkhir Tanggal akhir
     * @return List sesi dalam rentang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<SesiBelajar> ambilBerdasarkanRentangTanggal(LocalDate tanggalMulai,
                                                            LocalDate tanggalAkhir) throws SQLException {
        return daoSesiBelajar.ambilBerdasarkanRentangTanggal(tanggalMulai, tanggalAkhir);
    }

    /**
     * Menyelesaikan sesi belajar dan update spaced repetition data.
     *
     * @param idSesi ID sesi
     * @param ratingPerforma Rating performa (1-5)
     * @param catatan Catatan sesi (opsional)
     * @throws SQLException jika terjadi kesalahan database
     */
    public void selesaikanSesi(int idSesi, int ratingPerforma, String catatan) throws SQLException {
        if (ratingPerforma < 1 || ratingPerforma > 5) {
            throw new IllegalArgumentException("Rating performa harus antara 1-5");
        }

        // Ambil data sesi
        SesiBelajar sesi = daoSesiBelajar.ambilBerdasarkanId(idSesi);
        if (sesi == null) {
            throw new IllegalArgumentException("Sesi tidak ditemukan");
        }

        // Tandai sesi selesai
        daoSesiBelajar.tandaiSelesai(idSesi, ratingPerforma, catatan);

        // Update spaced repetition untuk topik terkait (rating 1-5 dipetakan di layanan topik)
        int nilaiKualitas = ratingPerforma;
        layananTopik.prosesHasilReview(sesi.getIdTopik(), nilaiKualitas);
    }

    /**
     * Menghitung sesi yang selesai hari ini.
     *
     * @return Jumlah sesi selesai
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungSesiSelesaiHariIni() throws SQLException {
        return daoSesiBelajar.hitungSesiSelesaiHariIni();
    }

    /**
     * Menghitung total sesi untuk topik.
     *
     * @param idTopik ID topik
     * @return Jumlah sesi
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanTopik(int idTopik) throws SQLException {
        return daoSesiBelajar.hitungBerdasarkanTopikId(idTopik);
    }

    /**
     * Menghitung total sesi untuk mata kuliah.
     *
     * @param idMataKuliah ID mata kuliah
     * @return Jumlah sesi
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanMataKuliah(int idMataKuliah) throws SQLException {
        return daoSesiBelajar.hitungBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Membuat jadwal sesi belajar otomatis berdasarkan spaced repetition.
     *
     * @param idMataKuliah ID mata kuliah (-1 untuk semua)
     * @return Jumlah sesi yang dibuat
     * @throws SQLException jika terjadi kesalahan database
     */
    public int buatJadwalOtomatis(int idMataKuliah) throws SQLException {
        List<Topik> topikUntukDiulang = layananTopik.ambilTopikUntukDiulang(idMataKuliah);
        int jumlahDibuat = 0;

        for (Topik topik : topikUntukDiulang) {
            SesiBelajar sesi = new SesiBelajar();
            sesi.setIdTopik(topik.getId());
            sesi.setIdMataKuliah(topik.getIdMataKuliah());
            sesi.setTanggalJadwal(LocalDate.now());
            sesi.setTipeSesi("REVIEW");
            sesi.setDurasiMenit(30); // Default 30 menit

            daoSesiBelajar.simpan(sesi);
            jumlahDibuat++;
        }

        return jumlahDibuat;
    }

    /**
     * Validasi data sesi belajar.
     */
    private void validasiSesiBelajar(SesiBelajar sesi) {
        if (sesi == null) {
            throw new IllegalArgumentException("Data sesi belajar tidak boleh null");
        }
        if (sesi.getTanggalJadwal() == null) {
            throw new IllegalArgumentException("Tanggal jadwal tidak boleh kosong");
        }
        if (sesi.getIdTopik() <= 0) {
            throw new IllegalArgumentException("ID topik tidak valid");
        }
        if (sesi.getIdMataKuliah() <= 0) {
            throw new IllegalArgumentException("ID mata kuliah tidak valid");
        }
    }
}
