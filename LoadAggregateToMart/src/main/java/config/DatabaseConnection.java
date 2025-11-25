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

    private String host;
    private String port;
    private String name;
    private String username;
    private String password;
    private String options;

    private Connection connection;

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    public Connection getConnection() {
        if (connection == null) {
            try {
                createConnection();
            } catch (SQLException e) {
                System.out.println("❌ Cannot connect to database MART");
                return null;
            }
        }
        return connection;
    }

    private void createConnection() throws SQLException {
        try {
            Class.forName(MYSQL_DRIVER);

            String connectionString =
                    "jdbc:mysql://" + host + ":" + port + "/" + name + "?" + options;

            connection = DriverManager.getConnection(
                    connectionString,
                    username,
                    password
            );

            System.out.println("✅ Connected to MART: " + host + "/" + name);

        } catch (ClassNotFoundException ignored) {
            System.out.println("❌ Driver not found: " + MYSQL_DRIVER);
        }
    }
}

