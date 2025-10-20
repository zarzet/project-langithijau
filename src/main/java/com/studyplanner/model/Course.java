package com.studyplanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk Mata Kuliah
 */
public class Course {
    private int id;
    private String name;
    private String code;
    private String description;
    private List<Topic> topics;

    public Course() {
        this.topics = new ArrayList<>();
    }

    public Course(int id, String name, String code, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.topics = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}

