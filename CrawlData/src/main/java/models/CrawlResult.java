package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CrawlResult {
    private String name;
    private String date;
    private List<String> listPrize;
}
