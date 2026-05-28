package com.example.familypills.data.model;

public class Medicine {
    private int id;
    
    private String name;
    private String barcode;
    private int totalQuantity;
    private String unit;
    private String expiryDate;
    private String imagePath;
    
    private String quantity;
    private String lastUpdated;
    private boolean isRunningLow;
    private boolean isExpired;
    private boolean isReminderEnabled;

    public Medicine() {
    }

    public Medicine(String name, String barcode, int totalQuantity, String unit, String expiryDate, String imagePath) {
        this.name = name;
        this.barcode = barcode;
        this.totalQuantity = totalQuantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.imagePath = imagePath;
    }

    public Medicine(String barcode, String name, String quantity, String expiryDate, String lastUpdated, boolean isRunningLow, boolean isExpired, boolean isReminderEnabled) {
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.lastUpdated = lastUpdated;
        this.isRunningLow = isRunningLow;
        this.isExpired = isExpired;
        this.isReminderEnabled = isReminderEnabled;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isRunningLow() { return isRunningLow; }
    public void setRunningLow(boolean runningLow) { isRunningLow = runningLow; }

    public boolean isExpired() { return isExpired; }
    public void setExpired(boolean expired) { isExpired = expired; }

    public boolean isReminderEnabled() { return isReminderEnabled; }
    public void setReminderEnabled(boolean reminderEnabled) { isReminderEnabled = reminderEnabled; }
}
