package com.studyplanner.algoritma;

import com.studyplanner.model.Topik;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PengulanganBerjarak {

    private static final AlgoritmaSM2 FSRS = new AlgoritmaSM2();

    public static LocalDate hitungTanggalUlasanBerikutnya(Topik topik, int ratingPerforma) {
        int ratingFsrs = AlgoritmaSM2.petaRatingFsrs(ratingPerforma);
        AlgoritmaSM2.KondisiMemori kondisi = ambilKondisi(topik);
        long hariSejakUlasan = topik.getTanggalUlasanTerakhir() != null
                ? Math.max(0, ChronoUnit.DAYS.between(topik.getTanggalUlasanTerakhir(), LocalDate.now()))
                : 0;

        AlgoritmaSM2.OpsiInterval opsi = FSRS.hitungKeadaanBerikutnya(
                kondisi,
                targetRetensi(topik),
                hariSejakUlasan);
        AlgoritmaSM2.KeadaanKartu hasil = opsi.pilih(ratingFsrs);

        int intervalBaru = Math.max(1, (int) Math.round(hasil.interval()));
        topik.setStabilitasFsrs(hasil.kondisiMemori().stabilitas());
        topik.setKesulitanFsrs(hasil.kondisiMemori().kesulitan());
        topik.setRetensiDiinginkan(targetRetensi(topik));
        topik.setInterval(intervalBaru);
        topik.setTanggalUlasanTerakhir(LocalDate.now());
        topik.setJumlahUlasan(topik.getJumlahUlasan() + 1);

        return LocalDate.now().plusDays(intervalBaru);
    }

    public static boolean perluUlasanHariIni(Topik topik) {
        if (topik.getTanggalBelajarPertama() == null) {
            return false;
        }

        AlgoritmaSM2.KondisiMemori kondisi = ambilKondisi(topik);
        
        if (kondisi == null || kondisi.adalahKosong()) {
            if (topik.getTanggalUlasanTerakhir() == null) {
                LocalDate tanggalHarusUlas = topik.getTanggalBelajarPertama().plusDays(1);
                return !LocalDate.now().isBefore(tanggalHarusUlas);
            }
            LocalDate tanggalUlasanBerikutnya = topik.getTanggalUlasanTerakhir().plusDays(topik.getInterval());
            return !LocalDate.now().isBefore(tanggalUlasanBerikutnya);
        }

        long hariSejakUlasan = topik.getTanggalUlasanTerakhir() != null
                ? ChronoUnit.DAYS.between(topik.getTanggalUlasanTerakhir(), LocalDate.now())
                : 0;
        
        if (hariSejakUlasan < 0) {
            return false;
        }
        
        double retrievability = FSRS.hitungRetrievability(kondisi, hariSejakUlasan);
        double targetRetensi = targetRetensi(topik);
        
        return retrievability <= targetRetensi;
    }

    public static double hitungPrioritasTopik(Topik topik, LocalDate tanggalUjian) {
        double prioritas = 0.0;

        double targetRetensi = targetRetensi(topik);
        AlgoritmaSM2.KondisiMemori kondisi = ambilKondisi(topik);
        long hariSejakUlasan = topik.getTanggalUlasanTerakhir() != null
                ? Math.max(0, ChronoUnit.DAYS.between(topik.getTanggalUlasanTerakhir(), LocalDate.now()))
                : 0;
        double retrievability = kondisi != null
                ? FSRS.hitungRetrievability(kondisi, hariSejakUlasan)
                : 0.0;

        prioritas += (topik.getPrioritas() / 5.0) * 25;
        prioritas += (topik.getTingkatKesulitan() / 5.0) * 20;

        if (tanggalUjian != null) {
            long hariMenujuUjian = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), tanggalUjian);
            if (hariMenujuUjian <= 0) {
                prioritas += 30;
            } else if (hariMenujuUjian <= 3) {
                prioritas += 30;
            } else if (hariMenujuUjian <= 7) {
                prioritas += 25;
            } else if (hariMenujuUjian <= 14) {
                prioritas += 20;
            } else if (hariMenujuUjian <= 30) {
                prioritas += 15;
            } else {
                prioritas += 10;
            }
        } else {
            prioritas += 10;
        }

        if (topik.getJumlahUlasan() == 0) {
            prioritas += 20;
        } else if (topik.getJumlahUlasan() <= 2) {
            prioritas += 15;
        } else if (topik.getJumlahUlasan() <= 5) {
            prioritas += 10;
        } else {
            prioritas += 5;
        }

        double jarakRetensi = Math.max(0.0, targetRetensi - retrievability);
        prioritas += jarakRetensi * 50;

        if (topik.isDikuasai()) {
            prioritas -= 20;
        }

        return prioritas;
    }

    public static String getDeskripsiInterval(int hari) {
        if (hari == 1) {
            return "Besok";
        } else if (hari <= 7) {
            return hari + " hari lagi";
        } else if (hari <= 30) {
            int minggu = hari / 7;
            return minggu + " minggu lagi";
        } else if (hari <= 365) {
            int bulan = hari / 30;
            return bulan + " bulan lagi";
        } else {
            int tahun = hari / 365;
            return tahun + " tahun lagi";
        }
    }

    private static AlgoritmaSM2.KondisiMemori ambilKondisi(Topik topik) {
        if (topik.getStabilitasFsrs() > 0 && topik.getKesulitanFsrs() > 0) {
            return new AlgoritmaSM2.KondisiMemori(topik.getStabilitasFsrs(), topik.getKesulitanFsrs());
        }
        if (topik.getJumlahUlasan() > 0) {
            try {
                return FSRS.kondisiAwalDariSm2(
                        topik.getFaktorKemudahan(),
                        topik.getInterval(),
                        targetRetensi(topik));
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private static double targetRetensi(Topik topik) {
        return topik.getRetensiDiinginkan() > 0 ? topik.getRetensiDiinginkan() : 0.9;
    }
}
