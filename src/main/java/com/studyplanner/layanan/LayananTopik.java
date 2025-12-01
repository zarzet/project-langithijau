package com.studyplanner.layanan;

import com.studyplanner.algoritma.AlgoritmaSM2;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOSesiBelajar;
import com.studyplanner.dao.DAOTopik;
import com.studyplanner.model.Topik;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Layanan untuk mengelola business logic terkait Topik.
 * Termasuk logika spaced repetition dengan FSRS (diadaptasi dari SM-2, rating tetap 1-5).
 */
public class LayananTopik {

    private final DAOTopik daoTopik;
    private final DAOSesiBelajar daoSesiBelajar;
    private final AlgoritmaSM2 algoritmaFSRS;

    // Batas minimum faktor kemudahan lama untuk menjaga kompatibilitas data
    private static final double FAKTOR_KEMUDAHAN_MINIMAL = 1.3;

    public LayananTopik(ManajerBasisData manajerDB) {
        this.daoTopik = new DAOTopik(manajerDB);
        this.daoSesiBelajar = new DAOSesiBelajar(manajerDB);
        this.algoritmaFSRS = new AlgoritmaSM2();
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
     * Menghapus topik beserta semua sesi belajar terkait (cascade delete).
     *
     * @param idTopik ID topik
     * @return true jika berhasil
     * @throws SQLException jika terjadi kesalahan database
     */
    public boolean hapus(int idTopik) throws SQLException {
        // Cascade delete: hapus sesi belajar terkait terlebih dahulu
        daoSesiBelajar.hapusBerdasarkanTopikId(idTopik);
        
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
     * Mengambil semua topik berdasarkan user ID.
     *
     * @param userId ID user
     * @return List topik milik user tersebut
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilSemuaByUserId(int userId) throws SQLException {
        return daoTopik.ambilSemuaByUserId(userId);
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
     * Mengambil topik yang perlu diulang hari ini berdasarkan user ID.
     *
     * @param userId ID user
     * @param idMataKuliah ID mata kuliah (-1 untuk semua mata kuliah user)
     * @return List topik yang perlu diulang
     * @throws SQLException jika terjadi kesalahan database
     */
    public List<Topik> ambilTopikUntukDiulangByUserId(int userId, int idMataKuliah) throws SQLException {
        return daoTopik.ambilTopikUntukDiulangByUserId(userId, idMataKuliah, LocalDate.now());
    }

    /**
     * Memproses hasil review topik dengan FSRS (rating 1-5 dipetakan ke tombol FSRS).
     *
     * @return tanggal ulasan berikutnya yang direkomendasikan
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
        AlgoritmaSM2.KondisiMemori kondisiAwal = ambilKondisiMemoriAwal(topik);
        int ratingFsrs = AlgoritmaSM2.petaRatingFsrs(nilaiKualitas);

        AlgoritmaSM2.OpsiInterval opsi = algoritmaFSRS.hitungKeadaanBerikutnya(
                kondisiAwal,
                targetRetensi,
                hariSejakUlasan);
        AlgoritmaSM2.KeadaanKartu hasil = opsi.pilih(ratingFsrs);

        int intervalBaru = Math.max(1, (int) Math.round(hasil.interval()));
        double stabilitasBaru = hasil.kondisiMemori().stabilitas();
        double kesulitanBaru = hasil.kondisiMemori().kesulitan();
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

        daoTopik.perbaruiDataSpacedRepetition(
                idTopik,
                intervalBaru,
                tanggalUlasanTerakhir,
                faktorKemudahanBaru,
                jumlahUlasanBaru,
                stabilitasBaru,
                kesulitanBaru,
                targetRetensi,
                topik.getPeluruhanFsrs()
        );

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
     * Menghitung total topik berdasarkan user ID.
     *
     * @param userId ID user
     * @return Jumlah topik
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungByUserId(int userId) throws SQLException {
        return daoTopik.hitungByUserId(userId);
    }

    /**
     * Menghitung topik yang dikuasai berdasarkan user ID.
     *
     * @param userId ID user
     * @return Jumlah topik dikuasai
     * @throws SQLException jika terjadi kesalahan database
     */
    public int hitungDikuasaiByUserId(int userId) throws SQLException {
        return daoTopik.hitungDikuasaiByUserId(userId);
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

    private AlgoritmaSM2.KondisiMemori ambilKondisiMemoriAwal(Topik topik) {
        if (topik.getStabilitasFsrs() > 0 && topik.getKesulitanFsrs() > 0) {
            return new AlgoritmaSM2.KondisiMemori(topik.getStabilitasFsrs(), topik.getKesulitanFsrs());
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
