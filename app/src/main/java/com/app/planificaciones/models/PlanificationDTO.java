package com.app.planificaciones.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Esta clase tiene exactamente los mismo campos que el documento de la colección "planification" de Firebase,
 * cuando desee guardar un documento en la colección "planification" de Firebase, debe crear un objeto de esta clase
 */
public class PlanificationDTO implements Serializable {

    private String dateCreated;
    private String details;
    private boolean status;
    private boolean deleted;
    private Long timestamp;
    private String title;
    private String week;

    private List<Map<String, Object>> details_planification = new ArrayList<>();

    private List<Map<String, Object>> resource = new ArrayList<>();

    public PlanificationDTO() {

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public List<Map<String, Object>> getDetails_planification() {
        return details_planification;
    }

    public void setDetails_planification(List<Map<String, Object>> details_planification) {
        this.details_planification = details_planification;
    }

    public List<Map<String, Object>> getResource() {
        return resource;
    }

    public void setResource(List<Map<String, Object>> resource) {
        this.resource = resource;
    }
}
