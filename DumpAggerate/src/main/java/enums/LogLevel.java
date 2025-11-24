package enums;

public enum LogLevel {
    SUCCESS(1),
    PENDING(10),
    ERROR(100);

    private final int level;

    LogLevel(int value) {
        this.level = value;
    }
    public int getLevel() {
        return level;
    }

}
