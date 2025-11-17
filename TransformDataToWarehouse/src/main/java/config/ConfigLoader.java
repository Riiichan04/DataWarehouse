package config;

import java.util.Properties;

public class ConfigLoader {
    // Cần xem lại
    private static final Properties config = new Properties();
    static {
        //load config
    }

    private static DatabaseConnection createDbConnection(String prefix) {
        return new DatabaseConnection(
                config.getProperty(prefix + "name"),
                "mysql",
                config.getProperty(prefix + "host"),
                config.getProperty(prefix + "port"),
                config.getProperty(prefix + "username"),
                config.getProperty(prefix + "password"),
                "useUnicode=true&characterEncoding=UTF-8" // Giả định encoding
        );
    }

    public static DatabaseConnection loadControlDBConfig() {
        return createDbConnection("db.control.");
    }

    public static DatabaseConnection loadStagingDBConfig() {
        return createDbConnection("db.staging.");
    }

}