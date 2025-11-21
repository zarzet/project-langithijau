package com.studyplanner.layanan;

import com.studyplanner.algoritma.AlgoritmaFSRS;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOTopik;
import com.studyplanner.model.ModelFSRS;
import com.studyplanner.model.Topik;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Layanan untuk mengelola business logic terkait Topik.
 * Termasuk logika spaced repetition dengan algoritma FSRS.
 */
public class LayananTopik {

    private final DAOTopik daoTopik;
    private final AlgoritmaFSRS algoritmaFSRS;

    // Batas minimum faktor kemudahan lama untuk menjaga kompatibilitas data
    private static final double FAKTOR_KEMUDAHAN_MINIMAL = 1.3;

    public LayananTopik(ManajerBasisData manajerDB) {
        this.daoTopik = new DAOTopik(manajerDB);
        this.algoritmaFSRS = muatModelFSRS(manajerDB);
    }

    /**
     * Menambahkan topik baru dengan validasi.
     *
     * @param topik Data topik
     * @return ID topik yang baru dibuat
     * @throws IllegalArgumentException jika data tidak valid
     * @throws SQLException jika terjadi kesalahan database
     */
    public int tambah(Topik topik) throws SQLException {
        validasiTopik(topik);
        return daoTopik.simpan(topik);
    }

    /**
     * Memperbarui data topik.
     *
     * @param topik Data topik yang diperbarui
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean perbarui(Topik topik) throws SQLException {
        validasiTopik(topik);
        return daoTopik.perbarui(topik);
    }

    /**
     * Menghapus topik.
     *
     * @param idTopik ID topik
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int idTopik) throws SQLException {
        return daoTopik.hapus(idTopik);
    }

    /**
     * Mengambil semua topik.
     *
     * @return List semua topik
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilSemua() throws SQLException {
        return daoTopik.ambilSemua();
    }

    /**
     * Mengambil semua topik berdasarkan mata kuliah.
     *
     * @param idMataKuliah ID mata kuliah
     * @return List topik
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilBerdasarkanMataKuliah(int idMataKuliah) throws SQLException {
        return daoTopik.ambilBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Mengambil topik berdasarkan ID.
     *
     * @param id ID topik
     * @return Topik atau null
     * @throws SQLException jika terjadi kesalahan database
     */
    public Topik ambilBerdasarkanId(int id) throws SQLException {
        return daoTopik.ambilBerdasarkanId(id);
    }

    /**
     * Mengambil topik yang perlu diulang hari ini berdasarkan spaced repetition.
     *
     * @param idMataKuliah ID mata kuliah (-1 untuk semua)
     * @return List topik yang perlu diulang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilTopikUntukDiulang(int idMataKuliah) throws SQLException {
        return daoTopik.ambilTopikUntukDiulang(idMataKuliah, LocalDate.now());
    }

    /**
     * Memproses hasil review topik dan update spaced repetition data menggunakan FSRS.
     *
     * @param idTopik ID topik
     * @param nilaiKualitas Nilai kualitas jawaban (1-5)
     *                      1: Salah total atau blackout
     *                      2: Salah, tapi ingat setelah melihat jawaban
     *                      3: Benar, tapi sulit mengingat
     *                      4: Benar, dengan sedikit keraguan
     *                      5: Benar, sempurna
     * @return Tanggal ulasan berikutnya yang direkomendasikan
     * @throws SQLException jika terjadi kesalahan database
     */
    public LocalDate prosesHasilReview(int idTopik, int nilaiKualitas) throws SQLException {
        if (nilaiKualitas < 1 || nilaiKualitas > 5) {
            throw new IllegalArgumentException("Nilai kualitas harus antara 1-5");
        }

        Topik topik = daoTopik.ambilBerdasarkanId(idTopik);
        if (topik == null) {
            throw new IllegalArgumentException("Topik tidak ditemukan");
        }

        if (topik.getTanggalBelajarPertama() == null) {
            topik.setTanggalBelajarPertama(LocalDate.now());
        }

        long hariSejakUlasan = 0;
        if (topik.getTanggalUlasanTerakhir() != null) {
            hariSejakUlasan = Math.max(0, ChronoUnit.DAYS.between(topik.getTanggalUlasanTerakhir(), LocalDate.now()));
        }

        double targetRetensi = topik.getRetensiDiinginkan() > 0 ? topik.getRetensiDiinginkan() : 0.9;
        AlgoritmaFSRS.KondisiMemori kondisiAwal = ambilKondisiMemoriAwal(topik);
        int ratingFsrs = AlgoritmaFSRS.petaRatingPenggunaKeFsrs(nilaiKualitas);

        AlgoritmaFSRS.OpsiInterval opsi = algoritmaFSRS.hitungKeadaanBerikutnya(
                kondisiAwal,
                targetRetensi,
                hariSejakUlasan);
        AlgoritmaFSRS.KeadaanKartu hasil = opsi.pilih(ratingFsrs);

        int intervalBaru = Math.max(1, (int) Math.round(hasil.getInterval()));
        double stabilitasBaru = hasil.getKondisiMemori().getStabilitas();
        double kesulitanBaru = hasil.getKondisiMemori().getKesulitan();
        double faktorKemudahanBaru = Math.max(FAKTOR_KEMUDAHAN_MINIMAL, topik.getFaktorKemudahan());
        int jumlahUlasanBaru = topik.getJumlahUlasan() + 1;
        LocalDate tanggalUlasanTerakhir = LocalDate.now();

        topik.setInterval(intervalBaru);
        topik.setJumlahUlasan(jumlahUlasanBaru);
        topik.setStabilitasFsrs(stabilitasBaru);
        topik.setKesulitanFsrs(kesulitanBaru);
        topik.setRetensiDiinginkan(targetRetensi);
        topik.setTanggalUlasanTerakhir(tanggalUlasanTerakhir);
        topik.setFaktorKemudahan(faktorKemudahanBaru);

        if (nilaiKualitas >= 4 && intervalBaru >= 30) {
            topik.setDikuasai(true);
        }

        daoTopik.perbarui(topik);
        return tanggalUlasanTerakhir.plusDays(intervalBaru);
    }

