package enums;

public enum LogLevel {
    PENDING(1),
    SUCCESS(10),
    ERROR(100);

    private final int level;

    LogLevel(int value) {
        this.level = value;
    }
    public int getLevel() {
        return level;
    }

}
