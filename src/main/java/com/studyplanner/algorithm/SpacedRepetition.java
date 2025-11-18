package com.studyplanner.algorithm;

import com.studyplanner.model.Topic;
import java.time.LocalDate;

public class SpacedRepetition {
    
    public static LocalDate calculateNextReviewDate(Topic topic, int performanceRating) {
        if (performanceRating < 0) performanceRating = 0;
        if (performanceRating > 5) performanceRating = 5;
        
        double currentEF = topic.getEasinessFactor();
        int currentInterval = topic.getInterval();
        int reviewCount = topic.getReviewCount();
        
        double newEF = currentEF + (0.1 - (5 - performanceRating) * (0.08 + (5 - performanceRating) * 0.02));
        
        if (newEF < 1.3) {
            newEF = 1.3;
        }
        
        topic.setEasinessFactor(newEF);
        
        int newInterval;
        
        if (performanceRating < 3) {
            newInterval = 1;
            topic.setReviewCount(0);
        } else {
            if (reviewCount == 0) {
                newInterval = 1;
            } else if (reviewCount == 1) {
                newInterval = 6;
            } else {
                newInterval = (int) Math.ceil(currentInterval * newEF);
            }
            
            topic.setReviewCount(reviewCount + 1);
        }
        
        topic.setInterval(newInterval);
        
        if (reviewCount >= 5 && performanceRating >= 4 && newInterval >= 30) {
            topic.setMastered(true);
        }
        
        LocalDate nextReviewDate = LocalDate.now().plusDays(newInterval);
        topic.setLastReviewDate(LocalDate.now());
        
        return nextReviewDate;
    }
    
    public static boolean needsReviewToday(Topic topic) {
        if (topic.getFirstStudyDate() == null) {
            return false;
        }
        
        if (topic.isMastered() && topic.getInterval() > 60) {
            return false;
        }
        
        if (topic.getLastReviewDate() == null) {
            LocalDate shouldReviewDate = topic.getFirstStudyDate().plusDays(1);
            return !LocalDate.now().isBefore(shouldReviewDate);
        }
        
        LocalDate nextReviewDate = topic.getLastReviewDate().plusDays(topic.getInterval());
        
        return !LocalDate.now().isBefore(nextReviewDate);
    }
    
    public static double calculateTopicPriority(Topic topic, LocalDate examDate) {
        double priority = 0.0;
        
        priority += (topic.getPriority() / 5.0) * 30;
        
        priority += (topic.getDifficultyLevel() / 5.0) * 20;
        
        if (examDate != null) {
            long daysUntilExam = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), examDate);
            if (daysUntilExam <= 0) {
                priority += 30;
            } else if (daysUntilExam <= 3) {
                priority += 30;
            } else if (daysUntilExam <= 7) {
                priority += 25;
            } else if (daysUntilExam <= 14) {
                priority += 20;
            } else if (daysUntilExam <= 30) {
                priority += 15;
            } else {
                priority += 10;
            }
        } else {
            priority += 10;
        }
        
        if (topic.getReviewCount() == 0) {
            priority += 20;
        } else if (topic.getReviewCount() <= 2) {
            priority += 15;
        } else if (topic.getReviewCount() <= 5) {
            priority += 10;
        } else {
            priority += 5;
        }
        
        if (needsReviewToday(topic)) {
            priority += 15;
        }
        
        if (topic.isMastered()) {
            priority -= 20;
        }
        
        return priority;
    }
    
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

