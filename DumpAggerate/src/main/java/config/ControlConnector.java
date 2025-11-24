package config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class ControlConnector {
    static Jdbi jdbi;

    public static Jdbi createJdbi() {
        if (jdbi == null) {
            LoadConfigOperator config = new LoadConfigOperator();
            DatabaseConnection connection = config.loadControlDatabase();
            jdbi = Jdbi.create(connection.getConnection());
            jdbi.installPlugin(new SqlObjectPlugin());

        }
        return jdbi;
    }

    public static Jdbi getInstance() {
        if (jdbi == null) {
            jdbi = createJdbi();
        }
        return jdbi;
    }
    public static void initialize(DatabaseConnection dbConn) {
        if (dbConn == null || dbConn.getConnection() == null) {
            throw new RuntimeException("DatabaseConnection is null or cannot get connection.");
        }
        jdbi = Jdbi.create(dbConn.getConnection());
        jdbi.installPlugin(new SqlObjectPlugin());

    }

}
