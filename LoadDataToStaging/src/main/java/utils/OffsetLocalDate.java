package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OffsetLocalDate {
    private long days;
    private long weeks;
    private long months;
    private long years;

    public OffsetLocalDate() {
        this.days = 0;
        this.weeks = 0;
        this.months = 0;
        this.years = 0;
    }
}
