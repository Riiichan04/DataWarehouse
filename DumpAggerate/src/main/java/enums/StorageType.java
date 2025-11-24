package enums;

public enum StorageType {
    STAGING(1),
    WAREHOUSE(2),
    DATA_MART(3);

    private int id;

    StorageType(int id) {}

    public int getType() {
        return this.id;
    }
}
