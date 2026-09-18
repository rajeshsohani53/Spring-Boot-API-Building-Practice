package com.rajesh.model;
public class Student {
    private int id;
    private String name;
    private String course;

    // No-arg constructor — Jackson needs this to create an empty object first
    public Student() {
    }

    public Student(int id, String name, String course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    // Getters (for writing JSON — Part 4)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }

    // Setters (for reading JSON — Part 5)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCourse(String course) { this.course = course; }
}