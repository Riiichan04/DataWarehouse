package config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class JDBIConnector {

    private static Jdbi warehouseJdbi;
    private static Jdbi controlJdbi;

    public static Jdbi getWarehouseInstance() {
        if (warehouseJdbi == null) {
            DatabaseConnection stagingConfig = new ConfigLoader().loadWarehouseDBConfig();
            String url = "jdbc:mysql://" + stagingConfig.getHost() + ":" + stagingConfig.getPort() + "/" + stagingConfig.getName() + "?" + stagingConfig.getCharacterEncoding();
            warehouseJdbi = Jdbi.create(url, stagingConfig.getUsername(), stagingConfig.getPassword());
            warehouseJdbi.installPlugin(new SqlObjectPlugin());
        }
        return warehouseJdbi;
    }

    public static Jdbi getControlInstance() {
        if (controlJdbi == null) {
            DatabaseConnection controlConfig = new ConfigLoader().loadControlDBConfig();
            String url = "jdbc:mysql://" + controlConfig.getHost() + ":" + controlConfig.getPort() + "/" + controlConfig.getName() + "?" + controlConfig.getCharacterEncoding();
            controlJdbi = Jdbi.create(url, controlConfig.getUsername(), controlConfig.getPassword());
            controlJdbi.installPlugin(new SqlObjectPlugin());
        }
        return controlJdbi;
    }

}