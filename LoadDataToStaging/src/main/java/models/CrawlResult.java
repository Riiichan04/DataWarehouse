package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrawlResult {
    private String date;
    private String prizeName;
    private String companyName;
    private String result;
    private String createdAt;

    public CrawlResult(String... listParam) {
        this.date = listParam[0];
        this.prizeName = listParam[1];
        this.companyName = listParam[2];
        this.result = listParam[3];
        this.createdAt = listParam[4];
    }
}
