package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {
    private int id;
    private String name;
    private int type; // 1: Staging, 2: Warehouse, etc.
    private String host;
    private String port;
    private String username;
    private String password;
}
