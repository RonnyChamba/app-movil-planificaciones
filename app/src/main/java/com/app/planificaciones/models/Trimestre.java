package com.app.planificaciones.models;

import java.io.Serializable;

public class Trimestre implements Serializable {

    private String uid;
    private String title;
    private String details;
    private String course;
    private Integer numberWeek;
    private Long timestamp;

    public Trimestre() {

    }

    public Trimestre(String uid, String title, String details, String course, Integer numberWeek, Long timestamp) {
        this.uid = uid;
        this.title = title;
        this.details = details;
        this.course = course;
        this.numberWeek = numberWeek;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getNumberWeek() {
        return numberWeek;
    }

    public void setNumberWeek(Integer numberWeek) {
        this.numberWeek = numberWeek;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return title;
    }
}
