package enums;

import lombok.Getter;

@Getter
public enum CrawlType {
    NORTH(1), MIDDLE(2), SOUTH(3);
    private final int value;

    CrawlType(int value) {
        this.value = value;
    }

}
