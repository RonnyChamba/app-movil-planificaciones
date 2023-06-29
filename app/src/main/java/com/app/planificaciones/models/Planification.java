package com.app.planificaciones.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Planification implements Serializable {


    private String uid;
    private String dateCreated;
    private String details;
    private boolean status;
    private boolean deleted;

    private String timestampDate;
    private String title;
    private String week;

    private List<Map<String, Object>> detail_planification;

    private List<Map<String, Object>> resources;

    public Planification() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    public String getTimestampDate() {
        return timestampDate;
    }

    public void setTimestampDate(String timestampDate) {
        this.timestampDate = timestampDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public List<Map<String, Object>> getDetail_planification() {
        return detail_planification;
    }

    public void setDetail_planification(List<Map<String, Object>> detail_planification) {
        this.detail_planification = detail_planification;
    }

    public List<Map<String, Object>> getResources() {
        return resources;
    }

    public void setResources(List<Map<String, Object>> resources) {
        this.resources = resources;
    }
}
