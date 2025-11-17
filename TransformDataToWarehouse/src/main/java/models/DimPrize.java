package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DimPrize {
    private int id;
    private String codePrize;
    private String name;
    private Timestamp createdAt;
    private Timestamp expiredAt;

}
