package com.studyplanner.algorithm;

import com.studyplanner.model.*;
import com.studyplanner.database.DatabaseManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generator Jadwal Belajar dengan Interleaving dan Spaced Repetition
 */
public class ScheduleGenerator {
    private DatabaseManager dbManager;
    private static final int MAX_SESSIONS_PER_DAY = 6;
    private static final int MIN_SESSIONS_PER_DAY = 3;
    
    public ScheduleGenerator(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    /**
     * Generate jadwal belajar untuk beberapa hari ke depan
     * 
     * @param daysAhead Jumlah hari ke depan yang akan dijadwalkan
     * @return Map dengan key tanggal dan value list StudySession
     */
    public Map<LocalDate, List<StudySession>> generateSchedule(int daysAhead) throws SQLException {
        Map<LocalDate, List<StudySession>> schedule = new LinkedHashMap<>();
        
        // Ambil semua data yang diperlukan
        List<Course> courses = dbManager.getAllCourses();
        List<Topic> allTopics = dbManager.getAllTopics();
        List<ExamSchedule> upcomingExams = dbManager.getUpcomingExams();
        
        // Buat map exam berdasarkan course ID untuk pencarian cepat
        Map<Integer, ExamSchedule> examByCourse = new HashMap<>();
        for (ExamSchedule exam : upcomingExams) {
            examByCourse.put(exam.getCourseId(), exam);
        }
        
        // Generate untuk setiap hari
        for (int day = 0; day < daysAhead; day++) {
            LocalDate targetDate = LocalDate.now().plusDays(day);
            List<StudySession> dailySessions = generateDailySchedule(
                targetDate, allTopics, examByCourse, courses
            );
            
            if (!dailySessions.isEmpty()) {
                schedule.put(targetDate, dailySessions);
            }
        }
        
        return schedule;
    }
    
    /**
     * Generate jadwal untuk satu hari dengan menerapkan Interleaving
     */
    private List<StudySession> generateDailySchedule(
            LocalDate date, 
            List<Topic> allTopics, 
            Map<Integer, ExamSchedule> examByCourse,
            List<Course> courses) {
        
        List<StudySession> sessions = new ArrayList<>();
        
        // Filter topik yang belum mastered
        List<Topic> activeTopics = allTopics.stream()
                .filter(t -> !t.isMastered())
                .collect(Collectors.toList());
        
        if (activeTopics.isEmpty()) {
            return sessions;
        }
        
        // Hitung prioritas untuk setiap topik
        List<TopicWithPriority> prioritizedTopics = new ArrayList<>();
        for (Topic topic : activeTopics) {
            ExamSchedule exam = examByCourse.get(topic.getCourseId());
            LocalDate examDate = (exam != null) ? exam.getExamDate() : null;
            double priority = SpacedRepetition.calculateTopicPriority(topic, examDate);
            
            prioritizedTopics.add(new TopicWithPriority(topic, priority));
        }
        
        // Sort berdasarkan prioritas (descending)
        prioritizedTopics.sort((a, b) -> Double.compare(b.priority, a.priority));
        
        // Implementasi Interleaving: Pilih topik dari berbagai mata kuliah
        Set<Integer> usedCourses = new HashSet<>();
        List<Topic> selectedTopics = new ArrayList<>();
        
        // Putaran 1: Ambil 1 topik prioritas tertinggi dari setiap course
        for (TopicWithPriority tp : prioritizedTopics) {
            if (!usedCourses.contains(tp.topic.getCourseId())) {
                selectedTopics.add(tp.topic);
                usedCourses.add(tp.topic.getCourseId());
                
                if (selectedTopics.size() >= MIN_SESSIONS_PER_DAY) {
                    break;
                }
            }
        }
        
        // Putaran 2: Jika masih kurang, tambahkan topik prioritas tinggi lainnya
        for (TopicWithPriority tp : prioritizedTopics) {
            if (!selectedTopics.contains(tp.topic) && selectedTopics.size() < MAX_SESSIONS_PER_DAY) {
                selectedTopics.add(tp.topic);
            }
        }
        
        // Buat study session untuk setiap topik yang dipilih
        for (Topic topic : selectedTopics) {
            StudySession session = new StudySession();
            session.setTopicId(topic.getId());
            session.setCourseId(topic.getCourseId());
            session.setScheduledDate(date);
            
            // Tentukan tipe sesi
            if (topic.getFirstStudyDate() == null) {
                session.setSessionType("INITIAL_STUDY");
                session.setDurationMinutes(45); // Sesi awal lebih lama
            } else if (SpacedRepetition.needsReviewToday(topic)) {
                session.setSessionType("REVIEW");
                session.setDurationMinutes(30);
            } else {
                session.setSessionType("PRACTICE");
                session.setDurationMinutes(30);
            }
            
            // Set nama untuk display
            session.setTopicName(topic.getName());
            
            // Cari nama course
            for (Course course : courses) {
                if (course.getId() == topic.getCourseId()) {
                    session.setCourseName(course.getCode() + " - " + course.getName());
                    break;
                }
            }
            
            sessions.add(session);
        }
        
        // Shuffle untuk variasi (tetap mempertahankan interleaving)
        Collections.shuffle(sessions);
        
        return sessions;
    }
    
    /**
     * Generate dan simpan jadwal ke database
     */
    public void generateAndSaveSchedule(int daysAhead) throws SQLException {
        Map<LocalDate, List<StudySession>> schedule = generateSchedule(daysAhead);
        
        for (Map.Entry<LocalDate, List<StudySession>> entry : schedule.entrySet()) {
            for (StudySession session : entry.getValue()) {
                // Cek apakah sudah ada sesi yang sama
                List<StudySession> existingSessions = dbManager.getSessionsByDate(entry.getKey());
                boolean exists = existingSessions.stream()
                        .anyMatch(s -> s.getTopicId() == session.getTopicId() 
                                    && s.getSessionType().equals(session.getSessionType()));
                
                if (!exists) {
                    dbManager.addStudySession(session);
                }
            }
        }
    }
    
    /**
     * Helper class untuk menyimpan topic dengan prioritasnya
     */
    private static class TopicWithPriority {
        Topic topic;
        double priority;
        
        TopicWithPriority(Topic topic, double priority) {
            this.topic = topic;
            this.priority = priority;
        }
    }
    
    /**
     * Mendapatkan statistik progress belajar
     */
    public StudyProgress getStudyProgress() throws SQLException {
        List<Topic> allTopics = dbManager.getAllTopics();
        List<StudySession> todaySessions = dbManager.getTodaySessions();
        
        int totalTopics = allTopics.size();
        int masteredTopics = (int) allTopics.stream().filter(Topic::isMastered).count();
        int todayCompleted = (int) todaySessions.stream().filter(StudySession::isCompleted).count();
        int todayTotal = todaySessions.size();
        
        StudyProgress progress = new StudyProgress();
        progress.setTotalTopics(totalTopics);
        progress.setMasteredTopics(masteredTopics);
        progress.setTodayCompleted(todayCompleted);
        progress.setTodayTotal(todayTotal);
        
        if (totalTopics > 0) {
            progress.setOverallProgress((masteredTopics * 100.0) / totalTopics);
        }
        
        if (todayTotal > 0) {
            progress.setTodayProgress((todayCompleted * 100.0) / todayTotal);
        }
        
        return progress;
    }
    
    /**
     * Class untuk menyimpan informasi progress
     */
    public static class StudyProgress {
        private int totalTopics;
        private int masteredTopics;
        private int todayTotal;
        private int todayCompleted;
        private double overallProgress;
        private double todayProgress;
        
        // Getters and Setters
        public int getTotalTopics() { return totalTopics; }
        public void setTotalTopics(int totalTopics) { this.totalTopics = totalTopics; }
        
        public int getMasteredTopics() { return masteredTopics; }
        public void setMasteredTopics(int masteredTopics) { this.masteredTopics = masteredTopics; }
        
        public int getTodayTotal() { return todayTotal; }
        public void setTodayTotal(int todayTotal) { this.todayTotal = todayTotal; }
        
        public int getTodayCompleted() { return todayCompleted; }
        public void setTodayCompleted(int todayCompleted) { this.todayCompleted = todayCompleted; }
        
        public double getOverallProgress() { return overallProgress; }
        public void setOverallProgress(double overallProgress) { this.overallProgress = overallProgress; }
        
        public double getTodayProgress() { return todayProgress; }
        public void setTodayProgress(double todayProgress) { this.todayProgress = todayProgress; }
    }
}

