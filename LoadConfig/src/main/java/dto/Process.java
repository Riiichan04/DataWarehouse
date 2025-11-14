package dto;

public class Process {
    int id;
    String scriptName;
    String targetPath;

    public Process(int id, String scriptName, String targetPath) {
        this.id = id;
        this.scriptName = scriptName;
        this.targetPath = targetPath;
    }

    public Process() {
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", scriptName='" + scriptName + '\'' +
                ", targetPath='" + targetPath + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
