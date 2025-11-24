package config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    private String options;
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private Connection connection = null;

    public Connection getConnection() {
        try {
            if (connection == null) {
                createConnection();
            }
            return connection;
        } catch (SQLException e) {
            //Log here
            return null;
        }
    }

    private void createConnection() throws SQLException {
        try {
            Class.forName(MYSQL_DRIVER);
            String connectionString = "jdbc:mysql://" + host + ":" + port + "/" + name + "?" + options;
            connection = DriverManager.getConnection(connectionString, username, password);
        } catch (ClassNotFoundException ignored) {
        }
    }

    @Override
    public String toString() {
        return "DatabaseConnection{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", options='" + options + '\'' +
                '}';
    }
}
