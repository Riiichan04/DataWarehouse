package config;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class JDBIConnector {

    private static Jdbi stagingJdbi;
    private static Jdbi controlJdbi;
    private static Jdbi warehouseJdbi;

    public static Jdbi getStagingInstance() {
        if (stagingJdbi == null) {
            DatabaseConnection stagingConfig = new ConfigLoader().loadStagingDBConfig();
            String url = "jdbc:mysql://" + stagingConfig.getHost() + ":" + stagingConfig.getPort() + "/" + stagingConfig.getName() + "?" + stagingConfig.getCharacterEncoding();
            stagingJdbi = Jdbi.create(url, stagingConfig.getUsername(), stagingConfig.getPassword());
            stagingJdbi.installPlugin(new SqlObjectPlugin());
        }
        return stagingJdbi;
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

    public static Jdbi getWarehouseInstance() {
        if (warehouseJdbi == null) {
            DatabaseConnection warehouseDBConfig = new ConfigLoader().loadWarehouseDBConfig();
            String url = "jdbc:mysql://" + warehouseDBConfig.getHost() + ":" + warehouseDBConfig.getPort() + "/" + warehouseDBConfig.getName() + "?" + warehouseDBConfig.getCharacterEncoding();
            warehouseJdbi = Jdbi.create(url, warehouseDBConfig.getUsername(), warehouseDBConfig.getPassword());
            warehouseJdbi.installPlugin(new SqlObjectPlugin());
        }
        return warehouseJdbi;
    }

}