    /**
     * Menandai topik sebagai dikuasai.
     *
     * @param idTopik ID topik
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean tandaiDikuasai(int idTopik) throws SQLException {
        return daoTopik.tandaiDikuasai(idTopik, true);
    }

    /**
     * Menghitung total topik untuk mata kuliah.
     *
     * @param idMataKuliah ID mata kuliah
     * @return Jumlah topik
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungBerdasarkanMataKuliah(int idMataKuliah) throws SQLException {
        return daoTopik.hitungBerdasarkanMataKuliahId(idMataKuliah);
    }

    /**
     * Validasi data topik.
     */
    private void validasiTopik(Topik topik) {
        if (topik == null) {
            throw new IllegalArgumentException("Data topik tidak boleh null");
        }
        if (topik.getNama() == null || topik.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama topik tidak boleh kosong");
        }
        if (topik.getIdMataKuliah() <= 0) {
            throw new IllegalArgumentException("ID mata kuliah tidak valid");
        }
        if (topik.getPrioritas() < 1 || topik.getPrioritas() > 5) {
            throw new IllegalArgumentException("Prioritas harus antara 1-5");
        }
        if (topik.getTingkatKesulitan() < 1 || topik.getTingkatKesulitan() > 5) {
            throw new IllegalArgumentException("Tingkat kesulitan harus antara 1-5");
        }
        if (topik.getRetensiDiinginkan() <= 0 || topik.getRetensiDiinginkan() > 0.99) {
            topik.setRetensiDiinginkan(0.9);
        }
    }

    private AlgoritmaFSRS muatModelFSRS(ManajerBasisData manajerDB) {
        try {
            ModelFSRS model = manajerDB.ambilModelFSRSTerbaru();
            if (model != null && model.getBobot() != null && model.getBobot().length >= 21) {
                return new AlgoritmaFSRS(model.getBobot());
            }
        } catch (Exception ignored) {
            // jika gagal muat model tersimpan, gunakan default
        }
        return new AlgoritmaFSRS();
    }

    private AlgoritmaFSRS.KondisiMemori ambilKondisiMemoriAwal(Topik topik) {
        if (topik.getStabilitasFsrs() > 0 && topik.getKesulitanFsrs() > 0) {
            return new AlgoritmaFSRS.KondisiMemori(topik.getStabilitasFsrs(), topik.getKesulitanFsrs());
        }

        if (topik.getJumlahUlasan() > 0) {
            try {
                return algoritmaFSRS.kondisiAwalDariSm2(
                        topik.getFaktorKemudahan(),
                        topik.getInterval(),
                        topik.getRetensiDiinginkan() > 0 ? topik.getRetensiDiinginkan() : 0.9);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        return null;
    }
}
