package models;

import enums.MartLoadStatus;

public class MartTableLoadDetail {
    private String tableName;
    private int rowCount;
    private MartLoadStatus status;
    private String message;

    public MartTableLoadDetail(String tableName, int rowCount, MartLoadStatus status, String message) {
        this.tableName = tableName;
        this.rowCount = rowCount;
        this.status = status;
        this.message = message;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public MartLoadStatus getStatus() {
        return status;
    }

    public void setStatus(MartLoadStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // getters & setters
}

