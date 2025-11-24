package config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DatabaseConnection {
    private String name;
    private String type;
    private String host;
    private String port;
    private String username;
    private String password;
    private String characterEncoding = "useUnicode=true&characterEncoding=UTF-8";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

}