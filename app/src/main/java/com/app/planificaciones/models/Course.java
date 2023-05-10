package com.app.planificaciones.models;

public class Course {

    private String uid;
    private String name;
    private String parallel;
    private String description;
    private Teacher teacher;

    public Course(String name, String parallel, String description) {
        this.name = name;
        this.parallel = parallel;
        this.description = description;
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
