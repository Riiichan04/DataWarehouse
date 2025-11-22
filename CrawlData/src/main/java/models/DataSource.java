package models;

import enums.CrawlType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataSource {
    private int sourceId;
    private String url;
    private String name;
    private CrawlType type;
}
