package config;

import org.jdbi.v3.core.Jdbi;

public class StagingConnector {
    static Jdbi jdbi;

    public static Jdbi createJdbi() {
        if (jdbi == null) {
            LoadConfigOperator config = new LoadConfigOperator();
            DatabaseConnection connection = config.loadStagingDatabase();
            System.out.println(connection.toString());
            System.out.println(connection.getConnection().toString());
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
