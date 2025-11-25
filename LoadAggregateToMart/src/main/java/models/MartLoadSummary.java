package models;

import java.util.List;

public class MartLoadSummary {
    private boolean success;
    private int totalTables;
    private int successTables;
    private int failedTables;
    private List<MartTableLoadDetail> details;

    public MartLoadSummary(boolean success, int totalTables, int successTables, int failedTables, List<MartTableLoadDetail> details) {
        this.success = success;
        this.totalTables = totalTables;
        this.successTables = successTables;
        this.failedTables = failedTables;
        this.details = details;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotalTables() {
        return totalTables;
    }

    public void setTotalTables(int totalTables) {
        this.totalTables = totalTables;
    }

    public int getSuccessTables() {
        return successTables;
    }

    public void setSuccessTables(int successTables) {
        this.successTables = successTables;
    }

    public int getFailedTables() {
        return failedTables;
    }

    public void setFailedTables(int failedTables) {
        this.failedTables = failedTables;
    }

    public List<MartTableLoadDetail> getDetails() {
        return details;
    }

    public void setDetails(List<MartTableLoadDetail> details) {
        this.details = details;
    }

    // getters & setters
    
}


