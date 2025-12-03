package com.studyplanner.algoritma;

import com.studyplanner.model.*;
import com.studyplanner.basisdata.ManajerBasisData;
import com.studyplanner.layanan.LayananMataKuliah;
import com.studyplanner.layanan.LayananTopik;
import com.studyplanner.layanan.LayananJadwalUjian;
import com.studyplanner.layanan.LayananSesiBelajar;
import com.studyplanner.utilitas.ManajerOtentikasi;
import com.studyplanner.konfigurasi.KonfigurasiJadwal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PembuatJadwal {
    private final LayananMataKuliah layananMataKuliah;
    private final LayananTopik layananTopik;
    private final LayananJadwalUjian layananJadwalUjian;
    private final LayananSesiBelajar layananSesiBelajar;

    public PembuatJadwal(ManajerBasisData manajerBasisData) {
        this.layananMataKuliah = new LayananMataKuliah(manajerBasisData);
        this.layananTopik = new LayananTopik(manajerBasisData);
        this.layananJadwalUjian = new LayananJadwalUjian(manajerBasisData);
        this.layananSesiBelajar = new LayananSesiBelajar(manajerBasisData);
    }

    public Map<LocalDate, List<SesiBelajar>> buatJadwal(int hariKeDepan) throws SQLException {
        Map<LocalDate, List<SesiBelajar>> jadwal = new LinkedHashMap<>();

        int userId = ManajerOtentikasi.getInstance().ambilIdPengguna()
                .orElseThrow(() -> new IllegalStateException("User belum login"));
        List<MataKuliah> daftarMataKuliah = layananMataKuliah.ambilSemuaByUserId(userId);
        List<Topik> semuaTopik = layananTopik.ambilSemuaByUserId(userId);
        List<JadwalUjian> ujianMendatang = layananJadwalUjian.ambilUjianMendatang();

        Map<Integer, JadwalUjian> ujianPerMataKuliah = new HashMap<>();
        for (JadwalUjian ujian : ujianMendatang) {
            ujianPerMataKuliah.put(ujian.getIdMataKuliah(), ujian);
        }

        for (int hari = 0; hari < hariKeDepan; hari++) {
            LocalDate tanggalTarget = LocalDate.now().plusDays(hari);
            List<SesiBelajar> sesiHarian = buatJadwalHarian(
                    tanggalTarget, semuaTopik, ujianPerMataKuliah, daftarMataKuliah);

            if (!sesiHarian.isEmpty()) {
                jadwal.put(tanggalTarget, sesiHarian);
            }
        }

        return jadwal;
    }

    private List<SesiBelajar> buatJadwalHarian(
            LocalDate tanggal,
            List<Topik> semuaTopik,
            Map<Integer, JadwalUjian> ujianPerMataKuliah,
            List<MataKuliah> daftarMataKuliah) {

        List<SesiBelajar> sesi = new ArrayList<>();

        List<Topik> topikAktif = semuaTopik.stream()
                .filter(t -> !t.isDikuasai())
                .collect(Collectors.toList());

        if (topikAktif.isEmpty()) {
            return sesi;
        }

        List<TopikDenganPrioritas> topikPrioritas = new ArrayList<>();
        for (Topik topik : topikAktif) {
            JadwalUjian ujian = ujianPerMataKuliah.get(topik.getIdMataKuliah());
            LocalDate tanggalUjian = (ujian != null) ? ujian.getTanggalUjian() : null;
            double prioritas = PengulanganBerjarak.hitungPrioritasTopik(topik, tanggalUjian);

            topikPrioritas.add(new TopikDenganPrioritas(topik, prioritas));
        }

        topikPrioritas.sort((a, b) -> Double.compare(b.prioritas, a.prioritas));

        Set<Integer> mataKuliahTerpakai = new HashSet<>();
        List<Topik> topikTerpilih = new ArrayList<>();

        for (TopikDenganPrioritas tp : topikPrioritas) {
            if (!mataKuliahTerpakai.contains(tp.topik.getIdMataKuliah())) {
                topikTerpilih.add(tp.topik);
                mataKuliahTerpakai.add(tp.topik.getIdMataKuliah());

                if (topikTerpilih.size() >= KonfigurasiJadwal.MIN_SESI_PER_HARI) {
                    break;
                }
            }
        }

        for (TopikDenganPrioritas tp : topikPrioritas) {
            if (!topikTerpilih.contains(tp.topik) && topikTerpilih.size() < KonfigurasiJadwal.MAKS_SESI_PER_HARI) {
                topikTerpilih.add(tp.topik);
            }
        }

        for (Topik topik : topikTerpilih) {
            SesiBelajar sesiBaru = new SesiBelajar();
            sesiBaru.setIdTopik(topik.getId());
            sesiBaru.setIdMataKuliah(topik.getIdMataKuliah());
            sesiBaru.setTanggalJadwal(tanggal);

            if (topik.getTanggalBelajarPertama() == null) {
                sesiBaru.setTipeSesi("INITIAL_STUDY");
                sesiBaru.setDurasiMenit(45);
            } else if (PengulanganBerjarak.perluUlasanHariIni(topik)) {
                sesiBaru.setTipeSesi("REVIEW");
                sesiBaru.setDurasiMenit(30);
            } else {
                sesiBaru.setTipeSesi("PRACTICE");
                sesiBaru.setDurasiMenit(30);
            }

            sesiBaru.setNamaTopik(topik.getNama());

            for (MataKuliah mk : daftarMataKuliah) {
                if (mk.getId() == topik.getIdMataKuliah()) {
                    sesiBaru.setNamaMataKuliah(mk.getKode() + " - " + mk.getNama());
                    break;
                }
            }

            sesi.add(sesiBaru);
        }

        Collections.shuffle(sesi);

        return sesi;
    }

    public void buatDanSimpanJadwal(int hariKeDepan) throws SQLException {
        Map<LocalDate, List<SesiBelajar>> jadwal = buatJadwal(hariKeDepan);

        for (Map.Entry<LocalDate, List<SesiBelajar>> entry : jadwal.entrySet()) {
            for (SesiBelajar sesi : entry.getValue()) {
                List<SesiBelajar> sesiAda = layananSesiBelajar.ambilBerdasarkanTanggal(entry.getKey());
                boolean ada = sesiAda.stream()
                        .anyMatch(s -> s.getIdTopik() == sesi.getIdTopik()
                                && s.getTipeSesi().equals(sesi.getTipeSesi()));

                if (!ada) {
                    layananSesiBelajar.tambah(sesi);
                }
            }
        }
    }

    private static class TopikDenganPrioritas {
        Topik topik;
        double prioritas;

        TopikDenganPrioritas(Topik topik, double prioritas) {
            this.topik = topik;
            this.prioritas = prioritas;
        }
    }

    public KemajuanBelajar ambilKemajuanBelajar() throws SQLException {
        // Return empty progress jika user belum login
        if (!ManajerOtentikasi.getInstance().isLoggedIn()) {
            return new KemajuanBelajar();
        }
        
        int userId = ManajerOtentikasi.getInstance().ambilIdPengguna().orElse(-1);
        if (userId < 0) {
            return new KemajuanBelajar();
        }
        
        List<Topik> semuaTopik = layananTopik.ambilSemuaByUserId(userId);
        List<SesiBelajar> sesiHariIni = layananSesiBelajar.ambilSesiHariIniByUserId(userId);

        int totalTopik = semuaTopik.size();
        int topikDikuasai = (int) semuaTopik.stream().filter(Topik::isDikuasai).count();
        int selesaiHariIni = (int) sesiHariIni.stream().filter(SesiBelajar::isSelesai).count();
        int totalHariIni = sesiHariIni.size();

        KemajuanBelajar kemajuan = new KemajuanBelajar();
        kemajuan.setTotalTopik(totalTopik);
        kemajuan.setTopikDikuasai(topikDikuasai);
        kemajuan.setSelesaiHariIni(selesaiHariIni);
        kemajuan.setTotalHariIni(totalHariIni);

        if (totalTopik > 0) {
            kemajuan.setKemajuanKeseluruhan((topikDikuasai * 100.0) / totalTopik);
        }

        if (totalHariIni > 0) {
            kemajuan.setKemajuanHariIni((selesaiHariIni * 100.0) / totalHariIni);
        }

        return kemajuan;
    }

    public static class KemajuanBelajar {
        private int totalTopik;
        private int topikDikuasai;
        private int totalHariIni;
        private int selesaiHariIni;
        private double kemajuanKeseluruhan;
        private double kemajuanHariIni;

        public int getTotalTopik() {
            return totalTopik;
        }

        public void setTotalTopik(int totalTopik) {
            this.totalTopik = totalTopik;
        }

        public int getTopikDikuasai() {
            return topikDikuasai;
        }

        public void setTopikDikuasai(int topikDikuasai) {
            this.topikDikuasai = topikDikuasai;
        }

        public int getTotalHariIni() {
            return totalHariIni;
        }

        public void setTotalHariIni(int totalHariIni) {
            this.totalHariIni = totalHariIni;
        }

        public int getSelesaiHariIni() {
            return selesaiHariIni;
        }

        public void setSelesaiHariIni(int selesaiHariIni) {
            this.selesaiHariIni = selesaiHariIni;
        }

        public double getKemajuanKeseluruhan() {
            return kemajuanKeseluruhan;
        }

        public void setKemajuanKeseluruhan(double kemajuanKeseluruhan) {
            this.kemajuanKeseluruhan = kemajuanKeseluruhan;
        }

        public double getKemajuanHariIni() {
            return kemajuanHariIni;
        }

        public void setKemajuanHariIni(double kemajuanHariIni) {
            this.kemajuanHariIni = kemajuanHariIni;
        }
    }
}
