package config;

import org.jdbi.v3.core.Jdbi;

public class JDBIConnector {
    static Jdbi jdbi;

    private static Jdbi createJdbi() {
        if (jdbi == null) {
            //FIXME: Add config here
            ConfigLoader config = new ConfigLoader("");
            DatabaseConnection connection = config.loadDatabaseConfig();
            jdbi = Jdbi.create(connection.getConnection());
            jdbi.installPlugins();
        }
        return jdbi;
    }

    public static Jdbi getInstance() {
        if (jdbi == null) {
            jdbi = createJdbi();
        }
        return jdbi;
    }
}
