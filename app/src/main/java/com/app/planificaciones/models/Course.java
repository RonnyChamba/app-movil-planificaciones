package com.app.planificaciones.models;

import java.io.Serializable;
import java.util.Map;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String name;
    private String parallel;
    private String description;

    private Map<String, Object> tutor;
    private Teacher teacher;

    private String periodo;


    public Course() {
    }

    public Course(String name, String parallel, String description, String periodo, Map<String, Object> tutor) {
        this.name = name;
        this.parallel = parallel;
        this.description = description;
        this.periodo = periodo;
        this.tutor = tutor;
    }

    public Map<String, Object> getTutor() {
        return tutor;
    }

    public void setTutor(Map<String, Object> tutor) {
        this.tutor = tutor;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParallel() {
        return parallel;
    }

    public void setParallel(String parallel) {
        this.parallel = parallel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Course{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", parallel='" + parallel + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
