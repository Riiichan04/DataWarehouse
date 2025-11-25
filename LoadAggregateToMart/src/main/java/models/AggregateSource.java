package models;

public class AggregateSource {
    private String fileName;
    private long fileSize;
    private String generatedDate;

    public AggregateSource(String fileName, long fileSize, String generatedDate) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.generatedDate = generatedDate;
    }

    // getters & setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }
}