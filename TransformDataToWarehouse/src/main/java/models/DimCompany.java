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
public class DimCompany {
    private int id;
    private String codeCompany;
    private String name;
    private int regionId;
    private Timestamp createdAt;
    private Timestamp expiredAt;
}
