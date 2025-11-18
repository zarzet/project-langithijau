package com.studyplanner.algorithm;

import com.studyplanner.model.*;
import com.studyplanner.database.DatabaseManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleGenerator {
    private DatabaseManager dbManager;
    private static final int MAX_SESSIONS_PER_DAY = 6;
    private static final int MIN_SESSIONS_PER_DAY = 3;
    
    public ScheduleGenerator(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    public Map<LocalDate, List<StudySession>> generateSchedule(int daysAhead) throws SQLException {
        Map<LocalDate, List<StudySession>> schedule = new LinkedHashMap<>();
        
        List<Course> courses = dbManager.getAllCourses();
        List<Topic> allTopics = dbManager.getAllTopics();
        List<ExamSchedule> upcomingExams = dbManager.getUpcomingExams();
        
        Map<Integer, ExamSchedule> examByCourse = new HashMap<>();
        for (ExamSchedule exam : upcomingExams) {
            examByCourse.put(exam.getCourseId(), exam);
        }
        
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
    
    private List<StudySession> generateDailySchedule(
            LocalDate date, 
            List<Topic> allTopics, 
            Map<Integer, ExamSchedule> examByCourse,
            List<Course> courses) {
        
        List<StudySession> sessions = new ArrayList<>();
        
        List<Topic> activeTopics = allTopics.stream()
                .filter(t -> !t.isMastered())
                .collect(Collectors.toList());
        
        if (activeTopics.isEmpty()) {
            return sessions;
        }
        
        List<TopicWithPriority> prioritizedTopics = new ArrayList<>();
        for (Topic topic : activeTopics) {
            ExamSchedule exam = examByCourse.get(topic.getCourseId());
            LocalDate examDate = (exam != null) ? exam.getExamDate() : null;
            double priority = SpacedRepetition.calculateTopicPriority(topic, examDate);
            
            prioritizedTopics.add(new TopicWithPriority(topic, priority));
        }
        
        prioritizedTopics.sort((a, b) -> Double.compare(b.priority, a.priority));
        
        Set<Integer> usedCourses = new HashSet<>();
        List<Topic> selectedTopics = new ArrayList<>();
        
        for (TopicWithPriority tp : prioritizedTopics) {
            if (!usedCourses.contains(tp.topic.getCourseId())) {
                selectedTopics.add(tp.topic);
                usedCourses.add(tp.topic.getCourseId());
                
                if (selectedTopics.size() >= MIN_SESSIONS_PER_DAY) {
                    break;
                }
            }
        }
        
        for (TopicWithPriority tp : prioritizedTopics) {
            if (!selectedTopics.contains(tp.topic) && selectedTopics.size() < MAX_SESSIONS_PER_DAY) {
                selectedTopics.add(tp.topic);
            }
        }
        
        for (Topic topic : selectedTopics) {
            StudySession session = new StudySession();
            session.setTopicId(topic.getId());
            session.setCourseId(topic.getCourseId());
            session.setScheduledDate(date);
            
            if (topic.getFirstStudyDate() == null) {
                session.setSessionType("INITIAL_STUDY");
                session.setDurationMinutes(45);
            } else if (SpacedRepetition.needsReviewToday(topic)) {
                session.setSessionType("REVIEW");
                session.setDurationMinutes(30);
            } else {
                session.setSessionType("PRACTICE");
                session.setDurationMinutes(30);
            }
            
            session.setTopicName(topic.getName());
            
            for (Course course : courses) {
                if (course.getId() == topic.getCourseId()) {
                    session.setCourseName(course.getCode() + " - " + course.getName());
                    break;
                }
            }
            
            sessions.add(session);
        }
        
        Collections.shuffle(sessions);
        
        return sessions;
    }
    
    public void generateAndSaveSchedule(int daysAhead) throws SQLException {
        Map<LocalDate, List<StudySession>> schedule = generateSchedule(daysAhead);
        
        for (Map.Entry<LocalDate, List<StudySession>> entry : schedule.entrySet()) {
            for (StudySession session : entry.getValue()) {
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
    
    private static class TopicWithPriority {
        Topic topic;
        double priority;
        
        TopicWithPriority(Topic topic, double priority) {
            this.topic = topic;
            this.priority = priority;
        }
    }
    
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
    
    public static class StudyProgress {
        private int totalTopics;
        private int masteredTopics;
        private int todayTotal;
        private int todayCompleted;
        private double overallProgress;
        private double todayProgress;
        
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

