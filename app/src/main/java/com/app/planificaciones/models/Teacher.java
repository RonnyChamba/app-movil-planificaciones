package com.app.planificaciones.models;

import java.io.Serializable;
import java.util.List;

public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String displayName;
    private String email;
    private String photoUrl;
    private String phoneNumber;
    private String dni;
    private String lastName;
    private String rol;
    private String status;
    private List<String> titles;
    private List<String> courses;
    private List<Course> coursesList;

    public Teacher() {
    }

    public Teacher(String uid, String displayName, String email, String photoUrl, String phoneNumber, String dni, String lastName, String rol, String status, List<String> titles, List<String> courses) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.dni = dni;
        this.lastName = lastName;
        this.rol = rol;
        this.status = status;
        this.titles = titles;
        this.courses = courses;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public List<Course> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(List<Course> coursesList) {
        this.coursesList = coursesList;
    }

    @Override
    public String toString() {

        return this.displayName + " " + this.lastName;
    }
}
