package com.example.familypills.data.model;

/**
 * Statistics response from backend
 */
public class StatsResponse {
    private int totalMedicines;
    private int runningLowCount;
    private int expiredCount;
    private int expiredSoon;
    private String lastUpdated;

    public StatsResponse() {
    }

    public StatsResponse(int totalMedicines, int runningLowCount, int expiredCount, int expiredSoon, String lastUpdated) {
        this.totalMedicines = totalMedicines;
        this.runningLowCount = runningLowCount;
        this.expiredCount = expiredCount;
        this.expiredSoon = expiredSoon;
        this.lastUpdated = lastUpdated;
    }

    public int getTotalMedicines() { return totalMedicines; }
    public void setTotalMedicines(int totalMedicines) { this.totalMedicines = totalMedicines; }

    public int getRunningLowCount() { return runningLowCount; }
    public void setRunningLowCount(int runningLowCount) { this.runningLowCount = runningLowCount; }

    public int getExpiredCount() { return expiredCount; }
    public void setExpiredCount(int expiredCount) { this.expiredCount = expiredCount; }

    public int getExpiredSoon() { return expiredSoon; }
    public void setExpiredSoon(int expiredSoon) { this.expiredSoon = expiredSoon; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
