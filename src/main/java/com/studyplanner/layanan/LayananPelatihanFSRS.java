package com.studyplanner.layanan;

import com.studyplanner.algoritma.AlgoritmaFSRS;
import com.studyplanner.algoritma.fsrs.FSRSItem;
import com.studyplanner.algoritma.fsrs.FSRSReview;
import com.studyplanner.algoritma.fsrs.HasilPelatihanFSRS;
import com.studyplanner.algoritma.fsrs.PelatihFSRS;
import com.studyplanner.algoritma.fsrs.PelatihFSRSKonfigurasi;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.dao.DAOSesiBelajar;
import com.studyplanner.model.ModelFSRS;
import com.studyplanner.model.SesiBelajar;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Layanan untuk membangun dataset FSRS dari riwayat sesi belajar dan melatih bobot.
 */
public class LayananPelatihanFSRS {

    private final DAOSesiBelajar daoSesiBelajar;
    private final ManajerBasisData manajerBasisData;
    private final PelatihFSRS pelatihFSRS;
    private static final int MIN_SESI_BARU = 50;
    private static final int MIN_ITEM_TRAIN = 50;
    private static final int MIN_JEDA_JAM = 24;

    public LayananPelatihanFSRS(ManajerBasisData manajerBasisData) {
        this.manajerBasisData = manajerBasisData;
        this.daoSesiBelajar = new DAOSesiBelajar(manajerBasisData);
        this.pelatihFSRS = new PelatihFSRS();
    }

    public HasilPelatihanFSRS latihDanSimpan(PelatihFSRSKonfigurasi konfigurasi) throws SQLException {
        List<FSRSItem> dataset = bangunDataset();
        HasilPelatihanFSRS hasil = pelatihFSRS.latih(dataset, konfigurasi);
        manajerBasisData.simpanModelFSRS(hasil.getBobotTerbaik(), hasil.getLossRataRata(), hasil.getAccuracy(),
                "latih otomatis");
        return hasil;
    }

    /**
     * Latih otomatis hanya jika syarat minimal terpenuhi dan ada data baru.
     */
    public HasilLatihOtomatis latihJikaPerlu() throws SQLException {
        ModelFSRS modelTerakhir = manajerBasisData.ambilModelFSRSTerbaru();
        LocalDateTime waktuTerakhir = parseWaktu(modelTerakhir);
        LocalDateTime batas = LocalDateTime.now().minusHours(MIN_JEDA_JAM);
        if (waktuTerakhir != null && waktuTerakhir.isAfter(batas)) {
            return HasilLatihOtomatis.dilewati("Sudah dilatih kurang dari " + MIN_JEDA_JAM + " jam.");
        }

        List<SesiBelajar> sesiSelesai = daoSesiBelajar.ambilSesiSelesaiDenganRating();
        if (sesiSelesai.size() < MIN_SESI_BARU) {
            return HasilLatihOtomatis.dilewati("Butuh minimal " + MIN_SESI_BARU + " sesi selesai.");
        }

        List<FSRSItem> dataset = bangunDatasetDariSesi(sesiSelesai);
        if (dataset.size() < MIN_ITEM_TRAIN) {
            return HasilLatihOtomatis.dilewati("Dataset terlalu sedikit (" + dataset.size() + " item).");
        }

        PelatihFSRSKonfigurasi konfigurasi = PelatihFSRSKonfigurasi.bawaan();
        HasilPelatihanFSRS hasil = pelatihFSRS.latih(dataset, konfigurasi);
        manajerBasisData.simpanModelFSRS(hasil.getBobotTerbaik(), hasil.getLossRataRata(), hasil.getAccuracy(),
                "latih otomatis");
        return HasilLatihOtomatis.berhasil(hasil.getLossRataRata(), hasil.getAccuracy(), dataset.size());
    }

    public List<FSRSItem> bangunDataset() throws SQLException {
        List<SesiBelajar> sesiSelesai = daoSesiBelajar.ambilSesiSelesaiDenganRating();
        return bangunDatasetDariSesi(sesiSelesai);
    }

    private List<FSRSItem> bangunDatasetDariSesi(List<SesiBelajar> sesiSelesai) {
        Map<Integer, List<SesiBelajar>> perTopik = new HashMap<>();
        for (SesiBelajar sesi : sesiSelesai) {
            perTopik.computeIfAbsent(sesi.getIdTopik(), k -> new ArrayList<>()).add(sesi);
        }

        List<FSRSItem> items = new ArrayList<>();
        for (List<SesiBelajar> daftar : perTopik.values()) {
            daftar.sort((a, b) -> ambilTanggal(a).compareTo(ambilTanggal(b)));
            List<FSRSReview> reviews = new ArrayList<>();
            LocalDate tanggalSebelumnya = null;
            for (SesiBelajar sesi : daftar) {
                LocalDate t = ambilTanggal(sesi);
                if (tanggalSebelumnya == null) {
                    tanggalSebelumnya = t;
                    continue;
                }
                int delta = (int) Math.max(1, ChronoUnit.DAYS.between(tanggalSebelumnya, t));
                int ratingFsrs = AlgoritmaFSRS.petaRatingPenggunaKeFsrs(sesi.getRatingPerforma());
                reviews.add(new FSRSReview(ratingFsrs, delta));
                tanggalSebelumnya = t;
            }
            if (!reviews.isEmpty()) {
                items.add(new FSRSItem(reviews));
            }
        }
        return items;
    }

    private LocalDate ambilTanggal(SesiBelajar sesi) {
        if (sesi.getSelesaiPada() != null) {
            return sesi.getSelesaiPada().toLocalDate();
        }
        return sesi.getTanggalJadwal() != null ? sesi.getTanggalJadwal() : LocalDate.now();
    }

    private LocalDateTime parseWaktu(ModelFSRS model) {
        if (model == null || model.getDibuatPada() == null) return null;
        String waktu = model.getDibuatPada();
        try {
            return LocalDateTime.parse(waktu);
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(waktu, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignored) {
        }
        return null;
    }

    public record HasilLatihOtomatis(boolean dilakukan, String alasan, double loss, double akurasi, int jumlahItem) {
        public static HasilLatihOtomatis dilewati(String alasan) {
            return new HasilLatihOtomatis(false, alasan, 0, 0, 0);
        }

        public static HasilLatihOtomatis berhasil(double loss, double akurasi, int jumlahItem) {
            return new HasilLatihOtomatis(true, null, loss, akurasi, jumlahItem);
        }
    }
}
