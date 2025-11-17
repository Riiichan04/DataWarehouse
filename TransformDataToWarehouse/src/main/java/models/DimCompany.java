package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DimCompany {
    private int id;
    private String codeCompany;
    private String name;
    private Timestamp createDate;
    private Date expiredDate;
}
