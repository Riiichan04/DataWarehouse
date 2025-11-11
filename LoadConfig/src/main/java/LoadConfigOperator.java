import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//FIXME: This is a temp file for defining structure

//3.4 Viết Script loadConfig từ db.config
public class LoadConfigOperator {

    static class Source {
        public int sourceId;
        public String name;
        public String url;
    }

    static class Process {
        public int id;
        public String scriptName;
        public String targetPath;
    }

    static class Config {
        public String runDate;
        public Source source;
        public List<Process> processList;
    }

    public static void main(String[] args) {
        String runDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));

        try {
            Properties prop = new Properties();
            prop.load(LoadConfigOperator.class.getClassLoader()
                    .getResourceAsStream("application.properties"));

            String dbControlUrl = prop.getProperty("db.control.url");
            String dbControlUser = prop.getProperty("db.control.username");
            String dbControlPass = prop.getProperty("db.control.password");

            String configJsonPath = prop.getProperty("config.json.path");
            String stagingFolder = prop.getProperty("staging.result.folder");
            String warehouseFolder = prop.getProperty("warehouse.folder");

            Connection conn = DriverManager.getConnection(dbControlUrl, dbControlUser, dbControlPass);
            System.out.println("Connected to db.control");

            // ------------------------- Lấy source active -------------------------
            Source source = null;
            PreparedStatement stSource = conn.prepareStatement(
                    "SELECT id, name, url FROM cfg_source WHERE isActive=1 LIMIT 1"
            );
            ResultSet rsSource = stSource.executeQuery();
            if (rsSource.next()) {
                source = new Source();
                source.sourceId = rsSource.getInt("id");
                source.name = rsSource.getString("name");
                source.url = rsSource.getString("url");
            } else {
                System.out.println("No active source found!");
                conn.close();
                return;
            }

            // ------------------------- Tạo source_process nếu chưa có -------------------------
            String targetCsv = stagingFolder + "/result_" + runDate + ".csv";
            PreparedStatement stInsertSourceProcess = conn.prepareStatement(
                    "INSERT INTO cfg_source_process(name, dataSourceId, targetPath, description, createdAt, updatedAt) " +
                            "SELECT ?, ?, ?, ?, NOW(), NOW() FROM dual " +
                            "WHERE NOT EXISTS (SELECT 1 FROM cfg_source_process WHERE dataSourceId=? AND targetPath=?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            stInsertSourceProcess.setString(1, "crawl_" + runDate);
            stInsertSourceProcess.setInt(2, source.sourceId);
            stInsertSourceProcess.setString(3, targetCsv);
            stInsertSourceProcess.setString(4, "Daily crawl process " + runDate);
            stInsertSourceProcess.setInt(5, source.sourceId);
            stInsertSourceProcess.setString(6, targetCsv);
            stInsertSourceProcess.executeUpdate();

            // ------------------------- Lấy danh sách process -------------------------
            List<Process> processList = new ArrayList<>();
            PreparedStatement stProcess = conn.prepareStatement(
                    "SELECT id, targetPath, name AS scriptName FROM cfg_source_process WHERE dataSourceId=?"
            );
            stProcess.setInt(1, source.sourceId);
            ResultSet rsProcess = stProcess.executeQuery();
            while (rsProcess.next()) {
                Process p = new Process();
                p.id = rsProcess.getInt("id");
                p.targetPath = rsProcess.getString("targetPath");
                p.scriptName = rsProcess.getString("scriptName");
                processList.add(p);
            }

            // ------------------------- Tạo cfg_staging_process & cfg_warehouse_process nếu cần -------------------------
//            for (Process p : processList) {
//                String stagingTable = "staging_result_" + runDate;
//                String warehouseTable = "warehouse_" + runDate;
//
//                PreparedStatement stInsertStaging = conn.prepareStatement(
//                        "INSERT INTO cfg_staging_process(name, sourcePath, targetDbId, targetTableName, dependentProcessId, createdAt, updatedAt, description) " +
//                                "SELECT ?, ?, 1, ?, ?, NOW(), NOW(), 'auto' FROM dual " +
//                                "WHERE NOT EXISTS (SELECT 1 FROM cfg_staging_process WHERE dependentProcessId=? AND targetTableName=?)"
//                );
//                stInsertStaging.setString(1, "staging_" + runDate);
//                stInsertStaging.setString(2, p.targetPath);
//                stInsertStaging.setString(3, stagingTable);
//                stInsertStaging.setInt(4, p.id);
//                stInsertStaging.setInt(5, p.id);
//                stInsertStaging.setString(6, stagingTable);
//                stInsertStaging.executeUpdate();
//
//                PreparedStatement stInsertWarehouse = conn.prepareStatement(
//                        "INSERT INTO cfg_warehouse_process(name, sourceDbId, sourceTableName, targetDbId, targetTableName, dependentProcessId, createdAt, updatedAt,description) " +
//                                "SELECT ?, 1, ?, 2, ?, ?, NOW(), NOW(),1 FROM dual " +
//                                "WHERE NOT EXISTS (SELECT 1 FROM cfg_warehouse_process WHERE dependentProcessId=? AND targetTableName=?)"
//                );
//                stInsertWarehouse.setString(1, "warehouse_" + runDate);
//                stInsertWarehouse.setString(2, stagingTable);
//                stInsertWarehouse.setString(3, warehouseTable);
//                stInsertWarehouse.setInt(4, p.id);
//                stInsertWarehouse.setInt(5, p.id);
//                stInsertWarehouse.setString(6, warehouseTable);
//                stInsertWarehouse.executeUpdate();
//            }

            // ------------------------- Xuất config.json -------------------------
            Config config = new Config();
            config.runDate = runDate;
            config.source = source;
            config.processList = processList;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(configJsonPath)) {
                gson.toJson(config, writer);
                System.out.println("config.json created at " + configJsonPath);
            }

            conn.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

}
