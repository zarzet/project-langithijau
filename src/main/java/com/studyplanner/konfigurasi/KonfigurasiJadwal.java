package com.studyplanner.konfigurasi;

/**
 * Konfigurasi parameter untuk penjadwalan.
 * Menyediakan konstanta untuk pengaturan jadwal belajar.
 */
public final class KonfigurasiJadwal {

    private KonfigurasiJadwal() {
        // Prevent instantiation
    }

    // ============================================
    // Konstanta Sesi Belajar
    // ============================================
    
    /**
     * Jumlah maksimum sesi belajar per hari.
     */
    public static final int MAKS_SESI_PER_HARI = 6;
    
    /**
     * Jumlah minimum sesi belajar per hari.
     */
    public static final int MIN_SESI_PER_HARI = 3;
    
    /**
     * Durasi default sesi belajar dalam menit.
     */
    public static final int DURASI_SESI_DEFAULT_MENIT = 30;
    
    /**
     * Durasi sesi review dalam menit.
     */
    public static final int DURASI_SESI_REVIEW_MENIT = 25;
    
    /**
     * Durasi sesi pembelajaran baru dalam menit.
     */
    public static final int DURASI_SESI_BARU_MENIT = 45;
    
    // ============================================
    // Konstanta Rentang Waktu
    // ============================================
    
    /**
     * Jumlah hari ke depan default untuk penjadwalan.
     */
    public static final int HARI_KE_DEPAN_DEFAULT = 7;
    
    /**
     * Jumlah hari sebelum ujian untuk intensifikasi belajar.
     */
    public static final int HARI_SEBELUM_UJIAN_INTENSIF = 7;
    
    // ============================================
    // Konstanta Prioritas
    // ============================================
    
    /**
     * Prioritas minimum (terendah).
     */
    public static final int PRIORITAS_MINIMUM = 1;
    
    /**
     * Prioritas maksimum (tertinggi).
     */
    public static final int PRIORITAS_MAKSIMUM = 5;
    
    /**
     * Prioritas default untuk topik baru.
     */
    public static final int PRIORITAS_DEFAULT = 3;
    
    // ============================================
    // Konstanta Tingkat Kesulitan
    // ============================================
    
    /**
     * Tingkat kesulitan minimum.
     */
    public static final int KESULITAN_MINIMUM = 1;
    
    /**
     * Tingkat kesulitan maksimum.
     */
    public static final int KESULITAN_MAKSIMUM = 5;
    
    /**
     * Tingkat kesulitan default untuk topik baru.
     */
    public static final int KESULITAN_DEFAULT = 3;
    
    // ============================================
    // Konstanta Retensi
    // ============================================
    
    /**
     * Retensi diinginkan default (90%).
     */
    public static final double RETENSI_DIINGINKAN_DEFAULT = 0.9;
    
    /**
     * Faktor kemudahan awal (SM-2).
     */
    public static final double FAKTOR_KEMUDAHAN_AWAL = 2.5;
}
