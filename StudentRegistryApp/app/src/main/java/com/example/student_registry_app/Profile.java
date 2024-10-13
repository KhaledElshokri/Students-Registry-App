package com.example.student_registry_app;

public class Profile {
    private int profileID;
    private int studentID;
    private String name;
    private String surname;
    private double gpa;
    private String creationDate;

    // Constructor
    public Profile(int profileID, String name, String surname, double gpa, String creationDate, int studentID)
    {
        this.profileID = profileID;
        this.studentID = studentID;
        this.name = name;
        this.surname = surname;
        this.gpa = gpa;
        this.creationDate = creationDate;
    }

    // Getters and Setters
    public int getProfileID() { return profileID; }
    public void setProfileID(int profileID) { this.profileID = profileID; }

    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public double getGPA() { return gpa; }
    public void setGPA(double gpa) { this.gpa = gpa; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
}

