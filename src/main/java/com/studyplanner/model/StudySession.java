package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk Sesi Belajar
 */
public class StudySession {
    private int id;
    private int topicId;
    private int courseId;
    private LocalDate scheduledDate;
    private String sessionType; // "INITIAL_STUDY", "REVIEW", "PRACTICE"
    private boolean completed;
    private LocalDateTime completedAt;
    private int performanceRating; // 0-5, diisi setelah sesi selesai
    private String notes;
    private int durationMinutes; // Estimasi atau durasi aktual
    
    // Untuk keperluan display
    private String topicName;
    private String courseName;

    public StudySession() {
        this.completed = false;
        this.durationMinutes = 30; // Default 30 menit
    }

    public StudySession(int id, int topicId, int courseId, LocalDate scheduledDate, String sessionType) {
        this();
        this.id = id;
        this.topicId = topicId;
        this.courseId = courseId;
        this.scheduledDate = scheduledDate;
        this.sessionType = sessionType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public int getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(int performanceRating) {
        this.performanceRating = performanceRating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return sessionType + ": " + topicName + " (" + courseName + ")";
    }
}

