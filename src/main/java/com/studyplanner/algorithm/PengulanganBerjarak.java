package com.studyplanner.algorithm;

import com.studyplanner.model.Topik;
import java.time.LocalDate;

public class PengulanganBerjarak {

    public static LocalDate hitungTanggalUlasanBerikutnya(Topik topik, int ratingPerforma) {
        if (ratingPerforma < 0)
            ratingPerforma = 0;
        if (ratingPerforma > 5)
            ratingPerforma = 5;

        double faktorKemudahanSaatIni = topik.getFaktorKemudahan();
        int intervalSaatIni = topik.getInterval();
        int jumlahUlasan = topik.getJumlahUlasan();

        double faktorKemudahanBaru = faktorKemudahanSaatIni
                + (0.1 - (5 - ratingPerforma) * (0.08 + (5 - ratingPerforma) * 0.02));

        if (faktorKemudahanBaru < 1.3) {
            faktorKemudahanBaru = 1.3;
        }

        topik.setFaktorKemudahan(faktorKemudahanBaru);

        int intervalBaru;

        if (ratingPerforma < 3) {
            intervalBaru = 1;
            topik.setJumlahUlasan(0);
        } else {
            if (jumlahUlasan == 0) {
                intervalBaru = 1;
            } else if (jumlahUlasan == 1) {
                intervalBaru = 6;
            } else {
                intervalBaru = (int) Math.ceil(intervalSaatIni * faktorKemudahanBaru);
            }

            topik.setJumlahUlasan(jumlahUlasan + 1);
        }

        topik.setInterval(intervalBaru);

        if (jumlahUlasan >= 5 && ratingPerforma >= 4 && intervalBaru >= 30) {
            topik.setDikuasai(true);
        }

        LocalDate tanggalUlasanBerikutnya = LocalDate.now().plusDays(intervalBaru);
        topik.setTanggalUlasanTerakhir(LocalDate.now());

        return tanggalUlasanBerikutnya;
    }

    public static boolean perluUlasanHariIni(Topik topik) {
        if (topik.getTanggalBelajarPertama() == null) {
            return false;
        }

        if (topik.isDikuasai() && topik.getInterval() > 60) {
            return false;
        }

        if (topik.getTanggalUlasanTerakhir() == null) {
            LocalDate tanggalHarusUlas = topik.getTanggalBelajarPertama().plusDays(1);
            return !LocalDate.now().isBefore(tanggalHarusUlas);
        }

        LocalDate tanggalUlasanBerikutnya = topik.getTanggalUlasanTerakhir().plusDays(topik.getInterval());

        return !LocalDate.now().isBefore(tanggalUlasanBerikutnya);
    }

    public static double hitungPrioritasTopik(Topik topik, LocalDate tanggalUjian) {
        double prioritas = 0.0;

        prioritas += (topik.getPrioritas() / 5.0) * 30;

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

        if (perluUlasanHariIni(topik)) {
            prioritas += 15;
        }

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
}
