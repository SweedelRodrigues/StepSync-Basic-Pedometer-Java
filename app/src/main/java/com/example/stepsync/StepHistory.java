package com.example.stepsync;

public class StepHistory {

    String date;
    long steps;

    public StepHistory() {}

    public StepHistory(String date, long steps) {
        this.date = date;
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public long getSteps() {
        return steps;
    }
}

