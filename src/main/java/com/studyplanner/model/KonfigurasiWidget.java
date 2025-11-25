package com.studyplanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk menyimpan konfigurasi widget yang ditampilkan di dashboard.
 */
public class KonfigurasiWidget {

    /**
     * Enum untuk jenis-jenis widget yang tersedia.
     */
    public enum JenisWidget {
        RUNTUTAN_BELAJAR("Hari Beruntun", "Lacak runtutan belajar harian Anda", "streak"),
        JAM_ANALOG("Jam", "Tampilkan jam analog", "clock"),
        WAKTU_BELAJAR("Waktu Belajar Hari Ini", "Lihat total waktu belajar hari ini", "study_time"),
        ULASAN_BERIKUTNYA("Ulasan Berikutnya", "Topik yang perlu diulang", "next_review"),
        TUGAS_MENDATANG("Tugas Mendatang", "Daftar tugas dalam beberapa hari ke depan", "upcoming_tasks");

        private final String namaDisplay;
        private final String deskripsi;
        private final String kode;

        JenisWidget(String namaDisplay, String deskripsi, String kode) {
            this.namaDisplay = namaDisplay;
            this.deskripsi = deskripsi;
            this.kode = kode;
        }

        public String getNamaDisplay() {
            return namaDisplay;
        }

        public String getDeskripsi() {
            return deskripsi;
        }

        public String getKode() {
            return kode;
        }

        public static JenisWidget dariKode(String kode) {
            for (JenisWidget jenis : values()) {
                if (jenis.kode.equals(kode)) {
                    return jenis;
                }
            }
            return null;
        }
    }

    private List<JenisWidget> widgetAktif;

    public KonfigurasiWidget() {
        this.widgetAktif = new ArrayList<>();
    }

    public KonfigurasiWidget(List<JenisWidget> widgetAktif) {
        this.widgetAktif = new ArrayList<>(widgetAktif);
    }

    /**
     * Dapatkan daftar widget yang aktif (dalam urutan tampilan).
     */
    public List<JenisWidget> getWidgetAktif() {
        return new ArrayList<>(widgetAktif);
    }

    /**
     * Atur daftar widget yang aktif.
     */
    public void setWidgetAktif(List<JenisWidget> widgetAktif) {
        this.widgetAktif = new ArrayList<>(widgetAktif);
    }

    /**
     * Tambah widget ke daftar aktif.
     */
    public void tambahWidget(JenisWidget widget) {
        if (!widgetAktif.contains(widget)) {
            widgetAktif.add(widget);
        }
    }

    /**
     * Hapus widget dari daftar aktif.
     */
    public void hapusWidget(JenisWidget widget) {
        widgetAktif.remove(widget);
    }

    /**
     * Pindahkan widget ke posisi baru (untuk drag & drop).
     */
    public void pindahWidget(int dariIndex, int keIndex) {
        if (dariIndex < 0 || dariIndex >= widgetAktif.size() ||
            keIndex < 0 || keIndex >= widgetAktif.size()) {
            return;
        }
        JenisWidget widget = widgetAktif.remove(dariIndex);
        widgetAktif.add(keIndex, widget);
    }

    /**
     * Cek apakah widget tertentu aktif.
     */
    public boolean isWidgetAktif(JenisWidget widget) {
        return widgetAktif.contains(widget);
    }

    /**
     * Konversi ke string untuk disimpan di preferensi.
     * Format: "kode1,kode2,kode3"
     */
    public String keString() {
        if (widgetAktif.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < widgetAktif.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(widgetAktif.get(i).getKode());
        }
        return sb.toString();
    }

    /**
     * Buat konfigurasi dari string.
     */
    public static KonfigurasiWidget dariString(String str) {
        KonfigurasiWidget config = new KonfigurasiWidget();
        if (str == null || str.trim().isEmpty()) {
            return config;
        }
        String[] kodes = str.split(",");
        for (String kode : kodes) {
            JenisWidget widget = JenisWidget.dariKode(kode.trim());
            if (widget != null) {
                config.tambahWidget(widget);
            }
        }
        return config;
    }

    /**
     * Buat konfigurasi default dengan semua widget aktif.
     */
    public static KonfigurasiWidget buatDefault() {
        KonfigurasiWidget config = new KonfigurasiWidget();
        config.tambahWidget(JenisWidget.RUNTUTAN_BELAJAR);
        config.tambahWidget(JenisWidget.JAM_ANALOG);
        config.tambahWidget(JenisWidget.WAKTU_BELAJAR);
        config.tambahWidget(JenisWidget.ULASAN_BERIKUTNYA);
        config.tambahWidget(JenisWidget.TUGAS_MENDATANG);
        return config;
    }
}
