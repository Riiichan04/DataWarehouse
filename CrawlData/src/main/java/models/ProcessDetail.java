package models;

import lombok.Getter;

@Getter
public class ProcessDetail {
    private static ProcessDetail instance;
    private final int processId = 4;
    private String name;
    private String targetPath;

    private ProcessDetail() {
    }

    public static synchronized ProcessDetail getInstance() {
        if (instance == null) {
            instance = new ProcessDetail();
        }
        return instance;
    }

    public synchronized void initData(String name, String targetPath) {
        this.name = name;
        this.targetPath = targetPath;
    }

}