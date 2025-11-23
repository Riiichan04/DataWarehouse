package config;

import java.util.Properties;

public class ConfigLoader {
    // Cần xem lại
    private static final Properties config = new Properties();
    static {
        //load config (temp)
        config.setProperty("db.staging.name", "");
        config.setProperty("db.staging.host", "");
        config.setProperty("db.staging.port", "");
        config.setProperty("db.staging.username", "");
        config.setProperty("db.staging.password", "");
        config.setProperty("db.warehouse.name", "");
        config.setProperty("db.warehouse.host", "");
        config.setProperty("db.warehouse.port", "");
        config.setProperty("db.warehouse.username", "");
        config.setProperty("db.warehouse.password", "");
        config.setProperty("db.control.name", "");
        config.setProperty("db.control.host", "");
        config.setProperty("db.control.port", "");
        config.setProperty("db.control.username", "");
        config.setProperty("db.control.password", "");
    }

    private static DatabaseConnection createDbConnection(String prefix) {
        return new DatabaseConnection(
                config.getProperty(prefix + "name"),
                "mysql",
                config.getProperty(prefix + "host"),
                config.getProperty(prefix + "port"),
                config.getProperty(prefix + "username"),
                config.getProperty(prefix + "password"),
                "useUnicode=true&characterEncoding=UTF-8"
        );
    }

    public static DatabaseConnection loadControlDBConfig() {
        return createDbConnection("db.control.");
    }

    public static DatabaseConnection loadStagingDBConfig() {
        return createDbConnection("db.staging.");
    }

    public static DatabaseConnection loadWarehouseDBConfig() {
        return createDbConnection("db.warehouse.");
    }
}