package com.studyplanner.algorithm;

import com.studyplanner.model.Topic;
import java.time.LocalDate;

/**
 * Implementasi Algoritma Spaced Repetition menggunakan SM-2 (SuperMemo 2)
 * 
 * Algoritma ini menghitung interval waktu optimal untuk mengulang materi
 * berdasarkan performa pengguna saat review.
 */
public class SpacedRepetition {
    
    /**
     * Menghitung tanggal review berikutnya berdasarkan performa
     * 
     * @param topic Topic yang direview
     * @param performanceRating Rating performa (0-5)
     *        0-2: Sulit/Lupa (reset interval)
     *        3: Cukup (interval bertambah sedikit)
     *        4: Baik (interval bertambah normal)
     *        5: Mudah (interval bertambah signifikan)
     * @return Tanggal untuk review berikutnya
     */
    public static LocalDate calculateNextReviewDate(Topic topic, int performanceRating) {
        // Validasi rating
        if (performanceRating < 0) performanceRating = 0;
        if (performanceRating > 5) performanceRating = 5;
        
        double currentEF = topic.getEasinessFactor();
        int currentInterval = topic.getInterval();
        int reviewCount = topic.getReviewCount();
        
        // Update Easiness Factor (EF) berdasarkan rating
        // Formula SM-2: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        double newEF = currentEF + (0.1 - (5 - performanceRating) * (0.08 + (5 - performanceRating) * 0.02));
        
        // EF tidak boleh kurang dari 1.3
        if (newEF < 1.3) {
            newEF = 1.3;
        }
        
        topic.setEasinessFactor(newEF);
        
        int newInterval;
        
        // Jika rating < 3, reset interval (materi sulit/lupa)
        if (performanceRating < 3) {
            newInterval = 1;
            topic.setReviewCount(0); // Reset review count
        } else {
            // Hitung interval baru
            if (reviewCount == 0) {
                // Review pertama: 1 hari
                newInterval = 1;
            } else if (reviewCount == 1) {
                // Review kedua: 6 hari
                newInterval = 6;
            } else {
                // Review selanjutnya: interval sebelumnya * EF
                newInterval = (int) Math.ceil(currentInterval * newEF);
            }
            
            topic.setReviewCount(reviewCount + 1);
        }
        
        topic.setInterval(newInterval);
        
        // Tandai sebagai mastered jika sudah direview 5+ kali dengan performa baik
        if (reviewCount >= 5 && performanceRating >= 4 && newInterval >= 30) {
            topic.setMastered(true);
        }
        
        // Hitung tanggal review berikutnya
        LocalDate nextReviewDate = LocalDate.now().plusDays(newInterval);
        topic.setLastReviewDate(LocalDate.now());
        
        return nextReviewDate;
    }
    
    /**
     * Menentukan apakah topik perlu direview hari ini
     */
    public static boolean needsReviewToday(Topic topic) {
        // Jika belum pernah dipelajari
        if (topic.getFirstStudyDate() == null) {
            return false;
        }
        
        // Jika sudah mastered dan interval > 60 hari, tidak perlu review sering
        if (topic.isMastered() && topic.getInterval() > 60) {
            return false;
        }
        
        // Jika belum pernah direview, review setelah 1 hari
        if (topic.getLastReviewDate() == null) {
            LocalDate shouldReviewDate = topic.getFirstStudyDate().plusDays(1);
            return !LocalDate.now().isBefore(shouldReviewDate);
        }
        
        // Hitung tanggal review berikutnya berdasarkan interval
        LocalDate nextReviewDate = topic.getLastReviewDate().plusDays(topic.getInterval());
        
        // Perlu review jika hari ini >= tanggal review
        return !LocalDate.now().isBefore(nextReviewDate);
    }
    
    /**
     * Menghitung prioritas topik untuk dijadwalkan
     * Prioritas lebih tinggi = lebih penting untuk dipelajari
     */
    public static double calculateTopicPriority(Topic topic, LocalDate examDate) {
        double priority = 0.0;
        
        // Faktor 1: Prioritas yang ditetapkan pengguna (bobot 30%)
        priority += (topic.getPriority() / 5.0) * 30;
        
        // Faktor 2: Tingkat kesulitan (bobot 20%)
        priority += (topic.getDifficultyLevel() / 5.0) * 20;
        
        // Faktor 3: Kedekatan dengan ujian (bobot 30%)
        if (examDate != null) {
            long daysUntilExam = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), examDate);
            if (daysUntilExam <= 0) {
                priority += 30; // Ujian sudah lewat atau hari ini
            } else if (daysUntilExam <= 3) {
                priority += 30; // Sangat dekat
            } else if (daysUntilExam <= 7) {
                priority += 25; // Dekat
            } else if (daysUntilExam <= 14) {
                priority += 20; // Menengah
            } else if (daysUntilExam <= 30) {
                priority += 15; // Jauh
            } else {
                priority += 10; // Sangat jauh
            }
        } else {
            priority += 10; // Tidak ada ujian
        }
        
        // Faktor 4: Seberapa sering direview (bobot 20%)
        // Semakin jarang direview, semakin tinggi prioritas
        if (topic.getReviewCount() == 0) {
            priority += 20; // Belum pernah direview
        } else if (topic.getReviewCount() <= 2) {
            priority += 15;
        } else if (topic.getReviewCount() <= 5) {
            priority += 10;
        } else {
            priority += 5;
        }
        
        // Bonus: Jika topik perlu direview hari ini
        if (needsReviewToday(topic)) {
            priority += 15;
        }
        
        // Penalti: Jika sudah mastered
        if (topic.isMastered()) {
            priority -= 20;
        }
        
        return priority;
    }
    
    /**
     * Mendapatkan deskripsi interval dalam format yang mudah dibaca
     */
    public static String getIntervalDescription(int days) {
        if (days == 1) {
            return "Besok";
        } else if (days <= 7) {
            return days + " hari lagi";
        } else if (days <= 30) {
            int weeks = days / 7;
            return weeks + " minggu lagi";
        } else if (days <= 365) {
            int months = days / 30;
            return months + " bulan lagi";
        } else {
            int years = days / 365;
            return years + " tahun lagi";
        }
    }
}

