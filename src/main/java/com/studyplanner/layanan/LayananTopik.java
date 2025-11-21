package com.studyplanner.layanan;

import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOTopik;
import com.studyplanner.model.Topik;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Layanan untuk mengelola business logic terkait Topik.
 * Termasuk logika spaced repetition (SM-2 algorithm).
 */
public class LayananTopik {

    private final DAOTopik daoTopik;

    // Konstanta untuk SM-2 algorithm
    private static final double FAKTOR_KEMUDAHAN_MINIMAL = 1.3;

    public LayananTopik(ManajerBasisData manajerDB) {
        this.daoTopik = new DAOTopik(manajerDB);
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
     * Memproses hasil review topik dan update spaced repetition data.
     * Menggunakan algoritma SM-2.
     *
     * @param idTopik ID topik
     * @param nilaiKualitas Nilai kualitas jawaban (0-5)
     *                      0: Blackout, tidak ingat sama sekali
     *                      1: Salah, tapi ingat setelah melihat jawaban
     *                      2: Salah, tapi jawaban mudah diingat
     *                      3: Benar, tapi sulit mengingat
     *                      4: Benar, dengan sedikit keraguan
     *                      5: Benar, sempurna
     * @throws SQLException jika terjadi kesalahan database
     */
    public void prosesHasilReview(int idTopik, int nilaiKualitas) throws SQLException {
        if (nilaiKualitas < 0 || nilaiKualitas > 5) {
            throw new IllegalArgumentException("Nilai kualitas harus antara 0-5");
        }

        Topik topik = daoTopik.ambilBerdasarkanId(idTopik);
        if (topik == null) {
            throw new IllegalArgumentException("Topik tidak ditemukan");
        }

        // Ambil nilai saat ini
        int intervalLama = topik.getInterval();
        double faktorKemudahanLama = topik.getFaktorKemudahan();
        int jumlahUlasanLama = topik.getJumlahUlasan();

        // Hitung faktor kemudahan baru (SM-2 formula)
        double faktorKemudahanBaru = faktorKemudahanLama +
                (0.1 - (5 - nilaiKualitas) * (0.08 + (5 - nilaiKualitas) * 0.02));

        if (faktorKemudahanBaru < FAKTOR_KEMUDAHAN_MINIMAL) {
            faktorKemudahanBaru = FAKTOR_KEMUDAHAN_MINIMAL;
        }

        // Hitung interval baru
        int intervalBaru;
        if (nilaiKualitas < 3) {
            // Jika gagal, reset interval
            intervalBaru = 1;
        } else {
            if (jumlahUlasanLama == 0) {
                intervalBaru = 1;
            } else if (jumlahUlasanLama == 1) {
                intervalBaru = 6;
            } else {
                intervalBaru = (int) Math.round(intervalLama * faktorKemudahanBaru);
            }
        }

        // Update database
        daoTopik.perbaruiDataSpacedRepetition(
                idTopik,
                intervalBaru,
                LocalDate.now(),
                faktorKemudahanBaru,
                jumlahUlasanLama + 1
        );
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
    }
}
