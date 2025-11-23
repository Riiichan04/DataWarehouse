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
public class DimRegion {
    private int id;
    private String codeRegion;
    private String name;
    private String description;
    private Timestamp createdAt;
    private Timestamp expiredAt;

}
