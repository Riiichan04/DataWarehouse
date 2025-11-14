package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
    public static Properties loadProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(DbUtil.class.getClassLoader().getResourceAsStream("application.properties"));
        return prop;
    }

    public static Connection getConnection(Properties prop) throws SQLException {
        return DriverManager.getConnection(
                prop.getProperty("db.control.url"),
                prop.getProperty("db.control.username"),
                prop.getProperty("db.control.password")
        );
    }
}
