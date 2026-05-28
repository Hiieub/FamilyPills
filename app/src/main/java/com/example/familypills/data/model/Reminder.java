package com.example.familypills.data.model;

public class Reminder {
    private int id;
    
    private int medicineId;
    private String time;
    private String days; // Comma separated days like "2,3,5"
    private String usage;
    private String note;
    private boolean isActive = true;

    public Reminder(int medicineId, String time, String days, String usage, String note) {
        this.medicineId = medicineId;
        this.time = time;
        this.days = days;
        this.usage = usage;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getDays() { return days; }
    public void setDays(String days) { this.days = days; }
    
    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
