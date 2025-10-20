package com.studyplanner.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model untuk Jadwal Ujian/Kuis
 */
public class ExamSchedule {
    private int id;
    private int courseId;
    private String examType; // "MIDTERM", "FINAL", "QUIZ", "ASSIGNMENT"
    private String title;
    private LocalDate examDate;
    private LocalTime examTime;
    private String location;
    private String notes;
    private boolean completed;

    public ExamSchedule() {
        this.completed = false;
    }

    public ExamSchedule(int id, int courseId, String examType, String title, LocalDate examDate) {
        this();
        this.id = id;
        this.courseId = courseId;
        this.examType = examType;
        this.title = title;
        this.examDate = examDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public LocalTime getExamTime() {
        return examTime;
    }

    public void setExamTime(LocalTime examTime) {
        this.examTime = examTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getDaysUntilExam() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), examDate);
    }

    @Override
    public String toString() {
        return title + " (" + examDate + ")";
    }
}

