package com.example.myattendence.activity;

public class Each_student_attendence {
    private String id,attempt;


    public Each_student_attendence(String id, String attempt) {
        this.id = id;
        this.attempt = attempt;
    }

    public String getId() {
        return id;
    }

    public String getAttempt() {
        return attempt;
    }
}
