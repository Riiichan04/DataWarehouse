package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotteryStaging {
    private int id;
    private String date;
    private String companyName;
    private String prizeName;
    private String regionName;
    private String result;
    private String createdAt;

}
