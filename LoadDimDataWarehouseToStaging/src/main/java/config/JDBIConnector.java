package config;

import org.jdbi.v3.core.Jdbi;

public class JDBIConnector {

    private static Jdbi stagingJdbi;
    private static Jdbi controlJdbi;
    private static Jdbi warehouseJdbi;

    public static Jdbi getStagingInstance() {
        if (stagingJdbi == null) {
            DatabaseConnection stagingConfig = ConfigLoader.loadStagingDBConfig();
            String url = "jdbc:mysql://" + stagingConfig.getHost() + ":" + stagingConfig.getPort() + "/" + stagingConfig.getName() + "?" + stagingConfig.getCharacterEncoding();
            stagingJdbi = Jdbi.create(url, stagingConfig.getUsername(), stagingConfig.getPassword());
        }
        return stagingJdbi;
    }

    public static Jdbi getControlInstance() {
        if (controlJdbi == null) {
            DatabaseConnection controlConfig = ConfigLoader.loadControlDBConfig();
            String url = "jdbc:mysql://" + controlConfig.getHost() + ":" + controlConfig.getPort() + "/" + controlConfig.getName() + "?" + controlConfig.getCharacterEncoding();
            controlJdbi = Jdbi.create(url, controlConfig.getUsername(), controlConfig.getPassword());
        }
        return controlJdbi;
    }

    public static Jdbi getWarehouseInstance() {
        if (warehouseJdbi == null) {
            DatabaseConnection warehouseDBConfig = ConfigLoader.loadWarehouseDBConfig();
            String url = "jdbc:mysql://" + warehouseDBConfig.getHost() + ":" + warehouseDBConfig.getPort() + "/" + warehouseDBConfig.getName() + "?" + warehouseDBConfig.getCharacterEncoding();
            warehouseJdbi = Jdbi.create(url, warehouseDBConfig.getUsername(), warehouseDBConfig.getPassword());
        }
        return warehouseJdbi;
    }

}