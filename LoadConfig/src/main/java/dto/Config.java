package dto;

import java.util.List;

public class Config {
    String runDate;
    Source source;
     List<Process> processList;

    public Config() {
    }

    @Override
    public String toString() {
        return "Config{" +
                "runDate='" + runDate + '\'' +
                ", source=" + source +
                ", processList=" + processList +
                '}';
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }
}
