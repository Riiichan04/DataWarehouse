package enums;

import lombok.Getter;

@Getter
public enum StorageType {
    STAGING(1),
    WAREHOUSE(2),
    DATA_MART(4);
    private final int id;

    StorageType(int id) {
        this.id = id;
    }
}