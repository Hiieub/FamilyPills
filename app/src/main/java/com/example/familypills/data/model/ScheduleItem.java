package com.example.familypills.data.model;

public class ScheduleItem {
    public enum Status { MISSED, TAKEN, UPCOMING }
    public enum TimeOfDay { MORNING, NOON, EVENING, NIGHT }

    private String id;
    private String time;
    private String medicineName;
    private String dosage;
    private Status status;
    private TimeOfDay timeOfDay;

    public ScheduleItem(String id, String time, String medicineName, String dosage, Status status, TimeOfDay timeOfDay) {
        this.id = id;
        this.time = time;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.status = status;
        this.timeOfDay = timeOfDay;
    }

    public String getId() { return id; }
    public String getTime() { return time; }
    public String getMedicineName() { return medicineName; }
    public String getDosage() { return dosage; }
    public Status getStatus() { return status; }
    public TimeOfDay getTimeOfDay() { return timeOfDay; }

    public void setStatus(Status status) { this.status = status; }
}
