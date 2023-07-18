package com.app.planificaciones.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DetailsPlanification implements Serializable {


    private static final long serialVersionUID = 1L;

    private String uid;
    private String dateCreated;

    private String observation;

    private String planification;

    private boolean status;

    private List<Map<String, Object>> items;

    private Map<String, Object> resource;

    private Map<String, Object> teacher;

    // Para el detalle de la planificacion, aqui se setea la planificacion a la que pertenece
    private Planification planificationObject;

    public DetailsPlanification(String dateCreated, String observation, String planification, boolean status, List<Map<String, Object>> items, Map<String, Object> resource) {
        this.dateCreated = dateCreated;
        this.observation = observation;
        this.planification = planification;
        this.status = status;
        this.items = items;
        this.resource = resource;
    }

    public DetailsPlanification() {
    }

    public Planification getPlanificationObject() {
        return planificationObject;
    }

    public void setPlanificationObject(Planification planificationObject) {
        this.planificationObject = planificationObject;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getTeacher() {
        return teacher;
    }

    public void setTeacher(Map<String, Object> teacher) {
        this.teacher = teacher;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getPlanification() {
        return planification;
    }

    public void setPlanification(String planification) {
        this.planification = planification;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public Map<String, Object> getResource() {
        return resource;
    }

    public void setResource(Map<String, Object> resource) {
        this.resource = resource;
    }
}
