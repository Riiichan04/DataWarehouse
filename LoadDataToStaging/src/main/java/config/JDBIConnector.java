package config;

import org.jdbi.v3.core.Jdbi;

public class JDBIConnector {
    static Jdbi jdbi;

    private static Jdbi createJdbi() {
        if (jdbi == null) {
            LoadConfigOperator config = new LoadConfigOperator();
            DatabaseConnection connection = config.loadConfig();
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
