package com.studyplanner.model;

import java.time.LocalDate;

/**
 * Model untuk Topik dalam Mata Kuliah
 */
public class Topic {
    private int id;
    private int courseId;
    private String name;
    private String description;
    private int priority; // 1-5, dimana 5 adalah prioritas tertinggi
    private int difficultyLevel; // 1-5, dimana 5 adalah paling sulit
    private LocalDate firstStudyDate;
    private LocalDate lastReviewDate;
    private int reviewCount;
    private double easinessFactor; // Untuk algoritma SM-2 Spaced Repetition
    private int interval; // Interval hari untuk review berikutnya
    private boolean mastered; // Apakah topik sudah dikuasai

    public Topic() {
        this.priority = 3;
        this.difficultyLevel = 3;
        this.reviewCount = 0;
        this.easinessFactor = 2.5; // Default untuk SM-2
        this.interval = 1;
        this.mastered = false;
    }

    public Topic(int id, int courseId, String name, String description) {
        this();
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public LocalDate getFirstStudyDate() {
        return firstStudyDate;
    }

    public void setFirstStudyDate(LocalDate firstStudyDate) {
        this.firstStudyDate = firstStudyDate;
    }

    public LocalDate getLastReviewDate() {
        return lastReviewDate;
    }

    public void setLastReviewDate(LocalDate lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getEasinessFactor() {
        return easinessFactor;
    }

    public void setEasinessFactor(double easinessFactor) {
        this.easinessFactor = easinessFactor;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isMastered() {
        return mastered;
    }

    public void setMastered(boolean mastered) {
        this.mastered = mastered;
    }

    @Override
    public String toString() {
        return name;
    }
}

